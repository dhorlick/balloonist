/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.smithandtinkers.gui.FileAction;
import com.smithandtinkers.svg.SvgFlavor;
import com.smithandtinkers.svg.SvgOutputter;

/**
 * @author dhorlick
 */
public class ExportAsSvgAction extends FileAction
{
	private ArtworkFrame artworkFrame;
	private SvgFlavor flavor;
	private final SvgOutputter svgOutputter = new SvgOutputter();
	
	public ExportAsSvgAction(String designatedTitle, Frame designatedFrame, boolean designatedSaveMode, ArtworkFrame designatedArtworkFrame, SvgFlavor designatedFlavor)
	{
		super(designatedTitle, designatedFrame, designatedSaveMode, "svg");
		artworkFrame = designatedArtworkFrame;
		setFlavor(designatedFlavor);
	}

	public SvgFlavor getFlavor()
	{
		return flavor;
	}
	
	public void setFlavor(SvgFlavor designatedFlavor)
	{
		flavor = designatedFlavor;
	}
	
	/**
	 * @see com.smithandtinkers.gui.FileAction#processFile(java.io.File)
	 */
	public void processFile(File theFile) throws Exception
	{
		final List drawableList = new ArrayList();
		
		drawableList.add(artworkFrame.getArtwork().getSill());
		
		svgOutputter.outputSvg(new FileOutputStream(theFile), drawableList, true, getFlavor());
	}
}
