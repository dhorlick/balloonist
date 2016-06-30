/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.Balloonist;
import com.smithandtinkers.gui.AbstractApplication;
import com.smithandtinkers.gui.Application;
import com.smithandtinkers.gui.ApplicationFrame;
import com.smithandtinkers.io.FileList;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.smithandtinkers.layout.PreferencesPlumb;
import com.smithandtinkers.gui.DOptionPane;
import com.smithandtinkers.gui.DocentAction;
import com.smithandtinkers.gui.ExceptionHandler;
import com.smithandtinkers.gui.HypertextPane;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.util.*;

/**
 * The front-facing portion of the Balloonist Swing application.
 *
 * @author dhorlick
 */
public class BalloonistApplication extends AbstractApplication implements Application
{	
	private SplashScreen splashScreen;
	
	public static final String USER_AGREEMENT_LOCATION = "manual/terms.html";

	public BalloonistApplication()
	{
		super();
	}
	
	protected final void init()
	{
		super.init();
		
		if (PlatformFriend.RUNNING_ON_WINDOWS)
		{
			try
			{
				SystemFlavorMap sfm =
				(SystemFlavorMap)SystemFlavorMap.getDefaultFlavorMap();

				sfm.setNativesForFlavor(new DataFlavor("application/pdf; class=java.io.InputStream"), new String [] {"Portable Document Format"} );

				// ^^ it appears critical that TasteSpoon and its associated class-level DataFlavors
				//    have not reached the class loader yet.

				// Why this should be I can only imagine. Maybe something to do with weak hash maps?

			} catch (Exception e) {throw new BugException(e);}
		}

		if (!PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			DOptionPane.showMessageDialog(null,
				PossibleAction.DIALOG_TEXT.getString("javaVersionMessage"),
				PossibleAction.DIALOG_TEXT.getString("javaVersionTitle"), JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
        
		promptForTermsAgreementIfNecessary();

		initSplashScreen();

		ExceptionHandler.install();

		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					final ArtworkFrame artworkFrame = new ArtworkFrame(BalloonistApplication.this);
					artworkFrame.applyDefaults();
				}
			});
		}
		catch (InterruptedException e)
		{
			throw new BugException(e);
		}
		catch (InvocationTargetException e)
		{
			throw new BugException(e);
		}
	}
	
	protected void initializeMacOSX()
	{
		// System.out.println("TEXTLAYOUT_OUTLINE_UNRELIABLE: "+PlatformFriend.TEXTLAYOUT_BOUNDS_UNRELIABLE);
		super.initializeMacOSX();
		
		if (PlatformFriend.TEXTLAYOUT_BOUNDS_UNRELIABLE)
		{
			DOptionPane.showMessageDialog(null,
				PossibleAction.DIALOG_TEXT.getString("macOsVersionMessage"),
				PossibleAction.DIALOG_TEXT.getString("macOsVersionTitle"), JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		UIManager.put("Slider.focusInsets", new javax.swing.plaf.InsetsUIResource(0,0,0,0)); // TODO route out remaining unwanted margin
	}
	
	public void displayPreferences()
	{
		BalloonistPrefsPanel balloonistPrefs = new BalloonistPrefsPanel(BalloonEngineState.getInstance().getBalloonistPreferences());
		int response = JOptionPane.showConfirmDialog(findActiveApplicationFrame(), balloonistPrefs, PresentableEdit.MENU_TEXT.getString("preferencesLabel"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (response==JOptionPane.OK_OPTION)
		{
			PreferencesPlumb.save(
					balloonistPrefs.overlayBalloonistPreferencesTo(
					BalloonEngineState.getInstance().getBalloonistPreferences()));
			jogApplicationFrames();
		}
	}
	
	public void register(ApplicationFrame designatedFrame)
	{
		boolean staggered = stagger(designatedFrame);

		// System.out.println("Staggering result: "+staggered);

		getApplicationFrames().add(designatedFrame);

		// splashScreen.setAlwaysOnTop(false);
		designatedFrame.setApplication(this);
		
		Logger.println("hiding splash screen...");
		
		takeDownSplashScreen();
	}

	public void unregister(ApplicationFrame designatedFrame)
	{
		designatedFrame.setApplication(null);
		getApplicationFrames().remove(designatedFrame);

		if (getApplicationFrames().size()==0)
		{
			System.exit(0);
			// TODO get rid of whatever held refs are making this necessary
		}
	}

	private void initSplashScreen()
	{
		if (!PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER)
		{
			splashScreen = new SplashScreen();
			// splashScreen.setAlwaysOnTop(true);
			splashScreen.setVisible(true);
		}
	}

	public void takeDownSplashScreen()
	{
		if (splashScreen!=null)
		{
			if (splashScreen.isVisible())
				splashScreen.setVisible(false);

			splashScreen = null;
		}

		// TODO make sure that image is being made available for garbage collection
	}
	
	public static void promptForTermsAgreementIfNecessary()
	{
		if (!BalloonEngineState.getInstance().isAgreedToTerms())
		{
			final Dimension desiredSize = new Dimension(650,420);

			JEditorPane htmlPane = new HypertextPane();

			URL agreementURL = ClassLoader.getSystemResource(USER_AGREEMENT_LOCATION);

			try
			{
				htmlPane.setPage(agreementURL);
				// htmlPane.setPreferredSize(null);
			}
			catch (IOException exception)
			{
				System.out.println("agreementURL="+agreementURL);
				throw new BugException(exception);
			}

			// TODO prompt for user's name & org, or at least store username

			JScrollPane htmlScrollPane = new JScrollPane(htmlPane);
			// boxed.setMaximumSize(desiredSize);

			htmlScrollPane.setVerticalScrollBarPolicy(
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			htmlScrollPane.setMaximumSize(desiredSize);
			htmlScrollPane.setPreferredSize(desiredSize);
			htmlScrollPane.setMinimumSize(new Dimension(10, 10));

			Object[] options = {PossibleAction.DIALOG_TEXT.getString("acceptLabel"),
								PossibleAction.DIALOG_TEXT.getString("declineLabel")};

			int reply = JOptionPane.showOptionDialog(null,
					htmlScrollPane,
					PossibleAction.DIALOG_TEXT.getString("usageAgreementLabel"),
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);

			if (reply==JOptionPane.OK_OPTION)
			{
				PreferencesPlumb.saveAgreedToTerms(true);
				BalloonEngineState.getInstance().setAgreedToTerms(true);
			}
			else
			{
				System.exit(0);
			}
		}
	}

	/**
	 * @param designatedMaximumNumberOfExceptions the maximum number of IOExceptions that
	 *        can be encountered before this method gives up.
	 *        providing null tells the method never to give up.
	 *
	 * @return a List of any IOExceptions that were encountered
	 */
	private TypesafeList attemptOpeningQueuedFiles(Integer designatedMaximumNumberOfExceptions)
	{
		Logger.println("Attempt Opening Queued Files: there are "+unopenedFiles.size()+ " file(s) to open. "+new java.util.Date());

		TypesafeList ioExceptionsEncountered = new TypesafeList(IOException.class);

		Iterator walk = unopenedFiles.iterator();

		while (walk.hasNext())
		{
			try
			{
				File file = (File) walk.next();
				open(file);
			}
			catch (IOException exception)
			{
				if (designatedMaximumNumberOfExceptions==null || ioExceptionsEncountered.size()<designatedMaximumNumberOfExceptions.intValue())
				{
					ioExceptionsEncountered.add(exception);
				}
				else
				{
					return ioExceptionsEncountered;
				}
			}

			walk.remove();
		}

		return ioExceptionsEncountered;
	}
	
	public void queueFileToOpen(File designatedFile) throws IOException
	{
		/* System.out.println("Queueing file. "+new java.util.Date());
		System.out.println("\tthis="+this);
		System.out.println("\tclass loader="+getClass().getClassLoader());
		System.out.println("\tcurrent thread="+Thread.currentThread()); */

		ApplicationFrame active = findActiveApplicationFrame();
		if (active == null)
		{
			// initialization still in progress

			unopenedFiles.add(designatedFile);

			// System.out.println("\tqueued files = "+unopenedFiles.size());
		}
		else
		{
			// System.out.println("\tapplication is initialized. Opening that queued file immediately...");
			open(designatedFile);
		}
	}
	
	private void open(File requestedFile) throws IOException
	{
		ApplicationFrame active = findActiveApplicationFrame();

		try
		{
			OpenArtworkAction openArtworkAction = new OpenArtworkAction(active, this);
			openArtworkAction.processFile(requestedFile);
		}
		catch (IOException exception)
		{
			throw exception;
		}
		catch (RuntimeException exception)
		{
			throw exception;
		}
		catch (Exception exception)
		{
			throw new BugException(exception);
		}
	}
	
	public void open(final FileList designatedFileList)
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					final Balloonist balloonist = Balloonist.getInstance();
					/* System.out.println("\tinstance="+balloonist);
					System.out.println("\tclass loader="+balloonist.getClass().getClassLoader());
					System.out.println("\tcurrent thread="+Thread.currentThread()); */

					List exceptions = attemptOpeningQueuedFiles(new Integer(6));

					Iterator walkFiles = designatedFileList.iterator();
					
					while (walkFiles.hasNext())
					{
						final File file = (File) walkFiles.next();
						
						try
						{
							open(file);
						}
						catch (IOException exception)
						{
							exception.printStackTrace();
						}
					}

					// TODO display any exceptions in a dialog box
				}
			});
		}
		catch (InvocationTargetException ex)
		{
			throw new BugException(ex);
		}
		catch (InterruptedException ex)
		{
			throw new BugException(ex);
		}
	}

	public void displayAboutBox()
	{
		new DocentAction(findActiveApplicationFrame()).displayAboutBox();
	}

	public String getApplicationName()
	{
		return BalloonEngineState.APP_PROPS.getString("name");
	}
}
