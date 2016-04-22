package l4g;

import java.awt.Color;
import java.awt.Graphics2D;

import loot.graphics.Layer;
import loot.graphics.TextBox;

public class RankMeter extends Layer
{
	private TextBox tb_title;
	private TextBox[] tb_names;
	private TextBox[] tb_ranks;

	public String[] names;
	public long[] scores;
	
	private final static int line_height = 24;
	
	public RankMeter(double pos_x, double pos_y, double pos_z, String title)
	{
		super(pos_x, pos_y, pos_z, 90, line_height * 4);
		
		tb_title = new TextBox(0, 0, 180, line_height);
		tb_title.foreground_color = Color.BLUE;
		tb_title.margin_left = 3;
		tb_title.margin_top = 3;
		children.add(tb_title);
		tb_names = new TextBox[5];
		tb_ranks = new TextBox[5];
		
		tb_title.text = title;
		names = new String[5];
		scores = new long[5];
		
		for ( int iRank = 0; iRank < 5; iRank++ )
		{
			tb_names[iRank] = new TextBox(0, line_height * (iRank + 1), 125, line_height);
			tb_names[iRank].margin_left = 3;
			tb_names[iRank].margin_top = 3;
			children.add(tb_names[iRank]);
			tb_ranks[iRank] = new TextBox(125, line_height * (iRank + 1), 55, line_height);
			tb_ranks[iRank].margin_left = 3;
			tb_ranks[iRank].margin_top = 3;
			children.add(tb_ranks[iRank]);

			names[iRank] = "아직 안 정해짐!";
			scores[iRank] = 0;
		}
	}
	
	public void PopUp()
	{
		if ( pos_z <= -7.90 )
			pos_z = -7.7;
		else
			pos_z += 0.01;
	}
	
	@Override
	public void Draw(Graphics2D g_origin)
	{
		for ( int iRank = 0; iRank < 5; iRank++ )
		{
			byte[] origin_asciis = names[iRank].getBytes();
			byte[] new_asciis = new byte[20];
			
			for ( int iAscii = 0; iAscii < 20; ++iAscii )
			{
				if ( iAscii < origin_asciis.length )
					new_asciis[iAscii] = origin_asciis[iAscii];
				else
					new_asciis[iAscii] = ' ';
			}
			
			String newName = new String(new_asciis);
			
			if ( iRank == 0 && names[iRank].equals("아직 안 정해짐!") == false && newName.equals(tb_names[iRank].text) == false )
				PopUp();
			
			tb_names[iRank].text = newName;
			tb_ranks[iRank].text = String.format("%7d", scores[iRank]);
		}
		
		if (pos_z > -8 )
			pos_z -= 0.001;
		
		super.Draw(g_origin);
	}
}
