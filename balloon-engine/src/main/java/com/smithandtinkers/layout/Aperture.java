/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.geom.ShapingContext;
import com.smithandtinkers.graphics.DrawingFilter;
import com.smithandtinkers.graphics.PlottingContext;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.io.Serializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.geom.Shapeable;
import com.smithandtinkers.geom.ShapeFriend;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.svg.ScalableVectorGraphicsTranslator;
import com.smithandtinkers.svg.ShapeList;
import com.smithandtinkers.util.*;

/**
 * Something thru which clipped artwork elements can be seen. Can optionally have a border
 * or background color.
 *
 * @author dhorlick
 */
public class Aperture implements Serializable, Saveable, Shapeable, Colorful, Resizeable
{
	private static final String IDENTIFIER_FILL_COLOR = "fill-color";
	private static final String IDENTIFIER_OUTLINE_COLOR = "outline-color";
	public static final String IDENTIFIER_APERTURE = "aperture";
	private static final String IDENTIFIER_SHAPE = "shape";
	
	private Color outlineColor = Color.black;
	private double borderWidthInPoints = 1.0;
	private Color fillColor = Color.white;
	private Shape shape;
	
	/**
	 * Used for tighter fit during drags.
	 */
	private Rectangle2D temporaryClippingShape;
	
	private static ScalableVectorGraphicsTranslator svgTranslator = new ScalableVectorGraphicsTranslator();
		// very not multithreaded

	public Aperture()
	{
	}
	
	public void setOutlineColor(Color designatedOutlineColor)
	{
		outlineColor = designatedOutlineColor;
	}
	
	public void setBorderWidthInPoints(double designatedBorderWidthInPoints)
	{
		borderWidthInPoints = designatedBorderWidthInPoints;
	}
	
	public void setFillColor(Color designatedFillColor)
	{
		fillColor = designatedFillColor;
	}
	
	public void setShape(Shape designatedShape)
	{
		if (shape!=designatedShape)
		{
			shape = designatedShape;
			
			refreshTemporaryClippingShape();
		}
	}
	
	public Color getOutlineColor()
	{
		return outlineColor;
	}
	
	public double getBorderWidthInPoints()
	{
		return borderWidthInPoints;
	}
	
	public Color getFillColor()
	{
		return fillColor;
	}
	
	/**
	 * A plain-vanilla shape getter. Does no heavy-lifting.
	 * 
	 * @return the internal shape representation.
	 */
	public Shape getShape()
	{
		return shape;
	}

	public Rectangle2D enclose()
	{
		if (shape==null)
			return null;
		
		if (shape instanceof Rectangle2D)
			return (Rectangle2D) shape;
				
		return shape.getBounds2D();
	}
	
	public void draw(DrawingContext drawingContext)
	{
		drawBack(drawingContext);
		drawFront(drawingContext);
	}
	
	public void drawFront(DrawingContext drawingContext)
	{
		if (Logger.isOn())
			Logger.println("shape="+shape);
		
		if (shape!=null && outlineColor!=null)
		{
			final Color oldColor = drawingContext.getColor();
		
			drawingContext.setColor(outlineColor);
			drawingContext.draw(shape);
			
			drawingContext.setColor(oldColor);
		}
	}
	
	public void drawBack(DrawingContext drawingContext)
	{
		if (Logger.isOn())
			Logger.println("shape="+shape);
		
		if (shape!=null && fillColor!=null)
		{
			final Color oldColor = drawingContext.getColor();
		
			drawingContext.setColor(fillColor);
			drawingContext.fill(shape);
			
			drawingContext.setColor(oldColor);
		}
	}
	
	public void enforceClippingBoundary(DrawingContext designatedDrawingContext)
	{
		if (designatedDrawingContext.getDrawingFilter().getFilterState()==DrawingFilter.RETAIN
				&& shape instanceof Rectangle2D)
			designatedDrawingContext.clip(temporaryClippingShape);
		else
			designatedDrawingContext.clip(shape);
	}
	
	/* (non-Javadoc)
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element apertureElement = doc.createElement(IDENTIFIER_APERTURE);
		
		if (outlineColor!=null)
			apertureElement.setAttribute(IDENTIFIER_OUTLINE_COLOR, ShapeFriend.describe(outlineColor));
		
		if (fillColor!=null)
			apertureElement.setAttribute(IDENTIFIER_FILL_COLOR, ShapeFriend.describe(fillColor));
		
		if (shape!=null)
		{
			Element shapeElement = doc.createElement(IDENTIFIER_SHAPE);
			apertureElement.appendChild(shapeElement);
			svgTranslator.shapeToSvg(doc, shapeElement, shape);
		}
		
		parent.appendChild(apertureElement);
	}
	
	public void open(Object parent, Node node, ArchiveContext archiveContext)
			throws NumberFormatException
	{
		Element element = (Element) node;
		
		if (element.hasAttribute(IDENTIFIER_OUTLINE_COLOR))
			setOutlineColor(ShapeFriend.parseColor(element.getAttribute(IDENTIFIER_OUTLINE_COLOR)));
		else
			setOutlineColor(null);
		
		if (element.hasAttribute(IDENTIFIER_FILL_COLOR))
			setFillColor(ShapeFriend.parseColor(element.getAttribute(IDENTIFIER_FILL_COLOR)));
		else
			setFillColor(null);
		
		Node shapeNode = XmlFriend.excavateImmediateSubnode(element, IDENTIFIER_SHAPE);
		if (shapeNode!=null)
		{
			try
			{
				ShapeList shapeList = svgTranslator.svgToShape(shapeNode);
				if (shapeList.size()>0)
					setShape((Shape)shapeList.get(0));
			}
			catch (StructureException e)
			{
				throw new BugException(e);
			}
		}
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Aperture {"
			 + "outlineColor = " + outlineColor + ", "
			 + "borderWidthInPoints = " + borderWidthInPoints + ", "
			 + "fillColor = " + fillColor + ", "
			 + "shape = " + shape
		+ "}";
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return shape;
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
	}

	public void resizeBoundsTo(Rectangle2D rect)
	{
	}

	public void setWidth(double designatedWidth)
	{
		if (shape instanceof Rectangle2D)
		{
			Rectangle2D rect = (Rectangle2D) shape;
			
			if (designatedWidth!=rect.getWidth())
			{
				rect.setRect(rect.getX(), rect.getY(), designatedWidth, rect.getHeight());
			}
		}
		else
		{
			// TODO ...
		}
		
		refreshTemporaryClippingShape();
	}

	public void setHeight(double designatedHeight)
	{
		if (shape instanceof Rectangle2D)
		{
			Rectangle2D rect = (Rectangle2D) shape;
			
			if (designatedHeight!=rect.getHeight())
			{
				rect.setRect(rect.getX(), rect.getY(), rect.getWidth(), designatedHeight);
			}
		}
		else
		{
			// TODO ...
		}
		
		refreshTemporaryClippingShape();
	}

	public void setLocation(Point2D location)
	{
		// TODO ...
	}

	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		// System.out.println("resize: designatedSide="+designatedSide.getClass().getName()+ ", shape="+shape.getClass().getName());
		
		if (!(shape instanceof Rectangle2D))
			return false;
		
		Rectangle2D rect = getResizeableBounds2D();
		
		if (designatedSide==PerimeterSegment.TOP_SIDE)
		{			
			// ShapeFriend.setY(rect, rect.getY()+newY-oldY);
			ShapeFriend.setHeight(rect, rect.getHeight()+oldY-newY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_SIDE)
		{
			ShapeFriend.setHeight(rect, rect.getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.LEFT_SIDE)
		{
			// ShapeFriend.setX(rect, rect.getX()+newX-oldX);
			ShapeFriend.setWidth(rect, rect.getWidth()-newX+oldX);
		}
		else if (designatedSide==PerimeterSegment.RIGHT_SIDE)
		{
			ShapeFriend.setWidth(rect, rect.getWidth()+newX-oldX);
		}
		else if (designatedSide==PerimeterSegment.TOP_LEFT_CORNER)
		{			
			ShapeFriend.setWidth(rect, rect.getWidth()-newX+oldX);
			ShapeFriend.setHeight(rect, rect.getHeight()-newY+oldY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_LEFT_CORNER)
		{
			ShapeFriend.setWidth(rect, rect.getWidth()-newX+oldX);
			ShapeFriend.setHeight(rect, rect.getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_RIGHT_CORNER)
		{
			ShapeFriend.setWidth(rect, rect.getWidth()+newX-oldX);
			ShapeFriend.setHeight(rect, rect.getHeight()+newY-oldY);
		}
		else if (designatedSide==PerimeterSegment.TOP_RIGHT_CORNER)
		{
			// ShapeFriend.setY(rect, rect.getY()+newY-oldY);
			ShapeFriend.setWidth(rect, rect.getWidth()+newX-oldX);
			ShapeFriend.setHeight(rect, rect.getHeight()-newY+oldY);
		}
		
		refreshTemporaryClippingShape();
		
		return true;
	}

	public void translate(double dx, double dy)
	{
	}

	public void setLocation(double newX, double newY)
	{
	}

	public double getHeight()
	{
		return getPreferredBounds().getHeight();
	}

	public java.awt.geom.Point2D getLocation()
	{
		Rectangle2D rect = getPreferredBounds();
		return new Point2D.Double(rect.getX(), rect.getY()); // TODO use center point instead?
	}

	public Rectangle2D getPreferredBounds()
	{
		Rectangle2D rect = null;
		
		Shape apertureShape = getShape();
				
		if (apertureShape instanceof Rectangle2D)
			rect = (Rectangle2D) apertureShape;
		else
			rect = apertureShape.getBounds();
		
		return rect;
	}

	public Rectangle2D getResizeableBounds2D()
	{
		return getPreferredBounds();
	}

	public double getWidth()
	{
		return getPreferredBounds().getWidth();
	}
	
	public Rectangle2D circumscribeOutline(Rectangle2D outer, Rectangle2D result)
	{
		if (result==null)
			result = new Rectangle2D.Double();
		
		result.setRect(
			outer.getX()+borderWidthInPoints, outer.getY()+borderWidthInPoints, 
			outer.getWidth()-(2.0*borderWidthInPoints), outer.getHeight()-(2.0*borderWidthInPoints));
			
		return result;
	}
	
	private void refreshTemporaryClippingShape()
	{
		if (shape instanceof Rectangle2D)
			temporaryClippingShape = circumscribeOutline((Rectangle2D)shape, temporaryClippingShape);
	}
}
