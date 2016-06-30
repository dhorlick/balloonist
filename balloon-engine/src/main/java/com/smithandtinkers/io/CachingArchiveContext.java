/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.smithandtinkers.svg.GraphicalContent;


public class CachingArchiveContext extends AbstractArchiveContext implements ArchiveContext
{
	private Map graphicalContentIndex = new HashMap();
	
	public CachingArchiveContext()
	{
	}
	
	/**
	 * @see com.smithandtinkers.io.ArchiveContext#write(com.smithandtinkers.svg.GraphicalContent)
	 */
	public String write(GraphicalContent graphicalContent)
	{
		String proposedComponentName = graphicalContent.toString();
		
		for (int index=2; graphicalContentIndex.containsKey(proposedComponentName); index++)
		{
			proposedComponentName = graphicalContent.toString() + index;
		}
		
		graphicalContentIndex.put(proposedComponentName, graphicalContent);
		return proposedComponentName;
	}

	public GraphicalContent read(String requestedName)
	{
		if (graphicalContentIndex.containsKey(requestedName))
		{
			return (GraphicalContent) graphicalContentIndex.get(requestedName);
		}
		else
		{
			return null;
		}
	}
	
	public Set getNames()
	{
		return graphicalContentIndex.keySet();
	}

	public boolean containsName(String requestedName)
	{
		return graphicalContentIndex.containsKey(requestedName);
	}
	
	public void put(String designatedComponentName, GraphicalContent graphicalContent)
	{
		graphicalContentIndex.put(designatedComponentName, graphicalContent);
	}
	
	public String toString()
	{
		return "CachingArchiveContext { graphicalContentIndex = "+graphicalContentIndex
			+", listenerToNotifyAboutUndoableEdits = " + getListenerToNotifyAboutUndoableEdits()
			+" }";
	}
}
