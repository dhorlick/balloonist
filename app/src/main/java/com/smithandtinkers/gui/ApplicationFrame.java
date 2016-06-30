/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.gui;

import javax.swing.JFrame;


/**
 *
 * @author dhorlick
 */
public abstract class ApplicationFrame extends JFrame
{
	private Application application;
	
	public ApplicationFrame(String designatedTitle, Application designatedApplication)
	{
		super(designatedTitle);
		
		if (designatedApplication!=null)
			designatedApplication.register(this);
	}
	
	public Application getApplication()
	{
		return application;
	}
	
	public void setApplication(final Application designatedApplication)
	{
		application = designatedApplication;
	}
	
	
	/**
	 * @return true, if the request to close is successful
	 *         false, if the user is prompted and elects to cancel the close request
	 */
	public abstract boolean close();
}
