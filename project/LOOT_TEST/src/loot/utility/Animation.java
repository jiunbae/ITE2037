package loot.utility;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Animation {
	BufferedImage image;
	Image[] images;
	int row, col, width, height;
	
	public Animation(int row, int col, int width, int height)
	{
		this.row = row;
		this.col = col;
		this.width = width;
		this.height = height;
	}
	
	private boolean splitImage()
	{
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				images[i * col + j] = image.getSubimage(j * width, i*height, width, height);
		return true;
	}
	
	public boolean loadImage(String file)
	{
		try {
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			return false;
		}
		return splitImage();
	}
	
	Image getImage(int index)
	{
		return images[index];
	}
	Image getImage(int row, int col)
	{
		return getImage(row * col);
	}
	
}
