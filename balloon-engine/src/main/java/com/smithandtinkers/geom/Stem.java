/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.layout.Selectable;
import com.smithandtinkers.util.Saveable;

/**
 * Models the pointers emanating from balloon perimeters. Stems attribute dialogue to one or more characters.
 * Some people use the term "tails" to describe this feature.
 *
 * @author dhorlick
 */
public interface Stem extends Saveable, Selectable
{
	public Point2D.Double getFocus();
	
	/**
	 * @return a number between 0.0 and 1.0, exclusive.
	 */
	public double getLeadingEdgePositionAsPerimeterFraction();
	
	/**
	 * @param designatedLeadingEdgePositionAsSideFraction a number between 0.0 and 1.0, exclusive.
	 */
	public void setLeadingEdgePositionAsSideFraction(double designatedLeadingEdgePositionAsSideFraction);
	
	public double getRootWidthInPoints();
	
	/**
	 * Optional.
	 */
	public Stemmed getParentStemmed();
	public double getLeadingEdgeX();
	public double getLeadingEdgeY();
	public double getTrailingEdgeX();
	public double getTrailingEdgeY();
	
	public boolean contains(double x, double y);
	
	/**
	 * @param designatedWidth {@link com.smithandtinkers.geom.AbstractStem#maxAllowableRootWidthInPoints}
	 */
	public void setRootWidthInPoints(double designatedWidth);

	public void setFocus(Point2D.Double designatedFocus);
	
	public void stampSegment(GeneralPath gp, PlottingContext guideSlate);
}
