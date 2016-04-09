package l4g2ep1;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import l4g2ep1.common.*;
import loot.ImageResourceManager;

public class Presenter_Mode4 extends JFrame
{
	// 쓸 일은 없지만 안 적으면 경고가 뜨기에 그냥 적어 둔 버전 번호
	private static final long serialVersionUID = 1L;
	
	enum State
	{
		Not_Started,
		Running,
		Waiting,
		Skipping,
		Completed
	}
	
	/*
	 * 게임 진행 관련 필드 목록
	 */
	private State state;
	private Classroom_Settings settings;
	private Classroom classroom;
	private Thread thr_oneGame;
	private Thread thr_batch;
	
	/*
	 * UI 관련 필드 목록 
	 */
	private Font font_dot_um_che;				//텍스트 출력에 사용할 돋움체 font
	private loot.ImageResourceManager images;	//강의실 그리기에 사용할 이미지를 관리해 주는 친구

	private JLabel label_classroom;
	private Canvas canvas_classroom;			//강의실의 현재 모습을 그릴 캔버스
	private JCheckBox cb_reveal_classroom;		//강의실 전체를 보여줄지 플레이어#0에 대해서만 보여줄지 설정하는 체크박스
	private JTextArea tb_myInfo;				//플레이어#0에 대한 현재 정보
	private JTextField tb_gameNumberToPlayback;	//돌려 볼 게임 번호를 입력하는 칸
	private JButton bt_start_playback;
	private JButton bt_advanceTurn_playback;
	private JLabel label_infos;
	private JTextArea tb_cellInfo;				//마우스로 가리키는 칸에 대한 현재 정보
	private JTextArea tb_othersInfo;			//시야, 행동, 사건 정보
	private JLabel label_playback;
	private JLabel label_run100games;
	private JTextField tb_seed_100numbers;		//100개의 게임 번호를 만들기 위한 seed가 될 문자열을 입력하는 칸
	private JButton bt_start_run100games;
	private JTextArea tb_result_run100games;	//100판 결과
	
	@SuppressWarnings("unused")
	private final int cell_width = 450 / Constants.Classroom_Width < 450 / Constants.Classroom_Height ? 450 / Constants.Classroom_Width : 450 / Constants.Classroom_Height;
	private final int cell_height = cell_width;
	private final int cells_width = cell_width * Constants.Classroom_Width + cell_width / 20;
	private final int cells_height = cell_height * Constants.Classroom_Height + cell_height / 20;
	private final int cells_left = ( 450 - cells_width ) / 2;
	private final int cells_top = ( 450 - cells_height ) / 2;
	private int cell_visibility[][] = new int[Constants.Classroom_Height][Constants.Classroom_Width]; // 0 - 시야 밖, 1 - 시야 범위 안, 2 - 포착 범위 안
	private int pos_x = -1;
	private int pos_y = -1;
	
	public Presenter_Mode4(Classroom_Settings settings)
	{
		/*
		 * Frame 초기화
		 */
		super("L4G2EP1 Presenter - mode 4");
		rootPane.setPreferredSize(new Dimension(1024, 768));
		setResizable(false);
		setLocationByPlatform(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);

		/*
		 * 게임 진행 관련 필드 설정
		 */
		this.settings = settings;
		
		/*
		 * UI 관련 필드 설정
		 */
		font_dot_um_che = new Font("돋움체", Font.PLAIN, 12);
		
		images = new ImageResourceManager();
	
		label_classroom = new JLabel();
		label_classroom.setSize(450, 12);
		label_classroom.setLocation(8, 8);
		label_classroom.setText("Classroom Overview");
		label_classroom.setFont(font_dot_um_che);
		add(label_classroom);
		
		canvas_classroom = new Canvas();
		canvas_classroom.setSize(450, 450);
		canvas_classroom.setLocation(8, 28);
		canvas_classroom.setIgnoreRepaint(true);
		canvas_classroom.setBackground(Color.black);
		canvas_classroom.addMouseListener(listener_canvas_classroom_mouse);
		add(canvas_classroom);

		JPanel panel_contains_cb_reveal_classroom = new JPanel();
		panel_contains_cb_reveal_classroom.setSize(450, 24);
		panel_contains_cb_reveal_classroom.setLocation(8, 486);
		panel_contains_cb_reveal_classroom.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		add(panel_contains_cb_reveal_classroom);
		
		cb_reveal_classroom = new JCheckBox();
		cb_reveal_classroom.setText("Reveal Entire Classroom");
		cb_reveal_classroom.setFont(font_dot_um_che);
		cb_reveal_classroom.setEnabled(false);
		cb_reveal_classroom.addActionListener(listener_cb_reveal_classroom_click);
		cb_reveal_classroom.addKeyListener(listener_frame_keyInput);
		panel_contains_cb_reveal_classroom.add(cb_reveal_classroom);
		
		tb_myInfo = new JTextArea();
		tb_myInfo.setSize(450, 150);
		tb_myInfo.setLocation(8, 518);
		tb_myInfo.setText("");
		tb_myInfo.setEditable(false);
		tb_myInfo.setFont(font_dot_um_che);
		add(tb_myInfo);
		
		label_playback = new JLabel();
		label_playback.setSize(392, 12);
		label_playback.setLocation(8, 676);
		label_playback.setText("Playback Settings");
		label_playback.setFont(font_dot_um_che);
		add(label_playback);
		
		tb_gameNumberToPlayback = new JTextField();
		tb_gameNumberToPlayback.setSize(304, 20);
		tb_gameNumberToPlayback.setLocation(8, 696);
		tb_gameNumberToPlayback.setText("Enter game number here");
		tb_gameNumberToPlayback.setFont(font_dot_um_che);
		tb_gameNumberToPlayback.addFocusListener(listener_tb_focused);
		add(tb_gameNumberToPlayback);
		
		bt_start_playback = new JButton();
		bt_start_playback.setSize(140, 20);
		bt_start_playback.setLocation(318, 696);
		bt_start_playback.setText("Start");
		bt_start_playback.setFont(font_dot_um_che);
		bt_start_playback.addActionListener(listener_bt_start_playback_click);
		bt_start_playback.setFocusable(false);
		add(bt_start_playback);
		
		bt_advanceTurn_playback = new JButton();
		bt_advanceTurn_playback.setSize(450, 36);
		bt_advanceTurn_playback.setLocation(8, 724);
		bt_advanceTurn_playback.setText("Advance Turn");
		bt_advanceTurn_playback.setFont(font_dot_um_che);
		bt_advanceTurn_playback.setEnabled(false);
		bt_advanceTurn_playback.addActionListener(listener_bt_advanceTurn_playback_click);
		bt_advanceTurn_playback.setFocusable(false);
		add(bt_advanceTurn_playback);
		
		label_infos = new JLabel();
		label_infos.setSize(300, 12);
		label_infos.setLocation(466, 8);
		label_infos.setText("Informations");
		label_infos.setFont(font_dot_um_che);
		add(label_infos);

		
		tb_cellInfo = new JTextArea();
		tb_cellInfo.setText("Click cell to view informations");
		tb_cellInfo.setEditable(false);
		tb_cellInfo.setFont(font_dot_um_che);
		add(tb_cellInfo);
		
		JScrollPane panel_contains_tb_cellInfo = new JScrollPane(tb_cellInfo);
		panel_contains_tb_cellInfo.setSize(300, 300);
		panel_contains_tb_cellInfo.setLocation(466, 28);
		panel_contains_tb_cellInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(panel_contains_tb_cellInfo);

		tb_othersInfo = new JTextArea();
		tb_othersInfo.setText("");
		tb_othersInfo.setEditable(false);
		tb_othersInfo.setFont(font_dot_um_che);

		JScrollPane panel_contains_tb_othersInfo = new JScrollPane(tb_othersInfo);
		panel_contains_tb_othersInfo.setSize(300, 424);
		panel_contains_tb_othersInfo.setLocation(466, 336);
		panel_contains_tb_othersInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(panel_contains_tb_othersInfo);

		label_run100games = new JLabel();
		label_run100games.setSize(242, 12);
		label_run100games.setLocation(774, 8);
		label_run100games.setText("Batch Test Settings");
		label_run100games.setFont(font_dot_um_che);
		add(label_run100games);
		
		tb_seed_100numbers = new JTextField();
		tb_seed_100numbers.setSize(174, 20);
		tb_seed_100numbers.setLocation(774, 28);
		tb_seed_100numbers.setText("Enter seed text here");
		tb_seed_100numbers.setFont(font_dot_um_che);
		tb_seed_100numbers.addFocusListener(listener_tb_focused);
		add(tb_seed_100numbers);
		
		bt_start_run100games = new JButton();
		bt_start_run100games.setSize(60, 20);
		bt_start_run100games.setLocation(956, 28);
		bt_start_run100games.setText("Run");
		bt_start_run100games.setFont(font_dot_um_che);
		bt_start_run100games.addActionListener(listener_bt_start_run100games_click);
		bt_start_run100games.setFocusable(false);
		add(bt_start_run100games);
		
		tb_result_run100games = new JTextArea();
		tb_result_run100games.setFont(font_dot_um_che);
		tb_result_run100games.setEditable(false);
		
		JScrollPane panel_contains_lb_result_run100games = new JScrollPane(tb_result_run100games);
		panel_contains_lb_result_run100games.setSize(242, 704);
		panel_contains_lb_result_run100games.setLocation(774, 56);
		panel_contains_lb_result_run100games.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(panel_contains_lb_result_run100games);

		pack();
		
		canvas_classroom.createBufferStrategy(2);
		
		images.LoadImage("Images/cell_outOfSight.png", "cell_outOfSight");
		images.LoadImage("Images/cell_empty.png", "cell_empty");
		images.LoadImage("Images/cell_survivor.png", "cell_survivor");
		images.LoadImage("Images/cell_corpse.png", "cell_corpse");
		images.LoadImage("Images/cell_infected.png", "cell_infected");
		images.LoadImage("Images/cell_survivor_corpse.png", "cell_survivor_corpse");
		images.LoadImage("Images/cell_infected_corpse.png", "cell_infected_corpse");
		images.LoadImage("Images/cell_empty_spot.png", "cell_empty_spot");
		images.LoadImage("Images/cell_survivor_spot.png", "cell_survivor_spot");
		images.LoadImage("Images/cell_corpse_spot.png", "cell_corpse_spot");
		images.LoadImage("Images/cell_infected_spot.png", "cell_infected_spot");
		images.LoadImage("Images/cell_survivor_corpse_spot.png", "cell_survivor_corpse_spot");
		images.LoadImage("Images/cell_infected_corpse_spot.png", "cell_infected_corpse_spot");
		images.LoadImage("Images/cell_corpse_detect.png", "cell_corpse_detect");		
		
		state = State.Not_Started;
	}
	
	MouseListener listener_canvas_classroom_mouse = new MouseListener()
	{
		
		@Override
		public void mouseReleased(MouseEvent e) { }
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if ( state == State.Waiting )
			{
				int cursor_x = e.getX() - cells_left;
				int cursor_y = e.getY() - cells_top;
				
				if ( cursor_x < 0 ||
					 cursor_y < 0 ||
					 cursor_x >= cell_width * Constants.Classroom_Width ||
					 cursor_y >= cell_height * Constants.Classroom_Height )
					return;
				
				pos_x = cursor_x / cell_width;
				pos_y = cursor_y / cell_height;
				
				WriteCellInfo();
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) { }
		
		@Override
		public void mouseEntered(MouseEvent e) { }
		
		@Override
		public void mouseClicked(MouseEvent e) { }
	}; 
	
	KeyListener listener_frame_keyInput = new KeyListener()
	{
		@Override
		public void keyTyped(KeyEvent e) { }
		
		@Override
		public void keyReleased(KeyEvent e) { }
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if ( e.getKeyCode() == KeyEvent.VK_ENTER && state == State.Waiting )
				listener_bt_advanceTurn_playback_click.actionPerformed(null);
		}
	};
	
	ActionListener listener_cb_reveal_classroom_click = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			if ( state != State.Not_Started )
			{
				DrawClassroom();
				WriteMyInfo();
				WriteCellInfo();
				WriteOthersInfo();
			}
		}
	};

	
	ActionListener listener_bt_start_playback_click = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			//이미 게임이 실행중이라면 완전히 끝내버림
			if ( state == State.Running || state == State.Waiting )
			{
				bt_start_playback.setEnabled(false);
				bt_advanceTurn_playback.setEnabled(false);
				state = State.Skipping;

				synchronized (classroom)
				{
					classroom.isSkipping = true;
					classroom.notify();
				}
			}
			
			if ( state == State.Not_Started || state == State.Completed )
			{
				int gameNumber;
				
				try
				{
					gameNumber = Integer.parseInt(tb_gameNumberToPlayback.getText());
				}
				catch ( NumberFormatException ex )
				{
					gameNumber = -1;
					tb_gameNumberToPlayback.setText("Game number was selected randomly.");
				}
				
				settings.game_number = gameNumber;
				
				classroom = new Classroom(settings);
				
				classroom.turn_ended = listener_classroom_turn_ended;
				classroom.game_completed = listener_classroom_game_completed;
				
				thr_oneGame = new Thread(classroom);
				
				tb_gameNumberToPlayback.setEnabled(false);
				bt_start_playback.setText("Skip to end");
				cb_reveal_classroom.setEnabled(true);
				
				state = State.Running;
				thr_oneGame.start();
			}
		}
	};
	
	ActionListener listener_bt_advanceTurn_playback_click = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if ( state == State.Waiting )
			{
				state = State.Running;
				bt_advanceTurn_playback.setEnabled(false);
				
				synchronized (classroom)
				{
					classroom.notify();
				}
			}
		}
	};

	ActionListener listener_bt_start_run100games_click = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			bt_start_run100games.setEnabled(false);
			
			thr_batch = new Thread(run_thr_batch);
			thr_batch.start();
		}
	};
	
	Runnable run_thr_batch = new Runnable()
	{
		@Override
		public void run()
		{
			GameNumbers numbers = new GameNumbers(100);

			int seed = 0;
			
			if ( tb_seed_100numbers.getText().length() != 0 )
			{
				seed = tb_seed_100numbers.getText().hashCode();
				
				if ( seed < 1 )
					seed += Integer.MAX_VALUE;
			}
			
			numbers.Create(seed);
			
			Classroom_Settings batch_settings = new Classroom_Settings(settings);
			
			tb_result_run100games.setText("");
			
			for ( int iGame = 0; iGame < 100; ++iGame )
			{
				batch_settings.game_number = numbers.data[iGame];
				Classroom classroom = new Classroom(batch_settings);
				classroom.run();
				
				tb_result_run100games.append(String.format("Game # %d - %d(%d)\n", classroom.gameInfo.gameNumber, classroom.scoreboard.final_grades[0], classroom.scoreboard.final_ranks[0]));
				tb_result_run100games.setCaretPosition(tb_result_run100games.getText().length());
			}
			
			bt_start_run100games.setEnabled(true);	
		}
	};
	
	FocusListener listener_tb_focused = new FocusListener()
	{
		@Override
		public void focusLost(FocusEvent e) { }
		
		@Override
		public void focusGained(FocusEvent e)
		{
			JTextField tb = (JTextField)e.getSource();
			tb.selectAll();
		}
	};

	ActionListener listener_classroom_turn_ended = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			DrawClassroom();
			WriteMyInfo();
			WriteCellInfo();
			WriteOthersInfo();
			
			if ( state != State.Skipping )
			{
				state = State.Waiting;

				bt_start_playback.setEnabled(true);
				bt_advanceTurn_playback.setEnabled(true);
			}
		}
	};
	

	ActionListener listener_classroom_game_completed = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			DrawClassroom();
			WriteMyInfo();
			WriteCellInfo();
			WriteOthersInfo();

			bt_advanceTurn_playback.setEnabled(false);
			
			state = State.Completed;
			
			tb_gameNumberToPlayback.setEnabled(true);
			bt_start_playback.setEnabled(true);
			bt_start_playback.setText("Start");
		}
	};
	
	void DrawClassroom()
	{
		BufferStrategy buf = canvas_classroom.getBufferStrategy();
		Graphics2D g = (Graphics2D) buf.getDrawGraphics();

		//전체 강의실을 보여주는 경우
		if ( cb_reveal_classroom.isSelected() == true )
		{
			for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
				for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
				{
					cell_visibility[iRow][iColumn] = 1;
					
					// 0x00 - empty, 0x01 - survivor, 0x10 - corpse, 0x02 - infected
					int imageSeed = 0x00;

					for ( PlayerInfo info : classroom.cells.data[iRow][iColumn].playersInTheCell )
					{
						switch (info.state)
						{
						case Survivor:
							imageSeed |= 0x01;
							break;
						case Corpse:
							imageSeed |= 0x10;
							break;
						case Infected:
							imageSeed |= 0x02;
							break;
						default:
							break;
						}
					}

					switch (imageSeed)
					{
					case 0x00:
						g.drawImage(images.GetImage("cell_empty"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					case 0x01:
						g.drawImage(images.GetImage("cell_survivor"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					case 0x10:
						g.drawImage(images.GetImage("cell_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					case 0x02:
						g.drawImage(images.GetImage("cell_infected"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					case 0x11:
						g.drawImage(images.GetImage("cell_survivor_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					case 0x12:
						g.drawImage(images.GetImage("cell_infected_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
						break;
					default:
						break;
					}
				}
		}
		//플레이어#0의 시점에서 강의실을 보여주는 경우
		else
		{
			
			l4g2ep1.common.Point pos = classroom.playerInfos[0].position;
			
			switch (classroom.playerInfos[0].state)
			{
			case Survivor:
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
					{
						cell_visibility[iRow][iColumn] = 0;
						
						if ( classroom.players[0].CanSee(iColumn, iRow) == true )
							cell_visibility[iRow][iColumn] = 1;
						else
						{
							// 다른 생존자에 의해 전달받아 알게 된 영역 체크
							for ( PlayerInfo otherInfo : classroom.players[0].othersInfo_withinSight )
							{
								if ( otherInfo.state == PlayerInfo.State.Survivor && classroom.players[otherInfo.ID].CanSee(iColumn, iRow) == true )
								{
									cell_visibility[iRow][iColumn] = 2;
									break;
								}
							}
						}
					}
				break;
			case Corpse:
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						cell_visibility[iRow][iColumn] = 0;
				
				cell_visibility[classroom.playerInfos[0].position.y][classroom.playerInfos[0].position.x] = 1;
				break;
			case Infected:
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						cell_visibility[iRow][iColumn] = 0;
				
				for ( int rowOffset = -2; rowOffset <= 2; ++rowOffset )
					for ( int columnOffset = -2; columnOffset <= 2; ++columnOffset )
						if ( Point.IsValid(pos.x + columnOffset, pos.y + rowOffset) == true )
							cell_visibility[pos.y + rowOffset][pos.x + columnOffset] = 1;
	
				// 시체가 존재하는 영역 체크
				for ( PlayerInfo otherInfo : classroom.playerInfos )
					if ( otherInfo.state == PlayerInfo.State.Corpse && cell_visibility[otherInfo.position.y][otherInfo.position.x] == 0 )
						cell_visibility[otherInfo.position.y][otherInfo.position.x] = 2;
				break;
			case Soul:
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						cell_visibility[iRow][iColumn] = 1;
				break;
			default:
				break;
			}
			
			for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
				for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
				{
					if ( cell_visibility[iRow][iColumn] == 0 )
					{
						g.drawImage(images.GetImage("cell_outOfSight"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
					}
					else
					{
						// 0x00 - empty, 0x01 - survivor, 0x10 - corpse, 0x02 -
						// infected
						int imageSeed = 0x00;

						for ( PlayerInfo info : classroom.cells.data[iRow][iColumn].playersInTheCell )
						{
							switch (info.state)
							{
							case Survivor:
								imageSeed |= 0x01;
								break;
							case Corpse:
								imageSeed |= 0x10;
								break;
							case Infected:
								imageSeed |= 0x02;
								break;
							default:
								break;
							}
						}

						// 시야 범위 내의 칸
						if ( cell_visibility[iRow][iColumn] == 1 )
						{
							switch (imageSeed)
							{
							case 0x00:
								g.drawImage(images.GetImage("cell_empty"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							case 0x01:
								g.drawImage(images.GetImage("cell_survivor"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							case 0x10:
								g.drawImage(images.GetImage("cell_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							case 0x02:
								g.drawImage(images.GetImage("cell_infected"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							case 0x11:
								g.drawImage(images.GetImage("cell_survivor_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							case 0x12:
								g.drawImage(images.GetImage("cell_infected_corpse"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
								break;
							default:
								break;
							}
						}
						// 포착 또는 감지된 칸
						else
						{
							// 감염체는 시체밖에 감지할 수 없으므로 해당 칸은 무조건 시체만 있는 것으로 표시
							if ( classroom.playerInfos[0].state == PlayerInfo.State.Infected )
							{
								g.drawImage(images.GetImage("cell_corpse_detect"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
							}
							else
							{
								switch (imageSeed)
								{
								case 0x00:
									g.drawImage(images.GetImage("cell_empty_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								case 0x01:
									g.drawImage(images.GetImage("cell_survivor_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								case 0x10:
									g.drawImage(images.GetImage("cell_corpse_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								case 0x02:
									g.drawImage(images.GetImage("cell_infected_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								case 0x11:
									g.drawImage(images.GetImage("cell_survivor_corpse_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								case 0x12:
									g.drawImage(images.GetImage("cell_infected_corpse_spot"), cells_left + cell_width * iColumn, cells_top + cell_height * iRow, cell_width * 105 / 100, cell_height * 105 / 100, null);
									break;
								default:
									break;
								}
							}
						}
					}
				}
		}
		

		buf.show();
		g.dispose();
	}
	
	void WriteMyInfo()
	{
		PlayerInfo myInfo = classroom.playerInfos[0];
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Game #%d - Turn %d\n\n", classroom.gameInfo.gameNumber, classroom.gameInfo.currentTurnNumber));

		sb.append(String.format("%s | State: %s, HP: %d, Position: \n\n", classroom.players[myInfo.ID].name, myInfo.state.toString(), myInfo.hitPoint, myInfo.position));
		
		sb.append("Score:\nSMax STot CMax CTot IMax ITot\n");
		
		sb.append(classroom.scoreboard.scores[0]);
		
		tb_myInfo.setText(sb.toString());
	}
	
	void WriteCellInfo()
	{
		if ( Point.IsValid(pos_x, pos_y) == false )
			return;
		
		CellInfo cellInfo = classroom.cells.data[pos_y][pos_x];
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("(%d, %d)", pos_x, pos_y));
		
		if ( cell_visibility[pos_y][pos_x] == 0 )
		{
			sb.append(" - Cannot see this cell.\n");
		}
		else
		{
			if ( cell_visibility[pos_y][pos_x] == 2 )
			{
				sb.append(" - Out of sight.\n");
			}
			else
			{
				sb.append(" - Within sight.\n");
			}
			
			sb.append("Players:\n");
			for ( PlayerInfo player : cellInfo.playersInTheCell )
			{
				if ( cell_visibility[pos_y][pos_x] == 1 ||
						classroom.playerInfos[0].state != PlayerInfo.State.Infected ||
						player.state == PlayerInfo.State.Corpse )
					sb.append(String.format("%s | %s, HP: %d\n", classroom.players[player.ID].name, player.state.toString(), player.hitPoint));
			}

			if ( cell_visibility[pos_y][pos_x] == 1 )
			{
				sb.append("Actions:\n");
				for ( Action action : cellInfo.actionsInTheCell )
				{
					if ( action.type == Action.TypeCode.Move )
						sb.append(String.format("%s | %s -> %s\n", classroom.players[action.actorID].name, action.location_from, action.location_to));
					else
						sb.append(String.format("%s | Spawn to %s\n", classroom.players[action.actorID].name, action.location_to));
				}
				
				sb.append("Reactions:\n");
				for ( Reaction reaction : cellInfo.reactionsInTheCell )
				{
					sb.append(String.format("%s -> %s | %s\n", classroom.players[reaction.subjectID].name, classroom.players[reaction.objectID].name, reaction.type));
				}
			}	
		}

		tb_cellInfo.setText(sb.toString());
	}
	
	void WriteOthersInfo()
	{
		StringBuilder sb = new StringBuilder();
		
		if ( cb_reveal_classroom.isSelected() == true )
		{
			sb.append("Players:\n");
			for ( PlayerInfo player : classroom.playerInfos )
				sb.append(String.format("%s | %s, HP: %d\n", classroom.players[player.ID].name, player.state.toString(), player.hitPoint));

			sb.append("Actions:\n");
			for ( Action action : classroom.moves )
				sb.append(String.format("%s | %s -> %s\n", classroom.players[action.actorID].name, action.location_from, action.location_to));
			
			for ( Action action : classroom.spawns )
				sb.append(String.format("%s | Spawn to %s\n", classroom.players[action.actorID].name, action.location_to));				
				
			sb.append("Reactions:\n");
			for ( Reaction reaction : classroom.reactions )
				sb.append(String.format("%s -> %s | %s\n", classroom.players[reaction.subjectID].name, classroom.players[reaction.objectID].name, reaction.type));
		}
		else
		{
			Player me = classroom.players[0];
			
			sb.append("Players within sight:\n");
			for ( PlayerInfo player : me.othersInfo_withinSight )
				sb.append(String.format("%s | %s, HP: %d\n", classroom.players[player.ID].name, player.state.toString(), player.hitPoint));

			if ( me.receiveOthersInfo_detected == true )
			{
				sb.append("\nPlayers detected:\n");
				for ( PlayerInfo player : me.othersInfo_detected )
					sb.append(String.format("%s | %s, HP: %d\n", classroom.players[player.ID].name, player.state.toString(), player.hitPoint));
			}
			else
				sb.append("\nYou are not receiving infos of detected players.\n");
			
			if ( me.receiveActions == true )
			{
				sb.append("\nActions:\n");
				for ( Action action : me.actions )
				{
					if ( action.type == Action.TypeCode.Move )
						sb.append(String.format("%s | %s -> %s\n", classroom.players[action.actorID].name, action.location_from, action.location_to));
					else
						sb.append(String.format("%s | Spawn to %s\n", classroom.players[action.actorID].name, action.location_to));
				}
			}
			else
				sb.append("\nYou are not receiving infos of actions.\n");
			
			if ( me.receiveReactions == true )
			{
				sb.append("\nReactions:\n");
				for ( Reaction reaction : me.reactions )
					sb.append(String.format("%s -> %s | %s\n", classroom.players[reaction.subjectID].name, classroom.players[reaction.objectID].name, reaction.type));
			}
			else
				sb.append("\nYou are not receiving infos of reactions.\n");
		}
		
		tb_othersInfo.setText(sb.toString());
	}
}
