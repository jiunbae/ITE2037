package l4g2ep1;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import l4g2ep1.common.*;
import l4g2ep1.sampleplayers.*;

/**
 * 각 플레이어를 생성 및 관리하며 게임을 진행하는 강의실을 나타냅니다.
 * 플레이어 인스턴스, 게임 설정, 게임 진행에 관련한 정보, 게임 결과가 들어 있습니다.
 * 
 * 주의:
 * 이 클래스는 L4G2EP1에서 가장 복잡하게 구성되어 있으므로
 * 내용물을 아예 그냥 안 보는 것이 건강에 이롭습니다.
 * 게임에 대한 기본 정보는 나누어 준 문서 파일에 자세히 설명되어 있습니다.
 * 
 * @author Racin
 * 
 */
public class Classroom implements Runnable
{
	/**
	 * 강의실의 현재 상태(게임 진행 상태)를 나타내는 열거자입니다.
	 * 
	 * @author Racin
	 * 
	 */
	public enum State
	{
		Not_Defined,
		Initialized,
		Running,
		Waiting_Decision_Survivor_Move, // Waiting_Decision 계열은 직접 조작 가능한 플레이어가 있을 때만 사용됨
		Waiting_Decision_Corpse_Stay,
		Waiting_Decision_Infected_Move,
		Waiting_Decision_Soul_Stay,
		Waiting_Decision_Soul_Spawn,
		Completed
	}

	public State state;						// 강의실의 현재 상태
	public Classroom_Settings settings;		// 게임 설정을 위해 생성시 입력받은 설정들
	public GameInfo gameInfo;				// 이번 게임과 관련한 정보
	
	public Player[] players;				// 각 플레이어 인스턴스 목록
	public PlayerInfo[] playerInfos;		// 각 플레이어에 대한 정보 목록
	public PlayerStat[] playerStats;		// 각 플레이어에 대해 강의실 내부에서 기록해 두는 (숨은) 정보 목록

	public Decision[] decisions;			// 각 플레이어가 수행한 의사 결정 기록 목록
	public ArrayList<Action> moves;			// 이번 턴에 수행된 이동 행동 목록
	public ArrayList<Action> spawns;		// 이번 턴에 수행된 배치 행동 목록
	public ArrayList<Action> valid_actions;	// 이번 턴에 수행된 유효한 행동 목록
	public ArrayList<Reaction> reactions;	// 이번 턴에 발생한 사건 목록

	public Cells cells;						// 강의실 내의 모든 칸에 대한 정보

	public Scoreboard scoreboard;			// 플레이어의 현재 점수 및 학점 관련 정보

	public Point accepted_point;			// 직접 조작 가능한 플레이어를 사용할 때 마우스 클릭을 통한 의사 결정의 결과를 담는 필드
	public ActionListener request_decision;	// 직접 조작 가능한 플레이어를 사용할 때 의사 결정을 요청하는 이벤트
	public ActionListener invalid_decision; // 직접 조작 가능한 플레이어를 사용할 때 잘못된 의사 결정을 수행했음을 알리는 이벤트
	
	public ActionListener turn_ended;		// Presenter를 사용할 때 이번 턴이 종료되었음을 알리는 이벤트
	public ActionListener game_completed;	// Presenter를 사용할 때 게임이 모두 종료되었음을 알리는 이벤트
	public boolean isSkipping;				// Presenter를 사용할 때 이벤트 알림 없이 바로 게임을 종료해야 하는지 여부

	public Classroom(Classroom_Settings settings)
	{
		state = State.Not_Defined;
		this.settings = settings;
		gameInfo = new GameInfo();

		players = new Player[Constants.Total_Players];
		playerInfos = new PlayerInfo[Constants.Total_Players];
		playerStats = new PlayerStat[Constants.Total_Players];

		decisions = new Decision[Constants.Total_Players];
		moves = new ArrayList<Action>();
		spawns = new ArrayList<Action>();
		valid_actions = new ArrayList<Action>();
		reactions = new ArrayList<Reaction>();

		cells = new Cells();

		scoreboard = new Scoreboard();
	}

	/*
	 * 게임을 시작하기 직전에 수행되는 초기화 과정을 모아 둔 메서드
	 */
	void Initialize()
	{
		int iPlayer = 0;

		/* 게임 번호 설정 */
		if ( settings.game_number != -1 )
		{
			gameInfo.gameNumber = settings.game_number;
		}
		else
		{
			java.util.Random rand = new java.util.Random();
			gameInfo.gameNumber = rand.nextInt();
			
			if ( gameInfo.gameNumber < 0 )
				gameInfo.gameNumber += Integer.MAX_VALUE;
		}

		/* 플레이어 등록 */

		// 조종 가능한 플레이어를 사용하는 경우 가장 먼저 등록
		if ( settings.use_ctrlable_player == true )
		{
			players[iPlayer] = new ControllablePlayer(this);
			++iPlayer;
		}

		// 직접 작성한 플레이어를 사용하는 경우 연이어 등록
		try
		{
			for ( Class<? extends Player> customPlayer_class : settings.custom_player_classes )
			{
				players[iPlayer] = (Player) customPlayer_class.newInstance();
				++iPlayer;
			}
		}
		catch (Exception e)
		{
			System.err.println("직접 작성한 플레이어 목록이 잘못 되어 있습니다.");
			System.exit(1);
		}

		// 남은 자리는 임의의 샘플 플레이어들로 채움
		if ( iPlayer != Constants.Total_Players )
		{
			/*
			 * 주의:
			 * 
			 * 여기서는 각 샘플 플레이어를 임의의 분포로 추가하기 위해 랜덤 요소를 사용했습니다.
			 * 하지만 여러분의 플레이어는 Random class를 사용할 수 없습니다.
			 */
			java.util.Random rand;
			
			if ( settings.seed_for_sample_players == null ||
				 settings.seed_for_sample_players.isEmpty() == true ||
				 settings.seed_for_sample_players.length() == 0 )
				settings.seed_for_sample_players = "14OOP";
			
			rand = new Random(settings.seed_for_sample_players.hashCode());
			
			for ( ; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				switch ( rand.nextInt(6) )
				{
				case 0:
					players[iPlayer] = new SamplePlayer_Loner(iPlayer);
					break;
				case 1:
					players[iPlayer] = new SamplePlayer_Scout(iPlayer);
					break;
				case 2:
					players[iPlayer] = new SamplePlayer_Corpse_Bomb(iPlayer);
					break;
				case 3:
					players[iPlayer] = new SamplePlayer_Paramedic(iPlayer);
					break;
				case 4:
					players[iPlayer] = new SamplePlayer_Seeker(iPlayer);
					break;
				default:
					players[iPlayer] = new SamplePlayer_Predator(iPlayer);
					break;
				}
			}
		}

		// 데이터 초기화
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			playerInfos[iPlayer] = new PlayerInfo(iPlayer);
			playerStats[iPlayer] = new PlayerStat();
			decisions[iPlayer] = new Decision();
		}
		
		//첫 턴에는 모두 영혼 상태로 배치 의사 결정을 하게 되므로 그에 맞게 정보 초기화
		for ( iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			playerInfos[iPlayer].state = PlayerInfo.State.Soul;
			decisions[iPlayer].type = Decision.TypeCode.Soul_Spawn;
			
			players[iPlayer].gameInfo = gameInfo;
			players[iPlayer].myInfo = playerInfos[iPlayer];
			players[iPlayer].cells = cells;
			players[iPlayer].myScore = scoreboard.scores[iPlayer];

			//추가 정보를 수신하는 플레이어에 한해 다른 영혼 정보 추가
			if ( players[iPlayer].receiveOthersInfo_detected == true )
				for ( int iOtherPlayer = 0; iOtherPlayer < Constants.Total_Players; ++iOtherPlayer )
					players[iPlayer].othersInfo_detected.add(playerInfos[iOtherPlayer]);
		}

		state = State.Initialized;
	}

	/*
	 * 전체 게임을 진행하는 메서드
	 */
	void Start()
	{
		state = State.Running;

		//게임은 0턴 ~ 120턴(포함)까지 진행됨
		for ( gameInfo.currentTurnNumber = 0; gameInfo.currentTurnNumber <= Constants.Total_Turns; ++gameInfo.currentTurnNumber )
		{
			//콘솔 모드로 설정되어 있는 경우 매 턴이 시작될 때 턴 번호 출력
			if ( settings.use_console_mode == true )
				System.out.println("----- Turn " + gameInfo.currentTurnNumber + " -----");

			//이번 턴 진행
			AdvanceTurn();

			//턴 종료 이벤트 수신자가 설정되어 있는 경우 이벤트 발생 후 대기
			if ( turn_ended != null )
			{
				synchronized (this)
				{
					if ( isSkipping == false )
					{
						turn_ended.actionPerformed(null);
						
						try
						{
							this.wait();
						}
						catch (InterruptedException e)
						{
						}
					}
				}
			}
		}

		//게임이 끝나면 학점 산출
		scoreboard.CalculateGrades();

		//콘솔 모드로 설정되어 있는 경우 게임 결과를 콘솔에 출력
		if ( settings.use_console_mode == true )
			PrintResultToConsole_AtEndOfTheGame();

		state = State.Completed;

		//게임 종료 이벤트 수신자가 설정되어 있는 경우 이벤트 발생
		if ( game_completed != null )
			game_completed.actionPerformed(null);
	}

	/*
	 * 게임 내의 한 턴을 진행하는 메서드
	 */
	void AdvanceTurn()
	{
		/*
		 * 각 상태 트리거는 유효성 검사 직후 reset
		 * 각 점수는 변화 요인이 발생할때마다 Scoreboard에 반영
		 * 각 Max 기록은 상태가 정상적으로 전환될 때 초기화
		 */

		/*
		 * Phase 1. 생존자 / 감염체 이동
		 */

		// 행동 및 사건 목록 초기화
		moves.clear();
		spawns.clear();
		valid_actions.clear();
		reactions.clear();

		// 이동 및 대기 의사 결정 수신
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			try
			{
				switch (decisions[iPlayer].type)
				{
				case Survivor_Move:
					decisions[iPlayer].direction = players[iPlayer].Survivor_Move();
					break;
				case Corpse_Stay:
					players[iPlayer].Corpse_Stay();
					break;
				case Infected_Move:
					decisions[iPlayer].direction = players[iPlayer].Infected_Move();
					break;
				case Soul_Stay:
				case Soul_Spawn:
					players[iPlayer].Soul_Stay();
					break;
				default:
					break;
				}
			}
			catch (Exception e)
			{
				// 런타임 예외를 일으킨 경우 패널티 부여
				playerInfos[iPlayer].state = PlayerInfo.State.Soul;
				playerInfos[iPlayer].position.x = -1;
				playerInfos[iPlayer].position.y = -1;
				playerStats[iPlayer].transition_cooldown = Constants.Soul_Interval_Penalty;

				// 이중 패널티 방지를 위해 '원래 영혼 상태였던 것처럼' 보정
				decisions[iPlayer].type = Decision.TypeCode.Soul_Stay;
				
				//영혼 파괴 점수 갱신
				scoreboard.Update(Scoreboard.ScoreTypeCode.Soul_Destruction, iPlayer, 1);

				// 사건 등록
				reactions.add(new Reaction(iPlayer, Reaction.TypeCode.Arrested, iPlayer, decisions[iPlayer].location_from));

				// 예외 정보를 출력하도록 설정되어 있는 경우 출력
				if ( settings.print_errors == true )
				{
					System.err.printf("Error. %s makes runtime exception. - Game #%d, Turn %d\n", players[iPlayer].name, gameInfo.gameNumber, gameInfo.currentTurnNumber);
					e.printStackTrace();
				}
			}
		}

		// 이동 목록 생성
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Decision decision = decisions[iPlayer];

			if ( decision.type == Decision.TypeCode.Survivor_Move || decision.type == Decision.TypeCode.Infected_Move )
			{
				if ( players[iPlayer].IsValidMove(decision.direction) == true )
				{
					moves.add(new Action(iPlayer, decision, true));
				}
				else
				{
					moves.add(new Action(iPlayer, decision, false));
				}
			}
		}

		// 이동 목록에 따라 플레이어 위치 수정
		for ( Action move : moves )
		{
			if ( move.type == Action.TypeCode.Move )
			{
				//유효한 행동 목록에 추가
				valid_actions.add(move);
				
				// 감염체가 제자리에 서 있는 것을 선택했는지 체크
				if ( playerInfos[move.actorID].state == PlayerInfo.State.Infected && move.location_from.equals(move.location_to) )
				{
					playerStats[move.actorID].infected_prayingAtThisTurn = true;
				}
				// 그렇지 않다면 위치 수정
				else
				{
					playerInfos[move.actorID].position.x = move.location_to.x;
					playerInfos[move.actorID].position.y = move.location_to.y;
				}
			}
			else
			{
				// 잘못된 이동: 영혼 패널티 부여
				playerInfos[move.actorID].state = PlayerInfo.State.Soul;
				playerInfos[move.actorID].position.x = -1;
				playerInfos[move.actorID].position.y = -1;
				playerStats[move.actorID].transition_cooldown = Constants.Soul_Interval_Penalty;

				//영혼 자유 점수 갱신
				scoreboard.Update(Scoreboard.ScoreTypeCode.Soul_Freedom, move.actorID, 1);
				
				// 사건 등록
				reactions.add(new Reaction(move.actorID, Reaction.TypeCode.Punished, move.actorID, move.location_to));

				// 오류 정보를 출력하도록 설정되어 있는 경우 출력
				if ( settings.print_errors == true )
				{
					System.err.printf("Error. %s attempts to move from %s to %s. - Game #%d, Turn %d\n", players[move.actorID].name, move.location_from, move.location_to, gameInfo.gameNumber, gameInfo.currentTurnNumber);
				}
			}
		}

		// 직접 감염을 결정하는 턴인 경우 생존자들의 직접 감염 선택 여부 확인
		if ( gameInfo.isDirectInfectionChoosingTurn == true )
		{
			gameInfo.isDirectInfectionChoosingTurn = false;

			for ( int i = 0; i < Constants.Total_Players; i++ )
			{
				if ( playerInfos[i].state == PlayerInfo.State.Survivor )
				{
					playerStats[i].survivor_acceptDirectInfection = players[i].acceptDirectInfection;
				}
				else
				{
					playerStats[i].survivor_acceptDirectInfection = false;
				}
			}
		}

		/*
		 * Phase 2. 영혼 부활
		 */

		// 영혼들을 위한 시야 정보 갱신
		RefreshSightInfo(true);

		// 배치 의사 결정 수신
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; iPlayer++ )
		{
			try
			{
				switch (decisions[iPlayer].type)
				{
				case Soul_Spawn:
					decisions[iPlayer].location_to = new Point(players[iPlayer].Soul_Spawn());
					break;
				default:
					break;
				}
			}
			catch (Exception e)
			{
				// 런타임 예외를 일으킨 경우 패널티 부여
				playerInfos[iPlayer].state = PlayerInfo.State.Soul;
				playerInfos[iPlayer].position.x = -1;
				playerInfos[iPlayer].position.y = -1;
				playerStats[iPlayer].transition_cooldown = Constants.Soul_Interval_Penalty;

				// 이중 패널티 방지를 위해 '아직 배치될 때가 아니었던 것처럼' 보정
				decisions[iPlayer].type = Decision.TypeCode.Soul_Stay;
				
				//영혼 파괴 점수 갱신
				scoreboard.Update(Scoreboard.ScoreTypeCode.Soul_Destruction, iPlayer, 1);

				// 사건 등록
				reactions.add(new Reaction(iPlayer, Reaction.TypeCode.Arrested, iPlayer, decisions[iPlayer].location_from));

				//예외 정보를 출력하도록 설정되어 있는 경우 출력
				if ( settings.print_errors == true )
				{
					System.err.printf("Error. %s makes runtime exception. - Game #%d, Turn %d\n", players[iPlayer].name, gameInfo.gameNumber, gameInfo.currentTurnNumber);
					e.printStackTrace();
				}
			}
		}

		// 배치 목록 생성
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			Decision decision = decisions[iPlayer];

			if ( decision.type == Decision.TypeCode.Soul_Spawn )
			{
				if ( decision.location_to.IsValid() == true )
				{
					spawns.add(new Action(iPlayer, decision, true));
				}
				else
				{
					spawns.add(new Action(iPlayer, decision, false));
				}
			}
		}

		// 배치 목록에 따라 플레이어 위치 수정
		for ( Action spawn : spawns )
		{
			if ( spawn.type == Action.TypeCode.Spawn )
			{
				//유효한 행동 목록에 추가
				valid_actions.add(spawn);
				
				// 상태 전환: 영혼 -> 생존자
				playerInfos[spawn.actorID].state = PlayerInfo.State.Survivor;
				playerInfos[spawn.actorID].position.x = spawn.location_to.x;
				playerInfos[spawn.actorID].position.y = spawn.location_to.y;
			}
			else
			{
				// 잘못된 배치: 영혼 패널티 부여
				playerInfos[spawn.actorID].state = PlayerInfo.State.Soul;
				playerInfos[spawn.actorID].position.x = -1;
				playerInfos[spawn.actorID].position.y = -1;
				playerStats[spawn.actorID].transition_cooldown = Constants.Soul_Interval_Penalty;

				//영혼 자유 점수 갱신
				scoreboard.Update(Scoreboard.ScoreTypeCode.Soul_Freedom, spawn.actorID, 1);

				// 사건 등록
				reactions.add(new Reaction(spawn.actorID, Reaction.TypeCode.Punished, spawn.actorID, spawn.location_to));

				// 오류 정보를 출력하도록 설정되어 있는 경우 출력
				if ( settings.print_errors == true )
					System.err.printf("Error. %s attempts to spawn at %s. - Game #%d, Turn %d\n", players[spawn.actorID].name, spawn.location_to, gameInfo.gameNumber, gameInfo.currentTurnNumber);
			}
		}

		/*
		 * Phase 3. Rise
		 */

		for ( int i = 0; i < Constants.Total_Players; i++ )
		{
			// 시간이 된 시체가 있다면
			if ( playerInfos[i].state == PlayerInfo.State.Corpse && playerStats[i].transition_cooldown == 0 )
			{
				// 상태 전환: 시체 -> 감염체
				playerInfos[i].state = PlayerInfo.State.Infected;

				// 시체 Max 기록 초기화
				playerStats[i].current_healed_players.clear();

				// 감염체로 일어난 첫 턴은 체력이 감소하지 않음
				playerStats[i].infected_healedAtThisTurn = true;

				// 사건 등록
				reactions.add(new Reaction(i, Reaction.TypeCode.Rise, i, playerInfos[i].position));
			}
		}

		/*
		 * Phase 4. DirectInfect
		 */

		// 직접 감염이 발생해야 한다면
		if ( gameInfo.directInfectionCountdown == 0 )
		{
			for ( int i = 0; i < Constants.Total_Players; i++ )
			{
				// 직접 감염을 선택했던 생존자가 있다면
				if ( playerStats[i].survivor_acceptDirectInfection == true && playerInfos[i].state == PlayerInfo.State.Survivor )
				{
					// 상태 전환: 생존자 -> 감염체
					playerInfos[i].state = PlayerInfo.State.Infected;
					playerInfos[i].hitPoint = Constants.Infected_Initial_Point;

					// 직접 감염으로 감염체가 된 첫 턴은 체력이 감소하지 않음
					playerStats[i].infected_healedAtThisTurn = true;

					// 사건 등록
					reactions.add(new Reaction(i, Reaction.TypeCode.DirectInfect, i, playerInfos[i].position));
				}
			}
		}

		/*
		 * Phase 5. Kill
		 */

		// 모든 감염체에 대해
		for ( int iInfected = 0; iInfected < Constants.Total_Players; iInfected++ )
		{
			if ( playerInfos[iInfected].state == PlayerInfo.State.Infected )
			{
				boolean isKillOccured = false;

				// 모든 생존자에 대해
				for ( int iSurvivor = 0; iSurvivor < Constants.Total_Players; iSurvivor++ )
				{
					// 해당 감염체와 같은 칸에 생존자가 있다면
					if ( playerInfos[iSurvivor].state == PlayerInfo.State.Survivor &&
							playerInfos[iSurvivor].position.equals(playerInfos[iInfected].position) )
					{
						isKillOccured = true;

						// 생존자는 죽은 것으로 체크
						playerStats[iSurvivor].survivor_deadAtThisTurn = true;

						// 감염체 Max 기록 증가
						++playerStats[iInfected].current_kill_point;

						// 사건 등록
						reactions.add(new Reaction(iInfected, Reaction.TypeCode.Kill, iSurvivor, playerInfos[iInfected].position));
					}
				}

				// 감염체가 이번 턴에 생존자를 처치한 경우 감염체 Max 기록 갱신
				if ( isKillOccured == true )
				{
					scoreboard.Update(Scoreboard.ScoreTypeCode.Infected_Max, iInfected, playerStats[iInfected].current_kill_point);
				}
			}
		}

		// 모든 생존자에 대해
		for ( int iSurvivor = 0; iSurvivor < Constants.Total_Players; iSurvivor++ )
		{
			if ( playerInfos[iSurvivor].state == PlayerInfo.State.Survivor )
			{
				// 이번 턴에 죽은 것으로 체크되어 있다면
				if ( playerStats[iSurvivor].survivor_deadAtThisTurn == true )
				{
					playerStats[iSurvivor].survivor_deadAtThisTurn = false;

					// 상태 전환: 생존자 -> 시체
					playerInfos[iSurvivor].state = PlayerInfo.State.Corpse;
					playerInfos[iSurvivor].hitPoint = Constants.Infected_Initial_Point;
					playerStats[iSurvivor].transition_cooldown = Constants.Corpse_Interval_Rise;

					// 생존자 Max 기록 초기화
					playerStats[iSurvivor].current_survive_point = 0;
				}
				// 죽지 않았다면 생존자로 이번 턴을 마무리할 것이므로 생존자 Max 기록 갱신
				else
				{
					scoreboard.Update(Scoreboard.ScoreTypeCode.Survivor_Max, iSurvivor, ++playerStats[iSurvivor].current_survive_point);
				}
			}
		}

		/*
		 * Phase 6. Heal
		 */

		// 모든 시체에 대해
		for ( int iCorpse = 0; iCorpse < Constants.Total_Players; iCorpse++ )
		{
			if ( playerInfos[iCorpse].state == PlayerInfo.State.Corpse )
			{
				boolean isNewHealOccured = false;

				// 모든 감염체에 대해
				for ( int iInfected = 0; iInfected < Constants.Total_Players; iInfected++ )
				{
					// 해당 시체와 같은 칸에 감염체가 있다면
					if ( playerInfos[iInfected].state == PlayerInfo.State.Infected && playerInfos[iCorpse].position.equals(playerInfos[iInfected].position) )
					{
						// 감염체가 이번 턴에 치유를 받은 것으로 체크
						playerStats[iInfected].infected_healedAtThisTurn = true;

						// 감염체의 체력을 회복시키고 시체 Total 기록 갱신, Max 기록 추가
						playerInfos[iInfected].hitPoint += Constants.Corpse_Rate_Heal;
						scoreboard.Update(Scoreboard.ScoreTypeCode.Corpse_Total, iCorpse, Constants.Corpse_Rate_Heal);
						if ( playerStats[iCorpse].current_healed_players.contains(iInfected) == false )
						{
							isNewHealOccured = true;
							playerStats[iCorpse].current_healed_players.add(iInfected);
						}

						// 시체의 초기 체력을 증가시키고 감염체 Total 기록 갱신
						playerInfos[iCorpse].hitPoint += Constants.Infected_Rate_Infection;
						scoreboard.Update(Scoreboard.ScoreTypeCode.Infected_Total, iInfected, Constants.Infected_Rate_Infection);

						// 사건 등록
						reactions.add(new Reaction(iCorpse, Reaction.TypeCode.Heal, iInfected, playerInfos[iCorpse].position));
					}
				}

				// 이번 턴에 새로운 감염체를 치유한 경우 시체 Max 기록 갱신
				if ( isNewHealOccured == true )
				{
					scoreboard.Update(Scoreboard.ScoreTypeCode.Corpse_Max, iCorpse, playerStats[iCorpse].current_healed_players.size());
				}
			}
		}

		/*
		 * Phase 7. Vanish
		 */

		// 모든 감염체에 대해
		for ( int iInfected = 0; iInfected < Constants.Total_Players; iInfected++ )
		{
			if ( playerInfos[iInfected].state == PlayerInfo.State.Infected )
			{
				// 이번 턴에 치유받았다면 (제자리에 머물렀다 하더라도) 체력이 감소하지 않음
				if ( playerStats[iInfected].infected_healedAtThisTurn == true )
				{
					playerStats[iInfected].infected_healedAtThisTurn = false;
					playerStats[iInfected].infected_prayingAtThisTurn = false;
				}
				// 이번 턴에 아무도 처치하지 않았다면 체력 감소
				else
				{
					// 이번 턴에 제자리에 서 있었다면 체력 대폭 감소
					if ( playerStats[iInfected].infected_prayingAtThisTurn == true )
					{
						playerStats[iInfected].infected_prayingAtThisTurn = false;

						playerInfos[iInfected].hitPoint -= Constants.Infected_Cost_Pray;
					}
					else
					{
						playerInfos[iInfected].hitPoint -= Constants.Infected_Cost_Move;
					}
				}

				// 감소한 결과 체력이 0 이하가 된 경우
				if ( playerInfos[iInfected].hitPoint <= 0 )
				{
					// 상태 전환: 감염체 -> 영혼
					playerInfos[iInfected].hitPoint = 0;
					playerInfos[iInfected].state = PlayerInfo.State.Soul;
					playerStats[iInfected].transition_cooldown = Constants.Soul_Interval_Respawn;

					// 감염체 Max 기록 초기화
					playerStats[iInfected].current_kill_point = 0;

					// 사건 등록
					reactions.add(new Reaction(iInfected, Reaction.TypeCode.Vanish, iInfected, playerInfos[iInfected].position));
				}
			}
		}

		/*
		 * Phase 8. 다음 턴 준비
		 */

		// 플레이어 정보 초기화
		for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
		{
			// 인스턴스 참조를 저장하고 나중에 액세스하는 것을 방지하기 위해 새로 생성
			PlayerInfo info = new PlayerInfo(playerInfos[iPlayer]);

			playerInfos[iPlayer] = info;
			players[iPlayer].myInfo = info;
		}

		// 시야 정보 갱신
		RefreshSightInfo(false);

		// 쿨다운 진행
		if ( gameInfo.directInfectionCountdown != -1 )
		{
			--gameInfo.directInfectionCountdown;
		}

		for ( int i = 0; i < Constants.Total_Players; i++ )
		{
			if ( playerStats[i].transition_cooldown != 0 )
			{
				--playerStats[i].transition_cooldown;
			}
		}

		// 다음 턴에 직접 감염을 준비해야 하는지 체크
		boolean isAllSurvivors = true;

		for ( int i = 0; i < Constants.Total_Players; i++ )
		{
			// 시체 또는 감염체 상태인 플레이어가 한 명이라도 존재할 경우 직접 감염은 시작되지 않음
			if ( playerInfos[i].state == PlayerInfo.State.Corpse || playerInfos[i].state == PlayerInfo.State.Infected )
			{
				isAllSurvivors = false;
				break;
			}
		}

		if ( isAllSurvivors == true && gameInfo.directInfectionCountdown == -1 )
		{
			gameInfo.isDirectInfectionChoosingTurn = true;
			gameInfo.directInfectionCountdown = Constants.Survivor_Interval_DirectInfection;
		}

		// 콘솔 모드로 설정되어 있는 경우 턴 결과를 콘솔에 출력
		if ( settings.use_console_mode == true )
			PrintResultToConsole_AtEndOfTurn();

		// 다음 턴에 수행할 Decision 설정
		for ( int i = 0; i < Constants.Total_Players; i++ )
		{
			decisions[i].location_from.x = playerInfos[i].position.x;
			decisions[i].location_from.y = playerInfos[i].position.y;

			switch (playerInfos[i].state)
			{
			case Survivor:
				decisions[i].type = Decision.TypeCode.Survivor_Move;
				break;
			case Corpse:
				decisions[i].type = Decision.TypeCode.Corpse_Stay;
				break;
			case Infected:
				decisions[i].type = Decision.TypeCode.Infected_Move;
				break;
			case Soul:
				if ( playerStats[i].transition_cooldown == 0 )
				{
					decisions[i].type = Decision.TypeCode.Soul_Spawn;
				}
				else
				{
					decisions[i].type = Decision.TypeCode.Soul_Stay;
				}
				break;
			default:
				break;
			}
		}
	}

	/*
	 * 플레이어에게 제공될 시야 정보 갱신
	 * isForSpawn이 true인 경우(배치 준비) 이번에 배치될 영혼들에 대해서만 갱신 수행
	 */
	private void RefreshSightInfo(boolean isForSpawn)
	{
		// 배치 준비중인 경우
		if ( isForSpawn == true )
		{
			// 만약 이번 턴에 배치될 영혼이 하나도 없는 경우 갱신 생략
			boolean isNoSoulToSpawn = true;

			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				if ( playerInfos[iPlayer].state == PlayerInfo.State.Soul && playerStats[iPlayer].transition_cooldown == 0 )
				{
					isNoSoulToSpawn = false;
					break;
				}

			if ( isNoSoulToSpawn == true )
				return;

			
			// 배치될 영혼들의 정보 목록 초기화
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				if ( playerInfos[iPlayer].state == PlayerInfo.State.Soul && playerStats[iPlayer].transition_cooldown == 0 )
				{
					try
					{
						players[iPlayer].othersInfo_withinSight.clear();
						players[iPlayer].othersInfo_detected.clear();
						players[iPlayer].actions.clear();
						players[iPlayer].reactions.clear();
					}
					catch (Exception e)
					{
						// 혹시 플레이어가 목록 필드를 망가뜨린 경우 재생성
						players[iPlayer].othersInfo_withinSight = new ArrayList<PlayerInfo>();
						players[iPlayer].othersInfo_detected = new ArrayList<PlayerInfo>();
						players[iPlayer].actions = new ArrayList<Action>();
						players[iPlayer].reactions = new ArrayList<Reaction>();
					}
				}
			}

			// 우선 모든 칸을 '빈 칸'으로 간주
			cells.Reset();

			// 플레이어 정보를 각 칸에 추가
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				PlayerInfo info = playerInfos[iPlayer];

				// 영혼 상태의 플레이어는 강의실 위에 떠 있으므로 제외
				if ( info.state != PlayerInfo.State.Soul )
				{
					Point pos = playerInfos[iPlayer].position;
					CellInfo cell = cells.data[pos.y][pos.x];

					// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
					if ( cell == CellInfo.Blank )
					{
						cell = new CellInfo();
						cells.data[pos.y][pos.x] = cell;
					}

					cell.playersInTheCell.add(info);
				}
			}

			// 성공적인 이동 정보를 각 칸에 추가(아직 배치는 이루어지지 않았으므로 성공적인 '이동'만 목록에 존재)
			for ( Action action : valid_actions )
			{
				Point pos = action.location_to;
				CellInfo cell = cells.data[pos.y][pos.x];

				// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
				if ( cell == CellInfo.Blank )
				{
					cell = new CellInfo();
					cells.data[pos.y][pos.x] = cell;
				}

				cell.actionsInTheCell.add(action);
			}

			// 사건 정보를 각 칸에 추가
			for ( Reaction reaction : reactions )
			{
				Point pos = reaction.location;

				// 좌표 정보가 정상적인 사건만 추가
				if ( pos.IsValid() == true )
				{
					CellInfo cell = cells.data[pos.y][pos.x];

					// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
					if ( cell == CellInfo.Blank )
					{
						cell = new CellInfo();
						cells.data[pos.y][pos.x] = cell;
					}

					cell.reactionsInTheCell.add(reaction);
				}
			}

			// 배치될 영혼에게 제공할 정보 추가
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				Player player = players[iPlayer];
				PlayerInfo info = playerInfos[iPlayer];

				if ( info.state == PlayerInfo.State.Soul && playerStats[iPlayer].transition_cooldown == 0 )
				{
					// 모든 칸에 대한 시야, 행동, 사건 정보 추가
					CellInfo cell;
					for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
						for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							cell = cells.data[iRow][iColumn];
							player.othersInfo_withinSight.addAll(cell.playersInTheCell);
							if ( player.receiveActions == true )
								player.actions.addAll(cell.actionsInTheCell);
							if ( player.receiveReactions == true )
								player.reactions.addAll(cell.reactionsInTheCell);
						}

					// 강의실 위에 떠 있는 영혼 정보 추가
					if ( player.receiveOthersInfo_detected == true )
						for ( PlayerInfo otherInfo : playerInfos )
							if ( otherInfo.state == PlayerInfo.State.Soul )
								player.othersInfo_detected.add(otherInfo);
				}
			}
		}
		else
		{
			// 플레이어별 정보 목록 초기화
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				try
				{
					players[iPlayer].othersInfo_withinSight.clear();
					players[iPlayer].othersInfo_detected.clear();
					players[iPlayer].actions.clear();
					players[iPlayer].reactions.clear();
				}
				catch (Exception e)
				{
					// 혹시 플레이어가 목록 필드를 망가뜨린 경우 재생성
					players[iPlayer].othersInfo_withinSight = new ArrayList<PlayerInfo>();
					players[iPlayer].othersInfo_detected = new ArrayList<PlayerInfo>();
					players[iPlayer].actions = new ArrayList<Action>();
					players[iPlayer].reactions = new ArrayList<Reaction>();
				}
			}

			// 우선 모든 칸을 '빈 칸'으로 간주
			cells.Reset();

			// 플레이어 정보를 각 칸에 추가
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				PlayerInfo info = playerInfos[iPlayer];

				// 영혼 상태의 플레이어는 강의실 위에 떠 있으므로 제외
				if ( info.state != PlayerInfo.State.Soul )
				{
					Point pos = playerInfos[iPlayer].position;
					CellInfo cell = cells.data[pos.y][pos.x];

					// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
					if ( cell == CellInfo.Blank )
					{
						cell = new CellInfo();
						cells.data[pos.y][pos.x] = cell;
					}

					cell.playersInTheCell.add(info);
				}
			}

			// 시야 범위 내의 다른 플레이어 정보 추가
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				Player player = players[iPlayer];
				PlayerInfo info = playerInfos[iPlayer];
				Point pos;

				switch (info.state)
				{
				case Survivor:
					/*
					 * 아래 순서대로 칸 정보 검색 후 추가
					 * _ _ 0 _ _
					 * _ 1 2 3 _
					 * 4 5 6 7 8
					 * _ 9 A B _
					 * _ _ C _ _
					 */
					pos = new Point(info.position);

					pos.y -= 2;
					AddOthersInfoToPlayer(player, pos);

					++pos.y;
					--pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);

					++pos.y;
					pos.x -= 3;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);

					++pos.y;
					pos.x -= 3;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);
					++pos.x;
					AddOthersInfoToPlayer(player, pos);

					++pos.y;
					--pos.x;
					AddOthersInfoToPlayer(player, pos);
					break;
				case Corpse:
					// 자신이 현재 위치한 칸에 대한 정보만 추가
					AddOthersInfoToPlayer(player, info.position);
					break;
				case Infected:
					// 자신 중심 5x5칸에 대한 정보 추가
					pos = new Point();
					for ( int rowOffset = -2; rowOffset <= 2; ++rowOffset )
					{
						pos.y = info.position.y + rowOffset;
						for ( int columnOffset = -2; columnOffset <= 2; ++columnOffset )
						{
							pos.x = info.position.x + columnOffset;
							AddOthersInfoToPlayer(player, pos);
						}
					}
					break;
				case Soul:
					// 모든 칸에 대한 정보 추가
					CellInfo cell;
					for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
						for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
						{
							cell = cells.data[iRow][iColumn];
							player.othersInfo_withinSight.addAll(cell.playersInTheCell);
						}
					break;
				default:
					break;
				}
			}

			// 포착 및 감지 정보 생성
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				PlayerInfo myInfo = playerInfos[iPlayer];
				Player player = players[iPlayer];

				switch (myInfo.state)
				{
				case Survivor:
					// 생존자 - 포착 정보 생성 및 전파, 생존자 Total 기록 갱신
					int numberOfOtherSurvivors = -1; // 다른 생존자 수 집계에 나 자신은 제외하므로
														// 초기값을 -1로 지정
					int numberOfOtherNonSurvivors = 0;
					int point_spot;

					// 내 시야 범위 내의 자신 및 다른 생존자 검색, 다른 생존자 수와 시체/감염체 수 집계
					ArrayList<PlayerInfo> otherSurvivorsInSight = new ArrayList<PlayerInfo>();
					for ( PlayerInfo otherInfo : player.othersInfo_withinSight )
					{
						if ( otherInfo.state == PlayerInfo.State.Survivor )
						{
							otherSurvivorsInSight.add(otherInfo);
							++numberOfOtherSurvivors;
						}
						else
						{
							++numberOfOtherNonSurvivors;
						}

						//사건 등록
						reactions.add(new Reaction(iPlayer, Reaction.TypeCode.Spot, otherInfo.ID, myInfo.position));
					}

					// 자신 및 다른 생존자들에게 내가 포착한 플레이어 정보 전파
					for ( PlayerInfo otherSurvivor : otherSurvivorsInSight )
					{
						// 해당 생존자가 포착 정보를 수신한다면 전파
						if ( players[otherSurvivor.ID].receiveOthersInfo_detected == true )
							for ( PlayerInfo otherInfo : player.othersInfo_withinSight )
								// 해당 포착 정보를 보유하고 있지 않은 경우에만 전파
								if ( players[otherSurvivor.ID].othersInfo_detected.contains(otherInfo) == false )
									players[otherSurvivor.ID].othersInfo_detected.add(otherInfo);
					}

					// 점수 산정 및 갱신 - 시야 범위 내의 시체 및 감염체 수 * 시야 범위 내의 생존자 수(자신 제외)
					point_spot = numberOfOtherNonSurvivors * numberOfOtherSurvivors;

					if ( point_spot != 0 )
						scoreboard.Update(Scoreboard.ScoreTypeCode.Survivor_Total, iPlayer, point_spot);
					break;
				case Infected:
					// 감염체 - 강의실 내의 시체 감지
					// 자신이 감지 정보를 수신한다면 추가
					if ( player.receiveOthersInfo_detected == true )
						for ( PlayerInfo otherInfo : playerInfos )
							if ( otherInfo.state == PlayerInfo.State.Corpse )
								player.othersInfo_detected.add(otherInfo);
					break;
				case Soul:
					// 영혼 - 강의실 위에 떠 있는 영혼 감지
					// 자신이 감지 정보를 수신한다면 추가
					if ( player.receiveOthersInfo_detected == true )
						for ( PlayerInfo otherInfo : playerInfos )
							if ( otherInfo.state == PlayerInfo.State.Soul )
								player.othersInfo_detected.add(otherInfo);
					break;
				default:
					break;
				}
			}

			// 유효한 행동 정보를 각 칸에 추가
			for ( Action action : valid_actions )
			{
				Point pos = action.location_to;
				CellInfo cell = cells.data[pos.y][pos.x];

				// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
				if ( cell == CellInfo.Blank )
				{
					cell = new CellInfo();
					cells.data[pos.y][pos.x] = cell;
				}

				cell.actionsInTheCell.add(action);
			}

			// 사건 정보를 각 칸에 추가
			for ( Reaction reaction : reactions )
			{
				Point pos = reaction.location;
				
				//유효한 좌표에서 발생한 사건만 추가
				if ( pos.IsValid() == true )
				{
					CellInfo cell = cells.data[pos.y][pos.x];
	
					// 빈 칸에 처음 정보를 추가할 때 해당 칸에 대한 새 인스턴스 생성
					if ( cell == CellInfo.Blank )
					{
						cell = new CellInfo();
						cells.data[pos.y][pos.x] = cell;
					}
	
					cell.reactionsInTheCell.add(reaction);
				}
			}

			// 행동 및 사건 정보 추가
			for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
			{
				Player player = players[iPlayer];

				// 해당 플레이어가 정보 수신을 희망한 경우에만 추가
				if ( player.receiveActions == true || player.receiveReactions == true )
				{
					PlayerInfo info = playerInfos[iPlayer];
					Point pos;

					switch (info.state)
					{
					case Survivor:
						/*
						 * 아래 순서대로 칸 정보 검색 후 추가
						 * _ _ 0 _ _
						 * _ 1 2 3 _
						 * 4 5 6 7 8
						 * _ 9 A B _
						 * _ _ C _ _
						 */
						pos = new Point(info.position);

						pos.y -= 2;
						AddAdditionalInfoToPlayer(player, pos);

						++pos.y;
						--pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);

						++pos.y;
						pos.x -= 3;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);

						++pos.y;
						pos.x -= 3;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						++pos.x;
						AddAdditionalInfoToPlayer(player, pos);

						++pos.y;
						--pos.x;
						AddAdditionalInfoToPlayer(player, pos);
						break;
					case Corpse:
						// 자신이 현재 위치한 칸에 대한 정보만 추가
						AddAdditionalInfoToPlayer(player, info.position);
						break;
					case Infected:
						// 자신 중심 5x5칸에 대한 정보 추가
						pos = new Point();
						for ( int rowOffset = -2; rowOffset <= 2; ++rowOffset )
						{
							pos.y = info.position.y + rowOffset;
							for ( int columnOffset = -2; columnOffset <= 2; ++columnOffset )
							{
								pos.x = info.position.x + columnOffset;
								AddAdditionalInfoToPlayer(player, pos);
							}
						}
						break;
					case Soul:
						// 모든 칸에 대한 정보 추가
						CellInfo cell;
						for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
							for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
							{
								cell = cells.data[iRow][iColumn];
								if ( player.receiveActions == true )
									player.actions.addAll(cell.actionsInTheCell);
								if ( player.receiveReactions == true )
									player.reactions.addAll(cell.reactionsInTheCell);
							}
						
						break;
					default:
						break;
					}
				}
			}
		}
	}
	

	/*
	 * 특정 플레이어의 시야 범위 내에 있는 해당 위치의 다른 플레이어 정보를 목록에 추가
	 */
	private void AddOthersInfoToPlayer(Player player, Point pos)
	{
		if ( pos.IsValid() == true )
		{
			CellInfo cell = cells.data[pos.y][pos.x];
			player.othersInfo_withinSight.addAll(cell.playersInTheCell);
		}
	}
	

	/*
	 * 특정 플레이어의 시야 범위 내에 있는 해당 위치의 행동 및 사건 정보를 목록에 추가
	 */
	private void AddAdditionalInfoToPlayer(Player player, Point pos)
	{
		if ( pos.IsValid() == true )
		{
			CellInfo cell = cells.data[pos.y][pos.x];
			if ( player.receiveActions == true )
				player.actions.addAll(cell.actionsInTheCell);
			if ( player.receiveReactions == true )
				player.reactions.addAll(cell.reactionsInTheCell);
		}
	}
	

	/*
	 * 턴이 끝날 때 각 정보를 콘솔에 출력
	 */
	private void PrintResultToConsole_AtEndOfTurn()
	{
		// 플레이어#0에 대한 요소만 출력하려는 경우
		if ( settings.print_first_player_only == true )
		{
			if ( settings.print_playerInfos == true )
			{
				System.out.println(players[0].name + " | State: " + playerInfos[0].state + ", HP: " + playerInfos[0].hitPoint + ", Position: " + playerInfos[0].position);
			}

			if ( settings.print_decisions == true )
			{
				switch (decisions[0].type)
				{
				case Survivor_Move:
					System.out.println("Decision: Move to " + decisions[0].direction + " as Survivor");
					break;
				case Corpse_Stay:
					System.out.println("Decision: Stay as Corpse");
					break;
				case Infected_Move:
					System.out.println("Decision: Move to " + decisions[0].direction + " as Infected");
					break;
				case Soul_Stay:
					System.out.println("Decision: Stay as Soul");
					break;
				case Soul_Spawn:
					System.out.println("Decision: Spawn at " + decisions[0].location_to);
					break;
				default:
					break;
				}
			}

			if ( settings.print_actions == true )
			{
				for ( Action move : moves )
					if ( move.actorID == 0 )
					{
						System.out.println("Action: Move from " + move.location_from + " to " + move.location_to + (move.type == Action.TypeCode.Illigal ? " - Illigal" : ""));
						break;
					}

				for ( Action spawn : spawns )
					if ( spawn.actorID == 0 )
					{
						System.out.println("Action: Spawn at " + spawn.location_to + (spawn.type == Action.TypeCode.Illigal ? " - Illigal" : ""));
						break;
					}
			}

			if ( settings.print_reactions == true )
			{
				System.out.println("Reactions:");

				for ( Reaction reaction : reactions )
					if ( reaction.subjectID == 0 )
					{
						System.out.println("Type: " + reaction.type + ", Subject: " + players[reaction.subjectID].name + ", Object: " + players[reaction.objectID].name + ", Location: " + reaction.location);
					}
			}

			if ( settings.print_scores_during_game == true )
			{
				System.out.println(
						"Score:\n" +
								"SMax STot CMax CTot IMax ITot");

				System.out.println(scoreboard.scores[0]);
			}
		}
		// 모든 플레이어에 대해 출력하려는 경우
		else
		{
			if ( settings.print_playerInfos == true )
			{
				System.out.println("PlayerInfos:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.println(players[0].name + " | State: " + playerInfos[0].state + ", HP: " + playerInfos[0].hitPoint + ", Position: " + playerInfos[0].position);
			}

			if ( settings.print_decisions == true )
			{
				System.out.println("Decisions:");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
				{
					switch (decisions[iPlayer].type)
					{
					case Survivor_Move:
						System.out.println(players[iPlayer].name + ": Move to " + decisions[iPlayer].direction + " as Survivor");
						break;
					case Corpse_Stay:
						System.out.println(players[iPlayer].name + ": Stay as Corpse");
						break;
					case Infected_Move:
						System.out.println(players[iPlayer].name + ": Move to " + decisions[iPlayer].direction + " as Infected");
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
				for ( Action move : moves )
					System.out.println(players[move.actorID].name + ": Move from " + move.location_from + " to " + move.location_to + (move.type == Action.TypeCode.Illigal ? " - Illigal" : ""));

				System.out.println("Spawns:");
				for ( Action spawn : spawns )
					System.out.println(players[spawn.actorID].name + ": Spawn at " + spawn.location_to + (spawn.type == Action.TypeCode.Illigal ? " - Illigal" : ""));
			}

			if ( settings.print_reactions == true )
			{
				System.out.println("Reactions:");
				for ( Reaction reaction : reactions )
					System.out.println("Type: " + reaction.type + ", Subject: " + players[reaction.subjectID].name + ", Object: " + players[reaction.objectID].name + ", Location: " + reaction.location);
			}

			if ( settings.print_scores_during_game == true )
			{
				System.out.println(
						"Score:\n" +
								"SMax STot CMax CTot IMax ITot - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.println(scoreboard.scores[iPlayer].toString() + " - " + players[iPlayer].name);
			}
		}
	}
	
	/*
	 * 게임 결과를 콘솔에 출력
	 */
	private void PrintResultToConsole_AtEndOfTheGame()
	{
		if ( settings.print_first_player_only == true )
		{
			if ( settings.print_scores_at_the_end == true )
			{
				System.out.println(
						"Score|Grade(Rank):\n" +
								"       SMax         STot         CMax         CTot         IMax         ITot |    Final");
				System.out.printf("%3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d) |  %3d(%2d)\n", scoreboard.scores[0].data[0], scoreboard.grades[0][0], scoreboard.ranks[0][0], scoreboard.scores[0].data[1], scoreboard.grades[0][1], scoreboard.ranks[0][1], scoreboard.scores[0].data[2], scoreboard.grades[0][2], scoreboard.ranks[0][2], scoreboard.scores[0].data[3], scoreboard.grades[0][3], scoreboard.ranks[0][3], scoreboard.scores[0].data[4], scoreboard.grades[0][4], scoreboard.ranks[0][4], scoreboard.scores[0].data[5], scoreboard.grades[0][5], scoreboard.ranks[0][5], scoreboard.final_grades[0], scoreboard.final_ranks[0]);
			}
			else
			{
				System.out.println(
						"Grade(Rank):\n" +
								"   SMax     STot     CMax     CTot     IMax     ITot |    Final");
				System.out.printf("%3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d) |  %3d(%2d)\n", scoreboard.grades[0][0], scoreboard.ranks[0][0], scoreboard.grades[0][1], scoreboard.ranks[0][1], scoreboard.grades[0][2], scoreboard.ranks[0][2], scoreboard.grades[0][3], scoreboard.ranks[0][3], scoreboard.grades[0][4], scoreboard.ranks[0][4], scoreboard.grades[0][5], scoreboard.ranks[0][5], scoreboard.final_grades[0], scoreboard.final_ranks[0]);
			}
		}
		else
		{
			if ( settings.print_scores_at_the_end == true )
			{
				System.out.println(
						"Score|Grade(Rank):\n" +
								"       SMax         STot         CMax         CTot         IMax         ITot |    Final - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.printf("%3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d)  %3d|%3d(%2d) |  %3d(%2d) - %s\n", scoreboard.scores[iPlayer].data[0], scoreboard.grades[iPlayer][0], scoreboard.ranks[iPlayer][0], scoreboard.scores[iPlayer].data[1], scoreboard.grades[iPlayer][1], scoreboard.ranks[iPlayer][1], scoreboard.scores[iPlayer].data[2], scoreboard.grades[iPlayer][2], scoreboard.ranks[iPlayer][2], scoreboard.scores[iPlayer].data[3], scoreboard.grades[iPlayer][3], scoreboard.ranks[iPlayer][3], scoreboard.scores[iPlayer].data[4], scoreboard.grades[iPlayer][4], scoreboard.ranks[iPlayer][4], scoreboard.scores[iPlayer].data[5], scoreboard.grades[iPlayer][5], scoreboard.ranks[iPlayer][5], scoreboard.final_grades[iPlayer], scoreboard.final_ranks[iPlayer], players[iPlayer].name);
			}
			else
			{
				System.out.println(
						"Grade(Rank):\n" +
								"   SMax     STot     CMax     CTot     IMax     ITot |    Final - Name");
				for ( int iPlayer = 0; iPlayer < Constants.Total_Players; ++iPlayer )
					System.out.printf("%3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d)  %3d(%2d) |  %3d(%2d) - %s\n", scoreboard.grades[iPlayer][0], scoreboard.ranks[iPlayer][0], scoreboard.grades[iPlayer][1], scoreboard.ranks[iPlayer][1], scoreboard.grades[iPlayer][2], scoreboard.ranks[iPlayer][2], scoreboard.grades[iPlayer][3], scoreboard.ranks[iPlayer][3], scoreboard.grades[iPlayer][4], scoreboard.ranks[iPlayer][4], scoreboard.grades[iPlayer][5], scoreboard.ranks[iPlayer][5], scoreboard.final_grades[iPlayer], scoreboard.final_ranks[iPlayer], players[iPlayer].name);
			}
		}
	}
	
	@Override
	public void run()
	{
		Initialize();
		Start();
	}
}
