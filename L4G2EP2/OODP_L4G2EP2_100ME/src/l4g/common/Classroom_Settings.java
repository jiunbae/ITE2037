package l4g.common;

import java.util.ArrayList;
import java.util.function.Supplier;

import l4g.common.Player;

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
	public long game_number;

	/**
	 * 직접 키보드 또는 마우스로 조작할 수 있는 플레이어를 사용하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean use_ctrlable_player;

	/**
	 * 직접 작성하여 참여시킬 플레이어 클래스의 목록입니다.
	 * settings.custom_player_classes.add(Player_YOURNAMEHERE.class);
	 * ...와 같이 여러분이 만든 클래스 이름에 .class를 붙여 이 목록에 담으면 강의실에 여러분의 플레이어가 추가됩니다.
	 * 강의실 정원 대비 빈 자리는 샘플 플레이어로 채워집니다.
	 */
	public ArrayList<Class<? extends Player>> custom_player_classes;

	/**
	 * 무작위 의사 결정을 수행하는 무법자 Bot 플레이어를 최대 몇 명까지 추가할 것인지를 설정합니다.
	 * 무법자긴 하지만 같은 게임 번호, 같은 ID를 사용하면 항상 동일한 의사 결정을 수행합니다.
	 * 기본값은 10입니다.<br>
	 * <br>
	 * <b>주의:</b> 무법자 Bot 플레이어는 정규 게임에서는 사용되지 않습니다.
	 * 또한, 여러분이 작성하는 플레이어는 어떤 이유로든 <code>java.util.Random class</code>를 사용할 수 없습니다.
	 * 무법자 Bot 플레이어는 단순히 테스트를 위해 '여러분의 플레이어가 고득점을 올리는 상황'을 좀 더 쉽게 연출해 보는 용도로 사용해야 합니다. 
	 */
	public int max_numberOfHornDonePlayer;
	
	/**
	 * NPC 플레이어(학점 산정에 영향을 주지 않는 플레이어)가 몇 명 있는지를 나타냅니다.
	 * NPC 플레이어의 점수는 각 부문별 최고/최저 점수를 계산할 때 제외됩니다.
	 * (NPC 플레이어가 1등을 해도 여러분의 학점이 떨어지지는 않습니다)
	 * NPC 플레이어는 반드시 다른 플레이어들보다 먼저 등록해 놓아야 합니다.
	 * 
	 * 기본값은 0입니다.
	 */
	public int numberOfNPCs;
	
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
	 * 플레이어의 현재 상태를 표시하려는 경우 true로 설정합니다.
	 * print_first_player_only가 true일 때는 행동 전 자신이 알고 있는 플레이어들의 상태 정보 + 턴 종료 시점에서의 자신의 상태 정보를 표시하며,
	 * false일 때는 턴 종료 시점에서의 모든 플레이어의 상태 정보를 표시합니다.
	 * 기본값은 true입니다.
	 */
	public boolean print_playerInfos;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 수행한 의사 결정들의 내용을 표시하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean print_decisions;
	
	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 수행한 행동들을 표시하려는 경우 true로 설정합니다.
	 * print_first_player_only가 true일 때는 자신의 시야 범위 내에서 전 턴에 수행된 행동 정보(의사 결정의 근거가 되는)
	 * + 자신이 이번 턴에 수행한 행동 정보를 표시하며,
	 * false일 때는 모든 행동 정보 이동 목록 / 배치 목록으로 나누어 표시합니다. 
	 * 기본값은 true입니다.
	 */
	public boolean print_actions;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들이 발생시킨 사건(발견 사건 제외)들을 표시하려는 경우 true로 설정합니다.
	 * print_first_player_only가 true일 때는 자신의 시야 범위 내에서 전 턴에 일어난 사건 정보(의사 결정의 근거가 되는)
	 * + 자신이 이번 턴에 일으킨 사건 정보를 표시하며,
	 * false일 때는 모든 사건 정보를 표시합니다. 
	 * 기본값은 true입니다.
	 */
	public boolean print_reactions;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 매 턴마다 플레이어들의 현재 점수를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 false입니다.
	 */
	public boolean print_scores_at_each_turns;

	/**
	 * 콘솔 창에 게임 경과를 표시할 때
	 * 게임 종료 후 플레이어들의 점수를 표시하려는 경우 true로 설정합니다.
	 * 기본값은 true입니다.
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
	 * 기본값은 "16OODP" 입니다.
	 */
	public String seed_for_sample_players;

	/**
	 * 초기화 작업이 끝났을 때 호출되는 콜백 메서드입니다.
	 * 이 필드에 여러분의 메서드(또는 람다 식)를 넣으면 초기화 작업이 끝난 직후에 해당 메서드를 호출해 줍니다.
	 * 뭔 소린지 모르겠으면 그냥 null로 두어도 무방합니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_StandBy;

	/**
	 * 게임이 시작될 때 호출되는 콜백 메서드입니다.
	 * 이 필드에 여러분의 메서드(또는 람다 식)를 넣으면 게임이 시작되기 직전에 해당 메서드를 호출해 줍니다.
	 * 뭔 소린지 모르겠으면 그냥 null로 두어도 무방합니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_StartGame;

	/**
	 * 턴이 시작될 때 호출되는 콜백 메서드입니다.
	 * 이 필드에 여러분의 메서드(또는 람다 식)를 넣으면 새 턴이 시작되기 직전에 해당 메서드를 호출해 줍니다.
	 * 뭔 소린지 모르겠으면 그냥 null로 두어도 무방합니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_StartTurn;

	/**
	 * 직접 조작 가능한 플레이어를 사용하는 경우 사용자의 차례가 되었을 때 호출되는 콜백 메서드입니다.
	 * 이 필드는 강의실과 Presenter를 연결하기 위해 사용되며 여러분이 손 댈 일은 거의 없습니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_RequestDecision;

	/**
	 * 직접 조작 가능한 플레이어를 사용하는 경우 사용자의 선택이 유효하지 않을 때 호출되는 콜백 메서드입니다.
	 * 이 필드는 강의실과 Presenter를 연결하기 위해 사용되며 여러분이 손 댈 일은 거의 없습니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_InvalidDecision;

	/**
	 * 턴이 끝났을 때 호출되는 콜백 메서드입니다.
	 * 이 필드에 여러분의 메서드(또는 람다 식)를 넣으면 턴이 끝난 직후에 해당 메서드를 호출해 줍니다.
	 * 뭔 소린지 모르겠으면 그냥 null로 두어도 무방합니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_EndTurn;

	/**
	 * 게임이 끝났을 때 호출되는 콜백 메서드입니다.
	 * 이 필드에 여러분의 메서드(또는 람다 식)를 넣으면 게임이 끝난 직후에 해당 메서드를 호출해 줍니다.
	 * 뭔 소린지 모르겠으면 그냥 null로 두어도 무방합니다.
	 * 기본값은 null입니다.
	 */
	public Supplier<Boolean> callback_EndGame;

	/**
	 * 새로운 <code>Classroom_Settings class</code>의 인스턴스를 생성하고
	 * 모든 설정들을 기본값으로 초기화합니다.
	 */
	public Classroom_Settings()
	{
		game_number = -1L;
		use_ctrlable_player = false;
		custom_player_classes = new ArrayList<Class<? extends Player>>();
		max_numberOfHornDonePlayer = 10;
		numberOfNPCs = 0;
		use_console_mode = true;
		print_first_player_only = true;
		print_playerInfos = true;
		print_decisions = false;
		print_actions = true;
		print_reactions = true;
		print_scores_at_each_turns = false;
		print_scores_at_the_end = true;
		print_errors = true;
		seed_for_sample_players = "16OODP";
		callback_StandBy = null;
		callback_StartGame = null;
		callback_StartTurn = null;
		callback_RequestDecision = null;
		callback_InvalidDecision = null;
		callback_EndTurn = null;
		callback_EndGame = null;
	}
	
	/**
	 * 주어진 설정 인스턴스와 동일한 새로운 <code>Classroom_Settings class</code>의 인스턴스를 생성합니다.
	 */
	public Classroom_Settings(Classroom_Settings other)
	{
		game_number = other.game_number;
		use_ctrlable_player = other.use_ctrlable_player;
		custom_player_classes = new ArrayList<Class<? extends Player>>(other.custom_player_classes);
		max_numberOfHornDonePlayer = other.max_numberOfHornDonePlayer;
		numberOfNPCs = other.numberOfNPCs;
		use_console_mode = other.use_console_mode;
		print_first_player_only = other.print_first_player_only;
		print_playerInfos = other.print_playerInfos;
		print_decisions = other.print_decisions;
		print_actions = other.print_actions;
		print_reactions = other.print_reactions;
		print_scores_at_each_turns = other.print_scores_at_each_turns;
		print_scores_at_the_end = other.print_scores_at_the_end;
		print_errors = other.print_errors;
		seed_for_sample_players = other.seed_for_sample_players;
		callback_StandBy = other.callback_StandBy;
		callback_StartGame = other.callback_StartGame;
		callback_StartTurn = other.callback_StartTurn;
		callback_RequestDecision = other.callback_RequestDecision;
		callback_InvalidDecision = other.callback_InvalidDecision;
		callback_EndTurn = other.callback_EndTurn;
		callback_EndGame = other.callback_EndGame;
	}
}
