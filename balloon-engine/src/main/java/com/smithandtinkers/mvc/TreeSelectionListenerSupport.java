/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import java.awt.Component;

import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.smithandtinkers.util.NullHatingTypesafeList;

/**
 * @author dhorlick
 */
public class TreeSelectionListenerSupport extends NullHatingTypesafeList
{
	public TreeSelectionListenerSupport()
	{
		super(TreeSelectionListener.class);
	}

	/**
	 * @param tse if the source inside the event is not null, that component will be excluded from
	 * the list of components to be notified about the selection.
	 */
	public void fireValueChanged(TreeSelectionEvent tse)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			TreeSelectionListener tsl = (TreeSelectionListener) get(loop);
			
			if (tse==null || tsl!=tse.getSource())
			{
				// System.out.println("fireValueChanged: tsl: "+tsl);
				// System.out.println("fireValueChanged: tse.getSource(): "+tse.getSource());
				tsl.valueChanged(tse);
			}
		}
	}
	
	public void addTreeSelectionListener(TreeSelectionListener designatedListener)
	{
		add(designatedListener);
	}
	
	public void removeTreeSelectionListener(TreeSelectionListener designatedListener)
	{
		add(designatedListener);
	}
}
