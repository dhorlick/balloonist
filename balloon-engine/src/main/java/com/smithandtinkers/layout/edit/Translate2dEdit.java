/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import javax.swing.undo.UndoableEdit;

public class Translate2dEdit extends SelectionEdit
{
	private double deltaX;
	private double deltaY;
	
	public Translate2dEdit(double designatedOldX, double designatedOldY, double designatedNewX, double designatedNewY)
	{
		super(MENU_TEXT.getString("relocateLabel"));
		setDeltaX(designatedNewX-designatedOldX);
		setDeltaY(designatedNewY-designatedOldY);
	}

	public void setDeltaX(double designatedDeltaX)
	{
		deltaX = designatedDeltaX;
	}

	public void setDeltaY(double designatedDeltaY)
	{
		deltaY = designatedDeltaY;
	}
	
	public double getDeltaX()
	{
		return deltaX;
	}

	public double getDeltaY()
	{
		return deltaY;
	}

	public boolean execute(Selectable selectable)
	{
		// System.out.println("Genosha");
		
		if (selectable instanceof Relocateable)
		{
			Relocateable relocateable = (Relocateable) selectable;
			
			relocateable.translate(deltaX, deltaY);
			
			Crowd.reLayoutCrowdsOf(relocateable);
			
			return true;
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		if (selectable instanceof Relocateable)
		{
			Relocateable relocateable = (Relocateable) selectable;
			relocateable.translate(-1.0 * deltaX, -1.0 * deltaY);
			
			Crowd.reLayoutCrowdsOf(relocateable);
			
			return true;
		}
		
		return false;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		if (!isIsolate() && anEdit instanceof Translate2dEdit)
		{
			Translate2dEdit newerTranslateEdit = (Translate2dEdit) anEdit;
			
			deltaX += newerTranslateEdit.getDeltaX();
			deltaY += newerTranslateEdit.getDeltaY();
			
			return true;
		}
		
		return false;
	}

	public boolean hasEffect()
	{
		if (deltaX==0.0 && deltaY==0.0)
			return false;
		
		return true;
	}
}
