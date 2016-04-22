package l4g;

import java.awt.event.KeyEvent;

import l4g.common.Classroom_Settings;
import l4g.common.Constants;
import l4g.common.GameNumberManager;
import l4g.common.StateCode;
import l4g.data.Action;
import l4g.data.PlayerInfo;
import loot.GameFrame;
import loot.GameFrameSettings;
import loot.graphics.DrawableObject;
import loot.graphics.Layer;
import loot.graphics.Point3D;
import loot.graphics.TextBox;
import loot.graphics.Viewport;

/**
 * 보다 직관적인 테스트를 위해 강의실 상황을 시각적으로 보여줍니다.
 * 
 * @author Racin
 *
 */
public class Presenter_Mode4_Old extends GameFrame
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

	
	boolean isOfficialGameNotStarted;
	
	int max_CIs = 0;
	int max_row = 0;
	int max_column = 0;
	double max_pos_x = 0;
	double max_pos_y = 0;
	
	
	
	/**
	 * 전체 화면(2D정보빡스들 + 3D강의실)을 구성합니다.
	 */
	Viewport screen;
	
	/**
	 * 3D 공간 내의 2D강의실을 구성합니다.
	 */
	Layer board;

	RankMeter[] ranks_top;
	RankMeter[] ranks_best;
	RankMeter[] ranks_crit;
	
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
	
	
	double vel_x;
	double vel_y;
	
	final double acc = 3;
	
	public Presenter_Mode4_Old(GameFrameSettings gfs, Classroom_Settings cs)
	{
		super(gfs);
		this.cs = new Classroom_Settings(cs);

		isOfficialGameNotStarted = true;
		
		/*
		 * 매 턴이 시작될 때 화면을 갱신하고 게임을 일시 정지 
		 */
		this.cs.callback_StartTurn = () ->
		{
			if ( isSkipping == true )
				return true;
			
			UpdateScreen();
			state = PresenterStateCode.Waiting_Enter;
			return false;
		};
		
		/*
		 * 게임이 끝나면 전체 강의실 내 플레이어 분포를 보여줌
		 */
		this.cs.callback_EndGame = () ->
		{
			state = PresenterStateCode.Completed;
			UpdateScreen();
			Update1GameStats();
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
		inputs.BindKey(KeyEvent.VK_ESCAPE, 20);
		inputs.BindKey(KeyEvent.VK_F1, 21);

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
		screen.pointOfView_z = 1;
		screen.view_baseDistance = 1;
		screen.view_maxDistance = Double.POSITIVE_INFINITY;
		
		RankMeter newMeter;

		ranks_top = new RankMeter[Classroom.Score_Titles + 1];

		newMeter = new RankMeter(-400, 270, -8, "Top Survivor(Max):");
		ranks_top[0] = newMeter;
		screen.children.add(newMeter);
		
		newMeter = new RankMeter(-200, 270, -8, "Top Survivor(Tot):");
		ranks_top[1] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-400, 120, -8, "Top Corpse(Max):");
		ranks_top[2] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-200, 120, -8, "Top Corpse(Tot):");
		ranks_top[3] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-400, -30, -8, "Top Infected(Max):");
		ranks_top[4] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-200, -30, -8, "Top Infected(Tot):");
		ranks_top[5] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-400, -180, -8, "Top Soul(Freedom):");
		ranks_top[6] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-200, -180, -8, "Top Soul(Destruction):");
		ranks_top[7] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(-200, -330, -8, "Top Player of L4G2EP2:");
		ranks_top[8] = newMeter;
		screen.children.add(newMeter);

		
		
		ranks_best = new RankMeter[Classroom.Score_Titles + 1];

		newMeter = new RankMeter(0, 270, -8, "Best Survivor(Max):");
		ranks_best[0] = newMeter;
		screen.children.add(newMeter);
		
		newMeter = new RankMeter(200, 270, -8, "Best Survivor(Tot):");
		ranks_best[1] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(0, 120, -8, "Best Corpse(Max):");
		ranks_best[2] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(200, 120, -8, "Best Corpse(Tot):");
		ranks_best[3] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(0, -30, -8, "Best Infected(Max):");
		ranks_best[4] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(200, -30, -8, "Best Infected(Tot):");
		ranks_best[5] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(0, -180, -8, "Best Soul(Freedom):");
		ranks_best[6] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(200, -180, -8, "Best Soul(Destruction):");
		ranks_best[7] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(200, -330, -8, "Best Player of L4G2EP2:");
		ranks_best[8] = newMeter;
		screen.children.add(newMeter);
		
		
		ranks_crit = new RankMeter[Classroom.Score_Titles + 1];

		newMeter = new RankMeter(400, 270, -8, "Critical Survivor(Max):");
		ranks_crit[0] = newMeter;
		screen.children.add(newMeter);
		
		newMeter = new RankMeter(600, 270, -8, "Critical Survivor(Tot):");
		ranks_crit[1] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(400, 120, -8, "Critical Corpse(Max):");
		ranks_crit[2] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(600, 120, -8, "Critical Corpse(Tot):");
		ranks_crit[3] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(400, -30, -8, "Critical Infected(Max):");
		ranks_crit[4] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(600, -30, -8, "Critical Infected(Tot):");
		ranks_crit[5] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(400, -180, -8, "Critical Soul(Freedom):");
		ranks_crit[6] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(600, -180, -8, "Critical Soul(Destruction):");
		ranks_crit[7] = newMeter;
		screen.children.add(newMeter);

		newMeter = new RankMeter(600, -330, -8, "Critical Player of L4G2EP2:");
		ranks_crit[8] = newMeter;
		screen.children.add(newMeter);
		
		
		
		
		
		tb_cellInfo = new TextBox(8, 8, 210, 40);
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
		
		//방향키를 누르면 카메라 속도 변화
		if ( inputs.buttons[0].isPressed == true)
			vel_y += acc;
		if ( inputs.buttons[1].isPressed == true)
			vel_x -= acc;
		if ( inputs.buttons[2].isPressed == true)
			vel_x += acc;
		if ( inputs.buttons[3].isPressed == true)
			vel_y -= acc;
		
		vel_x *= 0.8;
		vel_y *= 0.8;
		
		if ( vel_x < 0.0001 && vel_x > -0.0001 )
			vel_x = 0;
		if ( vel_y < 0.0001 && vel_y > -0.0001 )
			vel_y = 0;

		if ( screen.pointOfView_z > 0.01 )
		{
			if ( max_CIs == 0 )
			{
				max_pos_x = 0;
				max_pos_y = 0;
			}
			else
			{
				Point3D offset = board.TransformTo3DPosition(board.TransformToExternalPosition(cells[max_row][max_column].x + cells[max_row][max_column].width / 2, cells[max_row][max_column].y + cells[max_row][max_column].height / 2));
				
				if ( offset.x != Double.POSITIVE_INFINITY )
				{
					max_pos_x = offset.x;
					max_pos_y = offset.y;
				}
				else
				{
					max_pos_x = 0;
					max_pos_y = 0;
				}
			}
			
			vel_x += ( max_pos_x - screen.pointOfView_x ) / 70;
			vel_y += ( max_pos_y - screen.pointOfView_y ) / 70;
		}

		screen.pointOfView_x += vel_x;
		screen.pointOfView_y += vel_y;
		
		//대괄호 키를 누르는 동안 카메라 높이 조절
		if ( inputs.buttons[4].isPressed == true && screen.pointOfView_z <= 1.5)
			screen.pointOfView_z += 0.02;
		if ( inputs.buttons[5].isPressed == true )
			screen.pointOfView_z -= 0.02;
		
		tb_cellInfo.trigger_hide = screen.pointOfView_z < -0.01;
		
		//엔터 키를 누르면
		if ( inputs.buttons[6].IsPressedNow() == true )
		{
			//게임을 아직 시작하지 않은 경우 시작
			if ( state == PresenterStateCode.Not_Started )
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
		
		// BS 키를 누르면 게임을 끝까지 걍 진행
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
		if ( ( inputs.buttons[14].IsPressedNow() == true || inputs.buttons[8].IsPressedNow() == true )&& state != PresenterStateCode.Not_Started&& cell_focused != null)
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
		if ( ( inputs.buttons[15].IsPressedNow() == true || inputs.buttons[9].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started&& cell_focused != null)
		{
			System.out.printf("Corpses at (%d, %d) / Turn#%d\n", row_focused, column_focused, classroom.turnNumber);
			
			classroom.players[0].cells[row_focused][column_focused].ForEach_Players(player ->
			{
				if ( player.state == StateCode.Corpse )
					System.out.println(classroom.players[player.ID].name + " - HP: " + player.HP);
			});

			System.out.println();
		}
		
		//3. Infecteds
		if ( ( inputs.buttons[16].IsPressedNow() == true || inputs.buttons[10].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started&& cell_focused != null)
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
		if ( ( inputs.buttons[17].IsPressedNow() == true || inputs.buttons[11].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started&& cell_focused != null)
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
		if ( ( inputs.buttons[18].IsPressedNow() == true || inputs.buttons[12].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started&& cell_focused != null)
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
		if ( ( inputs.buttons[19].IsPressedNow() == true || inputs.buttons[13].IsPressedNow() == true ) && state != PresenterStateCode.Not_Started&& cell_focused != null)
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
		
		//ESC. 시점 x/y좌표 초기화
		if ( inputs.buttons[20].IsPressedNow() == true )
		{
			screen.pointOfView_x = 0;
			screen.pointOfView_y = 0;
			vel_x = 0;
			vel_y = 0;
		}
		
		//F1. 정규 게임 시작
		if ( inputs.buttons[21].IsPressedNow() == true && isOfficialGameNotStarted == true )
		{
			isOfficialGameNotStarted = false;
			
			
			Thread thr_officialGame = new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					GameNumberManager numbers = new GameNumberManager(10000);
					numbers.Load("10000numbers.txt");
					
					Grader grader = new Grader();
					Classroom_Settings settings = new Classroom_Settings(cs);
					
					settings.callback_StartTurn = null;
					settings.callback_EndGame = null;

					Classroom classroom = null;
					
					for ( int iGame = 0; iGame < 10000; ++iGame )
					{
						if ( iGame % 1000 == 0 )
							System.out.println(iGame + " / 10000 games completed...");
						
						settings.game_number = numbers.data[iGame];
						classroom = new Classroom(settings);
						classroom.Initialize();
						classroom.Start();
						grader.Update(classroom);
						UpdateOfficialGameStats(classroom, grader);
					}
					
					System.out.println("Done.");
				}
			});
			
			thr_officialGame.start();
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
	
	void Update1GameStats()
	{
		for ( int iTitle = 0; iTitle < Classroom.Score_Titles; iTitle++ )
		{
			for ( int iRank = 0; iRank < 5; iRank++ )
			{
				ranks_top[iTitle].names[iRank] = classroom.players[classroom.rankedPlayers[iTitle][iRank+1]].name;
				ranks_top[iTitle].scores[iRank] = classroom.scores[classroom.rankedPlayers[iTitle][iRank+1]][iTitle];
			}
		}
		
		for ( int iRank = 0; iRank < 5; iRank++ )
		{
			ranks_top[Classroom.Score_Titles].names[iRank] = classroom.players[classroom.final_rankedPlayers[iRank+1]].name;
			ranks_top[Classroom.Score_Titles].scores[iRank] = classroom.final_grades[classroom.final_rankedPlayers[iRank+1]];
		}
	}
	
	void UpdateOfficialGameStats(Classroom classroom, Grader grader)
	{
		for ( int iTitle = 0; iTitle <= Classroom.Score_Titles; iTitle++ )
		{
			for ( int iRank = 0; iRank < 5; iRank++ )
			{
				ranks_best[iTitle].names[iRank] = classroom.players[grader.IDs_winners_best[iTitle][iRank]].name;
				ranks_best[iTitle].scores[iRank] = grader.data_best[grader.IDs_winners_best[iTitle][iRank]][iTitle];
				ranks_crit[iTitle].names[iRank] = classroom.players[grader.IDs_winners_crit[iTitle][iRank]].name;
				ranks_crit[iTitle].scores[iRank] = grader.data_crit[grader.IDs_winners_crit[iTitle][iRank]][iTitle];
			}
		}
	}
	
	/**
	 * 강의실 화면을 갱신합니다.
	 */
	void UpdateScreen()
	{
		DrawableObject cell;
		
		int current_CIs;
		boolean isSurvivorHere = false;
		boolean isCorpseHere = false;
		boolean isInfectedHere = false;
		
		max_CIs = 0;

		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
			{
				current_CIs = 0;
				isSurvivorHere = false;
				isCorpseHere = false;
				isInfectedHere = false;
				
				for ( PlayerInfo info : classroom.cells[iRow][iColumn].players )
				{
					switch ( classroom.playerStats[info.ID].state )
					{
					case Survivor:
						isSurvivorHere = true;
						break;
					case Corpse:
						isCorpseHere = true;
						++current_CIs;
						break;
					case Infected:
						isInfectedHere = true;
						++current_CIs;
					default:
						break;
					}
				}
				
				if ( max_CIs < current_CIs)
				{
					max_CIs = current_CIs;
					max_row = iRow;
					max_column = iColumn;
				}
				
				String imageName = "cell";
				
				if ( isSurvivorHere == false && isCorpseHere == false && isInfectedHere == false )
				{
					imageName += "_empty";
				}
				else
				{
					if ( isSurvivorHere == true )
						imageName += "_survivor";
					if ( isInfectedHere == true )
						imageName += "_infected";
					if ( isCorpseHere == true )
						imageName += "_corpse";
				}
				
				cell = cells[iRow][iColumn];
				cell.image = images.GetImage(imageName);
				
				if ( cell.image == null )
				{
					System.out.println(imageName);
				}
			}
		
		if ( max_CIs != 0 )
		{
			obj_myPosition.x = cells[max_row][max_column].x;
			obj_myPosition.y = cells[max_row][max_column].y;
			obj_myPosition.image = images.GetImage("myPosition_infected");
			obj_myPosition.trigger_hide = false;
		}
		else
		{
			obj_myPosition.trigger_hide = true;
		}
	}
	
	/**
	 * 마우스 포인터가 가리키는 칸에 대한 정보를 갱신합니다.
	 */
	void UpdateCellInfo()
	{
		if ( state == PresenterStateCode.Waiting_Enter || state == PresenterStateCode.Completed )
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

			tb_cellInfo.height = 40;
			tb_cellInfo.text = String.format(
					"Game#%d Turn#%d\n" +
					"---- ---- ---- ---- ---- ----\n",
					classroom.gameNumber,
					classroom.turnNumber);

			if ( found == true )
			{
				Cell cell = classroom.cells[iRow][iColumn];
				row_focused = iRow;
				column_focused = iColumn;
				
				for ( PlayerInfo player : cell.players )
				{
					PlayerStat stat = classroom.playerStats[player.ID];
					tb_cellInfo.height += 14;
					tb_cellInfo.text += String.format("%.10s: %s\n", classroom.players[player.ID].name, stat.state);
				}
			}
		}
	}
}
