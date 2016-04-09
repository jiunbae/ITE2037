package l4g;

import l4g.common.DirectionCode;
import l4g.common.Point;
import l4g.common.Point_Immutable;

/**
 * 플레이어가 매 턴 수행하는 의사 결정과 관련한 정보들을 담는 클래스입니다.
 * 이 클래스는 강의실 내부에서만 사용되며 여러분은 이 클래스를 볼 수 없습니다.
 * 어떤 플레이어가 어떤 행동을 했었는지 확인하려면 행동 목록을 참고하세요.
 * 
 * @author Racin
 *
 */
class Decision
{
	/**
	 * 의사 결정 형식을 구분하기 위한 열거자입니다.
	 * 
	 * @author Racin
	 *
	 */
	enum TypeCode
	{
		Not_Defined,
		Survivor_Move,
		Corpse_Stay,
		Infected_Move,
		Soul_Stay,
		Soul_Spawn
	}
	
	/**
	 * 이번 의사 결정의 형식을 나타냅니다.
	 */
	TypeCode type;
	
	/**
	 * 이동 의사 결정인 경우 선택한 방향을 나타냅니다.
	 */
	DirectionCode move_direction;
	
	/**
	 * 배치 의사 결정인 경우 선택한 좌표를 나타냅니다.
	 */
	Point spawn_point;

	/**
	 * 의사 결정을 수행한, 플레이어가 원래 있던 칸의 좌표를 나타냅니다.
	 */
	Point_Immutable location_from;
	
	/**
	 * 검증 후 확정된, 이동 또는 배치하려는 칸의 좌표를 나타냅니다.
	 * 이동 또는 배치가 유효하지 않은 경우 이 필드의 값은 <code>Constants.Pos_Sky</code>가 됩니다.
	 */
	Point_Immutable location_to;

	/**
	 * Note: 여러분은 이 클래스를 전혀 사용할 수 없습니다.
	 * 어떤 플레이어가 어떤 행동을 했었는지 확인하려면 행동 목록을 참고하세요.
	 */
	Decision()
	{
	}
}
