package loot;

import java.awt.Color;
import java.util.HashMap;

/**
 * GameFrame class 또는 그 하위 클래스들에 대한 세부 설정을 수행하기 위해 사용하는 클래스입니다.
 * 
 * @author Racin
 *
 */
public class GameFrameSettings
{
	/**
	 * 초기에 설정할 창 제목입니다.<br>
	 * setTitle()을 통해 변경하지 않는 경우 창 제목은 이 필드의 값으로 계속 유지됩니다.<br>
	 * 기본값은 "개발중!" 입니다.
	 */
	public String window_title = "개발중!";
	
	/**
	 * 게임 화면의 가로 길이(픽셀 수)입니다.<br>
	 * 기본값은 800입니다. 
	 */
	public int canvas_width = 800;
	
	/**
	 * 게임 화면의 세로 길이(픽셀 수)입니다.<br>
	 * 기본값은 600입니다.
	 */
	public int canvas_height = 600;

	/**
	 * 게임 화면의 배경색입니다.<br>
	 * 기본값은 <code>Color.WHITE</code>입니다.
	 */
	public Color canvas_backgroundColor = Color.WHITE;

	/**
	 * 게임을 진행하는 각 프레임 사이의 간격을 나노초 단위로 설정합니다.<br>
	 * 기본값인 16666666ns의 경우 FPS(초당 프레임 수)로 환산하면 약 60이 됩니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * 이 값은 절대적이지 않으며 성능은 실행 환경에 따라 달라질 수도 있습니다.<br>
	 * 특히 게임 루프를 실제 타이밍 모드로 동작시킬 때는<br>
	 * 여러분의 게임을 만들어 가는 과정에서 이 값을 수시로 보정하여<br>
	 * 프로그램이 원하는 속도로 실행되도록 도모할 필요가 있습니다.
	 */
	public long gameLoop_interval_ns = 16666666;
	
	/**
	 * 게임 루프를 가상 타이밍 모드로 동작시킬지 여부를 설정합니다.<br>
	 * 이 값이 true 인 경우 매 프레임이 수행 시간과 관계 없이 항상 interval_ms만큼 지연되며 timeStamp 또한 항상 interval_ms만큼 증가합니다.<br>
	 * 이 값이 false 인 경우 매 프레임이 최소 interval_ms만큼 지연됨을 보장하며 timeStamp는 실제 시간에 기반하여 증가합니다.<br>
	 * 기본값은 true입니다.
	 */
	public boolean gameLoop_use_virtualTimingMode = true;
	
	/**
	 * 게임 내에서 총 몇 개의 가상 버튼을 bind하여 사용할 것인지 설정합니다.<br>
	 * 기본값은 8입니다.
	 */
	public int numberOfButtons = 8;
	
	public HashMap<String, String> path = new HashMap<String, String>();
	
	public GameFrameSettings()
	{
	}
	
	public GameFrameSettings(GameFrameSettings other)
	{
		window_title = other.window_title;
		canvas_width = other.canvas_width;
		canvas_height = other.canvas_height;
		canvas_backgroundColor = other.canvas_backgroundColor;
		gameLoop_interval_ns = other.gameLoop_interval_ns;
		gameLoop_use_virtualTimingMode = other.gameLoop_use_virtualTimingMode;
		numberOfButtons = other.numberOfButtons;
	}
}
