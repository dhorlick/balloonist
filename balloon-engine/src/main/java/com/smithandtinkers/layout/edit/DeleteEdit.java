/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.List;

/**
 *
 * @author dhorlick
 */
public class DeleteEdit extends StatefulSelectionEdit
{
	public DeleteEdit()
	{
		super(MENU_TEXT.getString("clearLabel"), null);
	}

	public Object setState(Selectable selectable, Object value)
	{
		if (value==null) // deletion
		{
			Crowd.reLayoutCrowdsOf(selectable);
			
			List parent = (List) selectable.getParent();
			
			if (parent==null)
				return NO_EFFECT;
			
			int position = parent.indexOf(selectable);
			parent.remove(selectable);
			selectable.setParent(null);
			
			if (position==-1)
				position = 0; // I'm feeling charitable to the outside world today.
			
			return new Survivor(parent, position);
		}
		else // un-deletion
		{
			Survivor survivor = (Survivor) value;
			
			int slot = survivor.getPosition();
			
			// System.out.println("desired slot="+slot);
			// System.out.println("parent size="+survivor.getParent().size());
			
			if (slot>survivor.getParent().size())
				slot = survivor.getParent().size();
			
			// System.out.println("actual slot="+slot);
			
			survivor.getParent().add(slot, selectable);
			
			Crowd.reLayoutCrowdsOf(selectable);
			
			return null;
		}
	}
	
	public static class Survivor
	{
		private List parent;
		private int position;
		
		public Survivor()
		{
		}
		
		public Survivor(List designatedParent, int designatedPosition)
		{
			setParent(designatedParent);
			setPosition(designatedPosition);
		}

		public List getParent()
		{
			return parent;
		}

		public void setParent(List parent)
		{
			this.parent = parent;
		}

		public int getPosition()
		{
			return position;
		}

		public void setPosition(int position)
		{
			this.position = position;
		}
	}
}
