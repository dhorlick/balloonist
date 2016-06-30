/*
 Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class FocusInclinationEdit extends StatefulMultipleEdit
{
	public FocusInclinationEdit(double newInclinationInRadians)
	{
		super(MENU_TEXT.getString("changeFocusInclinationLabel"), new Double(newInclinationInRadians));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof AbstractStem && value instanceof Number)
		{
			AbstractStem thingAsStem = (AbstractStem) thing;
			Number valueAsNumber = (Number) value;
			
			double oldFocusInclination = thingAsStem.getFocusStemInclinationInRadians();
			
			/* System.out.println("new focus inclination="
					+(valueAsNumber.doubleValue()*360.0/(2.0*3.14159268))
					+"�"); */
			
			thingAsStem.setFocusStemInclinationInRadians(valueAsNumber.doubleValue());
			
			return new Double(oldFocusInclination);
		}
		
		return NO_EFFECT;
	}
}
