/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;

/**
 *
 * @author dhorlick
 */
public abstract class AbstractLineBreakMeasurer
{
	protected FontRenderContext fontRenderContext;
	protected AttributedCharacterIterator attributedCharacterIterator;
		
	public AbstractLineBreakMeasurer()
	{
	}
	
	public void setAttributedCharacterIterator(AttributedCharacterIterator designatedAttributedCharacterIterator)
	{
		attributedCharacterIterator = designatedAttributedCharacterIterator;
	}
	
	public FontRenderContext getFontRenderContext()
	{
		return fontRenderContext;
	}
	
	public void setFontRenderContext(FontRenderContext designatedFontRenderContext)
	{
		fontRenderContext = designatedFontRenderContext;
	}
	
	public abstract AbstractTextLayout nextLayout(float requestedWrappingHeight);
	
	public abstract AbstractTextLayout nextLayout(float requestedWrappingHeight, int requestedEndIndex);
	
	public int getPosition()
	{
		return attributedCharacterIterator.getIndex();
	}
}
