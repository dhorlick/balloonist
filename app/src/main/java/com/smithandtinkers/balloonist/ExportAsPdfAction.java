/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.gui.Application;
import com.smithandtinkers.gui.ApplicationFileAction;
import com.smithandtinkers.gui.ApplicationFrame;
import com.smithandtinkers.gui.FileAction;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Drawing;
import com.smithandtinkers.layout.edit.PresentableEdit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author dhorlick
 */
public class ExportAsPdfAction extends ApplicationFileAction
{
	public ExportAsPdfAction(ApplicationFrame designatedFrame, Application designatedApplication)
	{
		super(PresentableEdit.MENU_TEXT.getString("exportAsPdfLabel"), designatedFrame, true, "pdf", designatedApplication);
	}

	public void processFile(File theFile) throws Exception
	{
		final FileOutputStream fileOutputStream = new FileOutputStream(theFile);
		
		// System.out.println("getArtwork()="+getArtwork());
		// System.out.println("getArtwork().getEnclosure()="+getArtwork().getEnclosure());
		
		PdfFriend.writeToPortableDocumentFormat(new Drawing()
		{
			public void drawOnto(DrawingContext drawingContext)
			{
				getArtwork().getSill().draw(drawingContext);
			}
			
		}, fileOutputStream, (float)getArtwork().getWidth(), (float)getArtwork().getHeight(), 
				(float)getArtwork().getEnclosure().getWidth(), (float)getArtwork().getEnclosure().getHeight());
		
		try
		{
			fileOutputStream.close();
		}
		catch (IOException exception)
		{
		}

	}
	
	public ArtworkFrame getArtworkFrame()
	{
		return (ArtworkFrame) getApplicationFrame();
	}
	
	public Artwork getArtwork()
	{
		if (getArtworkFrame()!=null)
			return getArtworkFrame().getArtwork();
		else
			return null;
	}
}
