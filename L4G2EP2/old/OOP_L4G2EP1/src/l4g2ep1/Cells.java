package l4g2ep1;

import l4g2ep1.common.*;

/**
 * 강의실 내의 모든 칸들에 대한 정보를 담고 있는 클래스입니다.
 * 여러분은 이 클래스를 볼 수 없으며
 * 대신 Player class에 정의된 getter 메서드들을 사용하여 자신의 시야 범위에 있는 칸에 대한 정보만 선택적으로 받아 볼 수 있습니다. 
 * 
 * @author Racin
 *
 */
class Cells
{
	/*
	 * 강의실 내의 각 칸에 대한 정보를 담는 2차원 배열입니다.
	 * 
	 * --> 2차원 평면을 나타내는 2차원 배열을 쓸 때 좌표 순서는 일반적으로 arr[y][x]를 사용합니다. 
	 */
	CellInfo[][] data;

	/*
	 * 여러분이 직접 이 클래스의 인스턴스를 만들 수는 없습니다.
	 * 여러분의 시야 범위 내에 있는 각 칸에 대한 정보는 Player class에 정의된 getter 메서드들을 사용하여 받아 볼 수 있습니다.
	 */
	Cells()
	{
		data = new CellInfo[Constants.Classroom_Height][Constants.Classroom_Width];
		
		Reset();
	}

	/*
	 * 칸 정보를 '모든 칸이 빈 칸'인 것으로 간주하도록 재설정합니다.
	 */
	void Reset()
	{
		for ( int iRow = 0; iRow < Constants.Classroom_Height; ++iRow )
			for ( int iColumn = 0; iColumn < Constants.Classroom_Width; ++iColumn )
				data[iRow][iColumn] = CellInfo.Blank;
	}
}
