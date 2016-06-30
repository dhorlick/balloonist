/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import com.smithandtinkers.util.PlatformFriend;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author dhorlick
 */
public class OrientableTextLayout
{
	private TextLayout horizontalTextLayout;
	private AbstractTextLayout verticalTextLayout;
	
	public OrientableTextLayout()
	{
	}

	public TextLayout getHorizontalTextLayout()
	{
		return horizontalTextLayout;
	}

	/**
	 * @throws IllegalArgumentException if you try to set horizontal AND vertical text layouts.
	 */
	public void setHorizontalTextLayout(TextLayout designatedHorizontalTextLayout) throws IllegalArgumentException
	{
		if (designatedHorizontalTextLayout!=null && verticalTextLayout!=null)
			throw new IllegalArgumentException(OrientableLineBreakMeasurer.NOT_BOTH_ERROR_MESSAGE);
		
		horizontalTextLayout = designatedHorizontalTextLayout;
	}

	public AbstractTextLayout getVerticalTextLayout()
	{
		return verticalTextLayout;
	}

	/**
	 * @throws IllegalArgumentException if you try to set horizontal AND vertical text layouts.
	 */
	public void setVerticalTextLayout(AbstractTextLayout designatedVerticalTextLayout) throws IllegalArgumentException
	{
		if (designatedVerticalTextLayout!=null && horizontalTextLayout!=null)
			throw new IllegalArgumentException(OrientableLineBreakMeasurer.NOT_BOTH_ERROR_MESSAGE);
		
		verticalTextLayout = designatedVerticalTextLayout;
	}
	
	public float getAscent()
	{
		if (horizontalTextLayout!=null)
			return horizontalTextLayout.getAscent();
		else
			return verticalTextLayout.getAscent();
	}
	
	public float getDescent()
	{
		if (horizontalTextLayout!=null)
			return horizontalTextLayout.getDescent();
		else
			return verticalTextLayout.getDescent();
	}
	
	public float getLeading()
	{
		if (horizontalTextLayout!=null)
			return horizontalTextLayout.getLeading();
		else
			return verticalTextLayout.getLeading();
	}
	
	public Rectangle2D getBounds()
	{
		if (horizontalTextLayout!=null)
		{
			if (PlatformFriend.TEXTLAYOUT_BOUNDS_UNRELIABLE)
			{
				return horizontalTextLayout.getOutline(null).getBounds();
				// see Apple problem ID 3451587
			}
			else
			{
				return horizontalTextLayout.getBounds();
			}
		}
		else
		{
			return verticalTextLayout.getBounds();
		}
	}
	
	public int getCharacterCount()
	{
		if (horizontalTextLayout!=null)
			return horizontalTextLayout.getCharacterCount();
		else
			return verticalTextLayout.getCharacterCount();
	}
	
	public boolean isVertical()
	{
		return (horizontalTextLayout==null);
	}
	
	public void draw(Graphics2D graphics, float x, float y)
	{
		if (horizontalTextLayout!=null)
		{
			if (graphics!=null)
				horizontalTextLayout.draw(graphics, x, y);
		}
		else
		{
			verticalTextLayout.draw(graphics, x, y);
		}
	}
}
