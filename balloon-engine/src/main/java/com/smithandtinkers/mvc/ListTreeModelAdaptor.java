/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author dhorlick
 */
public abstract class ListTreeModelAdaptor implements TreeModel
{
	private SingleThreadedTreeModelListenerSupport treeModelSupport = new SingleThreadedTreeModelListenerSupport();
	
	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index)
	{
		// System.out.println("getChild: starting w. index="+index);
		
		List list = (List) parent;
		// System.out.println("getChild: returning "+list.get(index));
		return list.get(index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent)
	{
		// System.out.println("getChildCount: starting");
		
		if (parent instanceof List)
		{
			List list = (List) parent;
			// System.out.println("getChildCount: returning amount "+list.size());
			return list.size();
		}
		else
		{
			// System.out.println("getChildCount: returning zero");
			return 0;
		}
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node)
	{
		boolean leaf = !(node instanceof List);
		// System.out.print("isLeaf: node=");
		// System.out.print(node);
		// System.out.println(" starting and returning "+leaf);
		
		return leaf;
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		treeModelSupport.fireNodeChange(new TreeModelEvent(this, path)); // TODO only do this if necessary
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		// System.out.println("getIndexOfChild: starting");
		
		List list = (List) parent;
		
		int index = list.indexOf(child);
		// System.out.println("getIndexOfChild: returning "+ index);
		return index;
	}

	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		treeModelSupport.addTreeModelListener(l);
	}

	/**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		treeModelSupport.removeTreeModelListener(l);
	}
	
	public SingleThreadedTreeModelListenerSupport getTreeModelSupport()
	{
		return treeModelSupport;
	}
}
