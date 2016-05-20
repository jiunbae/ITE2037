package loot;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * 프로그램에서 사용할 Image들을 읽어오고 관리하는 클래스입니다.
 * 
 * @author Racin
 * 
 */
public class ImageResourceManager
{
	HashMap<String, Image> images;

	public ImageResourceManager()
	{
		images = new HashMap<String, Image>();
	}
	
	/**
	 * 주어진 색을 사용하여 임시로 사용할 단색 Image를 만들어 등록합니다.
	 * 
	 * @param color
	 * 			  임시 Image를 만들 색상입니다. 
	 * @param imageName
	 * 			  프로그램 내에서 사용하기 위해 부여하는 이 Image의 이름입니다.
	 * @return 성공적으로 등록한 경우 true를 반환합니다.
	 */
	public boolean CreateTempImage(Color color, String imageName)
	{
		if ( images.containsKey(imageName) == true )
			return false;
		
		BufferedImage newImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		newImage.setRGB(0, 0, color.getRGB());
		
		images.put(imageName, newImage);
		
		return true;
	}

	/**
	 * 해당 파일에서 Image를 읽어와 주어진 이름으로 등록합니다.
	 * 
	 * @param fileName
	 *            Image를 읽어올 파일 이름입니다.
	 * @param imageName
	 *            프로그램 내에서 사용하기 위해 부여하는 이 Image의 이름입니다.
	 * @return 성공적으로 등록한 경우 true를 반환합니다.
	 */
	public boolean LoadImage(String fileName, String imageName)
	{
		try
		{
			//이미 해당 이름의 Image가 등록되어 있는 경우 실패
			if ( images.containsKey(imageName) == true )
				return false;

			File f = new File(fileName);

			//해당 이름의 파일이 없는 경우 실패
			if ( f.exists() == false )
				return false;

			Image newImage = ImageIO.read(f);

			images.put(imageName, newImage);
		}
		catch (Exception e)
		{
			//Image를 읽어오지 못 한 경우 실패
			return false;
		}

		return true;
	}

	/**
	 * 해당 이름으로 등록된 Image를 가져옵니다.
	 * 
	 * @param imageName
	 *            등록할 때 부여한 Image 이름입니다.
	 * @return 해당 이름의 Image가 있는 경우 그 Image를 반환합니다. 그렇지 않은 경우 null을 반환합니다.
	 */
	public Image GetImage(String imageName)
	{
		return images.get(imageName);
	}

	/**
	 * 해당 이름으로 등록된 Image를 제거합니다.
	 * 
	 * @param imageName
	 *            등록할 때 부여한 Image 이름입니다.
	 */
	public void DisposeImage(String imageName)
	{
		images.remove(imageName);
	}
}
