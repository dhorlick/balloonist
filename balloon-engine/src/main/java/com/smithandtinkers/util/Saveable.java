/**
 Copyleft Jul 13, 2004 by Dave Horlick
*/

package com.smithandtinkers.util;

import org.w3c.dom.*;

import com.smithandtinkers.io.ArchiveContext;


/**
 * @author dhorlick
 */
public interface Saveable
{
	public void save(Document doc, Node parent, ArchiveContext archiveContext);
	
	/**
	 * @param parent The parent object within the hierarchy, or null if this object is at the top.
	 * @param archiveContext Coordinates the persistence of embedded items.
	 * @throws NumberFormatException if XML elements that are supposed to be numeric aren't
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException;
}
