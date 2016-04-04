package l4g2ep1;

import java.util.ArrayList;

/**
 * 강의실 내의 칸 하나와 연관된 정보를 담기 위한 클래스입니다.
 * 
 * @author Racin
 *
 */
public class CellInfo
{
	/**
	 * '비어 있는 칸'을 나타내는 정적 인스턴스입니다.
	 */
	public static CellInfo Blank = new CellInfo();

	/*
	 * 여기 있는 목록들은 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 목록 안의 요소들을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 목록 자체 및 내용물을 수정할 수는 없습니다 )
	 */
	ArrayList<PlayerInfo> playersInTheCell;
	ArrayList<Action> actionsInTheCell;
	ArrayList<Reaction> reactionsInTheCell;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 여러분의 시야 범위 내에서 수행된 행동들은 모두 자동으로 목록에 기록되어 전달됩니다.
	 */
	CellInfo()
	{
		playersInTheCell = new ArrayList<PlayerInfo>();
		actionsInTheCell = new ArrayList<Action>();
		reactionsInTheCell = new ArrayList<Reaction>();
	}

	/**
	 * 현재 이 칸에 있는 플레이어의 수를 반환합니다.
	 */
	public int GetNumberOfPlayersInTheCell() { return playersInTheCell.size(); }

	/**
	 * 지난 턴에 이 칸에서 수행된 행동의 갯수를 반환합니다.
	 */
	public int GetNumberOfActionsInTheCell() { return actionsInTheCell.size(); }
 
	/**
	 * 지난 턴에 이 칸에서 발생한 사건의 갯수를 반환합니다.
	 */
	public int GetNumberOfReactionsInTheCell() { return reactionsInTheCell.size(); }

	/**
	 * 현재 이 칸에 있는 해당 번째 플레이어 정보를 목록에서 가져옵니다.
	 * 플레이어 수는 GetNumberOfPlayersInTheCell()을 통해 알 수 있습니다.
	 */
	public PlayerInfo GetPlayerInfo(int index) { return playersInTheCell.get(index); }

	/**
	 * 지난 턴에 이 칸에서 수행된 해당 번째 행동 정보를 목록에서 가져옵니다.
	 * 행동의 갯수는 GetNumberOfActionsInTheCell()을 통해 알 수 있습니다.
	 */
	public Action GetAction(int index) { return actionsInTheCell.get(index); }

	/**
	 * 지난 턴에 이 칸에서 발생한 해당 번째 사건 정보를 목록에서 가져옵니다.
	 * 사건의 갯수는 GetNumberOfReactionsInTheCell()을 통해 알 수 있습니다.
	 */
	public Reaction GetReaction(int index) { return reactionsInTheCell.get(index); }
}
