/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;

/**
 * @author dhorlick
 */
public class AbstractResizeable extends AbstractRelocateable implements Resizeable, VisiblySelectable
{
	private static final String IDENTIFIER_HEIGHT_IN_POINTS = "height-in-points";
	private static final String IDENTIFIER_WIDTH_IN_POINTS = "width-in-points";
	
    /**
     * A bounding Rectangle2D that must always be maintained so as to be centered around
     * the origin.
     */
	private Rectangle2D boundingRectangle = new Rectangle2D.Double();
	
    /**
     * @param rect a template rectangle
     */
	protected void resizeBoundsTo(Rectangle2D rect)
	{
		if (rect.getCenterX()==0.0 && rect.getCenterY()==0.0)
        {
            boundingRectangle.setRect(rect);
            return;
        }
        
        boundingRectangle.setRect(-rect.getWidth()/2.0, -rect.getHeight()/2.0, 
                rect.getWidth(), rect.getHeight());
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		if (designatedSide==PerimeterSegment.TOP_SIDE)
		{
			translate(0, (newY-oldY)/2.0);
			setHeight(getHeight()+oldY-newY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_SIDE)
		{
			translate(0, (newY-oldY)/2.0);
			setHeight(getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.LEFT_SIDE)
		{
			translate((newX-oldX)/2.0, 0.0);
			setWidth(getWidth()+oldX-newX);
		}
		else if (designatedSide==PerimeterSegment.RIGHT_SIDE)
		{
			translate((newX-oldX)/2.0, 0.0);
			setWidth(getWidth()+newX-oldX);
		}
		else if (designatedSide==PerimeterSegment.TOP_LEFT_CORNER)
		{			
			translate((newX-oldX)/2.0, (newY-oldY)/2.0);
            setSize(getWidth()+oldX-newX, getHeight()+oldY-newY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_LEFT_CORNER)
		{
			translate((newX-oldX)/2.0, (newY-oldY)/2.0);
            setSize(getWidth()+oldX-newX, getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_RIGHT_CORNER)
		{
			translate((newX-oldX)/2.0, (newY-oldY)/2.0);
            setSize(getWidth()+newX-oldX, getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.TOP_RIGHT_CORNER)
		{
			translate((newX-oldX)/2.0, (newY-oldY)/2.0);
            setSize(getWidth()+newX-oldX, getHeight()+oldY-newY);
		}
		
		// setLayedOut(false);
		
		return true;
	}
	
	/**
	 * @param designatedLocation The location to set.
	 */
	public void setLocation(Point2D designatedLocation)
	{
		setLocation(designatedLocation.getX(), designatedLocation.getY());
	}
    
	/**
	 * The actual rectangular boundary, or if unavailable, the preferred one.
     *
     * Will generally not be origin-centered.
     * 
     * Makes a defensive copy, if necessary.
	 * 
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		return getPreferredBounds();
	}
	
    /**
     * A reference to the origin-centered boundary. Use with caution.
     */
	protected Rectangle2D getBoundingRectangle()
	{
		return boundingRectangle;
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return getBoundingRectangle().getWidth();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return getBoundingRectangle().getHeight();
	}
	
	public void setWidth(double designatedWidth)
	{
		getBoundingRectangle().setRect(-designatedWidth/2.0, getBoundingRectangle().getY(), designatedWidth, getBoundingRectangle().getHeight());
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setHeight(double designatedHeight)
	{
		getBoundingRectangle().setRect(getBoundingRectangle().getX(), -designatedHeight/2.0, getBoundingRectangle().getWidth(), designatedHeight);
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setSize(double designatedWidth, double designatedHeight)
	{
		getBoundingRectangle().setRect(-designatedWidth/2.0, -designatedHeight/2.0, designatedWidth, designatedHeight);
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setBounds(Rectangle2D designatedBounds)
	{
		super.getLocation().setLocation(designatedBounds.getX()+designatedBounds.getWidth()/2.0,
                designatedBounds.getY()+getHeight()/2.0);
		resizeBoundsTo(designatedBounds);
	}
	
	public void setBounds(double newX, double newY, double newWidth, double newHeight)
	{
		super.getLocation().setLocation(newX+newWidth/2.0, newY+newWidth/2.0);
		boundingRectangle.setRect(-newWidth/2.0, -newHeight/2.0, newWidth, newHeight);
	}

	// public void drawBefore(Graphics2D graphics, ImageObserver observer, boolean arrange, Selection selected, Transmogrification transmogrification)
	public void drawBefore(DrawingContext drawingContext)
	{
		// if selected, draw a blue rectangle outline
		Selection selected = drawingContext.getSelected();
		
		if (selected!=null && selected.isSelected( (Drawable) this)) // TODO avoid this cast
		{
			drawBefore(drawingContext, getResizeableBounds2D());
		}
	}
	
	public static void drawBefore(DrawingContext drawingContext, Rectangle2D resizeableBounds)
	{
		final Color ORIGINAL_COLOR = drawingContext.getColor();
		drawingContext.setColor(Resizeable.TRANSLUCENT_BLUE);
		drawingContext.fill(resizeableBounds);
		drawingContext.setColor(ORIGINAL_COLOR);
	}
	
	public void drawAfter(DrawingContext drawingContext)
	{
		Selection selected = drawingContext.getSelected();
		// if selected, indicate with translucent blue corner dots
		
		if (selected!=null && selected.isSelected( (Drawable) this)) // TODO avoid this cast
		{
			Rectangle2D rect = getResizeableBounds2D();
			drawAfter(drawingContext, rect);
		}
	}

	/**
	 * Paints selection hoogies for selected items.
	 */
	public static void drawAfter(DrawingContext drawingContext, Rectangle2D rect)
	{
		Color ORIGINAL_COLOR = drawingContext.getColor();
		drawingContext.setColor(Resizeable.TRANSLUCENT_BLUE);
		drawingContext.fillOval((int)rect.getX()-3, (int)rect.getY()-3, 6, 6);
		drawingContext.fillOval((int)(rect.getX()+rect.getWidth()-4), (int)rect.getY()-3, 6, 6);
		drawingContext.fillOval((int)(rect.getX()+rect.getWidth()-4), (int)(rect.getY()+rect.getHeight()-4), 6, 6);
		drawingContext.fillOval((int)(rect.getX()-3), (int)(rect.getY()+rect.getHeight()-4), 6, 6);
		
		drawingContext.setColor(ORIGINAL_COLOR);
	}
	
	public void extractAttributesAndSubElements(Object parent, Element element)
	{
		super.extractAttributesAndSubElements(parent, element);
		
		if (element.hasAttribute(IDENTIFIER_WIDTH_IN_POINTS))
			setWidth(Double.parseDouble(element.getAttribute(IDENTIFIER_WIDTH_IN_POINTS)));
		
		if (element.hasAttribute(IDENTIFIER_HEIGHT_IN_POINTS))
			setHeight(Double.parseDouble(element.getAttribute(IDENTIFIER_HEIGHT_IN_POINTS)));
	}
	
	public void applyAttributesAndSubElements(Document doc, Element element)
	{
		super.applyAttributesAndSubElements(doc, element);
		
		if (getWidth()>0.0)
		{
			element.setAttribute(IDENTIFIER_WIDTH_IN_POINTS, String.valueOf(getWidth()));
		}
		
		if (getHeight()>0.0)
		{
			element.setAttribute(IDENTIFIER_HEIGHT_IN_POINTS, String.valueOf(getHeight()));
		}
	}
	
	public Rectangle2D getPreferredBounds()
	{
		Rectangle2D theRect = new Rectangle2D.Double();
		theRect.setRect(getLocation().getX() - boundingRectangle.getWidth()/2.0, getLocation().getY() - boundingRectangle.getHeight()/2.0, 
                boundingRectangle.getWidth(), boundingRectangle.getHeight());
		return theRect;
	}
	
	public void paintSelection(DrawingContext drawingContext)
	{
		drawingContext.plotCornerDots(getPreferredBounds(), TRANSLUCENT_BLUE, DOT_DIAMETER, true);
	}
	
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selection)
	{
		if (isSelected(selection))
			paintSelection(drawingContext);
	}
	
	/**
	 * Evaluates whether the two provided resizeables prefer the same boundaries. For
	 * abstract resizeables, this would essential make them "equal".
	 */
	public static boolean samePreferredBounds(Resizeable resizeable1, Resizeable resizeable2)
	{
		if (resizeable1==resizeable2)
			return true;
		
		if (resizeable1==null || resizeable2==null)
			return false;
		
		Rectangle2D prefBounds1 = resizeable1.getPreferredBounds();
		Rectangle2D prefBounds2 = resizeable2.getPreferredBounds();
		
		if (prefBounds1==null || prefBounds2==null)
			return false;
		
		return (prefBounds1.equals(prefBounds2));
	}
	
	public boolean samePreferredBounds(Resizeable otherResizeable)
	{
		return samePreferredBounds(this, otherResizeable);
	}
	
	/**
	 * If necessary, scales the provided resizeable so that it fits within the
	 * bounds provided.
	 *
	 * @param lazy if true, allows one of the dimension to overrun its boundaries
	 */
	public static void fit(Resizeable designatedResizeable, Rectangle2D bounds, boolean lazy)
	{
		if (designatedResizeable==null || bounds==null)
			return;
		
		double widthFactor = designatedResizeable.getWidth()/bounds.getWidth();
		double heightFactor = designatedResizeable.getHeight()/bounds.getHeight();
		
		if (heightFactor<=1.0 && heightFactor<=1.0)
			return;
		
		double chosenFactor = 0.0;
		
		if (widthFactor>heightFactor)
		{
			if (lazy)
				chosenFactor = heightFactor;
			else
				chosenFactor = widthFactor;
		}
		else
		{
			if (lazy)
				chosenFactor = widthFactor;
			else
				chosenFactor = heightFactor;
		}
		
		designatedResizeable.setWidth(designatedResizeable.getWidth()/chosenFactor);
		designatedResizeable.setHeight(designatedResizeable.getHeight()/chosenFactor);
	}
}
