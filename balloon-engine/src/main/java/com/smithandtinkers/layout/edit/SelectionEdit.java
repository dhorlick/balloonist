/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.smithandtinkers.util.TypesafeList;

import java.util.Collection;
import java.util.Iterator;


public abstract class SelectionEdit extends PresentableEdit
{
	/**
	 * don't change the composition of this in a subclass
	 */
	protected TypesafeList affectedItems = new TypesafeList(Selectable.class);
	
	public SelectionEdit(String designatedPresentationName)
	{
		super(designatedPresentationName);
	}
	
	public boolean add(Selectable designatedSelectable)
	{
		return affectedItems.add(designatedSelectable);
	}
	
	public void addSelectablesFrom(Selection selection)
	{
		for (int loop=0; loop<=selection.getSelectedItemsCount()-1; loop++)
		{
			if (selection.getSelectedItem(loop) instanceof Selectable)
			{
				add(selection.getSelectedItem(loop));
			}
		}
	}

	public boolean execute()
	{
		boolean hadEffect = false;
		
		// System.out.println("Executing on: "+this);
		
		if (hasEffect())
		{
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
				hadEffect = execute( (Selectable) affectedItems.get(loop)) || hadEffect;
			
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
		
		// System.out.println("backing out. affectedItems.size()="+affectedItems.size());
		
		if (hasEffect())
		{
			// we're going to do this backwards to avoid messing up un-delete operations
			
			for (int loop=affectedItems.size()-1; loop>=0; loop--)
			{
				// System.out.print(loop+") ");
				hadEffect = backout( (Selectable) affectedItems.get(loop)) || hadEffect;
			}
		}
		
		return hadEffect;
	}
	
	public void addSelectablesFrom(Collection designatedCollection)
	{
		Iterator walk = designatedCollection.iterator();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			if (item instanceof Selectable)
			{
				add((Selectable)item);
			}
		}
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.SelectionEdit {"
			 + "affectedItems = " + affectedItems + ", "
			 + "presentationName = " + getPresentationName() + ", "
			 + "isolate = " + isIsolate()
		+ "}";
	}
	
	public void reExecute()
	{
		if (hasEffect())
		{
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
			{
				if (affectedItems.get(loop) instanceof Selectable)
				{
					reExecute( (Selectable)affectedItems.get(loop));	
				}
			}
		}
	}
	
	public boolean reExecute(Selectable item)
	{
		return execute(item);
	}
	
	/**
	 * @return Did the operation have an effect?
	 */
	public abstract boolean execute(Selectable selectable);
	
	/**
	 * @return Did the operation have an effect?
	 */
	public abstract boolean backout(Selectable selectable);
	
	public abstract boolean addEdit(UndoableEdit anEdit);
}
