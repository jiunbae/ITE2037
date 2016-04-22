package loot;

/**
 * 게임을 실행하기 전에 초기화 작업을 수행하는 Initialize(),<br>
 * 게임을 진행하면서 매 프레임마다 반복적으로 실행하게 될 Update()와 Draw()를 구현하기 위한 인터페이스입니다.<br>
 * 
 * @author Racin
 *
 */
public interface IGameLoopMethods
{
	/**
	 * 게임 루프를 시작하기 전에 한 번 호출되는 메서드입니다.<br>
	 * Initialize()는 필요한 이미지, 데이터 등을 읽어오거나 마우스 / 키보드 관련 필드들을 초기화하는 용도로 사용하세요.
	 * 
	 * @return 초기화 성공 여부를 반환합니다. false를 반환하면 게임 루프에 진입하지 않습니다.
	 */
	boolean Initialize();

	/**
	 * 게임 루프의 한 프레임을 구성하는 메서드입니다.<br>
	 * Update()는 시간의 흐름에 따라 발생하는 여러 데이터들의 변화를 반영하고<br>
	 * 사용자의 입력을 처리하는 용도로 사용하세요.
	 * 
	 * @param timeStamp
	 *            게임 루프가 시작된 이후 지난 시각을 밀리초 단위로 나타내는 값입니다.
	 * @return 이번 프레임에서 Draw()를 호출할 것인지 여부를 반환합니다. false를 반환하면 Draw()가 호출되지 않습니다.
	 */
	boolean Update(long timeStamp);

	/**
	 * 게임 루프의 한 프레임을 구성하는 메서드입니다.<br>
	 * Draw()는 Update()에서 발생한 변화를 토대로 화면을 새로 그리는 용도로 사용하세요.
	 * 
	 * @param timeStamp
	 *            게임 루프가 시작된 이후 지난 시각을 밀리초 단위로 나타내는 값입니다.
	 */
	void Draw(long timeStamp);
}
