package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 행동의 결과로 발생한 사건 하나를 나타냅니다.
 * 
 * @author Racin
 * 
 */
public class Reaction
{
	/**
	 * 사건의 종류를 구분하기 위한 열거자입니다.
	 * 
	 * @author Racin
	 * 
	 */
	public enum TypeCode
	{
		Not_Defined,

		/**
		 * subject(생존자)가 object(시체 또는 감염체)를 발견
		 */
		Spot,

		/**
		 * subject(시체)가 object(감염체)를 치유
		 */
		Heal,

		/**
		 * subject(시체)가 감염체가 됨
		 */
		Rise,

		/**
		 * subject(생존자)가 직접 감염을 통해 감염체가 됨
		 */
		DirectInfect,

		/**
		 * subject(감염체)가 object(생존자)를 죽임
		 */
		Kill,

		/**
		 * subject(감염체)가 영혼이 됨
		 */
		Vanish,

		/**
		 * subject(생존자, 감염체, 영혼)가 잘못된 의사 결정으로 영혼이 됨
		 */
		Punished,

		/**
		 * subject(생존자, 시체, 감염체, 영혼)가 런타임 예외를 일으켜 영혼이 됨
		 */
		Arrested,
	}

	/*
	 * 여기 있는 필드들은 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 내용을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 필드의 내용을 수정할 수는 없습니다 )
	 */
	int subjectID;
	TypeCode type;
	int objectID;
	Point location;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 여러분의 시야 범위 내에서 발생했던 사건들은 모두 자동으로 목록에 기록되어 전달됩니다.
	 */
	Reaction(int subjectID, TypeCode type, int objectID, Point location)
	{
		this.subjectID = subjectID;
		this.type = type;
		this.objectID = objectID;
		this.location = new Point(location);
	}

	/**
	 * 이 사건의 subject(행위를 하는 자)가 되는 플레이어의 ID를 가져옵니다.
	 * 상호 작용이 아닌 일부 사건의 경우 subject와 object가 동일하게 기록되어 있습니다.
	 */
	public int GetSubjectID() { return subjectID; }

	/**
	 * 이 사건의 형식을 가져옵니다.
	 * 형식 목록은 Reaction.TypeCode. 를 입력하면 확인할 수 있습니다.
	 */
	public TypeCode GetType() { return type; }

	/**
	 * 이 사건의 object(행위를 당하는 자)가 되는 플레이어의 ID를 가져옵니다.
	 * 상호 작용이 아닌 일부 사건의 경우 subject와 object가 동일하게 기록되어 있습니다. 
	 */
	public int GetObjectID()
	{
		return objectID;
	}

	/**
	 * 이 사건이 발생한 위치를 가져옵니다.
	 */
	public Point GetLocation()
	{
		return new Point(location);
	}

	/**
	 * 이 사건이 해당 플레이어와 연관되어 있는지 여부를 반환합니다.
	 * subject 또는 object가 해당 플레이어인 경우 연관된 것으로 간주합니다.
	 * 
	 * @param ID
	 *            검사할 플레이어의 ID입니다.
	 * @return 해당 플레이어와 연관되어 있다면 true, 그렇지 않다면 false입니다.
	 */
	public boolean IsInvolvedWith(int ID)
	{
		return subjectID == ID || objectID == ID;
	}

	/**
	 * 이 사건이 해당 위치에서 발생했는지 여부를 반환합니다.
	 * 
	 * @param location 연관 여부를 확인할 좌표입니다.
	 * @return 해당 위치와 연관되어 있다면 true, 그렇지 않다면 false입니다.
	 */
	public boolean IsInvolvedIn(Point location)
	{
		return this.location.equals(location);
	}
}
