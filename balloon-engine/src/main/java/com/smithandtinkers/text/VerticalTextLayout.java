/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import com.smithandtinkers.BalloonEngineState;
import java.awt.Graphics2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

/**
 *
 * @author dhorlick
 */
public class VerticalTextLayout extends AbstractTextLayout
{
	public VerticalTextLayout()
	{
	}
	
	public void reset()
	{
		super.reset();
	}
	
	/**
	 * @param graphics2D a Graphics2D object to draw onto, or null to just go thru the motions
	 */
	public void draw(Graphics2D graphics2D, float x, float y)
	{
		attributedCharacterIterator.setIndex(beginIndex);
		
		int loop=0;
		
		for (char theChar = attributedCharacterIterator.current();
				attributedCharacterIterator.getIndex() < endIndex && theChar != AttributedCharacterIterator.DONE;
				theChar = attributedCharacterIterator.next())
		{
			Map attributesShuttle = attributedCharacterIterator.getAttributes(); // TODO slow and inefficient
			attributesShuttle = BalloonEngineState.getInstance().getStyleStrategy().replaceUnsavoryAttributes(graphics2D, attributesShuttle);
			
			AttributedString aString = new AttributedString(String.valueOf(theChar), attributesShuttle);
			
			if (graphics2D!=null)
				graphics2D.drawString(aString.getIterator(), x, y+(loop*getAscent()));
			
			// irritableAttributedCharacterIterator.setIrritated(false);
			// irritableAttributedCharacterIterator.next();
			loop++;
		}
	}	
}
