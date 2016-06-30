/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.UndoableEdit;


public abstract class MultipleEdit extends PresentableEdit
{
	/**
	 * don't change the composition of this in a subclass
	 */
	protected List affectedItems = new ArrayList();
	
	public MultipleEdit(String designatedPresentationName)
	{
		super(designatedPresentationName);
	}
	
	public boolean add(Object designatedItem)
	{
		if (designatedItem!=null && !affectedItems.contains(designatedItem))
			return affectedItems.add(designatedItem);
		else
			return false;
	}
	
	public boolean execute()
	{
		boolean hadEffect = false;
		
		// System.out.println("Executing on: "+this);
		
		if (hasEffect())
		{
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
				hadEffect = execute( affectedItems.get(loop)) || hadEffect;
			
			return hadEffect;
		}
		else
		{
			return false;
		}
	}

	public boolean backout()
	{
		boolean hadEffect = false;
		
		if (hasEffect())
		{
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
				hadEffect = backout( affectedItems.get(loop)) || hadEffect;
		}
		
		return hadEffect;
	}
	
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
	
	public int size()
	{
		return affectedItems.size();
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.MultipleEdit {"
			 + "affectedItems = " + affectedItems + ", "
			 + "presentationName = " + getPresentationName() + ", "
			 + "isolate = " + isIsolate()
		+ "}";
	}
	
	public void redo()
	{
		super.redo();
	}
	
	public boolean reExecute(Object item)
	{
		return execute(item);
	}
	
	/**
	 * @return Did the operation have an effect?
	 */
	public abstract boolean execute(Object item);
	
	/**
	 * @return Did the operation have an effect?
	 */
	public abstract boolean backout(Object item);
	
	public abstract boolean addEdit(UndoableEdit anEdit);
}
