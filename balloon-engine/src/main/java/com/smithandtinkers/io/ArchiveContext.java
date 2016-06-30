/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.io;

import com.smithandtinkers.svg.GraphicalContent;

import java.util.List;
import javax.swing.event.UndoableEditListener;


public interface ArchiveContext
{
	/**
	 * Registers graphic content in an artwork tree so that it can be assigned an appropriate
	 * component name and written into an archive.
	 * 
	 * @return the archive component name used for this resource. will usually be the final  
	 * portion of the URL after the last / separator, but before any anchors or query. 
	 * 
	 * If there is a name conflict with another, distinct image, a numerical suffix will be
	 * appended to this.
	 * 
	 * If the source is absent entirely, a generic name will be generated.
	 */
	public String write(GraphicalContent graphicalContent);
	
	/**
	 * Reloads persisted graphics that have already received an explicit component name.
	 * 
	 * @param designatedComponentName
	 * @param graphicalContent
	 */
	public void put(String designatedComponentName, GraphicalContent graphicalContent);
	
	/**
	 * @param requestedName The final portion of the URL after the last / separator, but before any anchors or query.
	 * @return The corresponding graphical content, if any.
	 */
	public GraphicalContent read(String requestedName);

	/**
	 * @param requestedName The final portion of the URL after the last / separator, but before any anchors or query.
	 */
	public boolean containsName(String requestedName);
	
	public UndoableEditListener getListenerToNotifyAboutUndoableEdits();
	
	public List getMissingFontFamilies();
}
