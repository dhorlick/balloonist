/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.layout.*;


/**
 *
 * @author dhorlick
 */
public class BubblePeriodEdit extends StatefulMultipleEdit
{	
	public BubblePeriodEdit(double newBubblePeriod)
	{
		super(MENU_TEXT.getString("changeBubbleDensityLabel"), new Double(newBubblePeriod));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof AbstractStem && value instanceof Number)
		{
			AbstractStem thingAsStem = (AbstractStem) thing;
			Number valueAsNumber = (Number) value;
			
			double oldBubblePeriod = thingAsStem.getBubblePeriod();
			
			thingAsStem.setBubblePeriod(valueAsNumber.doubleValue());
			
			return new Double(oldBubblePeriod);
		}
		
		return NO_EFFECT;
	}
}
