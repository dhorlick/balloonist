/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class FlipWidthEdit extends SelectionEdit
{
	/**
	 * If we merge two of these events, we'll need a way to have them refrain from doing anything.
	 */
	private boolean active = true;
	
	public FlipWidthEdit()
	{
		super(MENU_TEXT.getString("flipLabel"));
	}
	
	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		if (anEdit instanceof FlipWidthEdit)
		{
			FlipWidthEdit otherFlipWidthEdit = (FlipWidthEdit) anEdit;
			
			if (active && otherFlipWidthEdit.hasEffect())
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
			
			if (resizeable.getWidth()==0)
				return false;
			
			if (resizeable instanceof Sill)
			{
				// we need to translate the sill
				
				Sill sill = (Sill) resizeable;
				sill.translate(resizeable.getWidth(), 0.0);
			}
			
			resizeable.setWidth(-resizeable.getWidth());
			
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
