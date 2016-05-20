package loot.graphics;

import java.awt.Graphics2D;
import java.util.Iterator;

/**
 * 2차원 내부 좌표계를 만들고 이를 설정된 각도에 따라 회전시킨 다음 내부 요소들을 그리는 클래스입니다.<br>
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
public class RotatableLayer extends Layer
{	
	/**
	 * 회전할 각도(단위는 radian)입니다.<br>
	 * 이 값이 양수면 회전은 시계 방향으로 일어납니다.<br>
	 * 참고: 각도 값을 degree <-> radian간 변환할 때는<br>
	 * RotatableLayer.GetDegreeFromRadian() 또는 RotatableLayer.GetRadianFromDegree()를 사용하세요.
	 */
	public double angle;
	
	/**
	 * 회전 중심의 x위치를 지정합니다.<br>
	 * 회전 중심이 Layer 내부에 있는 경우 이 값은 0 ~ 1.0 사이의 범위를 가지며<br>
	 * 이 값이 0일 경우 좌측 끝, 1.0일 경우 우측 끝을 회전 중심으로 삼습니다.<br>
	 * 기본값은 0.5(Layer의 한 가운데)입니다.
	 */
	public double rotate_origin_x;
	
	/**
	 * 회전 중심의 y위치를 지정합니다.<br>
	 * 회전 중심이 Layer 내부에 있는 경우 이 값은 0 ~ 1.0 사이의 범위를 가지며<br>
	 * 이 값이 0일 경우 윗쪽 끝, 1.0일 경우 아랫쪽 끝을 회전 중심으로 삼습니다.<br>
	 * 기본값은 0.5(Layer의 한 가운데)입니다.
	 */
	public double rotate_origin_y;

	public RotatableLayer(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		
		rotate_origin_x = 0.5;
		rotate_origin_y = 0.5;
	}

	public RotatableLayer(int x, int y, int width, int height, double view_width, double view_height)
	{
		super(x, y, width, height, view_width, view_height);
		
		rotate_origin_x = 0.5;
		rotate_origin_y = 0.5;
	}

	public RotatableLayer(int x, int y, int width, int height, double angle, double rotate_origin_x, double rotate_origin_y)
	{
		super(x, y, width, height);
		
		this.angle = angle;
		this.rotate_origin_x = rotate_origin_x;
		this.rotate_origin_y = rotate_origin_y;
	}

	public RotatableLayer(int x, int y, int width, int height, double view_width, double view_height, double angle, double rotate_origin_x, double rotate_origin_y)
	{
		super(x, y, width, height, view_width, view_height);
		
		this.angle = angle;
		this.rotate_origin_x = rotate_origin_x;
		this.rotate_origin_y = rotate_origin_y;
	}
	
	public RotatableLayer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		
		rotate_origin_x = 0.5;
		rotate_origin_y = 0.5;
	}
	
	public RotatableLayer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y, double view_width, double view_height)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y, view_width, view_height);
		
		rotate_origin_x = 0.5;
		rotate_origin_y = 0.5;
	}
	
	public RotatableLayer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y, double angle, double rotate_origin_x, double rotate_origin_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		
		this.angle = angle;
		this.rotate_origin_x = rotate_origin_x;
		this.rotate_origin_y = rotate_origin_y;
	}
	
	public RotatableLayer(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y, double view_width, double view_height, double angle, double rotate_origin_x, double rotate_origin_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y, view_width, view_height);
		
		this.angle = angle;
		this.rotate_origin_x = rotate_origin_x;
		this.rotate_origin_y = rotate_origin_y;
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
		transform_out.rotate(angle, rotate_origin_x * width, rotate_origin_y * height);
		transform_out.scale(width / view_width, height / view_height);
		transform_out.translate(view_width * view_origin_x, view_height * view_origin_y);

		transform_in.setToIdentity();
		transform_in.translate(-view_width * view_origin_x, -view_height * view_origin_y);
		transform_in.scale(view_width / width, view_height / height);					//'a/b배'의 역연산은 'b/a배' 
		transform_in.rotate(-angle, rotate_origin_x * width, rotate_origin_y * height);	//'a만큼 회전'의 역연산은 '-a만큼 회전'
		transform_in.translate(-x, -y);													//'(a, b)만큼 이동'의 역연산은 '(-a, -b)만큼 이동'

		//그리기 작업을 위해 변환 행렬을 g에 적용
		g.transform(transform_out);
	
		for ( Iterator<VisualObject> iterator = children.iterator(); iterator.hasNext(); )
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
	
	/**
	 * 주어진 degree 각도값에 해당하는 radian 각도값을 반환합니다.<br>
	 * 참고: 이 메서드는 '유효한 가장 작은' 각도값이 아닌 그냥 있는 그대로의 각도값을 반환합니다.<br>
	 * 필요한 경우 '반환값 %= 2.0 * Math.PI' 계산을 별도로 수행하여 각도값의 크기를 적당히 작게 유지해 사용하세요.
	 */
	public static double GetRadianFromDegree(double angle_degree)
	{
		return angle_degree / 180.0 * Math.PI;
	}
	
	/**
	 * 주어진 radian 각도값에 해당하는 degree 각도값을 반환합니다.<br> 
	 * 참고: 이 메서드는 '유효한 가장 작은' 각도값이 아닌 그냥 있는 그대로의 각도값을 반환합니다.<br>
	 * 필요한 경우 '반환값 %= 360.0' 계산을 별도로 수행하여 각도값의 크기를 적당히 작게 유지해 사용하세요.
	 */
	public static double GetDegreeFromRadian(double angle_radian)
	{
		return angle_radian / Math.PI * 180.0;
	}
}
