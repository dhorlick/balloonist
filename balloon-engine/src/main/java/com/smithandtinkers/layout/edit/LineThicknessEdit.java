/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class LineThicknessEdit extends StatefulMultipleEdit
{
	public LineThicknessEdit(double designatedLineThickness)
	{
		super(MENU_TEXT.getString("changeLineThicknessLabel"), new Double(designatedLineThickness));
	}

	public Object setState(Object thing, Object value)
	{
		if (thing==null || value==null || !(thing instanceof Thick) || !(value instanceof Number))
			return NO_EFFECT;
		
		Thick thick = (Thick) thing;
		final double oldValue = thick.getLineThickness();
		
		Number valueAsNumber = (Number) value;
		thick.setLineThickness(valueAsNumber.doubleValue());
		return new Double(oldValue);
	}
}
