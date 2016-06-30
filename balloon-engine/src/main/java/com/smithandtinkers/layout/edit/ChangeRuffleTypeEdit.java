/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.RuffleableSuperEllipse;
import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class ChangeRuffleTypeEdit extends StatefulMultipleEdit
{
	public ChangeRuffleTypeEdit(RuffleableSuperEllipse.Type designatedType)
	{
		super(MENU_TEXT.getString("changeRuffleTypeLabel"), designatedType);
	}
	
	public Object setState(Object thing, Object value)
	{
		if (thing instanceof RuffleableSuperEllipse && value instanceof RuffleableSuperEllipse.Type)
		{
			RuffleableSuperEllipse thingAsRuffled = (RuffleableSuperEllipse) thing;
			RuffleableSuperEllipse.Type valueAsType = (RuffleableSuperEllipse.Type) value;
			
			RuffleableSuperEllipse.Type oldType = thingAsRuffled.getType();
			
			thingAsRuffled.setType(valueAsType);
			
			return oldType;
		}
		
		return NO_EFFECT;
	}
}
