package loot;

import java.awt.Canvas;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * 게임 화면에 대한 키보드 / 마우스 입력을 처리하는 클래스입니다. 
 * 
 * @author Racin
 *
 */
public class InputManager
{
	/* -----------------------------------------------
	 * 
	 * 여러분이 사용하게 될 public 요소들이 있는 부분
	 * 
	 */
	
	/**
	 * 게임 내에서 사용되는 버튼 하나의 상태를 나타내는 클래스입니다.<br>
	 * 이 클래스는 여러분이 직접 인스턴스화할 수 없으며<br>
	 * InputManager.buttons 배열을 통해 ID별 각 버튼의 현재 상태를 확인하거나<br>
	 * InputManager.buttons_changed 목록을 통해 이번 프레임에 상태가 바뀐 버튼들만 따로 확인할 수 있습니다.
	 * 
	 * @author Racin
	 *
	 */
	public class ButtonState
	{
		/**
		 * 이 버튼의 일련 번호입니다.<br>
		 * 만약 이 버튼이 아직 bind되지 않았다면 이 값은 -1입니다.
		 */
		public int ID;
		
		/**
		 * 이 버튼이 현재 눌려 있는지 여부를 나타냅니다.<br>
		 * <br>
		 * isPressed와 isChanged의 값을 통해 다음과 같은 사실을 알 수 있습니다:<br>
		 * <br>
		 * isPressed값 | isChanged값  | 의미<br>
		 * --------------------------------------------------<br>
		 * true        | true         | 뗀 상태였는데 이번에 누름<br>
		 * true        | false        | 계속 누르고 있음<br>
		 * false       | true         | 눌렀던 상태였는데 이번에 뗌<br>
		 * false       | false        | 계속 떼어져 있음<br>
		 */
		public boolean isPressed;
		
		/**
		 * 이 버튼이 지난 AcceptInputs() 호출 이후로 상태가 바뀌었는지 여부를 나타냅니다.<br>
		 * <br>
		 * isPressed와 isChanged의 값을 통해 다음과 같은 사실을 알 수 있습니다:<br>
		 * <br>
		 * isPressed값 | isChanged값  | 의미<br>
		 * --------------------------------------------------<br>
		 * true        | true         | 뗀 상태였는데 이번에 누름<br>
		 * true        | false        | 계속 누르고 있음<br>
		 * false       | true         | 눌렀던 상태였는데 이번에 뗌<br>
		 * false       | false        | 계속 떼어져 있음<br>
		 */
		public boolean isChanged;
		
		/**
		 * 이 버튼이 이전까지는 뗀 상태였다가 이번 프레임에 눌렀는지 여부를 반환합니다.<br>
		 * 반환값은 isPressed && isChanged와 동일합니다.
		 */
		public boolean IsPressedNow()
		{
			return isPressed && isChanged;
		}

		/**
		 * 이 버튼이 이전까지는 누른 상태였다가 이번 프레임에 뗐는지 여부를 반환합니다.<br>
		 * 반환값은 isPressed == false && isChanged와 동일합니다.
		 */
		public boolean IsReleasedNow()
		{
			return isPressed == false && isChanged;
		}
		
		/**
		 * 이 필드는 InputManager 내부에서 입력 처리 성능 향상을 위해 사용되며<br>
		 * 여러분은 이 필드를 볼 수 없습니다.
		 */
		private boolean isAcceptedInThisFrame;
		
		/**
		 * ButtonState class는 여러분이 직접 인스턴스화할 수 없습니다.
		 */
		private ButtonState()
		{
			ID = -1;
		}
	}
	
	/**
	 * 키보드 또는 마우스에 bind된 버튼들이 담겨 있는 배열입니다.<br>
	 * ID가 0인 버튼은 이 배열의 0번째 칸에 담겨 있습니다.<br>
	 * 아직 bind되지 않은 버튼의 경우 ID가 -1로 설정되어 있습니다.<br>
	 * <br>
	 * 주의:<br>
	 * GameLoop.Update()의 시작 부분에서<br>
	 * 먼저 AcceptInputs()를 한 번 호출하여<br>
	 * 지난 frame 이후 수행된 실제 키보드 / 마우스 입력이 각 버튼에 반영되도록 해야 합니다. 
	 */
	public ButtonState[] buttons;
	
	/**
	 * 지난 AcceptInputs() 호출 이후로 상태가 바뀐 버튼들의 목록입니다.<br>
	 * <br>
	 * 주의:<br>
	 * GameLoop.Update()의 시작 부분에서<br>
	 * 먼저 AcceptInputs()를 한 번 호출하여<br>
	 * 지난 frame 이후 수행된 실제 키보드 / 마우스 입력이 각 버튼에 반영되도록 해야 합니다. 
	 */
	public ArrayList<ButtonState> buttons_changed;
	
	/**
	 * 현재 게임 화면 상에 있는 마우스 커서의 위치를 나타냅니다.<br>
	 * <br>
	 * 주의:<br>
	 * GameLoop.Update()의 시작 부분에서<br>
	 * 먼저 AcceptInputs()를 한 번 호출하여<br>
	 * 지난 frame 이후 수행된 실제 마우스 이동이 이 필드에 반영되도록 해야 합니다. 
	 */
	public Point pos_mouseCursor;

	/**
	 * 마우스 커서의 위치가 지난 AcceptInputs() 호출 이후로 바뀌었는지 여부를 나타냅니다.<br>
	 * <br>
	 * 주의:<br>
	 * GameLoop.Update()의 시작 부분에서<br>
	 * 먼저 AcceptInputs()를 한 번 호출하여<br>
	 * 지난 frame 이후 수행된 실제 마우스 이동이 이 필드에 반영되도록 해야 합니다. 
	 */
	public boolean isMouseCursorMoved;
	
	
	/**
	 * 새로운 InputManager class의 인스턴스를 생성합니다.
	 * 
	 * @param canvas 여러분이 만들고 있는 게임에서 화면을 그리기 위해 사용하는 Canvas class의 인스턴스를 여기에 넣으세요.
	 * @param numberOfButtons 여러분이 만들고 있는 게임에서 키보드 / 마우스를 모두 포함하여 총 몇 개의 버튼을 사용할 것인지를 여기에 입력하세요.
	 */
	public InputManager(Canvas canvas, int numberOfButtons)
	{
		//public 필드 초기화 부분
		keyBindings_IDtoKeyCode = new int[numberOfButtons];
		for ( int iBinding = 0; iBinding < numberOfButtons; ++iBinding )
			keyBindings_IDtoKeyCode[iBinding] = -1;
		
		mouseBindings_buttonIdxToID = new int[MouseInfo.getNumberOfButtons()];
		for ( int iBinding = 0; iBinding < mouseBindings_buttonIdxToID.length; ++iBinding )
			mouseBindings_buttonIdxToID[iBinding] = -1;

		buttons = new ButtonState[numberOfButtons];
		for ( int iButton = 0; iButton < numberOfButtons; ++iButton )
			buttons[iButton] = new ButtonState();
		
		buttons_changed = new ArrayList<>();
		
		pos_mouseCursor = new Point();
		
		isMouseCursorMoved = false;
		
		//private 필드 초기화 부분
		buttonInputQueue = new ButtonState[length_buttonInputQueue];
		for ( int iQueue = 0; iQueue < length_buttonInputQueue; ++iQueue )
			buttonInputQueue[iQueue] = new ButtonState();
		
		lock_buttonInputQueue = new Object();
		
		pos_lastMouseCursor = new Point();
		
		canvas.addKeyListener(listener_key);
		canvas.addMouseListener(listener_mouse_click);
		canvas.addMouseMotionListener(listener_mouse_move);
	}

	/**
	 * GameLoop.Update()의 시작 부분에서 단 한 번 호출됨으로써<br>
	 * 지난 frame 이후 들어온 키보드 / 마우스 입력을 각 버튼에 반영합니다.<br>
	 * <br>
	 * 주의:<br>
	 * GameLoop.Update()가 아닌 다른 곳에서 이 메서드를 호출하거나<br>
	 * GameLoop.Update() 안에서 이 메서드를 두 번 이상 호출하지 않도록 유의해야 합니다.
	 */
	public void AcceptInputs()
	{
		//변경 및 적용 여부 관련 필드들 초기화
		for ( ButtonState state : buttons )
		{
			state.isChanged = false;
			state.isAcceptedInThisFrame = false;
		}
		
		buttons_changed.clear();
		
		isMouseCursorMoved = false;

		
		//버튼 입력 반영 시작 - 현재 시각 기준으로 마지막 입력이 어디인지 체크해 둠, 이 때 현재 시각 기준 마우스 커서 위치도 백업해 둠 
		int idx_buttonInputQueue_last;
		Point pos_lastMouseCursor_fixed = new Point();
		
		synchronized ( lock_buttonInputQueue )
		{
			idx_buttonInputQueue_last = idx_buttonInputQueue_end;
			pos_lastMouseCursor_fixed.x = pos_lastMouseCursor.x;
			pos_lastMouseCursor_fixed.y = pos_lastMouseCursor.y;
		}
		
		/*
		 * 버튼 입력 반영:
		 * 큐의 시작 위치와 끝 위치가 같을 때(큐의 길이가 0이 될 때)까지 하나 하나 뽑아서 적용.
		 * 단, 같은 버튼에 대해 여러 입력이 들어온 경우 가장 마지막에 들어온 것만 적용.
		 * (성능 향상을 위해 사실상 queue를 stack처럼 다루고 있지만 결과론적 측면에서는 queue 연산이므로 그냥 큐 라고 부름)
		 */
		while ( idx_buttonInputQueue_start != idx_buttonInputQueue_last )
		{
			if ( idx_buttonInputQueue_last == 0 )
				idx_buttonInputQueue_last = length_buttonInputQueue - 1;
			else
				--idx_buttonInputQueue_last;

			//변화를 반영할 버튼 찾기 - 항상 존재
			ButtonState changes = buttonInputQueue[idx_buttonInputQueue_last];
			ButtonState relatedButton = buttons[changes.ID];
			
			//해당 버튼이 이미 변화가 반영된 경우에는 스킵(마지막 변경점만 적용), 그렇지 않은 경우 변화 적용
			if ( relatedButton.isAcceptedInThisFrame == false )
			{
				//만약 버튼의 누름 상태가 바뀐 경우 이를 적용 후 표시, '상태가 바뀐 버튼 목록'에 추가
				if ( relatedButton.isPressed != changes.isPressed )
				{
					relatedButton.isPressed = changes.isPressed;
					relatedButton.isChanged = true;
					
					buttons_changed.add(relatedButton);
				}
				
				//그리고 이 버튼을 '변화가 반영되었음'으로 표시하여 다른(더 오래된) 변경점 적용을 방지
				relatedButton.isAcceptedInThisFrame = true;
			}
		}
		
		//마우스 커서 이동 반영
		if ( pos_mouseCursor.equals(pos_lastMouseCursor_fixed) == false )
		{
			pos_mouseCursor = pos_lastMouseCursor_fixed;
			isMouseCursorMoved = true;
		}
	}

	/**
	 * 해당 키를 주어진 버튼에 bind합니다.<br>
	 * 이미 해당 키가 bind되어 있거나 이미 주어진 버튼이 bind되어 있는 경우 실패합니다.<br>
	 * 성공 여부를 반환합니다.
	 * 
	 * @param keyCode
	 * 			bind할 키에 대한 코드 값입니다.<br>
	 * 			<code>KeyEvent.</code>을 입력하면 목록에 뜨는 <code>VK_LEFT</code>와 같은 값들을 사용하면 됩니다. 
	 * @param buttonID
	 * 			bind할 버튼의 일련 번호입니다.
	 */
	public boolean BindKey(int keyCode, int buttonID)
	{
		//이미 해당 키가 bind되어 있다면 실패
		for ( int code : keyBindings_IDtoKeyCode )
			if ( code == keyCode )
				return false;
		
		//이미 해당 버튼이 bind되어 있다면 실패
		if ( buttons[buttonID].ID != -1 )
			return false;
		
		//bind 수행
		keyBindings_IDtoKeyCode[buttonID] = keyCode;
		buttons[buttonID].ID = buttonID;
		
		return true;
	}
	
	/**
	 * 해당 마우스_버튼을 주어진 버튼에 bind합니다.<br>
	 * 이미 해당 마우스_버튼이 bind되어 있거나 이미 주어진 버튼이 bind되어 있는 경우 실패합니다.<br>
	 * 성공 여부를 반환합니다.
	 * 
	 * @param mouseButtonNumber
	 * 			bind할 마우스_버튼에 대한 번호입니다.<br>
	 * 			<code>MouseEvent.</code>을 입력하면 목록에 뜨는 <code>BUTTON1</code>와 같은 값들을 사용하면 됩니다. 
	 * @param buttonID
	 * 			bind할 버튼의 일련 번호입니다.
	 */
	public boolean BindMouseButton(int mouseButtonNumber, int buttonID)
	{
		//이미 해당 키가 bind되어 있다면 실패
		if ( mouseBindings_buttonIdxToID[mouseButtonNumber] != -1 )
			return false;
		
		//이미 해당 버튼이 bind되어 있다면 실패
		if ( buttons[buttonID].ID != -1 )
			return false;
		
		//bind 수행
		mouseBindings_buttonIdxToID[mouseButtonNumber] = buttonID;
		buttons[buttonID].ID = buttonID;
		
		return true;
	}
	
	/**
	 * 해당 버튼에 대한 bind를 해제합니다.
	 * 
	 * @param buttonID
	 */
	public void Unbind(int buttonID)
	{
		//해당 버튼이 마우스_버튼에 bind되어 있다면 해제
		for ( int iMouseButton = 0; iMouseButton < mouseBindings_buttonIdxToID.length; ++iMouseButton )
			if ( mouseBindings_buttonIdxToID[iMouseButton] == buttonID )
			{
				mouseBindings_buttonIdxToID[iMouseButton] = -1;
				buttons[buttonID].ID = -1;
				return;
			}
		
		//해당 버튼이 특정 키에 bind되어 있다면 해제
		if ( keyBindings_IDtoKeyCode[buttonID] != -1 )
		{
			keyBindings_IDtoKeyCode[buttonID] = -1;
			buttons[buttonID].ID = -1;
		}
	}
		
	
	/* -----------------------------------------------
	 * 
	 * 여러분이 몰라도 될 private 요소들이 있는 부분
	 * 
	 */
	
	/**
	 * 키보드 입력은 종류가 많으므로<br>
	 * 설정한 총 버튼 수만큼 배열을 잡아 둔 다음<br>
	 * 각 버튼의 ID를 index로 삼아 bind된 keyCode를 배열 안에 기록<br>
	 * --> 기록된 값이 -1이면 해당 버튼은 키보드에 매핑되지 않았음을 의미 
	 */
	private int[] keyBindings_IDtoKeyCode;
	
	/**
	 * 마우스 입력은 종류가 적으므로<br>
	 * 마우스에 달린 총 버튼(게임 내에서 쓰는 버튼이 아닌 실제 마우스_버튼) 수만큼 배열을 잡아 둔 다음<br>
	 * 각 마우스_버튼에 bind된 버튼의 ID를 배열 안에 기록<br>
	 * --> 기록된 값이 -1이면 해당 마우스_버튼은 어떤 버튼에도 매핑되지 않았음을 의미
	 */
	private int[] mouseBindings_buttonIdxToID;

	/**
	 * 아래에 있는 큐의 길이<br>
	 * --> 충분히 길게 잡지 않으면 넘칠 수 있음<br>
	 * --> 10FPS 환경이라 하더라도 1/10초 안에 256번 키 입력을 하는 것은 물리적으로 어려울테니 괜찮을 듯
	 */
	private static final int length_buttonInputQueue = 256;
	
	/**
	 * 다음 AcceptInputs()를 호출하기 전까지 들어오는 버튼 입력들을 담아 두기 위한 큐
	 */
	private ButtonState[] buttonInputQueue;

	/**
	 * 큐의 시작 위치 - 이전 AcceptInputs()가 호출된 다음 들어온 가장 첫 버튼 입력의 위치
	 */
	private int idx_buttonInputQueue_start = 0;
	
	/**
	 * 큐의 끝 위치 - 이전 AcceptInputs()가 호출된 다음 들어온 가장 마지막 버튼 입력의 바로 다음 칸 위치<br>
	 * <br>
	 * --> 시작 위치와 끝 위치가 같다면 큐가 비어 있음을 의미<br>
	 * --> 사실 큐를 단순 배열로 구현했기 때문에 큐가 가득 차 버리면 시작 위치와 끝 위치가 같아져버림<br>
	 * --> 그러므로 절대 꽉 차는 일이 없도록 배열을 좀 길게 잡아 둘 필요가 있음
	 */
	private int idx_buttonInputQueue_end;
	
	/**
	 * 멀티스레딩 환경에서 enqueue 작업이 새지 않도록 막기 위한 스레드 잠금용 object
	 */
	private Object lock_buttonInputQueue;

	/**
	 * 이전 AcceptInputs()가 호출된 다음 가장 마지막으로 확인된 마우스 커서 위치
	 */
	private Point pos_lastMouseCursor;
	
	/**
	 * 키보드 입력을 받아 처리하기 위한 이벤트 수신자
	 */
	private KeyListener listener_key = new KeyListener()
	{
		@Override
		public void keyTyped(KeyEvent e) { }
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			ButtonState changes;
			int ID = -1;
			int keyCode = e.getKeyCode();
			
			//해당 키와 매핑된 버튼 탐색
			for ( int iKeyBinding = 0; iKeyBinding < keyBindings_IDtoKeyCode.length; ++iKeyBinding )
				if ( keyBindings_IDtoKeyCode[iKeyBinding] == keyCode )
				{
					ID = iKeyBinding;
					break;
				}
			
			//매핑된 버튼이 없다면 해당 키 입력은 무시
			if ( ID == -1 )
				return;
			
			//입력 큐의 마지막에 버튼 뗌 정보 추가
			synchronized ( lock_buttonInputQueue )
			{
				changes = buttonInputQueue[idx_buttonInputQueue_end];
				changes.ID = ID;
				changes.isPressed = false;
				++idx_buttonInputQueue_end;
				idx_buttonInputQueue_end %= length_buttonInputQueue;
			}
			
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			ButtonState changes;
			int ID = -1;
			int keyCode = e.getKeyCode();
			
			//해당 키와 매핑된 버튼 탐색
			for ( int iKeyBinding = 0; iKeyBinding < keyBindings_IDtoKeyCode.length; ++iKeyBinding )
				if ( keyBindings_IDtoKeyCode[iKeyBinding] == keyCode )
				{
					ID = iKeyBinding;
					break;
				}
			
			//매핑된 버튼이 없다면 해당 키 입력은 무시
			if ( ID == -1 )
				return;
			
			//입력 큐의 마지막에 버튼 뗌 정보 추가
			synchronized ( lock_buttonInputQueue )
			{
				changes = buttonInputQueue[idx_buttonInputQueue_end];
				changes.ID = ID;
				changes.isPressed = true;
				++idx_buttonInputQueue_end;
				idx_buttonInputQueue_end %= length_buttonInputQueue;
			}
		}
	};

	/**
	 * 마우스 클릭 입력을 받아 처리하기 위한 이벤트 수신자
	 */
	private MouseListener listener_mouse_click = new MouseListener()
	{
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			ButtonState changes;
			int ID = -1;
			int buttonNumber = e.getButton();
			
			//해당 마우스_버튼에 매핑된 버튼 확인
			ID = mouseBindings_buttonIdxToID[buttonNumber];

			//매핑된 버튼이 없다면 해당 키 입력은 무시
			if ( ID == -1 )
				return;
			
			//입력 큐의 마지막에 버튼 뗌 정보 추가
			synchronized ( lock_buttonInputQueue )
			{
				changes = buttonInputQueue[idx_buttonInputQueue_end];
				changes.ID = ID;
				changes.isPressed = false;
				++idx_buttonInputQueue_end;
				idx_buttonInputQueue_end %= length_buttonInputQueue;
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			ButtonState changes;
			int ID = -1;
			int buttonNumber = e.getButton();
			
			//해당 마우스_버튼에 매핑된 버튼 확인
			ID = mouseBindings_buttonIdxToID[buttonNumber];

			//매핑된 버튼이 없다면 해당 키 입력은 무시
			if ( ID == -1 )
				return;
			
			//입력 큐의 마지막에 버튼 누름 정보 추가
			synchronized ( lock_buttonInputQueue )
			{
				changes = buttonInputQueue[idx_buttonInputQueue_end];
				changes.ID = ID;
				changes.isPressed = true;
				++idx_buttonInputQueue_end;
				idx_buttonInputQueue_end %= length_buttonInputQueue;
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) { }
		
		@Override
		public void mouseEntered(MouseEvent e) { }
		
		@Override
		public void mouseClicked(MouseEvent e) { }
	};
	
	/**
	 * 마우스 이동을 받아 처리하기 위한 이벤트 수신자
	 */
	private MouseMotionListener listener_mouse_move = new MouseMotionListener()
	{
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			//새로운 좌표를 기억
			pos_lastMouseCursor = e.getPoint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			//새로운 좌표를 기억
			pos_lastMouseCursor = e.getPoint();			
		}
	};
}
