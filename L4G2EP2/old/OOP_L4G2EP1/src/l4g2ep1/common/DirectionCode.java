package l4g2ep1.common;

/**
 * 이동 의사 결정에서 이번 턴에 이동할 방향을 나타내기 위해 사용하는 열거자입니다.
 * 
 * @author Racin
 *
 */
public enum DirectionCode
{
	Up,
	Down,
	Left,
	Right,
	
	/**
	 * 주의:
	 * 생존자는 제자리에 머무를 수 없습니다.
	 * 만약 Survivor_Move()가 DirectionCode.Stay를 반환하는 경우 잘못된 이동으로 간주되어 체포되게 됩니다. 
	 */
	Stay
}
