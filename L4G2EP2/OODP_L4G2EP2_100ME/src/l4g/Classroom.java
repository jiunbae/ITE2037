package l4g;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.jar.Attributes.Name;

import l4g.Decision.TypeCode;
import l4g.bots.*;
import l4g.common.*;
import l4g.data.*;

import TEST.TEST;

/**
 * 각 플레이어를 생성 및 관리하며 게임을 진행하는 강의실을 나타냅니다.
 * 플레이어 인스턴스, 게임 설정, 게임 진행에 관련한 정보, 게임 결과가 들어 있습니다.<br>
 * <br>
 * <b>주의:</b> 이 클래스는 L4G에서 가장 복잡한 구조를 가지고 있으므로
 * 내용물을 그냥 아예 안 보는 것이 건강에 이롭습니다.
 * 게임에 대한 기본 정보는 나누어 준 문서 파일에 자세히 설명되어 있습니다.
 * 뭐, 비슷한 이유로, 이 클래스에 붙은 주석들은
 * 기본적으로 조교 본인 참고 용도로 적어 두었으니
 * 어렵거나 불친절해도 이해해 주세요.
 * 
 * @author Racin
 * 
 */
public class Classroom
{
	/**
	 * 강의실의 현재 상태(게임 진행 상태)를 나타내는 열거자입니다.
	 * 
	 * @author Racin
	 *
	 */
	public enum ClassroomStateCode
	{
		Not_Defined,
		Initialized,
		Running,
		Waiting_Callback_Lock,
		Waiting_Decision_Survivor_Move, // Waiting_Decision 계열은 직접 조작 가능한 플레이어가 있을 때만 사용됨
		Waiting_Decision_Corpse_Stay,
		Waiting_Decision_Infected_Move,
		Waiting_Decision_Soul_Stay,
		Waiting_Decision_Soul_Spawn,
		Completed
	}
	public TEST test;
	/* ------------------------------------------------------
	 * 주 설정 필드들
	 */

	/**
	 * 현재 진행중인 게임 번호입니다.
	 */
	public long gameNumber;

	/**
	 * 강의실의 현재 상태를 나타냅니다.
	 */
	public ClassroomStateCode state;

	/**
	 * 생성자를 통해 입력받은 설정 정보입니다.
	 */
	public Classroom_Settings settings;

	/**
	 * 현재 게임에 참여중인 플레이어 목록입니다.
	 */
	public Player[] players;


	/* ------------------------------------------------------
	 * 강의실 측 정보들 or 상수 필드들(영속적)
	 */

	/**
	 * 이번 턴 번호의 <b원본</b>입니다.
	 */
	public int turnNumber;

	/**
	 * 이번 턴에 직접 감염 수락 여부를 확인해야 하는지 여부를 나타내는 <b>원본</b> 필드입니다.
	 */
	public boolean isDirectInfectionChoosingTurn;

	/**
	 * 양수: 직접 감염이 시작된 후 발동되기까지 몇 턴 남았는지를 나타냅니다.<br>
	 * 0: 직접 감염이 이번 턴에 발동될 예정임을 나타냅니다.<br>
	 * 음수: 직접 감염이 시작되지 않았음을 나타냅니다.
	 */
	public int directInfectionCountdown;

	/**
	 * 각 플레이어에 대한 <b>원본</b> 정보 목록입니다.
	 */
	public PlayerStat[] playerStats;
	
	/**
	 * 직전 턴이 끝났을 때 생존자 상태였던 플레이어 정보만 모아 둔 목록입니다.
	 */
	public ArrayList<PlayerStat> playerStats_Survivor;
	
	/**
	 * 직전 턴이 끝났을 때 시체 상태였던 플레이어 정보만 모아 둔 목록입니다.
	 */
	public ArrayList<PlayerStat> playerStats_Corpse;
	
	/**
	 * 직전 턴이 끝났을 때 감염체 상태였던 플레이어 정보만 모아 둔 목록입니다.
	 */
	public ArrayList<PlayerStat> playerStats_Infected;
	
	/**
	 * 직전 턴이 끝났을 때 영혼 상태였던 플레이어 정보만 모아 둔 목록입니다.
	 */
	public ArrayList<PlayerStat> playerStats_Soul;

	/**
	 * 각 플레이어가 매 턴 수행하는 의사 결정 하나를 나타냅니다.
	 */
	public Decision[] decisions;

	/**
	 * 각 칸에 고정적으로 매핑되는 상수 좌표 목록입니다.
	 */
	public Point_Immutable[][] points;

	/**
	 * 각 칸에 대한 <b>원본</b> 정보 목록입니다.
	 */
	public Cell[][] cells;

	/**
	 * 각 플레이어의 부문별 목록입니다.
	 */
	public int[][] scores;

	/**
	 * 각 플레이어의 부문별 학점 목록입니다.
	 */
	public int[][] grades;

	/**
	 * 각 플레이어의 부문별 순위 목록입니다.
	 * 1등의 순위 값은 0이 아닌 1입니다.
	 */
	public int[][] ranks;

	/**
	 * 각 부문의 순위별 플레이어 ID 목록입니다.
	 * 목록의 0번째 칸은 사용하지 않으며, 1번째 칸에는 1등의 ID가 들어갑니다.
	 */
	public int[][] rankedPlayers;

	/**
	 * 플레이어별 최종학점 목록입니다.
	 */
	public int[] final_grades;

	/**
	 * 플레이어별 최종학점 순위 목록입니다.
	 * 1등의 순위 값은 0이 아닌 1입니다.
	 */
	public int[] final_ranks;

	/**
	 * 최종학점 순위별 플레이어 ID 목록입니다.
	 * 목록의 0번째 칸은 사용하지 않으며, 1번째 칸에는 1등의 ID가 들어갑니다.
	 */
	public int[] final_rankedPlayers;

	/*
	 * 아래의 정적 상수 필드들은 위에 있던 여러 데이터 배열들을 액세스하기 위해 사용됩니다.
	 * 여러분은 그리 신경쓰지 않아도 되겠군요.
	 */
	public static final int Score_Titles = 8;
	public static final int Score_Survivor_Max = 0;
	public static final int Score_Survivor_Total = 1;
	public static final int Score_Corpse_Max = 2;
	public static final int Score_Corpse_Total = 3;
	public static final int Score_Infected_Max = 4;
	public static final int Score_Infected_Total = 5;
	public static final int Score_Soul_Freedom = 6;
	public static final int Score_Soul_Destruction = 7;

	/* ------------------------------------------------------
	 * 플레이어들에게 제공할 정보 목록들(매 턴 재생성)
	 */

	/**
	 * 이번 턴에 대한 정보입니다.
	 */
	public TurnInfo turnInfo;

	/**
	 * 각 플레이어에 대한 정보 목록입니다.
	 */
	public PlayerInfo[] playerInfos;

	/**
	 * 각 칸에 대한 정보 목록입니다.
	 */
	public CellInfo[][] cellInfos;

	/**
	 * '누가 있는지만 알고 있는 칸'에 대한 정보 목록입니다.
	 * 이 정보는 <code>cellInfos</code>에 든 내용의 일부만 담은 것이며
	 * 생존자들 사이의 발견 정보 교환, 또는 시체의 자기 칸 감지 매커니즘에 의해 선택적으로 제공됩니다.
	 */
	public CellInfo[][] cellInfos_playerInfosOnly;

	/**
	 * '시체의 존재만 느껴지는 칸'에 대한 정보 목록입니다.
	 * 이 정보는 <code>cellInfos</code>에 든 내용의 일부만 담은 것이며
	 * 감염체의 모든 시체 감지 매커니즘에 의해 선택적으로 제공됩니다.
	 */
	public CellInfo[][] cellInfos_corpseInfosOnly;


	/* ------------------------------------------------------
	 * 강의실 외부와 연결하기 위한 필드들 및 정적 메서드 하나
	 */

	/**
	 * 게임 진행을 동기화하고 싶을 때 사용하는 lock입니다.
	 * lock이 왜 lock인지, lock으로 뭘 어떻게 lock한다는 것인지는 운영체제 시간에 배울 것입니다.
	 * 어쨋든, 콜백 메서드가 false를 반환할 때 강의실은 이 lock이 풀릴 때까지 기다립니다.
	 */
	public Object lock;

	/**
	 * 초기화 작업이 끝났을 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_StandBy;

	/**
	 * 게임이 시작될 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_StartGame;

	/**
	 * 턴이 시작될 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_StartTurn;

	/**
	 * 직접 조작 가능한 플레이어를 사용하는 경우 마우스 등을 통해 사용자가 선택한 좌표를 나타냅니다.
	 */
	public Point pos_acceptedFromControllablePlayer;

	/**
	 * 직접 조작 가능한 플레이어를 사용하는 경우 사용자의 차례가 되었을 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_RequestDecision;

	/**
	 * 직접 조작 가능한 플레이어를 사용하는 경우 사용자의 선택이 유효하지 않을 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_InvalidDecision;

	/**
	 * 턴이 끝났을 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_EndTurn;

	/**
	 * 게임이 끝났을 때 호출되는 콜백 메서드입니다.
	 */
	public Supplier<Boolean> callback_EndGame;

	/**
	 * '아무 것도 하지 않는' 메서드입니다.
	 * 말 그대로 '아무 것도 하고 싶지 않을 때' 사용하며,
	 * 주요 목적은 여러 가지 콜백 필드들의 기본값으로 쓰는 것입니다.
	 */
	public static Boolean DoNothing()
	{
		return true;
	}

	/* 
	 * 데이터 선언 끝
	 * ------------------------------------------------------
	 */



	/* ------------------------------------------------------
	 * '큰 메서드'들
	 */

	/**
	 * 새로운 <code>Classroom class</code>의 인스턴스를 초기화합니다.
	 * 
	 * @param settings
	 *            아마도 <code>main()</code>에서 생성한, 강의실 세부 설정값들을 모아 둔 클래스의 인스턴스입니다.
	 */
	public Classroom(Classroom_Settings settings)
	{
		state = ClassroomStateCode.Not_Defined;
		this.settings = settings;

		players = new Player[Constants.Total_Players];

		playerStats = new PlayerStat[Constants.Total_Players];
		
		playerStats_Survivor = new ArrayList<PlayerStat>();
		
		playerStats_Corpse = new ArrayList<PlayerStat>();
		
		playerStats_Infected = new ArrayList<PlayerStat>();
		
		playerStats_Soul = new ArrayList<PlayerStat>();

		// 다른 목록들과 다르게 decisions 내용물은 플레이어 등록 상황과 무관하게 재사용 가능하므로 강의실 초기화할 때만 한 번 생성
		decisions = new Decision[Constants.Total_Players];
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			decisions[iPlayer] = new Decision();

		playerInfos = new PlayerInfo[Constants.Total_Players];

		points = new Point_Immutable[Constants.Classroom_Height][Constants.Classroom_Width];
		cells = new Cell[Constants.Classroom_Height][Constants.Classroom_Width];
		cellInfos = new CellInfo[Constants.Classroom_Height][Constants.Classroom_Width];
		cellInfos_playerInfosOnly = new CellInfo[Constants.Classroom_Height][Constants.Classroom_Width];
		cellInfos_corpseInfosOnly = new CellInfo[Constants.Classroom_Height][Constants.Classroom_Width];

		for ( int iRow = 0; iRow < Constants.Classroom_Height; iRow++ )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; iColumn++ )
			{
				points[iRow][iColumn] = new Point_Immutable(iRow, iColumn);
				cells[iRow][iColumn] = new Cell();
			}

		scores = new int[Constants.Total_Players][Score_Titles];
		grades = new int[Constants.Total_Players][Score_Titles];
		ranks = new int[Constants.Total_Players][Score_Titles];
		rankedPlayers = new int[Score_Titles][Constants.Total_Players + 1];
		final_grades = new int[Constants.Total_Players];
		final_ranks = new int[Constants.Total_Players];
		final_rankedPlayers = new int[Constants.Total_Players + 1];

		lock = new Object();

		Supplier<Boolean> doNothing = Classroom::DoNothing;
		
		if ( settings.callback_StandBy == null )
			callback_StandBy = doNothing;
		else
			callback_StandBy = settings.callback_StandBy;
		
		if ( settings.callback_StartGame == null )
			callback_StartGame = doNothing;
		else
			callback_StartGame = settings.callback_StartGame;
		
		pos_acceptedFromControllablePlayer = new Point(0, 0);
		
		if ( settings.callback_StartTurn == null )
			callback_StartTurn = doNothing;
		else
			callback_StartTurn = settings.callback_StartTurn;
		
		if ( settings.callback_RequestDecision == null )
			callback_RequestDecision = doNothing;
		else
			callback_RequestDecision = settings.callback_RequestDecision;
		
		if ( settings.callback_InvalidDecision == null )
			callback_InvalidDecision = doNothing;
		else
			callback_InvalidDecision = settings.callback_InvalidDecision;
		
		if ( settings.callback_EndTurn == null )
			callback_EndTurn = doNothing;
		else
			callback_EndTurn = settings.callback_EndTurn;
		
		if ( settings.callback_EndGame == null )
			callback_EndGame = doNothing;
		else
			callback_EndGame = settings.callback_EndGame;
		
	}

	/**
	 * 게임을 시작하기 직전에 수행되는 초기화 과정을 모아 둔 메서드입니다.
	 */
	public void Initialize()
	{
		/* ----------------------------------------
		 * 게임 번호 설정
		 */
		if ( settings.game_number != -1L )
			gameNumber = settings.game_number;
		else
		{
			/*
			 * 주의:
			 * 여기서는 '임의의 게임 번호'를 만들기 위해 랜덤 요소를 사용했습니다.
			 * 하지만 우리 규칙상 여러분의 코드에서는 Random class를 사용할 수 없습니다.
			 */
			java.util.Random rand = new java.util.Random();
			gameNumber = rand.nextLong();

			if ( gameNumber < 0 )
				gameNumber += Long.MAX_VALUE;
		}


		/* ----------------------------------------
		 * 플레이어 등록
		 */
		int iPlayer = 0;

		// 직접 조작 가능한 플레이어를 사용하는 경우 가장 먼저 등록
		if ( settings.use_ctrlable_player == true )
		{
			players[iPlayer] = new ControllablePlayer(iPlayer, this);
			++iPlayer;
		}

		// 직접 작성한 플레이어를 사용하는 경우 연이어 등록
		try
		{
			for ( Class<? extends Player> customPlayer_class : settings.custom_player_classes )
			{
				players[iPlayer] = (Player) customPlayer_class.getConstructors()[0].newInstance(iPlayer);
				++iPlayer;
			}
		}
		catch ( Exception e )
		{
			System.err.println("직접 작성한 플레이어 목록이 유효하지 않습니다. \n" + "아마도 main()에 있을 강의실 설정 부분을 다시 확인해 주세요.");
			e.printStackTrace();
			System.err.flush();
			System.exit(1);
		}

		// 남은 자리는 샘플 플레이어들로 채움
		if ( iPlayer != Constants.Total_Players )
		{
			/*
			 * 주의:
			 * 여기서는 각 샘플 플레이어를 임의의 순서로 추가하기 위해 랜덤 요소를 사용했습니다.
			 * 하지만 우리 규칙상 여러분의 코드에서는 Random class를 사용할 수 없습니다.
			 */
			java.util.Random rand;

			// seed 설정
			String seed = settings.seed_for_sample_players;
			if ( seed == null || seed.isEmpty() == true || seed.length() == 0 )
				seed = "16OODP";

			rand = new Random(seed.hashCode());
			
			int current_numberOfHornDonePlayer = 0;

			for ( ; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				int rv = rand.nextInt(110);
				
				if ( rv < 20 )
				{
					players[iPlayer] = new Bot_Loner(iPlayer);
				}
				else if ( rv < 40 )
				{
					players[iPlayer] = new Bot_Scout(iPlayer);
				}
				else if ( rv < 50 )
				{
					players[iPlayer] = new Bot_CorpseBomb(iPlayer);
				}
				else if ( rv < 70 )
				{
					players[iPlayer] = new Bot_Seeker(iPlayer);
				}
				else if ( rv < 90 )
				{
					players[iPlayer] = new Bot_Predator(iPlayer);
				}
				else
				{
					if ( settings.max_numberOfHornDonePlayer <= current_numberOfHornDonePlayer )
						--iPlayer;
					else
					{
						players[iPlayer] = new Bot_HornDone(iPlayer);
						++current_numberOfHornDonePlayer;
					}
				}
			}
		}

		/* ----------------------------------------
		 * 데이터 초기화 - 플레이어 및 칸 정보를 0턴째 상황(모두 배치 준비 된 영혼이며 강의실은 텅텅 빈 상황)에 맞게 설정
		 */
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			players[iPlayer].gameNumber = gameNumber;
			PlayerStat newStat = new PlayerStat(iPlayer);
			playerStats[iPlayer] = newStat;
			decisions[iPlayer].type = TypeCode.Soul_Spawn;
			playerInfos[iPlayer] = new PlayerInfo(newStat);
			players[iPlayer].myInfo = playerInfos[iPlayer];
			playerStats_Soul.add(newStat);
		}

		for ( int iRow = 0; iRow < Constants.Classroom_Height; iRow++ )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; iColumn++ )
			{
				cellInfos[iRow][iColumn] = CellInfo.Blank;
				cellInfos_playerInfosOnly[iRow][iColumn] = CellInfo.Blank;
				cellInfos_corpseInfosOnly[iRow][iColumn] = CellInfo.Blank;
			}

		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			for ( int iTitle = 0; iTitle < Score_Titles; iTitle++ )
				scores[iPlayer][iTitle] = 0;

		state = ClassroomStateCode.Initialized;

		DoCallBack(callback_StandBy);
	}

	/**
	 * L4G 게임을 시작합니다.
	 */
	public void Start()
	{
		DoCallBack(callback_StartGame);

		state = ClassroomStateCode.Running;

		for ( turnNumber = 0; turnNumber <= Constants.Max_Turn_Numbers; turnNumber++ )
			AdvanceTurn();

		CalculateGrades();
		
		//게임 종료 후 콘솔 창에 경과를 표시하기로 한 경우 표시
		if ( settings.use_console_mode == true )
			PrintResultToConsole_EndGame();

		state = ClassroomStateCode.Completed;

		DoCallBack(callback_EndGame);
	}

	/**
	 * 게임 내의 한 턴을 진행합니다.
	 */
	private void AdvanceTurn()
	{
		int iPlayer;
		
		// 콘솔 창에 경과를 표시하며 매 턴 출력할 정보를 하나라도 지정한 경우 턴 번호 출력
		if ( settings.use_console_mode == true &&
				( settings.print_actions == true || settings.print_decisions == true ||
				settings.print_playerInfos == true ||
				settings.print_reactions == true || settings.print_scores_at_each_turns == true ) )
			System.out.printf("\nTurn %d ------------------------------------------------------------\n", turnNumber);

		// 쿨다운 갱신
		--directInfectionCountdown;
		for ( PlayerStat stat : playerStats )
			if ( stat.transitionCooldown >= 0)
				--stat.transitionCooldown;
		
		// 이번 턴에 직접 감염을 준비해야 하는지 체크
		isDirectInfectionChoosingTurn = false;

		if ( directInfectionCountdown < 0 )
		{
			// 생존자가 존재하며 시체 및 감염체가 존재하지 않는 턴에 직접 감염 시작
			if ( playerStats_Survivor.isEmpty() == false &&
					playerStats_Corpse.isEmpty() == true && playerStats_Infected.isEmpty() == true )
			{
				isDirectInfectionChoosingTurn = true;
				directInfectionCountdown = Constants.Duration_Direct_Infection;
			}
		}

		// 이번 턴에 대한 정보 제공, 각 플레이어의 현재 상태 및 위치 백업(감염체 처치 판정을 위해), 각 플레이어가 수행할 의사 결정 지정
		turnInfo = new TurnInfo(turnNumber, isDirectInfectionChoosingTurn);
		
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			players[iPlayer].turnInfo = turnInfo;
			
			playerStats[iPlayer].lastState = playerStats[iPlayer].state;
			playerStats[iPlayer].lastPosition = playerStats[iPlayer].position;

			decisions[iPlayer].location_from = playerStats[iPlayer].position;
			
			switch ( playerStats[iPlayer].state )
			{
			case Survivor:
				decisions[iPlayer].type = TypeCode.Survivor_Move;
				break;
			case Corpse:
				decisions[iPlayer].type = TypeCode.Corpse_Stay;
				break;
			case Infected:
				decisions[iPlayer].type = TypeCode.Infected_Move;
				break;
			case Soul:
				if ( playerStats[iPlayer].transitionCooldown <= 0 )
					decisions[iPlayer].type = TypeCode.Soul_Spawn;
				else
					decisions[iPlayer].type = TypeCode.Soul_Stay;
				break;
			}
		}
		
		// 각 플레이어에게 알려주는 현재 점수 갱신
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Score score = players[iPlayer].myScore;
			
			score.survivor_max = scores[iPlayer][Score_Survivor_Max];
			score.survivor_total = scores[iPlayer][Score_Survivor_Total];
			score.corpse_max = scores[iPlayer][Score_Corpse_Max];
			score.corpse_total = scores[iPlayer][Score_Corpse_Total];
			score.infected_max = scores[iPlayer][Score_Infected_Max];
			score.infected_total = scores[iPlayer][Score_Infected_Total];
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 1. 생존자와 감염체 이동, 시체와 영혼 대기
		 */

		// 모든 플레이어들을 위한 시야 정보 갱신
		RefreshInfos(false);

		// 시야 정보까지 갱신되어야 실질적인 한 턴이 시작된 것이므로 여기서 콜백 메서드 호출
		DoCallBack(callback_StartTurn);
		
		// 콘솔 창에 첫 플레이어의 게임 경과를 출력하려는 경우 이 시점(의사 결정 직전)에서 출력
		if ( settings.use_console_mode == true )
			PrintStartInfoToConsole_FirstPlayerOnly(false);

		// 의사 결정 메서드 호출
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			try
			{
				switch ( decisions[iPlayer].type )
				{
				case Survivor_Move:
					decisions[iPlayer].move_direction = players[iPlayer].Survivor_Move();
					break;
				case Corpse_Stay:
					players[iPlayer].Corpse_Stay();
					break;
				case Infected_Move:
					decisions[iPlayer].move_direction = players[iPlayer].Infected_Move();
					break;
				case Soul_Stay:
				case Soul_Spawn:
					players[iPlayer].Soul_Stay();
					break;
				default:
					break;
				}
			}
			catch ( Exception e )
			{
				// 의사 결정 도중 런타임 예외를 일으킨 경우(그게 시체/영혼 대기라 할지라도) 잡아감

				// 사건 등록
				AddReaction(iPlayer, Reaction.TypeCode.Arrested);

				// 오류 정보를 출력하도록 설정되어 있는 경우 출력
				if ( settings.print_errors == true )
				{
					System.err.printf("Error. %s makes runtime exception. - Game #%d, Turn %d\n", players[iPlayer].name, gameNumber, turnNumber);
					e.printStackTrace();
				}

				// 영혼 패널티 부여
				playerStats[iPlayer].state = StateCode.Soul;
				playerStats[iPlayer].position = Constants.Pos_Sky;
				playerStats[iPlayer].transitionCooldown = Constants.Duration_Soul_Penalty;

				// 이중 패널티 방지를 위해 '원래 영혼 상태였던 것처럼' 보정
				decisions[iPlayer].type = TypeCode.Soul_Stay;

				// 영혼 파괴 점수 갱신
				UpdateScore_Total(Score_Soul_Destruction, iPlayer, 1);
			}
		}

		// 이동 의사 결정 검증 및 반영
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Decision decision = decisions[iPlayer];
			PlayerStat stat = playerStats[iPlayer];

			// 모든 이동 의사 결정에 대해...
			if ( decision.type == TypeCode.Survivor_Move || decision.type == TypeCode.Infected_Move )
			{
				// 그 결과가 강의실 밖을 나가는지 검사
				Point_Immutable pos_destination = GetAdjacentPoint_Immutable(stat.position, decision.move_direction);
				decision.location_to = pos_destination;

				// 강의실 밖을 나가거나 생존자가 Stay를 선택한 경우 잡아감
				if ( pos_destination == Constants.Pos_Sky || decision.type == TypeCode.Survivor_Move && decision.move_direction == DirectionCode.Stay )
				{
					// 사건 등록
					AddReaction(iPlayer, Reaction.TypeCode.Punished);

					// 오류 정보를 출력하도록 설정되어 있는 경우 출력
					if ( settings.print_errors == true )
						System.err.printf("Error. %s(%s) attempts to move %s from %s. - Game #%d, Turn %d\n", players[iPlayer].name, stat.state, decision.move_direction, stat.position, gameNumber, turnNumber);

					// 영혼 패널티 부여
					stat.state = StateCode.Soul;
					stat.position = Constants.Pos_Sky;
					stat.transitionCooldown = Constants.Duration_Soul_Penalty;

					// 이중 패널티 방지를 위해 '원래 영혼 상태였던 것처럼' 보정
					decisions[iPlayer].type = TypeCode.Soul_Stay;

					// 영혼 자유 점수 갱신
					UpdateScore_Total(Score_Soul_Freedom, iPlayer, 1);
				}

				// 잡히지 않은 경우 이동 결과 반영
				else
				{
					// 행동 등록
					AddAction(iPlayer, Action.TypeCode.Move, pos_destination);

					// 이번 턴이 직접 감염 수락 여부를 결정하는 턴이라면 생존자의 수락 여부 체크
					if ( decision.type == TypeCode.Survivor_Move )
						stat.survivor_AcceptedDirectInfection = players[iPlayer].trigger_acceptDirectInfection;

					// 감염체가 Stay를 선택했는지 여부를 체크
					if ( decision.type == TypeCode.Infected_Move && decision.move_direction == DirectionCode.Stay )
						stat.infected_Stayed = true;
					else
						stat.infected_Stayed = false;

					// 플레이어 위치 변경
					stat.position = pos_destination;
				}
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 2. 영혼 배치
		 */

		// 영혼들을 위한 시야 정보 갱신
		RefreshInfos(true);
		
		// 콘솔 창에 첫 플레이어의 게임 경과를 출력하려는 경우 이 시점(의사 결정 직전)에서 출력
		if ( settings.use_console_mode == true && decisions[0].type == TypeCode.Soul_Spawn )
			PrintStartInfoToConsole_FirstPlayerOnly(true);

		// 의사 결정 메서드 호출
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			try
			{
				if ( decisions[iPlayer].type == TypeCode.Soul_Spawn )
					decisions[iPlayer].spawn_point = players[iPlayer].Soul_Spawn();
			}
			catch ( Exception e )
			{
				// 의사 결정 도중 런타임 예외를 일으킨 경우 '배치 유예'로 간주

				// 쿨다운 1턴으로 재설정
				playerStats[iPlayer].transitionCooldown = 1;

				// 이중 검사 방지를 위해 '원래 영혼 상태였던 것처럼' 보정
				decisions[iPlayer].type = TypeCode.Soul_Stay;
			}
		}

		// 배치 의사 결정 검증 및 반영
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Decision decision = decisions[iPlayer];
			PlayerStat stat = playerStats[iPlayer];

			// 모든 배치 의사 결정에 대해...
			if ( decision.type == TypeCode.Soul_Spawn )
			{
				// 강의실 밖을 선택했는지 검사
				Point_Immutable pos_destination = GetSamePoint_Immutable(decision.spawn_point);
				decision.location_to = pos_destination;

				// 강의실 밖을 선택한 경우 '배치 유예'로 간주
				if ( pos_destination == Constants.Pos_Sky )
				{
					// 쿨다운 1턴으로 재설정
					playerStats[iPlayer].transitionCooldown = 1;
				}

				// 잡히지 않은 경우 배치 결과 반영
				else
				{
					// 행동 등록
					AddAction(iPlayer, Action.TypeCode.Spawn, pos_destination);

					// 상태 전환: 영혼 -> 생존자
					stat.state = StateCode.Survivor;
					stat.position = pos_destination;
				}
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 3. 시체 일어남
		 */

		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			PlayerStat stat = playerStats[iPlayer];

			// 일어날 때가 된 모든 시체에 대해...
			if ( stat.state == StateCode.Corpse && stat.transitionCooldown == 0 )
			{
				// 상태 전환: 시체 -> 감염체
				stat.state = StateCode.Infected;

				// 시체 MAX 기록 초기화
				stat.corpse_CurrentHealedPlayers.clear();

				// 감염체로 일어난 첫 턴은 체력이 감소하지 않음
				stat.infected_Healed = true;

				// 기도 카운터 초기화
				stat.infected_CurrentPrayCounter = 0;
				
				// 사건 등록
				AddReaction(iPlayer, Reaction.TypeCode.Rise);
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 4. 직접 감염 적용
		 */

		// 직접 감염이 발생해야 한다면
		if ( directInfectionCountdown == 0 )
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				PlayerStat stat = playerStats[iPlayer];

				// 직접 감염을 선택했던 생존자들은
				if ( stat.survivor_AcceptedDirectInfection == true && stat.state == StateCode.Survivor )
				{
					// 상태 전환: 생존자 -> 감염체
					stat.state = StateCode.Infected;
					stat.HP = Constants.Corpse_Init_HP;

					// 직접 감염으로 감염체가 된 첫 턴은 체력이 감소하지 않음
					stat.infected_Healed = true;

					// 사건 등록
					AddReaction(iPlayer, Reaction.TypeCode.DirectInfect);
				}

				// 직접 감염 적용이 끝나면 선택 여부 필드는 reset
				stat.survivor_AcceptedDirectInfection = false;
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 5. 감염체가 생존자를 처치
		 */
		
		// 모든 생존자에 대해
		for ( int iSurvivor = 0; iSurvivor < Constants.Total_Players; ++iSurvivor )
		{
			PlayerStat stat_survivor = playerStats[iSurvivor];
			
			if ( stat_survivor.state == StateCode.Survivor )
			{
				// 자신이 이번 턴에 죽는지 여부를 검사
				for ( int iInfected = 0; iInfected < Constants.Total_Players; ++iInfected )
				{
					PlayerStat stat_infected = playerStats[iInfected];
					
					// 생존자와 같은 칸에 감염체가 하나라도 있다면 사망
					if ( stat_infected.state == StateCode.Infected && stat_survivor.position == stat_infected.position )
					{
						stat_survivor.survivor_Dead = true;
						break;
					}
				}
				
				// 이번 턴에 죽을 예정이면 죽임
				if ( stat_survivor.survivor_Dead == true )
				{
					stat_survivor.survivor_Dead = false;
					
					// 모든 감염체에 대해
					for ( int iInfected = 0; iInfected < Constants.Total_Players; ++iInfected )
					{
						PlayerStat stat_infected = playerStats[iInfected];
						
						if ( stat_infected.state == StateCode.Infected )
						{
							// 자신을 직접 처치했거나 자신의 이전 턴 시야 범위에 있던(처치 도움) 감염체들의 감염체 MAX 점수 증가 및 사건 기록
							if ( stat_survivor.position == stat_infected.position ||
									stat_survivor.lastState == StateCode.Survivor && stat_survivor.lastPosition.GetDistance(stat_infected.lastPosition) <= 2 )
							{
								stat_infected.infected_KilledOrAssisted = true;
								
								++stat_infected.infected_CurrentKillStreaks;
								
								AddReaction(iInfected, Reaction.TypeCode.Kill, iSurvivor);
							}
						}
					}

					// 상태 전환: 생존자 -> 시체
					stat_survivor.state = StateCode.Corpse;
					stat_survivor.HP = Constants.Corpse_Init_HP;
					stat_survivor.corpse_radius = Constants.Corpse_Init_Radius;
					stat_survivor.transitionCooldown = Constants.Corpse_Rise_Cooldown;

					// 생존자 MAX 기록 초기화
					stat_survivor.survivor_CurrentSurvivedTurns = 0;
				}
				
				// 이번 턴에 죽지 않았다면 생존자로 턴을 마무리할 것이므로 생존자 MAX 기록 증가 및 갱신
				else
				{
					UpdateScore_Max(Score_Survivor_Max, iSurvivor, ++stat_survivor.survivor_CurrentSurvivedTurns);
				}
			}
		}
		
		// 모든 감염체에 대해
		for ( int iInfected = 0; iInfected < Constants.Total_Players; ++iInfected )
		{
			PlayerStat stat_infected = playerStats[iInfected];
			
			// 이번 턴에 처치 또는 처치 도움을 기록한 경우 감염체 MAX 기록 갱신
			if ( stat_infected.infected_KilledOrAssisted == true )
			{
				stat_infected.infected_KilledOrAssisted = false;
				
				UpdateScore_Max(Score_Infected_Max, iInfected, stat_infected.infected_CurrentKillStreaks);
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 6. 시체가 감염체를 치유
		 */

		// 모든 시체에 대해
		for ( int iCorpse = 0; iCorpse < Constants.Total_Players; iCorpse++ )
		{
			PlayerStat stat_corpse = playerStats[iCorpse];

			if ( stat_corpse.state == StateCode.Corpse )
			{
				boolean isNewHealOccured = false;

				// 모든 감염체에 대해
				for ( int iInfected = 0; iInfected < Constants.Total_Players; iInfected++ )
				{
					PlayerStat stat_infected = playerStats[iInfected];

					// 해당 시체의 파편 범위에 감염체가 있다면
					if ( stat_infected.state == StateCode.Infected && stat_corpse.position.GetDistance(stat_infected.position) <= stat_corpse.corpse_radius )
					{
						// 감염체가 이번 턴에 치유를 받은 것으로 체크
						stat_infected.infected_Healed = true;

						// 감염체의 체력을 회복시키고 시체 Total 기록 갱신, Max 기록 추가
						stat_infected.HP += Constants.Corpse_Heal_Power;
						UpdateScore_Total(Score_Corpse_Total, iCorpse, Constants.Corpse_Heal_Power);

						if ( stat_corpse.corpse_CurrentHealedPlayers.contains(iInfected) == false )
						{
							isNewHealOccured = true;
							stat_corpse.corpse_CurrentHealedPlayers.add(iInfected);
						}

						// 시체의 초기 체력을 증가시키고 감염체 Total 기록 갱신
						stat_corpse.HP += Constants.Infected_Infection_Power;
						UpdateScore_Total(Score_Infected_Total, iInfected, Constants.Infected_Infection_Power);

						// 사건 등록
						AddReaction(iCorpse, Reaction.TypeCode.Heal, iInfected);
					}
				}
				
				// 치유가 끝나면 시체의 살덩이 파편 범위를 1 감소
				if ( stat_corpse.corpse_radius > 0 )
					--stat_corpse.corpse_radius;

				// 이번 턴에 새로운 감염체를 치유한 경우 시체 Max 기록 갱신
				if ( isNewHealOccured == true )
					UpdateScore_Max(Score_Corpse_Max, iCorpse, stat_corpse.corpse_CurrentHealedPlayers.size());
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 7. 감염체 소멸
		 */

		// 모든 감염체에 대해
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			PlayerStat stat = playerStats[iPlayer];

			if ( stat.state == StateCode.Infected )
			{
				// 이번 턴에 치유받았다면 체력이 감소하지 않음(이번 턴에 감염체가 된 경우도 여기에 포함)
				if ( stat.infected_Healed == true )
				{
					stat.infected_Healed = false;

					// 정화 기도에 실패할 때마다 기도 효율 초기화
					stat.infected_PrayPower = 1;
				}
				
				// 이번 턴에 치유받지 않았다면 체력 감소
				else
				{
					// 이번 턴에 가만히 서 있었다면 정화 기도로 간주
					if ( stat.infected_Stayed == true )
					{
						stat.infected_CurrentPrayCounter += stat.infected_PrayPower;

						// 소멸에 필요한 카운터를 다 모은 경우 모두 소모하고 체력을 '몹시 작은 수'로 만듦
						if ( stat.infected_CurrentPrayCounter >= Constants.Infected_RequiredPrayerCounter )
						{
							stat.infected_CurrentPrayCounter = 0;
							stat.HP = Integer.MIN_VALUE;
						}

						// 카운터가 아직 부족한 경우 평소처럼 체력 1 감소 후 기도 효율 증가
						else
						{
							--stat.HP;
							
							++stat.infected_PrayPower;
						}
					}
					// 이번 턴에 이동했다면 체력 1 감소
					else
					{
						--stat.HP;

						// 이동할 때마다 기도 효율 초기화
						stat.infected_PrayPower = 1;
					}
				}

				// 감소 결과 체력이 0 이하가 된 경우 소멸
				if ( stat.HP <= 0 )
				{
					// 상태 전환: 감염체 -> 영혼
					stat.state = StateCode.Soul;
					
					if ( stat.HP == Integer.MIN_VALUE )
						stat.transitionCooldown = Constants.Soul_Spawn_Cooldown_AfterPray;
					else
						stat.transitionCooldown = Constants.Soul_Spawn_Cooldown;

					// 체력 수치는 0으로 보정
					stat.HP = 0;

					// 감염체 MAX 기록 초기화
					stat.infected_CurrentKillStreaks = 0;

					// 사건 등록
					AddReaction(iPlayer, Reaction.TypeCode.Vanish);
					
					// 사건 등록 후 영혼을 하늘에 보냄
					stat.position = Constants.Pos_Sky;
				}
			}
		}

		/* --------------------------------------------------------------------------------------------
		 * Phase 8. 생존자 발견
		 */
		
		//턴 종료 직전이니 각 상태별 플레이어 목록 재집계
		playerStats_Survivor.clear();
		playerStats_Corpse.clear();
		playerStats_Infected.clear();
		playerStats_Soul.clear();
		
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			PlayerStat stat = playerStats[iPlayer];
			
			switch ( stat.state )
			{
			case Survivor:
				playerStats_Survivor.add(stat);
				break;
			case Corpse:
				playerStats_Corpse.add(stat);
				break;
			case Infected:
				playerStats_Infected.add(stat);
				break;
			case Soul:
				playerStats_Soul.add(stat);
				break;
			}
		}
		
		//모든 생존자에 대해
		for ( PlayerStat stat_survivor : playerStats_Survivor )
		{
			int numberOfSurvivors_withinSight = 0;
			int numberOfOthers_withinSight = 0;
			
			//자신의 시야 내의 플레이어 발견
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				PlayerStat stat_other = playerStats[iPlayer];
				
				if ( stat_survivor.position.GetDistance(stat_other.position) <= 2 )
				{
					if ( stat_other.state == StateCode.Survivor )
						++numberOfSurvivors_withinSight;
					else
						++numberOfOthers_withinSight;
					
					//사건 등록은 생략
					//AddReaction(stat_survivor.ID, Reaction.TypeCode.Spot, iPlayer);
				}
			}
			
			//자신을 제외한 생존자 수 * 시체 및 감염체 수만큼 Total 기록 갱신
			--numberOfSurvivors_withinSight;
			UpdateScore_Total(Score_Survivor_Total, stat_survivor.ID, numberOfSurvivors_withinSight * numberOfOthers_withinSight);
		}
		
		//콘솔 창에 경과를 표시하기로 한 경우 표시
		if ( settings.use_console_mode == true )
			PrintResultToConsole_EndTurn();

		DoCallBack(callback_EndTurn);
	}


	/* ------------------------------------------------------
	 * '작은 메서드'들
	 */

	/**
	 * 주어진 정보를 토대로 새로운 사건을 생성하여 해당 사건이 발생한 칸에 추가합니다.
	 * 이 메서드는 행위의 주체와 대상이 같을 때 사용하는 버전입니다.
	 */
	private void AddReaction(int ID, Reaction.TypeCode type)
	{
		Point_Immutable location = playerStats[ID].position;

		//강의실 위에서 일어난 일은 기록하지 않음
		if ( location == Constants.Pos_Sky )
			return;
		
		int rowToAdd = location.row;
		int columnToAdd = location.column;
		Reaction newReaction = new Reaction(ID, type, location);

		cells[rowToAdd][columnToAdd].reactions.add(newReaction);
	}

	/**
	 * 주어진 정보를 토대로 새로운 사건을 생성하여 해당 사건이 발생한 칸에 추가합니다.
	 * 이 메서드는 행위의 주체와 대상이 다를 때 사용하는 버전입니다.
	 */
	private void AddReaction(int subjectID, Reaction.TypeCode type, int objectID)
	{
		Point_Immutable location_subject = playerStats[subjectID].position;
		Point_Immutable location_object = playerStats[objectID].position;

		//강의실 위에서 일어난 일은 기록하지 않음
		if ( location_subject == Constants.Pos_Sky )
			return;
		
		int rowToAdd = location_subject.row;
		int columnToAdd = location_subject.column;
		Reaction newReaction = new Reaction(subjectID, type, objectID, location_subject, location_object);

		cells[rowToAdd][columnToAdd].reactions.add(newReaction);
	}

	/**
	 * 주어진 정보를 토대로 새로운 행동을 생성하여 해당 행동이 수행된 칸에 추가합니다.
	 */
	private void AddAction(int actorID, Action.TypeCode type, Point_Immutable location_to)
	{
		Point_Immutable location_from = playerStats[actorID].position;

		//강의실 위에서 일어난 일은 기록하지 않음
		if ( location_to == Constants.Pos_Sky )
			return;

		int rowToAdd = location_to.row;
		int columnToAdd = location_to.column;
		Action newAction = new Action(actorID, type, location_from, location_to);

		cells[rowToAdd][columnToAdd].actions.add(newAction);
	}

	/**
	 * 각 플레이어에게 제공되는 각종 정보를 갱신합니다.
	 * 
	 * @param isForSpawn
	 *            배치를 위해 영혼들에게 제공되는 정보만 갱신하려는 경우 true입니다.
	 *            이 값이 false인 경우 갱신 후 강의실 각 칸별 행동 / 사건 목록이 초기화됩니다.
	 */
	private void RefreshInfos(boolean isForSpawn)
	{
		int iPlayer;
		int numberOfSurvivorsAndCorpses = 0;
		int numberOfInfecteds = 0;
		int numberOfSoulsToSpawn = 0;
		ArrayList<PlayerStat> survivors = new ArrayList<PlayerStat>();
		int iRow;
		int iColumn;

		// 각 칸별 플레이어 목록 초기화
		for ( Cell[] cell_row : cells )
			for ( Cell cell : cell_row )
			{
				cell.players.clear();
				cell.corpses.clear();
			}
		
		// 강의실 내의 플레이어 분포 집계 및 플레이어 정보 갱신
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			//플레이어 정보를 해당 플레이어의 myInfo에 전달, 각 칸에 기록
			PlayerInfo newInfo = new PlayerInfo(playerStats[iPlayer]);
			Cell cell_target = null;

			playerInfos[iPlayer] = newInfo;
			players[iPlayer].myInfo = newInfo;
			
			if ( newInfo.state != StateCode.Soul )
			{
				cell_target = cells[newInfo.position.row][newInfo.position.column];
				cell_target.players.add(newInfo);
			}
			
			switch ( newInfo.state )
			{
			case Survivor:
				++numberOfSurvivorsAndCorpses;
				survivors.add(playerStats[iPlayer]);
				break;
			case Corpse:
				++numberOfSurvivorsAndCorpses;
				cell_target.corpses.add(playerInfos[iPlayer]);
				break;
			case Infected:
				++numberOfInfecteds;
				break;
			case Soul:
				if ( playerStats[iPlayer].transitionCooldown == 0 )
					++numberOfSoulsToSpawn;
				break;
			}
		}
		
		/*
		 * 칸 정보 생성
		 */
		// 배치 정보를 생성하려는 경우
		if ( isForSpawn == true )
		{
			// 배치할 영혼이 없는 경우 skip
			if ( numberOfSoulsToSpawn == 0 )
				return;
			
			//그렇지 않은 경우 생존자, 시체, 감염체가 없는 것으로 간주하도록 값 보정
			numberOfSurvivorsAndCorpses = 0;
			numberOfInfecteds = 0;
		}
		
		//직전 턴에 cells에 담긴 정보를 토대로 이번 턴에 전달할 칸 정보 생성
		for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow)
		{
			for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn)
			{
				Cell cell_origin = cells[iRow][iColumn];
				
				cellInfos[iRow][iColumn] = cell_origin.MakeCellInfo(0);
				
				if ( numberOfSurvivorsAndCorpses != 0 )
					cellInfos_playerInfosOnly[iRow][iColumn] = cell_origin.MakeCellInfo(1);
				
				if ( numberOfInfecteds != 0 )
					cellInfos_corpseInfosOnly[iRow][iColumn] = cell_origin.MakeCellInfo(2);
			}
		}
		
		
		/*
		 * 칸 정보 초기화
		 */
		// 전체 정보를 생성한 다음에는 이번 턴 정보를 받기 위해 직전 턴 행동 / 사건 정보 모두 제거
		if ( isForSpawn == false )
		{
			for ( Cell[] cell_row : cells )
				for ( Cell cell : cell_row )
				{
					cell.ResetPreviousLists();
				}
		}
		
		
		/*
		 * 각 플레이어별 시야 정보 갱신
		 */
		// 배치 정보를 갱신하려는 경우
		if ( isForSpawn == true )
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				//모든 이번 턴에 배치할 영혼들에 대해
				if ( playerStats[iPlayer].state == StateCode.Soul && playerStats[iPlayer].transitionCooldown == 0 )
				{
					CellInfo[][] cellsOfThePlayer = players[iPlayer].cells;
					
					//영혼의 시야는 강의실 전체이므로 그냥 모두 집어 넣으면 끝
					for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					{
						for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
						}
					}
				}
			}			
		}
		// 전체 정보를 갱신하려는 경우
		else
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				PlayerStat stat = playerStats[iPlayer];
				CellInfo[][] cellsOfThePlayer = players[iPlayer].cells;
				
				switch ( stat.state )
				{
				case Survivor:
					//생존자의 시야는 주변 두 칸이며 시야 내 생존자의 시야에 있는 플레이어 목록을 확인 가능
					
					//먼저 모두 빈 칸으로 초기화
					for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
						for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
							cellsOfThePlayer[iRow][iColumn] = CellInfo.Blank;
					
					//시야 내의 다른 생존자로부터 전달받은 정보 기록
					for ( PlayerStat stat_other : survivors )
					{
						int distanceBetween = stat_other.position.GetDistance(stat.position);
						
						//해당 생존자가 내 시야 범위 내에 있다면
						if ( distanceBetween == 1 || distanceBetween == 2 )
						{
							iRow = stat_other.position.row - 2;
							iColumn = stat_other.position.column;
							if ( iRow >= 0 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iRow;
							--iColumn;
							if ( iRow >= 0 && iColumn >= 0)
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iColumn;
							if ( iRow >= 0 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];

							++iColumn;
							if ( iRow >= 0 && iColumn < Constants.Classroom_Width)
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iRow;
							iColumn -= 3;
							if ( iColumn >= 0 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iColumn;
							if ( iColumn >= 0 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iColumn;
							cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];

							++iColumn;
							if ( iColumn < Constants.Classroom_Width )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];

							++iColumn;
							if ( iColumn < Constants.Classroom_Width )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iRow;
							iColumn -= 3;
							if ( iRow < Constants.Classroom_Height && iColumn >= 0 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iColumn;
							if ( iRow < Constants.Classroom_Height )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iColumn;
							if ( iRow < Constants.Classroom_Height && iColumn < Constants.Classroom_Width )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							
							++iRow;
							--iColumn;
							if ( iRow < Constants.Classroom_Height )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
						}
					}
					
					//내 시야 범위 내 정보 기록
					iRow = stat.position.row - 2;
					iColumn = stat.position.column;
					if ( iRow >= 0 )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iRow;
					--iColumn;
					if ( iRow >= 0 && iColumn >= 0)
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iColumn;
					if ( iRow >= 0 )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];

					++iColumn;
					if ( iRow >= 0 && iColumn < Constants.Classroom_Width)
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iRow;
					iColumn -= 3;
					if ( iColumn >= 0 )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iColumn;
					if ( iColumn >= 0 )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iColumn;
					cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];

					++iColumn;
					if ( iColumn < Constants.Classroom_Width )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];

					++iColumn;
					if ( iColumn < Constants.Classroom_Width )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iRow;
					iColumn -= 3;
					if ( iRow < Constants.Classroom_Height && iColumn >= 0 )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iColumn;
					if ( iRow < Constants.Classroom_Height )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iColumn;
					if ( iRow < Constants.Classroom_Height && iColumn < Constants.Classroom_Width )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					
					++iRow;
					--iColumn;
					if ( iRow < Constants.Classroom_Height )
						cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
					break;
				case Corpse:
					//시체는 시야가 없으며 자신과 같은 칸에 있는 플레이어 목록만 확인 가능
					for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					{
						for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							if ( stat.position.equals(iRow, iColumn) )
								cellsOfThePlayer[iRow][iColumn] = cellInfos_playerInfosOnly[iRow][iColumn];
							else
								cellsOfThePlayer[iRow][iColumn] = CellInfo.Blank;
						}
					}
					break;
				case Infected:
					//감염체의 시야는 자신 중심 5x5칸이며 강의실 내 모든 시체 위치 확인 가능
					for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					{
						for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							int row_distance = iRow - stat.position.row;
							int column_distance = iColumn - stat.position.column;
							
							if ( row_distance >= -2 && row_distance <= 2 &&
									column_distance >= -2 && column_distance <= 2 )
								cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
							else
								cellsOfThePlayer[iRow][iColumn] = cellInfos_corpseInfosOnly[iRow][iColumn];
						}
					}
					break;
				case Soul:
					//영혼의 시야는 강의실 전체이므로 그냥 모두 집어 넣으면 끝
					for ( iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					{
						for ( iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							cellsOfThePlayer[iRow][iColumn] = cellInfos[iRow][iColumn];
						}
					}
					break;
				}
			}
		}
	}

	/**
	 * 강의실 외부에 진행 상황을 알리기 위해 주어진 콜백 메서드를 호출합니다.
	 * 콜백 메서드가 false를 반환한 경우 외부에서 lock을 해제할 때까지 게임 진행을 일시 정지합니다.
	 * 
	 * @param callback
	 *            호출할 콜백 메서드입니다. 여기에는 주로 미리 설정된 여러 <code>callback_</code>계열 필드들 중 하나가 사용됩니다.
	 */
	private void DoCallBack(Supplier<Boolean> callback)
	{
		synchronized ( lock )
		{
			ClassroomStateCode oldState = state;

			if ( callback.get() == false )
			{
				state = ClassroomStateCode.Waiting_Callback_Lock;

				try
				{
					lock.wait();
				}
				catch ( InterruptedException e )
				{
				}

				state = oldState;
			}
		}
	}

	/**
	 * 주어진 좌표와 동일한 칸을 가리키는 상수 좌표를 반환합니다.
	 * 만약 강의실을 벗어나는 경우 붙잡혀 하늘로 가야 하므로 <code>Constants.Pos_Sky</code>를 반환합니다.
	 */
	private Point_Immutable GetSamePoint_Immutable(Point point)
	{
		//좌표가 null인 경우 무조건 잡아감
		if ( point == null )
			return Constants.Pos_Sky;
		
		int targetRow = point.row;
		int targetColumn = point.column;

		if ( targetRow < 0 || targetRow >= Constants.Classroom_Height || targetColumn < 0 || targetColumn >= Constants.Classroom_Width )
			return Constants.Pos_Sky;

		return points[targetRow][targetColumn];
	}

	/**
	 * 주어진 원점 좌표에서 지정된 방향에 위치한 가장 가까운 상수 좌표를 반환합니다.
	 * 만약 강의실을 벗어나는 경우 붙잡혀 하늘로 가야 하므로 <code>Constants.Pos_Sky</code>를 반환합니다.
	 */
	private Point_Immutable GetAdjacentPoint_Immutable(Point_Immutable origin, DirectionCode direction)
	{
		int targetRow;
		int targetColumn;

		//방향이 null인 경우 무조건 잡아감
		if ( direction == null )
			return Constants.Pos_Sky;
		
		switch ( direction )
		{
		case Up:
			targetRow = origin.row - 1;
			targetColumn = origin.column;
			break;
		case Left:
			targetRow = origin.row;
			targetColumn = origin.column - 1;
			break;
		case Right:
			targetRow = origin.row;
			targetColumn = origin.column + 1;
			break;
		case Down:
			targetRow = origin.row + 1;
			targetColumn = origin.column;
			break;
		default:
			targetRow = origin.row;
			targetColumn = origin.column;
			break;
		}

		if ( targetRow < 0 || targetRow >= Constants.Classroom_Height || targetColumn < 0 || targetColumn >= Constants.Classroom_Width )
			return Constants.Pos_Sky;

		else
			return points[targetRow][targetColumn];
	}

	/**
	 * 해당 Max 부문 점수를 갱신합니다. 주어진 값이 현재 점수보다 큰 경우 갱신이 이루어집니다.
	 */
	private void UpdateScore_Max(int title, int ID, int value)
	{
		if ( scores[ID][title] < value )
			scores[ID][title] = value;
	}

	/**
	 * 해당 Total 부문 점수를 갱신합니다. 주어진 값을 현재 점수에 더합니다.
	 */
	private void UpdateScore_Total(int title, int ID, int value)
	{
		scores[ID][title] += value;
	}

	/**
	 * 현재 기록된 점수들을 토대로 학점 및 순위를 산출합니다.
	 * 동점자가 발생한 경우 먼저 제출된 플레이어(ID가 더 빠른 플레이어)가 이깁니다.
	 */
	private void CalculateGrades()
	{
		int currentTitle;
		int currentValue;
		int otherValue;
		int iPlayer;
		int[] priorityOfTitles = new int[6];

		/*
		 * 데이터 초기화 - 일단 모두 0점으로 간주 --> 따라서 모두 1등!
		 */
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			for ( currentTitle = 0; currentTitle < Score_Titles; currentTitle++ )
			{
				grades[iPlayer][currentTitle] = 0;
				ranks[iPlayer][currentTitle] = 1;
			}

			final_grades[iPlayer] = 0;
			final_ranks[iPlayer] = 1;
		}

		/*
		 * 부문별 학점 산출
		 */
		CalculateGrades_Max(Score_Survivor_Max);
		CalculateGrades_Total(Score_Survivor_Total);
		CalculateGrades_Max(Score_Corpse_Max);
		CalculateGrades_Total(Score_Corpse_Total);
		CalculateGrades_Max(Score_Infected_Max);
		CalculateGrades_Total(Score_Infected_Total);
		CalculateGrades_Total(Score_Soul_Freedom);
		CalculateGrades_Total(Score_Soul_Destruction);

		/*
		 * 최종학점 산출
		 */
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			// 드랍할 부문 선택을 위한 우선 순위 결정
			for ( currentTitle = 0; currentTitle < 6; ++currentTitle )
			{
				priorityOfTitles[currentTitle] = 0;
				currentValue = grades[iPlayer][currentTitle];
				
				for ( int otherTitle = 0; otherTitle < currentTitle; ++otherTitle )
				{
					otherValue = grades[iPlayer][otherTitle];
					
					if ( currentValue > otherValue )
						++priorityOfTitles[otherTitle];
					else
						++priorityOfTitles[currentTitle];
				}
			}
			
			// 우선 순위가 높은 네 부문 점수를 합산하여 최종학점 산출
			for ( currentTitle = 0; currentTitle < 6; ++currentTitle )
			{
				if ( priorityOfTitles[currentTitle] < 4 )
					final_grades[iPlayer] += grades[iPlayer][currentTitle];
			}
		}
		
		/*
		 * 최종학점을 토대로 최종 순위 산출
		 */
		// 플레이어별 순위 값 계산
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			currentValue = final_grades[iPlayer];

			// 이전에 기록된 순위표에서...
			for ( int iOtherPlayer = 0; iOtherPlayer < iPlayer; iOtherPlayer++ )
			{
				otherValue = final_grades[iOtherPlayer];

				// 나보다 최종학점이 낮은 사람이 있으면 '이제 내가 위로 올라왔으니' 그 사람의 순위 1 하락
				if ( currentValue > otherValue )
					++final_ranks[iOtherPlayer];

				// 나와 최종학점이 같거나 더 높은 사람이 있으면 사람이 있으면 '현재 내 위에 그 사람이 있으니' 내 순위 1 하락
				else
					++final_ranks[iPlayer];
			}
		}

		// 플레이어별 순위 값을 토대로 순위별 플레이어 ID 기입
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			final_rankedPlayers[final_ranks[iPlayer]] = iPlayer;
	}

	/**
	 * 주어진 부문에 대한 학점을 집계합니다.
	 * Max 부문은 1등의 점수를 100으로 놓고 선형 정규화를 수행합니다.
	 * 동점자가 발생한 경우 먼저 제출된 플레이어(ID가 더 빠른 플레이어)가 이깁니다.
	 */
	private void CalculateGrades_Max(int title)
	{
		int currentValue;
		int otherValue;
		int iPlayer;
		int maxScore = 0;

		// 최대값 산출
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			// NPC가 아닐 때만 최대값 산출 과정에 반영
			if ( iPlayer > settings.numberOfNPCs ||
					iPlayer == settings.numberOfNPCs && settings.use_ctrlable_player == false )
			{
				currentValue = scores[iPlayer][title];
				if ( currentValue > maxScore )
				{
					maxScore = currentValue;
				}
			}
		}

		// 학점 및 순위 산출
		// 이 때 아무도 점수를 따지 못 한 경우 0으로 나누게 되어 강의실이 폭발하므로 그냥 모두 만점처리(순위는 낸 순서대로 매겨짐)
		if ( maxScore == 0 )
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				grades[iPlayer][title] = 100;

				ranks[iPlayer][title] = iPlayer + 1;
			}
		}
		// 그렇지 않은 경우 O(n^2) 출동
		else
		{
			// 플레이어별 순위 값 계산
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				currentValue = scores[iPlayer][title] * 100 / maxScore;
				
				if ( currentValue > 100 )
					currentValue = 100;
				
				grades[iPlayer][title] = currentValue;

				// 이전에 기록된 순위표에서...
				for ( int iOtherPlayer = 0; iOtherPlayer < iPlayer; iOtherPlayer++ )
				{
					otherValue = grades[iOtherPlayer][title];

					// 나보다 학점이 낮은 사람이 있으면 '이제 내가 위로 올라왔으니' 그 사람의 순위 1 하락
					if ( currentValue > otherValue )
						++ranks[iOtherPlayer][title];

					// 나와 학점이 같거나 더 높은 사람이 있으면 '현재 내 위에 그 사람이 있으니' 내 순위 1 하락
					else
						++ranks[iPlayer][title];
				}
			}
		}

		// 플레이어별 순위 값을 토대로 순위별 플레이어 ID 기입
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			rankedPlayers[title][ranks[iPlayer][title]] = iPlayer;
	}

	/**
	 * 주어진 부문에 대한 학점을 집계합니다.
	 * Total 부문은 1등의 점수를 100, 뒤에서 1등의 점수를 0으로 놓고 선형 정규화를 수행합니다.
	 * 동점자가 발생한 경우 먼저 제출된 플레이어(ID가 더 빠른 플레이어)가 이깁니다.
	 */
	private void CalculateGrades_Total(int title)
	{
		int currentValue;
		int otherValue;
		int iPlayer;
		int maxScore = 0;
		int minScore = Integer.MAX_VALUE;

		// 최대값, 최소값 산출
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			// NPC가 아닐 때만 최대값, 최소값 산출 과정에 반영
			if ( iPlayer > settings.numberOfNPCs ||
					iPlayer == settings.numberOfNPCs && settings.use_ctrlable_player == false )
			{
				currentValue = scores[iPlayer][title];
				if ( currentValue > maxScore )
					maxScore = currentValue;
				if ( currentValue < minScore )
					minScore = currentValue;
			}
		}

		// 학점 및 순위 산출(매 루프마다 세기 귀찮으니 Max점수에 미리 min점수를 빼 둠)
		maxScore -= minScore;

		// 이 때 점수가 다 똑같은 경우 0으로 나누게 되어 강의실이 폭발하므로 그냥 모두 만점처리(순위는 낸 순서대로 매겨짐)
		if ( maxScore == 0 )
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				grades[iPlayer][title] = 100;

				ranks[iPlayer][title] = iPlayer + 1;
			}
		}
		// 그렇지 않은 경우 O(n^2) 출동
		else
		{
			for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			{
				currentValue = ( scores[iPlayer][title] - minScore ) * 100 / maxScore;
				
				if ( currentValue < 0 )
					currentValue = 0;
				
				if ( currentValue > 100 )
					currentValue = 100;
				
				grades[iPlayer][title] = currentValue;

				// 이전에 기록된 순위표에서...
				for ( int iOtherPlayer = 0; iOtherPlayer < iPlayer; iOtherPlayer++ )
				{
					otherValue = grades[iOtherPlayer][title];

					// 나보다 학점이 낮은 사람이 있으면 '이제 내가 위로 올라왔으니' 그 사람의 순위 1 하락
					if ( currentValue > otherValue )
						++ranks[iOtherPlayer][title];

					// 나와 학점이 같거나 더 높은 사람이 있으면 '현재 내 위에 그 사람이 있으니' 내 순위 1 하락
					else
						++ranks[iPlayer][title];
				}
			}
		}

		// 플레이어별 순위 값을 토대로 순위별 플레이어 ID 기입
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
			rankedPlayers[title][ranks[iPlayer][title]] = iPlayer;
	}

	/**
	 * 각 정보를 콘솔 창에 출력합니다.
	 * 이 메서드는 각 턴이 끝나기 직전 호출됩니다. 
	 */
	private void PrintResultToConsole_EndTurn()
	{
		// 플레이어#0에 대한 요소만 출력하려는 경우
		if ( settings.print_first_player_only == true )
		{
			PlayerStat stat = playerStats[0];
			Point_Immutable pos = stat.position;

			if ( settings.print_decisions == true )
			{
				switch (decisions[0].type)
				{
				case Survivor_Move:
					System.out.println("Decision: Move to " + decisions[0].move_direction + " as Survivor");
					break;
				case Corpse_Stay:
					System.out.println("Decision: Stay as Corpse");
					break;
				case Infected_Move:
					System.out.println("Decision: Move to " + decisions[0].move_direction + " as Infected");
					break;
				case Soul_Stay:
					System.out.println("Decision: Stay as Soul");
					break;
				case Soul_Spawn:
					System.out.println("Decision: Spawn at " + decisions[0].spawn_point);
					break;
				default:
					break;
				}
			}

			if ( settings.print_actions == true )
			{
				switch (decisions[0].type)
				{
				case Survivor_Move:
				case Infected_Move:
					System.out.println("Action: Moved from " + decisions[0].location_from + " to " + decisions[0].location_to);
					break;
				case Soul_Spawn:
					System.out.println("Action: Spawned at " + decisions[0].location_to);
					break;
				default:
					break;
				}
			}
			
			if ( settings.print_playerInfos == true )
			{
				System.out.println(players[0].name + " | State: " + stat.state + ", HP: " + stat.HP + ", Position: " + pos);
			}
			
			if ( settings.print_scores_at_each_turns == true )
			{
				System.out.println(
						"Score:\n" +
								"SMax STot CMax CTot IMax ITot");

				System.out.printf("%4d %4d %4d %4d %4d %4d\n",
						scores[0][Score_Survivor_Max], scores[0][Score_Survivor_Total],
						scores[0][Score_Corpse_Max], scores[0][Score_Corpse_Total],
						scores[0][Score_Infected_Max], scores[0][Score_Infected_Total]);
			}
		}
		// 모든 플레이어에 대해 출력하려는 경우
		else
		{
			if ( settings.print_decisions == true )
			{
				System.out.println("Decisions:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				{
					switch (decisions[iPlayer].type)
					{
					case Survivor_Move:
						System.out.println(players[iPlayer].name + ": Move to " + decisions[iPlayer].move_direction + " as Survivor");
						break;
					case Corpse_Stay:
						System.out.println(players[iPlayer].name + ": Stay as Corpse");
						break;
					case Infected_Move:
						System.out.println(players[iPlayer].name + ": Move to " + decisions[iPlayer].move_direction + " as Infected");
						break;
					case Soul_Stay:
						System.out.println(players[iPlayer].name + ": Stay as Soul");
						break;
					case Soul_Spawn:
						System.out.println(players[iPlayer].name + ": Spawn at " + decisions[iPlayer].location_to);
						break;
					default:
						break;
					}
				}
			}

			if ( settings.print_actions == true )
			{
				System.out.println("Moves:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					switch (decisions[iPlayer].type)
					{
					case Survivor_Move:
					case Infected_Move:
						System.out.println(players[iPlayer].name + ": Moved from " + decisions[iPlayer].location_from + " to " + decisions[iPlayer].location_to);
						break;
					default:
						break;
					}
				System.out.println("Spawns:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					switch (decisions[0].type)
					{
					case Soul_Spawn:
						System.out.println(players[iPlayer].name + ": Spawned at " + decisions[iPlayer].location_to);
						break;
					default:
						break;
					}
			}

			if ( settings.print_reactions == true )
			{
				System.out.println("Reactions(except Spots):");
				for ( Cell[] cell_row : cells )
				{
					for ( Cell cell : cell_row )
					{
						for ( Reaction reaction : cell.reactions )
						{
							System.out.println("Type: " + reaction.type + ", Subject: " + players[reaction.subjectID].name +
								", Object: " + players[reaction.objectID].name + ", Location: " + reaction.location_subject);
						}
					}
				}
			}
			
			if ( settings.print_playerInfos == true )
			{
				System.out.println("playerInfos:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.println(players[iPlayer].name + " | State: " + playerStats[iPlayer].state + ", HP: " + playerStats[iPlayer].HP + ", Position: " + playerStats[iPlayer].position);
			}

			if ( settings.print_scores_at_each_turns == true )
			{
				System.out.println(
						"Score:\n" +
								"SMax STot CMax CTot IMax ITot - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.printf("%4d %4d %4d %4d %4d %4d - %s\n",
							scores[iPlayer][Score_Survivor_Max], scores[iPlayer][Score_Survivor_Total],
							scores[iPlayer][Score_Corpse_Max], scores[iPlayer][Score_Corpse_Total],
							scores[iPlayer][Score_Infected_Max], scores[iPlayer][Score_Infected_Total],
							players[iPlayer].name);
			}
		}
	}
	
	/**
	 * 각 정보를 콘솔 창에 출력합니다.
	 * 이 메서드는 전체 게임이 끝나기 직전 호출됩니다.
	 */
	private void PrintResultToConsole_EndGame()
	{
		System.out.println("Result of Game#" + gameNumber);
		
		if ( settings.print_first_player_only == true )
		{
			if ( settings.print_scores_at_the_end == true )
			{
				for(int i = 0; i < Constants.Total_Players; ++i)
				{
					if(settings.print_actions == true){

						System.out.println(
								players[i].name + " Score| Grade(Rank):\n" +
										"         SMax           STot           CMax           CTot           IMax           ITot |     Final");
						System.out.printf("%4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d) |  %4d(%2d)\n",
								scores[i][0], grades[i][0], ranks[i][0], scores[i][1], grades[i][1], ranks[i][1], scores[i][2], grades[i][2],
								ranks[i][2], scores[i][3], grades[i][3], ranks[i][3], scores[i][4], grades[i][4], ranks[i][4], scores[i][5],
								grades[i][5], ranks[i][5], final_grades[i], final_ranks[i]);
						//System.out.println(
						//		"Score| Grade(Rank):\n" +
						//				"         SMax           STot           CMax           CTot           IMax           ITot |     Final");
						//System.out.printf("%4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d) |  %4d(%2d)\n", scores[0][0], grades[0][0], ranks[0][0], scores[0][1], grades[0][1], ranks[0][1], scores[0][2], grades[0][2], ranks[0][2], scores[0][3], grades[0][3], ranks[0][3], scores[0][4], grades[0][4], ranks[0][4], scores[0][5], grades[0][5], ranks[0][5], final_grades[0], final_ranks[0]);
					
					} else {

						List<Integer> score = new ArrayList<Integer>();
						score.add(scores[i][0]);
						score.add(grades[i][0]);
						score.add(ranks[i][0]);
						score.add(scores[i][1]);
						score.add(grades[i][1]);
						score.add(ranks[i][1]);
						score.add(scores[i][2]);
						score.add(grades[i][2]);
						score.add(ranks[i][2]);
						score.add(scores[i][3]);
						score.add(grades[i][3]);
						score.add(ranks[i][3]);
						score.add(scores[i][4]);
						score.add(grades[i][4]);
						score.add(ranks[i][4]);
						score.add(scores[i][5]);
						score.add(grades[i][5]);
						score.add(ranks[i][5]);
						score.add(final_grades[i]);
						score.add(final_ranks[i]);
						test.push(players[i].name, score);
					}
				}
				
			}
			else
			{
				System.out.println(
						"Grade(Rank):\n" +
								"    SMax      STot      CMax      CTot      IMax      ITot |     Final");
				System.out.printf("%4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d) |  %4d(%2d)\n", grades[0][0], ranks[0][0], grades[0][1], ranks[0][1], grades[0][2], ranks[0][2], grades[0][3], ranks[0][3], grades[0][4], ranks[0][4], grades[0][5], ranks[0][5], final_grades[0], final_ranks[0]);
			}
		}
		else
		{
			if ( settings.print_scores_at_the_end == true )
			{
				System.out.println(
						"Score| Grade(Rank):\n" +
								"         SMax           STot           CMax           CTot           IMax           ITot |     Final - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.printf("%4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d)  %4d|%4d(%2d) |  %4d(%2d) - %s\n", scores[iPlayer][0], grades[iPlayer][0], ranks[iPlayer][0], scores[iPlayer][1], grades[iPlayer][1], ranks[iPlayer][1], scores[iPlayer][2], grades[iPlayer][2], ranks[iPlayer][2], scores[iPlayer][3], grades[iPlayer][3], ranks[iPlayer][3], scores[iPlayer][4], grades[iPlayer][4], ranks[iPlayer][4], scores[iPlayer][5], grades[iPlayer][5], ranks[iPlayer][5], final_grades[iPlayer], final_ranks[iPlayer], players[iPlayer].name);
			}
			else
			{
				System.out.println(
						"Grade(Rank):\n" +
								"    SMax      STot      CMax      CTot      IMax      ITot |     Final - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.printf("%4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d)  %4d(%2d) |  %4d(%2d) - %s\n", grades[iPlayer][0], ranks[iPlayer][0], grades[iPlayer][1], ranks[iPlayer][1], grades[iPlayer][2], ranks[iPlayer][2], grades[iPlayer][3], ranks[iPlayer][3], grades[iPlayer][4], ranks[iPlayer][4], grades[iPlayer][5], ranks[iPlayer][5], final_grades[iPlayer], final_ranks[iPlayer], players[iPlayer].name);
			}
		}
	}

	/**
	 * 첫 플레이어의 의사 결정에 사용된 각 정보를 콘솔 창에 출력합니다.
	 */
	private void PrintStartInfoToConsole_FirstPlayerOnly(boolean isForSpawn)
	{
		//이동 직전에 주는 정보
		if ( isForSpawn == false )
		{
			if ( settings.print_playerInfos == true || 
					settings.print_actions == true ||
					settings.print_reactions == true)
				System.out.println("------ Informations for Move/Stay decision ------");
			
			if ( settings.print_playerInfos == true )
			{
				System.out.println("Players on current sight:");

				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Players(player ->
						{
							System.out.println(players[player.ID].name + " | State: " + player.state + ", HP: " + player.HP + ", Position: " + player.position);
						});
			}

			if ( settings.print_actions == true )
			{
				System.out.println("Actions seen during last turn:");

				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Actions(action ->
						{
							if ( action.type == Action.TypeCode.Move )
								System.out.println(players[action.actorID].name + ": Moved from " + action.location_from + " to " + action.location_to);
							else
								System.out.println(players[action.actorID].name + ": Spawned at " + action.location_to);
						});
			}
			
			if ( settings.print_reactions == true )
			{
				System.out.println("Reactions seen during last turn:");

				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Reactions(reaction ->
						{
							System.out.println("Type: " + reaction.type + ", Subject: " + players[reaction.subjectID].name +
								", Object: " + players[reaction.objectID].name + ", Location: " + reaction.location_subject);
						});
			}
		}
		
		//배치 직전에 주는 정보
		else
		{
			if ( settings.print_playerInfos == true || 
					settings.print_actions == true ||
					settings.print_reactions == true)
				System.out.println("------ Informations for Spawn decision ------");
			
			if ( settings.print_playerInfos == true )
			{
				System.out.println("Players on current sight:");
	
				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Players(player ->
						{
							System.out.println(players[player.ID].name + " | State: " + player.state + ", HP: " + player.HP + ", Position: " + player.position);
						});
			}
			
			if ( settings.print_actions == true )
			{
				System.out.println("Actions seen during current turn\'s move phase:");

				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Actions(action ->
						{
							if ( action.type == Action.TypeCode.Move )
								System.out.println(players[action.actorID].name + ": Moved from " + action.location_from + " to " + action.location_to);
							else
								System.out.println(players[action.actorID].name + ": Spawned at " + action.location_to);
						});
			}
			
			if ( settings.print_reactions == true )
			{
				System.out.println("Reactions seen current turn\'s move phase:");

				for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
					for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						players[0].cells[iRow][iColumn].ForEach_Reactions(reaction ->
						{
							System.out.println("Type: " + reaction.type + ", Subject: " + players[reaction.subjectID].name +
								", Object: " + players[reaction.objectID].name + ", Location: " + reaction.location_subject);
						});
			}
		}
	}
}
