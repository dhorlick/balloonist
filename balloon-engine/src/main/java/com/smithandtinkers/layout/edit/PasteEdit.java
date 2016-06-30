/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.*;


/**
 *
 * @author dhorlick
 */
public class PasteEdit extends SelectionEdit
{
	private TypesafeList parent;
	
	private boolean displaceLookalikes = true;
	private boolean behind;
	
	public PasteEdit(TypesafeList designatedTypesafeList)
	{
		super(MENU_TEXT.getString("pasteLabel"));
		setParent(designatedTypesafeList);
	}

	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean execute(Selectable selectable)
	{
		if (hasEffect())
		{
			if (selectable instanceof PubliclyCloneable)
			{
				PubliclyCloneable publiclyCloneable = (PubliclyCloneable) selectable;
				try
				{
					Selectable clone = (Selectable) publiclyCloneable.clone();
					
					if (displaceLookalikes && clone instanceof Resizeable && sharesBoundaryPreference(parent, (Resizeable)clone))
					{
						Resizeable cloneAsResizeable = (Resizeable)clone;
						cloneAsResizeable.translate(32, 32);
					}
					
					if (behind)
						parent.add(clone);
					else
						parent.add(0, clone);
					
					// this will break encapsulation, but is necessary to allow for backout:
					
					int selectableIndex = affectedItems.indexOf(selectable);
					affectedItems.set(selectableIndex, clone);
					
					return true;
				}
				catch (CloneNotSupportedException exception)
				{
					throw new BugException(exception);
				}
			}
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		if (hasEffect())
		{
			return parent.remove(selectable);
		}
		
		return false;
	}

	public boolean hasEffect()
	{
		return (parent!=null);
	}
	
	public TypesafeList getParent()
	{
		return parent;
	}

	public void setParent(TypesafeList designatedParent)
	{
		parent = designatedParent;
	}

	public boolean isDisplaceLookalikes()
	{
		return displaceLookalikes;
	}

	public void setDisplaceLookalikes(boolean displaceLookalikes)
	{
		this.displaceLookalikes = displaceLookalikes;
	}
	
	public static boolean sharesBoundaryPreference(TypesafeList parent, Resizeable resizeable)
	{
		if (resizeable==null)
			return false;
		
		for (int loop=0; loop<=parent.size()-1; loop++)
		{
			if (parent.get(loop) instanceof Resizeable)
			{
				Resizeable siblingResizeable = (Resizeable) parent.get(loop);
				
				if (siblingResizeable!=null && AbstractResizeable.samePreferredBounds(siblingResizeable, resizeable))
					return true;
			}
		}
		
		return false;
	}

	public boolean isBehind()
	{
		return behind;
	}

	public void setBehind(boolean behind)
	{
		this.behind = behind;
	}
}
