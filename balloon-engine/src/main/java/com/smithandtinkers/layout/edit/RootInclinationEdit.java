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
public class RootInclinationEdit extends StatefulMultipleEdit
{	
	public RootInclinationEdit(double newInclinationInRadians)
	{
		super(MENU_TEXT.getString("changeRootInclinationLabel"), new Double(newInclinationInRadians));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof AbstractStem && value instanceof Number)
		{
			AbstractStem thingAsStem = (AbstractStem) thing;
			Number valueAsNumber = (Number) value;
			
			double oldRootInclination = thingAsStem.getRootStemInclinationInRadians();
			
			/* System.out.println("new root inclination="
					+(valueAsNumber.doubleValue()*360.0/(2.0*3.14159268))
					+"�"); */
			
			thingAsStem.setRootStemInclinationInRadians(valueAsNumber.doubleValue());
			
			return new Double(oldRootInclination);
		}
		
		return NO_EFFECT;
	}
}
