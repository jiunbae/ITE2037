package loot.graphics;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 * Image를 사용하여 2차원 평면 내(게임 화면 자체, 또는 Layer 안) 또는 3차원 공간 내(Viewport 안)에 그릴 수 있는 요소 하나를 나타냅니다.<br>
 * 참고: 만약 이 클래스의 인스턴스를 2차원 평면 내에서 사용하려는 경우<br>
 * 이 클래스 대신 DrawableObject class를 사용하는 것이 더 좋을 것입니다.
 * 
 * @author Racin
 *
 */
public class DrawableObject3D extends VisualObject3D
{
	/**
	 * 이 요소를 그릴 때 사용할 Image입니다.<br>
	 * GameFrame이 가지고 있는 images.GetImage()를 호출하면 이 필드를 좀 더 간편하게 다룰 수 있습니다.<br>
	 * 참고: Image는 이 요소의 크기(width와 height)에 따라 자동으로 확대 / 축소되어 그려집니다. 
	 */
	public Image image;

	// NamedObject
	public DrawableObject3D(String name) {
		super(name);
	}
	
	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D()
	{
		super(true);
	}
	
	/**
	 * 이 생성자는 argument를 통해 새 요소를 2차원 평면에 그릴지 3차원 공간에 그릴지 결정합니다.
	 */
	public DrawableObject3D(boolean trigger_coordination)
	{
		super(trigger_coordination);
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(Image image)
	{
		super(true);
		this.image = image;
	}

	/**
	 * 이 생성자는 새 요소를 2차원 평면에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(int width, int height)
	{
		super(width, height);
	}

	/**
	 * 이 생성자는 새 요소를 2차원 평면에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(int width, int height, Image image)
	{
		super(width, height);
		this.image = image;
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(double radius_x, double radius_y)
	{
		super(radius_x, radius_y);
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(double radius_x, double radius_y, Image image)
	{
		super(radius_x, radius_y);
		this.image = image;
	}
	
	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
	}

	/**
	 * 이 생성자는 새 요소를 3차원 공간에 그릴 요소로 간주합니다.
	 */
	public DrawableObject3D(double pos_x, double pos_y, double pos_z, double radius_x, double radius_y, Image image)
	{
		super(pos_x, pos_y, pos_z, radius_x, radius_y);
		this.image = image;
	}
	
	/**
	 * 이 생성자는 원본 요소 설정에 따라 새 요소를 2차원 평면에 그릴지 3차원 공간에 그릴지 결정합니다.
	 */
	public DrawableObject3D(DrawableObject3D other)
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
