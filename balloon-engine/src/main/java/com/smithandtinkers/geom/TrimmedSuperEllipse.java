/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


import com.smithandtinkers.layout.PerimeterSegment;

/**
 * @author dhorlick
 */
public class TrimmedSuperEllipse extends AbstractSuperEllipse implements SuperEllipse // TODO might be better if this didn't extend AbstractSuperEllipse
{
	private SuperEllipse superEllipse;
	private boolean inner = true;
	
	public TrimmedSuperEllipse()
	{
	}
	
	public TrimmedSuperEllipse(SuperEllipse designatedSuperEllipse, Marginal designatedMarginal, boolean designatedInner)
	{
		setSuperEllipse(designatedSuperEllipse);
		setMarginal(designatedMarginal);
		setInner(designatedInner);
	}
	
	public void setSuperEllipse(SuperEllipse designatedSuperEllipse)
	{
		superEllipse = designatedSuperEllipse;
	}

	public SuperEllipse getSuperEllipse()
	{
		return superEllipse;
	}
	
	public double getTrimInPoints()
	{
		if (inner)
			return getMarginal().getInnerMarginInPoints();
		else
			return getMarginal().getOuterMarginInPoints();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getHeinParameter()
	 */
	public double getHeinParameter()
	{
		return superEllipse.getHeinParameter();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMajorAxis()
	 */
	public double getSemiMajorAxis()
	{
		return superEllipse.getSemiMajorAxis() - getTrimInPoints();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMajorAxis(double)
	 */
	public void setSemiMajorAxis(double designatedSemiMajorAxis)
	{
		superEllipse.setSemiMajorAxis(designatedSemiMajorAxis + getTrimInPoints());
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMinorAxis()
	 */
	public double getSemiMinorAxis()
	{
		return superEllipse.getSemiMinorAxis() - getTrimInPoints();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMinorAxis(double)
	 */
	public void setSemiMinorAxis(double designatedSemiMinorAxis)
	{
//		setSemiMinorAxis(designatedSemiMinorAxis + getTrimInPoints());		
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		superEllipse.reshape(oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getLocation()
	 */
	public Point2D getLocation()
	{
		return superEllipse.getLocation();
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#setLocation(java.awt.geom.Point2D)
	 */
	public void setLocation(Point2D location)
	{
		superEllipse.setLocation(location);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#translate(double, double)
	 */
	public void translate(double dx, double dy)
	{
		superEllipse.translate(dx, dy);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		return superEllipse.getResizeableBounds2D();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, int oldX, int oldY, int newX, int newY)
	{
		return superEllipse.resize(designatedSide, oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getPreferredBounds()
	 */
	public Rectangle2D getPreferredBounds()
	{
		return superEllipse.getPreferredBounds();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return superEllipse.getWidth();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return superEllipse.getWidth();
	}

	public boolean isInner()
	{
		return inner;
	}

	public void setInner(boolean designatedInner)
	{
		inner = designatedInner;
	}
	
	public void setMarginal(Marginal desingnatedMarginal)
	{
		setParent(desingnatedMarginal);
	}
	
	public Marginal getMarginal()
	{
		return (Marginal) getParent();
	}
	
	public void setRuffled(boolean designatedRuffled) // TODO get rid of this
	{
	}
	
	public final boolean isRuffled()
	{
		return false;
	}
	
	public int determineRuffleQuantity(double designatedArcLength)
	{
		return 0;
	}

	public float pickAppropriateRuffleMagnification(int index) // TODO ditch
	{
		return 1.0f;
	}
}
