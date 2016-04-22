package l4g.common;




/**
 * L4G에서 사용하는 갖가지 상수들을 몽땅 모아 놓은 클래스입니다.<br>
 * <br>
 * <b>Note:</b> 여기 있는 필드들의 값은 (상수니까) 실행 도중 변경되지 않습니다. 
 * <code>Constants.java</code> 파일을 열어 보면 
 * 모든 필드에 <code>final</code>이 붙어 있는 것을 확인할 수 있으며 
 * 이는 우리가 평소 접해 본 일반적인 <code>const</code> 키워드와 동일한 효과를 냅니다.
 * 
 * @author Racin
 *
 */
public class Constants
{
	/**
	 * <b>강의실의 가로 길이</b>를 나타냅니다.
	 * 
	 * 기본값은 13입니다.
	 */
	public static final int Classroom_Width = 13;
	
	/**
	 * <b>강의실의 세로 길이</b>를 나타냅니다.
	 * 
	 * 기본값은 13입니다.
	 */
	public static final int Classroom_Height = 13;
	
	/**
	 * L4G에 참여하는 <b>총 플레이어 수</b>를 나타냅니다.
	 * 
	 * 기본값은 100입니다.
	 * 정규 게임에서, 이 중 87자리는 여러분이 제출한 플레이어 클래스로 채워지며
	 * 남은 자리는 team Sigma 소속 플레이어, 또는 모드 설정에 따라 '직접 조작 가능한 플레이어'가 차지합니다.
	 * 등록된 플레이어 클래스 수가 모자라는 경우 남은 자리는 bot들로 채웁니다.
	 */
	public static final int Total_Players = 100;
	
	/**
	 * 한 게임의 <b>총 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 120이며 이 때 게임은 총 121턴(0턴 시작 ~ 120턴 끝)간 진행됩니다.
	 */
	public static final int Max_Turn_Numbers = 120;
	
	/**
	 * <b>직접 감염이 시작되고 나서 실제로 발동되기까지 걸리는 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 10입니다.
	 * 일반적으로 0턴째 영혼 배치 이후 1턴째가 되면 직접 감염이 시작되며
	 * 실제 발동은 (1 + 이 필드의 값)턴의 이동이 끝난 다음 이루어집니다.
	 */
	public static final int Duration_Direct_Infection = 10;

	/**
	 * 강의실 탈출 또는 파괴를 시도하다 적발되어 강제 영혼 상태가 되었을 때
	 * <b>다음 배치까지 걸리는 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 20입니다.
	 * 플레이어가 N턴째에 런타임 오류를 내다 적발되어 영혼 상태가 되면
	 * (N + 이 필드의 값)턴의 대기가 끝난 다음 생존자로 재배치하게 됩니다.
	 */
	public static final int Duration_Soul_Penalty = 20;

	/**
	 * 생존자가 죽어 시체가 되었을 때 갖는 <b>초기 체력</b>입니다.
	 * 
	 * 기본값은 10입니다.
	 * '시체가 되었다'는 것은 '같은 칸에 감염체가 있어서 죽었다'는 것이며
	 * 턴 진행 순서에 따라 '감염체->시체 감염시키기'는 '감염체->생존자 죽이기'보다 늦게 발생하므로
	 * 모든 시체는 항상 이 값보다 큰 체력을 지닌 채로 일어나게 됩니다. 
	 */
	public static final int Corpse_Init_HP = 10;
	
	/**
	 * 시체가 사망한 턴에 갖는 <b>살덩이 파편의 초기 범위(칸 수)</b>입니다.
	 * 
	 * 기본값은 2(주변 두 칸)입니다.
	 * 살덩이 파편의 범위는 매 턴이 끝날 때마다(사망한 턴 포함) 1씩 감소하며 최소값은 0(시체가 있는 칸만 적용)입니다.
	 */
	public static final int Corpse_Init_Radius = 2;
	
	/**
	 * 시체가 같은 칸에 있는 감염체를 <b>한 턴에 얼마나 치유하는지</b>를 나타냅니다.
	 * 
	 * 기본값은 3입니다.
	 */
	public static final int Corpse_Heal_Power = 3;
	
	/**
	 * 시체가 감염되어 <b>일어나기까지 대기할 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 3입니다.
	 * 생존자가 N턴째에 죽어 시체가 되면 (N + 이 필드의 값)턴의 대기가 끝난 다음 일어나 감염체가 됩니다.
	 */
	public static final int Corpse_Rise_Cooldown = 3;
	
	/**
	 * 감염체가 같은 칸에 있는 시체를 <b>한 턴에 얼마나 감염시키는지</b>를 나타냅니다.
	 * 
	 * 기본값은 3입니다.
	 * 여기서 말하는 감염은 해당 시체의 체력량을 증가시키는 것을 의미합니다.
	 */
	public static final int Infected_Infection_Power = 3;
	
	/**
	 * 감염체가 <b>정화 기도 카운터를 몇 개 모아야 소멸할 수 있는지</b>를 나타닙니다.
	 * 
	 * 기본값은 6입니다.
	 */
	public static final int Infected_RequiredPrayerCounter = 6;
	
	/**
	 * 영혼이 <b>재배치하기까지 대기할 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 3입니다.
	 * 감염체가 N턴째에 소멸하여 영혼이 되면 (N + 이 필드의 값)턴의 대기가 끝난 다음 생존자로 재배치하게 됩니다.
	 */
	public static final int Soul_Spawn_Cooldown = 3;
	
	/**
	 * 정화 기도에 성공하여 영혼이 된 플레이어가 <b>재배치하기까지 대기할 턴 수</b>를 나타냅니다.
	 * 
	 * 기본값은 2입니다.
	 * 감염체가 N턴째에 소멸하여 영혼이 되면 (N + 이 필드의 값)턴의 대기가 끝난 다음 생존자로 재배치하게 됩니다.
	 */
	public static final int Soul_Spawn_Cooldown_AfterPray = 2;
	
	/**
	 * 영혼 상태인 플레이어가 위치하는 '하늘(강의실 위)'을 나타낼 때 사용하는 가상 좌표입니다.
	 */
	public static final Point_Immutable Pos_Sky = new Point_Immutable(Integer.MIN_VALUE, Integer.MIN_VALUE);
}
