package loot.graphics;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 * 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에 위치하여 눈으로 직접 볼 수 있는 요소 하나를 추상화합니다.
 * 
 * @author Racin
 *
 */
public abstract class VisualObject extends NamedObject
{
	/**
	 * 이 요소가 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에 위치할 x좌표(width가 양수인 경우 가장 왼쪽 픽셀의 좌표)입니다.<br>
	 * 2차원 평면에서 x축은 왼쪽에서 오른쪽으로 진행합니다.<br>
	 * 이 요소가 Layer 또는 Viewport에 포함되어 있는 경우 이 값은 그리기 과정에서 변경될 수 있습니다.
	 */
	public int x;
	
	/**
	 * 이 요소가 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에 위치할 y좌표(height가 양수인 경우 가장 윗 픽셀의 좌표)입니다.<br>
	 * 2차원 평면에서 y축은 <b>위에서 아래로</b> 진행합니다.<br>
	 * 이 요소가 Layer 또는 Viewport에 포함되어 있는 경우 이 값은 그리기 과정에서 변경될 수 있습니다.
	 */
	public int y;
	
	/**
	 * 이 요소가 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에서 차지할 가로 길이(픽셀 수)입니다.<br>
	 * 이 값이 음수면 요소는 좌/우 반전되어 보이게 됩니다.<br>
	 * 이 요소가 Layer 또는 Viewport에 포함되어 있는 경우 이 값은 그리기 과정에서 변경될 수 있습니다.
	 */
	public int width;
	
	/**
	 * 이 요소가 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에서 차지할 세로 길이(픽셀 수)입니다.<br>
	 * 이 값이 음수면 요소는 상/하 반전되어 보이게 됩니다.<br>
	 * 이 요소가 Layer 또는 Viewport에 포함되어 있는 경우 이 값은 그리기 과정에서 변경될 수 있습니다.
	 */
	public int height;
	
	/**
	 * 이 필드를 true로 설정해 두면 Layer는 이 요소를 게임 화면에 그리지 않습니다.<br>
	 * 만약 이 요소가 어떤 Layer에도 포함되어 있지 않는 경우 이 필드는 아무 영향도 주지 않습니다.
	 */
	public boolean trigger_hide;
	
	/**
	 * 이 필드를 true로 설정해 두면 Layer는 이 요소를 적중 테스트 과정에서 무시합니다.<br>
	 * 만약 이 요소가 어떤 Layer에도 포함되어 있지 않는 경우 이 필드는 아무 영향도 주지 않습니다.
	 */
	public boolean trigger_ignoreDuringHitTest;
	
	/**
	 * 이 필드를 true로 설정해 두면 Layer는 이 요소를 내부 목록에서 제거합니다.<br>
	 * 만약 이 요소가 어떤 Layer에도 포함되어 있지 않는 경우 이 필드는 아무 영향도 주지 않습니다.
	 */
	public boolean trigger_remove;
	
	// NamedObject
	public VisualObject(String name) {
		super(name);
	}
	
	public VisualObject()
	{
	}

	public VisualObject(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public VisualObject(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public VisualObject(VisualObject other)
	{
		x = other.x;
		y = other.y;
		width = other.width;
		height = other.height;
		trigger_hide = other.trigger_hide;
	}
	
	/**
	 * 요소를 화면에 그립니다.<br>
	 * 이 메서드는 각 클래스들마다 다르게 구현되어 있으며<br>
	 * 어쨋든 요소를 화면에 그립니다.
	 * 
	 * @param g
	 * 		GameFrame에는 g 라는 필드가 들어 있습니다.<br>
	 * 		여러분이 Draw(g)를 직접 호출할 때(게임 화면에 직접 그릴 때)는 더 고민하지 말고 그 필드를 그냥 넣으면 됩니다.
	 */
	public abstract void Draw(Graphics2D g);

	
	/* ---------------------------------------------------
	 * 
	 * 적중 테스트를 위한 메서드들
	 * 
	 */
	
	/**
	 * 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.
	 * 
	 * @param x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	public boolean HitTest(int x, int y)
	{
		return	this.x <= x && this.x + width >= x &&
				this.y <= y && this.y + height >= y;
	}
	
	/**
	 * 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.
	 * 
	 * @param pos 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final boolean HitTest(Point pos)
	{
		return HitTest(pos.x, pos.y);
	}

	/**
	 * 이 메서드는 VisualObject.HitTest()와 정확히 동일한 작업을 수행합니다.<br>
	 * 하지만 클래스 계층이 여러 단계에 걸쳐 형성되었을 때 어떤 하위 클래스든 가장 단순한 범위 기반 적중 테스트(여기서 하는 작업)를 할 수 있도록<br>
	 * 내부적으로 이러한 '다른 이름의' 메서드를 만들어 사용합니다.<br>
	 * 어쨋든 여러분은 이 메서드를 사용할 수 없습니다.
	 * 
	 * @param x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	protected final boolean HitTest_Base(int x, int y)
	{
		return	this.x <= x && this.x + width >= x &&
				this.y <= y && this.y + height >= y;
	}

	
	/* ---------------------------------------------------
	 * 
	 * 상대좌표 계산 및 좌표 변환을 위한 메서드들
	 * 
	 */
	
	/**
	 * 해당 좌표의 이 요소를 기준으로 하는 상대좌표를 반환합니다.
	 * 
	 * @param x 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	public Point GetRelativePosition(int x, int y)
	{
		return new Point(x - this.x, y - this.y);
	}
	
	/**
	 * 해당 좌표의 이 요소를 기준으로 하는 상대좌표를 반환합니다.
	 * 
	 * @param pos 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final Point GetRelativePosition(Point pos)
	{
		return GetRelativePosition(pos.x, pos.y);
	}
	
	/**
	 * 이 요소의 중심점(너비의 절반, 높이의 절반 위치에 있는 점)을 기준으로 하는 해당 좌표의 상대좌표를 반환합니다.
	 * 
	 * @param x 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	public final Point GetRelativePositionFromCenter(int x, int y)
	{
		return GetRelativePosition(x - width / 2, y - height / 2);
	}
	
	/**
	 * 이 요소의 중심점(너비의 절반, 높이의 절반 위치에 있는 점)을 기준으로 하는 해당 좌표의 상대좌표를 반환합니다.
	 * 
	 * @param pos 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final Point GetRelativePositionFromCenter(Point pos)
	{
		return GetRelativePosition(pos.x - width / 2, pos.y - height / 2);
	}
}