package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 의사 결정의 결과로 수행된 행동 하나를 나타냅니다.
 * 
 * @author Racin
 *
 */
public class Action
{
	/**
	 * 행동의 종류를 구분하기 위한 열거자입니다.
	 * 
	 * @author Racin
	 * 
	 */
	public enum TypeCode
	{
		Not_Defined,

		/**
		 * 자신의 위치를 변경합니다. 생존자 및 감염체 상태일 때 발생합니다.
		 */
		Move,

		/**
		 * 강의실 내의 새로운 위치로 자신을 배치시킵니다. 영혼 상태에서 부활할 때 발생합니다.
		 */
		Spawn,

		/**
		 * 잘못된 이동 또는 배치를 시도했을 때 발생합니다.
		 * 
		 * (플레이어들은 이 type을 가진 행동을 볼 수 없으며
		 * 대신 사건 목록에서 type이 Punished / Arrested인 사건을 통해 누가 잘못된 의사 결정을 했는지 확인할 수 있습니다)
		 */
		Illigal,
	}
	
	/*
	 * 여기 있는 필드들은 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 내용을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 필드의 내용을 수정할 수는 없습니다 )
	 */
	int actorID;
	TypeCode type;
	Point location_from;
	Point location_to;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 여러분의 시야 범위 내에서 수행된 행동들은 모두 자동으로 목록에 기록되어 전달됩니다.
	 */
	Action(int actorID, Decision origin, boolean isLegal)
	{
		this.actorID = actorID;

		location_from = new Point(origin.location_from);

		switch (origin.type)
		{
		case Survivor_Move:
		case Infected_Move:
			type = TypeCode.Move;
			location_to = new Point(origin.location_from, origin.direction);
			break;
		case Soul_Spawn:
			type = TypeCode.Spawn;
			location_to = new Point(origin.location_to);
			break;
		default:
			type = TypeCode.Not_Defined;
			break;
		}

		if ( isLegal == false )
		{
			type = TypeCode.Illigal;
		}
	}

	/**
	 * 이 행동을 수행한 플레이어의 ID를 가져옵니다.
	 */
	public int GetActorID() { return actorID; }

	/**
	 * 이 행동의 형식을 가져옵니다.
	 * 형식 목록은 Action.TypeCode. 를 입력하면 확인할 수 있습니다.
	 */
	public TypeCode GetType() { return type; }

	/**
	 * 이 행동이 수행된 시작 위치를 가져옵니다.
	 * 배치 행동의 경우 시작 위치와 도착 위치가 동일하게 기록되어 있습니다.
	 */
	public Point GetLocation_From() { return new Point(location_from); }

	/**
	 * 이 행동이 수행된 도착 위치를 가져옵니다.
	 * 배치 행동의 경우 시작 위치와 도착 위치가 동일하게 기록되어 있습니다.
	 */
	public Point GetLocation_To() { return new Point(location_to); }

	/**
	 * 이 행동이 해당 좌표와 연관되어 있는지 여부를 반환합니다.
	 * 시작 위치 또는 도착 위치가 해당 좌표인 경우 연관되어 있는 것으로 간주합니다.
	 * 
	 * @return 이 행동이 해당 좌표와 연관되어 있다면 true, 그렇지 않다면 false입니다.
	 */
	public boolean IsInvolvedIn(Point location) { 	return location.equals(location_from) || location.equals(location_to); }

	/**
	 * 이 행동이 해당 플레이어에 의해 수행되었는지 여부를 반환합니다.
	 * 
	 * @param ID
	 *            연관 여부를 확인할 플레이어의 ID입니다.
	 * @return 이 행동이 해당 플레이어와 연관되어 있다면 true, 그렇지 않다면 false입니다.
	 */
	public boolean IsInvolvedWith(int ID)	{ return actorID == ID; }
}
