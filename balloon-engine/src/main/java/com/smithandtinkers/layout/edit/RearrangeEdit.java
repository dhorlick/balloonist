/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.List;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author dhorlick
 */
public class RearrangeEdit extends MultipleEdit
{
	private List oldList;
	private List newList;
	
	private int oldIndex = -1;
	private int newIndex = -1;
	
	public RearrangeEdit()
	{
		super(MENU_TEXT.getString("rearrangeLabel"));
	}
	
	public RearrangeEdit(List designatedOldContainer, List designatedNewContainer)
	{
		this();
		setOldList(designatedOldContainer);
		setNewList(designatedNewContainer);
	}

	public boolean execute(Object item)
	{
		// System.out.println("rearrange edit: executing");
		
		if (getOldList()!=null)
		{
			oldIndex = getOldList().indexOf(item);
			// System.out.println("removing item from old list...");
			getOldList().remove(item);
		}
		else
			System.out.println("Couldn't remove! "+this);
		
		if (getNewList()!=null)
		{
			// System.out.println("new list size before execute="+getNewList().size());
			
			if (newIndex==-1)
				getNewList().add(item);
			else
				getNewList().add(newIndex, item);
				
			Crowd.reLayoutCrowdsOf(item);
			
			// System.out.println("new list size After execute="+getNewList().size());
		}
		
		return true;
	}

	public boolean backout(Object item)
	{
		// System.out.println("backing out...");
		
		if (getNewList()!=null)
		{
			// System.out.println("\tbackout: removing. size before="+getNewList().size());
			boolean result = getNewList().remove(item);
			// System.out.println("\tbackout: remove result: "+result);
			// System.out.println("\tafterwards: getNewList().indexOf(item)="+getNewList().indexOf(item));
			// System.out.println("\tsize after="+getNewList().size());
		}
		else
		{
			System.out.println("couldn't remove! "+this);
		}
		
		if (getOldList()!=null)
		{
			if (oldIndex == -1)
				getOldList().add(item);
			else
				getOldList().add(oldIndex, item);
				
			Crowd.reLayoutCrowdsOf(item);
		}
		
		return true;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		return false;
	}

	public boolean hasEffect()
	{
		return (getOldList()!=null && getOldList()!=getNewList());
	}

	public List getOldList()
	{
		return oldList;
	}

	public void setOldList(List designatedOldList)
	{
		oldList = designatedOldList;
	}

	public List getNewList()
	{
		return newList;
	}

	public void setNewList(List designatedNewList)
	{
		newList = designatedNewList;
	}

	public int getNewIndex()
	{
		return newIndex;
	}

	/**
	 * @param designatedNewIndex -1, or the new index
	 * @throws IllegalArgumentException
	 */
	public void setOldIndex(int designatedNewIndex)
	{
		if (designatedNewIndex<-1)
			throw new IllegalArgumentException(String.valueOf(designatedNewIndex));
		
		newIndex = designatedNewIndex;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.RearrangeEdit {"
			 + "oldList = " + oldList + ", "
			 + "newList = " + newList + ", "
			 + "oldIndex = " + oldIndex + ", "
			 + "newIndex = " + newIndex
		+ "}";
	}
}
