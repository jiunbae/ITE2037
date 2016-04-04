package l4g2ep1.common;

/**
 * 강의실 내의 각 칸에 대한 좌표를 나타내는 클래스입니다.
 * 
 * 주의: 두 좌표를 비교할 때 단순히 == 연산자를 사용하면 보통은 원하는 결과가 나오지 않습니다.
 * 		 대신 p1.equals(p2)와 같이
 * 		 어떤 좌표에 점을 찍고 다른 좌표를 argument로 넣어 equals() 메서드를 호출한 다음
 * 		 반환값이 true인지 검사하여 두 좌표의 값이 같은지 여부를 확인해야 합니다.
 * 
 * @author Racin
 *
 */
public final class Point
{
	/**
	 * 좌표의 x 요소(가로방향, 왼쪽에서 오른쪽으로 증가)입니다.
	 */
	public int x;
	
	/**
	 * 좌표의 y 요소(세로방향, 위에서 아래로 증가)입니다.
	 */
	public int y;

	/**
	 * 아무 칸도 가리키지 않는 새로운 Point class의 인스턴스를 생성합니다.
	 */
	public Point()
	{
		x = -1;
		y = -1;
	}

	/**
	 * 주어진 좌표를 갖는 새로운 Point class의 인스턴스를 생성합니다.
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * 해당 좌표와 동일한 새로운 Point class의 인스턴스를 생성합니다.
	 */
	public Point(Point other)
	{
		x = other.x;
		y = other.y;
	}
	
	/**
	 * 주어진 원점 좌표에서 해당 방향으로 이동했을 때 도달하게 될 곳을 가리키는 새로운 Point class의 인스턴스를 생성합니다.
	 */
	public Point(Point origin, DirectionCode direction)
	{
		x = origin.x;
		y = origin.y;
		
		if ( direction != null )
		{
			switch ( direction )
			{
			case Up:
				--y;
				break;
			case Left:
				--x;
				break;
			case Right:
				++x;
				break;
			case Down:
				++y;
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 이 좌표가 강의실 내의 한 칸을 가리키는 유효한 좌표인지 여부를 반환합니다.
	 */
	public boolean IsValid()
	{
		// 좌표가 강의실 영역을 벗어났는지 여부 확인
		return x >= 0 && x < Constants.Classroom_Width &&
				y >= 0 && y < Constants.Classroom_Height;
	}
	
	/**
	 * 주어진 좌표가 강의실 내의 한 칸을 가리키는 유효한 좌표인지 여부를 반환합니다.
	 */
	public static boolean IsValid(int x, int y)
	{
		return x >= 0 && x < Constants.Classroom_Width &&
				y >= 0 && y < Constants.Classroom_Height;
	}

	/**
	 * 주어진 좌표가 강의실 내의 한 칸을 가리키는 유효한 좌표인지 여부를 반환합니다.
	 */
	public static boolean IsValid(Point p)
	{
		return IsValid(p.x, p.y);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		//만약 비교하려는 인스턴스가 같은 Point 형식이라면 좌표간 비교 수행
		if ( obj.getClass() == this.getClass() )
			return equals((Point)obj);
		
		//그렇지 않은 경우 같을 리가 없으니 false 반환
		return false;
	}

	public boolean equals(Point other)
	{
		return x == other.x && y == other.y;
	}

	public boolean equals(int x, int y)
	{
		return this.x == x && this.y == y;
	}

	@Override
	public String toString()
	{
		return String.format("(%d, %d)", x, y);
	}
}
