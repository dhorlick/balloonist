/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;

/**
 *
 * @author dhorlick
 */
public abstract class AbstractTextLayout
{	
	protected AttributedCharacterIterator attributedCharacterIterator;
	
	protected int characterCount;
	
	protected int beginIndex = -1;
	protected int endIndex = -1;
	
	private float ascent;
	private float descent;
	private float leading;
	
	protected Rectangle2D bounds;
	
	public int getCharacterCount()
	{
		return characterCount;
	}
	
	public void setCharacterCount(int designatedCharacterCount)
	{
		if (characterCount != designatedCharacterCount)
		{
			if (designatedCharacterCount<0)
				throw new IllegalArgumentException("cannot be less than zero: "+designatedCharacterCount);
			
			characterCount = designatedCharacterCount;
		}
	}
	
	public void reset()
	{
		characterCount = 0;
		ascent = 0.0f;
		descent = 0.0f;
		leading = 0.0f;
		beginIndex = -1;
		endIndex = -1;
	}
	
	public abstract void draw(Graphics2D graphics2D, float x, float y);

	public int getBeginIndex()
	{
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex)
	{
		this.beginIndex = beginIndex;
	}
	
	/**
	 * @return an end index, or -1 if one has not been set (sorry)
	 */
	public int getEndIndex()
	{
		return endIndex;
	}

	public void setEndIndex(int endIndex)
	{
		this.endIndex = endIndex;
	}
	
	public Rectangle2D getBounds()
	{
		return bounds;
	}
	
	public void setBounds(Rectangle2D designatedBounds)
	{
		bounds = designatedBounds;
	}
	
	public float getAscent()
	{
		return ascent;
	}
	
	public void setAscent(float designatedAscent)
	{
		ascent = designatedAscent;
	}
	
	public float getDescent()
	{
		return descent;
	}
	
	public void setDescent(float designatedDescent)
	{
		descent = designatedDescent;
	}

	public float getLeading()
	{
		return leading;
	}
	
	public void setLeading(float designatedLeading)
	{
		leading = designatedLeading;
	}
	
	public AttributedCharacterIterator getAttributedCharacterIterator()
	{
		return attributedCharacterIterator;
	}
	
	public void setAttributedCharacterIterator(AttributedCharacterIterator attributedCharacterIterator)
	{
		this.attributedCharacterIterator = attributedCharacterIterator;
	}
}
