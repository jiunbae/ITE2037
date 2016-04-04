package l4g2ep1.common;

/**
 * L4G2EP1에서 사용하는 여러 상수들을 담고 있습니다.
 * 
 * @author Racin
 * 
 */
public class Constants
{

	/**
	 * 강의실의 가로 길이입니다.
	 */
	public static final int Classroom_Width = 9;

	/**
	 * 강의실의 세로 길이입니다.
	 */
	public static final int Classroom_Height = 9;

	/**
	 * 게임에 참여하는 총 플레이어 수입니다.
	 */
	public static final int Total_Players = 40;

	/**
	 * 게임이 총 몇 턴으로 구성되어 있는지 나타냅니다.
	 */
	public static final int Total_Turns = 120;

	/**
	 * 직접 감염이 시작된 다음 적용되기까지 걸리는 턴 수입니다.
	 */
	public static final int Survivor_Interval_DirectInfection = 10;

	/**
	 * 시체가 다시 일어나기까지 걸리는 턴 수입니다.
	 */
	public static final int Corpse_Interval_Rise = 3;

	/**
	 * 시체가 한 턴에 한 감염체를 치유할 수 있는 체력량입니다.
	 */
	public static final int Corpse_Rate_Heal = 2;

	/**
	 * 감염체가 초기에 보유하는 최소 체력입니다.
	 */
	public static final int Infected_Initial_Point = 10;

	/**
	 * 감염체가 한 턴에 한 시체를 감염시킬 수 있는 체력량입니다.
	 */
	public static final int Infected_Rate_Infection = 3;

	/**
	 * 감염체가 다른 시체가 없는 칸으로 이동했을 때 소모되는 체력량입니다.
	 */
	public static final int Infected_Cost_Move = 1;

	/**
	 * 감염체가 다른 시체가 없는 칸에 머물러 있을 때 때 소모되는 체력량입니다.
	 */
	public static final int Infected_Cost_Pray = 5;

	/**
	 * 영혼이 재배치하기까지 걸리는 턴 수입니다.
	 */
	public static final int Soul_Interval_Respawn = 3;

	/**
	 * 오류 또는 잘못된 이동으로 영혼 패널티를 받았을 때 재배치하기까지 걸리는 턴 수입니다.
	 */
	public static final int Soul_Interval_Penalty = 10;
}
