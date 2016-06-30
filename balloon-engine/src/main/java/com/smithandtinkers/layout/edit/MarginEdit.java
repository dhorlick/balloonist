/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.layout.*;

import java.util.Iterator;
import javax.swing.undo.CannotRedoException;


/**
 *
 * @author dhorlick
 */
public class MarginEdit extends StatefulSelectionEdit
{
	public MarginEdit(double designatedNewMargin)
	{
		super(MENU_TEXT.getString("marginChangeLabel"), new Double(designatedNewMargin));
	}

	public Object setState(Selectable selectable, Object value)
	{
		if (selectable instanceof Marginal && value !=null && value instanceof Double)
		{
			Marginal marginal = (Marginal) selectable;
			Double immutableMarginValue = (Double) value;
			
			double oldMarginValue = marginal.getMarginInPoints();
			marginal.setMarginInPoints(immutableMarginValue.doubleValue());
			return new Double(oldMarginValue);
		}
		
		return NO_EFFECT;
	}

	public boolean backout()
	{
		boolean result;
		
		result = super.backout();
		
		if (result)
			requestReLayout();
		
		return result;
	}
	
	private void requestReLayout()
	{
		Iterator walk = affectedItems.iterator();
			
		while (walk.hasNext())
			Crowd.reLayoutCrowdsOf(walk.next());
	}

	public void redo() throws CannotRedoException
	{
		super.redo();
		
		requestReLayout();
	}
}
