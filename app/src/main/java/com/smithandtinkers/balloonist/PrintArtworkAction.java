/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContextFactory;
import com.smithandtinkers.graphics.awt.DefaultDrawingContext;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Selection;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.*;


/**
 * Prints artwork using the Java Printing API
 *
 * @author dhorlick
 */
public class PrintArtworkAction extends PossibleAction implements Printable
{
	private ArtworkFrame artworkFrame;
	
	public PrintArtworkAction(final String designatedTitle, final ArtworkFrame designatedArtworkFrame)
	{
		super(designatedTitle); // TODO i8n
		setArtworkFrame(designatedArtworkFrame);
	}
	
	private void setArtworkFrame(ArtworkFrame designatedArtworkFrame)
	{
		artworkFrame = designatedArtworkFrame;
	}

	public Artwork getArtwork()
	{
		if (getArtworkFrame()==null)
			return null;
		else
			return getArtworkFrame().getArtwork();
	}
	
	public ArtworkFrame getArtworkFrame()
	{
		return artworkFrame;
	}
	
	public int print(Graphics graphics, PageFormat format, int pageNumber)
	{
		// System.out.println("Received a print request");
		
		if (getArtwork().getSill()==null || graphics==null)
			return PAGE_EXISTS;
		
		Graphics2D graphics2D = (Graphics2D) graphics;
			
		if (pageNumber == 0)
		{	
			// System.out.println("Printing page 0");
			
			// setDoubleBuffered(false);
			
			final DefaultDrawingContext drawingContext = (DefaultDrawingContext) DrawingContextFactory.getDrawingContext(); // TODO disintermediate!
			drawingContext.setSelected(new Selection()); // TODO figure out why this is necessary
			drawingContext.setGraphics(graphics2D);
			
			getArtwork().getSill().draw(drawingContext);

			// setDoubleBuffered(true);
			
			return Printable.PAGE_EXISTS;
		}
		else
		{
			// System.out.println("not printing page: "+pageNumber);
			
			return Printable.NO_SUCH_PAGE;
		}
	}

	public static String describePaper(Paper designatedPaper)
	{
		if (designatedPaper==null)
			return "null";

		return "java.awt.print.Paper {"
			 + "mHeight = " + designatedPaper.getHeight() + ", "
			 + "mWidth = " + designatedPaper.getWidth() + ", " 
			 + "imageableX = " + designatedPaper.getImageableX() + ", "
			 + "imageableY = " + designatedPaper.getImageableY() + ", "
			 + "imageableWidth = " + designatedPaper.getImageableWidth() + ", "
			 + "imageableHeight = " + designatedPaper.getImageableHeight()
		+ "}";
	}

	public void process(int modifiers) throws Exception
	{
		if (getArtwork()==null)
		{
			throw new IllegalStateException("No artwork populated.");
		}
		
		try
		{
			PrinterJob printJob = PrinterJob.getPrinterJob();
		
			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();

			// We've already considered margins.

			if (getArtwork().getEnclosure().getWidth() > getArtwork().getEnclosure().getHeight())
			{
				pageFormat.setOrientation(PageFormat.LANDSCAPE);
				paper.setImageableArea(0.0, 0.0, 
						pageFormat.getHeight(), pageFormat.getWidth());
			}
			else
			{
				pageFormat.setOrientation(PageFormat.PORTRAIT);
				paper.setImageableArea(0.0, 0.0, 
						pageFormat.getWidth(), pageFormat.getHeight());
			}

			pageFormat.setPaper(paper);

			printJob.setPrintable(this, pageFormat);

			// System.out.println(describePaper(pageFormat.getPaper()));
			// System.out.flush();
			// System.exit(0);

			if (printJob.printDialog())
			{			
				printJob.print();
			}
		}
		catch (PrinterException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
