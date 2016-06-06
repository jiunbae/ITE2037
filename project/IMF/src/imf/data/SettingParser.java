package imf.data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import loot.GameFrameSettings;

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
	GameFrameSettings settings;
	public SettingParser(String name, GameFrameSettings settings)
	{
		this.settings = settings;
		defaultSetting();

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
					appendSetting(s[0], s[1]);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ee) {
			ee.printStackTrace();
		}	
	}
	
	private void defaultSetting()
	{
		appendSetting("ui", "res/ui/");
		appendSetting("res", "res/");
		appendSetting("map", "data/map/");
		appendSetting("ver", "0.13");
	}
	
	private void appendSetting(String key, String value)
	{
		settings.path.put(key, value);
	}
}
