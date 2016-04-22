package l4g;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import l4g.data.Action;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;
import l4g.data.Reaction;

/**
 * 강의실 내의 한 칸을 나타내는 클래스입니다.
 * 여러분은 이 클래스의 사본인 <code>CellInfo</code> 인스턴스를 사용하게 됩니다.
 * 
 * 이 클래스는 여러분 코드에서 생성할 수 없으며
 * 이미 만들어진 인스턴스들에 액세스할 수도 없습니다.
 * 
 * @author Racin
 *
 */
public class Cell
{
	// 이 세 목록은 매 턴마다 새로 생성, 각 플레이어에게 전달되는 CellInfo는 이 목록 자체를 private 필드로 가져감
	public ArrayList<PlayerInfo> players;
	public ArrayList<Action> actions;
	public ArrayList<Reaction> reactions;
	
	//이 목록은 CellInfo에 선택적으로 포함되기 위해 players와 함께 다루어짐
	public ArrayList<PlayerInfo> corpses;

	private final ArrayList<Action> empty_actions;
	private final ArrayList<Reaction> empty_reactions;
	
	Cell()
	{
		players = new ArrayList<PlayerInfo>();
		actions = new ArrayList<Action>();
		reactions = new ArrayList<Reaction>();
		
		corpses = new ArrayList<PlayerInfo>();
		
		empty_actions = new ArrayList<Action>();
		empty_reactions = new ArrayList<Reaction>();
	}

	/**
	 * 이 칸에 대한 사본 정보를 생성합니다.
	 * 
	 * @param mode
	 *            0인 경우 모든 정보를 담습니다.
	 *            1인 경우 플레이어 정보만을 담습니다.
	 *            그 외의 경우 시체 정보만을 담습니다.
	 */
	public CellInfo MakeCellInfo(int mode)
	{
		switch ( mode )
		{
		case 0:
			if ( players.isEmpty() == true && actions.isEmpty() == true && reactions.isEmpty() == true )
				return CellInfo.Blank;
			return new CellInfo(players, actions, reactions);
		case 1:
			if ( players.isEmpty() == true )
				return CellInfo.Blank;
			return new CellInfo(players, empty_actions, empty_reactions);
		default:
			if ( corpses.isEmpty() == true )
				return CellInfo.Blank;
			return new CellInfo(corpses, empty_actions, empty_reactions);
		}
	}
	
	public void ResetPreviousLists()
	{
		actions = new ArrayList<Action>();
		reactions = new ArrayList<Reaction>();
	}

	/* -------------------------------------------------------
	 * '순회 메서드' 정의 부분
	 */

	/**
	 * 이 칸에 있는 총 플레이어 수를 반환합니다.
	 */
	public int Count_Players()
	{
		return players.size();
	}

	/**
	 * 이 칸에 있는 플레이어들 중 주어진 조건에 맞는 플레이어 수를 반환합니다.
	 * 
	 * @param condition
	 *            플레이어 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public int CountIf_Players(Predicate<PlayerInfo> condition)
	{
		int count = 0;

		for ( PlayerInfo player : players )
			if ( condition.test(player) == true )
				++count;

		return count;
	}

	/**
	 * 이 칸에 있는 플레이어들 각각에 대해 주어진 메서드를 실행합니다.
	 * 이걸 도대체 어디다 쓰라고 만든 것인지 궁금하면 Developer's Guide 문서를 참조하세요.
	 * 
	 * @param action
	 *            플레이어 정보를 인수로 받고 특정 작업을 수행하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public void ForEach_Players(Consumer<PlayerInfo> action)
	{
		players.forEach(action);
	}

	/**
	 * 이 칸에 있는 플레이어들 중 주어진 조건에 맞는 플레이어들만 모아 목록으로 만들어 반환합니다.
	 * 
	 * @param condition
	 *            플레이어 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public ArrayList<PlayerInfo> Select_Players(Predicate<PlayerInfo> condition)
	{
		ArrayList<PlayerInfo> result = new ArrayList<PlayerInfo>();

		for ( PlayerInfo player : players )
			if ( condition.test(player) == true )
				result.add(player);

		return result;
	}

	/**
	 * 이 칸에서 수행된 총 행동 수를 반환합니다.
	 */
	public int Count_Actions()
	{
		return actions.size();
	}

	/**
	 * 이 칸에서 수행된 행동들 중 주어진 조건에 맞는 행동 수를 반환합니다.
	 * 
	 * @param condition
	 *            행동 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public int CountIf_Actions(Predicate<Action> condition)
	{
		int count = 0;

		for ( Action action : actions )
			if ( condition.test(action) == true )
				++count;

		return count;
	}

	/**
	 * 이 칸에서 수행된 행동들 각각에 대해 주어진 메서드를 실행합니다.
	 * 이걸 도대체 어디다 쓰라고 만든 것인지 궁금하면 Developer's Guide 문서를 참조하세요.
	 * 
	 * @param action
	 *            행동 정보를 인수로 받고 특정 작업을 수행하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public void ForEach_Actions(Consumer<Action> action)
	{
		actions.forEach(action);
	}

	/**
	 * 이 칸에서 수행된 행동들 중 주어진 조건에 맞는 행동들만 모아 목록으로 만들어 반환합니다.
	 * 
	 * @param condition
	 *            행동 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public ArrayList<Action> Select_Actions(Predicate<Action> condition)
	{
		ArrayList<Action> result = new ArrayList<Action>();

		for ( Action action : actions )
			if ( condition.test(action) == true )
				result.add(action);

		return result;
	}


	/**
	 * 이 칸에서 발생한 총 사건 수를 반환합니다.
	 */
	public int Count_Reactions()
	{
		return reactions.size();
	}

	/**
	 * 이 칸에서 발생한 사건들 중 주어진 조건에 맞는 사건 수를 반환합니다.
	 * 
	 * @param condition
	 *            사건 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public int CountIf_Reactions(Predicate<Reaction> condition)
	{
		int count = 0;

		for ( Reaction reaction : reactions )
			if ( condition.test(reaction) == true )
				++count;

		return count;
	}
	
	/**
	 * 이 칸에서 발생한 사건들 각각에 대해 주어진 메서드를 실행합니다.
	 * 이걸 도대체 어디다 쓰라고 만든 것인지 궁금하면 Developer's Guide 문서를 참조하세요.
	 * 
	 * @param action
	 *            사건 정보를 인수로 받고 특정 작업을 수행하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public void ForEach_Reactions(Consumer<Reaction> action)
	{
		reactions.forEach(action);
	}

	/**
	 * 이 칸에서 발생한 사건들 중 주어진 조건에 맞는 사건들만 모아 목록으로 만들어 반환합니다.
	 * 
	 * @param condition
	 *            사건 정보를 인수로 받아 검사 후 true/false를 반환하는 메서드 또는 람다 식을 넣으세요.
	 *            람다 식의 사용 방법이 궁금하면 Developer's Guide 문서를 참조하세요.
	 */
	public ArrayList<Reaction> Select_Reactions(Predicate<Reaction> condition)
	{
		ArrayList<Reaction> result = new ArrayList<Reaction>();

		for ( Reaction reaction : reactions )
			if ( condition.test(reaction) == true )
				result.add(reaction);

		return result;
	}}
