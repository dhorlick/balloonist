/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.svg.GraphicalContent;


/**
 *
 * @author dhorlick
 */
public class LinkednessEdit extends StatefulMultipleEdit
{
	public LinkednessEdit(boolean designatedNewValue)
	{
		super(MENU_TEXT.getString("linkednessLabel"), new Boolean(designatedNewValue));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing==null || value==null || !(thing instanceof GraphicResizeable) || !(value instanceof Boolean))
			return NO_EFFECT;
		
		GraphicResizeable graphicResizeable = (GraphicResizeable) thing;
		GraphicalContent graphicalContent = graphicResizeable.getGraphicalContent();
		boolean oldValue = graphicalContent.isLinked();
		
		Boolean valueAsBoolean = (Boolean)value;
		graphicalContent.setLinked(valueAsBoolean.booleanValue());
		return new Boolean(oldValue);
	}
}
