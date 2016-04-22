package l4g;

import l4g.Classroom.ClassroomStateCode;
import l4g.common.DirectionCode;
import l4g.common.Player;
import l4g.common.Point;

public class ControllablePlayer extends Player
{
	public Classroom classroom;

	public ControllablePlayer(int ID, Classroom classroom)
	{
		super(ID, "³ª");
		
		this.classroom = classroom;
	}
	
	public DirectionCode GetDirection(Point pointToMove)
	{
		if ( pointToMove.equals(myInfo.position.row - 1, myInfo.position.column) == true )
			return DirectionCode.Up;

		if ( pointToMove.equals(myInfo.position.row, myInfo.position.column - 1) == true )
			return DirectionCode.Left;

		if ( pointToMove.equals(myInfo.position.row, myInfo.position.column + 1) == true )
			return DirectionCode.Right;

		if ( pointToMove.equals(myInfo.position.row + 1, myInfo.position.column) == true )
			return DirectionCode.Down;

		if ( pointToMove.equals(myInfo.position) == true )
			return DirectionCode.Stay;
		
		return null;
	}

	@Override
	public DirectionCode Survivor_Move()
	{
		while ( classroom.callback_RequestDecision != null )
		{
			classroom.DoCallBack(classroom.callback_RequestDecision, ClassroomStateCode.Waiting_Decision_Survivor_Move);
			
			if ( classroom.pos_acceptedFromControllablePlayer.row == -1 )
			{
				int valueToMakeError = 0;
				valueToMakeError = valueToMakeError / valueToMakeError;
			}
			
			if ( classroom.pos_acceptedFromControllablePlayer.GetDistance(myInfo.position) == 1 )
				break;
			
			classroom.DoCallBack(classroom.callback_InvalidDecision);
		}
		
		return GetDirection(classroom.pos_acceptedFromControllablePlayer);
	}

	@Override
	public void Corpse_Stay()
	{
		classroom.DoCallBack(classroom.callback_RequestDecision, ClassroomStateCode.Waiting_Decision_Corpse_Stay);
		
		if ( classroom.pos_acceptedFromControllablePlayer.row == -1 )
		{
			int valueToMakeError = 0;
			valueToMakeError = valueToMakeError / valueToMakeError;
		}
	}

	@Override
	public DirectionCode Infected_Move()
	{
		while ( classroom.callback_RequestDecision != null )
		{
			classroom.DoCallBack(classroom.callback_RequestDecision, ClassroomStateCode.Waiting_Decision_Infected_Move);
			
			if ( classroom.pos_acceptedFromControllablePlayer.row == -1 )
			{
				int valueToMakeError = 0;
				valueToMakeError = valueToMakeError / valueToMakeError;
			}
			
			if ( classroom.pos_acceptedFromControllablePlayer.GetDistance(myInfo.position) <= 1 )
				break;
			
			classroom.DoCallBack(classroom.callback_InvalidDecision);
		}
		
		return GetDirection(classroom.pos_acceptedFromControllablePlayer);
	}

	@Override
	public void Soul_Stay()
	{
		classroom.DoCallBack(classroom.callback_RequestDecision, ClassroomStateCode.Waiting_Decision_Soul_Stay);
		
		if ( classroom.pos_acceptedFromControllablePlayer.row == -1 )
		{
			int valueToMakeError = 0;
			valueToMakeError = valueToMakeError / valueToMakeError;
		}
	}

	@Override
	public Point Soul_Spawn()
	{
		classroom.DoCallBack(classroom.callback_RequestDecision, ClassroomStateCode.Waiting_Decision_Soul_Spawn);
		
		if ( classroom.pos_acceptedFromControllablePlayer.row == -1 )
		{
			int valueToMakeError = 0;
			valueToMakeError = valueToMakeError / valueToMakeError;
		}
		
		return classroom.pos_acceptedFromControllablePlayer;
	}
}
