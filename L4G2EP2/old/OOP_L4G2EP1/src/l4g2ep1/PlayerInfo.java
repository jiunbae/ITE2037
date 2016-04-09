package l4g2ep1;

import l4g2ep1.common.Point;

/**
 * 플레이어에게 주어지는 자신 또는 다른 플레이어에 대한 정보입니다.
 * 
 * @author Racin
 * 
 */
public class PlayerInfo
{
	/**
	 * 플레이어의 현재 상태를 구분하기 위한 열거자입니다.
	 * 
	 * @author Racin
	 *
	 */
	public enum State
	{
		Not_Defined,
		Survivor,
		Corpse,
		Infected,
		Soul
	}

	/*
	 * 여기 있는 필드들은 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 내용을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 필드의 내용을 수정할 수는 없습니다 )
	 */
	int ID;
	State state;
	int hitPoint;
	Point position;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 각 플레이어에 대한 정보들은 해당되는 목록에 자동으로 기록되어 전달됩니다.
	 */
	PlayerInfo(int ID)
	{
		this.ID = ID;
		state = State.Not_Defined;
		hitPoint = 0;
		position = new Point();
	}

	PlayerInfo(PlayerInfo other)
	{
		ID = other.ID;
		state = other.state;
		hitPoint = other.hitPoint;
		position = new Point(other.position);
	}

	/**
	 * 해당 플레이어의 고유 번호(ID)를 가져옵니다.
	 */
	public int GetID()
	{
		return ID;
	}

	/**
	 * 해당 플레이어의 현재 상태를 가져옵니다.
	 */
	public State GetState()
	{
		return state;
	}

	/**
	 * 해당 플레이어의 현재 체력을 가져옵니다.
	 * 생존자 또는 영혼의 경우 이 값은 0이며 사용되지 않습니다.
	 */
	public int GetHitPoint()
	{
		return hitPoint;
	}

	/**
	 * 해당 플레이어의 현재 위치를 가져옵니다.
	 */
	public Point GetPosition()
	{
		return new Point(position);
	}
}
