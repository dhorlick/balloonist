/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.graphics;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * Provides distinctive {@link java.awt.TexturePaint}s.
 *
 * @author dhorlick
 */
public class PaintStore
{
	private static PaintStore INSTANCE = new PaintStore();
	
	private TexturePaint OCEAN_WAVE_TEXTURE;
	private TexturePaint TURBULENT_WATER_TEXTURE;
		
	private PaintStore()
	{
		BufferedImage bufferedImage = GraphicsEnvironment.getLocalGraphicsEnvironment(
				).getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(2, 1);
		bufferedImage.setRGB(0, 0, 0xffccccff);
		bufferedImage.setRGB(1, 0, 0xff3333bb);
		OCEAN_WAVE_TEXTURE = new TexturePaint(bufferedImage, new Rectangle(0, 0, 2, 1));
		
		bufferedImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		bufferedImage.setRGB(0, 0, 0x00ddddff);
		bufferedImage.setRGB(1, 0, 0xbb4444bb);
		bufferedImage.setRGB(0, 1, 0xbb4444bb);
		bufferedImage.setRGB(1, 1, 0x00ddddff);
		TURBULENT_WATER_TEXTURE= new TexturePaint(bufferedImage, new Rectangle(0, 0, 2, 2));
	}
	
	public static PaintStore getInstance()
	{
		return INSTANCE;
	}
	
	public TexturePaint getOceanWaveTexture()
	{
		return OCEAN_WAVE_TEXTURE;
	}
	
	public TexturePaint getTurbulentWaterTexture()
	{
		return TURBULENT_WATER_TEXTURE;
	}
}
