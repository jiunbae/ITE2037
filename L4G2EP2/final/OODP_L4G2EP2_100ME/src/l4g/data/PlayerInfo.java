package l4g.data;

import l4g.PlayerStat;
import l4g.common.Point_Immutable;
import l4g.common.StateCode;

/**
 * 플레이어 한 명에 대한 정보를 제공하기 위한 클래스입니다.
 * 
 * 이 클래스의 모든 필드는 읽기 전용입니다. 
 * 
 * @author Racin
 *
 */
public class PlayerInfo
{
	/**
	 * 이 플레이어의 일련 번호입니다.
	 */
	public final int ID;
	
	/**
	 * 이 플레이어의 상태(생존자인지 시체인지 등등)입니다.
	 */
	public final StateCode state;
	
	/**
	 * 이 플레이어의 체력량입니다.
	 * 
	 * 이 값은 시체 또는 감염체 상태일 때만 유효합니다.
	 */
	public final int HP;
	
	/**
	 * 이 플레이어가 상태 전이(시체 일어남, 영혼 재배치, 직접감염 수락)를 수행하기까지 남은 턴 수입니다. 
	 * 이 값이 0이면 해당 플레이어는 이번 턴에 일어나거나, 배치 의사 결정을 수행하거나, 감염체가 됩니다. 
	 * 
	 * 이 값은 음수일 수 있으며, 이 경우 '상태 전이가 예정되어 있지 않음'을 의미합니다.
	 */
	public final int transition_cooldown;

	/**
	 * 이 플레이어의 위치입니다.
	 * 
	 * 영혼 상태의 플레이어들은 <code>Constants.Pos_Sky</code>에 위치한 것으로 설정됩니다.
	 * (어차피 영혼을 볼 수 있는 것은 내가 영혼일 때 뿐이므로 그리 큰 의미는 없습니다)
	 */
	public final Point_Immutable position;
	
	/**
	 * 주의: 여러분은 유효한 <code>PlayerStat</code> 인스턴스를 전혀 볼 수 없으며
	 * 심지어 직접 새 인스턴스를 생성할 수도 없으므로
	 * 이 생성자는 적어도 여러분에게는 아무 의미 없습니다.
	 */
	public PlayerInfo(PlayerStat stat)
	{
		ID = stat.ID;
		state = stat.state;
		HP = stat.HP;
		transition_cooldown = stat.transitionCooldown;
		position = stat.position;
	}
}
