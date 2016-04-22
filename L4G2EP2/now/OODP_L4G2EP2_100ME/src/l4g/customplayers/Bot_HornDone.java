package l4g.customplayers;

import java.util.Random;

import l4g.common.Constants;
import l4g.common.DirectionCode;
import l4g.common.Player;
import l4g.common.Point;

/**
 * 무작위 의사 결정을 수행하는 Bot 플레이어입니다.
 * 무법자긴 하지만 같은 게임 번호, 같은 ID를 사용하면 항상 동일한 의사 결정을 수행합니다.<br>
 * <br>
 * 직접 감염: 50% 확률로 수락합니다.<br>
 * 생존자 이동: 균등 확률로 이동 가능한 방향들 중 하나를 고릅니다.<br>
 * 감염체 이동: 20% 확률로 정화 기도를 시도합니다. 그렇지 않은 경우 균등 확률로 이동 가능한 방향들 중 하나를 고릅니다.<br>
 * 영혼 배치: 균등 확률로 아무 칸이나 고릅니다.<br>
 * <br>
 * <b>주의:</b> 무법자 Bot 플레이어는 정규 게임에서는 사용되지 않습니다.
 * 또한, 여러분이 작성하는 플레이어는 어떤 이유로든 <code>java.util.Random class</code>를 사용할 수 없습니다.
 * 무법자 Bot 플레이어는 단순히 테스트를 위해 '여러분의 플레이어가 고득점을 올리는 상황'을 좀 더 쉽게 연출해 보는 용도로 사용해야 합니다.
 *  
 * @author Racin
 *
 */
public class Bot_HornDone extends Player
{
	/**
	 * 이동할 방향의 우선 순위를 지정하기 위한 배열입니다.
	 * 모든 Bot 플레이어들은 자신의 ID 값에 따라 서로 다른 우선 순위를 가지고 이동을 수행합니다.
	 */
	DirectionCode[] directions;
	
	Random rand;

	public Bot_HornDone(int ID)
	{
		super(ID, "무법자#" + ID);
		
	}

	@Override
	public DirectionCode Survivor_Move()
	{
		DirectionCode result;
		int pos_directions;

		trigger_acceptDirectInfection = rand.nextBoolean();

		//이동 가능한 다음 방향을 선택(네 방향 중 적어도 둘은 항상 이동 가능하므로 무한 루프 사용) 
		while ( true )
		{
			pos_directions = rand.nextInt(4);
			result = directions[pos_directions];
			
			//해당 방향으로 이동 가능하다면 선택
			Point pos_destination = myInfo.position.GetAdjacentPoint(result);
			if ( pos_destination.row >= 0 && pos_destination.row < Constants.Classroom_Height &&
					pos_destination.column >= 0 && pos_destination.column < Constants.Classroom_Width )
				return result;
		}
	}

	@Override
	public void Corpse_Stay()
	{
	}

	@Override
	public DirectionCode Infected_Move()
	{
		DirectionCode result;
		int pos_directions = rand.nextInt(5);
		
		//20% 확률로 Stay 선택
		if ( pos_directions == 4 )
			return DirectionCode.Stay;
		
		//이동 가능한 다음 방향을 선택(네 방향 중 적어도 둘은 항상 이동 가능하므로 무한 루프 사용) 
		while ( true )
		{
			result = directions[pos_directions];
			
			//해당 방향으로 이동 가능하다면 선택
			Point pos_destination = myInfo.position.GetAdjacentPoint(result);
			if ( pos_destination.row >= 0 && pos_destination.row < Constants.Classroom_Height &&
					pos_destination.column >= 0 && pos_destination.column < Constants.Classroom_Width )
				return result;
			
			pos_directions = rand.nextInt(4);
		}
	}

	@Override
	public void Soul_Stay()
	{
		if ( turnInfo.turnNumber == 0 )
		{
			rand = new Random(ID + gameNumber);
			trigger_acceptDirectInfection = rand.nextBoolean();
			
			/*
			 * 이동 우선 순위 설정(ID값에 기반하여 4x3x2x1 = 24가지 경우 중 하나 선택)
			 */
			switch ( ID % 24 )
			{
			case 0:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Left, DirectionCode.Right, DirectionCode.Down};
				break;
			case 1:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Left, DirectionCode.Down, DirectionCode.Right};
				break;
			case 2:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Right, DirectionCode.Left, DirectionCode.Down};
				break;
			case 3:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Right, DirectionCode.Down, DirectionCode.Left};
				break;
			case 4:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Down, DirectionCode.Left, DirectionCode.Right};
				break;
			case 5:
				directions = new DirectionCode[]{DirectionCode.Up, DirectionCode.Down, DirectionCode.Right, DirectionCode.Left};
				break;

			case 6:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Up, DirectionCode.Right, DirectionCode.Down};
				break;
			case 7:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Up, DirectionCode.Down, DirectionCode.Right};
				break;
			case 8:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Right, DirectionCode.Up, DirectionCode.Down};
				break;
			case 9:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Right, DirectionCode.Down, DirectionCode.Up};
				break;
			case 10:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Down, DirectionCode.Up, DirectionCode.Right};
				break;
			case 11:
				directions = new DirectionCode[]{DirectionCode.Left, DirectionCode.Down, DirectionCode.Right, DirectionCode.Up};
				break;
			
			case 12:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Up, DirectionCode.Left, DirectionCode.Down};
				break;
			case 13:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Up, DirectionCode.Down, DirectionCode.Left};
				break;
			case 14:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Left, DirectionCode.Up, DirectionCode.Down};
				break;
			case 15:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Left, DirectionCode.Down, DirectionCode.Up};
				break;
			case 16:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Down, DirectionCode.Up, DirectionCode.Left};
				break;
			case 17:
				directions = new DirectionCode[]{DirectionCode.Right, DirectionCode.Down, DirectionCode.Left, DirectionCode.Up};
				break;

			case 18:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Up, DirectionCode.Left, DirectionCode.Right};
				break;
			case 19:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Up, DirectionCode.Right, DirectionCode.Left};
				break;
			case 20:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Left, DirectionCode.Up, DirectionCode.Right};
				break;
			case 21:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Left, DirectionCode.Right, DirectionCode.Up};
				break;
			case 22:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Right, DirectionCode.Up, DirectionCode.Left};
				break;
			default:
				directions = new DirectionCode[]{DirectionCode.Down, DirectionCode.Right, DirectionCode.Left, DirectionCode.Up};
				break;
			}
		}
	}

	@Override
	public Point Soul_Spawn()
	{
		return new Point(rand.nextInt(Constants.Classroom_Height), rand.nextInt(Constants.Classroom_Width));
	}

}
