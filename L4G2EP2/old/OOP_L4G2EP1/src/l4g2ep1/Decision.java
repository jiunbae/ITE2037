package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 플레이어가 매 턴 수행하는 의사 결정 하나를 나타냅니다.
 * 이 클래스는 강의실 내부에서만 사용되며 여러분은 이 클래스를 볼 수 없습니다.
 * 어떤 플레이어가 어떤 행동을 했었는지 확인하려면 행동 목록을 참고하세요.
 * 
 * @author Racin
 *
 */
class Decision
{
	enum TypeCode
	{
		Not_Defined,
		Survivor_Move,
		Corpse_Stay,
		Infected_Move,
		Soul_Stay,
		Soul_Spawn
	}

	TypeCode type;
	DirectionCode direction;
	Point location_from;
	Point location_to;

	/*
	 * 여러분은 이 클래스를 전혀 사용할 수 없습니다.
	 * 어떤 플레이어가 어떤 행동을 했었는지 확인하려면 행동 목록을 참고하세요.
	 */
	Decision()
	{
		location_from = new Point();
		location_to = new Point();
	}
}
