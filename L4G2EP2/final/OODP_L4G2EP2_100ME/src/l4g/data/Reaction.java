package l4g.data;

import l4g.common.Point_Immutable;

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

//		/**
//		 * subject(생존자)가 object(시체 또는 감염체)를 발견 - 이 사건은 너무 많이 발생하므로 등록하지 않음
//		 */
//		Spot,

		/**
		 * subject(시체)가 object(감염체)를 치유
		 */
		Heal,

		/**
		 * subject(시체)가 일어나 감염체가 됨
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
		 * subject(감염체)가 체력 고갈로 영혼이 됨
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

	/**
	 * 이 사건을 일으킨 플레이어(행위의 주체)의 ID입니다.
	 */
	public final int subjectID;

	/**
	 * 이 사건의 형식입니다.
	 * 형식 목록은 <code>Reaction.TypeCode</code> 치고 '쩜'을 찍으면 확인할 수 있습니다.
	 */
	public final TypeCode type;

	/**
	 * 이 사건을 당한 플레이어(행위의 대상)의 ID입니다.
	 * 혼자 일으키는 사건(Rise, DirectInfect 등)의 경우 이 값은 <code>subjectID</code>와 동일하게 지정됩니다.
	 */
	public final int objectID;

	/**
	 * 이 사건을 발생시킨 subject의 위치를 가져옵니다.
	 */
	public final Point_Immutable location_subject;

	/**
	 * 이 사건을 '당한' object의 위치를 가져옵니다.
	 */
	public final Point_Immutable location_object;

	/**
	 * 주의: 이 클래스의 인스턴스는 여러분이 굳이 새로 만들 필요가 없습니다.
	 * 만약 어떤 사건에 대한 사본을 소장하고 싶은 경우엔
	 * 그냥 그 인스턴스 자체를 여러분의 필드에 할당해 담아도 됩니다.
	 * 읽기 전용이라 다음 턴엔 어차피 새 인스턴스 만들어서 사용하기 때문입니다.
	 */
	public Reaction(int ID, TypeCode type, Point_Immutable location)
	{
		this.subjectID = ID;
		this.type = type;
		this.objectID = ID;
		this.location_subject = location;
		this.location_object = location;
	}

	/**
	 * 주의: 이 클래스의 인스턴스는 여러분이 굳이 새로 만들 필요가 없습니다.
	 * 만약 어떤 사건에 대한 사본을 소장하고 싶은 경우엔
	 * 그냥 그 인스턴스 자체를 여러분의 필드에 할당해 담아도 됩니다.
	 * 읽기 전용이라 다음 턴엔 어차피 새 인스턴스 만들어서 사용하기 때문입니다.
	 */
	public Reaction(int subjectID, TypeCode type, int objectID, Point_Immutable location_subject, Point_Immutable location_object)
	{
		this.subjectID = subjectID;
		this.type = type;
		this.objectID = objectID;
		this.location_subject = location_subject;
		this.location_object = location_object;
	}
}
