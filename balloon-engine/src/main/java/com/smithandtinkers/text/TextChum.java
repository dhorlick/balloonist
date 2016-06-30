/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.text.CharacterIterator;

/**
 * Provides helpful text functions.
 *
 * @author dhorlick
 */
public abstract class TextChum
{
	public static StringBuffer characterIteratorToStringBuffer(CharacterIterator iter)
	{
		final StringBuffer buffer = new StringBuffer();
		
		for (char c=iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			buffer.append(c);
		}
		
		return buffer;
	}
}
