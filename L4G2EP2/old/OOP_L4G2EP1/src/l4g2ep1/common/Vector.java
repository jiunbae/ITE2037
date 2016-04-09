package l4g2ep1.common;

/**
 * 두 좌표를 잇는 벡터 하나를 나타내는 클래스입니다.
 * 
 * @author Racin
 *
 */
public class Vector
{
	/**
	 * 벡터의 X축 방향(가로 방향) 성분입니다.
	 */
	public int x_offset;
	
	/**
	 * 벡터의 Y축 방향(세로 방향) 성분입니다.
	 */
	public int y_offset;
	
	public Vector(int x_origin, int y_origin, int x_target, int y_target)
	{
		x_offset = x_target - x_origin;
		y_offset = y_target - y_origin;
	}

	public Vector(Point origin, Point target)
	{
		this(origin.x, origin.y, target.x, target.y);
	}
	
	public Vector()
	{
		this(0, 0, 0, 0);
	}

	/**
	 * 두 좌표 사이의 X축 방향(가로 방향) 거리를 반환합니다.
	 * 반환값은 x_offset 필드의 절대값과 같습니다.
	 */
	public int GetDistance_X()
	{
		int x_distance = x_offset;
		
		if ( x_distance < 0 )
			x_distance = -x_distance;
		
		return x_distance;
	}

	/**
	 * 두 좌표 사이의 Y축 방향(세로 방향) 거리를 반환합니다.
	 * 반환값은 y_offset 필드의 절대값과 같습니다.
	 */
	public int GetDistance_Y()
	{
		int y_distance = y_offset;
		
		if ( y_distance < 0 )
			y_distance = -y_distance;
		
		return y_distance;
	}

	/**
	 * 두 좌표 사이의 거리(원점 좌표에서 도착점 좌표까지 이동하기 위해 걸리는 총 턴 수)를 반환합니다.
	 * 반환값은 X축 방향 거리와 Y축 방향 거리의 합과 같습니다.
	 */
	public int GetDistance()
	{
		return GetDistance_X() + GetDistance_Y();
	}
	
	/**
	 * 벡터의 길이의 제곱(원점 좌표와 도착점 좌표를 잇는 선분의 길이의 제곱)을 반환합니다.
	 * 반환값은 X축 방향 성분의 제곱과 Y축 방향 성분의 제곱의 합과 같습니다.
	 * (이 값은 실질적으로 쓰임새가 별로 없습니다. 대신 GetDistance()를 사용하여 두 좌표 사이의 실질적인 거리를 반환받아 사용하세요)
	 */
	public int GetSquaredLength()
	{
		return x_offset * x_offset + y_offset * y_offset; 
	}
}
