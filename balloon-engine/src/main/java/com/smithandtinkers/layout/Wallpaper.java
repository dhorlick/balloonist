/*
 Copyleft 2006 by Dave Horlick
*/

package com.smithandtinkers.layout;

import com.smithandtinkers.graphics.DrawingContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;


/**
 * Repeats a message diagonally over a region.
 *
 * This is useful for making a watermark.
 */
public class Wallpaper
{
	private String text;
	private Color color;
	private String comment;
	private float fontSize = 20f;
	
	public Wallpaper()
	{
	}
	
	public void setText(String requestedText)
	{
		if (text != requestedText)
		{
			text = requestedText;
		}
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setColor(Color designatedColor)
	{
		color = designatedColor;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setComment(String designatedComment)
	{
		comment = designatedComment;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public void setFontSize(float designatedFontSize)
	{
		if (fontSize != designatedFontSize)
		{
			fontSize = designatedFontSize;
		}
	}
	
	public float getFontSize()
	{
		return fontSize;
	}
	
	public int draw(DrawingContext drawingContext, Dimension requestedDimension)
	{
		if (getText()==null)
			return 0;
		
		if (!drawingContext.getClass().getName().endsWith("AwtDrawingContext"))
			return 0; // TODO handle this somewhere else
		
		if (color!=null)
			drawingContext.setColor(color);
		
		Shape shaped = phrase(drawingContext);
		
		// final int DELTA_X = (int)shaped.getBounds().getWidth();
		final int DELTA_X = (int) (getFontSize()*5f);
		Rectangle2D theBounds = shaped.getBounds();
		final int DELTA_Y = (int)(1.15f * theBounds.getHeight());
		
		boolean odd = false;
		
		for (int y = 0; y<requestedDimension.getHeight(); y+=DELTA_Y)
		{
			for (int x = 0; x<requestedDimension.getWidth(); x+=DELTA_X)
			{
				drawingContext.fill(shaped, x, y);
			}
		}
		
		if (getComment()!=null)
			drawingContext.comment(getComment());
			
		return 0;
	}
	
	private Shape phrase(DrawingContext drawingContext)
	{
		Map props = new HashMap();
		props.put(TextAttribute.SIZE, new Float(fontSize));
		props.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD); // TODO make weight a parameter
		final TextLayout textLayout = new TextLayout(getText(), props, drawingContext.getFontRenderContext());
		Shape shaped = textLayout.getOutline(AffineTransform.getRotateInstance(Math.PI/4.0));
		return shaped;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Wallpaper {"
			 + "text = " + text + ", "
			 + "color = " + color + ", "
			 + "comment = " + comment + ", "
			 + "fontSize = " + fontSize
		+ "}";
	}
	
	public void draw(DrawingContext drawingContext, Rectangle2D bounds)
	{
		if (drawingContext==null || bounds==null)
			return;
		
		drawingContext.translate(bounds.getX(), bounds.getY());
		Dimension size = new Dimension((int)bounds.getWidth(), (int)bounds.getHeight());

		draw(drawingContext, size);
		drawingContext.translate(-bounds.getX(), -bounds.getY());
	}
}