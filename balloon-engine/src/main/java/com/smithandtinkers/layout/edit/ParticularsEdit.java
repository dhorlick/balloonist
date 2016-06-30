/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.TypesafeList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.UndoableEdit;


public abstract class ParticularsEdit extends PresentableEdit
{
	// TODO make Multiple Edit extend this
	
	/**
	 * don't change the composition of this in a subclass
	 */
	protected TypesafeList affectedItems;
	
	public ParticularsEdit(String designatedPresentationName, Class designatedType)
	{
		super(designatedPresentationName);
		affectedItems = new TypesafeList(designatedType);
	}
	
	/**
	 * @throws ClassCastException
	 */
	public boolean add(Object designatedItem)
	{
		if (affectedItems.getConstituentType().isAssignableFrom(designatedItem.getClass()) )
			return affectedItems.add(designatedItem);
		else
		{
			System.out.println("wrong type!");
			return false;
		}
	}
	
	public abstract boolean execute();

	public abstract boolean backout();
	
	public void addItemsFrom(Collection designatedCollection)
	{
		addItemsFrom(designatedCollection.iterator());
	}
	
	public void addItemsFrom(Iterator walk)
	{
		while (walk.hasNext())
		{
			Object item = walk.next();			
			add(item);
		}
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.ParticularsEdit {"
			 + "affectedItems = " + affectedItems + ", "
			 + "presentationName = " + getPresentationName() + ", "
			 + "isolate = " + isIsolate()
		+ "}";
	}
		
	public abstract boolean addEdit(UndoableEdit anEdit);
}
