/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Kid;

import java.util.List;

/**
 *
 * @author dhorlick
 */
public class SendToBackEdit extends SelectionEdit
{
	public SendToBackEdit()
	{
		super(MENU_TEXT.getString("sendToBackLabel"));
	}
	
	public static boolean sendSelectableToBack(Selectable selectable)
	{
		if (selectable instanceof Kid)
		{
			Kid selectableAsKid = (Kid) selectable;
			
			// System.out.println("selectableAsKid="+selectableAsKid);
			
			if (selectableAsKid.getParent()!=null)
			{
				List parent = (List) selectableAsKid.getParent();
				
				if (parent.size()==1)
					return false;
				
				int oldIndex = parent.indexOf(selectableAsKid);
				int newIndex = parent.size()-1;
				
				parent.remove(selectableAsKid);
				parent.add(newIndex, selectableAsKid);

				// System.out.println("Sent to back. old index was: "+oldIndex);
				
				return true;
			}
		}
		
		return true;
	}

	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		return sendSelectableToBack(selectable);
	}

	public boolean backout(Selectable selectable)
	{
		return BringToFrontEdit.bringSelectableToFront(selectable);
	}

	public boolean hasEffect()
	{
		return true;
	}
}
