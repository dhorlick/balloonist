/**
 * Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.gui;

/**
 *
 * @author dhorlick
 */
public abstract class ApplicationFileAction extends FileAction
{
	public ApplicationFileAction(String designatedTitle, ApplicationFrame designatedFrame, boolean designatedSaveMode)
	{
		super(designatedTitle, designatedFrame, designatedSaveMode);
	}
	
	public ApplicationFileAction(String designatedTitle, ApplicationFrame designatedFrame, boolean designatedSaveMode, String designatedExtension, Application designatedApplication)
	{
		super(designatedTitle, designatedFrame, designatedSaveMode, designatedExtension);
	}
	
	public Application getApplication()
	{
		if (getApplicationFrame()==null)
			return null;
		else		
			return getApplicationFrame().getApplication();
	}
	
	public ApplicationFrame getApplicationFrame()
	{
		if (getFrame() instanceof ApplicationFrame)
		{
			return (ApplicationFrame) getFrame();
		}
		else
		{
			return null;
		}
	}
}
