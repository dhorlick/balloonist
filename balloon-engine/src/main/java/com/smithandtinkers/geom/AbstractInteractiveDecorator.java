/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.*;

public class AbstractInteractiveDecorator implements Interactive
{
	protected Interactive interactive;
	
	public AbstractInteractiveDecorator(Interactive designatedInteractive)
	{
		setInteractive(designatedInteractive);
	}
	
	protected void setInteractive(Interactive designatedInteractive)
	{
		interactive = designatedInteractive;
	}

	public Interactive getInteractive()
	{
		return interactive;
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		interactive.reshape(oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getLocation()
	 */
	public Point2D getLocation()
	{
		return interactive.getLocation();
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#setLocation(java.awt.geom.Point2D)
	 */
	public void setLocation(Point2D location)
	{
		interactive.setLocation(location);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#translate(double, double)
	 */
	public void translate(double dx, double dy)
	{
		interactive.translate(dx, dy);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		return interactive.getResizeableBounds2D();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		return interactive.resize(designatedSide, oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getPreferredBounds()
	 */
	public Rectangle2D getPreferredBounds()
	{
		return interactive.getPreferredBounds();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return interactive.getWidth();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return interactive.getHeight();
	}

	public void draw(DrawingContext drawingContext)
	{
		interactive.draw(drawingContext);
	}

	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		interactive.save(doc, parent, archiveContext);
	}

	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		interactive.open(parent, node, archiveContext);
	}

	public Collection getParent()
	{
		return interactive.getParent();
	}

	public void setParent(Collection designatedParent)
	{
		interactive.setParent(designatedParent);
	}

	public Object findForebear(Class requestedClass)
	{
		return interactive.findForebear(requestedClass);
	}

	public Object findProgenitor()
	{
		return interactive.findProgenitor();
	}

	public void adopted(Collection newParent)
	{
		interactive.adopted(newParent);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineAncestry()
	{
		// To reach accord between composite & decorator patterns, we need to tinker with result here
		
		Object[] determinedAncestry = interactive.determineAncestry();
		determinedAncestry[determinedAncestry.length-1]=this;
		return determinedAncestry;
	}	
	
	public void setWidth(double designatedWidth)
	{
		interactive.setWidth(designatedWidth);
	}

	public void setHeight(double designatedHeight)
	{
		interactive.setHeight(designatedHeight);
	}

	public void setLocation(double newX, double newY)
	{
		interactive.setLocation(newX, newY);
	}

	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return interactive.toShape(shapingContext, plottingContext);
	}
	
	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}
}
