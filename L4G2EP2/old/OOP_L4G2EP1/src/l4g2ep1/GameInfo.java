package l4g2ep1;

/**
 * 모든 플레이어에게 동일하게 주어지는, 현재 진행되고 있는 게임 자체에 대한 정보입니다.
 * 
 * @author Racin
 * 
 */
public class GameInfo
{
	/*
	 * 여기 있는 필드들은 여러분이 직접 볼 수 없으며 대신 아래에 정의된 getter 메서드들을 사용하여 내용을 읽어올 수 있습니다.
	 * ( 다시 말하면, 여러분이 이 필드의 내용을 수정할 수는 없습니다 )
	 */	
	int gameNumber;
	int currentTurnNumber;
	boolean isDirectInfectionChoosingTurn;
	int directInfectionCountdown;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 게임 정보는 매 게임이 시작될 때 각 플레이어에게 자동으로 주어집니다.
	 */
	GameInfo()
	{
		directInfectionCountdown = -1;
	}

	/**
	 * 현재 진행중인 게임 번호를 가져옵니다.
	 */
	public int GetGameNumber() { return gameNumber; }

	/**
	 * 현재 몇 턴째인지를 나타내는 번호를 가져옵니다.
	 */
	public int GetCurrentTurnNumber() { return currentTurnNumber; }

	/**
	 * 이번 턴이 직접 감염을 선택해야 하는 턴인지 여부를 가져옵니다.
	 * 이 값이 true면서 자신이 생존자인 경우 this.acceptDirectInfection 필드값을 변경함으로써 직접 감염 수락 여부를 선택할 수 있습니다.
	 */
	public boolean GetIsDirectInfectionChoosingTurn() { return isDirectInfectionChoosingTurn; }

	/**
	 * 직접 감염이 적용되기까지 남은 턴 수를 가져옵니다.
	 * 이 값이 -1인 경우 현재 직접 감염이 시작되지 않았음을 의미합니다.
	 */
	public int GetDirectInfectionCountdown() { return directInfectionCountdown; }
}
