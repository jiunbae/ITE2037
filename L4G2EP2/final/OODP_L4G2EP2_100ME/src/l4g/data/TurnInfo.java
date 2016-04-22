package l4g.data;

/**
 * 이번 턴에 대한, 좀 더 일반적인 정보를 담기 위한 클래스입니다.
 * 
 * @author Racin
 *
 */
public class TurnInfo
{
	/**
	 * 이번 턴이 몇 턴인지를 나타냅니다.
	 * 
	 * 이 필드의 값은 최소 0, 최대(포함) <code>Constants.Max_Turn_Numbers</code>입니다.
	 */
	public final int turnNumber;
	
	/**
	 * 이번 턴에 직접 감염 수락 여부를 확인해야 하는지 여부를 나타냅니다.
	 * 
	 * 이 필드의 값이 true인 턴에 여러분이 생존자라면,
	 * 여러분 클래스 안에 있는 <code>trigger_acceptDirectInfection</code> 필드의 값이 무엇이냐에 따라
	 * 직접 감염을 수락하거나 거절하게 됩니다.
	 */
	public final boolean isDirectInfectionChoosingTurn;
	
	/**
	 * 주의: 이 클래스의 인스턴스는 여러분이 굳이 새로 만들 필요가 없습니다.
	 * 만약 이번 턴에 대한 정보의 사본을 소장하고 싶은 경우엔
	 * 그냥 그 인스턴스 자체를 여러분의 필드에 할당해 담아도 됩니다.
	 * 읽기 전용이라 다음 턴엔 어차피 새 인스턴스 만들어서 사용하기 때문입니다.
	 */
	public TurnInfo(int turnNumber, boolean isDirectInfectionChoosingTurn)
	{
		this.turnNumber = turnNumber;
		this.isDirectInfectionChoosingTurn = isDirectInfectionChoosingTurn;
	}
}
