/*
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import javax.swing.undo.CannotRedoException;

import com.smithandtinkers.geom.AbstractSuperEllipse;
import com.smithandtinkers.geom.SuperEllipse;


/**
 *
 * @author dave
 */
public class HeinParameterEdit extends StatefulSelectionEdit
{
	public HeinParameterEdit(double designatedNewHeinParameter)
	{
		super(MENU_TEXT.getString("changeRectangularnessLabel"), new Double(designatedNewHeinParameter));
	}
	
	public Object setState(Selectable selectable, Object newValue)
	{
		if (selectable instanceof SuperEllipse && newValue!=null && newValue instanceof Number)
		{
			SuperEllipse selectableAsSuperEllipse = (SuperEllipse) selectable;
			double newHeinParameter = ((Number)newValue).doubleValue();
			
			if (AbstractSuperEllipse.wouldProspectiveHeinParameterBeAllowed(newHeinParameter))
			{
				double oldHeinParameter = selectableAsSuperEllipse.getHeinParameter();
				selectableAsSuperEllipse.setHeinParameter(newHeinParameter);
				return new Double(oldHeinParameter);
			}
		}
		
		return NO_EFFECT;
	}
	
	public boolean backout()
	{
		boolean result;
		
		result = super.backout();
		
		if (result)
			Crowd.reLayoutItemsWithin(affectedItems);
		
		return result;
	}
	
	public void redo() throws CannotRedoException
	{
		super.redo();
		
		Crowd.reLayoutItemsWithin(affectedItems);
	}
}
