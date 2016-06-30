/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.geom.Parallelogram;
import com.smithandtinkers.layout.*;


/**
 * Changes the inclination of a parallelogram.
 * 
 * @author dhorlick
 */
public class InclinationEdit extends StatefulSelectionEdit
{
	public InclinationEdit(double designatedNewInclination)
	{
		super(MENU_TEXT.getString("changeInclinationLabel"), new Double(designatedNewInclination));
	}

	public Object setState(Selectable selectable, Object designatedNewValue)
	{
		if (selectable instanceof Perch)
		{
			Perch perch = (Perch)selectable;
			
			if (perch.getUnperched() instanceof Parallelogram)
				selectable = (Selectable) perch.getUnperched();
		}
		
		if (selectable instanceof Parallelogram && designatedNewValue!=null && designatedNewValue instanceof Number)
		{
			Parallelogram selectableAsParallelogram = (Parallelogram) selectable;
			double newInclination = ((Number)designatedNewValue).doubleValue();
			
			if (Parallelogram.wouldProspectiveInclinationBeAllowed(newInclination))
			{
				// System.out.println("InclinationEdit: accepted. setting...");
				
				double oldInclination = selectableAsParallelogram.getInclination();
				selectableAsParallelogram.setInclination(newInclination);
								
				return new Double(oldInclination);
			}
		}
		
		return NO_EFFECT;
	}

	public boolean reExecute(Selectable item)
	{
		boolean retValue;
		
		retValue = super.reExecute(item);
		Crowd.reLayoutCrowdsOf(item);
		
		return retValue;
	}

	public boolean backout(Selectable selectable)
	{
		boolean retValue;
		
		retValue = super.backout(selectable);
		
		Crowd.reLayoutCrowdsOf(selectable);
		
		return retValue;
	}

}
