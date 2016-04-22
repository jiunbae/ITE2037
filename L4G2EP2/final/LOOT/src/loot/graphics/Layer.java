package loot.graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * 2차원 내부 좌표계를 만들고 이를 통해 내부 요소들을 그리는 클래스입니다.<br>
 * 참고 1: Layer를 상속받는 다른 클래스들은 각각 다른 좌표계를 더 만들어 사용합니다.<br>
 * 참고 2: Layer 또한 VisualObject3D의 하위 클래스이므로 다른 Layer의 내부 요소가 될 수 있습니다.<br>
 * <br>
 * <b>주의:</b><br>
 * Layer 및 2차원 좌표계를 사용하는 Layer의 하위 클래스들은 내부 요소의 최종 위치가 자신의 영역 내에 있는지 검사하지 않습니다.<br>
 * 대부분의 상황에서 이 현상은 큰 문제가 되지 않겠지만<br>
 * 만약 성능상의 문제로 '보이지 않는 요소'를 그리지 않도록 구성해야 한다면<br>
 * children 목록에 있는 각 요소를 수동으로 체크하여<br>
 * 영역을 벗어난 (그리고 더 이상 영역에 들어오지 않을) 요소의 trigger_remove 필드를 true로 설정함으로써<br>
 * Layer가 이번 프레임부터 해당 요소를 그리지 않도록 만들 수 있습니다.
 * 
 * @author Racin
 *
 */
public class Layer extends VisualObject3D
{
	/**
	 * 현재 포함된 내부 요소 목록입니다.<br>
	 * Layer 및 Layer를 상속받는 클래스들의 Draw(g)는 이 목록에 있는 요소들을 자신의 영역에 그립니다.
	 */
	public LinkedList<VisualObject> children;
	
	/**
	 * Layer가 다루는 2차원 내부 좌표계의 너비입니다.<br>
	 * <br>
	 * Layer 및 2차원 좌표계를 사용하는 Layer의 하위 클래스들은<br>
	 * (0, 0)을 좌상단 좌표로 삼고 너비가 view_width, 높이가 view_height인 가상 좌표계를 만들어<br>
	 * 내부 요소를 자신의 영역(x, y, width, height로 지정된)에 그립니다.<br>
	 * <br>
	 * 3차원 좌표계를 사용하는 Layer의 하위 클래스들은<br>
	 * (pointOfView_x, pointOfView_y, pointOfView_z + baseDistance)를 중점으로 삼고 너비가 view_width, 높이가 view_height인 가상 좌표계를 만들어<br>
	 * 내부 요소를 자신의 영역(x, y, width, height로 지정된)에 그립니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * Layer 및 2차원 좌표계를 사용하는 Layer의 하위 클래스들은 내부 요소의 최종 위치가 자신의 영역 내에 있는지 검사하지 않습니다.<br>
	 * 대부분의 상황에서 이 현상은 큰 문제가 되지 않겠지만<br>
	 * 만약 성능상의 문제로 '보이지 않는 요소'를 그리지 않도록 구성해야 한다면<br>
	 * children 목록에 있는 각 요소를 수동으로 체크하여<br>
	 * 영역을 벗어난 (그리고 더 이상 영역에 들어오지 않을) 요소의 trigger_remove 필드를 true로 설정함으로써<br>
	 * Layer가 이번 프레임부터 해당 요소를 그리지 않도록 만들 수 있습니다.
	 */
	public double view_width;
	
	/**
	 * Layer가 다루는 2차원 내부 좌표계의 높이입니다.<br>
	 * <br>
	 * Layer 및 2차원 좌표계를 사용하는 Layer의 하위 클래스들은<br>
	 * (0, 0)을 좌상단 좌표로 삼고 너비가 view_width, 높이가 view_height인 가상 좌표계를 만들어<br>
	 * 내부 요소를 자신의 영역(x, y, width, height로 지정된)에 그립니다.<br>
	 * <br>
	 * 3차원 좌표계를 사용하는 Layer의 하위 클래스들은<br>
	 * (pointOfView_x, pointOfView_y, pointOfView_z + baseDistance)를 중점으로 삼고 너비가 view_width, 높이가 view_height인 가상 좌표계를 만들어<br>
	 * 내부 요소를 자신의 영역(x, y, width, height로 지정된)에 그립니다.<br>
	 * <br>
	 * <b>주의:</b><br>
	 * Layer 및 2차원 좌표계를 사용하는 Layer의 하위 클래스들은 내부 요소의 최종 위치가 자신의 영역 내에 있는지 검사하지 않습니다.<br>
	 * 대부분의 상황에서 이 현상은 큰 문제가 되지 않겠지만<br>
	 * 만약 성능상의 문제로 '보이지 않는 요소'를 그리지 않도록 구성해야 한다면<br>
	 * children 목록에 있는 각 요소를 수동으로 체크하여<br>
	 * 영역을 벗어난 (그리고 더 이상 영역에 들어오지 않을) 요소의 trigger_remove 필드를 true로 설정함으로써<br>
	 * Layer가 이번 프레임부터 해당 요소를 그리지 않도록 만들 수 있습니다.
	 */
	public double view_height;
	
	/**
	 * Layer가 다루는 내부 좌표계의 원점 x좌표의 화면 상의 위치를 지정합니다.<br>
	 * 회전 중심이 Layer 내부에 있는 경우 이 값은 0 ~ 1.0 사이의 범위를 가지며<br>
	 * 이 값이 0일 경우 좌측 끝, 1.0일 경우 우측 끝을 의미합니다.
	 */
	public double view_origin_x;
	
	/**
	 * Layer가 다루는 내부 좌표계의 원점 y좌표의 화면 상의 위치를 지정합니다.<br>
	 * 회전 중심이 Layer 내부에 있는 경우 이 값은 0 ~ 1.0 사이의 범위를 가지며<br>
	 * 이 값이 0일 경우 좌측 끝, 1.0일 경우 우측 끝을 의미합니다.
	 */
	public double view_origin_y;

	/**
	 * Layer가 속한 좌표계의 좌표를 Layer가 다루는 내부 좌표로 변환하기 위한 변환 행렬입니다.<br>
	 * 이 변환 행렬에는 내부 요소 그리기에 사용된 변환 행렬의 역행렬이 기록됩니다.<br>
	 * 이 필드는 내부적으로 생성 및 사용되며 여러분이 직접 이 필드를 볼 수는 없습니다. 
	 */
	protected AffineTransform transform_in;
	
	/**
	 * Layer가 다루는 내부 좌표를 Layer가 속한 좌표계(상대적으로 외부에 있는 좌표계)의 좌표로 변환하기 위한 변환 행렬입니다.<br>
	 * 이 변환 행렬에는 내부 요소 그리기에 사용된 변환 행렬 정행렬이 기록됩니다.<br>
	 * 이 필드는 내부적으로 생성 및 사용되며 여러분이 직접 이 필드를 볼 수는 없습니다. 
	 */
	protected AffineTransform transform_out;
	
	public Layer(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		children = new LinkedList<>();
		transform_in = new AffineTransform();
		transform_out = new AffineTransform();
		
		if ( width < 0 )
			view_width = -width;
		else		
			view_width = width;
		
		if ( height < 0 )
			view_height = -height;
		else
			view_height = height;
		
		view_origin_x = 0;
		view_origin_y = 0;
	}
	
	public Layer(int x, int y, int width, int height, double view_width, double view_height)
	{
		super(x, y, width, height);
		children = new LinkedList<>();
		transform_in = new AffineTransform();
		transform_out = new AffineTransform();
		this.view_width = view_width;
		this.view_height = view_height;
		
		view_origin_x = 0;
		view_origin_y = 0;
	}
	
	public Layer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		children = new LinkedList<>();
		transform_in = new AffineTransform();
		transform_out = new AffineTransform();
		
		if ( radius_x < 0 )
			view_width = -radius_x * 2;
		else		
			view_width = radius_x * 2;
		
		if ( radius_y < 0 )
			view_height = -radius_y * 2;
		else
			view_height = radius_y * 2;
		
		view_origin_x = 0;
		view_origin_y = 0;
	}
	
	public Layer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y, double view_width, double view_height)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		children = new LinkedList<>();
		transform_in = new AffineTransform();
		transform_out = new AffineTransform();
		this.view_width = view_width;
		this.view_height = view_height;
		
		view_origin_x = 0;
		view_origin_y = 0;
	}
	
	/**
	 * 현재 Layer에 지정된 영역에 내부 요소들을 그립니다.
	 * 
	 * @param g_origin
	 * 		GameFrame에는 g 라는 필드가 들어 있습니다.<br>
	 * 		여러분이 Draw(g)를 직접 호출할 때(게임 화면에 직접 그릴 때)는 더 고민하지 말고 그 필드를 그냥 넣으면 됩니다.
	 */
	@Override
	public void Draw(Graphics2D g_origin)
	{
		Graphics2D g = (Graphics2D)g_origin.create();

		//변환 행렬 및 역행렬 작성
		transform_out.setToIdentity();
		transform_out.translate(x, y);
		transform_out.scale(width / view_width, height / view_height);
		transform_out.translate(view_width * view_origin_x, view_height * view_origin_y);

		transform_in.setToIdentity();
		transform_in.translate(-view_width * view_origin_x, -view_height * view_origin_y);
		transform_in.scale(view_width / width, view_height / height);	//'a/b배'의 역연산은 'b/a배' 
		transform_in.translate(-x, -y);									//'(a, b)만큼 이동'의 역연산은 '(-a, -b)만큼 이동'

		//그리기 작업을 위해 변환 행렬을 g에 적용
		g.transform(transform_out);
				
		for ( Iterator<VisualObject> iterator = children.descendingIterator(); iterator.hasNext(); )
		{
			VisualObject child = iterator.next();
			
			if ( child.trigger_remove == true )
			{
				iterator.remove();
				continue;
			}
			
			if ( child.trigger_hide == true )
				continue;
			
			child.Draw(g);
		}

		
		g.dispose();
	}
	
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
	@Override
	public boolean HitTest(int x, int y)
	{
		if ( HitTest_Base(x, y) == true )
		{
			//외부 좌표를 내부 좌표로 변환
			Point pos_internal = TransformToInternalPosition(x, y);
			
			for ( VisualObject child : children )
				if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest(pos_internal.x, pos_internal.y) == true )
					return true;
		}
		
		return false;
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고 1: Layer 및 2차원 내부 좌표계를 사용하는 Layer의 하위 클래스들은 해당 좌표를 2차원 내부 좌표로 변환하여 사용합니다.<br>
	 * 참고 2: double 형식의 특성상 이 메서드는 radius_z를 포함한 버전을 사용하는 것이 더 용이합니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 z좌표입니다.
	 */
	@Override
	public boolean HitTest3D(double pos_x, double pos_y, double pos_z)
	{
		if ( HitTest3D_Base(pos_x, pos_y, pos_z) == true )
		{
			//외부 3차원 좌표를 내부 2차원 좌표로 변환
			Point pos_external_2d = TransformTo2DPosition(pos_x, pos_y, pos_z);
			Point pos_internal = TransformToInternalPosition(pos_external_2d.x, pos_external_2d.y);
			
			for ( VisualObject child : children )
				if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest(pos_internal.x, pos_internal.y) == true )
					return true;
		}
		
		return false;
	}
	
	/**
	 * 3차원 공간 안에서, 이 요소가 차지하는 영역 위에 해당 좌표가 있는지(마우스 커서를 예로 들면, 커서가 이 요소 위에 있는지) 여부를 반환합니다.<br>
	 * 참고 1: Layer 및 2차원 내부 좌표계를 사용하는 Layer의 하위 클래스들은 해당 좌표를 2차원 내부 좌표로 변환하여 사용합니다.<br>
	 * 참고 2: LOOT에서 모든 요소는 실질적으로 2차원 요소이므로<br>
	 * 여기서는 적중 테스트를 용이하게 하기 위해 z축 방향 반지름을 적용하며<br>
	 * 요소의 실제 z좌표와 입력받은 z좌표 사이의 거리가 해당 반지름보다 작거나 같은 경우 적중한 것으로 간주합니다.
	 * 
	 * @param pos_x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 x좌표입니다.
	 * @param pos_y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 y좌표입니다.
	 * @param pos_z 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 3차원 z좌표입니다.
	 * @param radius_z 적중 여부를 검사할 때 적용하는 z축 방향 반지름입니다.
	 */
	@Override
	public boolean HitTest3D(double pos_x, double pos_y, double pos_z, double radius_z)
	{		
		if ( HitTest3D_Base(pos_x, pos_y, pos_z, radius_z) == true )
		{
			//외부 3차원 좌표를 내부 2차원 좌표로 변환
			Point pos_external_2d = TransformTo2DPosition(pos_x, pos_y, pos_z);
			Point pos_internal = TransformToInternalPosition(pos_external_2d.x, pos_external_2d.y);
			
			for ( VisualObject child : children )
				if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest(pos_internal.x, pos_internal.y) == true )
					return true;
		}
		
		return false;
	}
	
	/**
	 * 해당 좌표 위에 있는 내부 요소를 반환합니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우 null을 반환합니다.<br>
	 * 만약 해당 좌표 위에 두 개 이상의 요소가 있는 경우 가장 나중에 그려진(실제 게임 화면에 보이는) 요소를 반환합니다.
	 * 
	 * @param x 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	public VisualObject GetObjectAt(int x, int y)
	{
		if ( HitTest_Base(x, y) == true )
		{
			//외부 좌표를 내부 좌표로 변환
			Point pos_internal = TransformToInternalPosition(x, y);

			for ( VisualObject child : children )
				if ( child.trigger_hide == false && child.trigger_ignoreDuringHitTest == false && child.HitTest(pos_internal.x, pos_internal.y) == true )
					return child;
		}
		
		return null;
	}
	
	/**
	 * 해당 좌표 위에 있는 내부 요소를 반환합니다.<br>
	 * 만약 해당 좌표 위에 아무 요소도 없는 경우 null을 반환합니다.<br>
	 * 만약 해당 좌표 위에 두 개 이상의 요소가 있는 경우 가장 나중에 그려진(실제 게임 화면에 보이는) 요소를 반환합니다.
	 * 
	 * @param pos 적중 여부를 검사할, 이 요소가 속해 있는 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final VisualObject GetObjectAt(Point pos)
	{
		return GetObjectAt(pos.x, pos.y);
	}
	
	
	/* ---------------------------------------------------
	 * 
	 * 상대좌표 계산 및 좌표 변환을 위한 메서드들
	 * 
	 */
	
	/**
	 * 주어진 외부 좌표(이 요소가 속해 있는 좌표계 안에서의 좌표)를 내부 좌표(이 요소가 만든 내부 좌표계 안에서의 좌표)로 변환하여 반환합니다.
	 * 
	 * @param x 내부 좌표로 변환할, 이 요소가 속해 있는 좌표계 안에서의 x좌표입니다.
	 * @param y 내부 좌표로 변환할, 이 요소가 속해 있는 좌표계 안에서의 y좌표입니다.
	 */
	public Point TransformToInternalPosition(int x, int y)
	{
		//역행렬을 사용하여 좌표 변환 수행
		Point2D pos_internal = new Point2D.Double(x, y);
		transform_in.transform(pos_internal, pos_internal);

		return new Point((int)pos_internal.getX(), (int)pos_internal.getY());
	}
	
	/**
	 * 주어진 외부 좌표(이 요소가 속해 있는 좌표계 안에서의 좌표)를 내부 좌표(이 요소가 만든 내부 좌표계 안에서의 좌표)로 변환하여 반환합니다.
	 * 
	 * @param pos 내부 좌표로 변환할, 이 요소가 속해 있는 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final Point TransformToInternalPosition(Point pos)
	{
		return TransformToInternalPosition(pos.x, pos.y);
	}
	
	/**
	 * 주어진 내부 좌표(이 요소가 만든 내부 좌표계 안에서의 좌표)를 외부 좌표(이 요소가 속해 있는 좌표계 안에서의 좌표)로 변환하여 반환합니다.
	 * 
	 * @param x_internal 외부 좌표로 변환할, 이 요소가 만든 내부 좌표계 안에서의 x좌표입니다.
	 * @param y_internal 외부 좌표로 변환할, 이 요소가 만든 내부 좌표계 안에서의 y좌표입니다.
	 */
	public Point TransformToExternalPosition(int x_internal, int y_internal)
	{
		//변환 행렬을 사용하여 좌표 변환 수행
		Point2D pos_external = new Point2D.Double(x_internal, y_internal);
		transform_out.transform(pos_external, pos_external);

		return new Point((int)pos_external.getX(), (int)pos_external.getY());
	}
	
	/**
	 * 주어진 내부 좌표(이 요소가 만든 내부 좌표계 안에서의 좌표)를 외부 좌표(이 요소가 속해 있는 좌표계 안에서의 좌표)로 변환하여 반환합니다.
	 * 
	 * @param pos_internal 외부 좌표로 변환할, 이 요소가 만든 내부 좌표계 안에서의 2차원 좌표입니다.
	 */
	public final Point TransformToExternalPosition(Point pos_internal)
	{
		return TransformToExternalPosition(pos_internal.x, pos_internal.y);
	}
}
