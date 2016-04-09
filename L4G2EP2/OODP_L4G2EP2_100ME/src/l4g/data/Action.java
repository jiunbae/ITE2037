package l4g.data;

import l4g.common.Point_Immutable;

/**
 * 의사 결정의 결과로 수행된 행동 하나를 나타냅니다.
 * 
 * 이 클래스의 모든 필드는 읽기 전용입니다.
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
		 * 자신의 위치를 변경합니다. 생존자 및 감염체 상태일 때 수행 가능합니다.
		 */
		Move,
		
		/**
		 * 새로운 위치로 자신을 배치시킵니다. 영혼 상태에서 일정 턴이 경과한 다음 수행 가능합니다.
		 */
		Spawn
	}
	
	/**
	 * 이 행동을 수행한 플레이어의 ID입니다.
	 */
	public final int actorID;
	
	/**
	 * 이 행동의 형식입니다.
	 * 형식 목록은 <code>Action.TypeCode</code> 치고 '쩜'을 찍으면 확인할 수 있습니다.
	 */
	public final TypeCode type;
	
	/**
	 * 이 행동이 수행된 시작 위치입니다.
	 * 배치 행동의 경우 시작 위치는 <code>Constants.Pos_Sky</code>가 됩니다.
	 */
	public final Point_Immutable location_from;
	
	/**
	 * 이 행동이 수행된 도착 위치입니다.
	 */
	public final Point_Immutable location_to;
	
	/**
	 * 주의: 이 클래스의 인스턴스는 여러분이 굳이 새로 만들 필요가 없습니다.
	 * 만약 어떤 행동 정보의 사본을 소장하고 싶은 경우엔
	 * 그냥 그 인스턴스 자체를 여러분의 필드에 할당해 담아도 됩니다.
	 * 읽기 전용이라 다음 턴엔 어차피 새 인스턴스 만들어서 사용하기 때문입니다.
	 */
	public Action(int actorID, TypeCode type, Point_Immutable location_from, Point_Immutable location_to)
	{
		this.actorID = actorID;
		this.type = type;
		this.location_from = location_from;
		this.location_to = location_to;
	}
}
