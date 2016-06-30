/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.Set;

/**
 * An AttributedCharacterIterator wrapper that can render its contents 
 * uncooperative at request.
 *
 * This is useful for getting Graphics2D.drawString(AttributedCharacterIterator, float, float)
 * to correctly handle vertically-oriented text.
 * 
 * @author dhorlick
 */
public class IrritableAttributedCharacterIterator implements AttributedCharacterIterator
{
	private AttributedCharacterIterator attributedCharacterIterator;
	private boolean irritated;
	
	public IrritableAttributedCharacterIterator(AttributedCharacterIterator designatedAttributedCharacterIterator)
	{
		setAttributedCharacterIterator(designatedAttributedCharacterIterator);
	}

	public AttributedCharacterIterator getAttributedCharacterIterator()
	{
		return attributedCharacterIterator;
	}

	public void setAttributedCharacterIterator(AttributedCharacterIterator designatedAttributedCharacterIterator)
	{
		attributedCharacterIterator = designatedAttributedCharacterIterator;
	}

	public char setIndex(int position)
	{
		if (!isIrritated())
			return attributedCharacterIterator.setIndex(position);
		else
			return current();
	}

	public int getRunStart(Set attributes)
	{
		return attributedCharacterIterator.getRunStart();
	}

	public int getRunLimit(Set attributes)
	{
		return attributedCharacterIterator.getRunLimit(attributes);
	}

	public Object getAttribute(AttributedCharacterIterator.Attribute attribute)
	{
		return attributedCharacterIterator.getAttribute(attribute);
	}

	public int getRunLimit(AttributedCharacterIterator.Attribute attribute)
	{
		return attributedCharacterIterator.getRunLimit(attribute);
	}

	public int getRunStart(AttributedCharacterIterator.Attribute attribute)
	{
		return attributedCharacterIterator.getRunStart(attribute);
	}

	public char previous()
	{
		if (!isIrritated())
		{
			return attributedCharacterIterator.previous();
		}
		else
		{
			return current();
		}
	}

	public char next()
	{
		if (!isIrritated())
		{
			return attributedCharacterIterator.next();
		}
		else
		{
			return current();
		}
	}

	public char last()
	{
		if (!isIrritated())
			return attributedCharacterIterator.last();
		else
			return current();
	}

	public char current()
	{
		return attributedCharacterIterator.current();
	}

	public char first()
	{
		if (!isIrritated())
			return attributedCharacterIterator.first();
		else
			return current();
	}

	public Set getAllAttributeKeys()
	{
		return attributedCharacterIterator.getAllAttributeKeys();
	}

	public Map getAttributes()
	{
		return attributedCharacterIterator.getAttributes();
	}

	public int getBeginIndex()
	{
		if (!irritated)		
			return attributedCharacterIterator.getBeginIndex();
		else
			return getIndex();
	}

	public int getEndIndex()
	{
		if (!irritated)		
			return attributedCharacterIterator.getEndIndex();
		else
			return getIndex()+1;
	}

	public int getIndex()
	{
		return attributedCharacterIterator.getIndex();
	}

	public int getRunLimit()
	{
		if (!irritated)
			return attributedCharacterIterator.getRunLimit();
		else
			return getIndex()+1;
	}

	public int getRunStart()
	{
		if (!irritated)
			return attributedCharacterIterator.getRunLimit();
		else
			return getIndex();
	}
	
	public Object clone()
	{
		return new IrritableAttributedCharacterIterator(attributedCharacterIterator);
	}

	public boolean isIrritated()
	{
		return irritated;
	}

	public void setIrritated(boolean designatedIrritation)
	{
		irritated = designatedIrritation;
	}
}
