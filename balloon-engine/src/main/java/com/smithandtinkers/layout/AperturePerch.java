/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import com.smithandtinkers.geom.Marginal;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.geom.Shapeable;
import com.smithandtinkers.geom.ShapeableShapeAdaptor;

/**
 * @author dhorlick
 */
public class AperturePerch extends Aperture implements Perch
{
	private Shapeable inner;
	private Shapeable outer;
	private Shapeable unperched;
	
	private Marginal parent;
	
	public AperturePerch(Marginal designatedMarginal)
	{
		setMarginal(designatedMarginal);
	}
	
	/**
	 * @see com.smithandtinkers.geom.Perch#getInner()
	 */
	public Shapeable getInner()
	{
		return inner;
	}

	/**
	 * @see com.smithandtinkers.geom.Perch#getOuter()
	 */
	public Shapeable getOuter()
	{
		return outer;
	}

	/**
	 * @see com.smithandtinkers.geom.Perch#getUnperched()
	 */
	public Shapeable getUnperched()
	{
		return unperched;
	}

	/**
	 * @see com.smithandtinkers.util.Kid#getParent()
	 */
	public Collection getParent()
	{
		return parent;
	}

	/**
	 * @see com.smithandtinkers.util.Kid#setParent(java.util.Collection)
	 */
	public void setParent(Collection designatedParent)
	{
		parent = (Marginal) designatedParent;
		if (parent!=null)
			updateTrim();
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findForebear(java.lang.Class)
	 */
	public Object findForebear(Class requestedClass)
	{
		return null;
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findProgenitor()
	 */
	public Object findProgenitor()
	{
		return null;
	}

	/**
	 * @see com.smithandtinkers.util.Kid#adopted(java.util.Collection)
	 */
	public void adopted(Collection newParent)
	{
	}

	/**
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineAncestry()
	{
		return null;
	}
	
	/**
	 * @see com.smithandtinkers.layout.Aperture#setShape(java.awt.Shape)
	 * @param designatedShape a Rectangle2D instance
	 * @throws IllegalArgumentException if designatedShape is not a Rectangle2D
	 */
	public void setShape(Shape designatedShape)
	{
		if (designatedShape instanceof Rectangle2D)
		{
			setRect((Rectangle2D) designatedShape);
		}
		else
		{
			throw new IllegalArgumentException("Only rectangles are currently supported.");
		}
	}
	
	public void setRect(Rectangle2D designatedRect)
	{
		super.setShape(designatedRect);
		
		updateTrim();
		unperched = new ShapeableShapeAdaptor(designatedRect);
	}

	public static Rectangle2D.Double trim(Rectangle2D designatedRect, double amountInPoints)
	{
		return new Rectangle2D.Double(designatedRect.getX()+amountInPoints, designatedRect.getY()+amountInPoints, designatedRect.getWidth()-2.0*amountInPoints, designatedRect.getHeight()-2.0*amountInPoints);
	}
	
	/**
	 * @param designatedRect
	 */
	private void setOuter(Rectangle2D designatedRect)
	{
		outer = new ShapeableShapeAdaptor(designatedRect);
		// System.out.println("set outer to "+outer);
	}

	private void setInner(Rectangle2D designatedRect)
	{
		inner = new ShapeableShapeAdaptor(designatedRect);
	}

	public void setMarginal(Marginal designatedMarginal)
	{
		setParent(designatedMarginal);
		if (designatedMarginal != null)
			updateTrim();
	}
	
	public Marginal getMarginal()
	{
		return parent;
	}
	
	public double getTrimInPoints()
	{
		return parent.getMarginInPoints();
	}
	
	public void updateTrim()
	{
		Rectangle2D rect = getRect();
		
		if (rect!=null && parent!=null)
		{
			setInner(trim(rect, getTrimInPoints()));
			setOuter(trim(rect, -1.0*getTrimInPoints()));
		}
	}
	
	public Rectangle2D getRect()
	{
		Shape shape = getShape();
		if (shape==null || !(shape instanceof Rectangle2D))
			return null;
				
		return (Rectangle2D) shape;
	}

	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		boolean result;
		result = super.resize(designatedSide, oldX, oldY, newX, newY);
		
		updateTrim();
		
		return result;
	}
	
	public void setWidth(double designatedWidth)
	{
		super.setWidth(designatedWidth);
		
		updateTrim();
	}
	
	public void setHeight(double designatedHeight)
	{
		super.setHeight(designatedHeight);
		
		updateTrim();
	}
}
