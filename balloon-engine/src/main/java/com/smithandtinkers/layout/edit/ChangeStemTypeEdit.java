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
public class ChangeStemTypeEdit extends StatefulMultipleEdit
{
	public ChangeStemTypeEdit(AbstractStem.Type designatedType)
	{
		super(MENU_TEXT.getString("changeStemTypeLabel"), designatedType);
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof AbstractStem && value instanceof AbstractStem.Type)
		{
			AbstractStem thingAsStem = (AbstractStem) thing;
			AbstractStem.Type valueAsType = (AbstractStem.Type) value;
			
			AbstractStem.Type oldType = thingAsStem.getType();
			
			thingAsStem.setType(valueAsType);
			
			return oldType;
		}
		
		return NO_EFFECT;
	}
}
