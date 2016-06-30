/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class FlipHeightEdit extends SelectionEdit
{
	/**
	 * If we merge two of these events, we'll need a way to have them refrain from doing anything.
	 */
	private boolean active = true;
	
	public FlipHeightEdit()
	{
		super(MENU_TEXT.getString("flipLabel"));
	}
	
	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		if (anEdit instanceof FlipHeightEdit)
		{
			FlipHeightEdit otherFlipHeightEdit = (FlipHeightEdit) anEdit;
			
			if (active && otherFlipHeightEdit.hasEffect())
			{
				active = false;
			}
			else
			{
				active = true;
			}
			
			return true;
		}
		
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		if (selectable instanceof Resizeable)
		{
			Resizeable resizeable = (Resizeable) selectable;
			
			if (resizeable.getHeight()==0)
				return false;
			
			if (resizeable instanceof Sill)
			{
				// we need to translate the sill
				
				Sill sill = (Sill) resizeable;
				sill.translate(0.0, resizeable.getHeight());
			}
			
			resizeable.setHeight(-resizeable.getHeight());
			return true;
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		return execute(selectable); // same, both ways
	}

	public boolean hasEffect()
	{
		return active;
	}
}
