/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import javax.swing.undo.UndoableEdit;

import com.smithandtinkers.geom.Shapeable;

public class ReshapeEdit extends PointToPointEdit
{
	public ReshapeEdit(int designatedOldX, int designatedOldY, int designatedNewX, int designatedNewY)
	{
		super(MENU_TEXT.getString("reshapeLabel"));
		
		setOldX(designatedOldX);
		setOldY(designatedOldY);
		setNewX(designatedNewX);
		setNewY(designatedNewY);
	}
	
	public boolean execute(Selectable selectable)
	{
		if (selectable instanceof Shapeable)
		{
			Shapeable shapeable = (Shapeable) selectable;
			shapeable.reshape(oldX, oldY, newX, newY);
			
			return true;
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		// TODO this isn't really reversable
		
		if (selectable instanceof Shapeable)
		{
			Shapeable shapeable = (Shapeable) selectable;
			shapeable.reshape(newX, newY, oldX, oldY);
			
			Crowd.reLayoutCrowdsOf(shapeable);
			
			return true;
		}
		
		return false;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		if (anEdit instanceof ReshapeEdit)
		{
			ReshapeEdit newerReshapeEdit = (ReshapeEdit) anEdit;
			newerReshapeEdit.setNewX(newerReshapeEdit.getNewX());
			newerReshapeEdit.setNewY(newerReshapeEdit.getNewY());
			
			return true;
		}
			
		return false;
	}
}
