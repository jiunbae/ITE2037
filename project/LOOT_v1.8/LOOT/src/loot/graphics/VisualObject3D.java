package loot.graphics;

import java.awt.Point;

/**
 * 2차원 평면 내(게임 화면 자체, 또는 Layer 안) 또는 3차원 공간 내(Viewport 안)에 위치하여 눈으로 직접 볼 수 있는 요소 하나를 추상화합니다.<br>
 * 참고: 이 클래스의 인스턴스는 2차원용, 또는 3차원용으로 활용할 수 있으며 이는 trigger_coordination 값에 따라 달라집니다.<br>
 * trigger_coordination 값은 생성자를 호출할 때 적절한 값으로 자동 설정되므로<br>
 * 여러분이 원하는 동작을 수행하도록 적절한 생성자를 호출하면 되겠습니다.
 * 
 * @author Racin
 *
 */
public abstract class VisualObject3D extends VisualObject
{
	/**
	 * 이 요소가 3차원 공간 내(Viewport 안)에 위치할 중심점의 x좌표입니다.<br>
	 * 3차원 공간에서 x축은 왼쪽에서 오른쪽으로 진행합니다.<br> 
	 * this.trigger_coordination 값이 true인 경우 이 값을 토대로 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.  
	 */
	public double pos_x;
	
	/**
	 * 이 요소가 3차원 공간 내(Viewport 안)에 위치할 중심점의 y좌표입니다.<br>
	 * 3차원 공간에서 y축은 <b>아래에서 위로</b> 진행합니다.<br> 
	 * this.trigger_coordination 값이 true인 경우 이 값을 토대로 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.  
	 */
	public double pos_y;
	
	/**
	 * 이 요소가 3차원 공간 내(Viewport 안)에 위치할 중심점의 z좌표입니다.<br>
	 * 3차원 공간에서 z축은 화면 깊은 곳에서 내 눈 앞을 향하는 방향으로 진행합니다.<br> 
	 * this.trigger_coordination 값이 true인 경우 이 값을 토대로 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.  
	 */
	public double pos_z;
	
	/**
	 * 이 요소가 3차원 공간 내(Viewport 안)에서 중심점을 기준으로 좌/우로 차지할 x방향 반지름(<b>너비의 절반</b>)입니다.<br>
	 * this.trigger_coordination 값이 true인 경우 이 값을 토대로 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.  
	 */
	public double radius_x;

	/**
	 * 이 요소가 3차원 공간 내(Viewport 안)에서 중심점을 기준으로 상/하로 차지할 y방향 반지름(<b>높이의 절반</b>)입니다.<br>
	 * this.trigger_coordination 값이 true인 경우 이 값을 토대로 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.  
	 */
	public double radius_y;
	
	/**
	 * 이 필드를 true로 설정해 두면 Viewport가 이 요소를 그릴 위치와 크기를 지정합니다.<br>
	 * <br>
	 * 이 필드를 false로 설정해 두면 이 요소는 x, y, width, height값을 그대로 사용하여 그리기 작업을 수행합니다.<br>
	 * 이 경우 이 요소는 VisualObject class의 인스턴스와 동일한 방식으로 처리됩니다.<br>
	 * <br>
	 * 참고:<br>
	 * 요소를 생성할 때 2차원 좌표값(int 형식)을 argument로 하여 생성하여 호출한 경우 이 필드는 자동으로 false가 되며<br>
	 * 3차원 좌표값(double 형식)을 argument로 하여 호출한 경우 자동으로 true가 됩니다.<br>
	 * 어쨋든, 대부분의 경우 이 필드의 값은 생성 순간 이외에는 크게 신경쓰지 않아도 될 것입니다.
	 */
	public boolean trigger_coordination;

	// NamedObject
	public VisualObject3D(String name) {
		super(name);
	}

	/**
	 * 이 생성자는 argument를 통해 새 요소를 2차원 평면에 놓을지 3차원 공간에 놓을지 결정합니다.
	 */
	public VisualObject3D(boolean trigger_coordination)
	{
		this.trigger_coordination = trigger_coordination;
	}
	
	/**
	 * 이 생성자는 새 요소를 2차원 평면에 놓일 요소로 간주합니다.
	 */
	public VisualObject3D(int width, int height)
	{
		super(width, height);
	}

	/**
	 * 이 생성자는 새 요소를 2차원 평면에 놓일 요소로 간주합니다.
	 */
	public VisualObject3D(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 놓일 요소로 간주합니다.
	 */
	public VisualObject3D(double radius_x, double radius_y)
	{
		this.radius_x = radius_x;
		this.radius_y = radius_y;
		this.trigger_coordination = true;
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 놓일 요소로 간주합니다.
	 */
	public VisualObject3D(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y)
	{
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.pos_z = pos_z;
		this.radius_x = radius_x;
		this.radius_y = radius_y;
		this.trigger_coordination = true;
	}

	/**
	 * 이 생성자는 원본 요소 설정에 따라 새 요소를 2차원 평면에 놓을지 3차원 공간에 놓을지 결정합니다.
	 */
	public VisualObject3D(VisualObject3D other)
	{
		super(other);
		pos_x = other.pos_x;
		pos_y = other.pos_y;
		pos_z = other.pos_z;
		radius_x = other.radius_x;
		radius_y = other.radius_y;
		trigger_coordination = other.trigger_coordination;
	}
	
	
	/* ---------------------------------------------------
	 * 
	 * 적중 테스트를 위한 메서드들
	 * 
	 */
	
	public boolean HitTest3D(VisualObject3D object) {
		return HitTest3D(object.pos_x, object.pos_y, object.pos_z);
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: double 형식의 특성상 이 메서드는 radius_z를 포함한 버전을 사용하는 것이 더 용이합니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 z좌표입니다.
	 */
	public boolean HitTest3D(double pos_x, double pos_y, double pos_z)
	{
		return	this.pos_x - radius_x <= pos_x && this.pos_x + radius_x >= pos_x &&
				this.pos_y - radius_y <= pos_y && this.pos_y + radius_y >= pos_y &&
				this.pos_z == pos_z;
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: LOOT에서 모든 요소는 실질적으로 2차원 요소이므로<br>
	 * 여기서는 적중 테스트를 용이하게 하기 위해 z축 방향 반지름을 적용하며<br>
	 * 요소의 실제 z좌표와 입력받은 z좌표 사이의 거리가 해당 반지름보다 작거나 같은 경우 적중한 것으로 간주합니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 z좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	public boolean HitTest3D(double pos_x, double pos_y, double pos_z, double radius_z)
	{
		return	this.pos_x - radius_x <= pos_x && this.pos_x + radius_x >= pos_x &&
				this.pos_y - radius_y <= pos_y && this.pos_y + radius_y >= pos_y &&
				this.pos_z <= pos_z + radius_z && this.pos_z >= pos_z - radius_z;
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: double 형식의 특성상 이 메서드는 radius_z를 포함한 버전을 사용하는 것이 더 용이합니다.
	 * 
	 * @param pos 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 좌표입니다.
	 */
	public final boolean HitTest3D(Point3D pos)
	{
		return HitTest3D(pos.x, pos.y, pos.z);
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고: LOOT에서 모든 요소는 실질적으로 2차원 요소이므로<br>
	 * 여기서는 적중 테스트를 용이하게 하기 위해 z축 방향 반지름을 적용하며<br>
	 * 요소의 실제 z좌표와 입력받은 z좌표 사이의 거리가 해당 반지름보다 작거나 같은 경우 적중한 것으로 간주합니다.
	 * 
	 * @param pos 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	public final boolean HitTest3D(Point3D pos, double radius_z)
	{
		return HitTest3D(pos.x, pos.y, pos.z, radius_z);
	}
	

	/**
	 * 이 메서드는 VisualObject3D.HitTest3D()와 정확히 동일한 작업을 수행합니다.<br>
	 * 하지만 클래스 계층이 여러 단계에 걸쳐 형성되었을 때 어떤 하위 클래스든 가장 단순한 범위 기반 적중 테스트(여기서 하는 작업)를 할 수 있도록<br>
	 * 내부적으로 이러한 '다른 이름의' 메서드를 만들어 사용합니다.<br>
	 * 어쨋든 여러분은 이 메서드를 사용할 수 없습니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 z좌표입니다.
	 */
	protected final boolean HitTest3D_Base(double pos_x, double pos_y, double pos_z)
	{
		return	this.pos_x - radius_x <= pos_x && this.pos_x + radius_x >= pos_x &&
				this.pos_y - radius_y <= pos_y && this.pos_y + radius_y >= pos_y &&
				this.pos_z == pos_z;
	}
	
	/**
	 * 이 메서드는 VisualObject3D.HitTest3D()와 정확히 동일한 작업을 수행합니다.<br>
	 * 하지만 클래스 계층이 여러 단계에 걸쳐 형성되었을 때 어떤 하위 클래스든 가장 단순한 범위 기반 적중 테스트(여기서 하는 작업)를 할 수 있도록<br>
	 * 내부적으로 이러한 '다른 이름의' 메서드를 만들어 사용합니다.<br>
	 * 어쨋든 여러분은 이 메서드를 사용할 수 없습니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 z좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	protected final boolean HitTest3D_Base(double pos_x, double pos_y, double pos_z, double radius_z)
	{
		return	this.pos_x - radius_x <= pos_x && this.pos_x + radius_x >= pos_x &&
				this.pos_y - radius_y <= pos_y && this.pos_y + radius_y >= pos_y &&
				this.pos_z <= pos_z + radius_z && this.pos_z >= pos_z - radius_z;
	}
	
	
	/* ---------------------------------------------------
	 * 
	 * 상대좌표 계산 및 좌표 변환을 위한 메서드들
	 * 
	 */
	
	/**
	 * 3차원 공간 안에서, 해당 좌표의 이 요소를 기준으로 하는 상대좌표를 반환합니다.
	 * 
	 * @param pos_x 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 * @param pos_z 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 z좌표입니다.
	 */
	public Point3D GetRelativePosition3D(double pos_x, double pos_y, double pos_z)
	{
		return new Point3D(pos_x - this.pos_x, pos_y - this.pos_y, pos_z - this.pos_z);
	}
	
	/**
	 * 3차원 공간 안에서, 해당 좌표의 이 요소를 기준으로 하는 상대좌표를 반환합니다.
	 * 
	 * @param pos 상대좌표를 반환하려 하는, 이 요소가 속해 있는 좌표계 안에서의 3차원 좌표입니다.
	 */
	public Point3D GetRelativePosition3D(Point3D pos)
	{
		return GetRelativePosition3D(pos.x, pos.y, pos.z);
	}

	/**
	 * 이 요소를 기준으로 하는, 이 요소가 속해 있는 2차원 평면 내 좌표에 대한, 이 요소가 속해 있는 3차원 공간 내 상대좌표를 반환합니다.<br>
	 * 참고: 이 요소와 같은 평면 위에 있는 좌표를 대상으로 삼으므로 만들어진 상대좌표의 pox_z 값은 항상 0이 됩니다.
	 * 
	 * @param x 3차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 x좌표입니다.
	 * @param y 3차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 y좌표입니다.
	 */
	public Point3D GetRelativePosition3D(int x, int y)
	{
		return new Point3D(
				( ( x - this.x ) - width / 2.0 ) / width * radius_x * 2.0,
				( height / 2.0 - ( y - this.y ) ) / height * radius_y * 2.0,
				0);
	}
	
	/**
	 * 이 요소를 기준으로 하는, 이 요소가 속해 있는 2차원 평면 내 좌표에 대한, 이 요소가 속해 있는 3차원 공간 내 상대좌표를 반환합니다.<br>
	 * 참고: 이 요소와 같은 평면 위에 있는 좌표를 대상으로 삼으므로 만들어진 상대좌표의 pox_z 값은 항상 0이 됩니다.
	 * 
	 * @param pos 3차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 좌표입니다.
	 */
	public final Point3D GetRelativePosition3D(Point pos)
	{
		return GetRelativePosition3D(pos.x, pos.y);
	}

	/**
	 * 이 요소가 속해 있는 2차원 평면 내 좌표에 상응하는 3차원 좌표를 만들어 반환합니다.<br>
	 * 참고: 이 요소와 같은 평면 위에 있는 좌표를 대상으로 삼으므로 만들어진 좌표의 pox_z 값은 항상 이 요소의 pos_z와 동일합니다.
	 * 
	 * @param x 3차원 좌표를 만들려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 x좌표입니다.
	 * @param y 3차원 좌표를 만들려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 y좌표입니다.
	 */
	public Point3D TransformTo3DPosition(int x, int y)
	{
		Point3D result = GetRelativePosition3D(x, y);
		result.x += this.pos_x;
		result.y += this.pos_y;
		result.z = this.pos_z;
		return result;
	}
	
	/**
	 * 이 요소가 속해 있는 2차원 평면 내 좌표에 상응하는 3차원 좌표를 만들어 반환합니다.<br>
	 * 참고: 이 요소와 같은 평면 위에 있는 좌표를 대상으로 삼으므로 만들어진 좌표의 pox_z 값은 항상 이 요소의 pos_z와 동일합니다.
	 * 
	 * @param pos 3차원 좌표를 만들려 하는, 이 요소가 속해 있는 2차원 좌표계 안에서의 좌표입니다.
	 */
	public final Point3D TransformTo3DPosition(Point pos)
	{
		return TransformTo3DPosition(pos.x, pos.y);
	}
	
	/**
	 * 이 요소를 기준으로 하는, 이 요소가 속해 있는 3차원 공간 내 좌표에 대한, 이 요소가 속해 있는 2차원 평면 내 상대좌표를 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * VisualObject3D.GetRelativePosition()은 argument의 z좌표를 전혀 고려하지 않으며<br>
	 * 항상 ( pos_x, pos_y, 자신의 z좌표 )에 대한 2차원 상대좌표를 만들어 반환합니다.<br>
	 * 이는 VisualObject3D 입장에서 z축 방향의 '배율'(Viewport.view_baseDistance 값)을 알 수가 없기 때문이며<br>
	 * 여러분이 원하는, 해당 3차원 좌표에 대한 진짜 2차원 상대좌표를 구하려면<br>
	 * 이 메서드 대신 Viewport.GetRelativePositionFrom()을 사용해야 합니다. 
	 * 
	 * @param pos_x 2차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 2차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 y좌표입니다.
	 * @param pos_z
	 * 			2차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 z좌표입니다.<br>
	 * 			<br>
	 * 			<b>주의:</b><br>
	 * 			VisualObject3D.GetRelativePosition()은 이 값을 전혀 고려하지 않습니다.<br>
	 * 			자세한 내용은 메서드 설명을 참고하세요.
	 */
	public Point GetRelativePosition(double pos_x, double pos_y, double pos_z)
	{
		int result_x = (int)( ( ( pos_x - this.pos_x ) + radius_x ) / radius_x / 2.0 * width );
		int result_y = (int)( ( radius_y - ( pos_y - this.pos_y ) ) / radius_y / 2.0 * height );
		
		return new Point(result_x, result_y);
	}
	
	/**
	 * 이 요소를 기준으로 하는, 이 요소가 속해 있는 3차원 공간 내 좌표에 대한, 이 요소가 속해 있는 2차원 평면 내 상대좌표를 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * VisualObject3D.GetRelativePosition()은 argument의 z좌표를 전혀 고려하지 않으며<br>
	 * 항상 ( pos_x, pos_y, 자신의 z좌표 )에 대한 2차원 상대좌표를 만들어 반환합니다.<br>
	 * 이는 VisualObject3D 입장에서 z축 방향의 '배율'(Viewport.view_baseDistance 값)을 알 수가 없기 때문이며<br>
	 * 여러분이 원하는, 해당 3차원 좌표에 대한 진짜 2차원 상대좌표를 구하려면<br>
	 * Viewport.GetRelativePositionFrom()을 사용해야 합니다.
	 * 
	 * @param pos 2차원 상대좌표를 반환하려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 좌표입니다.<br>
	 * 			<br>
	 * 			<b>주의:</b><br>
	 * 			VisualObject3D.GetRelativePosition()은 이 좌표의 z 값을 전혀 고려하지 않습니다.<br>
	 * 			자세한 내용은 메서드 설명을 참고하세요.
	 */	
	public final Point GetRelativePosition(Point3D pos)
	{
		return GetRelativePosition(pos.x, pos.y, pos.z);
	}
	
	/**
	 * 이 요소가 속해 있는 3차원 공간 내에서 이 요소가 놓여 있는 평면에 대해 상응하는 2차원 좌표를 만들어 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * VisualObject3D.TransformTo2DPosition()은 argument의 z좌표를 전혀 고려하지 않으며<br>
	 * 항상 ( pos_x, pos_y, 자신의 z좌표 )에 대해 상응하는 2차원 좌표를 만들어 반환합니다.<br>
	 * 이는 VisualObject3D 입장에서 z축 방향의 '배율'(Viewport.view_baseDistance 값)을 알 수가 없기 때문이며<br>
	 * 여러분이 원하는, 해당 3차원 좌표에 대해 상응하는 진짜 2차원 좌표를 구하려면<br>
	 * Viewport.TransformToInternal2DPositionFromInternal3D()를 사용해야 합니다. 
	 * 
	 * @param pos_x 2차원 좌표를 만들려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 x좌표입니다.
	 * @param pos_y 2차원 좌표를 만들려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 y좌표입니다.
	 * @param pos_z
	 * 			2차원 좌표를 만들려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 z좌표입니다.<br>
	 * 			<br>
	 * 			<b>주의:</b><br>
	 * 			VisualObject3D.TransformTo2DPosition()은 이 값을 전혀 고려하지 않습니다.<br>
	 * 			자세한 내용은 메서드 설명을 참고하세요.
	 */
	public Point TransformTo2DPosition(double pos_x, double pos_y, double pos_z)
	{
		Point result = GetRelativePosition(pos_x, pos_y, pos_z);
		
		result.x += this.x;
		result.y += this.y;
		
		return result;
	}
	
	/**
	 * 이 요소가 속해 있는 3차원 공간 내에서 이 요소가 놓여 있는 평면에 대해 상응하는 2차원 좌표를 만들어 반환합니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * VisualObject3D.TransformTo2DPosition()은 argument의 z좌표를 전혀 고려하지 않으며<br>
	 * 항상 ( pos_x, pos_y, 자신의 z좌표 )에 대해 상응하는 2차원 좌표를 만들어 반환합니다.<br>
	 * 이는 VisualObject3D 입장에서 z축 방향의 '배율'(Viewport.view_baseDistance 값)을 알 수가 없기 때문이며<br>
	 * 여러분이 원하는, 해당 3차원 좌표에 대해 상응하는 진짜 2차원 좌표를 구하려면<br>
	 * Viewport.TransformToInternal2DPositionFromInternal3D()를 사용해야 합니다. 
	 * 
	 * @param pos 2차원 좌표를 만들려 하는, 이 요소가 속해 있는 3차원 좌표계 안에서의 좌표입니다.<br>
	 * 			<br>
	 * 			<b>주의:</b><br>
	 * 			VisualObject3D.TransformTo2DPosition()은 이 좌표의 z 값을 전혀 고려하지 않습니다.<br>
	 * 			자세한 내용은 메서드 설명을 참고하세요.
	 */
	public final Point TransformTo2DPosition(Point3D pos)
	{
		return TransformTo2DPosition(pos.x, pos.y, pos.z);
	}
}
