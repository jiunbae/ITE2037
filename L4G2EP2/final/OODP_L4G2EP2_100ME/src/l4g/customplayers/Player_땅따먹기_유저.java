package l4g.customplayers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import l4g.common.*;

/**
 * 여러분이 새로운 플레이어를 만들기 위해 실제로 작성하게 될 클래스입니다.
 * 
 * @author Racin
 *
 */
public class Player_땅따먹기_유저 extends Player
{
	// TODO#1 Alt + Shift + R을 써서 클래스 이름을 마음에 드는 이름으로 바꾸어 주세요. 클래스 이름과 플레이어 이름은 별개입니다.
	public Player_땅따먹기_유저(int ID)
	{
		
		// TODO#2 아래의 "이름!" 위치에 여러분이 만들 플레이어의 이름을 넣어 주세요. 클래스 이름과 플레이어 이름은 별개입니다.
		super(ID, "땅따먹기_유저");
		
		// TODO#3 직접 감염을 받으려는 경우 이 필드를 true로, 그렇지 않은 경우 false로 설정하세요.
		// 이 필드의 값 자체는 아무 때나 바꿀 수 있지만 실질적으로 한 게임에 직접 감염이 여러 번 발동되는 경우는 매우 드무니 그냥 고정시켜놔도 됩니다.
		this.trigger_acceptDirectInfection = false;
		
		
		// TODO#4 여기까지 왔으면 이제 Developer's Guide 문서와 각 BOT 플레이어 코드를 한 번만 더 읽어 보고 돌아옵시다.
		
		
	}
	
	/*
	 * TODO#5	이제 여러분이 그려 둔 노트를 보며 아래에 있는 다섯 가지 의사 결정 메서드를 완성하세요.
	 * 			당연히 한 방에 될 리 없으니, 중간중간 코드를 백업해 두는 것이 좋으며,
	 * 			코드 작성이 어려울 땐 아무 부담 없이 조교를 찾아 오세요.
	 * 
	 * 			L4G는 여러분의 '생각'을 추구하는 축제지 구글 굴리는 축제가 아닙니다!
	 * 
	 * 			여러분이 이번 축제에서 투자한 시간만큼, 이후 다른 과제 / 다른 업무에서 뻘짓을 벌이는 시간이 줄어들게 될 것입니다.
	 * 			그러니 자신이 뭔가 멋진 생각을 떠올렸다면, 이를 내 플레이어에 적용하기 위해 아낌 없는 노력을 투자해 보세요!
	 * 
	 * 			제출기한이 되어 황급히 파일을 업로드하고 Eclipse로 돌아와 여러분이 작성한 코드를 돌아 보면,
	 * 			'코드에 노력이란게 묻어 날 수도 있구나'라는 생각이 절로 들게 될 것입니다.
	 */
	
	LinkedList< HashMap< Point, DirectionCode > > point_list;
	Point_Immutable last_point;
	DirectionCode curr_direction, last_direction;
	ListIterator< HashMap< Point, DirectionCode > > curr_iterator;
	int prev_count;

	@Override
	public void Corpse_Stay()
	{
		// Note: 극한을 추구하는 것이 아니라면 이 메서드는 걍 비워 둬도 무방합니다.
	}

	@Override
	public void Soul_Stay()
	{
		if ( turnInfo.turnNumber == 0 )
		{
			/*
			 * Note: 영혼 대기 메서드는 L4G 게임이 시작되면 가장 먼저 호출되는 메서드입니다.
			 * 		 이 if문의 내용은 0턴째에만 실행되므로, 이 곳은 여러분이 추가로 만든 변수들을 초기화하는 용도로 쓰기에 참 알맞습니다. 
			 */
			
			HashMap< Point, DirectionCode > new_map;
			
			point_list = new LinkedList< HashMap< Point, DirectionCode > >();
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 12,2 ), DirectionCode.Up );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 8,2 ), DirectionCode.Left );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 8,0 ), DirectionCode.Right );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 8,5 ), DirectionCode.Up );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 5,5 ), DirectionCode.Left );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 5,0 ), DirectionCode.Right );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 5,3 ), DirectionCode.Up );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 2,3 ), DirectionCode.Left );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 2,0 ), DirectionCode.Right );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 2,6 ), DirectionCode.Up );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 0,6 ), DirectionCode.Down );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 6,6 ), DirectionCode.Right );
			point_list.add( new_map );
			
			new_map = new HashMap< Point, DirectionCode >();
			new_map.put( new Point( 6,12 ), null );
			point_list.add( new_map );
			
			
			curr_direction = DirectionCode.Right;
			last_direction = DirectionCode.Right;
			last_point = new Point_Immutable( 12, 0 );
			
			curr_iterator = point_list.listIterator();
		}
	}
	
	@Override
	public DirectionCode Survivor_Move()
	{
		HashMap< Point, DirectionCode > curr_map = curr_iterator.next();
		for( Point point : curr_map.keySet() )
		{
			if( point.equals( myInfo.position ) )
			{
				if( point.row == 0 || point.row == Constants.Classroom_Height-1 ||
						point.column == 0 || point.column == Constants.Classroom_Width-1)
				{
					last_point = myInfo.position;
					last_direction = curr_map.get( point );
					prev_count = 0;
				}
				else
					prev_count++;
				
				curr_direction = curr_map.get( point );
				break;
			}
			
			curr_iterator.previous();
		}
		
		return curr_direction;
	}

	@Override
	public DirectionCode Infected_Move()
	{
		return DirectionCode.Stay;
	}

	@Override
	public Point Soul_Spawn()
	{
		if( turnInfo.turnNumber == 1 )
			return new Point(12, 0);
		
		else if( last_point.equals( new Point( 6,12 ) ) )
		{
			int a = 0;
			a = a / a;
		}
		
		for( int i=0; i<prev_count; i++ )
			curr_iterator.previous();
		
		prev_count = 0;
		
		curr_direction = last_direction;
		return last_point.Copy();
	}
}
