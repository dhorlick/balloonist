/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.graphics;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Reverse-engineers {@link java.awt.FontMetrics} by actually writing on a fake Graphics2D object
 * and looking at the resulting area footprint.
 *
 * @author dhorlick
 */
public class EmpiricalFontMetrics
{
	private final static String ALL_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz"; 
	private final static String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	private final static String ALL_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private final static String DESCENDY_LOWERCASE = "gjpqy";
	private final static String NONDESCENDY_LOWERCASE = "abcdefhiklmnorstuvwxz";
	
	private int ascent;
	private int descent;
	private int leading;
	
	public EmpiricalFontMetrics(final Font requestedFont)
	{
		ascent = findHeight(requestedFont, UPPERCASE);
		descent = findHeight(requestedFont, ALL_LOWERCASE) - findHeight(requestedFont, NONDESCENDY_LOWERCASE);
		FontMetrics traditionalFontMetrics = findTraditionalFontMetrics(requestedFont);
		leading = traditionalFontMetrics.getLeading();
	}
	
	public static FontMetrics findTraditionalFontMetrics(final Font requestedFont)
    {
		if (requestedFont==null || requestedFont.getSize()==0)
			return null;
		
		final BufferedImage imageToGetFontMetricsWith = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		final Graphics2D graphicsToGetFontMetricsFrom =imageToGetFontMetricsWith.createGraphics();
		graphicsToGetFontMetricsFrom.setFont(requestedFont);
		
		return graphicsToGetFontMetricsFrom.getFontMetrics();
    }

	private int findHeight(final Font requestedFont, final String text)
	{
		// final int WHITE = -16777216;
		final int BLACK = -1;
		
		if (requestedFont==null || requestedFont.getSize()==0 || text==null || text.length()==0)
			return 0;
		
		final BufferedImage image = new BufferedImage(
				findTraditionalFontMetrics(requestedFont).getMaxAdvance() * text.length(),
				2*requestedFont.getSize(), BufferedImage.TYPE_BYTE_BINARY);
		
		Graphics2D graphicsToDrawOn = image.createGraphics();
		graphicsToDrawOn.setFont(requestedFont);
		final FontMetrics traditionalMetrics = graphicsToDrawOn.getFontMetrics();
		
		graphicsToDrawOn.drawString(text, 0, traditionalMetrics.getHeight());
		
		graphicsToDrawOn.dispose();
		
		int top=image.getHeight();
		int bottom=0;
		
		for (int y=0; y<image.getHeight(); y++)
		{
			for (int x=0; x<image.getWidth(); x++)
			{
				int rgb=image.getRGB(x, y);
				
				if (rgb==BLACK)
				{
					if (y<top)
						top=y;
					if (y>bottom)
						bottom=y;
					
					// we can skip to the next line now
					
					x = image.getWidth();
				}
			}
		}
		
		return bottom-top;
	}
	
	public int getAscent()
    {
	    return ascent;
    }
	
	public int getDescent()
	{
		return descent;
	}
	
	public int getLeading()
	{
		return leading;
	}
	
	public double getLeadingAsFractionOfHeight()
	{
		return (double)getLeading()/(double)getHeight();
	}
	
	public int getHeight()
	{
		return getAscent() + getDescent() + getLeading();
	}
	
	public String toString()
	{
		return "EmpiricalFontMetrics { "
				+ "ascent = " + getAscent()
				+ ", descent = " + getDescent()
				+ ", leading = " + getLeading()
			+ "}";
	}
	
	public static void main(String [] args)
	{
		final Font theFont = new Font("Serif", Font.PLAIN, 32);
		final EmpiricalFontMetrics efm = new EmpiricalFontMetrics(theFont);
		System.out.println("empiricalFontMetrics="+efm);
		FontMetrics traditionalFontMetrics = findTraditionalFontMetrics(theFont);
		System.out.println("traditionalFontMetrics="+traditionalFontMetrics);
		System.out.println("traditionalFontMetrics.getLeading()="+traditionalFontMetrics.getLeading());
	}
}
