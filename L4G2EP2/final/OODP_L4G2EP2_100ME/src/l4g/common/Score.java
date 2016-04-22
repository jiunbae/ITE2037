package l4g.common;

/**
 * 플레이어 한 명의 현재 점수를 담는 클래스입니다.
 * 각 필드 값은 매 턴 갱신되며, 여러분이 직접 고쳐도 실제 점수에 반영되거나 하진 않으니 안심하세요.<br>
 * <br>
 * <b>주의:</b>
 * 여기서 말하는 점수는
 * 게임이 끝났을 때 상대평가에 의해 산출되는 '학점(grade)'이 아닌,
 * 말 그대로 획득한 포인트, 즉, 원점수를 의미합니다.
 *  
 * @author Racin
 *
 */
public class Score
{
	/**
	 * 생존자 MAX 점수입니다.<br>
	 * <br>
	 * 현재 기록 획득: 턴이 끝날 때 생존자 상태인 경우 +1<br>
	 * 현재 기록 초기화: 사망하여 시체 상태가 되는 순간
	 */
	public int survivor_max;
	
	/**
	 * 생존자 Total 점수입니다.<br>
	 * <br>
	 * 획득: 턴이 끝날 때 시야 내에 있는 시체 또는 감염체 수만큼 +<br>
	 */
	public int survivor_total;
	
	/**
	 * 시체 MAX 점수입니다.<br>
	 * <br>
	 * 현재 기록 획득: 새로운 플레이어(이전에 치유한 적 없는)를 치유할 때마다 +1<br>
	 * 현재 기록 초기화: 3턴이 지나 감염체 상태가 되는 순간
	 */
	public int corpse_max;
	
	/**
	 * 시체 Total 점수입니다.<br>
	 * <br>
	 * 획득: 감염체를 치유한 체력량만큼 +<br>
	 */
	public int corpse_total;
	
	/**
	 * 감염체 MAX 점수입니다.<br>
	 * <br>
	 * 현재 기록 획득: 플레이어(이전에 처치한 적 있든 아니든)를 처치할 때마다 +1<br>
	 * 현재 기록 초기화: 체력이 0이 되어 영혼 상태가 되는 순간
	 */
	public int infected_max;
	
	/**
	 * 감염체 Total 점수입니다.<br>
	 * <br>
	 * 획득: 시체를 감염시킨 체력량만큼 +<br>
	 */
	public int infected_total;	

	public Score()
	{
		survivor_max = 0;
		survivor_total = 0;
		corpse_max = 0;
		corpse_total = 0;
		infected_max = 0;
		infected_total = 0;
	}
}
