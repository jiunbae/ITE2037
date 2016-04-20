package TEST;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class TEST {
	List<String> names = new ArrayList<String>();
	List<List<Double>> results = new ArrayList<List<Double>>();
	List<List<List<Integer>>> scores = new ArrayList<List<List<Integer>>>();
	
	
	List<String> types = new ArrayList<String>();
	List<Integer> typeCnt = new ArrayList<Integer>();
	List<List<Double>> typeRes = new ArrayList<List<Double>>();
	List<Double> resTotal = new ArrayList<Double>();
	
	//List<Integer> as a score of one round;
	//List<List<Integer>> as a score of rounds;
	//List<List<List<Integer>>> as a score of all;
	
	public void push(String name, List<Integer> score)
	{
		for (int i = 0; i < names.size(); i++)
		{
			if(names.get(i).equals(name))
			{
				scores.get(i).add(score);
				return;
			}
		}
		names.add(name);
		resTotal.add(0.0);
		List<List<Integer>> newScores = new ArrayList<List<Integer>>();
		newScores.add(score);
		scores.add(newScores);
	}
	public void org()
	{
		for(int player = 0; player < scores.size(); ++player)
		{
			List<Double> scaa = new ArrayList<Double>();
			for(int scorecnt = 0; scorecnt <scores.get(player).get(0).size(); ++ scorecnt)
			{
				int sca = 0;
				for(int games = 0; games < scores.get(player).size(); ++games)
				{
					sca += scores.get(player).get(games).get(scorecnt);
				}
				scaa.add((double) (sca / scores.get(player).size()));
			}
			results.add(scaa);
			
			for(int games = 0; games < scores.get(player).size(); ++games)
			{
				resTotal.set(player, resTotal.get(player) + scores.get(player).get(games).get(18));
			}
		}
		org2();
		return;
	}
	public void org2()
	{
		for(int player = 0; player < scores.size(); ++player)
		{
			int idx = 0;
			boolean chk = false;
			for (int i = 0; i < types.size(); i++)
			{
				if(types.get(i).equals(names.get(player).substring(0, 3)))
				{
					chk = true;
					typeCnt.set(i, typeCnt.get(i) + 1);
					break;
				}
			}
			if(!chk)
			{
				types.add(names.get(player).substring(0, 3));
				List<Double> newRet = new ArrayList<Double>();
				for(int i = 0; i < results.get(player).size(); i++)
					newRet.add(0.0);
				typeRes.add(newRet);
				typeCnt.add(1);
			}
			idx = types.indexOf(names.get(player).substring(0, 3));
			
			for(int i = 0; i < results.get(0).size(); i++)
				typeRes.get(idx).set(i, typeRes.get(idx).get(i) + results.get(player).get(i));
		}
		for(int type = 0; type < types.size(); ++type)
		{
			for(int s = 0; s < typeRes.get(type).size(); ++s)
			{
				typeRes.get(type).set(s, (double) (typeRes.get(type).get(s) / typeCnt.get(type)));
			}
		}
	}
	public void show(String name) throws IOException
	{
		FileOutputStream out = new FileOutputStream(name);

		out.write("name\tsMax\t\t\tSTot\t\t\tCMax\t\t\tCTot\t\t\tIMax\t\t\tITot\t\t\tFinal\r\n".getBytes());
		for(int i = 0; i < types.size(); ++i)
		{
			String s = types.get(i) + ":";
			for(int j = 0; j < typeRes.get(i).size(); ++j)
			{
				s += "\t" + Integer.parseInt(String.valueOf(Math.round(typeRes.get(i).get(j))));
			}
			s+="\r\n";
			out.write(s.getBytes());
		}
		
		out.write("name\t\tsMax\t\t\tSTot\t\t\tCMax\t\t\tCTot\t\t\tIMax\t\t\tITot\t\t\tFinal\r\n".getBytes());
		for(int i = 0; i < names.size(); i++)
		{
			String s = names.get(i) + ":";
			for(int j = 0; j < results.get(i).size(); j++)
			{
				s+= "\t" +results.get(i).get(j);
			}
			s+= "\t" + resTotal.get(i);
			s+= "\r\n";
			out.write(s.getBytes());
		}

		out.close();
	}
}
