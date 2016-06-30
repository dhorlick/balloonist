/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import java.util.EventObject;

import javax.swing.tree.TreePath;

/**
 * An event that characterizes the current selection, without respect to the previous selection.
 * 
 * Contrast with {@link javax.swing.tree.TreeSelectionModel}
 * 
 * @author dhorlick
 */
public class SelfContainedTreeSelectionEvent extends EventObject
{
	/** 
	 * Paths that are currently selected.  Will be null if nothing is currently selected. 
     */
	protected TreePath[] selection;

	public SelfContainedTreeSelectionEvent(Object designatedSource, TreePath[] designatedSelection)
	{
		super(designatedSource);
		setSelection(designatedSelection);
	}

	private void setSelection(TreePath[] designatedSelection)
	{
		selection = designatedSelection;
	}

	public TreePath[] getSelection()
	{
		return selection;
	}
	
	public String toString()
	{
		StringBuffer desc = new StringBuffer();
		
		desc.append("SelfContainedTreeSelectionEvent { selections = ");
		
		for (int loop=0; loop<=selection.length-1; loop++)
		{
			desc.append(selection[loop]);
			if (loop<selection.length-1)
				desc.append(", ");
		}
		
		desc.append(", source = [ ");
		desc.append(source);
		
		desc.append(" ] }");
		
		return desc.toString();
	}
}
