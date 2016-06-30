/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.FileAction;
import com.smithandtinkers.layout.AbstractResizeable;
import com.smithandtinkers.layout.edit.CreateEdit;
import com.smithandtinkers.layout.GraphicResizeable;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.svg.BudgetImageIO;
import com.smithandtinkers.svg.GraphicalContent;

import java.awt.geom.Rectangle2D;
import java.io.File;


/**
 * Places an image file. 
 *
 * @author dhorlick
 */
public class PlaceFileAction extends FileAction
{
	private ArtworkFrame artworkFrame;
	private boolean linked;
	
	public PlaceFileAction(String designatedTitle, ArtworkFrame designatedArtworkFrame, boolean designatedLinkedness)
	{
		super(designatedTitle, designatedArtworkFrame, false);
		artworkFrame = designatedArtworkFrame;
		linked = designatedLinkedness;
		addOptionalFileExtension(BudgetImageIO.FORMAT_PORTABLE_NETWORK_GRAPHICS);
		addOptionalFileExtension(BudgetImageIO.FORMAT_JOINT_PHOTO_EXPERTS_GROUP);
		addOptionalFileExtension(BudgetImageIO.FORMAT_GRAPHICS_INTERCHANGE_FORMAT);
	}
	
	public void processFile(File theFile) throws Exception
	{
		GraphicalContent graphicalContent = new GraphicalContent();
		// graphicalContent.setComponent(getArtworkFrame().getCanvas());
		graphicalContent.read(theFile);
		graphicalContent.setLinked(linked);
		
		
		
		addToTargetSill(graphicalContent, getArtworkFrame(), linked);
	}
	
	public ArtworkFrame getArtworkFrame()
	{
		return artworkFrame;
	}
	
	public static void addToTargetSill(GraphicalContent graphicalContent, ArtworkFrame artworkFrame,
			boolean designatedLinkedness)
	{
		Sill targetSill = artworkFrame.targetSill();
		GraphicResizeable graphicResizeable = new GraphicResizeable();
		
		String actionTitle = null;
		
		if (designatedLinkedness)
			actionTitle = PresentableEdit.MENU_TEXT.getString("placeLinkedLabel");
		else
			actionTitle = PresentableEdit.MENU_TEXT.getString("placeEmbeddedLabel");
			
		CreateEdit createEdit = new CreateEdit(actionTitle, targetSill, graphicResizeable);
		
		graphicResizeable.setX(targetSill.getWidth()/2.0);
		graphicResizeable.setY(targetSill.getHeight()/2.0);
		graphicResizeable.setGraphicalContent(graphicalContent);
		
		Rectangle2D targetSillBounds = targetSill.getResizeableBounds2D();
		
		if (targetSillBounds!=null)
			AbstractResizeable.fit(graphicResizeable, targetSillBounds, true);
			
		if (createEdit.execute())
		{
			artworkFrame.getCanvas().queueEdit(createEdit);
			artworkFrame.getCanvas().announceEdit();
		}
	}
}
