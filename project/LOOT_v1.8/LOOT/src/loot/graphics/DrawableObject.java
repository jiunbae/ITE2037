package loot.graphics;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Image를 사용하여 2차원 평면 내(게임 화면 자체, 또는 Layer 안)에 그릴 수 있는 요소 하나를 나타냅니다.
 * 
 * @author Racin
 *
 */
public class DrawableObject extends VisualObject
{
	/**
	 * 이 요소를 그릴 때 사용할 Image입니다.<br>
	 * GameFrame이 가지고 있는 images.GetImage()를 호출하면 이 필드를 좀 더 간편하게 다룰 수 있습니다.<br>
	 * 참고: Image는 이 요소의 크기(width와 height)에 따라 자동으로 확대 / 축소되어 그려집니다. 
	 */
	public Image image;

	// NamedObject
	public DrawableObject(String name) {
		super(name);
	}
	
	public DrawableObject()
	{
	}
	
	public DrawableObject(int width, int height)
	{
		super(width, height);
	}

	public DrawableObject(int width, int height, Image image)
	{
		super(width, height);
		this.image = image;
	}
	
	public DrawableObject(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}
	
	public DrawableObject(int x, int y, int width, int height, Image image)
	{
		super(x, y, width, height);
		this.image = image;
	}
	
	public DrawableObject(DrawableObject other)
	{
		super(other);
		image = other.image;
	}
	
	/**
	 * 현재 설정된 Image를 사용하여 이 요소를 화면에 그립니다.
	 * 
	 * @param g
	 * 		GameFrame에는 g 라는 필드가 들어 있습니다.<br>
	 * 		여러분이 Draw(g)를 직접 호출할 때(게임 화면에 직접 그릴 때)는 더 고민하지 말고 그 필드를 그냥 넣으면 됩니다.
	 */
	@Override
	public void Draw(Graphics2D g)
	{
		g.drawImage(image, x, y, width, height, null);
	}
}
