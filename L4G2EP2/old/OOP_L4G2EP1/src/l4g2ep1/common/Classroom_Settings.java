package l4g2ep1.common;

import java.util.ArrayList;

import l4g2ep1.Player;

/**
 * 강의실을 생성할 때 사용하는 여러 설정값들을 담고 있는 클래스입니다.
 * 
 * @author Racin
 * 
 */
public class Classroom_Settings
{
	/**
	 * 진행할 게임 번호입니다. 같은 번호의 게임은 언제 실행해도 같은 결과를 내게 되어 있습니다.
	 * 이 값을 -1로 두는 경우 랜덤 선택된 게임 번호를 사용합니다.
	 * 기본값은 -1입니다.
	 */
	public int game_number;

	/**
	 * 직접 키보드 또는 마우스로 조작할 수 있는 플레이어를 사용하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean use_ctrlable_player;

	/**
	 * 직접 작성하여 참여시킬 플레이어 클래스의 목록입니다.
	 * settings.custom_player_classes.add(Player_YOURNAMEHERE.class);
	 * ...와 같이 여러분이 만든 클래스 이름에 .class를 붙여 이 목록에 담으면 강의실에 여러분의 플레이어가 추가됩니다.
	 * 강의실 정원을 채우기 위해 빈 자리는 샘플 플레이어로 채워집니다.
	 */
	public ArrayList<Class<? extends Player>> custom_player_classes;

	/**
	 * 콘솔 창에 게임 경과를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean use_console_mode;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 0번 플레이어(직접 조종하는 플레이어 또는 직접 작성하여 참여시킨 첫 플레이어)에 대한 정보만 표시하려는 경우 true로 설정합니다.
	 * 모든 플레이어에 대한 정보를 표시하려는 경우 false로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_first_player_only;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들의 현재 상태를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_playerInfos;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 제기한 의사 결정들을 표시하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean print_decisions;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 수행한 행동들을 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_actions;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 발생시킨 사건들을 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_reactions;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들의 현재 점수를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_scores_during_game;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 게임 종료 후 플레이어들의 점수를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean print_scores_at_the_end;
	
	/**
	 * 런타임 예외의 내용을 콘솔 창에 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_errors;
	
	/**
	 * 샘플 플레이어의 분포와 순서를 설정할 때 사용하는 문자열입니다.
	 * 이 문자열을 '빈 문자열' 또는 null로 설정하는 경우 강의실 초기화 과정에서 다시 기본값으로 복구됩니다.
	 * 기본값은 "14OOP" 입니다.
	 */
	public String seed_for_sample_players;

	/**
	 * 새로운 Classroom_Settings class의 인스턴스를 생성하고
	 * 모든 설정들을 기본값으로 초기화합니다.
	 */
	public Classroom_Settings()
	{
		game_number = -1;
		use_ctrlable_player = false;
		custom_player_classes = new ArrayList<Class<? extends Player>>();
		use_console_mode = true;
		print_first_player_only = true;
		print_playerInfos = true;
		print_decisions = false;
		print_actions = true;
		print_reactions = true;
		print_scores_during_game = true;
		print_scores_at_the_end = false;
		print_errors = true;
		seed_for_sample_players = "14OOP";
	}
	
	public Classroom_Settings(Classroom_Settings other)
	{
		game_number = other.game_number;
		use_ctrlable_player = other.use_ctrlable_player;
		custom_player_classes = new ArrayList<Class<? extends Player>>(other.custom_player_classes);
		use_console_mode = other.use_console_mode;
		print_first_player_only = other.print_first_player_only;
		print_playerInfos = other.print_playerInfos;
		print_decisions = other.print_decisions;
		print_actions = other.print_actions;
		print_reactions = other.print_reactions;
		print_scores_during_game = other.print_scores_during_game;
		print_scores_at_the_end = other.print_scores_at_the_end;
		print_errors = other.print_errors;
		seed_for_sample_players = other.seed_for_sample_players;
	}
}
