package l4g.common;

/**
 * 특정 좌표를 나타내기 위한 <code>Point class, Point_Immutable class</code>의 부모인 추상 클래스입니다.
 * 따라서, 이 클래스는 신경쓰지 않아도 됩니다.<br>
 * <br>
 * <b>Note:</b> 이 클래스는 'public'이 붙어 있지 않기 때문에 여러분 코드에서는 어차피 보이지도 않습니다.
 * 
 * @author Racin
 *
 */
abstract class PointBase
{
	/**
	 * 이 좌표와 동일한 x, y값을 가진 새로운 Point 인스턴스를 만들어 반환합니다.
	 */
	public abstract Point Copy();
	
	/**
	 * 이 좌표에서 지정된 거리만큼 떨어진 위치를 가리키는 새로운 Point 인스턴스를 만들어 반환합니다.
	 */
	public abstract Point Offset(int offset_row, int offset_column);
	
	/**
	 * 이 좌표에서 지정된 방향에 위치한 가장 가까운 Point 인스턴스를 만들어 반환합니다.
	 */
	public abstract Point GetAdjacentPoint(DirectionCode direction);
	
	/**
	 * 이 좌표와 해당 좌표 사이의 칸 수를 반환합니다.
	 */
	public abstract int GetDistance(int row, int column);
	
	/**
	 * 이 좌표와 해당 좌표 사이의 칸 수를 반환합니다.
	 */
	public abstract int GetDistance(Point other);
	
	/**
	 * 이 좌표와 해당 좌표 사이의 칸 수를 반환합니다.
	 */
	public abstract int GetDistance(Point_Immutable other);
	
	/**
	 * 이 좌표와 해당 좌표가 동일한 칸을 가리키고 있는지 여부를 반환합니다.
	 */
	public abstract boolean equals(Point other);
	
	/**
	 * 이 좌표와 해당 좌표가 동일한 칸을 가리키고 있는지 여부를 반환합니다.
	 */
	public abstract boolean equals(Point_Immutable other);
	
	/**
	 * 이 좌표와 해당 좌표가 동일한 칸을 가리키고 있는지 여부를 반환합니다.
	 */
	public abstract boolean equals(int row, int column);
}
