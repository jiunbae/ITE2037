package l4g2ep1;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import l4g2ep1.common.*;
import loot.ImageResourceManager;

public class Presenter_Mode5 extends JFrame
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

	private JPanel panel_control; 
	private JCheckBox cb_focusToOnePlayer;		//강의실 전체를 보여줄지 한 플레이어에 대해서만 보여줄지 설정하는 체크박스
	private JComboBox<String> cb_perspective;	//주시할 플레이어를 선택할 콤보박스
	private JTextField tb_gameNumberToPlayback;	//돌려 볼 게임 번호를 입력하는 칸
	private JButton bt_start_playback;
	private JButton bt_advanceTurn_playback;
	private JButton bt_start_run10000games;
	private JRadioButton rb_oneGame;
	private JRadioButton rb_officialGame;
	
	private JPanel panel_classroom;
	private Canvas canvas_classroom;			//강의실의 현재 모습을 그릴 캔버스
	private JTextArea tb_myInfo;				//플레이어#0에 대한 현재 정보
	private JLabel label_infos;
	private JTextArea tb_cellInfo;				//마우스로 가리키는 칸에 대한 현재 정보
	private JTextArea tb_othersInfo;			//시야, 행동, 사건 정보
	

	private JPanel panel_statistics;
	/**
	 * [종목][순위][이름,점수]
	 */
	private JLabel[][][] tb_statistics = new JLabel[18][5][2];
	
	private JTextArea tb_myBest;
	
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
	
	public Presenter_Mode5(Classroom_Settings settings)
	{
		/*
		 * Frame 초기화
		 */
		super("L4G2EP1 Presenter - mode 5");
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
	
		//좌측 제어 영역
		panel_control = new JPanel();
		panel_control.setSize(256,768);
		panel_control.setLocation(0, 0);
		panel_control.setLayout(null);
		add(panel_control);
		
		JLabel label_oneGameSetting = new JLabel();
		label_oneGameSetting.setSize(256-8-8, 12);
		label_oneGameSetting.setLocation(8, 8);
		label_oneGameSetting.setText("One Game");
		//label_oneGameSetting.setFont(font_dot_um_che);
		panel_control.add(label_oneGameSetting);
		
		cb_focusToOnePlayer = new JCheckBox();
		cb_focusToOnePlayer.setSize(256-8-8, 21);
		cb_focusToOnePlayer.setLocation(8, 8+12+8);
		cb_focusToOnePlayer.setText("Focus on specific player");
		cb_focusToOnePlayer.setFont(font_dot_um_che);
		cb_focusToOnePlayer.addActionListener(listener_cb_focusToOnePlayer_click);
		cb_focusToOnePlayer.addKeyListener(listener_frame_keyInput);
		panel_control.add(cb_focusToOnePlayer);

		cb_perspective = new JComboBox<String>();
		cb_perspective.setSize(256-8-8-24, 20);
		cb_perspective.setLocation(8+24, 8+12+8+21+8);
		cb_perspective.setFont(font_dot_um_che);
		cb_perspective.addItemListener(listener_cb_perspective_changed);
		cb_perspective.addKeyListener(listener_frame_keyInput);
		cb_perspective.setEnabled(false);
		
		Classroom classroom_temp = new Classroom(settings);
		classroom_temp.Initialize();
		for ( Player player : classroom_temp.players )
			cb_perspective.addItem(new String(player.name));
		panel_control.add(cb_perspective);
		
		
		JLabel label_oneGameNumber = new JLabel();
		label_oneGameNumber.setSize(256-8-8, 12);
		label_oneGameNumber.setLocation(8, 101);
		label_oneGameNumber.setText("Game number:");
		label_oneGameNumber.setFont(font_dot_um_che);
		panel_control.add(label_oneGameNumber);
		
		tb_gameNumberToPlayback = new JTextField();
		tb_gameNumberToPlayback.setSize(256-16-8, 20);
		tb_gameNumberToPlayback.setLocation(16, 101+12+8);
		tb_gameNumberToPlayback.setText("Enter game number here");
		tb_gameNumberToPlayback.setFont(font_dot_um_che);
		tb_gameNumberToPlayback.addFocusListener(listener_tb_focused);
		panel_control.add(tb_gameNumberToPlayback);	

		bt_start_playback = new JButton();
		bt_start_playback.setSize(256-8-8, 20);
		bt_start_playback.setLocation(8, 101+12+8+20+8+20);
		bt_start_playback.setText("Start one game");
		bt_start_playback.setFont(font_dot_um_che);
		bt_start_playback.addActionListener(listener_bt_start_playback_click);
		bt_start_playback.setFocusable(false);
		panel_control.add(bt_start_playback);
		
		bt_advanceTurn_playback = new JButton();
		bt_advanceTurn_playback.setSize(256-8-8, 36);
		bt_advanceTurn_playback.setLocation(8, 101+12+8+20+8+20+8+20);
		bt_advanceTurn_playback.setText("Advance Turn");
		bt_advanceTurn_playback.setFont(font_dot_um_che);
		bt_advanceTurn_playback.setEnabled(false);
		bt_advanceTurn_playback.addActionListener(listener_bt_advanceTurn_playback_click);
		bt_advanceTurn_playback.setFocusable(false);
		panel_control.add(bt_advanceTurn_playback);
		
		JLabel label_officialGame = new JLabel();
		label_officialGame.setSize(256-8-8, 12);
		label_officialGame.setLocation(8, 280);
		label_officialGame.setText("Official Game");
		panel_control.add(label_officialGame);

		
		bt_start_run10000games = new JButton();
		bt_start_run10000games.setSize(256-8-8, 36);
		bt_start_run10000games.setLocation(8, 280+12+8);
		bt_start_run10000games.setText("Run Official Game");
		bt_start_run10000games.setFont(font_dot_um_che);
		bt_start_run10000games.addActionListener(listener_bt_start_run10000games_click);
		bt_start_run10000games.setFocusable(false);
		panel_control.add(bt_start_run10000games);

		
		JLabel label_view = new JLabel();
		label_view.setSize(256-8-8, 12);
		label_view.setLocation(8, 400);
		label_view.setText("View Selection");
		panel_control.add(label_view);
		
		rb_oneGame = new JRadioButton();
		rb_oneGame.setSize(256-8-8, 20);
		rb_oneGame.setLocation(8, 400+12+8);
		rb_oneGame.setText("One game");
		rb_oneGame.setFont(font_dot_um_che);
		rb_oneGame.setSelected(true);
		rb_oneGame.addChangeListener(listener_rb_oneGame_changed);
		rb_oneGame.addKeyListener(listener_frame_keyInput);
		panel_control.add(rb_oneGame);
		
		rb_officialGame = new JRadioButton();
		rb_officialGame.setSize(256-8-8, 20);
		rb_officialGame.setLocation(8, 400+12+8+20+8);
		rb_officialGame.setText("Official game");
		rb_officialGame.setFont(font_dot_um_che);
		panel_control.add(rb_officialGame);
		
		ButtonGroup group_rb = new ButtonGroup();
		group_rb.add(rb_oneGame);
		group_rb.add(rb_officialGame);

		
		//강의실 패널
		panel_classroom = new JPanel();
		panel_classroom.setSize(768,768);
		panel_classroom.setLocation(256, 0);
		panel_classroom.setLayout(null);
		add(panel_classroom);
		
		JLabel label_classroom = new JLabel();
		label_classroom.setSize(450, 12);
		label_classroom.setLocation(8, 8);
		label_classroom.setText("Classroom overview");
		label_classroom.setFont(font_dot_um_che);
		panel_classroom.add(label_classroom);
		
		canvas_classroom = new Canvas();
		canvas_classroom.setSize(450, 450);
		canvas_classroom.setLocation(8, 8+12+8);
		canvas_classroom.setIgnoreRepaint(true);
		canvas_classroom.setBackground(Color.black);
		canvas_classroom.addMouseListener(listener_canvas_classroom_mouse);
		panel_classroom.add(canvas_classroom);
		
		tb_myInfo = new JTextArea();
		tb_myInfo.setSize(450, 274);
		tb_myInfo.setText("");
		tb_myInfo.setEditable(false);
		tb_myInfo.setFont(font_dot_um_che);
		
		JScrollPane panel_contains_tb_myInfo = new JScrollPane(tb_myInfo);
		panel_contains_tb_myInfo.setSize(450, 274);
		panel_contains_tb_myInfo.setLocation(8, 8+12+8+450+8);
		panel_classroom.add(panel_contains_tb_myInfo);
		
		label_infos = new JLabel();
		label_infos.setSize(300, 12);
		label_infos.setLocation(466, 8);
		label_infos.setText("Informations");
		label_infos.setFont(font_dot_um_che);
		panel_classroom.add(label_infos);

		
		tb_cellInfo = new JTextArea();
		tb_cellInfo.setText("Click cell to view informations");
		tb_cellInfo.setEditable(false);
		tb_cellInfo.setFont(font_dot_um_che);
		panel_classroom.add(tb_cellInfo);
		
		JScrollPane panel_contains_tb_cellInfo = new JScrollPane(tb_cellInfo);
		panel_contains_tb_cellInfo.setSize(300, 300);
		panel_contains_tb_cellInfo.setLocation(466, 28);
		panel_contains_tb_cellInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_classroom.add(panel_contains_tb_cellInfo);

		tb_othersInfo = new JTextArea();
		tb_othersInfo.setText("");
		tb_othersInfo.setEditable(false);
		tb_othersInfo.setFont(font_dot_um_che);

		JScrollPane panel_contains_tb_othersInfo = new JScrollPane(tb_othersInfo);
		panel_contains_tb_othersInfo.setSize(300, 424);
		panel_contains_tb_othersInfo.setLocation(466, 336);
		panel_contains_tb_othersInfo.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_classroom.add(panel_contains_tb_othersInfo);
		
		//정규 게임 통계 패널
		panel_statistics = new JPanel();
		panel_statistics.setSize(768,768);
		panel_statistics.setLocation(256, 0);
		panel_statistics.setLayout(null);
		panel_statistics.setVisible(false);
		add(panel_statistics);
		
		/*
		 * 각 상태별 종목 먼저 표시
		 */
		//행 - 생존자, 시체, 감염체, 영혼
		for ( int iRow = 0; iRow < 4; ++iRow )
			//열 - Best Max, Crit. Max, Best Tot, Crit. Tot
			for ( int iColumn = 0; iColumn < 4; ++iColumn )
			{
				JLabel lb_title = new JLabel();
				
				switch ( iRow * 10 + iColumn )
				{
				case 0:
					lb_title.setText("Best Survivor (MAX: Long-Live)");
					break;
				case 1:
					lb_title.setText("Crit. Survivor (MAX: Long-Live)");
					break;
				case 2:
					lb_title.setText("Best Survivor (Total: Spot)");
					break;
				case 3:
					lb_title.setText("Crit. Survivor (Total: Spot)");
					break;

				case 10:
					lb_title.setText("Best Corpse (MAX: Fame)");
					break;
				case 11:
					lb_title.setText("Crit. Corpse (MAX: Fame)");
					break;
				case 12:
					lb_title.setText("Best Corpse (Total: Heal)");
					break;
				case 13:
					lb_title.setText("Crit. Corpse (Total: Heal)");
					break;

				case 20:
					lb_title.setText("Best Infected (MAX: Massacre)");
					break;
				case 21:
					lb_title.setText("Crit. Infected (MAX: Massacre)");
					break;
				case 22:
					lb_title.setText("Best Infected (Total: Infection)");
					break;
				case 23:
					lb_title.setText("Crit. Infected (Total: Infection)");
					break;

				case 30:
					lb_title.setText("Best Soul (Freedom)");
					break;
				case 31:
					lb_title.setText("Crit. Soul (Freedom)");
					break;
				case 32:
					lb_title.setText("Best Soul (Destruction)");
					break;
				case 33:
					lb_title.setText("Crit. Soul (Destruction)");
					break;
				}
				
				lb_title.setSize(182, 20);
				lb_title.setLocation(8 + 190 * iColumn, 8 + 152 * iRow);
				
				panel_statistics.add(lb_title);
				
				//등수 - 1등 ~ 5등
				for ( int iRank = 0; iRank < 5; ++iRank )
				{
					JLabel tb = new JLabel();
					tb.setSize(92, 20);
					tb.setLocation(8 + 190 * iColumn, 8 + 152 * iRow + 20 + 20 * iRank);
					tb.setFont(font_dot_um_che);
					
					tb_statistics[iRow*4+iColumn][iRank][0] = tb;
					panel_statistics.add(tb);

					tb = new JLabel();
					tb.setSize(82, 20);
					tb.setLocation(8 + 190 * iColumn + 100, 8 + 152 * iRow + 20 + 20 * iRank);
					tb.setFont(font_dot_um_che);
					
					tb_statistics[iRow*4+iColumn][iRank][1] = tb; 
					panel_statistics.add(tb);
				}
			}
		
		/*
		 * 최종 평점 순위 표시
		 */
		//Best Player of L4G2EP1
		JLabel lb_title = new JLabel();
		lb_title.setSize(182, 20);
		lb_title.setLocation(8 + 190 * 2, 8 + 152 * 4);
		lb_title.setText("Best Player of L4G2EP1");
		panel_statistics.add(lb_title);
		
		lb_title = new JLabel();
		lb_title.setSize(182, 20);
		lb_title.setLocation(8 + 190 * 3, 8 + 152 * 4);
		lb_title.setText("Crit. Player of L4G2EP1");
		panel_statistics.add(lb_title);

		
		//등수 - 1등 ~ 5등
		for ( int iRank = 0; iRank < 5; ++iRank )
		{
			JLabel tb = new JLabel();
			tb.setSize(92, 20);
			tb.setLocation(8 + 190 * 2, 8 + 152 * 4 + 20 + 20 * iRank);
			tb.setFont(font_dot_um_che);
			
			tb_statistics[4*4][iRank][0] = tb;
			panel_statistics.add(tb);

			tb = new JLabel();
			tb.setSize(82, 20);
			tb.setLocation(8 + 190 * 2 + 100, 8 + 152 * 4 + 20 + 20 * iRank);
			tb.setFont(font_dot_um_che);
			
			tb_statistics[4*4][iRank][1] = tb; 
			panel_statistics.add(tb);

			tb = new JLabel();
			tb.setSize(92, 20);
			tb.setLocation(8 + 190 * 3, 8 + 152 * 4 + 20 + 20 * iRank);
			tb.setFont(font_dot_um_che);
			
			tb_statistics[4*4+1][iRank][0] = tb;
			panel_statistics.add(tb);

			tb = new JLabel();
			tb.setSize(82, 20);
			tb.setLocation(8 + 190 * 3 + 100, 8 + 152 * 4 + 20 + 20 * iRank);
			tb.setFont(font_dot_um_che);
			
			tb_statistics[4*4+1][iRank][1] = tb; 
			panel_statistics.add(tb);
		}
		
		JLabel label_myBest_Header = new JLabel();
		label_myBest_Header.setSize(372, 20);
		label_myBest_Header.setLocation(8, 8 + 152 * 4);
		label_myBest_Header.setText("My Best Game Number");
		panel_statistics.add(label_myBest_Header);

		tb_myBest = new JTextArea();
		tb_myBest.setFont(font_dot_um_che);
		tb_myBest.setEditable(false);

		JScrollPane panel_contains_tb_myBest = new JScrollPane(tb_myBest);
		panel_contains_tb_myBest.setSize(372, 116);
		panel_contains_tb_myBest.setLocation(8, 8 + 152 * 4 + 20 + 8);
		panel_contains_tb_myBest.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_statistics.add(panel_contains_tb_myBest);
		
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
	
	ItemListener listener_cb_perspective_changed = new ItemListener()
	{
		@Override
		public void itemStateChanged(ItemEvent arg0)
		{
			if ( state != null && state != State.Not_Started )
			{
				DrawClassroom();
				WriteMyInfo();
				WriteCellInfo();
				WriteOthersInfo();
			}
		}
	};
	
	MouseListener listener_canvas_classroom_mouse = new MouseListener()
	{
		
		@Override
		public void mouseReleased(MouseEvent e) { }
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if ( state == State.Waiting || state == State.Completed )
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
	
	ActionListener listener_cb_focusToOnePlayer_click = new ActionListener()
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
			
			cb_perspective.setEnabled(cb_focusToOnePlayer.isSelected());
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
				cb_focusToOnePlayer.setEnabled(true);
				
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

	ActionListener listener_bt_start_run10000games_click = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			tb_myBest.setEnabled(false);
			
			bt_start_run10000games.setEnabled(false);
			
			thr_batch = new Thread(run_thr_batch);
			thr_batch.start();
		}
	};
	
	Runnable run_thr_batch = new Runnable()
	{
		@Override
		public void run()
		{
			GameNumbers numbers = new GameNumbers(10000);
			numbers.Load("10000numbers.txt");
			
			Grader grader = new Grader();

			Classroom classroom = null;
			Classroom_Settings batch_settings = new Classroom_Settings(settings);
			
			for ( int iGame = 0; iGame < 10000; ++iGame )
			{
				batch_settings.game_number = numbers.data[iGame];
				classroom = new Classroom(batch_settings);
				classroom.run();
				grader.Update(batch_settings.game_number, classroom.scoreboard);
				
				for ( int iRow = 0; iRow < 4; ++iRow )
					for ( int iColumn = 0; iColumn < 4; ++iColumn )
						for ( int iRank = 0; iRank < 5; ++iRank )
						{
							if ( iColumn % 2 == 0 )
							{
								tb_statistics[iRow * 4 + iColumn][iRank][0].setText(classroom.players[grader.IDs_winners_total[iRow * 2 + iColumn / 2][iRank]].name);
								tb_statistics[iRow * 4 + iColumn][iRank][1].setText(Long.toString(grader.data_total[grader.IDs_winners_total[iRow * 2 + iColumn / 2][iRank]][iRow * 2 + iColumn / 2]));
							}
							else
							{
								tb_statistics[iRow * 4 + iColumn][iRank][0].setText(classroom.players[grader.IDs_winners_max[iRow * 2 + iColumn / 2][iRank]].name);
								tb_statistics[iRow * 4 + iColumn][iRank][1].setText(Integer.toString(grader.data_max[grader.IDs_winners_max[iRow * 2 + iColumn / 2][iRank]][iRow * 2 + iColumn / 2]));
							}
						}
				
				for ( int iRank = 0; iRank < 5; ++iRank )
				{
					tb_statistics[16][iRank][0].setText(classroom.players[grader.IDs_winners_total[8][iRank]].name);
					tb_statistics[16][iRank][1].setText(Long.toString(grader.data_total[grader.IDs_winners_total[8][iRank]][8]));
	
					tb_statistics[17][iRank][0].setText(classroom.players[grader.IDs_winners_max[8][iRank]].name);
					tb_statistics[17][iRank][1].setText(Long.toString(grader.data_max[grader.IDs_winners_max[8][iRank]][8]));
				}
				
				tb_myBest.setText(String.format("Calculating... %d / 10000 games completed.", iGame));
			}

			tb_myBest.setEnabled(true);
			StringBuilder sb = new StringBuilder();
			
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				sb.append(String.format("%s: %d (%d points)\n", classroom.players[iPlayer].name, grader.criticalGameNumbers[iPlayer], grader.data_max[iPlayer][8]));
			
			tb_myBest.setText(sb.toString());

			bt_start_run10000games.setEnabled(true);
		}
	};
	
	ChangeListener listener_rb_oneGame_changed = new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			if ( rb_oneGame.isSelected() == true )
			{
				panel_statistics.setVisible(false);
				panel_classroom.setVisible(true);
				
				if ( state != State.Not_Started )
					DrawClassroom();
			}
			else
			{
				panel_classroom.setVisible(false);
				panel_statistics.setVisible(true);
			}
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
			state = State.Completed;
			
			DrawClassroom();
			WriteMyInfo();
			WriteCellInfo();
			WriteOthersInfo();

			bt_advanceTurn_playback.setEnabled(false);
			
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
		if ( cb_focusToOnePlayer.isSelected() == false )
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
			l4g2ep1.common.Point pos = classroom.playerInfos[cb_perspective.getSelectedIndex()].position;
			
			switch (classroom.playerInfos[cb_perspective.getSelectedIndex()].state)
			{
			case Survivor:
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
					{
						cell_visibility[iRow][iColumn] = 0;
						
						if ( classroom.players[cb_perspective.getSelectedIndex()].CanSee(iColumn, iRow) == true )
							cell_visibility[iRow][iColumn] = 1;
						else
						{
							// 다른 생존자에 의해 전달받아 알게 된 영역 체크
							for ( PlayerInfo otherInfo : classroom.players[cb_perspective.getSelectedIndex()].othersInfo_withinSight )
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
				
				cell_visibility[classroom.playerInfos[cb_perspective.getSelectedIndex()].position.y][classroom.playerInfos[cb_perspective.getSelectedIndex()].position.x] = 1;
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
							if ( classroom.playerInfos[cb_perspective.getSelectedIndex()].state == PlayerInfo.State.Infected )
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
		StringBuilder sb = new StringBuilder();

		if ( state == State.Completed )
		{
			sb.append(String.format("Game #%d - Ended\n\n", classroom.gameInfo.gameNumber, classroom.gameInfo.currentTurnNumber));

			if ( cb_focusToOnePlayer.isSelected() == true )
			{
				PlayerInfo myInfo = classroom.playerInfos[cb_perspective.getSelectedIndex()];
				
				sb.append(String.format("%s | State: %s, HP: %d, Position: %s\n\n", classroom.players[myInfo.ID].name, myInfo.state.toString(), myInfo.hitPoint, myInfo.position));

				sb.append("Score:\nSMax STot CMax CTot IMax ITot\n");
				sb.append(classroom.scoreboard.scores[cb_perspective.getSelectedIndex()]);

				sb.append("\nGrade:\nSMax STot CMax CTot IMax ITot | Final\n");
				sb.append(String.format("%4d %4d %4d %4d %4d %4d |  %4d\n", classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][0], classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][1], classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][2], classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][3], classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][4], classroom.scoreboard.grades[cb_perspective.getSelectedIndex()][5], classroom.scoreboard.final_grades[cb_perspective.getSelectedIndex()]));

				sb.append("Rank:\nSMax STot CMax CTot IMax ITot | Final\n");
				sb.append(String.format("%4d %4d %4d %4d %4d %4d |  %4d\n", classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][0], classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][1], classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][2], classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][3], classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][4], classroom.scoreboard.ranks[cb_perspective.getSelectedIndex()][5], classroom.scoreboard.final_ranks[cb_perspective.getSelectedIndex()]));
			}
			else
			{
				int numberOfSurvivors = 0;
				int numberOfCorpses = 0;
				int numberOfInfecteds = 0;
				int numberOfSouls = 0;
				
				for ( PlayerInfo player : classroom.playerInfos )
				{
					switch ( player.state )
					{
					case Survivor:
						++numberOfSurvivors;
						break;
					case Corpse:
						++numberOfCorpses;
						break;
					case Infected:
						++numberOfInfecteds;
						break;
					default:
						++numberOfSouls;
						break;
					}
				}
				
				sb.append(String.format("Survivors: %d, Corpses: %d, Infecteds: %d, Souls: %d\n\n", numberOfSurvivors, numberOfCorpses, numberOfInfecteds, numberOfSouls));
				
				sb.append("Score:\nSMax STot CMax CTot IMax ITot - name\n");
				
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					sb.append(String.format("%s - %s\n", classroom.scoreboard.scores[iPlayer], classroom.players[iPlayer].name));

				sb.append("\nGrade:\nSMax STot CMax CTot IMax ITot | Final - name\n");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					sb.append(String.format("%4d %4d %4d %4d %4d %4d |  %4d - %s\n", classroom.scoreboard.grades[iPlayer][0], classroom.scoreboard.grades[iPlayer][1], classroom.scoreboard.grades[iPlayer][2], classroom.scoreboard.grades[iPlayer][3], classroom.scoreboard.grades[iPlayer][4], classroom.scoreboard.grades[iPlayer][5], classroom.scoreboard.final_grades[iPlayer], classroom.players[iPlayer].name));

				sb.append("Rank:\nSMax STot CMax CTot IMax ITot | Final - name\n");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					sb.append(String.format("%4d %4d %4d %4d %4d %4d |  %4d - %s\n", classroom.scoreboard.ranks[iPlayer][0], classroom.scoreboard.ranks[iPlayer][1], classroom.scoreboard.ranks[iPlayer][2], classroom.scoreboard.ranks[iPlayer][3], classroom.scoreboard.ranks[iPlayer][4], classroom.scoreboard.ranks[iPlayer][5], classroom.scoreboard.final_ranks[iPlayer], classroom.players[iPlayer].name));
			}
			
			int[] ID_tops = new int[9];

			for ( int iPart = 0; iPart < 8; ++iPart )
			{
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					if ( classroom.scoreboard.ranks[iPlayer][iPart] == 1 )
					{
						ID_tops[iPart] = iPlayer;
						break;
					}
			}
			
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				if ( classroom.scoreboard.final_ranks[iPlayer] == 1 )
				{
					ID_tops[8] = iPlayer;
					break;
				}

			
			sb.append(String.format("\nTop Survivor (MAX): %s | %d\n", classroom.players[ID_tops[0]].name, classroom.scoreboard.scores[ID_tops[0]].data[0]));
			sb.append(String.format("Top Survivor (Tot): %s | %d\n", classroom.players[ID_tops[1]].name, classroom.scoreboard.scores[ID_tops[1]].data[1]));

			sb.append(String.format("\nTop Corpse (MAX): %s | %d\n", classroom.players[ID_tops[2]].name, classroom.scoreboard.scores[ID_tops[2]].data[2]));
			sb.append(String.format("Top Corpse (Tot): %s | %d\n", classroom.players[ID_tops[3]].name, classroom.scoreboard.scores[ID_tops[3]].data[3]));
		
			sb.append(String.format("\nTop Infected (MAX): %s | %d\n", classroom.players[ID_tops[4]].name, classroom.scoreboard.scores[ID_tops[4]].data[4]));
			sb.append(String.format("Top Infected (Tot): %s | %d\n", classroom.players[ID_tops[5]].name, classroom.scoreboard.scores[ID_tops[5]].data[5]));

			sb.append(String.format("\nTop Soul (MAX): %s | %d\n", classroom.players[ID_tops[6]].name, classroom.scoreboard.scores[ID_tops[6]].data[6]));
			sb.append(String.format("Top Soul (Tot): %s | %d\n", classroom.players[ID_tops[7]].name, classroom.scoreboard.scores[ID_tops[7]].data[7]));

			sb.append(String.format("\nTop Player of L4G2EP1: %s | %d\n", classroom.players[ID_tops[8]].name, classroom.scoreboard.final_grades[ID_tops[8]]));
		}
		else
		{
			sb.append(String.format("Game #%d - Turn %d\n\n", classroom.gameInfo.gameNumber, classroom.gameInfo.currentTurnNumber));
	
			if ( cb_focusToOnePlayer.isSelected() == true )
			{
				PlayerInfo myInfo = classroom.playerInfos[cb_perspective.getSelectedIndex()];
				
				sb.append(String.format("%s | State: %s, HP: %d, Position: %s\n\n", classroom.players[myInfo.ID].name, myInfo.state.toString(), myInfo.hitPoint, myInfo.position));
				
				sb.append("Score:\nSMax STot CMax CTot IMax ITot\n");
				
				sb.append(classroom.scoreboard.scores[cb_perspective.getSelectedIndex()]);
			}
			else
			{
				int numberOfSurvivors = 0;
				int numberOfCorpses = 0;
				int numberOfInfecteds = 0;
				int numberOfSouls = 0;
				
				for ( PlayerInfo player : classroom.playerInfos )
				{
					switch ( player.state )
					{
					case Survivor:
						++numberOfSurvivors;
						break;
					case Corpse:
						++numberOfCorpses;
						break;
					case Infected:
						++numberOfInfecteds;
						break;
					default:
						++numberOfSouls;
						break;
					}
				}
				
				sb.append(String.format("Survivors: %d, Corpses: %d, Infecteds: %d, Souls: %d\n\n", numberOfSurvivors, numberOfCorpses, numberOfInfecteds, numberOfSouls));
				
				sb.append("Score:\nSMax STot CMax CTot IMax ITot - name\n");
				
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					sb.append(String.format("%s - %s\n", classroom.scoreboard.scores[iPlayer], classroom.players[iPlayer].name));
			}
		}

		tb_myInfo.setText(sb.toString());
		tb_myInfo.setCaretPosition(0);
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
						classroom.playerInfos[cb_perspective.getSelectedIndex()].state != PlayerInfo.State.Infected ||
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
		
		if ( cb_focusToOnePlayer.isSelected() == false )
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
			Player me = classroom.players[cb_perspective.getSelectedIndex()];
			
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
