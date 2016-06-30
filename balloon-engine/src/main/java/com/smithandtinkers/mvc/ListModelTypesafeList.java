/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.KidTypesafeList;
import com.smithandtinkers.util.NullHatingTypesafeList;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/**
 * @author dhorlick
 */
public class ListModelTypesafeList extends KidTypesafeList implements ListModel
{
	private final NullHatingTypesafeList listEventSupport = new NullHatingTypesafeList(ListDataListener.class);
	
	public ListModelTypesafeList(Class designatedType)
	{
		super(designatedType);
	}
	
	public Object set(int index, Object element)
	{
		if (get(index)!=element)
		{
			Object retValue;
			
			retValue = super.set(index, element);
			
			fireEvent(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index));
			
			return retValue;
		}
		else
			return null;
	}
	
	public void add(int index, Object element)
	{
		super.add(index, element);
		
		fireEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
	}
	
	public Object remove(int index)
	{
		Object retValue;
		
		retValue = super.remove(index);
		fireEvent(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
		return retValue;
	}

	private void fireEvent(ListDataEvent designatedEvent)
	{
		for (int loop=0; loop<=listEventSupport.size()-1; loop++)
		{
			ListDataListener listener = (ListDataListener) listEventSupport.get(loop);
			
			switch (designatedEvent.getType())
			{
				case ListDataEvent.INTERVAL_ADDED:
					listener.intervalAdded(designatedEvent);
					break;
				case ListDataEvent.INTERVAL_REMOVED:
					listener.intervalRemoved(designatedEvent);
					break;
				case ListDataEvent.CONTENTS_CHANGED:
					listener.contentsChanged(designatedEvent);
					break;
			}
		}
	}

	public Object getElementAt(int index)
	{
		return get(index);
	}

	public void removeListDataListener(ListDataListener l)
	{
		listEventSupport.remove(l);
	}

	public void addListDataListener(ListDataListener l)
	{
		if (!listEventSupport.contains(l))
			listEventSupport.add(l);
	}

	public int getSize()
	{
		return size();
	}
}
