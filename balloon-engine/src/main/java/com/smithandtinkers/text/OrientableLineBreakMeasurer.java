/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;

/**
 * A wrapper that lets you break text across horizontal or vertical lines.
 *
 * Text is broken vertically in writing systems used by Japan and China.
 *
 * @author dhorlick
 */
public class OrientableLineBreakMeasurer
{
	private LineBreakMeasurer horizontalLineBreakMeasurer;
	private VerticalLineBreakMeasurer verticalLineBreakMeasurer;
	
	static final String NOT_BOTH_ERROR_MESSAGE = "Can't be horizontal AND vertical.";
	
	public OrientableLineBreakMeasurer()
	{
	}
	
	public OrientableLineBreakMeasurer(VerticalLineBreakMeasurer designatedVerticalLineBreakMeasurer)
	{
		setVerticalLineBreakMeasurer(designatedVerticalLineBreakMeasurer);
	}
	
	public OrientableLineBreakMeasurer(LineBreakMeasurer designatedHorizontalLineBreakMeasurer)
	{
		setHorizontalLineBreakMeasurer(designatedHorizontalLineBreakMeasurer);
	}

	public VerticalLineBreakMeasurer getVerticalLineBreakMeasurer()
	{
		return verticalLineBreakMeasurer;
	}

	public void setVerticalLineBreakMeasurer(VerticalLineBreakMeasurer designatedVerticalLineBreakMeasurer) throws IllegalArgumentException
	{
		if (designatedVerticalLineBreakMeasurer!=null && horizontalLineBreakMeasurer!=null)
			throw new IllegalArgumentException(NOT_BOTH_ERROR_MESSAGE);
		
		verticalLineBreakMeasurer = designatedVerticalLineBreakMeasurer;
	}

	public LineBreakMeasurer getHorizontalLineBreakMeasurer()
	{
		return horizontalLineBreakMeasurer;
	}

	public void setHorizontalLineBreakMeasurer(LineBreakMeasurer designatedHorizontalLineBreakMeasurer) throws IllegalArgumentException
	{
		if (designatedHorizontalLineBreakMeasurer!=null && verticalLineBreakMeasurer!=null)
			throw new IllegalArgumentException(NOT_BOTH_ERROR_MESSAGE);
			
		horizontalLineBreakMeasurer = designatedHorizontalLineBreakMeasurer;
	}
	
	public OrientableTextLayout nextLayout(float requstedWrappingFootprint)
	{
		OrientableTextLayout orientableTextLayout = null;
		
		if (horizontalLineBreakMeasurer!=null)
		{
			TextLayout textLayout = horizontalLineBreakMeasurer.nextLayout(requstedWrappingFootprint);
			
			if (textLayout!=null)
			{
				orientableTextLayout = new OrientableTextLayout();
				orientableTextLayout.setHorizontalTextLayout(textLayout);
			}
		}
		else
		{
			AbstractTextLayout verticalTextLayout = verticalLineBreakMeasurer.nextLayout(requstedWrappingFootprint);
			if (verticalTextLayout!=null)
			{
				orientableTextLayout = new OrientableTextLayout();
				orientableTextLayout.setVerticalTextLayout(verticalTextLayout);
			}
		}
		
		return orientableTextLayout;
	}
	
	public OrientableTextLayout nextLayout(float requstedWrappingFootprint, int requestedEndIndex) // TODO recycle old OrientableTextLayout's
	{
		OrientableTextLayout orientableTextLayout = null;
		
		if (horizontalLineBreakMeasurer!=null)
		{
			TextLayout textLayout = horizontalLineBreakMeasurer.nextLayout(requstedWrappingFootprint, requestedEndIndex, true);
			
			if (textLayout!=null)
			{
				orientableTextLayout = new OrientableTextLayout();
				orientableTextLayout.setHorizontalTextLayout(textLayout);
			}
		}
		else
		{
			AbstractTextLayout verticalTextLayout = verticalLineBreakMeasurer.nextLayout(requstedWrappingFootprint, requestedEndIndex);
			if (verticalTextLayout!=null)
			{
				orientableTextLayout = new OrientableTextLayout();
				orientableTextLayout.setVerticalTextLayout(verticalTextLayout);
			}
		}
		
		return orientableTextLayout;
	}
	
	public int getPosition()
	{
		if (horizontalLineBreakMeasurer!=null)
			return horizontalLineBreakMeasurer.getPosition();
		else
			return verticalLineBreakMeasurer.getPosition();
	}
	
	public String toString()
	{
		return "com.smithandtinkers.text.OrientableLineBreakMeasurer {"
			 + "horizontalLineBreakMeasurer = " + horizontalLineBreakMeasurer + ", "
			 + "verticalLineBreakMeasurer = " + verticalLineBreakMeasurer
		+ "}";
	}
}
