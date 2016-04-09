package l4g;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

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
 * 보다 직관적인 테스트를 위해 강의실 상황을 시각적으로 보여줍니다.
 * 
 * @author Racin
 *
 */
public class Presenter_Mode3 extends GameFrame
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
		Waiting_Enter,
		Completed
	}
	
	/**
	 * Presenter의 현재 상태입니다.
	 */
	PresenterStateCode state;
	
	/**
	 * ESC 키를 눌렀는지(게임 종료 시점까지 자동으로 진행하고 있는지) 여부를 나타냅니다.
	 */
	boolean isSkipping;
	
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
	 * 강의실 내의 각 칸을 나타내기 위한 친구들입니다.
	 */
	DrawableObject[][] cells;
	
	/**
	 * 현재 마우스 포인터가 가리키는 칸입니다.
	 * 마우스 포인터가 안 가리키고 있는 경우 null입니다.
	 */
	DrawableObject cell_focused;
	
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
	 * 현재 첫 플레이어가 있는 칸을 장식하기 위한 친구입니다.
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
	
	public Presenter_Mode3(GameFrameSettings gfs, Classroom_Settings cs)
	{
		super(gfs);
		this.cs = new Classroom_Settings(cs);

		/*
		 * 매 턴이 시작될 때 화면을 갱신하고 게임을 일시 정지 
		 */
		this.cs.callback_StartTurn = () ->
		{
			if ( isSkipping == true )
				return true;
			
			UpdateScreen();
			UpdateMyInfo();
			state = PresenterStateCode.Waiting_Enter;
			return false;
		};
		
		/*
		 * 게임이 끝나면 전체 강의실 내 플레이어 분포를 보여줌
		 */
		this.cs.callback_EndGame = () ->
		{
			state = PresenterStateCode.Completed;
			RevealScreen();
			UpdateMyInfo();
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
		inputs.BindKey(KeyEvent.VK_UP, 0);
		inputs.BindKey(KeyEvent.VK_LEFT, 1);
		inputs.BindKey(KeyEvent.VK_RIGHT, 2);
		inputs.BindKey(KeyEvent.VK_DOWN, 3);
		inputs.BindKey(KeyEvent.VK_OPEN_BRACKET, 4);
		inputs.BindKey(KeyEvent.VK_CLOSE_BRACKET, 5);
		inputs.BindKey(KeyEvent.VK_ENTER, 6);
		inputs.BindKey(KeyEvent.VK_BACK_SPACE, 7);
		inputs.BindKey(KeyEvent.VK_1, 8);
		inputs.BindKey(KeyEvent.VK_2, 9);
		inputs.BindKey(KeyEvent.VK_3, 10);
		inputs.BindKey(KeyEvent.VK_4, 11);
		inputs.BindKey(KeyEvent.VK_5, 12);
		inputs.BindKey(KeyEvent.VK_6, 13);
		inputs.BindKey(KeyEvent.VK_NUMPAD1, 14);
		inputs.BindKey(KeyEvent.VK_NUMPAD2, 15);
		inputs.BindKey(KeyEvent.VK_NUMPAD3, 16);
		inputs.BindKey(KeyEvent.VK_NUMPAD4, 17);
		inputs.BindKey(KeyEvent.VK_NUMPAD5, 18);
		inputs.BindKey(KeyEvent.VK_NUMPAD6, 19);

		cells = new DrawableObject[Constants.Classroom_Height][Constants.Classroom_Width];
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
				"화살표 키: 카메라 이동\n" +
				"대괄호 키: 카메라 거리 조절\n" +
				"엔터 키: 시작 / 다음 턴으로\n" +
				"빽스페이스 키: 마지막 턴으로\n" +
				"1~6키: 마우스로 가리킨 칸 정보 중\n" +
				"       특정 내역을 콘솔에 출력");
		screen.children.add(tb_keyInfo);
		
		tb_myInfo = new TextBox(8, settings.canvas_height - 231, 210, 75);
		screen.children.add(tb_myInfo);
		
		tb_cellInfo = new TextBox(8, settings.canvas_height - 148, 210, 140);
		screen.children.add(tb_cellInfo);
		
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
		
		DrawableObject cell; 
		
		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
			{
				cell = new DrawableObject(iColumn * 62, iRow * 62, 64, 64, images.GetImage("cell_outOfSight"));
				cells[iRow][iColumn] = cell;
				board.children.add(cell);
			}
		
		return true;
	}

	@Override
	public boolean Update(long timeStamp)
	{
		inputs.AcceptInputs();
		
		//방향키를 누르는 동안 카메라 이동
		if ( inputs.buttons[0].isPressed == true)
			screen.pointOfView_y += 3;
		if ( inputs.buttons[1].isPressed == true)
			screen.pointOfView_x -= 3;
		if ( inputs.buttons[2].isPressed == true)
			screen.pointOfView_x += 3;
		if ( inputs.buttons[3].isPressed == true)
			screen.pointOfView_y -= 3;
		
		//대괄호 키를 누르는 동안 카메라 높이 조절
		if ( inputs.buttons[4].isPressed == true && screen.pointOfView_z < 2)
			screen.pointOfView_z += 0.05;
		if ( inputs.buttons[5].isPressed == true && screen.pointOfView_z > 0.3)
			screen.pointOfView_z -= 0.05;
		
		//엔터 키를 누르면
		if ( inputs.buttons[6].IsPressedNow() == true )
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
				isSkipping = false;
				state = PresenterStateCode.Running;
			}
			//게임이 아직 안 끝났으며 턴 시작 준비가 끝난 경우 턴 진행
			else
			{
				synchronized ( classroom.lock )
				{
					if ( state == PresenterStateCode.Waiting_Enter )
					{
						state = PresenterStateCode.Running;
						classroom.lock.notifyAll();
					}
				}
			}
		}
		
		// ESC 키를 누르면 게임을 끝까지 걍 진행
		if ( inputs.buttons[7].IsPressedNow() == true && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed )
		{
			isSkipping = true;
			
			synchronized ( classroom.lock )
			{
				if ( state == PresenterStateCode.Waiting_Enter )
				{
					state = PresenterStateCode.Running;
					classroom.lock.notifyAll();
				}
			}
		}

		// 현재 마우스 커서 위치를 통해 '어떤 칸을 가리키고 있는지' 판별
		cell_focused = null;
		obj_cursor.trigger_hide = true;
		
		if ( screen.GetObjectAt(inputs.pos_mouseCursor) == board )
		{
			cell_focused = (DrawableObject)board.GetObjectAt(inputs.pos_mouseCursor);
			
			if ( cell_focused != null )
			{
				obj_cursor.x = cell_focused.x;
				obj_cursor.y = cell_focused.y;
				obj_cursor.trigger_hide = false;
			}
		}

		// 현재 가리키고 있는 칸에 대한 정보 갱신
		UpdateCellInfo();

		/* -----------------------------------------------------------------------------------
		 * 숫자 키 입력을 받았을 때 적절한 내용을 콘솔에 출력하는 부분
		 */
		
		//1. Survivors
		if ( ( inputs.buttons[14].IsPressedNow() == true || inputs.buttons[8].IsPressedNow() == true )&& state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Survivors at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Survivor )
					System.out.println(classroom.players[player.ID].name);
			});
			
			System.out.println();
		}
		
		//2. Corpses
		if ( ( inputs.buttons[15].IsPressedNow() == true || inputs.buttons[9].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Corpses at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Corpse )
					System.out.println(classroom.players[player.ID].name + " - HP: " + player.HP + ", Cooldown: " + player.transition_cooldown + " turn(s)");
			});

			System.out.println();
		}
		
		//3. Infecteds
		if ( ( inputs.buttons[16].IsPressedNow() == true || inputs.buttons[10].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Infecteds at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Infected )
					System.out.println(classroom.players[player.ID].name + " - HP: " + player.HP);
			});

			System.out.println();
		}
		
		//4. Moves
		if ( ( inputs.buttons[17].IsPressedNow() == true || inputs.buttons[11].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Moves on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Actions(action ->
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
		if ( ( inputs.buttons[18].IsPressedNow() == true || inputs.buttons[12].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Spawns on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Actions(action ->
			{
				if ( action.type == Action.TypeCode.Spawn )
					System.out.printf("%s\n",
							classroom.players[action.actorID].name);
			});
			
			System.out.println();
		}			
		
		//6. Reactions
		if ( ( inputs.buttons[19].IsPressedNow() == true || inputs.buttons[13].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started && state != PresenterStateCode.Completed && cell_focused != null)
		{
			System.out.printf("Reactions on (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber - 1);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Reactions(reaction ->
			{
				System.out.printf("%s: %s -> %s\n",
					reaction.type,
					classroom.players[reaction.subjectID].name,
					classroom.players[reaction.objectID].name);
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
	 * 첫 플레이어의 시야에 맞도록 강의실 화면을 갱신합니다.
	 */
	void UpdateScreen()
	{
		DrawableObject cell;
		
		boolean isSurvivorHere = false;
		boolean isCorpseHere = false;
		boolean isInfectedHere = false;
		boolean isWithinSight = false;
		boolean isWithinSpottedArea = false;

		Point_Immutable pos = classroom.players[0].myInfo.position;
		
		ArrayList<PlayerInfo> survivorsWithinSight = new ArrayList<>();
		
		if ( classroom.players[0].myInfo.state == StateCode.Survivor )
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
				isSurvivorHere = classroom.players[0].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Survivor) != 0;
				isCorpseHere = classroom.players[0].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Corpse) != 0;
				isInfectedHere = classroom.players[0].cells[iRow][iColumn].CountIf_Players(player -> player.state == StateCode.Infected) != 0;
				
				switch ( classroom.playerStats[0].state )
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
		
		switch ( classroom.players[0].myInfo.state )
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
	 * 첫 플레이어의 시야 또한 파악할 수 있도록 강의실 화면을 갱신합니다.
	 */
	void RevealScreen()
	{
		DrawableObject cell;
		
		int[][] occurances = new int[Constants.Classroom_Height][Constants.Classroom_Width];
		int occurance;

		boolean isWithinSight = false;
		
		Point_Immutable pos = classroom.playerStats[0].position;

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
				
				switch ( classroom.playerStats[0].state )
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

		switch ( classroom.playerStats[0].state )
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
		tb_myInfo.text = String.format(
				"%s\n" +
				"%s at %s, HP: %d\n" +
				"SMax STot CMax CTot IMax ITot\n" +
				"%4d %4d %4d %4d %4d %4d",
				classroom.players[0].name,
				classroom.playerStats[0].state,
				classroom.playerInfos[0].position,
				classroom.playerStats[0].HP,
				classroom.scores[0][0],
				classroom.scores[0][1],
				classroom.scores[0][2],
				classroom.scores[0][3],
				classroom.scores[0][4],
				classroom.scores[0][5]);
	}
	
	/**
	 * 마우스 포인터가 가리키는 칸에 대한 정보를 갱신합니다.
	 */
	void UpdateCellInfo()
	{
		if ( state == PresenterStateCode.Waiting_Enter )
		{
			int iRow = 0;
			int iColumn = 0;
			
			boolean found = false;
			
			for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			{
				for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
				{
					if ( cells[iRow][iColumn] == cell_focused )
					{
						found = true;
						break;
					}
				}
				
				if ( found == true )
					break;
			}
			
			if ( found == true )
			{
				CellInfo info = classroom.players[0].cells[iRow][iColumn];
				row_focused = iRow;
				column_focused = iColumn;
				
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
						iRow,
						iColumn,
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
