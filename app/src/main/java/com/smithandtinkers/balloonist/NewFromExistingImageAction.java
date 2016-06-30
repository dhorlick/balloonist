package com.smithandtinkers.balloonist;

import java.awt.geom.Rectangle2D;
import java.io.File;

import com.smithandtinkers.gui.ApplicationFileAction;
import com.smithandtinkers.layout.AbstractResizeable;
import com.smithandtinkers.layout.GraphicResizeable;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.svg.BudgetImageIO;
import com.smithandtinkers.svg.GraphicalContent;

public class NewFromExistingImageAction extends ApplicationFileAction 
{
	private boolean linked;
	
	public NewFromExistingImageAction(ArtworkFrame designatedArtworkFrame, boolean designatedLinked)
	{
		super(PresentableEdit.MENU_TEXT.getString(
				designatedLinked?"newFromExistingLinkedImageLabel":"newFromExistingEmbeddedImageLabel"), 
				designatedArtworkFrame, false);
		
		linked = designatedLinked;
		
		addOptionalFileExtension(BudgetImageIO.FORMAT_PORTABLE_NETWORK_GRAPHICS);
		addOptionalFileExtension(BudgetImageIO.FORMAT_JOINT_PHOTO_EXPERTS_GROUP);
		addOptionalFileExtension(BudgetImageIO.FORMAT_GRAPHICS_INTERCHANGE_FORMAT);
	}
	
	public void processFile(File theFile) throws Exception
    {
		GraphicalContent graphicalContent = new GraphicalContent();
		graphicalContent.read(theFile);
		graphicalContent.setLinked(linked);
	    
		// so far, so good
		
		// TODO look for alreadyOpen one?
		
		ArtworkFrame frameForNewArtwork = new ArtworkFrame((BalloonistApplication)getApplication());
		frameForNewArtwork.applyDefaults();
		
		frameForNewArtwork.getArtwork().getLayout().setApertureQuantity(1);
		frameForNewArtwork.getArtwork().getLayout().setAspectRatio(graphicalContent.determineAspectRatio());
		// frameForNewArtwork.getArtwork().enclose();
		frameForNewArtwork.getArtwork().getLayout().syncSill(frameForNewArtwork.getArtwork().getSill());
		GraphicResizeable graphicResizeable = new GraphicResizeable(graphicalContent);
		final Sill targetSill = (Sill) frameForNewArtwork.getArtwork().getSill().get(0);
		targetSill.setOutlineColor(null);
		
		graphicResizeable.setX(targetSill.getWidth()/2.0);
		graphicResizeable.setY(targetSill.getHeight()/2.0);
		Rectangle2D targetSillBounds = targetSill.getResizeableBounds2D();
		
		if (targetSillBounds!=null)
			AbstractResizeable.fit(graphicResizeable, targetSillBounds, false);
		
		targetSill.add(graphicResizeable);
		
		// TODO maybe lock the image so it can't be selected.
    }
}
