/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.layout.*;


/**
 *
 * @author dave
 */
public class LeadingEdgeEdit extends StatefulSelectionEdit
{	
	public LeadingEdgeEdit(double designatedNewLeadingEdgeFraction)
	{
		super(MENU_TEXT.getString("changeLeadingEdgeAngleLabel"), new Double(designatedNewLeadingEdgeFraction));
	}

	public Object setState(Selectable selectable, Object value)
	{
		if (selectable instanceof Stem && value!=null && value instanceof Double)
		{
			Stem stem = (Stem) selectable;
			Double valueAsDouble = (Double) value;
			double oldValue = stem.getLeadingEdgePositionAsPerimeterFraction();
			stem.setLeadingEdgePositionAsSideFraction(valueAsDouble.doubleValue());
			return new Double(oldValue);
		}
		
		return NO_EFFECT;
	}
}
