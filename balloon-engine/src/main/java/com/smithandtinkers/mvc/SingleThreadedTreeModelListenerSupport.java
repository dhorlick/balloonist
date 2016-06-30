/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import com.smithandtinkers.util.NullHatingTypesafeList;

/**
 * @author dhorlick
 */
public class SingleThreadedTreeModelListenerSupport extends NullHatingTypesafeList
{
	public SingleThreadedTreeModelListenerSupport()
	{
		super(TreeModelListener.class);
	}
	
	public void fireNodeChange(TreeModelEvent event)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			TreeModelListener listener = (TreeModelListener) get(loop);
			listener.treeNodesChanged(event);
		}
	}

	public void fireNodeInsert(TreeModelEvent event)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			TreeModelListener listener = (TreeModelListener) get(loop);
			listener.treeNodesInserted(event);
		}
	}
	
	public void fireNodeRemove(TreeModelEvent event)
	{
		for (int loop=size()-1; loop>=0; loop--)
		{
			TreeModelListener listener = (TreeModelListener) get(loop);
			listener.treeNodesRemoved(event);
		}
	}
	
	public void addTreeModelListener(TreeModelListener designatedListener)
	{
		add(designatedListener);
	}
	
	public void removeTreeModelListener(TreeModelListener designatedListener)
	{
		remove(designatedListener);
	}
}
