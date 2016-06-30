/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 * @author dhorlick
 */
public class AspectRatioEdit extends StatefulMultipleEdit
{
	public AspectRatioEdit(Layout designatedLayout, double designatedNewAspectRatio)
	{
		super(MENU_TEXT.getString("changeAspectRatioLabel"), new Double(designatedNewAspectRatio));
		add(designatedLayout);
	}
	
	public Object setState(Object thing, Object value)
	{
		if (thing instanceof Layout && value instanceof Number)
		{
			Layout layout = (Layout) thing;
			Number number = (Number) value;
			
			double newValue = number.doubleValue();
			
			if (newValue>0.0 && newValue<100.0) // TODO enforce this cap in controls
			{
				double oldAspectRatio = layout.getAspectRatio();
				layout.setAspectRatio(newValue);
				
				return new Double(oldAspectRatio);
			}
		}
		
		return NO_EFFECT;
	}
}
