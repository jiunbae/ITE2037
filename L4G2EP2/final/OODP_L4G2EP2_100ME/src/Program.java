import java.awt.Color;

import l4g.Classroom;
import l4g.Grader;
import l4g.Presenter_Mode3;
import l4g.Presenter_Mode5;
import l4g.Presenter_Mode4;
import l4g.common.Classroom_Settings;
import l4g.common.GameNumberManager;
import l4g.customplayers.*;
import loot.GameFrameSettings;


public class Program
{
	public static void main(String[] args)
	{
		//settings는 강의실 설정(게임 한 판 설정)을 위한 여러 필드들을 가지고 있습니다.
		Classroom_Settings settings = new Classroom_Settings();
		
		/*
		 * mode값을 바꿈으로써 게임을 여러 가지 방법으로 실행해 볼 수 있습니다.
		 * 
		 * mode값이	0이면 콘솔 창을 사용하여 게임 한 판을 진행하며
		 * 			매 턴마다 첫 플레이어(아마도 여러분이 만든 그 플레이어)가 내린 의사 결정의 내용,
		 * 			어떤 행동을 했는지, 시야 범위에서 어떤 사건들이 발생했는지를 출력하고,
		 *          턴 종료시 첫 플레이어의 현재 상태 및 점수를 출력한 다음
		 *			엔터 키를 입력받을 때까지 게임을 일시 정지합니다. 
		 * 			게임이 끝나면 첫 플레이어의 학점을 출력합니다.
		 * 
		 * mode값이 1이면 콘솔 창을 사용하여 게임 한 판을 진행하며 
		 * 			매 턴마다 모든 행동, 사건 정보를 출력하고, 
		 * 			턴 종료시 모든 플레이어의 현재 상태 및 점수를 출력한 다음 
		 *			엔터 키를 입력받을 때까지 게임을 일시 정지합니다. 
		 * 			게임이 끝나면 모든 플레이어의 학점을 출력합니다.
		 * 
		 * mode값이 2면 콘솔 창을 사용하여 임의의 게임 번호들을 통해 게임 1,000판을 진행하며
		 * 			게임이 모두 끝나면 각 부문별 순위 및 기록을 출력한 다음
		 * 			첫 플레이어(아마도 여러분이 만든 그 플레이어)의 기록을 출력합니다.
		 * 
		 * mode값이 3이면 테스트용 Presenter를 사용하여 게임 한 판을 진행하며
		 *			이를 통해 첫 플레이어(아마도 여러분이 만든 그 플레이어)가 획득할 수 있는 정보들을 매 턴마다 시각적으로 살펴 볼 수 있습니다.
		 *			게임이 끝나면 콘솔 창에 첫 플레이어의 학점을 출력합니다.
		 *			게임이 끝난 이후에도 엔터 키를 치면 또 다시 시작할 수 있습니다.
		 *
		 * mode값이 4면 리플레이용 Presenter를 사용하여 아래에서 설정한 특정 게임 번호로 게임 한 판을 진행하며
		 * 			mode 3와 동일한 방식으로 해당 판에 대한 다양한 정보들을 시각적으로 살펴 볼 수 있습니다.
		 *
		 * mode값이 5면 최종 버전 Presenter를 사용하여 게임 한 판을 시작합니다.
		 * 			키보드 / 마우스를 통해 여러분이 만든 플레이어들과 즐거운 시간을 보낼 수 있습니다!
		 */
		
		int mode = 5;

		

		/*
		 * 아래의 게임 번호를 -1이 아닌 다른 값으로 설정하면
		 * mode값이 0, 1, 3, 4, 5일 때
		 * 항상 해당 게임 번호를 사용하여 게임을 진행합니다.
		 * 주의: 게임 번호는 long 형식이므로 상수값 뒤에 L을 꼭 붙여 주세요.
		 * 
		 * TODO 아래에 여러분이 돌려 보고 싶은 게임 번호를 적어 주세요. 또는, 임의의 한 게임을 진행해 보려는 경우 -1로 설정해 주세요.
		 */
		settings.game_number = -1L;
		
		
		/*
		 * TODO mode 값이 0, 1, 2, 3, 4일 때
		 * 아래의 코드에서 Player_오작동하는_치유로봇 부분을 여러분이 작성한 플레이어 클래스 이름으로 바꿔 주세요.
		 * 그렇게 하면 해당 플레이어의 관점에서 각종 정보가 표시됩니다!
		 */
		settings.playerToFocus = Player_오작동하는_치유로봇.class;
		
		
		
		
		
		
		
		
		
		
		
		/* --------------------------------------------------------------------------------------------------------
		 * --------------------------------------------------------------------------------------------------------
		 * --------------------------------------------------------------------------------------------------------
		 * --------------------------------------------------------------------------------------------------------
		 * 
		 * 주의!
		 * 
		 * 아래의 설정값을 바꾸면 '정규 게임'과 다른 결과가 나오게 됩니다!
		 * 제대로 된 리플레이를 보고 싶다면 아래 내용은 절대 건드리지 말아 주세요!
		 * 
		 */
		
		
		
		/*
		 * 직접 만든 플레이어를 등록하는 부분입니다.
		 * 자신이 만든 플레이어 클래스를 아래의 주석 내용을 참고하여 등록하세요.
		 * (주석 내용을 복붙한 다음 Player_YOURNAMEHERE 대신 여러분의 플레이어 클래스 이름을 넣으면 됩니다)
		 * 
		 * 주의: 같은 클래스를 여러 번 등록하는 것도 가능합니다.
		 * 		 하지만 이렇게 하는 경우 여러 플레이어가 항상 같은 의사 결정을 하게 될 수 있으니
		 * 		 가급적 한 클래스는 한 번만 등록하는 것이 좋겠습니다.
		 */
		
		// settings.custom_player_classes.add(Player_YOURNAMEHERE.class);

		// team Mu, Omega, Lambda 플레이어 부분 ( 제출시각 기준, 총 83명 )
		settings.custom_player_classes.add(Player_여운학_난벽이좋아.class);
		settings.custom_player_classes.add(Player_niangniangpunch.class);
		settings.custom_player_classes.add(은조공주.class);
		settings.custom_player_classes.add(Player_Jeon_Hyeong_Jun.class);
		settings.custom_player_classes.add(Player_DongSimOne.class);
		
		settings.custom_player_classes.add(Player_MIDSIN.class);
		settings.custom_player_classes.add(Player_YangSangheon.class);
		settings.custom_player_classes.add(Player_run_survive.class);
		settings.custom_player_classes.add(Jerod.class);
		settings.custom_player_classes.add(Player_수수께끼의_도전자.class);
		
		// 10 ~ 19
		settings.custom_player_classes.add(Player_PuddingHell.class);		
		settings.custom_player_classes.add(SangU.class);
		settings.custom_player_classes.add(Ramyeon.class);
		settings.custom_player_classes.add(Player_안녕로봇.class);
		settings.custom_player_classes.add(Player_Horse_Save3.class);

		settings.custom_player_classes.add(LowerBodyParalysis.class);
		settings.custom_player_classes.add(Player_All_State_Stability.class);
		settings.custom_player_classes.add(Player_Jim_Raynor.class);
		settings.custom_player_classes.add(거긴춥다구.class);
		settings.custom_player_classes.add(Player_COUPROACH.class);
		

		// 20 ~ 29
		settings.custom_player_classes.add(Player_Hunter_Killer.class);
		settings.custom_player_classes.add(Player_군단이나오면일리단을잡겠습니다.class);
		settings.custom_player_classes.add(Player_HYCat.class);
		settings.custom_player_classes.add(Player_Ori.class);
		settings.custom_player_classes.add(유태욱.class);

		settings.custom_player_classes.add(Player_MJ_.class);
		settings.custom_player_classes.add(공주.class);
		settings.custom_player_classes.add(Player_NO_ANSWER.class);
		settings.custom_player_classes.add(Player_Four_Walls.class);
		settings.custom_player_classes.add(Player_Yong.class);

		
		// 30 ~ 39
		settings.custom_player_classes.add(OhGod.class);
		settings.custom_player_classes.add(TheGivingTree.class);
		settings.custom_player_classes.add(Fittest.class);
		settings.custom_player_classes.add(Player_2012004270.class);
		settings.custom_player_classes.add(Player_Baggio.class);

		settings.custom_player_classes.add(Nintendojam.class);
		settings.custom_player_classes.add(Player_LenaPark.class);
		settings.custom_player_classes.add(Player_무특징이특징.class);
		settings.custom_player_classes.add(Player_천사소녀네티.class);
		settings.custom_player_classes.add(Player_Arous_best.class);

		
		// 40 ~ 49
		settings.custom_player_classes.add(Player_Jisu.class);
		settings.custom_player_classes.add(Type_O.class);
		settings.custom_player_classes.add(Player_WinTreeC.class);
		settings.custom_player_classes.add(SeunDuBuJjiGae.class);
		settings.custom_player_classes.add(검은수염티치_봇열매여러개먹음.class);
		
		settings.custom_player_classes.add(Player_Jaeseok.class);
		settings.custom_player_classes.add(Player_Bravo.class);
		settings.custom_player_classes.add(Player_CanYouFeelMyHeartBeat.class);
		settings.custom_player_classes.add(Player_Corner.class);
		settings.custom_player_classes.add(Player_Dont_Let_Him_Gamble.class);

		
		// 50 ~ 59
		settings.custom_player_classes.add(I_DO_NOT_LIKE_JAVA.class);
		settings.custom_player_classes.add(Player_정현교.class);
		settings.custom_player_classes.add(Player_Jiwon.class);
		settings.custom_player_classes.add(Player_GradeGiver.class);
		settings.custom_player_classes.add(DingDong.class);

		settings.custom_player_classes.add(Player_DONT_CODES_AMEN.class);		
		settings.custom_player_classes.add(MissingNo.class);
		settings.custom_player_classes.add(Player_JEONGMIN.class);
		settings.custom_player_classes.add(Player_이름할게없다.class);
		settings.custom_player_classes.add(KIMAEHO2.class);

		
		// 60 ~ 69
		settings.custom_player_classes.add(corpse_king.class);
		settings.custom_player_classes.add(Player_LSHf1.class);
		settings.custom_player_classes.add(Byun.class);
		settings.custom_player_classes.add(Player_yaso.class);
		settings.custom_player_classes.add(Player_lee.class);

		settings.custom_player_classes.add(Player_BotBoot.class);
		settings.custom_player_classes.add(Player_ANG.class);
		settings.custom_player_classes.add(Player_INHWA.class);
		settings.custom_player_classes.add(Player_2010003969.class);
		settings.custom_player_classes.add(어차피우승은오승준.class);

		
		// 70 ~ 79
		settings.custom_player_classes.add(Player_죽으면시무룩.class);
		settings.custom_player_classes.add(SMP.class);
		settings.custom_player_classes.add(Player_아저씨.class);
		settings.custom_player_classes.add(Player_Charles.class);
		settings.custom_player_classes.add(Player_Soyoung2.class);
		
		settings.custom_player_classes.add(Player_I_Like_stroll.class);
		settings.custom_player_classes.add(Player_Songjunggi.class);
		settings.custom_player_classes.add(Player_맹호정찰.class);
		settings.custom_player_classes.add(TEJAVA.class);
		settings.custom_player_classes.add(Player_ONEROUTE.class);
		
		
		// 80 ~ 82
		settings.custom_player_classes.add(Jinhyeok.class);
		settings.custom_player_classes.add(Player_PreferName.class);
		settings.custom_player_classes.add(Player_PineApple.class);
		
		
		// team Sigma 플레이어 부분 (100자리 중 남은 자리만큼 추가, 총 17명)
		settings.custom_player_classes.add(Player_오작동하는_치유로봇.class);
		settings.custom_player_classes.add(Player_사생팬1.class);
		settings.custom_player_classes.add(Player_사생팬2.class);
		settings.custom_player_classes.add(Player_사생팬3.class);
		settings.custom_player_classes.add(Player_점수_약탈자.class);

		settings.custom_player_classes.add(Player_성동에어쇼2.class);
		settings.custom_player_classes.add(Player_성동에어쇼1.class);
		settings.custom_player_classes.add(Player_성동에어쇼3.class);
		settings.custom_player_classes.add(Player_땅따먹기_유저.class);
		settings.custom_player_classes.add(Player_예술가.class);

		settings.custom_player_classes.add(Player_ROOT.class);
		settings.custom_player_classes.add(Player_Yandere.class);
		settings.custom_player_classes.add(Player_PIMFY1.class);
		settings.custom_player_classes.add(Player_PIMFY2.class);
		settings.custom_player_classes.add(Player_PIMFY3.class);
		
		settings.custom_player_classes.add(Player_PIMFY4.class);
		settings.custom_player_classes.add(Player_PIMFY5.class);
		
		/*
		 * 정규 게임에서 NPC(학점 산정에 영향을 주지 않는 플레이어) 수를 설정하는 부분입니다.
		 * 여러분의 플레이어를 테스트할 땐 이 필드의 값은 그냥 0으로 두세요. 
		 */		
		settings.numberOfNPCs = 100 - 83;
		
		/*
		 * 게임에 참여할 '무법자 Bot 플레이어 수'의 최대값을 변경할 수 있습니다.
		 * 0 이하로 두면 무법자는 게임에 참여하지 않습니다.
		 * 
		 * '정규 게임'에서 이 값은 0으로 설정됩니다.  
		 */
		settings.max_numberOfHornDonePlayer = 0;
		
		/*
		 * 게임에 참여할 Bot 플레이어들의 분포와 순서를 정할 때 사용하는
		 * seed 문자열을 설정하는 부분입니다.
		 * 
		 * 기본값은 "16OODP"이며
		 * 이를 다른 문자열로 바꾸는 경우 게임에 같이 참여할 Bot 플레이어 목록도 바뀌게 됩니다.
		 * 
		 * 자신의 플레이어를 여러 가지 플레이어 조합 안에서 테스트해 보려는 경우
		 * "16OODP" 대신 다른 문자열을 설정해 보세요.
		 * 
		 * 주의: 만약 이 문자열을 '빈 문자열' 또는 null로 설정하는 경우
		 * 		 강의실을 초기화할 때 다시 기본값으로 복구됩니다.
		 */
		settings.seed_for_sample_players = "16OODP";		
		
		/*
		 * 아래 설정값을 true로 두면
		 * 게임 진행 도중 플레이어가 런타임 예외를 내는 경우
		 * 해당 예외에 대한 정보를 콘솔 창에 출력합니다.
		 * 
		 * 이거 이외에도 settings에는 다양한 설정 옵션들이 들어 있으니
		 * 아래의 각 mode별 실행 부분을 적절히 변경하여
		 * 여러분 입맛에 맞는 테스트를 진행해 볼 수 있습니다.
		 */
		settings.print_errors = false;
		
		
		
		
		
		/* --------------------------------------------------------------------------------------------
		 * 
		 * 이 아래에는 각 모드별 실행 코드가 나열되어 있습니다.
		 * 테스트 환경을 내 입맛대로 설정하고 싶은 게 아니라면
		 * 이 아래에 있는 내용은 안 봐도 됩니다.
		 * 
		 */

		if ( mode == 0 )
		{
			settings.print_focused_player_only = true;
			settings.print_decisions = true;
			settings.print_actions = true;
			settings.print_reactions = true;
			settings.print_playerInfos = true;
			settings.print_scores_at_each_turns = true;
			settings.print_scores_at_the_end = true;
			
			settings.callback_EndTurn = () ->
			{
				try
				{
					System.out.print("Press Enter to continue...");
					System.in.skip(System.in.available());
					System.in.read();
				}
				catch ( Exception e )
				{
					
				}
				return true;
			};
			
			Classroom classroom = new Classroom(settings);
			classroom.Initialize();
			classroom.Start();
		}
		
		if ( mode == 1 )
		{
			settings.print_focused_player_only = false;
			settings.print_decisions = false;
			settings.print_actions = false;
			settings.print_reactions = false;
			settings.print_playerInfos = false;
			settings.use_console_mode = true;
			settings.print_scores_at_each_turns = false;
			settings.print_scores_at_the_end = true;
			
			settings.callback_EndTurn = () ->
			{
				try
				{
					System.out.print("Press Enter to continue...");
					System.in.skip(System.in.available());
					System.in.read();
				}
				catch ( Exception e )
				{
					
				}
				return true;
			};
			
			Classroom classroom = new Classroom(settings);
			classroom.Initialize();
			classroom.Start();
		}
		
		if ( mode == 2 )
		{
			GameNumberManager numbers = new GameNumberManager(1000);
			numbers.Create(-1);
			
			Grader grader = new Grader();
			
			settings.use_console_mode = false;

			Classroom classroom = null;
			
			for ( int iGame = 0; iGame < 1000; ++iGame )
			{
				if ( iGame % 100 == 0 )
					System.out.println(iGame + " / 1000 games completed...");
				settings.game_number = numbers.data[iGame];
				classroom = new Classroom(settings);
				classroom.Initialize();
				classroom.Start();
				grader.Update(classroom);
			}

			System.out.println("Done.");
			
			grader.PrintResults(classroom);
			grader.PrintResultsOf(0, classroom);
		}
		
		if ( mode == 3 )
		{
			settings.print_focused_player_only = true;
			settings.print_decisions = false;
			settings.print_actions = false;
			settings.print_reactions = false;
			settings.print_playerInfos = false;
			settings.print_scores_at_each_turns = false;
			settings.print_scores_at_the_end = true;
			
			GameFrameSettings gfs = new GameFrameSettings();
			gfs.window_title = "L4G2EP2-100ME 테스트용 Presenter";
			gfs.canvas_width = 1080;
			gfs.canvas_height = 850;
			gfs.numberOfButtons = 20;
			gfs.canvas_backgroundColor = Color.lightGray;
			
			Presenter_Mode3 window = new Presenter_Mode3(gfs, settings);
			window.setVisible(true);
		}
		
		if ( mode == 4 )
		{
			settings.print_focused_player_only = true;
			settings.print_decisions = false;
			settings.print_actions = false;
			settings.print_reactions = false;
			settings.print_playerInfos = false;
			settings.print_scores_at_each_turns = false;
			settings.print_scores_at_the_end = true;
			
			GameFrameSettings gfs = new GameFrameSettings();
			gfs.window_title = "L4G2EP2-100ME 리플레이용 Presenter";
			gfs.canvas_width = 1080;
			gfs.canvas_height = 850;
			gfs.numberOfButtons = 20;
			gfs.canvas_backgroundColor = Color.lightGray;
			
			Presenter_Mode4 window = new Presenter_Mode4(gfs, settings);
			window.setVisible(true);
		}
		
		if ( mode == 5 )
		{
			settings.print_focused_player_only = true;
			settings.print_decisions = false;
			settings.print_actions = false;
			settings.print_reactions = false;
			settings.print_playerInfos = false;
			settings.print_scores_at_each_turns = false;
			settings.print_scores_at_the_end = false;
			settings.use_console_mode = false;
			
			// 마지막 플레이어(team Sigma)를 제거하고 직접 조작 가능한 플레이어 탑재
			settings.custom_player_classes.remove(settings.custom_player_classes.size() - 1);
			--settings.numberOfNPCs;
			settings.use_ctrlable_player = true;
			
			GameFrameSettings gfs = new GameFrameSettings();
			gfs.window_title = "Left 4 Grade 2 Episode 2 - 100ME";
			gfs.canvas_width = 1080;
			gfs.canvas_height = 850;
			gfs.numberOfButtons = 22;
			gfs.canvas_backgroundColor = Color.lightGray;
			
			Presenter_Mode5 window = new Presenter_Mode5(gfs, settings);
			window.setVisible(true);			
		}
	}
}

