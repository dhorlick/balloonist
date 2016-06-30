/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.io.FileOpener;
import com.smithandtinkers.util.TypesafeList;

/**
 * @author dhorlick
 */
public interface Application extends FileOpener
{
	public void register(ApplicationFrame designatedFrame);
	public void unregister(ApplicationFrame designatedFrame);
	public void quit();
	public boolean stagger(ApplicationFrame designatedFrame);
	public TypesafeList getApplicationFrames();
	
	/**
	 * Updates the views of all application frames.
	 * 
	 * This is useful for reflecting changes to the application state like preferences
	 * that are not part of the application model and are consequently missed by the
	 * event/listener framework.
	 */
	public void jogApplicationFrames();
}
