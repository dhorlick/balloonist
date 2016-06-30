/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.ApplicationFileAction;
import com.smithandtinkers.gui.ApplicationFrame;
import java.awt.Cursor;
import java.awt.Frame;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

import org.xml.sax.SAXParseException;

import com.smithandtinkers.gui.ListPanel;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.util.WidgetedTypesafeList;


/**
 * @author dhorlick
 */
public class OpenArtworkAction extends ApplicationFileAction
{
	public OpenArtworkAction(String designatedTitle, ApplicationFrame designatedFrame, BalloonistApplication designatedApplication)
	{
		super(designatedTitle, designatedFrame, false, Artwork.BALLOONIST_FILENAME_EXTENSION, designatedApplication);
	}
	
	public OpenArtworkAction(ApplicationFrame designatedFrame, BalloonistApplication designatedApplication)
	{
		this(PresentableEdit.MENU_TEXT.getString("openLabel"), designatedFrame, designatedApplication);
	}
	
	public void processFile(File theFile) throws Exception
	{
		ArtworkFrame preExisting = alreadyOpen(theFile);
		
		// System.out.println("preExisting="+preExisting);
		
		if (preExisting!=null)
		{
			preExisting.toFront();
			return;
		}
		
		ArtworkFrame frameForNewArtwork = null;
		ArtworkFrame originalArtworkFrame = null;
		
		try
		{
			originalArtworkFrame = getArtworkFrame();
			frameForNewArtwork = originalArtworkFrame;

			// System.out.println("processFile | frameForNewArtwork: "+frameForNewArtwork);

			if (frameForNewArtwork==null || frameForNewArtwork.isSignificant())
			{
				// System.out.println("significant");
				frameForNewArtwork = new ArtworkFrame((BalloonistApplication)getApplication());
				frameForNewArtwork.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // since this is a new frame
			}

			List missingFontFamilies = frameForNewArtwork.open(theFile);
			
			if (missingFontFamilies.size()>0)
			{
				final String MISSING_FONTS_MESSAGE = PossibleAction.DIALOG_TEXT.getString("missingFontFamiliesMessage");
				
				WidgetedTypesafeList widgeted = new WidgetedTypesafeList(String.class);
				widgeted.addAll(missingFontFamilies);
				
				final ListPanel listPanel = new ListPanel(widgeted, originalArtworkFrame, MISSING_FONTS_MESSAGE);
				
				final String MISSING_FONT_FAMILIES_LABEL = PossibleAction.DIALOG_TEXT.getString("missingFontLabel");
				JOptionPane.showMessageDialog(originalArtworkFrame, listPanel, MISSING_FONT_FAMILIES_LABEL,
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (SAXParseException exception)
		{
			exception.printStackTrace();
			
			recoverFromProblem(originalArtworkFrame, frameForNewArtwork);
			
			throw new IllegalArgumentException(DIALOG_TEXT.getString("invalidFileFormatMessage"));
		}
		catch (Exception exception)
		{
			recoverFromProblem(originalArtworkFrame, frameForNewArtwork);
			
			throw exception;
		}
		finally
		{
			if (frameForNewArtwork!=null && frameForNewArtwork!=getFrame())
			{
				frameForNewArtwork.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
			}
		}
	}
	
	public ArtworkFrame getArtworkFrame()
	{
		Frame frame = getOrDetermineFrame();
		
		if (frame instanceof ArtworkFrame)
			return (ArtworkFrame) frame;
		
		return null;
	}
	
	/**
	 * Tidies or disposes of the incompletely-loaded proposed artwork frame.
	 */
	private void recoverFromProblem(ArtworkFrame original, ArtworkFrame proposed)
	{
		if (proposed!=null)
		{
			if (original==proposed)
				proposed.applyDefaults();
			else
				proposed.dispose();
		}
	}
	
	public ArtworkFrame alreadyOpen(File requestedFile)
	{
		if (requestedFile==null)
			return null;
		
		Iterator walk = getApplication().getApplicationFrames().iterator();
		
		while (walk.hasNext())
		{
			final Object item = walk.next();
			
			if (item instanceof ArtworkFrame)
			{
				final ArtworkFrame aframe = (ArtworkFrame) item;
				
				if (aframe.getArtwork()!=null && requestedFile.equals(aframe.getArtwork().getFile()))
				{
					return aframe;
				}
			}
		}
		
		return null;
	}
}
