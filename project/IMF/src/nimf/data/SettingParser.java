package nimf.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Setting Parser Class
 * 
 * simple setting parser
 * 
 * @package	imf.data
 * @author MaybeS
 * @version 1.0.0
 */
public class SettingParser 
{	
	@SuppressWarnings("unchecked")
	public static void parse(String name, @SuppressWarnings("rawtypes") HashMap path)
	{
		appendDefault(path);
		try {
			BufferedReader br = new BufferedReader(new FileReader(name));
			while(true) 
			{
				String line = br.readLine();
				if (line == null) 
					break;
				else
				{
					String s[] = line.split("=");
					
					// replacer
					{
						int first = s[1].indexOf("$"), last = s[1].lastIndexOf("$");
						if (first != -1 && last != -1 && first != last)
							s[1] = s[1].replace(s[1].substring(first, last + 1), (String)path.get(s[1].substring(first + 1, last)));
						s[1] = s[1].replace("//", "/");
					}
					path.put(s[0], s[1]);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ee) {
			ee.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void appendDefault(@SuppressWarnings("rawtypes") HashMap path)
	{
		path.put("res", "res/");
		path.put("ui", "res/ui/");
		path.put("data", "data/");
		path.put("map", "data/map/");
		path.put("default", "data/default/");
		path.put("ver", "0.21");
	}
}
