/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import javax.swing.undo.UndoableEdit;

public class ResizeEdit extends PointToPointEdit
{
	private PerimeterSegment perimeterSegment;
	
	public ResizeEdit(PerimeterSegment designatedPerimeterSegment, int designatedOldX, int designatedOldY, int designatedNewX, int designatedNewY)
	{
		super(MENU_TEXT.getString("resizeLabel"));
		setPerimeterSegment(designatedPerimeterSegment);
		setOldX(designatedOldX);
		setOldY(designatedOldY);
		setNewX(designatedNewX);
		setNewY(designatedNewY);
	}

	public PerimeterSegment getPerimeterSegment()
	{
		return perimeterSegment;
	}

	public void setPerimeterSegment(PerimeterSegment designatedPerimeterSegment)
	{
		perimeterSegment = designatedPerimeterSegment;
	}

	public boolean execute(Selectable selectable)
	{
		if (selectable instanceof Resizeable)
		{
			Resizeable resizeable = (Resizeable) selectable;
			resizeable.resize(perimeterSegment, oldX, oldY, newX, newY);
			
			return true;
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		if (selectable instanceof Resizeable)
		{
			Resizeable resizeable = (Resizeable) selectable;
			resizeable.resize(perimeterSegment, newX, newY, oldX, oldY);
			
			Crowd.reLayoutCrowdsOf(resizeable);
			
			return true;
		}
		
		return false;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		if (anEdit instanceof ResizeEdit)
		{
			ResizeEdit newerResizeEdit = (ResizeEdit) anEdit;
			setNewX(newerResizeEdit.getNewX());
			setNewY(newerResizeEdit.getNewY());
			
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean reExecute(Selectable item)
	{
		boolean result;
		
		result = super.reExecute(item);
		
		if (item instanceof Resizeable)
			Crowd.reLayoutCrowdsOf(item);
		
		return result;
	}
}
