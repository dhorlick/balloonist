/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.NullHatingTypesafeList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author dhorlick
 */
public class TableEventSupport extends NullHatingTypesafeList
{
	public TableEventSupport()
	{
		super(TableModelListener.class);
	}
	
	public void addTableModelListener(TableModelListener l)
	{
		add(l);
	}
	
	public boolean removeTableModelListener(TableModelListener l)
	{
		return remove(l);
	}
	
	public void fireEvent(TableModelEvent designatedEvent)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			TableModelListener tableModelListener = (TableModelListener) get(loop);
			tableModelListener.tableChanged(designatedEvent);
		}
	}
}
