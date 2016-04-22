package loot.graphics;

/**
 * 3차원 공간 내(Viewport 안)의 특정 위치를 나타내기 위한 클래스입니다.<br>
 * 참고: 이 클래스는 특정 메서드의 반환 형식으로 사용하기 위해 아주 기본적인 기능만 포함하고 있으며<br>
 * 대부분의 경우 여러분은 벡터 연산을 하기 위해 별도의 코드를 사용해야 할 것입니다.
 * 
 * @author Racin
 *
 */
public class Point3D
{
	/**
	 * 위치를 나타내는 x좌표입니다.<br>
	 * 3차원 공간에서 x축은 왼쪽에서 오른쪽으로 진행합니다.
	 */
	public double x;
	
	/**
	 * 위치를 나타내는 y좌표입니다.<br>
	 * 3차원 공간에서 y축은 <b>아래에서 위로</b> 진행합니다.
	 */
	public double y;
	
	/**
	 * 위치를 나타내는 z좌표입니다.<br>
	 * 3차원 공간에서 z축은 화면 깊은 곳에서 내 눈 앞을 향하는 방향으로 진행합니다.
	 */
	public double z;
	
	public Point3D()
	{
	}
	
	public Point3D(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(Point3D other)
	{
		x = other.x;
		y = other.y;
		z = other.z;
	}
	
	/**
	 * 이 요소와 대상 좌표 사이의 스칼라 거리( sqrt(x거리^2 + y거리^2 + z거리^2) )를 구합니다.
	 */
	public double GetDistance(Point3D target)
	{
		return GetDistance(this, target);
	}
	
	/**
	 * 이 요소와 대상 좌표 사이의 스칼라 거리( sqrt(x거리^2 + y거리^2 + z거리^2) )를 구합니다.
	 */
	public double GetDistance(double target_x, double target_y, double target_z)
	{
		return GetDistance(this, target_x, target_y, target_z);
	}
	
	/**
	 * 두 좌표 사이의 스칼라 거리( sqrt(x거리^2 + y거리^2 + z거리^2) )를 구합니다.
	 */
	public static double GetDistance(Point3D origin, Point3D target)
	{
		return Math.sqrt(
				( target.x - origin.x ) * ( target.x - origin.x ) +
				( target.y - origin.y ) * ( target.y - origin.y ) +
				( target.z - origin.z ) * ( target.z - origin.z ) );
	}

	/**
	 * 두 좌표 사이의 스칼라 거리( sqrt(x거리^2 + y거리^2 + z거리^2) )를 구합니다.
	 */
	public static double GetDistance(Point3D origin, double target_x, double target_y, double target_z)
	{
		return Math.sqrt(
				( target_x - origin.x ) * ( target_x - origin.x ) +
				( target_y - origin.y ) * ( target_y - origin.y ) +
				( target_z - origin.z ) * ( target_z - origin.z ) );
	}

	/**
	 * 두 좌표 사이의 스칼라 거리( sqrt(x거리^2 + y거리^2 + z거리^2) )를 구합니다.
	 */
	public static double GetDistance(double origin_x, double origin_y, double origin_z, double target_x, double target_y, double target_z)
	{
		return Math.sqrt(
				( target_x - origin_x ) * ( target_x - origin_x ) +
				( target_y - origin_y ) * ( target_y - origin_y ) +
				( target_z - origin_z ) * ( target_z - origin_z ) );
	}
	
	@Override
	public String toString()
	{
		//이거 고친거임
		
		return String.format("(%.2f, %.2f, %.2f)", x, y, z);
	}
}
