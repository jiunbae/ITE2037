package l4g2ep1;

import l4g2ep1.PlayerInfo.State;
import l4g2ep1.common.Constants;
import l4g2ep1.common.DirectionCode;
import l4g2ep1.common.Point;
import l4g2ep1.common.Vector;

import java.util.ArrayList;

/**
 * 강의실 내의 플레이어 한 명을 나타내는 추상 클래스입니다.
 * 
 * @author Racin
 * 
 */
public abstract class Player
{
	/**
	 * 플레이어의 이름입니다.
	 */
	public String name;

	/**
	 * '직접 감염'을 받을 것인지 여부를 설정하는 필드입니다.
	 * 게임 도중 직접 감염 여부를 선택하는 턴에 이 필드의 값을 true로 설정해 두면 직접 감염을 수락하는 것으로 간주됩니다.
	 * 이번 턴에 직접 감염 여부를 선택해야 하는지 확인하려면 gameInfo.GetIsDirectInfectionChoosingTurn()의 반환값을 참고하세요. 
	 */
	public boolean acceptDirectInfection;

	/**
	 * 플레이어 자신이 이번 게임에서 현재 기록중인 점수입니다.
	 */
	public Score myScore;

	/**
	 * 이번 게임과 관련한 정보가 들어 있습니다.
	 */
	public GameInfo gameInfo;

	/**
	 * 플레이어 자신의 현재 상태를 담고 있습니다.
	 */
	public PlayerInfo myInfo;

	/**
	 * 매 턴마다 플레이어의 현재 상태에 따라 그 존재를 알게 된 다른 플레이어들의 상태를 받아 볼 것인지 여부를 나타냅니다.
	 * 
	 * 여기에는 생존자 상태일 때 다른 생존자의 포착으로 인해 알게 된 플레이어들,
	 * 감염체 상태일 때 감지한 시체들,
	 * 영혼 상태일 때 강의실 위에 떠 있는 다른 영혼들이 포함됩니다.
	 * 
	 * 각 플레이어 정보는 othersInfo_withinSight 목록과 여기에 중복 포함될 수 있습니다.
	 * (예를 들면, 감염체 상태일 때 시야 범위 내에 있는 시체는 두 목록에 모두 포함됩니다)
	 * 
	 * 이 값을 true로 설정하면 다음 턴부터 othersInfo_detected 목록에 해당 정보가 들어오게 됩니다.
	 */
	public boolean receiveOthersInfo_detected;

	/**
	 * 매 턴마다 시야 범위 안에 있는 자신 및 다른 플레이어들이 이전 턴에 수행한 행동을 받아 볼 것인지 여부를 나타냅니다.
	 * 이 값을 true로 설정하면 다음 턴부터 actions 목록에 해당 정보가 들어오게 됩니다.
	 */
	public boolean receiveActions;

	/**
	 * 매 턴마다 시야 범위 안에서 이전 턴에 발생한 사건을 받아 볼 것인지 여부를 나타냅니다.
	 * 이 값을 true로 설정하면 다음 턴부터 reactions 목록에 해당 정보가 들어오게 됩니다.
	 */
	public boolean receiveReactions;

	/**
	 * 플레이어의 현재 시야 범위 안에 있는 자신 및 다른 플레이어들의 상태에 대한 목록입니다.
	 * 플레이어의 시야 범위는 현재 상태가 무엇인지에 따라 다릅니다.
	 */
	public ArrayList<PlayerInfo> othersInfo_withinSight;

	/**
	 * 플레이어의 현재 상태에 따라 그 존재를 알게 된 다른 플레이어들의 상태에 대한 목록입니다.
	 * 
	 * 여기에는 생존자 상태일 때 나 자신 및 다른 생존자의 포착으로 인해 알게 된 플레이어들,
	 * 감염체 상태일 때 감지한 시체들,
	 * 영혼 상태일 때 강의실 위에 떠 있는 다른 영혼들이 포함됩니다.
	 * 
	 * 각 플레이어 정보는 othersInfo_withinSight 목록과 여기에 중복 포함될 수 있습니다.
	 * (예를 들면, 감염체 상태일 때 시야 범위 내에 있는 시체는 두 목록에 모두 포함됩니다)
	 * 
	 * 이 목록을 사용하려면 미리 receiveOthersInfo_detected 값을 true로 설정해 두어야 합니다.
	 */
	public ArrayList<PlayerInfo> othersInfo_detected;

	/**
	 * 플레이어의 현재 시야 범위 안에 있는 자신 및 다른 플레이어들이 이전 턴에 수행한 행동 목록입니다.
	 * 이 목록을 사용하려면 미리 receiveActions 값을 true로 설정해 두어야 합니다.
	 * 플레이어의 시야 범위는 현재 상태가 무엇인지에 따라 다릅니다.
	 */
	public ArrayList<Action> actions;

	/**
	 * 플레이어의 현재 시야 범위 안에서 이전 턴에 발생한 사건 목록입니다.
	 * 이 목록을 사용하려면 미리 receiveReactions 값을 true로 설정해 두어야 합니다.
	 * 플레이어의 시야 범위는 현재 상태가 무엇인지에 따라 다릅니다.
	 */
	public ArrayList<Reaction> reactions;

	/*
	 * 현재 강의실 내의 각 칸에 대한 정보를 담고 있습니다.
	 * 여러분의 플레이어는 이 필드를 직접 사용할 수 없으며
	 * 대신 아래에 정의된 getter 메서드들을 통해 원하는 (그리고 가능한) 정보를 선택적으로 받아 볼 수 있습니다.
	 */
	Cells cells;

	public Player()
	{
		othersInfo_withinSight = new ArrayList<PlayerInfo>();
		othersInfo_detected = new ArrayList<PlayerInfo>();
		actions = new ArrayList<Action>();
		reactions = new ArrayList<Reaction>();
	}

	/**
	 * 생존자 상태일 때 이번 턴에 이동할 방향을 반환합니다.
	 * 주의: 생존자가 DirectionCode.Stay를 반환하는 경우 부정 이동에 해당하므로 패널티를 받게 됩니다.
	 */
	public abstract DirectionCode Survivor_Move();

	/**
	 * 시체 상태일 때 이번 턴에 가만히 누워 있습니다.
	 * 시체 상태에서도 시야 정보를 받을 수 있으므로
	 * 원하는 경우 좀 더 세밀한 플레이를 위해 현재 정보를 토대로 생각을 진행할 수 있습니다.
	 */
	public abstract void Corpse_Stay();

	/**
	 * 감염체 상태일 때 이번 턴에 이동할 방향을 반환합니다.
	 * 만약 제자리에 머무르고 싶은 경우 DirectionCode.Stay를 반환하면 됩니다.
	 */
	public abstract DirectionCode Infected_Move();

	/**
	 * 영혼 상태일 때 이번 턴에 조용히 재배치를 기다립니다.
	 * 영혼 상태에서도 시야 정보를 받을 수 있으므로
	 * 원하는 경우 좀 더 세밀한 플레이를 위해 현재 정보를 토대로 생각을 진행할 수 있습니다.
	 * 이 메서드는 게임이 시작될 때 가장 먼저 호출되므로
	 * 직접 만든 데이터 필드들에 대한 초기화 코드는 현재 0턴째인지 여부를 검사하는 if문과 함께 여기에 추가하는 것이 좋겠습니다.
	 */
	public abstract void Soul_Stay();

	/**
	 * 영혼 상태일 때 이번 턴에 재배치할 강의실 내의 좌표를 반환합니다.
	 */
	public abstract Point Soul_Spawn();
	
	/*
	 * -------------------------------------------------------------------------------------
	 * 
	 * 아래에 정의된 메서드들은 여러분의 플레이어 코드에서도 사용할 수 있으며
	 * 여러분이 작성중인 메서드 안에서 this.을 입력하여 목록을 확인할 수 있습니다.
	 * 
	 * 주의:
	 * 		Player class는 l4g2ep1 package에 있으며
	 * 		따라서 같은 패키지에 있는 PlayerInfo class, Cells class의 '여러분은 볼 수 없는' 필드들을 직접 보고 사용할 수 있습니다.
	 * 		이러한 필드들을 쓰면 getter 메서드들을 사용하는 것에 비해 더 효율적이므로(특히 좌표를 다룰 때)
	 * 	 	가급적 여기 정의되어 있는 여러 유틸리티 메서드들을 활용하여 코드를 작성하도록 합시다.
	 * 
	 * 		->	여러분이 만들 Player_YOURNAMEHERE class는 l4g2ep1.customplayers package에 있으므로
	 * 			이 아래에 적혀 있는 코드를 그대로 가져가 사용하면 컴파일 오류를 내게 됩니다.
	 * 			어떤 기능을 코드로 작성하려는데 어떻게 해야 할지 잘 모르겠다면 꼭 조교의 도움을 요청하세요.
	 * 
	 * -------------------------------------------------------------------------------------
	 */

	/**
	 * 주어진 위치에서 해당 방향으로 인접한 칸의 좌표를 반환합니다.
	 * 주의: 반환되는 좌표는 유효성 검사가 되어 있지 않으므로 사용하기 전에 꼭 먼저 검사를 수행해야 합니다.
	 * 
	 * @param origin
	 *            인접한 칸을 계산할 원점 좌표입니다.
	 * @param direction
	 *            좌표를 반환받을 방향입니다.
	 */
	public final Point GetAdjacentPoint(Point origin, DirectionCode direction)
	{
		return new Point(origin, direction);
	}

	/**
	 * 자신의 현재 위치에서 해당 방향으로 인접한 칸의 좌표를 반환합니다.
	 * 주의: 반환되는 좌표는 유효성 검사가 되어 있지 않으므로 사용하기 전에 꼭 먼저 검사를 수행해야 합니다.
	 * 
	 * @param direction
	 *            좌표를 반환받을 방향입니다.
	 */
	public final Point GetAdjacentPoint(DirectionCode direction)
	{
		return GetAdjacentPoint(myInfo.position, direction);
	}

	/**
	 * 이번 턴에 해당 칸으로 이동할 수 있는지 여부를 반환합니다.
	 * Survivor_Move()나 Infected_Move()에서 방향을 반환하기 전에 이 메서드를 호출하여 혹시 있을 수 있는 논리 오류에 대비하세요.
	 * 
	 * @param destination_x
	 *            이동하려는 칸의 x좌표입니다.
	 * @param destination_y
	 *            이동하려는 칸의 y좌표입니다.
	 * @return 이동할 수 있는 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean IsValidMove(int destination_x, int destination_y)
	{
		// 만약 내가 지금 생존자 또는 감염체라면
		if ( myInfo.state == State.Survivor || myInfo.state == State.Infected )
		{
			// 생존자가 제자리에 대기하는 것은 불가능
			if ( myInfo.state == State.Survivor && myInfo.position.equals(destination_x, destination_y) == true )
				return false;

			// 한 번에 한 칸 이상 이동 불가능
			int offset_x = myInfo.position.x - destination_x;
			int offset_y = myInfo.position.y - destination_y;

			if ( offset_x * offset_y != 0 ||
					offset_x > 1 || offset_x < -1 || offset_y > 1 || offset_y < -1 )
				return false;

			// 마지막으로, 해당 좌표가 유효한지 여부 검사
			return IsValidSpawn(destination_x, destination_y);
		}

		// 생존자나 감염체가 아니라면 이동 자체가 불가능
		return false;
	}

	/**
	 * 이번 턴에 해당 칸으로 이동할 수 있는지 여부를 반환합니다.
	 * Survivor_Move()나 Infected_Move()에서 방향을 반환하기 전에 이 메서드를 호출하여 혹시 있을 수 있는 논리 오류에 대비하세요.
	 * 
	 * @param destination
	 *            이동하려는 칸의 좌표입니다.
	 * @return 이동할 수 있는 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean IsValidMove(Point destination)
	{
		return IsValidMove(destination.x, destination.y);
	}

	/**
	 * 이번 턴에 해당 방향으로 이동할 수 있는지 여부를 반환합니다.
	 * Survivor_Move()나 Infected_Move()에서 방향을 반환하기 전에 이 메서드를 호출하여 혹시 있을 수 있는 논리 오류에 대비하세요.
	 * 
	 * @param direction
	 *            이동하려는 방향입니다.
	 * @return 이동할 수 있는 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean IsValidMove(DirectionCode direction)
	{
		// 만약 내가 지금 생존자 또는 감염체라면
		if ( myInfo.state == State.Survivor || myInfo.state == State.Infected )
		{
			// 방향이 없으면 '대기'로 간주
			if ( direction == null )
				direction = DirectionCode.Stay;

			// 생존자가 제자리에 대기하는 것은 불가능
			if ( myInfo.state == State.Survivor && direction == DirectionCode.Stay )
				return false;

			// 마지막으로, 해당 좌표가 유효한지 여부 검사
			return IsValidSpawn(GetAdjacentPoint(direction));
		}

		// 생존자나 감염체가 아니라면 이동 자체가 불가능
		else
			return false;
	}

	/**
	 * 이번 턴에 해당 좌표로 재배치하는 것이 가능한지 여부를 반환합니다.
	 * Soul_Spawn()에서 좌표를 반환하기 전에 이 메서드를 호출하여 혹시 있을 수 있는 논리 오류에 대비하세요.
	 * 
	 * @param destination_x
	 *            재배치하려는 x좌표입니다.
	 * @param destination_y
	 *            재배치하려는 y좌표입니다.
	 * @return 재배치가 가능한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean IsValidSpawn(int destination_x, int destination_y)
	{
		// 좌표가 강의실 영역을 벗어났는지 여부 확인
		return destination_x >= 0 && destination_x < Constants.Classroom_Width &&
				destination_y >= 0 && destination_y < Constants.Classroom_Height;
	}

	/**
	 * 이번 턴에 해당 좌표로 재배치하는 것이 가능한지 여부를 반환합니다.
	 * Soul_Spawn()에서 좌표를 반환하기 전에 이 메서드를 호출하여 혹시 있을 수 있는 논리 오류에 대비하세요.
	 * 
	 * @param destination
	 *            재배치하려는 좌표입니다.
	 * @return 재배치가 가능한 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean IsValidSpawn(Point destination)
	{
		return IsValidSpawn(destination.x, destination.y);
	}

	/**
	 * 두 좌표 사이의 거리(이동하는데 걸리는 칸 수)를 반환합니다.
	 * 
	 * @param from_x
	 *            거리를 측정할 기준 x좌표입니다.
	 * @param from_y
	 *            거리를 측정할 기준 y좌표입니다.
	 * @param to_x
	 *            거리를 측정할 대상 x좌표입니다.
	 * @param to_y
	 *            거리를 측정할 대상 y좌표입니다.
	 */
	public final int GetDistance(int from_x, int from_y, int to_x, int to_y)
	{
		int offset_x = from_x - to_x;
		int offset_y = from_y - to_y;

		if ( offset_x < 0 )
			offset_x = -offset_x;

		if ( offset_y < 0 )
			offset_y = -offset_y;

		return offset_x + offset_y;
	}

	/**
	 * 두 좌표 사이의 거리(이동하는데 걸리는 칸 수)를 반환합니다.
	 * 
	 * @param from
	 *            거리를 측정할 기준 좌표입니다.
	 * @param to_x
	 *            거리를 측정할 대상 x좌표입니다.
	 * @param to_y
	 *            거리를 측정할 대상 y좌표입니다.
	 */
	public final int GetDistance(Point from, int to_x, int to_y)
	{
		return GetDistance(from.x, from.y, to_x, to_y);
	}

	/**
	 * 두 좌표 사이의 거리(이동하는데 걸리는 칸 수)를 반환합니다.
	 * 
	 * @param from
	 *            거리를 측정할 기준 좌표입니다.
	 * @param to
	 *            거리를 측정할 대상 좌표입니다.
	 */
	public final int GetDistance(Point from, Point to)
	{
		return GetDistance(from.x, from.y, to.x, to.y);
	}
	
	/**
	 * 두 플레이어가 위치한 좌표를 잇는 벡터를 만들어 반환합니다.
	 * 반환된 벡터는 기준 플레이어와 대상 플레이어가 어떤 방향으로 얼마나 떨어져 있는지 확인하는 용도로 쓰일 수 있습니다.
	 * 보통 기준 플레이어는 자기 자신 ( myInfo )이 됩니다.
	 * 기준 플레이어와 대상 플레이어가 서로 바뀌는 경우 벡터의 방향도 반전됩니다.
	 * 
	 * @param origin
	 * 			위치를 비교할 기준이 되는 플레이어입니다.
	 * @param target
	 * 			위치를 비교할 대상이 되는 플레이어입니다.
	 */
	public final Vector GetDistanceVectorBetweenPlayers(PlayerInfo origin, PlayerInfo target)
	{
		return new Vector(origin.position, target.position);
	}
	
	/**
	 * 해당 플레이어와 나 자신이 위치한 좌표를 잇는 벡터를 만들어 반환합니다.
	 * 반환된 벡터는 해당 플레이어가 나와 어떤 방향으로 얼마나 떨어져 있는지 확인하는 용도로 쓰일 수 있습니다.
	 * 
	 * @param other
	 * 			위치를 비교할 대상이 되는 플레이어입니다.
	 */
	public final Vector GetDistanceVectorBetweenPlayers(PlayerInfo other)
	{
		return GetDistanceVectorBetweenPlayers(myInfo, other);
	}

	/**
	 * 현재 해당 좌표의 칸을 직접 볼 수 있는지 여부를 반환합니다.
	 * 생존자는 자신 주위 2칸, 시체는 자신이 누워 있는 칸, 감염체는 자신 중심 5x5칸, 그리고 영혼은 모든 칸을 직접 볼 수 있습니다.
	 * 
	 * @param x
	 *            검사할 x좌표입니다.
	 * @param y
	 *            검사할 y좌표입니다.
	 * @return 해당 칸을 직접 볼 수 있는 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean CanSee(int x, int y)
	{
		// 유효하지 않은 좌표인 경우 볼 수 없음
		if ( IsValidSpawn(x, y) == false )
			return false;

		Point myPosition = myInfo.GetPosition();

		switch (myInfo.GetState())
		{
		case Survivor:
			// 생존자는 자신 주위 2칸을 볼 수 있음
			return GetDistance(myPosition, x, y) <= 2;
		case Corpse:
			// 시체는 자신이 누워 있는 칸만 볼 수 있음
			return myPosition.x == x && myPosition.y == y;
		case Infected:
			// 감염체는 자신 중심 5x5칸을 볼 수 있음
			int offset_x = myPosition.x - x;
			int offset_y = myPosition.y - y;
			return offset_x >= -2 && offset_x <= 2 && offset_y >= -2 && offset_y <= 2;
		case Soul:
			// 영혼은 유효한 모든 칸을 볼 수 있음
			return true;
		default:
			return false;
		}
	}

	/**
	 * 현재 해당 좌표의 칸을 직접 볼 수 있는지 여부를 반환합니다.
	 * 생존자는 자신 주위 2칸, 시체는 자신이 누워 있는 칸, 감염체는 자신 중심 5x5칸, 그리고 영혼은 모든 칸을 직접 볼 수
	 * 있습니다.
	 * 
	 * @param location
	 *            검사할 좌표입니다.
	 * @return 해당 칸을 직접 볼 수 있는 경우 true, 그렇지 않은 경우 false입니다.
	 */
	public final boolean CanSee(Point location)
	{
		return CanSee(location.x, location.y);
	}

	/**
	 * 해당 좌표의 칸에 대한 정보를 가져옵니다.
	 * 만약 해당 칸이 현재 시야 범위 내에 있지 않은 경우 '비어 있는 칸'을 반환합니다.
	 * 
	 * @param x
	 *            칸 정보를 가져올 x좌표입니다.
	 * @param y
	 *            칸 정보를 가져올 y좌표입니다.
	 */
	public final CellInfo GetCellInfo(int x, int y)
	{
		if ( CanSee(x, y) == false ) { return CellInfo.Blank; }

		return cells.data[y][x];
	}

	/**
	 * 해당 좌표의 칸에 대한 정보를 가져옵니다.
	 * 만약 해당 칸이 현재 시야 범위 내에 있지 않은 경우 '비어 있는 칸'을 반환합니다.
	 * 
	 * @param location
	 *            칸 정보를 가져올 좌표입니다.
	 */
	public final CellInfo GetCellInfo(Point location)
	{
		return GetCellInfo(location.x, location.y);
	}
}
