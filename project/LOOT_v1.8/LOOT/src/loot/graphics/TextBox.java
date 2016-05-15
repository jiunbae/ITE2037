package loot.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

/**
 * 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에 문자열을 적기 위해 사용하는 클래스입니다.<br>
 * <br>
 * <b>주의:</b><br>
 * 이 클래스는 디버그 및 테스트 용도로 사용하기 위해 만들어졌으며<br>
 * 여러 이유로 인해 이 클래스를 최종 버전 프로그램에서 사용하는 것은 바람직하지 않습니다.<br>
 * 프로그램 내에서 문자열을 보여 줄 필요가 있는 경우에는<br>
 * 해당 문자열을 미리 그린 다음 그림 파일로 만들어 사용하는 것이 좋겠습니다.
 *  
 * @author Racin
 *
 */
public class TextBox extends VisualObject
{
	/**
	 * 영역 내에 적으려 하는 문자열입니다.
	 */
	public String text;
	
	/**
	 * 문자열을 적을 때 사용할 Font입니다.<br>
	 * 이 필드를 null로 두는 경우 GameFrame에서 설정해 둔 기본 글자체를 사용합니다.<br>
	 * 기본값은 null입니다.
	 */
	public Font font = null;
	
	/**
	 * 각 줄 사이의 간격(픽셀 수)입니다.<br>
	 * 기본값은 2입니다.
	 */
	public int margin_between_lines = 2;
	
	/**
	 * 문자열을 적을 때 고려할 왼쪽 여백(픽셀 수)입니다.<br>
	 * 기본값은 8입니다.
	 */
	public int margin_left = 8;
	
	/**
	 * 문자열을 적을 때 고려할 윗 여백(픽셀 수)입니다.<br>
	 * 기본값은 8입니다.
	 */
	public int margin_top = 8;
	
	/**
	 * 문자열을 적을 때 사용할 전경색(문자 자체의 색)입니다.<br>
	 * 기본값은 Color.BLACK(검은 색)입니다.
	 */
	public Color foreground_color = Color.BLACK;

	/**
	 * 문자열을 적을 때 사용할 배경색입니다.<br>
	 * 기본값은 Color.WHITE(흰 색)입니다.
	 */
	public Color background_color = Color.WHITE;

	public TextBox()
	{
		text = "";
	}
	
	public TextBox(String text)
	{
		this.text = text;
	}

	public TextBox(int width, int height)
	{
		super(width, height);
		text = "";
	}

	public TextBox(int width, int height, String text)
	{
		super(width, height);
		this.text = text;
	}

	public TextBox(int x, int y, int width, int height)
	{
		super(x, y, width, height);
		text = "";
	}

	public TextBox(int x, int y, int width, int height, String text)
	{
		super(x, y, width, height);
		this.text = text;
	}

	/**
	 * text 필드에 설정된 문자열을 적습니다.
	 * 
	 * @param g
	 * 		GameFrame에는 g 라는 필드가 들어 있습니다.<br>
	 * 		여러분이 Draw(g)를 직접 호출할 때(게임 화면에 직접 그릴 때)는 더 고민하지 말고 그 필드를 그냥 넣으면 됩니다.
	 */
	@Override
	public void Draw(Graphics2D g)
	{
		//g에 설정되어 있던 색상 및 Font 정보 백업
		Color original_color = g.getColor();
		Font original_font = g.getFont();
		
		//문자 크기 지정 - font 필드에 설정된 값, 또는 g가 가진 기본값
		int font_size;
		
		if ( font == null )
			font_size = original_font.getSize();
		else
			font_size = font.getSize();
		
		//배경 칠하기
		g.setColor(background_color);
		g.fillRect(x, y, width, height);
		
		//문자열 적기
		g.setColor(foreground_color);
		g.setFont(font);
		StringTokenizer lines = new StringTokenizer(text, "\n");
		
		int x_line = x + margin_left;
		int y_line = y + margin_top + font_size;
		
		while ( lines.hasMoreTokens() == true )
		{
			g.drawString(lines.nextToken(), x_line, y_line);
			
			y_line += font_size;
			y_line += margin_between_lines;
		}

		//백업해 둔 색상 및 Font 정보 복원
		g.setFont(original_font);
		g.setColor(original_color);
	}
}
