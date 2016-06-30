/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.List;

import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.Logger;

/**
 *
 * @author dhorlick
 */
public class SendBackwardsEdit extends SelectionEdit
{
	public SendBackwardsEdit()
	{
		super(MENU_TEXT.getString("sendBackwardLabel"));
	}

	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		return sendSelectableBackwards(selectable);
	}
	
	public static boolean sendSelectableBackwards(Selectable selectable)
	{
		if (selectable instanceof Kid)
		{
			Kid selectableAsKid = (Kid) selectable;
			if (selectableAsKid.getParent()!=null)
			{
				List parent = (List) selectableAsKid.getParent();
				int index = parent.indexOf(selectableAsKid);

				Logger.println("old index = "+index);

				if (index<selectableAsKid.getParent().size()-1)
				{
					Object aside = parent.get(index+1);
					parent.set(index+1, selectableAsKid);
					parent.set(index, aside);

					Logger.println("brought forward.");
					
					return true;
				}
			}
			else
				Logger.println("Can't re-arrange; parent is null.");
		}
		else
			Logger.println("Can't re-arrange; selected item is not a kid.");
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		return BringForwardEdit.bringSelectableForward(selectable);
	}

	public boolean hasEffect()
	{
		return true;
	}
}
