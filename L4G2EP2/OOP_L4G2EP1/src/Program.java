import l4g2ep1.*;
import l4g2ep1.common.*;
import l4g2ep1.customplayers.*;

public class Program
{
	public static void main(String[] args)
	{
		Classroom_Settings settings = new Classroom_Settings();

		/*
		 * mode값을 바꿈으로써 게임을 여러 가지 방법으로 실행해 볼 수 있습니다.
		 * 
		 * mode값이	0이면 콘솔 창을 사용하여 게임 한 판을 진행하며
		 * 			첫 플레이어(아마도 여러분이 만든 그 플레이어)와 연관된 행동 / 사건 / 점수를 매 턴 출력하고
		 * 			게임이 끝나면 첫 플레이어의 학점을 출력합니다.
		 * 
		 * mode값이 1이면 콘솔 창을 사용하여 게임 한 판을 진행하며
		 * 			게임 도중에는 아무 정보도 출력하지 않다가
		 * 			게임이 끝나면 모든 플레이어의 학점을 출력합니다.
		 * 
		 * mode값이 2면 콘솔 창을 사용하여 임의의 게임 번호들을 통해 게임 10,000판을 진행하며
		 * 			게임이 모두 끝나면 각 부문별 순위 및 기록을 출력한 다음
		 * 			첫 플레이어(아마도 여러분이 만든 그 플레이어)의 기록을 출력합니다.
		 * 
		 * mode값이 3이면 콘솔 창을 사용하여 정규 게임에서 사용할 게임 번호들을 통해 게임 10,000판을 진행하며
		 * 			게임이 모두 끝나면 각 부문별 순위 및 기록을 출력합니다.
		 * 
		 * mode값이 4면 테스트용 Presenter를 사용하여 게임 한 판 또는 100판을 진행하며
		 *			이를 통해 원하는 게임 번호를 입력하고 버튼을 누르며 게임 진행 상황을 시각적으로 확인해 보거나
		 *			임의의 여러 게임을 진행하며 자신의 플레이어가 높은 / 낮은 점수를 받는 게임 번호를 찾아 분석해 볼 수 있습니다. 
		 *			(테스트용 Presenter는 Start 버튼을 누를 때마다 새 강의실을 생성하므로 한 번 켠 다음 여러 게임을 진행해 볼 수 있습니다)
		 *			테스트용 Presenter 사용 방법은 프로젝트 폴더 맨 아래에 있는 '버전 정보' 파일을 참고하세요.
		 *
		 * mode값이 5면 경연용 Presenter를 사용하여 게임 한 판 또는 10,000판을 진행하며
		 * 			이를 통해 원하는 게임 번호를 입력하고 버튼을 누르며 게임 진행 상황을 시각적으로 확인해 보거나
		 * 			정규 게임을 진행하며 각 시상 항목별 순위를 확인해 볼 수 있습니다. 
		 * 			경연용 Presenter 사용 방법은 프로젝트 폴더 맨 아래에 있는 '버전 정보' 파일을 참고하세요.
		 * 
		 * mode값이 6이면 최종 Presenter를 사용하여 임의의 게임 한 판을 진행할 수 있습니다!
		 * 
		 * TODO 자신이 원하는 방법으로 게임을 실행할 수 있도록 아래의 mode 값을 바꾸세요.
		 */
		
		int mode = 6;

		
		/*
		 * ----------------------------------------------------------------------------------------------------------------
		 * 
		 * TODO '승부 조작'을 위해 게임 번호를 고정시키고 고득점을 노리려면 아래의 코드에서 직접 게임 번호(0 또는 양수)를 지정하세요.
		 * 
		 * ----------------------------------------------------------------------------------------------------------------
		 */
		
		settings.game_number = -1;
		
		
		/*
		 * 직접 만든 플레이어를 등록하는 부분입니다.
		 * 
		 * TODO 자신이 만든 플레이어 클래스를 아래의 주석 내용을 참고하여 등록하세요.
		 * 		 (주석 내용을 복붙한 다음 Player_YOURNAMEHERE 대신 여러분의 플레이어 클래스 이름을 넣으면 됩니다)
		 * 
		 * 주의: 같은 클래스를 여러 번 등록하는 것도 가능합니다.
		 * 		 하지만 이렇게 하는 경우 여러 플레이어가 항상 같은 의사 결정을 하게 될 수 있으니
		 * 		 가급적 한 클래스는 한 번만 등록하는 것이 좋겠습니다.
		 * 		 
		 */
		
		//settings.custom_player_classes.add(Player_YOURNAMEHERE.class);
		
		settings.custom_player_classes.add(Player_TA.class);
		settings.custom_player_classes.add(Hungry.class);
		settings.custom_player_classes.add(Player_Rune.class);
		settings.custom_player_classes.add(Player_최태혁.class);
		settings.custom_player_classes.add(KBJ.class);

		settings.custom_player_classes.add(slave.class);
		settings.custom_player_classes.add(Player_King.class);
		settings.custom_player_classes.add(Player_JangHo.class);
		settings.custom_player_classes.add(Player_JiWon.class);
		settings.custom_player_classes.add(Player_잡캐.class);
		
		settings.custom_player_classes.add(Player_2008027687.class);
		settings.custom_player_classes.add(Player_JY.class);
		settings.custom_player_classes.add(Player_Druwa.class);
		settings.custom_player_classes.add(Player_JeongYunhee.class);
		settings.custom_player_classes.add(Player_lsh.class);
		
		settings.custom_player_classes.add(SIRA.class);
		settings.custom_player_classes.add(Player_DIA1TEAR.class);
		settings.custom_player_classes.add(Player_YOURNAMEHERE.class);
		settings.custom_player_classes.add(Player_seohui.class);
		settings.custom_player_classes.add(Player_ZombieStalker.class);
		
		settings.custom_player_classes.add(Player_HONG.class);
		settings.custom_player_classes.add(Player_joonwook.class);
		settings.custom_player_classes.add(Player_양대곤.class);
		settings.custom_player_classes.add(과대놈.class);
		settings.custom_player_classes.add(Player_Yoann.class);
		
		settings.custom_player_classes.add(Incorruptible_Corpse.class);
		settings.custom_player_classes.add(Versatile75.class);
		settings.custom_player_classes.add(적당히오래살자.class);
		settings.custom_player_classes.add(Player_GGYUBBYU.class);
		settings.custom_player_classes.add(Player_Bonus.class);
		
		settings.custom_player_classes.add(Player_one.class);
		settings.custom_player_classes.add(Player_SalesMan.class);
		settings.custom_player_classes.add(Player_JyounG.class);
		settings.custom_player_classes.add(Jitwo.class);
		settings.custom_player_classes.add(Hyerim.class);
		
		settings.custom_player_classes.add(Player_DKpal.class);
		settings.custom_player_classes.add(Player_2012004678_YoungJu_YOON.class);
		settings.custom_player_classes.add(민지.class);
		
		
		
		/*
		 * 게임에 참여할 샘플 플레이어들의 분포와 순서를 정할 때 사용하는
		 * seed 문자열을 설정하는 부분입니다.
		 * 
		 * 기본값은 "14OOP"이며
		 * 이를 다른 문자열로 바꾸는 경우 게임에 같이 참여할 샘플 플레이어 목록도 바뀌게 됩니다.
		 * 
		 * TODO 자신의 플레이어를 여러 가지 플레이어 조합 안에서 테스트해 보려는 경우
		 * 		 "14OOP" 대신 다른 문자열을 설정해 보세요.
		 * 
		 * 주의: 만약 이 문자열을 '빈 문자열' 또는 null로 설정하는 경우
		 * 		 강의실을 초기화할 때 다시 기본값으로 복구됩니다.
		 * 
		 */
		settings.seed_for_sample_players = "14OOP";
		
		
		
		
		/* ***************************************************************
		 * 
		 * 
		 * 이제 이 아래부터는 크게 신경쓰지 않아도 됩니다.
		 * 
		 * 
		 */
		
		if ( mode == 0 )
		{
			Classroom classroom = new Classroom(settings);
			classroom.run();
		}
		
		if ( mode == 1 )
		{
			settings.print_actions = false;
			settings.print_first_player_only = false;
			settings.print_playerInfos = false;
			settings.print_reactions = false;
			settings.print_scores_during_game = false;

			Classroom classroom = new Classroom(settings);
			classroom.run();
		}
		
		if ( mode == 2 )
		{
			GameNumbers numbers = new GameNumbers(10000);
			numbers.Create(-1);
			
			Grader grader = new Grader();
			
			settings.use_console_mode = false;

			Classroom classroom = null;
			
			for ( int iGame = 0; iGame < 10000; ++iGame )
			{
				settings.game_number = numbers.data[iGame];
				classroom = new Classroom(settings);
				classroom.run();
				grader.Update(settings.game_number, classroom.scoreboard);
			}
			
			grader.PrintResults(classroom);
			grader.PrintResultsOf(0, classroom);
		}
		
		if ( mode == 3 )
		{
			GameNumbers numbers = new GameNumbers(10000);
			numbers.Load("10000numbers.txt");
			
			Grader grader = new Grader();
			
			settings.use_console_mode = false;

			Classroom classroom = null;
			
			for ( int iGame = 0; iGame < 10000; ++iGame )
			{
				settings.game_number = numbers.data[iGame];
				classroom = new Classroom(settings);
				classroom.run();
				grader.Update(settings.game_number, classroom.scoreboard);
			}
			
			grader.PrintResults(classroom);
		}
		
		if ( mode == 4 )
		{
			settings.use_console_mode = false;
			
			Presenter_Mode4 presenter = new Presenter_Mode4(settings);
			presenter.setVisible(true);
		}
		
		if ( mode == 5 )
		{
			settings.use_console_mode = false;
			settings.print_errors = false;
			Presenter_Mode5 presenter = new Presenter_Mode5(settings);
			presenter.setVisible(true);
		}
		
		if ( mode == 6 )
		{
			settings.use_console_mode = false;
			settings.use_ctrlable_player = true;
			
			settings.custom_player_classes.remove(0);
			
			Presenter_Mode6 presenter = new Presenter_Mode6(settings);
			presenter.setVisible(true);
		}		
	}
}
