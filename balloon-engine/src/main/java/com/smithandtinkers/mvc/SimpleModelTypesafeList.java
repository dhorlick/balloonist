/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.TypesafeList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An easily-integrated but not particularly informative messaging model class.
 *
 * @author dhorlick
 */
public class SimpleModelTypesafeList extends TypesafeList
{
	private final SingleThreadedChangeSupport changeSupport = new SingleThreadedChangeSupport();
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public SimpleModelTypesafeList(Class designatedType)
	{
		super(designatedType);
	}
	
	public Object set(int index, Object element)
	{
		if (get(index)!=element)
		{
			Object retValue;
			
			retValue = super.set(index, element);
			
			fireEvent(CHANGE_EVENT);
			
			return retValue;
		}
		else
			return null;
	}
	
	public void add(int index, Object element)
	{
		super.add(index, element);
		
		fireEvent(CHANGE_EVENT);
	}
	
	public Object remove(int index)
	{
		Object retValue;
		
		retValue = super.remove(index);
		fireEvent(CHANGE_EVENT);
		return retValue;
	}

	private void fireEvent(ChangeEvent designatedEvent)
	{
		for (int loop=0; loop<=changeSupport.size()-1; loop++)
		{
			ChangeListener listener = (ChangeListener) changeSupport.get(loop);
			listener.stateChanged(CHANGE_EVENT);
		}
	}

	public void removeChangeListener(ChangeListener l)
	{
		changeSupport.remove(l);
	}

	public void addChangeListener(ChangeListener l)
	{
		if (!contains(l))
			changeSupport.add(l);
	}

	public void removeAllChangeListeners()
	{
		changeSupport.clear();
	}
}
