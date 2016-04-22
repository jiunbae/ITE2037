package l4g;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import l4g.Classroom.ClassroomStateCode;
import l4g.common.Classroom_Settings;
import l4g.common.Constants;
import l4g.common.Point_Immutable;
import l4g.common.StateCode;
import l4g.data.Action;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.DrawableObject;
import loot.graphics.Layer;
import loot.graphics.TextBox;
import loot.graphics.Viewport;

/**
 * 리플레이를 위해 변경된 Presenter입니다.
 * 
 * @author Racin
 *
 */
public class Presenter_Mode5 extends GameFrame
{
	// 쓸 일은 없지만 안 적으면 경고가 뜨기에 그냥 적어 둔 버전 번호
	private static final long serialVersionUID = 1L;

	/**
	 * Presenter의 현재 상태를 나타내기 위한 열거자입니다.
	 * 
	 * @author Racin
	 *
	 */
	enum PresenterStateCode
	{
		Not_Started,
		Running,
		Waiting_Decision,
		Completed
	}
	
	class CellObject extends DrawableObject
	{
		public int row;
		public int column;
		
		public CellObject(int row, int column)
		{
			super(column * 62, row * 62, 64, 64, images.GetImage("cell_outOfSight"));
			
			this.row = row;
			this.column = column;
		}
	}
	
	/**
	 * Presenter의 현재 상태입니다.
	 */
	PresenterStateCode state;
	
	/**
	 * 전체 화면(2D정보빡스들 + 3D강의실)을 구성합니다.
	 */
	Viewport screen;
	
	/**
	 * 3D 공간 내의 2D강의실을 구성합니다.
	 */
	Layer board;
	
	/**
	 * 뭐 누를 수 있는지 보여주기 위한 빡스입니다.
	 */
	TextBox tb_keyInfo;

	/**
	 * 첫 플레이어의 상태 및 점수를 보여주기 위한 빡스입니다.
	 */
	TextBox tb_myInfo;
	
	/**
	 * 현재 마우스 포인터가 가리키는 칸에 대한 정보를 보여주기 위한 빡스입니다.
	 */
	TextBox tb_cellInfo;
	
	/**
	 * 게임이 끝났을 때 학점을 보여주기 위한 빡스입니다.
	 */
	TextBox tb_grades;

	/**
	 * 강의실 내의 각 칸을 나타내기 위한 친구들입니다.
	 */
	CellObject[][] cells;
	
	/**
	 * 현재 마우스 포인터가 가리키는 칸입니다.
	 * 마우스 포인터가 안 가리키고 있는 경우 null입니다.
	 */
	CellObject cell_focused;
	
	/**
	 * 현재 마우스 포인터가 가리키는 칸의 세로 좌표입니다.
	 */
	int row_focused;

	/**
	 * 현재 마우스 포인터가 가리키는 칸의 가로 좌표입니다.
	 */
	int column_focused;

	/**
	 * 마우스 포인터가 가리키는 칸을 장식하기 위한 친구입니다.
	 */
	DrawableObject obj_cursor;

	/**
	 * 현재 주시중인 플레이어가 있는 칸을 장식하기 위한 친구입니다.
	 */
	DrawableObject obj_myPosition;

	/**
	 * 게임을 백그라운드에서 돌리기 위한 스레드입니다.
	 * 스레드가 뭔지는 지금으로서는 몰라도 됩니다.
	 */
	Thread thr;
	
	/**
	 * 게임이 진행되는 강의실입니다.
	 */
	Classroom classroom;

	/**
	 * 강의실을 구성하기 위한 설정 필드입니다.
	 */
	Classroom_Settings cs;

	/**
	 * 직접 조작 가능한 플레이어의 ID입니다.
	 */
	final int ID_playerToFocus = 0;
	
	/**
	 * 새 게임을 시작하기 위해 이전 게임을 강제 진행하고 있는지 여부를 나타냅니다.
	 */
	boolean isRestarting = false;
	
	public Presenter_Mode5(GameFrameSettings gfs, Classroom_Settings cs)
	{
		super(gfs);
		this.cs = new Classroom_Settings(cs);
		
		this.cs.callback_StartGame = () ->
		{
			tb_grades.trigger_hide = true;
			return true;
		};
		
		/*
		 * 의사 결정 요청을 받을 때마다 화면을 갱신하고 게임을 일시 정지 
		 */
		this.cs.callback_RequestDecision = () ->
		{
			state = PresenterStateCode.Waiting_Decision;
			UpdateScreen();
			UpdateMyInfo();
			UpdateCellInfo();
			return false;
		};
		
		/*
		 * 게임이 끝나면 전체 강의실 내 플레이어 분포를 보여줌
		 */
		this.cs.callback_EndGame = () ->
		{
			state = PresenterStateCode.Completed;
			RevealScreen();
			
			tb_grades.text = String.format(
					"Score| Grade(Rank):\n" +
					"          SMax            STot            CMax            CTot            IMax            ITot |      Final\n" +
					"%4d|%4d(%3d)  %4d|%4d(%3d)  %4d|%4d(%3d)  %4d|%4d(%3d)  %4d|%4d(%3d)  %4d|%4d(%3d) |  %4d(%3d)\n", classroom.scores[ID_playerToFocus][0], classroom.grades[ID_playerToFocus][0], classroom.ranks[ID_playerToFocus][0], classroom.scores[ID_playerToFocus][1], classroom.grades[ID_playerToFocus][1], classroom.ranks[ID_playerToFocus][1], classroom.scores[ID_playerToFocus][2], classroom.grades[ID_playerToFocus][2], classroom.ranks[ID_playerToFocus][2], classroom.scores[ID_playerToFocus][3], classroom.grades[ID_playerToFocus][3], classroom.ranks[ID_playerToFocus][3], classroom.scores[ID_playerToFocus][4], classroom.grades[ID_playerToFocus][4], classroom.ranks[ID_playerToFocus][4], classroom.scores[ID_playerToFocus][5], classroom.grades[ID_playerToFocus][5], classroom.ranks[ID_playerToFocus][5], classroom.final_grades[ID_playerToFocus], classroom.final_ranks[ID_playerToFocus]);

			tb_grades.trigger_hide = false;
			UpdateMyInfo();
			UpdateCellInfo();
			return true;
		};
		
		state = PresenterStateCode.Not_Started;
		
		/*
		 * 이미지 파일 읽어 오기
		 */
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
		images.LoadImage("Images/myPosition_survivor.png", "myPosition_survivor");
		images.LoadImage("Images/myPosition_corpse.png", "myPosition_corpse");
		images.LoadImage("Images/myPosition_infected.png", "myPosition_infected");
		images.LoadImage("Images/cursor.png", "cursor");
		images.LoadImage("Images/cursor_invalid.png", "cursor_invalid");

		/*
		 * 키를 버튼에 매핑
		 */
		inputs.BindKey(KeyEvent.VK_ENTER, 0);
		inputs.BindMouseButton(MouseEvent.BUTTON1, 1);
		inputs.BindKey(KeyEvent.VK_F1, 2);
		inputs.BindKey(KeyEvent.VK_F2, 3);
		inputs.BindKey(KeyEvent.VK_F3, 4);
		inputs.BindKey(KeyEvent.VK_1, 5);
		inputs.BindKey(KeyEvent.VK_2, 6);
		inputs.BindKey(KeyEvent.VK_3, 7);
		inputs.BindKey(KeyEvent.VK_4, 8);
		inputs.BindKey(KeyEvent.VK_5, 9);
		inputs.BindKey(KeyEvent.VK_6, 10);
		inputs.BindKey(KeyEvent.VK_NUMPAD1, 11);
		inputs.BindKey(KeyEvent.VK_NUMPAD2, 12);
		inputs.BindKey(KeyEvent.VK_NUMPAD3, 13);
		inputs.BindKey(KeyEvent.VK_NUMPAD4, 14);
		inputs.BindKey(KeyEvent.VK_NUMPAD5, 15);
		inputs.BindKey(KeyEvent.VK_NUMPAD6, 16);
		inputs.BindKey(KeyEvent.VK_UP, 17);
		inputs.BindKey(KeyEvent.VK_LEFT, 18);
		inputs.BindKey(KeyEvent.VK_RIGHT, 19);
		inputs.BindKey(KeyEvent.VK_DOWN, 20);
		inputs.BindKey(KeyEvent.VK_SPACE, 21);

		cells = new CellObject[Constants.Classroom_Height][Constants.Classroom_Width];
	}

	@Override
	public boolean Initialize()
	{
		/* -----------------------------------------------------------------------------------
		 * 화면 표시용 친구들 초기화
		 */
		LoadFont("돋움체 12");
		
		screen = new Viewport(0, 0, settings.canvas_width, settings.canvas_height);
		screen.pointOfView_z = 1.0;
		screen.pointOfView_x = -120;
		screen.view_baseDistance = 1;
		screen.view_maxDistance = Double.MAX_VALUE;
		
		tb_keyInfo = new TextBox(8, 8, 210, 110,
				"-------- 뭐 누를 수 있나 --------\n" +
				"좌클릭 / 엔터 키: 의사 결정 수행\n" +
				"F1 키: 새 게임 시작 / 재시작\n" +
				"F2 키: 게임 도중 런타임 오류 발생\n" +
				"F3 키: 직접 감염 여부 토글\n" +
				"1~6키: 마우스로 가리킨 칸 정보 중\n" +
				"       특정 내역을 콘솔에 출력");
		screen.children.add(tb_keyInfo);
		
		tb_myInfo = new TextBox(8, settings.canvas_height - 241, 210, 85);
		screen.children.add(tb_myInfo);
		
		tb_cellInfo = new TextBox(8, settings.canvas_height - 148, 210, 140);
		screen.children.add(tb_cellInfo);
		
		tb_grades = new TextBox((settings.canvas_width - 210) / 2 + 210 - 316, settings.canvas_height / 2 - 30, 660, 60);
		tb_grades.trigger_hide = true;
		screen.children.add(tb_grades);
		
		board = new Layer(0, 0, 0, 31 * Constants.Classroom_Width + 1, 31 * Constants.Classroom_Height + 1);
		screen.children.add(board);

		obj_cursor = new DrawableObject(0, 0, 64, 64, images.GetImage("cursor"));
		obj_cursor.trigger_hide = true;
		obj_cursor.trigger_ignoreDuringHitTest = true;
		board.children.add(obj_cursor);
		
		obj_myPosition = new DrawableObject(0, 0, 64, 64, images.GetImage("myPosition_survivor"));
		obj_myPosition.trigger_hide = true;
		obj_myPosition.trigger_ignoreDuringHitTest = true;
		board.children.add(obj_myPosition);
		
		CellObject cell; 
		
		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
			{
				cell = new CellObject(iRow, iColumn);
				cells[iRow][iColumn] = cell;
				board.children.add(cell);
			}
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		inputs.AcceptInputs();
		
		//좌클릭 또는 엔터 키를 누르면
		if ( inputs.buttons[0].IsPressedNow() == true || inputs.buttons[1].IsPressedNow() == true || inputs.buttons[21].IsPressedNow() == true )
		{
			//의사 결정 요청이 들어와 있는 경우 턴 진행
			if ( state == PresenterStateCode.Waiting_Decision )
			{
				synchronized ( classroom.lock )
				{
					classroom.pos_acceptedFromControllablePlayer.row = row_focused;
					classroom.pos_acceptedFromControllablePlayer.column = column_focused;
					state = PresenterStateCode.Running;
					classroom.lock.notifyAll();
				}
			}
		}
		
		// F1 키를 누르면 새 게임 시작 / 재시작
		if ( inputs.buttons[2].IsPressedNow() == true )
		{
			//게임이 끝났거나 아직 안 시작한 경우 시작
			if ( state == PresenterStateCode.Not_Started || state == PresenterStateCode.Completed )
			{
				thr = new Thread(new Runnable()
				{
					
					@Override
					public void run()
					{
						classroom = new Classroom(cs);
						classroom.Initialize();
						classroom.Start();
					}
				});
				
				thr.start();
				state = PresenterStateCode.Running;
			}
			//게임이 진행중인 경우 재시작
			else if ( isRestarting == false )
			{
				isRestarting = true;
				
				synchronized ( classroom.lock )
				{
					classroom.callback_RequestDecision = null;
					classroom.callback_EndGame = null;
					classroom.lock.notifyAll();
				}
				
				try
				{
					thr.join();
				}
				catch ( InterruptedException e )
				{
				}

				thr = new Thread(new Runnable()
				{
					
					@Override
					public void run()
					{
						classroom = new Classroom(cs);
						classroom.Initialize();
						classroom.Start();
					}
				});
				
				thr.start();
				state = PresenterStateCode.Running;
				
				isRestarting = false;
			}
		}
		
		// F2 키를 누르면 런타임 오류 발생
		if ( inputs.buttons[3].IsPressedNow() == true && state != PresenterStateCode.Not_Started )
		{
			synchronized ( classroom.lock )
			{
				if ( state == PresenterStateCode.Waiting_Decision )
				{
					classroom.pos_acceptedFromControllablePlayer.row = -1;
					state = PresenterStateCode.Running;
					classroom.lock.notifyAll();
				}
			}
		}
		
		// F3 키를 누르면 직접 감염 수락 여부 토글
		if ( inputs.buttons[4].IsPressedNow() == true && state != PresenterStateCode.Not_Started )
		{
			classroom.players[ID_playerToFocus].trigger_acceptDirectInfection = !classroom.players[ID_playerToFocus].trigger_acceptDirectInfection;
			UpdateMyInfo();
		}
		
		// 현재 마우스 커서 위치가 변경되었다면 현재 좌표가 '어떤 칸을 가리키고 있는지' 판별
		if ( inputs.isMouseCursorMoved == true && screen.GetObjectAt(inputs.pos_mouseCursor) == board )
		{
			cell_focused = (CellObject)board.GetObjectAt(inputs.pos_mouseCursor);
			
			if ( cell_focused != null )
			{
				obj_cursor.x = cell_focused.x;
				obj_cursor.y = cell_focused.y;
				obj_cursor.trigger_hide = false;
				row_focused = cell_focused.row;
				column_focused = cell_focused.column;

				UpdateCellInfo();
			}
		}
		
		if ( inputs.buttons[17].IsPressedNow() == true && row_focused > 0 )
		{
			--row_focused;
			cell_focused = cells[row_focused][column_focused];
			obj_cursor.x = cell_focused.x;
			obj_cursor.y = cell_focused.y;
			obj_cursor.trigger_hide = false;
			
			UpdateCellInfo();
		}
		
		if ( inputs.buttons[18].IsPressedNow() == true && column_focused > 0 )
		{
			--column_focused;
			cell_focused = cells[row_focused][column_focused];
			obj_cursor.x = cell_focused.x;
			obj_cursor.y = cell_focused.y;
			obj_cursor.trigger_hide = false;
			
			UpdateCellInfo();
		}
		
		if ( inputs.buttons[19].IsPressedNow() == true && column_focused < Constants.Classroom_Width - 1 )
		{
			++column_focused;
			cell_focused = cells[row_focused][column_focused];
			obj_cursor.x = cell_focused.x;
			obj_cursor.y = cell_focused.y;
			obj_cursor.trigger_hide = false;
			
			UpdateCellInfo();
		}
		
		if ( inputs.buttons[20].IsPressedNow() == true && row_focused < Constants.Classroom_Height - 1 )
		{
			++row_focused;
			cell_focused = cells[row_focused][column_focused];
			obj_cursor.x = cell_focused.x;
			obj_cursor.y = cell_focused.y;
			obj_cursor.trigger_hide = false;
			
			UpdateCellInfo();
		}

		/* -----------------------------------------------------------------------------------
		 * 숫자 키 입력을 받았을 때 적절한 내용을 콘솔에 출력하는 부분
		 */
		
		//1. Survivors
		if ( ( inputs.buttons[11].IsPressedNow() == true || inputs.buttons[5].IsPressedNow() == true )&& state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Survivors at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Survivor )
					System.out.println(classroom.players[player.ID].name);
			});
			
			System.out.println();
		}
		
		//2. Corpses
		if ( ( inputs.buttons[12].IsPressedNow() == true || inputs.buttons[6].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Corpses at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Corpse )
					System.out.println(classroom.players[player.ID].name + " - HP: " + player.HP + ", Cooldown: " + player.transition_cooldown + " turn(s)");
			});

			System.out.println();
		}
		
		//3. Infecteds
		if ( ( inputs.buttons[13].IsPressedNow() == true || inputs.buttons[7].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Infecteds at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Infected )
					System.out.println(classroom.players[player.ID].name + " - HP: " + player.HP);
			});

			System.out.println();
		}
		
		//4. Moves
		if ( ( inputs.buttons[14].IsPressedNow() == true || inputs.buttons[8].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Moves on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Actions(action ->
			{
				if ( action.type == Action.TypeCode.Move )
					System.out.printf("%s: %s -> %s as %s\n",
							classroom.players[action.actorID].name,
							action.location_from, action.location_to,
							classroom.playerInfos[action.actorID].state);
			});
			
			System.out.println();
		}			
		
		//5. Spawns
		if ( ( inputs.buttons[15].IsPressedNow() == true || inputs.buttons[9].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Spawns on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Actions(action ->
			{
				if ( action.type == Action.TypeCode.Spawn )
					System.out.printf("%s\n",
							classroom.players[action.actorID].name);
			});
			
			System.out.println();
		}			
		
		//6. Reactions
		if ( ( inputs.buttons[16].IsPressedNow() == true || inputs.buttons[10].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Reactions on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[ID_playerToFocus].cells[row_focused][column_focused].ForEach_Reactions(reaction ->
			{
				if ( reaction.subjectID == reaction.objectID )
				{
					System.out.printf("%s: %s\n",
							reaction.type,
							classroom.players[reaction.subjectID].name,
							classroom.players[reaction.objectID].name);
				}
				else
				{
					if ( reaction.location_subject == reaction.location_object )
					{
						System.out.printf("%s: %s -> %s\n",
								reaction.type,
								classroom.players[reaction.subjectID].name,
								classroom.players[reaction.objectID].name);
						
					}
					else
					{
						System.out.printf("%s: %s(%d, %d) -> %s(%d, %d)\n",
								reaction.type,
								classroom.players[reaction.subjectID].name,
								reaction.location_subject.row,
								reaction.location_subject.column,
								classroom.players[reaction.objectID].name,
								reaction.location_object.row,
								reaction.location_object.column);
					}
				}
			});
			
			System.out.println();
		}			
		
		return true;
	}

	@Override
	public void Draw(long timeStamp)
	{
		BeginDraw();
		ClearScreen();
		
		screen.Draw(g);
		
		EndDraw();
	}
	
	/**
	 * 주 플레이어의 시야에 맞도록 강의실 화면을 갱신합니다.
	 */
	void UpdateScreen()
	{
		DrawableObject cell;
		
		boolean isSurvivorHere = false;
		boolean isCorpseHere = false;
		boolean isInfectedHere = false;
		boolean isWithinSight = false;
		boolean isWithinSpottedArea = false;

		Point_Immutable pos = classroom.players[ID_playerToFocus].myInfo.position;
		
		ArrayList<PlayerInfo> survivorsWithinSight = new ArrayList<>();
		
		if ( classroom.players[ID_playerToFocus].myInfo.state == StateCode.Survivor )
		{
			for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
				for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
					for ( PlayerInfo player : classroom.cells[iRow][iColumn].Select_Players(player->player.state == StateCode.Survivor) )
						if ( player.position.GetDistance(pos) <= 2 )
							survivorsWithinSight.add(player);
		}
		
		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
			{
				isSurvivorHere = classroom.players[ID_playerToFocus].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Survivor) != 0;
				isCorpseHere = classroom.players[ID_playerToFocus].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Corpse) != 0;
				isInfectedHere = classroom.players[ID_playerToFocus].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Infected) != 0;
				
				switch ( classroom.playerStats[ID_playerToFocus].state )
				{
				case Survivor:
					isWithinSight = pos.GetDistance(iRow, iColumn) <= 2;
					break;
				case Corpse:
					isWithinSight = pos.GetDistance(iRow, iColumn) == 0;
					break;
				case Infected:
					isWithinSight = pos.row - iRow <= 2 && pos.row - iRow >= -2 && pos.column - iColumn <= 2 && pos.column - iColumn >= -2;
					break;
				default:
					isWithinSight = true;
					break;
				}
				
				isWithinSpottedArea = false;
				
				for ( PlayerInfo player : survivorsWithinSight )
				{
					if ( player.position.GetDistance(iRow, iColumn) <= 2 )
					{
						isWithinSpottedArea = true;
						break;
					}
				}
				
				cell = cells[iRow][iColumn];
				
				String imageName = "cell";
				
				if ( isSurvivorHere == false && isCorpseHere == false && isInfectedHere == false )
				{
					if ( isWithinSight == true )
						imageName += "_empty";
					else if ( isWithinSpottedArea == true )
						imageName += "_empty_spot";
					else
						imageName += "_outOfSight";
				}
				else
				{
					if ( isSurvivorHere == true )
						imageName += "_survivor";
					if ( isInfectedHere == true )
						imageName += "_infected";
					if ( isCorpseHere == true )
						imageName += "_corpse";
					
					if ( isWithinSight == false )
						imageName += "_spot";
				}
				
				cell.image = images.GetImage(imageName);
				
				if ( pos.GetDistance(iRow, iColumn) == 0 )
				{
					obj_myPosition.x = cell.x;
					obj_myPosition.y = cell.y;
				}
			}
		
		switch ( classroom.players[ID_playerToFocus].myInfo.state )
		{
		case Survivor:
			obj_myPosition.image = images.GetImage("myPosition_survivor");
			obj_myPosition.trigger_hide = false;
			break;
		case Corpse:
			obj_myPosition.image = images.GetImage("myPosition_corpse");
			obj_myPosition.trigger_hide = false;
			break;
		case Infected:
			obj_myPosition.image = images.GetImage("myPosition_infected");
			obj_myPosition.trigger_hide = false;
			break;
		default:
			obj_myPosition.trigger_hide = true;
			break;
		}
	}
	
	/**
	 * 전체 플레이어를 표시하되
	 * 주 플레이어의 시야 또한 파악할 수 있도록 강의실 화면을 갱신합니다.
	 */
	void RevealScreen()
	{
		DrawableObject cell;
		
		int[][] occurances = new int[Constants.Classroom_Height][Constants.Classroom_Width];
		int occurance;

		boolean isWithinSight = false;
		
		Point_Immutable pos = classroom.playerStats[ID_playerToFocus].position;

		for ( PlayerStat stat : classroom.playerStats )
		{
			switch ( stat.state )
			{
			case Survivor:
				occurances[stat.position.row][stat.position.column] |= 1;
				break;
			case Corpse:
				occurances[stat.position.row][stat.position.column] |= 2;
				break;
			case Infected:
				occurances[stat.position.row][stat.position.column] |= 4;
				break;
			default:
				break;
			}
		}
		
		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
			{
				cell = cells[iRow][iColumn];
				occurance = occurances[iRow][iColumn];
				
				switch ( classroom.playerStats[ID_playerToFocus].state )
				{
				case Survivor:
					isWithinSight = pos.GetDistance(iRow, iColumn) <= 2;
					break;
				case Corpse:
					isWithinSight = pos.GetDistance(iRow, iColumn) == 0;
					break;
				case Infected:
					isWithinSight = pos.row - iRow <= 2 && pos.row - iRow >= -2 && pos.column - iColumn <= 2 && pos.column - iColumn >= -2;
					break;
				default:
					isWithinSight = true;
					break;
				}
				
				String imageName = "cell";
				
				if ( (occurance & 7) == 0 )
				{
					if ( isWithinSight == true )
						imageName += "_empty";
					else
						imageName += "_outOfSight";
				}
				else
				{
					if ( (occurance & 1) == 1 )
						imageName += "_survivor";
					if ( (occurance & 4) == 4 )
						imageName += "_infected";
					if ( (occurance & 2) == 2 )
						imageName += "_corpse";
					
					if ( isWithinSight == false )
						imageName += "_spot";
				}
				
				cell.image = images.GetImage(imageName);

				if ( pos.GetDistance(iRow, iColumn) == 0 )
				{
					obj_myPosition.x = cell.x;
					obj_myPosition.y = cell.y;
				}
			}

		switch ( classroom.playerStats[ID_playerToFocus].state )
		{
		case Survivor:
			obj_myPosition.image = images.GetImage("myPosition_survivor");
			obj_myPosition.trigger_hide = false;
			break;
		case Corpse:
			obj_myPosition.image = images.GetImage("myPosition_corpse");
			obj_myPosition.trigger_hide = false;
			break;
		case Infected:
			obj_myPosition.image = images.GetImage("myPosition_infected");
			obj_myPosition.trigger_hide = false;
			break;
		default:
			obj_myPosition.trigger_hide = true;
			break;
		}
	}
	
	/**
	 * 첫 플레이어에 대한 정보를 갱신합니다.
	 */
	void UpdateMyInfo()
	{
		String todoString = "";
		
		switch ( classroom.state )
		{
		case Waiting_Decision_Survivor_Move:
			todoString = "할 일: 생존자 이동";
			break;
		case Waiting_Decision_Corpse_Stay:
			todoString = String.format("할 일: 시체 대기(%d 턴 남음)", classroom.playerInfos[ID_playerToFocus].transition_cooldown);
			break;
		case Waiting_Decision_Infected_Move:
			todoString = "할 일: 감염체 이동";
			break;
		case Waiting_Decision_Soul_Stay:
			todoString = String.format("할 일: 영혼 대기(%d 턴 남음)", classroom.playerInfos[ID_playerToFocus].transition_cooldown == -1 ? 0 : classroom.playerInfos[ID_playerToFocus].transition_cooldown);
			break;
		case Waiting_Decision_Soul_Spawn:
			todoString = "할 일: 영혼 부활";
			break;
		case Running:
			todoString = "할 일: (진행중...)";
			break;
		case Completed:
			todoString = "할 일: F1을 눌러 새 게임 시작";
			break;
		default:
			break;
		}
		
		tb_myInfo.text = String.format(
				(classroom.players[ID_playerToFocus].trigger_acceptDirectInfection == true ?
						"%s (직접 감염 ON)\n" :
						"%s (직접 감염 OFF)\n") +
				"%s at %s, HP: %d\n" +
				"SMax STot CMax CTot IMax ITot\n" +
				"%4d %4d %4d %4d %4d %4d\n" + 
				todoString,
				classroom.players[ID_playerToFocus].name,
				classroom.playerStats[ID_playerToFocus].state,
				classroom.playerInfos[ID_playerToFocus].position,
				classroom.playerStats[ID_playerToFocus].HP,
				classroom.scores[ID_playerToFocus][0],
				classroom.scores[ID_playerToFocus][1],
				classroom.scores[ID_playerToFocus][2],
				classroom.scores[ID_playerToFocus][3],
				classroom.scores[ID_playerToFocus][4],
				classroom.scores[ID_playerToFocus][5]);
	}
	
	/**
	 * 마우스 포인터가 가리키는 칸에 대한 정보를 갱신합니다.
	 */
	void UpdateCellInfo()
	{
		if ( state == PresenterStateCode.Waiting_Decision )
		{
			if ( row_focused != -1 )
			{
				CellInfo info = classroom.players[ID_playerToFocus].cells[row_focused][column_focused];
				
				switch ( classroom.playerInfos[ID_playerToFocus].state )
				{
				case Survivor:
					if ( classroom.playerInfos[ID_playerToFocus].position.GetDistance(row_focused, column_focused) == 1 )
						obj_cursor.image = images.GetImage("cursor");
					else
						obj_cursor.image = images.GetImage("cursor_invalid");
					break;
				case Corpse:
					obj_cursor.image = images.GetImage("cursor_invalid");
					break;
				case Infected:
					if ( classroom.playerInfos[ID_playerToFocus].position.GetDistance(row_focused, column_focused) <= 1 )
						obj_cursor.image = images.GetImage("cursor");
					else
						obj_cursor.image = images.GetImage("cursor_invalid");
					break;
				case Soul:
					if ( classroom.state == ClassroomStateCode.Waiting_Decision_Soul_Spawn )
						obj_cursor.image = images.GetImage("cursor");
					else
						obj_cursor.image = images.GetImage("cursor_invalid");
					break;
				} 
				
				tb_cellInfo.text = String.format(
						"Game#%d Turn#%d\n" +
						"---- ---- ---- ---- ---- ----\n" +
						"Informations of (%d, %d)\n" +
						"1. Survivors: %d\n" +
						"2.   Corpses: %d\n" +
						"3. Infecteds: %d\n" +
						"4.     Moves: %d\n" +
						"5.    Spawns: %d\n" +
						"6. Reactions: %d",
						classroom.gameNumber,
						classroom.turnNumber,
						row_focused,
						column_focused,
						info.CountIf_Players(player -> player.state == StateCode.Survivor),
						info.CountIf_Players(player -> player.state == StateCode.Corpse),
						info.CountIf_Players(player -> player.state == StateCode.Infected),
						info.CountIf_Actions(action -> action.type == Action.TypeCode.Move),
						info.CountIf_Actions(action -> action.type == Action.TypeCode.Spawn),
						info.Count_Reactions());
			}
			else
			{
				tb_cellInfo.text = String.format(
						"Game#%d Turn#%d\n" +
						"---- ---- ---- ---- ---- ----\n",
						classroom.gameNumber,
						classroom.turnNumber);
			}
		}
		else if ( state == PresenterStateCode.Completed )
		{
			tb_cellInfo.text = String.format(
					"Game#%d Turn#%d\n" +
					"---- ---- ---- ---- ---- ----\n",
					classroom.gameNumber,
					classroom.turnNumber);
		}
		else
		{
			tb_cellInfo.text = "";
		}
	}
}
