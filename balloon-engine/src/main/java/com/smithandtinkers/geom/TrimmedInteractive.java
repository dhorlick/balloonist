/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.PlottingContext;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.smithandtinkers.layout.Interactive;

public abstract class TrimmedInteractive extends AbstractInteractiveDecorator
{
	private boolean inner = true;
	
	public TrimmedInteractive(Interactive designatedInteractive, Marginal designatedMarginal, boolean designatedInner)
	{
		super(designatedInteractive);
		setMarginal(designatedMarginal);
		setInner(designatedInner);
	}

	public double getTrimInPoints()
	{
		if (inner)
			return getMarginal().getInnerMarginInPoints();
		else
			return getMarginal().getOuterMarginInPoints();
	}
	
	public Rectangle2D getResizeableBounds2D()
	{
		// System.out.println("getResizeableBounds2D: trimming");
		Rectangle2D bounds = super.getResizeableBounds2D();
		bounds.setRect(bounds.getX()+getTrimInPoints(), bounds.getY()+getTrimInPoints(), bounds.getWidth()-2.0*getTrimInPoints(), bounds.getHeight()-2.0*getTrimInPoints());
		return bounds;
	}
	
	public Rectangle2D getPreferredBounds()
	{
		// System.out.println("getPreferredBounds: trimming");
		Rectangle2D bounds = super.getPreferredBounds();
		// System.out.println("before bounds="+bounds);
		// System.out.println("bounds hash code="+System.identityHashCode(bounds));
		bounds.setRect(bounds.getX()+getTrimInPoints(), bounds.getY()+getTrimInPoints(), bounds.getWidth()-2.0*getTrimInPoints(), bounds.getHeight()-2.0*getTrimInPoints());
		// System.out.println("after bounds="+bounds);
		return bounds;
	}
	
	public double getWidth()
	{
		return super.getWidth()-2.0*getTrimInPoints();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return super.getHeight()-2.0*getTrimInPoints();
	}
	
	public Point2D getLocation()
	{
		Point2D returnable = super.getLocation();
		returnable.setLocation(returnable.getX()+getTrimInPoints(), returnable.getY()+getTrimInPoints());
		return returnable;
	}

	public void setMarginal(Marginal designatedMarginal)
	{
		setParent(designatedMarginal);
	}
	
	public Marginal getMarginal()
	{
		return (Marginal) getParent();
	}
	
	public boolean isInner()
	{
		return inner;
	}

	public void setInner(boolean designatedInner)
	{
		inner = designatedInner;
	}
	
	public abstract Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext);
}