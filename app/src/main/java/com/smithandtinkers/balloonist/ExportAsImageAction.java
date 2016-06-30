/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContextFactory;
import com.smithandtinkers.graphics.awt.DefaultDrawingContext;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.SimpleExportProfile;
import com.smithandtinkers.gui.FileAction;


/**
 * @author dhorlick
 */
public class ExportAsImageAction extends FileAction
{
	private double scaleFactor = 1.0;
	
	public ExportAsImageAction(String designatedTitle, Frame designatedFrame, ArtworkFrame designatedArtworkFrame, String designatedFormat)
	{
		super(designatedTitle, designatedFrame, true, designatedFormat);
	}
	
	public String getFormat()
	{
		return getOptionalFileExtension();
	}
	
	/**
	 * @see com.smithandtinkers.gui.FileAction#processFile(java.io.File)
	 */
	public void processFile(File theFile) throws Exception
	{
		FileOutputStream outputStream = new FileOutputStream(theFile);
		
		Dimension enclosure = getArtworkFrame().getArtwork().getEnclosure();
		
		DefaultDrawingContext drawingContext = (DefaultDrawingContext)DrawingContextFactory.getDrawingContext();
		BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment(
				).getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
				(int)Math.ceil(scaleFactor * (double)enclosure.getWidth()), 
				(int)Math.ceil(scaleFactor * (double)enclosure.getHeight()));
		Graphics2D g2 = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(image);
		drawingContext.setGraphics(g2);
		
		if (getScaleFactor()!=1.0)
			drawingContext.scale(getScaleFactor());
		
		drawingContext.setSelected(new Selection());
		drawingContext.setExportProfile(new SimpleExportProfile());
		drawingContext.antiAliasLines();
		
		drawingContext.setColor(Color.white);
		drawingContext.fillRect(0, 0, getArtworkFrame().getArtwork().getEnclosure().width, getArtworkFrame().getArtwork().getEnclosure().height);
				
		getArtworkFrame().getArtwork().getSill().draw(drawingContext);

		ImageIO.write(image, getFormat(), outputStream);
		
		g2.dispose();
	}
	
	public ArtworkFrame getArtworkFrame()
	{
		return (ArtworkFrame) getFrame();
	}

	public void setScaleFactor(double designatedScaleFactor)
	{
		if (designatedScaleFactor!=scaleFactor)
			scaleFactor = designatedScaleFactor;
	}
	
	public double getScaleFactor()
	{
		return scaleFactor;
	}
}
