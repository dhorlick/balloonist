/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author dhorlick
 */
public class WidgetedTypesafeList extends TypesafeList implements ComboBoxModel
{
	private Object selectedItem;
	
	private List listeners = new TypesafeList(ListDataListener.class);
	
	/**
	 * @param designatedConstituentType
	 */
	public WidgetedTypesafeList(Class designatedConstituentType)
	{
		super(designatedConstituentType);
	}

	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object anItem)
	{
		if (selectedItem!=null && !selectedItem.equals(anItem) || (selectedItem==null && anItem!=null))
		{
			selectedItem = anItem;
			fireSelection();
		}
	}

	private void fireSelection()
	{
		ListDataEvent listDataEvent = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, size());
		
		for (int loop=listeners.size()-1; loop>=0; loop--)
		{
			ListDataListener listDataListener = (ListDataListener) listeners.get(loop);
			listDataListener.contentsChanged(listDataEvent);
		}
	}

	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem()
	{
		return selectedItem;
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return super.size();
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index)
	{
		return super.get(index);
	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l)
	{
		listeners.add(l);
	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l)
	{
		listeners.remove(l);
	}
}
