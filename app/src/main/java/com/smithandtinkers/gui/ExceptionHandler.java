/**
 * Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.gui;

import javax.swing.SwingUtilities;


/**
 * Intended to receive & gracefully handle throwables permeating the
 * Event Dispatch Queue.
 */
public class ExceptionHandler
{
	public final static String NAME_OF_THIS_CLASS = "com.smithandtinkers.gui.ExceptionHandler";
	
	private static int instancesWaiting;
	
	public static void install()
	{
		if (!SwingUtilities.isEventDispatchThread())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				// we must resort to these shenanigans because we're here
				// they're necessary on OS X
				
				public void run()
				{
					System.setProperty("sun.awt.exception.handler",
							NAME_OF_THIS_CLASS);
				}
			});
		}
	}
}