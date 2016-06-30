/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.NullHatingTypesafeList;

public class SelfContainedTreeSelectionListenerSupport extends NullHatingTypesafeList
{
	public SelfContainedTreeSelectionListenerSupport()
	{
		super(SelfContainedTreeSelectionListener.class);
	}

	/**
	 * @param tse if the source inside the event is not null, that component will be excluded from
	 * the list of components to be notified about the selection.
	 */
	public void fireValueChanged(SelfContainedTreeSelectionEvent tse)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			SelfContainedTreeSelectionListener tsl = (SelfContainedTreeSelectionListener) get(loop);
			
			if (tse==null || tsl!=tse.getSource())
			{
				// System.out.println("fireValueChanged: tsl: "+tsl);
				// System.out.println("fireValueChanged: tse.getSource(): "+tse.getSource());
				tsl.valueChanged(tse);
			}
		}
	}
	
	public void addSelfContainedTreeSelectionListener(SelfContainedTreeSelectionListener designatedListener)
	{
		add(designatedListener);
	}
	
	public void removeSelfContainedTreeSelectionListener(SelfContainedTreeSelectionListener designatedListener)
	{
		add(designatedListener);
	}
}
