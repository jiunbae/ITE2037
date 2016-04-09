package l4g;

import java.util.ArrayList;

import l4g.common.Constants;
import l4g.common.Point_Immutable;
import l4g.common.StateCode;

/**
 * 게임 중 발생하는 플레이어 한 명에 대한 각종 정보들을 담는 클래스입니다.
 * 여러분은 이 클래스의 내용들 중 일부를 옮겨 담은 <code>PlayerInfo</code> 인스턴스를 대신 사용하게 됩니다.
 * 
 * 이 클래스는 여러분 코드에서 생성할 수 없으며
 * 이미 만들어진 인스턴스들에 액세스할 수도 없습니다.
 * 
 * @author Racin
 *
 */
public class PlayerStat
{
	/**
	 * 이 플레이어의 일련 번호입니다.
	 * 이 필드는 읽기 전용이며 <code>PlayerInfo</code>에 포함됩니다.
	 */
	public final int ID;
	
	/**
	 * 이 플레이어의 상태(생존자인지 시체인지 등등)입니다.
	 * 이 필드는 <code>PlayerInfo</code>에 포함됩니다.
	 */
	public StateCode state;
	
	/**
	 * 이 플레이어의 턴 시작 시점에서의 상태입니다.
	 */
	public StateCode lastState;

	/**
	 * 이 플레이어의 체력량입니다.
	 * 이 필드는 <code>PlayerInfo</code>에 포함됩니다.
	 */
	public int HP;
	
	/**
	 * 상태 전이(시체 일어남, 영혼 재배치, 직접감염 수락)까지 남은 턴 수입니다.
	 * 이 값은 쿨다운이 적용되는 순간(죽음, 소멸, 패널티 부여, 직접감염 선택) 갱신됩니다.
	 * 쿨다운 감소는 항상 턴이 시작되기 직전에 일어나며
	 * 쿨다운이 0이 된다면 이번 턴에 해당 사건이 발생하게 됩니다.
	 * 이 필드는 <code>PlayerInfo</code>에 포함됩니다.
	 */
	public int transitionCooldown;

	/**
	 * 이 플레이어의 위치입니다.
	 * 영혼 상태의 플레이어들은 <code>Constants.Pos_Sky</code>에 위치한 것으로 설정됩니다.
	 * 이 필드는 <code>PlayerInfo</code>에 포함됩니다.
	 */
	public Point_Immutable position;
	
	/**
	 * 이 플레이어의 턴 시작 시점에서의 위치입니다.
	 * 영혼 상태의 플레이어들은 <code>Constants.Pos_Sky</code>에 위치한 것으로 설정됩니다.
	 */
	public Point_Immutable lastPosition;
	
	/**
	 * 생존자가 직접감염을 수락했는지 여부입니다.
	 * 이 값은 직접감염을 수락한 순간 갱신되며
	 * 직접감염이 발동된 순간(자신이 실제로 받았든 아니든) reset됩니다.
	 */
	public boolean survivor_AcceptedDirectInfection;
	
	/**
	 * 생존자가 이번 턴에 죽었는지(죽게 될 지) 여부입니다.
	 * 이 값은 처치 사건이 발생한 순간 set되며
	 * 생존자 상태에서 시체 상태로 전이되는 순간 reset됩니다.
	 */
	public boolean survivor_Dead;
	
	/**
	 * 시체가 흩뿌린 살덩이 파편의 범위(칸 수)입니다.
	 * 이 값은 시체가 되는 순간 초기값으로 지정되며 매 턴이 끝날 때마다 1씩 감소합니다.
	 * 최소값은 0입니다.
	 */
	public int corpse_radius;	

	/**
	 * 감염체가 이번 턴에 Stay를 선택했는지 여부입니다.
	 * 이 값은 이동 의사 결정 처리 도중 갱신됩니다.
	 */
	public boolean infected_Stayed;
	
	/**
	 * 감염체가 이번 턴에 처치 또는 처치 도움을 기록했는지 여부입니다.
	 * 이 값은 처치 사건이 발생한 순간 set되며
	 * 처치 점수를 갱신하는 도중 reset됩니다.
	 */
	public boolean infected_KilledOrAssisted;
	
	/**
	 * 감염체가 이번 턴에 시체의 치유를 받았는지 여부입니다.
	 * 이 값은 치유 사건이 발생한 순간 set되며
	 * 감염체들의 체력을 깎는 도중 reset됩니다.
	 */
	public boolean infected_Healed;

	/**
	 * 감염체가 현재 정화 기도를 드릴 경우 몇 개의 카운터를 획득할 수 있는지(기도 효율)를 나타냅니다.
	 * 이 값은 정화 기도에 성공할 때마다 1씩 상승하며
	 * 다른 상태에서 감염체 상태로 전이되었거나, 치유를 받았거나, 정화 기도를 시도하지 않고 이동했다면 다시 1로 reset됩니다.
	 */
	public int infected_PrayPower;
	
	/**
	 * 감염체가 현재까지 모은 정화 기도 카운터 수를 나타냅니다.
	 * 이 값은 정화 기도에 성공할 때마다 Power만큼 증가하며
	 * 시체 상태에서 감염체 상태로 전이되는 순간,
	 * 또는 카운터를 소모하여 감염체 상태에서 영혼 상태로 전이되는 순간 reset됩니다.
	 */
	public int infected_CurrentPrayCounter;

	/**
	 * 현재 생존자로서 생존한 연속 턴 수입니다.
	 * 이 값은 생존자 상태에서 시체 상태로 전이되는 순간 reset됩니다.
	 */
	public int survivor_CurrentSurvivedTurns;
	
	/**
	 * 현재 시체로서 치유한 플레이어 ID 목록입니다.
	 * 이 값은 시체 상태에서 감염체 상태로 전이되는 순간 reset됩니다.
	 */
	public ArrayList<Integer> corpse_CurrentHealedPlayers;
	
	/**
	 * 현재 감염체로서 처치한 횟수입니다.
	 * 이 값은 소멸 사건이 발생한 순간 reset됩니다.
	 */
	public int infected_CurrentKillStreaks;
	
	/**
	 * 새로운 <code>PlayerStat class</code>의 인스턴스를 0턴째 영혼 상태에 해당하도록 초기화합니다.
	 * 이 생성자는 여러분이 볼 수 없습니다(여러분은 이 클래스의 인스턴스를 만들 수 없습니다).
	 */
	PlayerStat(int ID)
	{
		this.ID = ID;
		state = StateCode.Soul;
		lastState = StateCode.Soul;
		HP = 0;
		transitionCooldown = 0;
		position = Constants.Pos_Sky;
		lastPosition = Constants.Pos_Sky;
		
		survivor_AcceptedDirectInfection = false;
		survivor_Dead = false;
		
		corpse_radius = 0;
		
		infected_Stayed = false;
		infected_KilledOrAssisted = false;
		infected_Healed = false;
		
		survivor_CurrentSurvivedTurns = 0;
		corpse_CurrentHealedPlayers = new ArrayList<Integer>();
		infected_CurrentKillStreaks = 0;
	}
}
