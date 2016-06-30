/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.PlottingContext;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

import com.smithandtinkers.layout.Interactive;


/**
 * A parallelogram that has been trimmed. These are returned by {@link ParallelogramPerch#trim}.
 *
 * @author dhorlick
 */
public class TrimmedParallelogram extends TrimmedInteractive
{
	private ParallelogramShaper parallelogramShaper = new ParallelogramShaper();

	public TrimmedParallelogram(Parallelogram designatedParallelogram, Marginal designatedMarginal, boolean designatedInner)
	{
		super(designatedParallelogram, designatedMarginal, designatedInner);
	}
	
	/**
	 * @param designatedInteractive must be a Parallelogram here
	 * 
	 * @see com.smithandtinkers.geom.AbstractInteractiveDecorator#setInteractive(com.smithandtinkers.layout.Interactive)
	 */
	protected void setInteractive(Interactive designatedInteractive)
	{
		if (designatedInteractive instanceof Parallelogram)
		{
			super.setInteractive(designatedInteractive);
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	public Parallelogram getParallelogram()
	{
		return (Parallelogram) getInteractive();
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		Rectangle2D preferredBounds = getPreferredBounds();
		double trimmedInset = preferredBounds.getHeight() / getParallelogram().computeSlope();
		return parallelogramShaper.shapen(preferredBounds, trimmedInset);
	}
	
	public Rectangle2D getPreferredBounds()
	{
		// System.out.println("pb "+getTrimInPoints());
		
		Rectangle2D bounds = interactive.getPreferredBounds();
		double slope = getParallelogram().computeSlope();
		// double theta = Math.atan(slope);
		double adjustmentFactor = (1.0 + 1.0/slope);
		bounds.setRect(bounds.getX()+getTrimInPoints()*adjustmentFactor, bounds.getY()+getTrimInPoints(), bounds.getWidth()-2.0*getTrimInPoints()*adjustmentFactor, bounds.getHeight()-2.0*getTrimInPoints());
		return bounds;
	}
	
	public Rectangle2D getResizeableBounds2D()
	{
		Rectangle2D bounds = interactive.getResizeableBounds2D();
		double slope = getParallelogram().computeSlope();
		// double theta = Math.atan(slope);
		double adjustmentFactor = (1.0 + 1.0/slope);
		bounds.setRect(bounds.getX()+getTrimInPoints()*adjustmentFactor, bounds.getY()+getTrimInPoints(), bounds.getWidth()-2.0*getTrimInPoints()*adjustmentFactor, bounds.getHeight()-2.0*getTrimInPoints());
		return bounds;
	}
}
