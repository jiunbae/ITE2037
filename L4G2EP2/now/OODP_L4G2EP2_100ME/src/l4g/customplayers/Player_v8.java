package l4g.customplayers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import l4g.common.*;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;

/**
 * 
 * @author Prev
 *
 */
public class Player_v8 extends Player
{
	
	//현재 위치 좌표. 짧은 코드를 위해 따로선언
	// X = myInfo.position.column
	// Y = myInfo.position.row
	private int nowX;
	private int nowY;
	
	
	// 맵 크기. 짧은 코드를 위해 따로 선언
	final int MAP_H = Constants.Classroom_Height;
	final int MAP_W = Constants.Classroom_Width;
	
	/**
	 * 맵 이동시 변화량 배열
	 * 좌, 우, 상, 하, 제자리 순서
	 */
	final int[] di = {0, 0, -1, 1, 0};
	final int[] dj = {-1, 1, 0, 0, 0};
	
	
	/**
	 * 예측값 (Expectation)에 관련된 변수들
	 * 타일별 예상 플레이어 
	 * exp_surv: 예상 생존자
	 * exp_infs: 예상 감염자
	 * exp_corp: 예상 시체
	 * 
	 * exp_surv_tmp 및 exp_infs_tmp 는 이전 데이터를 사용하기 위해 임시로 사용되는 변수이다
	 * EXP_REV 는 예상 보정치로, 이전 데이터를 사용할시 신뢰도를 낮추는데 사용한다(Expectation Revision value)
	 */
	double[][] exp_surv = new double[MAP_H][MAP_W];
	double[][] exp_infs = new double[MAP_H][MAP_W];
	double[][] exp_corp = new double[MAP_H][MAP_W];
	
	double[][] exp_surv_tmp = new double[MAP_H][MAP_W];
	double[][] exp_infs_tmp = new double[MAP_H][MAP_W];
	
	final double EXP_REVS = 0.7;
	
	
	/**
	 * 맵에따른 위험도 가중치
	 * 중앙은 1이며 가장자리는 e다
	 * 가장자리로 갈수록 급격하게 위험해지기 때문에 지수함수 e^x의 분포를 따른다
	 */
	double[][] mapWeighting = new double[MAP_H][MAP_W];
	boolean[][] isMySights = new boolean[MAP_H][MAP_W];
	
	
	// 감염자일때 공격을 할것인가?
	//boolean attackWhenInfs = false;
	boolean trySurvive = true;
	
	
	/**
	 * 생성자
	 */
	public Player_v8(int ID)
	{
		super(ID, "v8_");
		this.trigger_acceptDirectInfection = false;
		
		
		// 멤버 변수 초기화
		for (int i=0; i<MAP_H; i++) {
			for (int j=0; j<MAP_W; j++) {
				exp_surv[i][j] = exp_infs[i][j] = exp_corp[i][j] = 0;
				isMySights[i][j] = false;
				
				int ti = (Constants.Classroom_Height / 2) - i;
				int tj = (Constants.Classroom_Width / 2) - j;
				mapWeighting[i][j] = Math.pow(Math.E,  Math.sqrt(ti * ti + tj * tj) / (Constants.Classroom_Width * 0.7) ); // 0.7은 2/sqrt(2)의 근사값임
			}
		}
	}
	
	/**
	 * 이 점이 유효한 점인지 체크한다
	 * @param x: Col
	 * @param y: Row
	 */
	private boolean isValidPoint(int x, int y) {
		return !(x < 0 || y < 0 || y >= Constants.Classroom_Height || x >= Constants.Classroom_Width);
	}
	
	
	/**
	 * 해당턴을 초기화함
	 * 생존자, 감염자의 예측값을 갱신하는것이 주요 목적이다
	 */
	private void initThisTurn() {
		int i, j, k;
		nowX = this.myInfo.position.column;
		nowY = this.myInfo.position.row;
		
		ArrayList<PlayerInfo> survivorsWithinSight = new ArrayList<>();
		if (this.myInfo.state == StateCode.Survivor)
			survivorsWithinSight.add( this.myInfo );
		
		// 변수들을  초기화해주고, 유효 시야를 얻거나 얻을 수 있게 준비한다 (생존자)
		for (i=0; i<MAP_H; i++) {
			for (j=0; j<MAP_W; j++) {
				exp_surv_tmp[i][j] = exp_infs_tmp[i][j] = 0;
				exp_corp[i][j] = 0;
				isMySights[i][j] = false;
				
				if (this.myInfo.state == StateCode.Survivor) {
					for ( PlayerInfo another : this.cells[i][j].Select_Players(pi -> pi.state == StateCode.Survivor) )
						if ( another.position.GetDistance(nowY, nowX) <= 2 )
							survivorsWithinSight.add(another);
				
				}else if (this.myInfo.state == StateCode.Infected) {
					if ( Math.abs(i-nowY) <= 2 && Math.abs(j-nowX) <=2 )
						isMySights[i][j] = true;
				
				}else if (this.myInfo.state == StateCode.Soul) {
					isMySights[i][j] = true;
				}
			}
		}
		
		
		for (i=0; i<MAP_H; i++) {
			for (j=0; j<MAP_W; j++) {
				CellInfo c = this.cells[i][j];
				
				// 생존자시 유효시야 체크
				for (PlayerInfo pi : survivorsWithinSight) {
					if ( pi.position.GetDistance(i, j) <= 2 ) {
						isMySights[i][j] = true;
						break;
					}
				}
				
				double[] pCounts = {
					c.CountIf_Players((pi) -> pi.state == StateCode.Survivor ),
					c.CountIf_Players((pi) -> pi.state == StateCode.Infected )
				};
				
				
				for (k=0; k<5; k++) {
					int ni = i + di[k];
					int nj = j + dj[k];
					
					if (!isValidPoint(nj, ni)) continue;
					
					
					if (isMySights[i][j]) {
						exp_infs_tmp[ni][nj] += pCounts[1] * 0.2; // 감염체가 발견되었을때 이들이 제자리 혹은 4방향으로 움직일 확률은 각각 20%
						if (k < 4) exp_surv_tmp[ni][nj] += pCounts[0] * 0.25; // 생존자가 발견되었을때 이들이 4방향으로 움직일 확률은 각각 25%
						
					}else {
						// 발견되지 않았을시 이전데이터를 활용한다
						//이는 실제 발견치보다 영향을 덜주어야하며 스스로 소멸할 가능성도 있기에 보정치(EXP_REV)를 곱해준다.
						exp_infs_tmp[ni][nj] += exp_infs[i][j] * 0.2 * EXP_REVS;
						if (k < 4) exp_surv_tmp[ni][nj] += exp_surv[i][j] * 0.25 * EXP_REVS;
					}
				}
				
				// 시체 처리
				c.ForEach_Players( (pi -> {
					int i2 = pi.position.row, j2 = pi.position.column;
					
					if (pi.state == StateCode.Corpse && pi.transition_cooldown > 0) {
						exp_corp[i2][j2] += 1;
					
					}else if (pi.state == StateCode.Infected && pi.transition_cooldown == 0) {
						for (int x=0; x<5; x++)
							exp_corp[i2 + di[x]][j2 + dj[x]] += 0.2;
					}
				}) );
			}
		}
		
		// 데이터 반영
		for (i=0; i<MAP_H; i++) {
			for (j=0; j<MAP_W; j++) {
				exp_infs[i][j] = exp_infs_tmp[i][j];
				exp_surv[i][j] = exp_surv_tmp[i][j];
			}
		}
	}
	
	
	/**
	 * 근처 (좌, 우, 상, 하) 점들의 예측값을 계산한다
	 * @param exps: 예측하기 위한 데이터 배열이다. 감염자를 찾고싶으면 exp_infs, 생존자를 찾고싶으면 exp_surv를 넣으면 된다
	 * @param futureExpRevision: 다음다음턴에 감영자나 생존자가 올 수 있을텐데 그 확률은 어느정도로 할까나의 비율이다
	 * @param valueWhenOutOfMap: 맵을 벗어났을때 어떤걸 넣어줄지 설정할수 있다. risk라면 MAX를 preference라면 MIN을 넣어주면 좋을거다
	 * @return [좌,우,상,하] 순서의 예측계산값 배열은 리턴한다
	 */
	private double[] getNearExpects(double exps[][], double futureExpRevision, double valueWhenOutOfMap, boolean useMapWeighting) {
		double exp[] = new double[4];  //좌, 우, 상, 하 순서
		for (int k=0; k<4; k++) {
			int ei = nowY + di[k]; int ej = nowX + dj[k];
			if (!isValidPoint(ej, ei)) continue;
			
			if (useMapWeighting)
				exp[k] = exps[ei][ej] * mapWeighting[ei][ej];
			
			for (int i=0; i<4; i++) {
				if (!isValidPoint(ej + dj[i], ei + di[i])) continue;
				exp[k] += exps[ei + di[i]][ej + dj[i]] * futureExpRevision;
			}
		}
		
		// 밖으로 벗어나가지 못하게
		if (nowX == 0) exp[0] = valueWhenOutOfMap;
		if (nowY == 0) exp[2] = valueWhenOutOfMap;
		if (nowX == Constants.Classroom_Width-1) exp[1] = valueWhenOutOfMap;
		if (nowY == Constants.Classroom_Height-1) exp[3] = valueWhenOutOfMap;
		
		return exp;
	}
	
	 */
	final int[] di = {0, 0, -1, 1, 0};
	final int[] dj = {-1, 1, 0, 0, 0};
	
	/**
	 * 예측값에 따라 알맞은 방향을 얻는다
	 * 3가지 방법으로 쓸 수 있다
	 * 
	 * 공통 인자
	 *  @param type: "max"또는 "min"을 넣어준다. "max"이면 예측값중 가장 큰곳으로 가고 "min"이면 작은곳으로 간다. (Boolean type이 아닌 이유는 가독성을 위해)
	 *  
	 * 첫번째 메서드
	 *  @param nearExp: getNearExpects 메서드로 호출된 값을 넣으면 된다
	 *  
	 * 두세번째 메서드는 getNearExpects 인자를 받아온다고 보면 된다.
	 */
	private DirectionCode getDirectionByExp(String type, double nearExp[]) {
		DirectionCode dc = null;
		double val = (type == "min" ? Double.MAX_VALUE : -Double.MAX_VALUE);
		
		for (int k=0; k<4; k++) {
			if ((type == "min" && nearExp[k] < val) || type == "max" && nearExp[k] > val) {
				val = nearExp[k];
				dc = DirectionCode.values()[k];
			}
		}
		return dc;
	}
	private DirectionCode getDirectionByExp(String type, double exps[][], double futureExpRevision, boolean useMapWeighting) {
		double valueWhenOutOfMap = (type == "min" ? Double.MAX_VALUE : -Double.MAX_VALUE);
		return getDirectionByExp(type, getNearExpects(exps, futureExpRevision, valueWhenOutOfMap, useMapWeighting));
	}
	private DirectionCode getDirectionByExp(String type, double exps[][], double futureExpRevision) {
		return getDirectionByExp(type, exps, futureExpRevision, true);
	}
	
	
	
	
	
	@Override
	public DirectionCode Survivor_Move()
	{
		initThisTurn();
		
		if (this.myScore.survivor_max > 60)
			trySurvive = false;
		
		
		if (this.turnInfo.turnNumber < 10) {
			if (this.trigger_acceptDirectInfection == true) {
				return getDirectionByExp("max", exp_surv, 0.1);
			}else {
				return getDirectionByExp("min", exp_surv, 0.05);
			}
		
		}else if (trySurvive) {
			// 생존자가 많으며 감염자가 적은곳으로 이동하게 한다
			double risks[][] = new double[MAP_H][MAP_W];
			for (int i=0; i<MAP_H; i++) 
				for (int j=0; j<MAP_W; j++)
					risks[i][j] = exp_infs[i][j] - exp_surv[i][j] * 0.57;
			
			return getDirectionByExp("min", risks, 0.04); //map weighting 때문에 min을 활용해야함
			
		}else {
			return getDirectionByExp("max", exp_infs, 0.04);
		}
		
	}

	@Override
	public void Corpse_Stay()
	{
		initThisTurn();
	}
	
	@Override
	public DirectionCode Infected_Move()
	{
		initThisTurn();
		
		if (exp_corp[nowX][nowY] >= 0.2)
			return getDirectionByExp("min", exp_corp, 0, false);
		else
			return DirectionCode.Stay; // 정화기도

	}

	@Override
	public void Soul_Stay()
	{
		initThisTurn();
	}

	@Override
	public Point Soul_Spawn()
	{
		//initThisTurn();
		
		int i, j, k;
		
		if ( turnInfo.turnNumber == 0 )
			return new Point(Constants.Classroom_Width / 2, Constants.Classroom_Height / 2);
		
		else {
			
			Double maxExp = 1 - Double.MAX_VALUE;
			Point p = new Point(0, 0);
			
			if ((this.myScore.survivor_max < 25 && this.myScore.survivor_total < 200) && ((120 - this.turnInfo.turnNumber) - this.myScore.survivor_max > 40)) {
				// 발견점수를 올리기 좋은곳에서 리스폰한다
				
				// 예상치를 n번정도 반복시켜 가장 안전한곳을 찾는다
				for (int repeat=0; repeat<5; repeat++) {
					for (i=0; i<MAP_H; i++)
						Arrays.fill(exp_infs_tmp[i], 0);
					
					for (i=0; i<MAP_H; i++) {
						for (j=0; j<MAP_W; j++) {
							exp_infs_tmp[i][j] = exp_infs[i][j] * 0.8;
							for (k=0; k<4; k++) {
								if (!isValidPoint(j+dj[k], i+di[k])) continue;
								exp_infs_tmp[i + di[k]][j + dj[k]] += exp_infs[i][j] * 0.2;
							}
						}
					}
					
					for (i=0; i<MAP_H; i++)
						for (j=0; j<MAP_W; j++)
							exp_infs[i][j] = exp_infs_tmp[i][j];
				}
				
				
				for (i=0; i<MAP_H; i++) {
					for (j=0; j<MAP_W; j++) {
						double d = exp_surv[i][j] * 0.05 - (exp_infs[i][j] * mapWeighting[i][j]);
						
						if (d > maxExp) {
							maxExp = d;
							p.row = i; p.column = j;
						}
					}
				}
				
				//System.out.printf("Response to: (%d, %d) / Val: ", p.row, p.column);
				//System.out.println(maxExp);
				trySurvive = true;
				
			}else {
				// 가장 감염체가 많을것으로 예상되는 타일에서 리스폰한다.
				for (i=0; i<MAP_H; i++) {
					for (j=0; j<MAP_W; j++) {
						if (exp_infs[i][j] > maxExp) {
							maxExp = exp_infs[i][j];
							p.row = i; p.column = j;
						}
					}
				}
				
				trySurvive = false;
			}
			return p;
		}
	}
}
