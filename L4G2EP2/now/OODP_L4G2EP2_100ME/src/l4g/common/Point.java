package l4g.common;

/**
 * 좌표 하나를 나타내는 클래스입니다.
 * 이 클래스의 인스턴스는 <code>Point_Immutable</code>(주로 각 정보 클래스의 '위치' 필드에서 사용됨) 인스턴스와 호환됩니다.
 * 
 * @author Racin
 *
 */
public class Point extends PointBase
{
	/**
	 * 이 좌표의 세로 방향 성분입니다.
	 * 0인 경우 맨 윗 줄을 나타냅니다.
	 */
	public int row;

	/**
	 * 이 좌표의 가로 방향 성분입니다.
	 * 0인 경우 맨 왼쪽 열을 나타냅니다.
	 */
	public int column;

	public Point(int row, int column)
	{
		this.row = row;
		this.column = column;
	}

	public Point(Point other)
	{
		this.row = other.row;
		this.column = other.column;
	}

	public Point(Point_Immutable other)
	{
		this.row = other.row;
		this.column = other.column;
	}

	public Point(Point other, int offset_row, int offset_column)
	{
		this.row = other.row + offset_row;
		this.column = other.column + offset_column;
	}

	public Point(Point_Immutable other, int offset_row, int offset_column)
	{
		this.row = other.row + offset_row;
		this.column = other.column + offset_column;
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
		return this.row == other.row && this.column == other.column;
	}
	
	@Override
	public boolean equals(int row, int column)
	{
		return this.row == row && this.column == column;
	}
	
	@Override
	public String toString()
	{
		return String.format("(%d, %d)", row, column);
	}
}
