package l4g2ep1;

import l4g2ep1.Classroom.State;
import l4g2ep1.common.*;

/**
 * Presenter를 통해 직접 조작 가능한 플레이어 하나를 나타냅니다.
 * 이 클래스는 상속될 수 없습니다.
 * 
 * @author Racin
 * 
 */
public final class ControllablePlayer extends Player
{
	// 강의실과 연결된 presenter를 사용해야 하므로 강의실에 대한 참조 필드 필요
	Classroom classroom;

	/**
	 * 새로운 조작 가능한 플레이어의 인스턴스를 생성합니다.
	 * 조작 가능한 플레이어는 받을 수 있는 모든 정보를 수신합니다.
	 * 
	 * @param classroom
	 *            게임을 진행할 강의실에 대한 참조입니다.
	 */
	public ControllablePlayer(Classroom classroom)
	{
		this.classroom = classroom;
		name = "나";
		receiveOthersInfo_detected = true;
		receiveActions = true;
		receiveReactions = true;
	}

	@Override
	public DirectionCode Survivor_Move()
	{
		DirectionCode result = DirectionCode.Stay;

		// 강의실을 '대기중' 상태로 바꾸고 presenter에 '의사 결정 요청' 이벤트 전달
		classroom.state = State.Waiting_Decision_Survivor_Move;
		classroom.request_decision.actionPerformed(null);
		
		while (true)
		{
			//presenter에서 의사 결정이 완료될때까지 대기
			synchronized (classroom)
			{
				try
				{
					classroom.wait();
				}
				catch (InterruptedException e)
				{
				}
			}

			// 수신한 의사 결정이 유효하지 않은 경우 '잘못된 의사 결정' 이벤트 전달 후 다시 대기
			if ( IsValidMove(classroom.accepted_point) == false )
				classroom.invalid_decision.actionPerformed(null);
			else
				break;
		}

		/*
		 * 유효한 생존자 의사 결정을 토대로 방향 계산.
		 * 
		 * 생존자는 항상 x / y좌표 중 하나만 +1 / -1만큼 변경 가능하므로
		 * 이동 전 / 후의 x좌표가 같다면 y좌표를 비교하여 아래 / 위 판별,
		 * 이동 전 / 후의 x좌표가 다르다면 x좌표를 비교하여 오른쪽 / 왼쪽 판별.
		 */
		if ( myInfo.position.x == classroom.accepted_point.x )
		{
			if ( myInfo.position.y < classroom.accepted_point.y )
				result = DirectionCode.Down;
			else
				result = DirectionCode.Up;
		}
		else
		{
			if ( myInfo.position.x < classroom.accepted_point.x )
				result = DirectionCode.Right;
			else
				result = DirectionCode.Left;
		}

		// 의사 결정이 끝나면 강의실 상태를 다시 '진행중'으로 설정
		classroom.state = State.Running;

		return result;
	}

	@Override
	public void Corpse_Stay()
	{
		// 강의실을 '대기중' 상태로 바꾸고 presenter에 '의사 결정 요청' 이벤트 전달
		classroom.state = State.Waiting_Decision_Corpse_Stay;
		classroom.request_decision.actionPerformed(null);

		synchronized (classroom)
		{
			// presenter에서 의사 결정이 완료될때까지 대기
			try
			{
				classroom.wait();
			}
			catch (InterruptedException e)
			{
			}
		}

		// 의사 결정이 끝나면 강의실 상태를 다시 '진행중'으로 설정
		classroom.state = State.Running;
	}

	@Override
	public DirectionCode Infected_Move()
	{
		DirectionCode result = DirectionCode.Stay;

		// 강의실을 '대기중' 상태로 바꾸고 presenter에 '의사 결정 요청' 이벤트 전달
		classroom.state = State.Waiting_Decision_Infected_Move;
		classroom.request_decision.actionPerformed(null);

		while (true)
		{
			synchronized (classroom)
			{
				// presenter에서 의사 결정이 완료될때까지 대기
				try
				{
					classroom.wait();
				}
				catch (InterruptedException e)
				{
				}
			}

			// 수신한 의사 결정이 유효하지 않은 경우 '잘못된 의사 결정' 이벤트 전달 후 다시 대기
			if ( IsValidMove(classroom.accepted_point) == false )
				classroom.invalid_decision.actionPerformed(null);
			else
				break;
		}

		/*
		 * 유효한 감염체 의사 결정을 토대로 방향 계산.
		 * 
		 * 감염체는 제자리에 머무르지 않는 경우 항상 x / y좌표 중 하나만 +1 / -1만큼 변경 가능하므로
		 * 이동 전 / 후의 x좌표가 같다면 y좌표를 비교하여 아래 / 위 판별, (y좌표도 같을 수 있으므로 비교를 두 번 수행)
		 * 이동 전 / 후의 x좌표가 다르다면 x좌표를 비교하여 오른쪽 / 왼쪽 판별. (x좌표가 다르다는 전제가 있으므로 비교를 한 번만 수행)
		 */
		if ( myInfo.position.x == classroom.accepted_point.x )
		{
			if ( myInfo.position.y < classroom.accepted_point.y )
				result = DirectionCode.Down;
			if ( myInfo.position.y > classroom.accepted_point.y )
				result = DirectionCode.Up;
		}
		else
		{
			if ( myInfo.position.x < classroom.accepted_point.x )
				result = DirectionCode.Right;
			else
				result = DirectionCode.Left;
		}

		// 의사 결정이 끝나면 강의실 상태를 다시 '진행중'으로 설정
		classroom.state = State.Running;

		return result;
	}

	@Override
	public void Soul_Stay()
	{
		// 강의실을 '대기중' 상태로 바꾸고 presenter에 '의사 결정 요청' 이벤트 전달
		classroom.state = State.Waiting_Decision_Soul_Stay;
		classroom.request_decision.actionPerformed(null);

		synchronized (classroom)
		{
			// presenter에서 의사 결정이 완료될때까지 대기
			try
			{
				classroom.wait();
			}
			catch (InterruptedException e)
			{
			}
		}

		// 의사 결정이 끝나면 강의실 상태를 다시 '진행중'으로 설정
		classroom.state = State.Running;
	}

	@Override
	public Point Soul_Spawn()
	{
		// 강의실을 '대기중' 상태로 바꾸고 presenter에 '의사 결정 요청' 이벤트 전달
		classroom.state = State.Waiting_Decision_Soul_Spawn;
		classroom.request_decision.actionPerformed(null);

		while (true)
		{
			synchronized (classroom)
			{
				// presenter에서 의사 결정이 완료될때까지 대기
				try
				{
					classroom.wait();
				}
				catch (InterruptedException e)
				{
				}
			}

			// 수신한 의사 결정이 유효하지 않은 경우 '잘못된 의사 결정' 이벤트 전달 후 다시 대기
			if ( IsValidSpawn(classroom.accepted_point) == false )
				classroom.invalid_decision.actionPerformed(null);
			else
				break;
		}

		// 의사 결정이 끝나면 강의실 상태를 다시 '진행중'으로 설정
		classroom.state = State.Running;

		return classroom.accepted_point;
	}
}
