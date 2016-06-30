/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.List;
import java.util.ResourceBundle;
import com.smithandtinkers.util.Kid;


/**
 *
 * @author dhorlick
 */
public class BringToFrontEdit extends SelectionEdit
{
	public static ResourceBundle MENU_TEXT = ResourceBundle.getBundle("resources/text/menu");
	
	public BringToFrontEdit()
	{
		super(MENU_TEXT.getString("bringToFrontLabel"));
	}

	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		return bringSelectableToFront(selectable);
	}
	
	public static boolean bringSelectableToFront(Selectable selectable)
	{
		if (selectable instanceof Kid)
		{
			Kid drawableAsKid = (Kid) selectable;
			if (drawableAsKid.getParent()!=null)
			{
				List parent = (List) drawableAsKid.getParent();
				
				if (parent.size()==1)
					return false;
				
				parent.remove(drawableAsKid);
				parent.add(0, drawableAsKid);

				// System.out.println("Brought to front.");
				// System.out.println("selected="+selected.toString());
				
				return true;
			}
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		return SendToBackEdit.sendSelectableToBack(selectable);
	}

	public boolean hasEffect()
	{
		return true;
	}
}
