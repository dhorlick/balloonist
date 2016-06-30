/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.Logger;

import java.util.List;

/**
 *
 * @author dhorlick
 */
public class BringForwardEdit extends SelectionEdit
{
	public BringForwardEdit()
	{
		super(MENU_TEXT.getString("bringForwardLabel"));
	}

	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		return bringSelectableForward(selectable);
	}
	
	public static boolean bringSelectableForward(Selectable selectable)
	{
		if (selectable instanceof Kid)
		{
			Kid drawableAsKid = (Kid) selectable;
			if (drawableAsKid.getParent()!=null)
			{
				List parent = (List) drawableAsKid.getParent();
				int index = parent.indexOf(drawableAsKid);

				Logger.println("old index = "+index);

				if (index>=1)
				{
					Object aside = parent.get(index-1);
					parent.set(index-1, drawableAsKid);
					parent.set(index, aside);

					Logger.println("sent backward.");
					
					return true;
				}
			}
			else
				Logger.println("Can't re-arrange; parent is null.");
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		return SendBackwardsEdit.sendSelectableBackwards(selectable);
	}

	public boolean hasEffect()
	{
		return true;
	}
}
