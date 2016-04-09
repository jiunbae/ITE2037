package l4g.common;

/**
 * 좌표 <b>상수</b> 하나를 나타내는 클래스입니다.
 * 이 클래스의 인스턴스는 여러분이 자주 사용할 <code>Point</code>(상수가 아닌 버전) 인스턴스와 호환됩니다.<br>
 * <br>
 * <b>Note:</b> 이런 클래스를 만든 이유는 그냥 성능 문제 때문이며
 * 여러분이 굳이 이 클래스의 인스턴스를 만들어 쓸 필요는 없습니다.
 * 
 * @author Racin
 *
 */
public class Point_Immutable extends PointBase
{
	/**
	 * 이 좌표의 세로 방향 성분입니다.
	 * 0인 경우 맨 윗 줄을 나타냅니다.
	 */
	public final int row;

	/**
	 * 이 좌표의 가로 방향 성분입니다.
	 * 0인 경우 맨 왼쪽 열을 나타냅니다.
	 */
	public final int column;

	/**
	 * 새로운 <code>Point_Immutable class</code>의 인스턴스를 초기화합니다.<br>
	 * <br>
	 * <b>Note:</b> 여러분이 굳이 이 클래스의 인스턴스를 만들어 쓸 필요는 없습니다.
	 * 어떤 좌표값을 기록해 두고 싶으면 이거 대신 <code>Point class</code>를 쓰세요.
	 */
	public Point_Immutable(int row, int column)
	{
		this.row = row;
		this.column = column;
	}
	
	/* ----------------------------------------------------
	 * 아래에 있는 각 메서드들에 대한 설명은 상위 클래스(PointBase class)에 적어 놓은 것을 공유합니다.
	 * 메서드 이름에 마우스를 갖다 대면 해당 설명을 읽어 볼 수 있습니다.
	 */

	@Override
	public Point Copy()
	{
		return new Point(row, column);
	}

	@Override
	public Point Offset(int offset_row, int offset_column)
	{
		return new Point(row + offset_row, column + offset_column);
	}
	
	@Override
	public Point GetAdjacentPoint(DirectionCode direction)
	{
		switch ( direction )
		{
		case Up:
			return new Point(this, -1, 0);
		case Left:
			return new Point(this, 0, -1);
		case Right:
			return new Point(this, 0, 1);
		case Down:
			return new Point(this, 1, 0);
		default:
			return new Point(this);
		}
	}

	@Override
	public int GetDistance(int row, int column)
	{
		return ( this.row > row ? this.row - row : row - this.row ) + ( this.column > column ? this.column - column : column - this.column );
	}

	@Override
	public int GetDistance(Point other)
	{
		return ( other.row > row ? other.row - row : row - other.row ) + ( other.column > column ? other.column - column : column - other.column );
	}

	@Override
	public int GetDistance(Point_Immutable other)
	{
		return ( other.row > row ? other.row - row : row - other.row ) + ( other.column > column ? other.column - column : column - other.column );
	}
	
	@Override
	public boolean equals(Point other)
	{
		return this.row == other.row && this.column == other.column;
	}

	@Override
	public boolean equals(Point_Immutable other)
	{
		return this == other ? true : this.row == other.row && this.column == other.column;
	}
	
	@Override
	public boolean equals(int row, int column)
	{
		return this.row == row && this.column == column;
	}
	
	@Override
	public String toString()
	{
		if ( this == Constants.Pos_Sky )
			return "(하늘)";
		
		return String.format("(%d, %d)", row, column);
	}
}
