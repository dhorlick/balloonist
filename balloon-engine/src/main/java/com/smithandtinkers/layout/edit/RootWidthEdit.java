/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.layout.*;


/**
 *
 * @author dave
 */
public class RootWidthEdit extends StatefulSelectionEdit
{
	public RootWidthEdit(double designatedNewRootWidthInPoints)
	{
		super(MENU_TEXT.getString("changeRootWidthLabel"), new Double(designatedNewRootWidthInPoints));
	}

	public Object setState(Selectable selectable, Object value)
	{
		// System.out.println("setState invoked. selectable: " + selectable + "value: "+value);
		
		if (selectable instanceof Stem && value!=null && value instanceof Number)
		{
			Stem stem = (Stem) selectable;
			Number newValueAsNumber = (Number) value;
			double newValueAsDouble = newValueAsNumber.doubleValue();
			
			// System.out.println("Entry condition #1 met. new value as double: " + newValueAsDouble);
			
			if (AbstractStem.wouldProspectiveRootWidthInPointsBeAllowed(newValueAsDouble))
			{
				// System.out.println("Entry condition #2 met. proceeding with switchout...");
				
				double oldValue = stem.getRootWidthInPoints();
				stem.setRootWidthInPoints(newValueAsDouble);
				return new Double(oldValue);
			}
		}
		
		return NO_EFFECT;
	}
}
