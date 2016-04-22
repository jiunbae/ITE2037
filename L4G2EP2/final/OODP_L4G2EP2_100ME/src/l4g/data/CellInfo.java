package l4g.data;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 강의실 내의 칸 하나와 연관된 정보들을 담기 위한 클래스입니다.
 * 
 * 이 클래스의 모든 비정적 필드는 읽기 전용인데다 숨겨져 있으며
 * 여러분은 미리 준비된 '순회 메서드'를 사용하여 각 정보들을 탐색할 수 있습니다.
 * 
 * @author Racin
 *
 */
public class CellInfo
{
	/**
	 * '비어 있는 칸'을 나타내는 정적 인스턴스입니다.
	 * 비어 있으므로 이 칸에는 아무도 없고, 따라서 이 칸에서는 어떤 행동/사건도 발생하지 않습니다.
	 * 그럼에도 불구하고 '비어 있는 칸'도 어쨋든 '유효한 칸'이므로,
	 * 여러 가지 순회 메서드들을 사용해 뭔가를 시도해도 오류가 나진 않습니다.
	 * 그러니, 여러분이 이 필드를 직접적으로 사용하여 if 검사를 할 일은 거의 없을 것입니다.
	 */
	public static final CellInfo Blank;

	/*
	 * Note: 클래스 내에서 static 하고 바로 중괄호 나오는 부분은
	 * 이 클래스의 static 필드를 초기화하기 위한 '정적 생성자'같은 부분입니다.
	 * static 필드는 C의 경우와 유사하게 '프로그램이 시작될 때' 만들어지므로
	 * 이 중괄호의 내용 또한 우리가 Ctrl + F11 누를 때 바로 실행됩니다.
	 * 결론적으로, 이 부분은 신경쓰지 않아도 됩니다.
	 */
	static
	{
		Blank = new CellInfo(new ArrayList<PlayerInfo>(), new ArrayList<Action>(), new ArrayList<Reaction>());
	}

	/* 
	 * 아래의 세 목록 필드는 private이므로 여러분이 볼 수 없습니다.
	 * 대신, 여러분은 이 파일 하단에서 정의하고 있는 갖가지 순회 메서드를 통해
	 * 각 목록의 내용을 액세스하며 적절한 데이터를 뽑아 낼 수 있습니다.
	 */
	private final ArrayList<PlayerInfo> players;
	private final ArrayList<Action> actions;
	private final ArrayList<Reaction> reactions;
	
	/**
	 * 주의: 이 클래스의 인스턴스는 여러분이 굳이 새로 만들 필요가 없습니다.
	 * '특정 칸에 있는 플레이어 목록'이 갖고 싶은 경우 Select()를 쓰세요.
	 * 하지만 대부분의 경우 그냥 CountIf()를 적절히 호출하는 것 만으로도 원하는 결과를 얻을 수 있을 것입니다.
	 */
	public CellInfo(ArrayList<PlayerInfo> players, ArrayList<Action> actions, ArrayList<Reaction> reactions)
	{
		this.players = players;
		this.actions = actions;
		this.reactions = reactions;
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
	}
}
