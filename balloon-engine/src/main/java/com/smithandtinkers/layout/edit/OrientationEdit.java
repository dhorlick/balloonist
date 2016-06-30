/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class OrientationEdit extends StatefulMultipleEdit
{
	public OrientationEdit(boolean designatedVerticality)
	{
		super(MENU_TEXT.getString("orientLabel"), Boolean.valueOf(designatedVerticality));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing==null || value==null || !(thing instanceof Orientable) || !(value instanceof Boolean))
			return NO_EFFECT;
		
		Orientable orientable = (Orientable) thing;
		boolean oldValue = orientable.isVertical();
		
		Boolean valueAsBoolean = (Boolean)value;
		orientable.setVertical(valueAsBoolean.booleanValue());
		return Boolean.valueOf(oldValue);
	}
}
