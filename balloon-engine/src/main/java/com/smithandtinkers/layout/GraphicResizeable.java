/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import com.smithandtinkers.BalloonEngineState;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.svg.ImageKernelMaker;
import com.smithandtinkers.svg.GraphicalContent;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.ChainedRuntimeException;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.Named;


public class GraphicResizeable extends AbstractResizeable implements Drawable, Named, ChangeListener
{
	private GraphicalContent graphicalContent;
	private boolean refreshmentRequested;
	
	private transient BufferedImage scaledImage;
	
	private transient Wallpaper insufficientMemoryWallpaper;
	
	public static final String IDENTIFIER_GRAPHICAL_CONTENT = "graphical-content";
		private static final String IDENTIFIER_SOURCE = "source";
		private static final String IDENTIFIER_COMPONENT = "component";
		
	private double lastEncounteredScaleFactor = 1.0; // I'm not any happier about this variable than you are.
	
	public GraphicResizeable()
	{
		super();
	}
	
	public GraphicResizeable(GraphicalContent designatedGraphicalContent)
	{
		this();
		setGraphicalContent(designatedGraphicalContent);
	}

	public GraphicalContent getGraphicalContent()
	{
		if (graphicalContent==null)
			setGraphicalContent(new GraphicalContent());
		
		return graphicalContent;
	}

	public void setGraphicalContent(GraphicalContent designatedGraphicalContent)
	{
		if (designatedGraphicalContent!=graphicalContent)
		{
			GraphicalContent oldGraphicalContent = graphicalContent;
			
			graphicalContent = designatedGraphicalContent;
			
			if (graphicalContent!=null)
			{
				if (graphicalContent.getWidth()>0)
					setWidth(graphicalContent.getWidth());
				
				if (graphicalContent.getHeight()>0)
					setHeight(graphicalContent.getHeight());
				
				// Logger.println("add change listener to self...");
				graphicalContent.getChangeSupport().addChangeListener(this);
				// Logger.println("\tresult: "+graphicalContent.getChangeSupport());
				
				stateChanged(new ChangeEvent(graphicalContent));
			}
			
			if (oldGraphicalContent!=null)
				oldGraphicalContent.getChangeSupport().removeChangeListener(this);
		}
	}

	/**
	 * @see com.smithandtinkers.layout.Drawable#draw(com.smithandtinkers.graphics.DrawingContext)
	 */
	public void draw(DrawingContext drawingContext)
	{
		Selection selected = drawingContext.getSelected();
		final double currentScaleFactor = drawingContext.getScaleFactor();			
			
		int x = (int)( getLocation().getX() - getWidth()/2.0 );
		int y = (int)( getLocation().getY() - getHeight()/2.0 );
        
		int width = (int) getWidth();
		int height = (int) getHeight();

		// final long START = System.currentTimeMillis();
		
		if (graphicalContent.isTooBig())
		{
			indicateInsufficientMemoryToDisplayImage(drawingContext);
		}
		else if (graphicalContent.isBroken() || !graphicalContent.isDedicated())
		{
			drawingContext.setColor(Color.red);
			drawingContext.drawLine(x, y, x+width, y+height);
			drawingContext.drawLine(x+width, y, x, y+height);
		}
		else
		{
			boolean drawn = false;
			// System.out.print("painting good image; view scale factor: ");
			// System.out.println(currentScaleFactor);
			
			if (drawingContext.isIntermediate()
					|| !drawingContext.isAntiAliasingLinesAndImages())
			{
				if (graphicalContent.getImage()!=null)
					drawingContext.drawImage(graphicalContent.getImage(), x, y, width, height, null);
				else
					indicateIncompletelyLoadedImage(drawingContext); // TODO instead, try and cobble something together from previous scale image (if there is one)
			}
			else
			{
				// System.out.println("\tchecking on refreshment appropriateness");
				
				if (graphicalContent.getImage()!=null && (scaledImage == null || lastEncounteredScaleFactor!=currentScaleFactor))
				{										
					if (graphicalContent.getImage().getWidth()!=getWidth() || graphicalContent.getImage().getHeight()!=getHeight()
							|| lastEncounteredScaleFactor!=currentScaleFactor)
					{
						// System.out.println("\t*** indeed appropriate! already in progress?");
						
						if (!isRefreshmentRequested())
						{
							Logger.println("image refresh: go");
							
							SwingUtilities.invokeLater(new Runnable() // switch to SwingWorker someday?
							{		
								public void run()
								{
									// System.out.println("queueing refreshing request: "+currentScaleFactor);
									refreshmentRequested = true;
						
									refreshScaledImage(currentScaleFactor);
								}
							});
						}
						else
						{
							Logger.println("image refreshment already requested.");
						}
					}
					else
					{
						// no scaling is required
						
						scaledImage = graphicalContent.getImage();
					}
				}
				
				if (scaledImage!=null)
				{
					// we're going to handle scaling ourselves
					
					drawingContext.drawImage(scaledImage, x, y, (int)getWidth(), (int)getHeight(), null);
					
					// I guess the width & height parameters are understood to be in "real" (i.e., unscaled) units
				}
				else if (graphicalContent.getImage()!=null)
				{
					drawingContext.drawImage(graphicalContent.getImage(), x, y, width, height, null);
				}
				else
				{
					indicateIncompletelyLoadedImage(drawingContext);
				}
			}
		}
		
		// If this is still down as selected from the last painting, let's go ahead and depict its selection
		
		drawAfter(drawingContext);
		
		if (selected!=null && selected.isDirty()
				&& (selected.getPointChoice()!=null || selected.getRectChoice()!=null))
		{
			// TODO ^^ ideally, Selection would be able to perform this mapping for us
			
			if (selected.getPointChoice()!=null)
			{
				double localX = Sill.mapAbsoluteXIntoCoordinateFrameOf(selected.getPointChoice().getX(), this);
				double localY = Sill.mapAbsoluteYIntoCoordinateFrameOf(selected.getPointChoice().getY(), this);
			
				if (localX > x && localX < x+getWidth()
						&& localY > y && localY < y + getHeight())
				{
					selected.registerSelection(this);
				}
			}
			else if (selected.getRectChoice()!=null)
			{
				if (selected.getRectChoice().intersects(x, y, getWidth(), getHeight() ))
					selected.registerSelection(this);
			}
		}
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, org.w3c.dom.Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		if (node instanceof Element)
		{
			Element element = (Element) node;
			extractAttributesAndSubnodes(parent, element, archiveContext);
		}
	}
	
	public void extractAttributesAndSubnodes(Object parent, Element element, ArchiveContext archiveContext)
	{
		try
		{
			String sourceUrlAsString = null;
			
			if (element.hasAttribute(IDENTIFIER_SOURCE))
			{
				sourceUrlAsString = element.getAttribute(IDENTIFIER_SOURCE);
			}
			
			if (element.hasAttribute(IDENTIFIER_COMPONENT))
			{
				String componentName = element.getAttribute(IDENTIFIER_COMPONENT);
				// System.out.println("sourceUrlAsString: "+sourceUrlAsString);
				
				if (archiveContext.containsName(componentName))
				{
					// embedded
					
					GraphicalContent red = archiveContext.read(componentName);
					setGraphicalContent(red);
					
					if (red==null || red.getImage()==null)
					{
						throw new IllegalStateException("Graphics missing from archive");
					}
				}
				else
				{
					System.err.println("Couldn't fix graphics for "+componentName);
					System.err.println("\tarchiveContext: "+archiveContext);
				}
			}
			else if (sourceUrlAsString!=null)
			{
				// linked
				
				URL sourceUrl = new URL(sourceUrlAsString);
				Logger.println("setting source to: "+sourceUrl);
				getGraphicalContent().setLinked(true);
				getGraphicalContent().setSource(sourceUrl);
				
				Logger.println("(re)freshing...");
				
				getGraphicalContent().refreshFromLink();
			}
			
			if (sourceUrlAsString!=null)
				getGraphicalContent().setSource(sourceUrlAsString);
				
			super.extractAttributesAndSubElements(parent, element);
				// we do this afterwards to avoid overwriting user-specified dimensions
		}
		catch (MalformedURLException exception)
		{
			throw new ChainedRuntimeException(exception); // TODO find less-severe alternative 
		}
		catch (IOException exception)
		{
			throw new ChainedRuntimeException(exception); // TODO find less-severe alternative
		}
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element graphicalContentElement = doc.createElement(IDENTIFIER_GRAPHICAL_CONTENT);
		applyAttributesAndSubElements(doc, graphicalContentElement, archiveContext);
		parent.appendChild(graphicalContentElement);
	}
	
	public void applyAttributesAndSubElements(Document doc, Element element, ArchiveContext archiveContext)
	{
		super.applyAttributesAndSubElements(doc, element);
		
		if (graphicalContent.getSource()!=null)
		{
			element.setAttribute(IDENTIFIER_SOURCE, graphicalContent.getSource().toString());
		}
		
		if (!graphicalContent.isLinked())
		{
			String componentName = archiveContext.write(graphicalContent);
			
			// System.out.println("GraphicResizeable: wrote graphical content to archive context as " + componentName);
			
			element.setAttribute(IDENTIFIER_COMPONENT, componentName);
		}
	}
	
	public String toString()
	{
		if (getName()!=null)
			return getName();
		
		if (graphicalContent!=null)
		{
			String graphicalContentName = graphicalContent.toString();
			
			if (graphicalContentName!=null)
				return graphicalContentName;
		}
		
		return AbstractNamed.NAMES_TEXT.getString("graphicLabel");
	}

	public void stateChanged(ChangeEvent e)
	{
		scaledImage = null;
		// System.out.println("\tgetParent()="+getParent());
		SingleThreadedChangeSupport.fireChangeEvent(this);
	}
	
	public void drawAfter(DrawingContext drawingContext)
	{
		if (drawingContext.getSelected()!=null && drawingContext.getSelected().isSelected(this))
		{
			drawingContext.setColor(Resizeable.TRANSLUCENT_BLUE);
			drawingContext.fill(getResizeableBounds2D());
		}
		
		drawingContext.setColor(Color.black);
		
		super.drawAfter(drawingContext);
	}
	public void setHeight(double designatedHeight)
	{
		super.setHeight(designatedHeight);
		scaledImage = null;
	}

	public void setWidth(double designatedWidth)
	{
		super.setWidth(designatedWidth);
		scaledImage = null;
	}

	public void setBounds(java.awt.geom.Rectangle2D designatedBounds)
	{
		super.setBounds(designatedBounds);
		scaledImage = null;
	}

	public void setSize(double designatedWidth, double designatedHeight)
	{
		super.setSize(designatedWidth, designatedHeight);
		scaledImage = null;
	}

	public void setBounds(double newX, double newY, double newWidth, double newHeight)
	{
		super.setBounds(newX, newY, newWidth, newHeight);
		scaledImage = null;
	}
	
	/*
	protected void finalize() throws Throwable
	{
		System.out.println("finalizing: "+this);
		super.finalize();
	}
	*/
	
	/**
	 * @param scaleFactor indicates by what factor we can expect the ultimate product to be scaled.
	 * We don't necessarily need to actually scale the image by this amount, but it is helpful to know for
	 * determining how much pyramidization is appropriate.
	 */
	private void refreshScaledImage(final double scaleFactor)
	{		
		// System.out.print("GraphicResizeable: refreshing scaled image at view scale factor: ");
		// System.out.println(scaleFactor);

		int width = (int) getWidth();
		int height = (int) getHeight();

		RenderingHints renderingHints = new RenderingHints(null);

		renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		renderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY );

		double widthFactor = scaleFactor*(double)width/getGraphicalContent().getWidth();
		double heightFactor = scaleFactor*(double)height/getGraphicalContent().getHeight();

		final float TAU_w = 1f/(float)(widthFactor);
		final float TAU_h = 1f/(float)(heightFactor);

		final int kernelWidth = (int) (2f * TAU_w);	// TODO confirm this is a good rule of thumb
		final int kernelHeight = (int) (2f * TAU_h);

		AffineTransform resizeTransform = AffineTransform.getScaleInstance(widthFactor, heightFactor);
		AffineTransformOp resizeImageOp = new AffineTransformOp(resizeTransform, renderingHints);

		if (!graphicalContent.isJpeg() && kernelWidth>2 && kernelHeight>2)
		{						
			final float [] kernelKernel = ImageKernelMaker.makeKernel(kernelWidth, kernelHeight,
					new ImageKernelMaker.ConeFunction((int)TAU_w, (int)TAU_h));
			// Colonel.print(kernelKernel, kernelWidth);
			Kernel kernel = new Kernel(kernelWidth, kernelHeight, kernelKernel);
			ConvolveOp pyramidize = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, renderingHints);

			// System.out.println("graphicalContent.getImage(): "+graphicalContent.getImage());
			BufferedImage blurred = pyramidize.filter(graphicalContent.getImage(), null);
			scaledImage = resizeImageOp.filter(blurred, null);

			lastEncounteredScaleFactor = scaleFactor;

			// TODO save memory; implement in-place convolution in concert with image tiling or custom bliting
		}
		else
		{
			scaledImage = resizeImageOp.filter(graphicalContent.getImage(), null);
		}

		// it might be better to skip the scaling op and use drawImage(image, x, y, w, h)

		SingleThreadedChangeSupport.fireChangeEvent(this);

		Logger.println("GraphicResizeable: done refreshing scaled image!");

		refreshmentRequested = false;
	}
	
	private void indicateIncompletelyLoadedImage(DrawingContext drawingContext)
	{
		int x = (int)( getLocation().getX() - getWidth()/2.0 );
		int y = (int)( getLocation().getY() - getHeight()/2.0 );
		
		int width = (int) getWidth();
		int height = (int) getHeight();
		
		drawingContext.setColor(Color.lightGray);
		drawingContext.fillRect(x, y, width, height);
		
		// TODO if would look better to write a titled repeating message or print a whole bunch of clocks
		
		try
		{
			graphicalContent.refreshFromFileCacheIfNecessary();
		}
		catch (IOException exception)
		{
			// this will set the broken flag, so we don't need to do anything further
		}
	}
	
	public boolean isRefreshmentRequested()
	{
		return refreshmentRequested; // TODO effectively synchronize?
	}
	
	private void indicateInsufficientMemoryToDisplayImage(DrawingContext drawingContext)
	{
		if (insufficientMemoryWallpaper==null)
		{
			insufficientMemoryWallpaper = new Wallpaper();
			insufficientMemoryWallpaper.setText(BalloonEngineState.DIALOG_TEXT.getString("memoryExhaustedMessage"));
			insufficientMemoryWallpaper.setFontSize(18f);
			insufficientMemoryWallpaper.setColor(Color.orange);
		}
		
		final Shape oldClip = drawingContext.getClip();
		drawingContext.clip(getPreferredBounds());
		insufficientMemoryWallpaper.draw(drawingContext, getPreferredBounds());
		drawingContext.setClip(oldClip);
	}
	
	public Rectangle2D getResizeableBounds2D()
	{
		Rectangle2D rect = super.getResizeableBounds2D();
		
		Sill grandad = (Sill) findForebear(Sill.class);
			
		if (grandad!=null && grandad.getAperture()!=null && grandad.getAperture().getShape() instanceof Rectangle2D)
			Rectangle2D.intersect(rect, (Rectangle2D)grandad.getAperture().getShape(), rect);
		
		return rect;
	}
}
