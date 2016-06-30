/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;

import com.smithandtinkers.util.NullHatingTypesafeList;

/**
 * @author dhorlick
 */
public class CaretListenerSupport extends NullHatingTypesafeList
{
	public CaretListenerSupport()
	{
		super(CaretListener.class);
	}
	
	/**
	 * @param ce if the source inside the event is not null, that component will be excluded from
	 * the list of components to be notified about the caret event.
	 */
	public void fireValueChanged(CaretEvent ce)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			CaretListener cl = (CaretListener) get(loop);
			
			if (ce==null || cl!=ce.getSource())
			{
				// System.out.println("fireValueChanged: tsl: "+tsl);
				// System.out.println("fireValueChanged: tse.getSource(): "+tse.getSource());
				cl.caretUpdate(ce);
			}
		}
	}
	
	public void addCaretListener(CaretListener designatedListener)
	{
		add(designatedListener);
	}
	
	public void removeCaretListener(CaretListener designatedListener)
	{
		add(designatedListener);
	}	
}
