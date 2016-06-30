/**
 * Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.UrlAction;
import com.smithandtinkers.svg.GraphicalContent;
import java.net.URL;


/**
 * @author dhorlick
 */
public class PlaceUrlAction extends UrlAction
{
	private boolean linked;
	
	public PlaceUrlAction(String designatedTitle, ArtworkFrame designatedArtworkFrame, boolean designatedLinkedness)
	{
		super(designatedTitle, designatedArtworkFrame);
		linked = designatedLinkedness;
	}
	
	public void processUrl(URL theUrl) throws Exception
	{
		GraphicalContent graphicalContent = new GraphicalContent();
		// graphicalContent.setComponent(getArtworkFrame().getCanvas());
		graphicalContent.read(theUrl);
		graphicalContent.setLinked(linked);
		
		PlaceFileAction.addToTargetSill(graphicalContent, getArtworkFrame(), linked);
	}
	
	public ArtworkFrame getArtworkFrame()
	{
		return (ArtworkFrame) getFrame();
	}
}
