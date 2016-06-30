/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.PlatformFriend;
import com.smithandtinkers.util.TypesafeList;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author dhorlick
 */
public abstract class AbstractApplication implements Application
{
	private TypesafeList applicationFrames = new TypesafeList(ApplicationFrame.class);
	protected TypesafeList unopenedFiles;	
	
	public AbstractApplication()
	{
		init();
	}
	
	protected void init()
	{
		unopenedFiles = new TypesafeList(File.class);
		
		if (PlatformFriend.RUNNING_ON_MAC)
		{
			initializeMacOSX();
		}
	}
	
	public void quit()
	{
		final ApplicationFrame active = findActiveApplicationFrame();
		
		boolean quitCancelled = false;
			
		if (active!=null)
		{
			quitCancelled = !active.close();
		
			if (quitCancelled)
				return;
		}
		
		final List remaining = new ArrayList(getApplicationFrames());
			// we have to copy this because we're going to be removing elements from the original one as we iterate.
		
		Collections.reverse(remaining);
			// we want to close in reverse creation-order
		
		final Iterator walk = remaining.iterator();
			
		while (!quitCancelled && walk.hasNext())
		{
			ApplicationFrame applicationFrame = (ApplicationFrame) walk.next();
			quitCancelled = !applicationFrame.close();
		}

		if (!quitCancelled)
		{
			// TODO some day it would be nice if to get AWT to quit implicitly

			System.exit(0);
		}
	}
	
	public TypesafeList getApplicationFrames()
	{
		return applicationFrames;
	}
	

	/**
	 * @return the active artwork frame, or null if one couldn't be found
	 */
	public ApplicationFrame findActiveApplicationFrame()
	{
		Iterator walk = applicationFrames.iterator();

		while (walk.hasNext())
		{
			ApplicationFrame af = (ApplicationFrame) walk.next();

			if (af.hasFocus() || (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER && af.isFocused())) // this probably won't wash with Java 1.3 even with the logical caveat
				return af;
		}

		return null;
	}
	
	/**
	 * Looks at all the open artwork frames. If there is a manageable number and one of
	 * them is at the same location of the designated one, the designated one will be
	 * displaced slightly.
	 *
	 * @return true, if the designatedFrame is either in a unique position, or was moved to one
	 *         false, if the designatedFrame was not in a unique position and there were
	 *                remaining slots in which to move it.
	 */
	public boolean stagger(ApplicationFrame designatedFrame)
	{
		Iterator walk = new ArrayList(getApplicationFrames()).iterator();

		while (walk.hasNext())
		{
			ApplicationFrame aframe = (ApplicationFrame) walk.next();
			if (aframe.getLocation().equals(designatedFrame.getLocation()))
			{
				// System.out.println("*** location conflict ***");

				Rectangle prospectiveBounds = designatedFrame.getBounds();
				prospectiveBounds.translate(10, 10);

				Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
				Rectangle screenRectangle = new Rectangle(0, 0, (int)screenDimension.getWidth(), (int)screenDimension.getHeight());

				// System.out.println("screenRectangle: "+screenRectangle);
				// System.out.println("prospectiveBounds: "+prospectiveBounds);

				if (screenRectangle.contains(prospectiveBounds))
				{
					// System.out.println("displacing...");
					Point place = designatedFrame.getLocation();
					place.translate(10, 10);
					designatedFrame.setLocation(place);
				}
				else
				{
					prospectiveBounds.translate(-10, 0);

					if (screenRectangle.contains(prospectiveBounds))
					{
						Point place = designatedFrame.getLocation();
						place.translate(0, 10);
						designatedFrame.setLocation(place);
					}
					else
					{
						// hopeless

						return false;
					}
				}
			}
		}

		return true;
	}
	
	public void jogApplicationFrames()
	{
		Iterator walk = applicationFrames.iterator();

		while (walk.hasNext())
		{
			ApplicationFrame af = (ApplicationFrame) walk.next();

			af.invalidate();
			af.validate();
			af.repaint();
		}
	}
	
	protected void initializeMacOSX()
	{
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", getApplicationName());
		
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		    java.lang.System.setProperty("apple.laf.useScreenMenuBar", "true");
		else
			java.lang.System.setProperty("com.apple.macos.useScreenMenuBar", "true");

		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			com.apple.eawt.Application macApplication = new com.apple.eawt.Application();

			macApplication.addApplicationListener(new com.apple.eawt.ApplicationListener()
				{
					public void handleAbout(com.apple.eawt.ApplicationEvent event)
					{
						AbstractApplication.this.displayAboutBox();
						event.setHandled(true);
					}

					public void handleQuit(com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(false); // we'd rather handle this ourselves, in our own sweet time

						SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									quit();
								}
							}
						);
					}

					public void handleOpenApplication(com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(true);
					}

					public void handleOpenFile(final com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(true);

						final File theFile = new File(event.getFilename());

						SwingUtilities.invokeLater(new Runnable() {

							public void run()
							{
								PossibleAction possibleAction = new PossibleAction("Open", null)
								{
									public void process(int modifiers) throws Exception
									{
										queueFileToOpen(theFile);
									}
								};

								ActionEvent actionEvent = new ActionEvent(event.getSource(), 0, possibleAction.getName());
								possibleAction.actionPerformed(actionEvent);
							}
						});
					}

					public void handlePreferences(com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(true);

						displayPreferences();
					}

					public void handlePrintFile(com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(true);
					}

					public void handleReOpenApplication(com.apple.eawt.ApplicationEvent event)
					{
						event.setHandled(true);
					}
				}
			);

			macApplication.setEnabledPreferencesMenu(true);
		}		
	}

	public abstract void displayAboutBox();
	
	public abstract void displayPreferences();
	
	public abstract void queueFileToOpen(File theFile) throws IOException;
	
	public abstract String getApplicationName();
}
