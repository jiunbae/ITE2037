package loot;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

import javax.swing.JFrame;

/**
 * LOOT 라이브러리의 각 요소를 사용하며 창 전체를 게임 화면으로 삼아 동작하는<br>
 * 게임 창 하나를 나타내는 클래스입니다.<br>
 * <br>
 * TODO 이 클래스를 사용하기 전에 GameFrame.java 파일을 열고 이 클래스에 들어 있는 각 필드 / 메서드를 한 번 살펴보세요.
 * 
 * @author Racin
 *
 */
@SuppressWarnings("serial")
public abstract class GameFrame extends JFrame implements IGameLoopMethods
{
	
	/* ----------------------------------------------
	 * 
	 * 게임 창을 구성하는 필드 선언 부분
	 * 
	 */
	
	/**
	 * 게임 창을 설정하기 위한 여러 값들이 들어 있습니다.<br>
	 * 자세한 내용은 GameFrameSettings.java 파일을 참고하세요.
	 */
	protected GameFrameSettings settings;
	
	/**
	 * 실질적인 게임 화면을 그리기 위해 사용됩니다.<br>
	 * 아마도 여러분이 이 필드를 직접 사용할 일은 없을 것입니다.
	 */
	protected Canvas canvas;
	
	/**
	 * 다루기 힘든 키보드 / 마우스 입력을 게임 내에서 사용하는 가상 버튼으로 매핑하고<br>
	 * 각 버튼의 현재 상태를 추적 및 관리해 줍니다.<br>
	 * 자세한 사용법은 <code>input.</code>을 입력하면 나오는 각 필드 / 메서드 설명을 참고하세요.
	 */
	protected InputManager inputs;

	/**
	 * 게임 화면을 그리기 위해 필요한 Image들을 그림 파일에서 미리 읽어와 손쉽게 사용할 수 있게 해 줍니다.<br>
	 * <br>
	 * 자세한 사용법은 <code>images.</code>을 입력하면 나오는 각 필드 / 메서드 설명을 참고하세요.
	 */
	protected ImageResourceManager images;
	
	/**
	 * 게임 내에서 사용할 음향 효과들을 소리 파일에서 미리 읽어 오고 재생해 줍니다.<br>
	 * <br>
	 * 자세한 사용법은 <code>audios.</code>을 입력하면 나오는 각 필드 / 메서드 설명을 참고하세요.
	 */
	protected AudioManager audios;
	
	/**
	 * 게임을 일정 시간 간격으로 진행시키기 위해 사용됩니다.<br>
	 * 아마도 여러분이 이 필드를 직접 사용할 일은 없을 것입니다.
	 */
	protected GameLoop loop;
	
	
	/* ----------------------------------------------
	 * 
	 * 여러분이 게임 화면을 그릴 때 사용할 필드 / 메서드들
	 * 
	 */
	
	/**
	 * <b>주의:</b><br>
	 * 이 필드는 여러분의 Draw() 내에서만 사용되어야 하며<br>
	 * 이 때 반드시 Draw() 시작 부분에서 BeginDraw()를,<br>
	 * 끝 부붙에서 EndDraw()를 호출해 주어야 합니다.<br>
	 * <br>
	 * 게임 화면을 그릴 때 사용할 '붓'이 되는 요소입니다.<br>
	 * BeginDraw()를 호출하면 이 필드에 적절한 인스턴스가 할당되며<br>
	 * 이후 <code>g.</code>를 입력하여 수동으로 그리기 작업을 수행할 수 있습니다.<br>
	 * 또는, DrawObject() 등을 호출하여 특정 요소를 간편하게 그릴 때도 이 필드가 사용됩니다.
	 */
	protected Graphics2D g;
	
	/**
	 * 주의: 이 메서드는 여러분이 작성하는 Draw()의 시작 부분에서 반드시 호출되어야 합니다.<br>
	 * <br>
	 * 게임 화면 그리기 작업을 시작합니다.
	 */
	public void BeginDraw()
	{
		if ( g == null )
		{
			buf = canvas.getBufferStrategy();
			g = (Graphics2D)buf.getDrawGraphics();
			g.setFont(currentFont);
			g.setColor(currentColor);
		}
	}
	
	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 여러분의 Draw() 내에서만 사용되어야 하며<br>
	 * 이 때 반드시 Draw() 시작 부분에서 BeginDraw()를,<br>
	 * 끝 부붙에서 EndDraw()를 호출해 주어야 합니다.<br>
	 * <br>
	 * 전체 화면을 지우고 배경색으로 다시 칠합니다.<br>
	 * 여러분이 작성하는 Draw()에서 BeginDraw()를 호출한 다음에 바로 이 메서드를 실행하면<br>
	 * 이전에 그려 둔 내용을 모두 지우고 새 그림을 그릴 수 있게 됩니다.<br>
	 * 배경색 설정은 GameFrameSettings class 안에 있는 canvas_backgroundColor 필드를 통해 할 수 있습니다.
	 */
	public void ClearScreen()
	{
		g.clearRect(0, 0, settings.canvas_width, settings.canvas_height);
	}

	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 게임이 시작되기 전에 Initialize()에서 미리 호출될 수 있도록 설계되었으며<br>
	 * Draw()에서 '지금 사용할 색 변경'을 하려면 대신 SetColor()를 사용해야 합니다.<br>
	 * <br>
	 * 다음 프레임부터 그림 그리기에 사용할 색을 지정합니다.<br>
	 * 이 색은 <code>g.</code>을 입력하면 나오는 draw...()와 fill...()에서 사용됩니다.<br>
	 * 예: drawRect()를 호출하면 현재 지정된 색으로 사각형을 그릴 수 있습니다.
	 * 
	 * @param newColor 지정할 색입니다. 보통은 <code>Color.</code>을 입력하면 나오는 색상 중 하나를 사용합니다.
	 */
	public void LoadColor(Color newColor)
	{
		currentColor = newColor;
	}
	
	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 여러분의 Draw() 내에서만 사용되어야 하며<br>
	 * 이 때 반드시 Draw() 시작 부분에서 BeginDraw()를,<br>
	 * 끝 부붙에서 EndDraw()를 호출해 주어야 합니다.<br>
	 * <br>
	 * 그림 그리기에 사용할 색을 설정합니다.<br>
	 * 이 색은 <code>g.</code>을 입력하면 나오는 draw...()와 fill...()에서 사용됩니다.<br>
	 * 예: drawRect()를 호출하면 현재 지정된 색으로 사각형을 그릴 수 있습니다.
	 * 
	 * @param newColor 지정할 색입니다. 보통은 <code>Color.</code>을 입력하면 나오는 색상 중 하나를 사용합니다.
	 */
	public void SetColor(Color newColor)
	{
		g.setColor(newColor);
		currentColor = newColor;
	}
	
	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 게임이 시작되기 전에 Initialize()에서 미리 호출될 수 있도록 설계되었으며<br>
	 * Draw()에서 '지금 사용할 글자체 변경'을 하려면 대신 SetFont()를 사용해야 합니다.<br>
	 * <br>
	 * 주어진 이름의 글자체를 게임 내에서 사용할 수 있도록 가져옵니다.<br>
	 * 가져온 글자체는 '현재 글자체'로 지정되며<br>
	 * <code>g.drawString()</code> 또는 this.DrawString()으로 문자열을 출력할 때 사용됩니다.<br>
	 * <br>
	 * 이 때 글자체의 크기와 스타일을 지정할 수 있습니다.<br>
	 * 크기와 스타일을 지정하려면 fontName을 "[이름] [스타일] [크기]"로 설정하면 됩니다.<br>
	 * 예: 14pt 굵은 굴림체를 사용하려는 경우 fontName을 "굴림체 BOLD 14"로 설정<br>
	 * <br>
	 * 스타일은 PLAIN, BOLD, ITALIC, BOLDITALIC 중 하나를 선택할 수 있으며 생략하면 PLAIN으로 간주됩니다.<br>
	 * 크기는 양수 범위 내에서 자유롭게 선택할 수 있으며 생략하면 12로 간주됩니다.
	 * 
	 * @param fontName
	 * 			가져올 글자체의 이름입니다. 필요한 경우 글자체의 크기와 스타일을 지정할 수도 있습니다.
	 * @return 글자체를 성공적으로 가져왔는지 여부를 반환합니다.
	 */
	public boolean LoadFont(String fontName)
	{
		//아직 해당 이름의 글자체를 가져온 적이 없다면 지금 시도
		if ( fonts.containsKey(fontName) == false )
		{
			Font newFont = Font.decode(fontName);
			
			//해당 이름의 글자체가 없다면 실패
			if ( newFont == null )
			{
				System.err.println("Error. 이름이 " + fontName + "인 글자체가 존재하지 않습니다.");
				return false;
			}
			
			//로드한 글자체를 목록에 추가
			fonts.put(fontName, newFont);
		}
		
		//가져온 글자체를 현재 글자체로 설정
		currentFont = fonts.get(fontName);
		return true;
	}
	
	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 여러분의 Draw() 내에서만 사용되어야 하며<br>
	 * 이 때 반드시 Draw() 시작 부분에서 BeginDraw()를,<br>
	 * 끝 부붙에서 EndDraw()를 호출해 주어야 합니다.<br>
	 * <br>
	 * 지금 사용할 글자체를 주어진 이름의 글자체로 설정합니다.<br>
	 * 만약 해당 이름의 글자체가 아직 준비되지 않았다면 설정하기 전에 먼저 가져오기 작업을 수행합니다.<br>
	 * 이 작업은 오래 걸릴 수 있으므로(게임 내에서 '랙'을 유발할 수 있으므로<br>
	 * 필요한 글자체는 미리 LoadFont()를 사용하여 가져와 두는 편이 좋겠습니다.<br>
	 * 이 때 fontName은 LoadFont()에서 설정했던 이름과 동일해야 합니다.<br>
	 * 헷갈리지 않도록 <code>static final String font_dotUmChe = "돋움체"</code>와 같이<br>
	 * 별도의 필드를 만들어 사용하면 좋겠습니다.<br>  
	 * <br>
	 * fontName을 통해 글자체의 크기와 스타일을 지정할 수 있습니다.<br>
	 * 크기와 스타일을 지정하려면 fontName을 "[이름] [스타일] [크기]"로 설정하면 됩니다.<br>
	 * 예: 14pt 굵은 굴림체를 사용하려는 경우 fontName을 "굴림체 BOLD 14"로 설정<br>
	 * <br>
	 * 스타일은 PLAIN, BOLD, ITALIC, BOLDITALIC 중 하나를 선택할 수 있으며 생략하면 PLAIN으로 간주됩니다.<br>
	 * 크기는 양수 범위 내에서 자유롭게 선택할 수 있으며 생략하면 12로 간주됩니다.
	 * 
	 * @param fontName
	 * 		설정할 글자체의 이름입니다. 이 값은 LoadFont()를 통해 지정한 값과 동일해야 합니다.
	 */
	public boolean SetFont(String fontName)
	{
		//해당 이름의 글자체를 이미 가져왔거나 지금 가져올 수 있다면 현재 글자체로 설정
		if ( LoadFont(fontName) == true )
		{
			g.setFont(currentFont);
			return true;
		}
		
		//해당 이름의 글자체가 없다면 실패
		return false;
	}
	
	/**
	 * <b>주의:</b><br>
	 * 이 메서드는 여러분의 Draw() 내에서만 사용되어야 하며<br>
	 * 이 때 반드시 Draw() 시작 부분에서 BeginDraw()를,<br>
	 * 끝 부붙에서 EndDraw()를 호출해 주어야 합니다.<br>
	 * <br>
	 * 주어진 양식에 따라 생성된 문자열을 게임 화면의 해당 위치에 그립니다.<br>
	 * 생성된 문자열은<br>
	 * LoadColor() 또는 SetColor()를 통해 지정된 색과<br>
	 * LoadFont() 또는 SetFont()를 통해 지정된 글자체를 사용하여 그려집니다.<br>
	 * 
	 * @param left 문자열을 그리기 시작할 위치(첫 글자의 왼쪽 모서리)를 나타내는 x좌표입니다.
	 * @param bottom 문자열을 그리기 시작할 위치(첫 글자의 아랫 모서리)를 나타내는 y좌표입니다.
	 * @param format 문자열을 생성할 양식입니다. 사용법은 <code>printf()</code>와 같습니다. 단, \n은 사용할 수 없습니다.
	 * @param args 양식에 따라 문자열에 포함될 요소들의 목록입니다. 사용법은 <code>printf()</code>와 같습니다.
	 */
	public void DrawString(int left, int bottom, String format, Object... args)
	{
		g.drawString(String.format(format, args), left, bottom);
	}

	/**
	 * 주의: 이 메서드는 여러분이 작성하는 Draw()의 끝 부분에서 반드시 호출되어야 합니다.<br>
	 * <br>
	 * 게임 화면 그리기 작업을 종료하고 화면을 갱신합니다.
	 */
	public void EndDraw()
	{
		if ( g != null )
		{
			g.clipRect(0, 0, settings.canvas_width, settings.canvas_height);
			buf.show();
			g.dispose();
			g = null;
		}
	}
	
	/* ----------------------------------------------
	 * 
	 * 여러분이 몰라도 될 요소들이 있는 부분
	 * 
	 */
	
	/**
	 * 새로운 GameFrame class의 인스턴스를 생성합니다.
	 * 
	 * @param settings
	 * 			  게임 창을 설정하기 위한 여러 값들이 들어 있습니다.<br>
	 * 			  자세한 내용은 GameFrameSettings.java 파일을 참고하세요.
	 */
	public GameFrame(GameFrameSettings settings)
	{
		this.settings = new GameFrameSettings(settings);
		
		//Frame 기본 설정
		setTitle(this.settings.window_title);		//창 제목을 주어진 문자열로 설정 
		setDefaultCloseOperation(EXIT_ON_CLOSE);	//창을 닫으면 프로그램 전체가 종료되도록 설정
		setLocationByPlatform(true);				//창의 초기 위치를 OS가 알아서 정해 주도록 설정
		setResizable(false);						//창의 크기를 사용자가 직접 바꿀 수 없도록 설정
		setIgnoreRepaint(true);						//창의 '자동 다시 그리기' 옵션을 해제 -> 항상 Draw()를 통해 수동으로 다시 그림
		
		//Canvas(게임 화면) 설정
		canvas = new Canvas();
		canvas.setSize(settings.canvas_width, settings.canvas_height);	//게임 화면의 크기를 주어진 값으로 설정
		canvas.setBackground(settings.canvas_backgroundColor);			//게임 화면의 배경색을 주어진 값으로 설정
		add(canvas);													//게임 화면을 창에 추가
		pack();															//창의 크기를 게임 화면 크기에 맞게 변경
		canvas.createBufferStrategy(2);									//게임 화면이 더블 버퍼링을 지원하도록 설정

		//기타 요소 설정
		
		inputs = new InputManager(canvas, settings.numberOfButtons);
		setFocusable(false);			//창이 직접 입력을 받을 수는 없도록 설정
		canvas.setFocusable(true);		//(창 대신) 게임 화면이 직접 입력을 받도록 설정

		images = new ImageResourceManager();
		audios = new AudioManager();
		
		loop = new GameLoop(settings.gameLoop_use_virtualTimingMode, settings.gameLoop_interval_ns, this);
		fonts = new HashMap<>();

		//Frame 이벤트 수신기 설정
		addWindowListener(listener_window_activated);	//창이 처음 열렸을 때 바로 게임이 실행되도록 구성한 수신기를 창에 연결(listener_window_activated는 파일 하단에 있음)
	}
	
	/**
	 * 게임 화면의 이중 버퍼링 기능을 사용하기 위해 필요한 필드입니다.
	 */
	private BufferStrategy buf;

	/**
	 * 현재 지정된 색입니다.<br>
	 * BeginDraw()를 호출할 때 여기 지정된 색이 자동으로 '지금 그릴 색'으로 설정됩니다.<br>
	 * 이 필드의 값을 바꾸려면 LoadColor() 또는 SetColor()를 사용하세요.
	 */
	private Color currentColor;
	
	/**
	 * 현재 지정된 글자체입니다.<br>
	 * BeginDraw()를 호출할 때 여기 지정된 글자체가 자동으로 '지금 사용할 글자체'로 설정됩니다.<br>
	 * 이 필드의 값을 바꾸려면 LoadFont() 또는 SetFont()를 사용하세요.
	 */
	private Font currentFont;
	
	/**
	 * 현재까지 가져온 글자체 목록입니다.<br>
	 * 글자체 가져오기 작업은 시간이 꽤 오래 걸리므로<br>
	 * 한 번 가져온 글자체는 여기에 담아 둔 다음 계속 재사용하게 됩니다.
	 */
	private HashMap<String, Font> fonts;
	
	/**
	 * 창이 처음 열렸을 때 바로 게임이 실행되도록 구성하기 위한 이벤트 수신기입니다.<br>
	 * 필드 선언이 갑자기 중괄호를 열고 그 안에 메서드를 정의하고 있지만(익명 클래스 정의)<br>
	 * 크게 신경쓰지는 않아도 좋습니다.
	 */
	private WindowListener listener_window_activated = new WindowListener()
	{
		boolean isFirstActivation = false;
		
		public void windowOpened(WindowEvent e) { }
		public void windowIconified(WindowEvent e) { }
		public void windowDeiconified(WindowEvent e) { }
		public void windowDeactivated(WindowEvent e) { }
		public void windowClosing(WindowEvent e) { }
		public void windowClosed(WindowEvent e) { }
		
		/**
		 * 창이 활성화되었을 때 실행되는 메서드입니다.<br>
		 * '활성화'는 처음 열렸거나 Alt+Tab 등을 통해 다시 바탕 화면 위로 올라왔을 때 발생합니다.<br>
		 * 하지만 역시 크게 신경쓰지 않아도 좋습니다.<br>
		 * <br>
		 * 게임 시작을 좀 천천히 하고 싶은 경우에는<br>
		 * 이 메서드를 건드려서 게임 루프 시작을 미루기보다는 '시작 버튼'을 만들어 배치하는 것을 고려해 보세요.
		 */
		@Override
		public void windowActivated(WindowEvent e)
		{
			//만약 이번이 첫 활성화라면
			if ( isFirstActivation == false )
			{
				//이미 첫 활성화가 되었다고 표시하고 게임 루프 시작
				isFirstActivation = true;
				loop.Start();
			}
		}
	};
}
