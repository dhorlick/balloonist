/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.graphics.PlottingContext;

import java.awt.Shape;
import java.awt.geom.Point2D;

import com.smithandtinkers.util.Kid;


/**
 * An artwork elements that can be optimally layed-out with respect to neighbor crowdables
 * with a {@link Crowd}.
 *
 * @author dhorlick
 */
public interface Crowdable extends Kid, Drawable
{
	public Shape preferredShape(PlottingContext plottingContext);
	public Shape preferredPerchedInnerShape(PlottingContext plottingContext);
	public Shape preferredPerchedOuterShape(PlottingContext plottingContext);
	
	public Point2D suggestCenter();
	public void arrange();
	public void setCenter(Point2D designatedCenter);
	public void setSize(float designatedSize);
	public float getSize();
}
