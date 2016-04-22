package l4g.customplayers;

import java.util.ArrayList;
import java.util.Arrays;

import l4g.common.*;
import l4g.data.CellInfo;
import l4g.data.PlayerInfo;

/**
 * 
 * @author MaybeS
 *
 */
public class TEJAVA extends Player
{
	/**
	 * Position Variable
	 * nowXY, HEIGHT, WIDTH
	 */
	private int nowX;
	private int nowY;
	private final int HEIGHT = Constants.Classroom_Height;
	private final int WIDTH = Constants.Classroom_Width;
	
	final double SIGHTRANGE = 2.;
	
	/**
	 * direction checker <br>
	 * left, right, up, down;
	 */
	final int[] dy = {0, 0, -1, 1, 0}, dx = {-1, 1, 0, 0, 0};
	final int dirSize = 4;

	/**
	 * sight checker <br>
	 */
	final int[] sy = { -2, -1, -1, -1, 0, 0, 0, 0, 1, 1, 1, 2},
				sx = { 0, -1, 0, 1, -2, -1, 1, 2, -1, 0, 1, 0};
	final int sightSize = 12;

	/**
	 * Expectation function: <br>
	 * CLAS: DEFAULT Map preference <br>
	 * SURV: expect survivor <br>
	 * INFE: expect infected <br>
	 * CORP: expect corps <br>
	 * COST: expect cost = total preference.
	 */
	final int SURV = 0;
	final int INFE = 1;
	final int CORP = 2;
	final int CLAS = 3;
	final int COST = 4;
	final int REVS = 5;
	
	double[][][] expect = new double[5][HEIGHT][WIDTH];
	double[][][] expTmp = new double[5][HEIGHT][WIDTH];
	boolean [][] sight = new boolean [HEIGHT][WIDTH];
	
	/**
	 * Expectation constant: <br>
	 * 									SURV.5<br>	INFE.1<br>	CORP.3<br>	CLAS.8<br>	COST.1<br>	REVS.4
	 */
	private final double[] EXPCONST = 	{0.5, 		0.1, 		0.3, 		0.8, 		0.1,	 	0.4 };

	/**
	 * Direction Probability constant: <br>
	 * 
	 * Survivor can move 4, 1/4 = 0.25
	 * Infected can move 5, 1/5 = 0.2
	 * Corps can't move, 1
	 * 									SURV.25<br>	INFE.2<br>	CORP1
	 */
	private final double[] DIRECTPR = 	{0.25,		0.2,		1 };
	
	
	final int SMAX = 0;
	final int STOT = 1;
	final int CMAX = 2;
	final int CTOT = 3;
	final int IMAX = 4;
	final int ITOT = 4;
	
	/**
	 * Expectation scores: <br>
	 * 									sMax40<br>	sTot700<br>	cMax70<br>	cTot950<br>	iMax25<br>	iTot650
	 */
	private final int[] EXPSCORE = 	{	35,			700,		70,			950,		25,			650	};
	
	boolean[][] isMySights = new boolean[HEIGHT][WIDTH];
	
	
	// 감염자일때 공격을 할것인가?
	boolean trySurvive = true;
	
	
	/**
	 * TEJAVA
	 * Initialize Variables; 
	 */
	public TEJAVA(int ID)
	{
		super(ID, "TEJAVA");
		this.trigger_acceptDirectInfection = false;

		for(int y = 0; y < HEIGHT; ++y)
			for(int x = 0; x < WIDTH; ++x)
			{
				expect[SURV][y][x] = expect[INFE][y][x] = expect[CORP][y][x] = 0;
				sight[y][x] = false;
				
				int yDist = HEIGHT / 2 - y, xDist = WIDTH / 2 - x;
				expect[CLAS][y][x] = Math.pow(Math.E, Math.sqrt(xDist * xDist + yDist * yDist) / ((WIDTH + HEIGHT) / 2) * EXPCONST[CLAS]);
			}
	}
	
	/**
	 * initialize
	 * Update expect Valuables
	 */
	private void initialize() {
		nowX = this.myInfo.position.column;
		nowY = this.myInfo.position.row;
		
		ArrayList<PlayerInfo> survivorSight = new ArrayList<>();
		if(myInfo.state == StateCode.Survivor)
			survivorSight.add(this.myInfo);
		// Initialize Variable, each state
		for (int y = 0; y < HEIGHT; ++y) 
		{
			for (int x = 0; x < WIDTH; ++x) 
			{
				expTmp[SURV][y][x] = expTmp[INFE][y][x] = 0;
				expect[CORP][y][x] = 0;
				isMySights[y][x] = false;
				
				switch (this.myInfo.state)
				{
					case Survivor:
						for ( PlayerInfo another : this.cells[y][x].Select_Players(info -> info.state == StateCode.Survivor) )
							if ( another.position.GetDistance(nowY, nowX) <= SIGHTRANGE )
								survivorSight.add(another);
						break;
					case Infected:
						if ( Math.abs(y - nowY) <= SIGHTRANGE && Math.abs(x - nowX) <= SIGHTRANGE )
							isMySights[y][x] = true;
						break;
					case Soul:
						isMySights[y][x] = true;
						break;
					default:
						break;
				} 
			}
		}
		
		// calculate expect Variables
		// if survivor, += 0.25 each, (cause survivor can move {up, down, left, right})
		// if infected, += 0.20 each, (cause infected can move {up, down, left, right, stay})
		// if corps, += 1, (cause corps can't move)
		for (int y = 0; y < HEIGHT; ++y) {
			for (int x = 0; x < WIDTH; ++x) {
				CellInfo c = this.cells[y][x];
				
				// if Survivor, check valid sight
				for (PlayerInfo pi : survivorSight) 
					if ( pi.position.GetDistance(y, x) <= SIGHTRANGE ) 
					{
						isMySights[y][x] = true;
						break;
					}
				
				double[] pCounts = {
					c.CountIf_Players((info) -> info.state == StateCode.Survivor ),
					c.CountIf_Players((info) -> info.state == StateCode.Infected )
				};
				
				
				for (int k = 0; k < 5; ++k) 
				{
					int ny = y + dx[k];
					int nx = x + dy[k];
					
					if (!isValidPoint(nx, ny)) 
						continue;
					
					// if is not my sight, give less affect using EXPCONST[REVS].
					if (isMySights[y][x])
					{
						expTmp[INFE][ny][nx] += pCounts[INFE] * DIRECTPR[INFE];
						if (k < dirSize)
							expTmp[SURV][ny][nx] += pCounts[SURV] * DIRECTPR[SURV];
					}
					else
					{
						expTmp[INFE][ny][nx] += expect[INFE][y][x] * DIRECTPR[INFE] * EXPCONST[REVS];
						if (k < dirSize)
							expTmp[SURV][ny][nx] += expect[SURV][y][x] * DIRECTPR[SURV] * EXPCONST[REVS];
					}
				}
				
				c.ForEach_Players( (info -> {
					int py = info.position.row, px = info.position.column;
					
					if (info.state == StateCode.Corpse && info.transition_cooldown > 0)
						expect[CORP][py][px] += DIRECTPR[CORP];
					else if (info.state == StateCode.Infected && info.transition_cooldown == 0)
						for (int kk= 0; kk < dirSize + 1; ++kk)
							expect[CORP][py + dx[kk]][px + dy[kk]] += DIRECTPR[INFE];
				}) );
			}
		}
		
		// Update expect Variables
		for (int y = 0; y < HEIGHT; ++y)
			for (int x = 0; x < WIDTH; ++x) 
			{
				expect[INFE][y][x] = expTmp[INFE][y][x];
				expect[SURV][y][x] = expTmp[SURV][y][x];
			}
	}
	
	/**
	 * return if given point is valid
	 */
	private boolean isValidPoint(int Column, int Raw) {
		return !(Column < 0 || Raw < 0 || Raw >= HEIGHT || Column >= WIDTH);
	}
	
	/**
	 * get expect of near position
	 * 
	 * @param exps: array of expect something
	 * @param expRev: value of next next turn expect
	 * @param INF: return value of check if inValidPoint
	 * 
	 * @return array of expect value [left, right, up, down]
	 */
	private double[] getNearExpects(double exps[][], double expRev, double INF, boolean mapPrefer) 
	{
		double exp[] = new double[4];
		
		for (int k = 0; k < dirSize; ++k) 
		{
			int ey = nowY + dy[k]; 
			int ex = nowX + dx[k];
			
			if (!isValidPoint(ex, ey))
			{
				exp[k] = INF;
				continue;
			}
			
			if (mapPrefer)
				exp[k] = exps[ey][ex] * expect[CLAS][ey][ex];
			
			for (int kk = 0; kk < dirSize; ++kk) 
			{
				if (!isValidPoint(ex + dx[kk], ey + dy[kk])) 
					continue;
				exp[k] += exps[ey + dy[kk]][ex + dx[kk]] * expRev;
			}
		}
		return exp;
	}
	
	/**
	 * getDirection given expects.
	 * if get expect, max value of expectation. or get Not expect, min value of expectation.
	 * 
	 * @param type: if expect, true or Not expect false.
	 * @param near: expect of near.
	 *
	 * @return DirectionCode, where to go using expect
	 */
	private DirectionCode getDirection(boolean type, double near[]) 
	{
		DirectionCode ret = null;
		double val = (type ? -Double.MAX_VALUE : Double.MAX_VALUE);
		
		for (int k = 0; k < 4; ++k) 
			if ((!type && near[k] < val) || type && near[k] > val) 
			{
				val = near[k];
				ret = DirectionCode.values()[k];
			}
		
		return ret;
	}

	/**
	 * getDirection(Not)Exp
	 * 
	 * @param exps: array of (Not)expect something
	 * @param expRev: value of next next turn expect
	 * @param mapPrefer: whether using map prefer
	 * 
	 * @return DirectionCode, where (Not) to go using expect
	 */
	
	/**
	 * getDirection that should not go
	 */
	private DirectionCode getDirectionNotExp(double exps[][], double expRev, boolean mapPrefer)
	{
		return getDirection(false, getNearExpects(exps, expRev,  Double.MAX_VALUE, mapPrefer));
	}

	/**
	 * getDirection that should go
	 */
	private DirectionCode getDirectionExp(double exps[][], double expRev, boolean mapPrefer)
	{
		return getDirection(true, getNearExpects(exps, expRev,  -Double.MAX_VALUE, mapPrefer));
	}
	
	@Override
	public DirectionCode Survivor_Move()
	{
		initialize();
		
		if (this.myScore.survivor_max > EXPSCORE[SMAX])
			trySurvive = false;

		//if turn < 10, search. for data
		if (this.turnInfo.turnNumber < 10) 
			if (this.trigger_acceptDirectInfection == true) 
				// this case will be not appear, 
				return getDirectionExp(expect[SURV], EXPCONST[CORP], true);
			else 
				return getDirectionNotExp(expect[SURV], EXPCONST[SURV], true);
		else if (trySurvive) 
		{
			double risks[][] = new double[HEIGHT][WIDTH];
			
			for (int y = 0; y < HEIGHT; ++y) 
				for (int x = 0; x < WIDTH; ++x)
				{
					// calc risk,
					// should go for many infect in my sight, but not my near direction.
					risks[y][x] = expect[INFE][y][x] * (1 - EXPCONST[INFE]);
					for(int k = 0; k < dirSize; ++k)
					{
						if(!isValidPoint(x+dx[k], y+dy[k]))
							continue;
						risks[y][x] += expect[INFE][y+dy[k]][x+dx[k]] * ( EXPCONST[INFE]) - expect[SURV][y+dy[k]][x+dx[k]] * EXPCONST[INFE];
					}
					
					for(int k = 0; k < sightSize; ++k)
					{
						if(!isValidPoint(x+sx[k], y+sy[k]))
						{
							risks[y][x] -= expect[INFE][y][x] / sightSize;
							continue;
						}
						risks[y][x] -= expect[INFE][y+sy[k]][x+sx[k]] * (EXPCONST[INFE]);
					}
				}

			return getDirectionNotExp(risks, EXPCONST[INFE], true);
		}
		else
			//good day to die.
			return getDirectionExp(expect[INFE], EXPCONST[INFE], true);
	}

	@Override
	public void Corpse_Stay()
	{
		initialize();
	}
	
	@Override
	public DirectionCode Infected_Move()
	{
		initialize();
		
		//when infected, not use map prefer
		if (expect[CORP][nowX][nowY] >= EXPCONST[CORP])
			return getDirectionNotExp(expect[CORP], 0, false);
		else
			return DirectionCode.Stay;

	}

	@Override
	public void Soul_Stay()
	{
		initialize();
	}

	@Override
	public Point Soul_Spawn()
	{
		if ( turnInfo.turnNumber == 0 )
			return new Point(WIDTH / 2, HEIGHT / 2);
		else 
		{
			
			Double maxExp = 1 - Double.MAX_VALUE;
			Point p = new Point(HEIGHT/2, WIDTH/2);
			
			//respawn for sMax and sTot;
			if ((this.myScore.survivor_max < EXPSCORE[SMAX] && this.myScore.survivor_total < EXPSCORE[STOT]) &&
					((120 - this.turnInfo.turnNumber) > EXPSCORE[SMAX])) 
			{
				//repeat for safety
				for (int repeat = 0; repeat < dirSize; ++repeat) 
				{
					for (int y = 0; y < HEIGHT; ++y)
						Arrays.fill(expTmp[INFE][y], 0);
					
					for (int y = 0; y < WIDTH; ++y) 
						for (int x = 0; x < WIDTH; ++x) 
						{
							expTmp[INFE][y][x] = expect[INFE][y][x] * (1 - EXPCONST[INFE]);
							for (int k = 0; k < dirSize; ++k) 
							{
								if (!isValidPoint(y + dy[k], x + dx[k])) 
									continue;
								expTmp[INFE][y + dy[k]][x + dx[k]] += expect[INFE][y][x] * DIRECTPR[INFE];
							}
						}
					
					for (int y = 0; y < HEIGHT; ++y)
						for (int x = 0; x < WIDTH; ++x)
							expect[INFE][y][x] = expTmp[INFE][y][x];
				}
				
				
				for (int y = 0; y < HEIGHT; ++y) 
					for (int x = 0; x < WIDTH; ++x) 
					{
						double exp = expect[SURV][y][x] * EXPCONST[SURV] - (expect[INFE][y][x] * expect[CLAS][y][x]);
						
						if (exp > maxExp) 
						{
							maxExp = exp;
							p.row = y; 
							p.column = x;
						}
					}
				trySurvive = true;
				
			}
			else 
			{
				for (int y = 0; y < HEIGHT; ++y) 
					for (int x = 0; x < WIDTH; ++x) 
						if (expect[INFE][y][x] > expect[INFE][p.row][p.column]) 
						{
							p.row = y; 
							p.column = x;
						}
				
				trySurvive = false;
			}
			return p;
		}
	}
}
