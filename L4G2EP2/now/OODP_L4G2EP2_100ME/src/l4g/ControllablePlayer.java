package l4g;

import l4g.common.DirectionCode;
import l4g.common.Player;
import l4g.common.Point;

public class ControllablePlayer extends Player
{
	public Classroom classroom;

	public ControllablePlayer(int ID, Classroom classroom)
	{
		super(ID, "나");
		
		this.classroom = classroom;

		System.err.println("Error. 직접 조작 가능한 플레이어는 이번 버전에서는 지원되지 않습니다. 며칠 뒤를 기대해 주세요.");
	}

	@Override
	public DirectionCode Survivor_Move()
	{
		return null;
	}

	@Override
	public void Corpse_Stay()
	{
	}

	@Override
	public DirectionCode Infected_Move()
	{
		return null;
	}

	@Override
	public void Soul_Stay()
	{
	}

	@Override
	public Point Soul_Spawn()
	{
		return null;
	}

}
