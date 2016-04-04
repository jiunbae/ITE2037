package l4g2ep1;

import java.util.ArrayList;

/**
 * 게임 내에서 각 플레이어의 현재 상태를 나타내기 위해 강의실 내부에서 사용하는 데이터를 담는 클래스입니다.
 * 이 클래스는 강의실 내부에서만 사용되며 여러분은 이 클래스를 볼 수 없습니다.
 * 
 * @author Racin
 * 
 */
class PlayerStat
{
	// 시체 또는 영혼 상태에서 다음 상태로 전이되기까지 남은 턴 수
	int transition_cooldown;

	
	// 생존자 상태에서 이번 턴에 직접 감염을 수락했는지 여부
	boolean survivor_acceptDirectInfection;
	
	// 생존자 상태에서 이번 턴에 사망했는지 여부
	boolean survivor_deadAtThisTurn;

	
	// 감염체 상태에서 이번 턴에 제자리에 머물렀는지 여부
	boolean infected_prayingAtThisTurn;
	
	// 감염체 상태에서 이번 턴에 시체에게 치유받았는지 여부 
	boolean infected_healedAtThisTurn;

	
	// 생존자 상태에서 현재까지 죽지 않고 연속으로 살아남은 턴 수
	int current_survive_point;
	
	// 이번 시체 상태에서 치유한 플레이어 ID 목록
	ArrayList<Integer> current_healed_players;

	// 감염체 상태에서 현재까지 소멸하지 않고 연속으로 처치한 횟수
	int current_kill_point;

	/*
	 * 이 클래스는 강의실 내부에서만 사용되며
	 * 여러분은 이 클래스를 전혀 사용할 수 없습니다.
	 */
	PlayerStat()
	{
		transition_cooldown = 0;

		survivor_acceptDirectInfection = false;
		infected_healedAtThisTurn = false;

		infected_prayingAtThisTurn = false;
		infected_healedAtThisTurn = false;

		current_survive_point = 0;
		current_healed_players = new ArrayList<Integer>();
		current_kill_point = 0;
	}
}
