/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

import java.util.List;

/**
 * A container with inner and outer margins.
 *
 * @author dhorlick
 */
public interface Marginal extends List
{
	public static final String IDENTIFIER_INNER_MARGIN = "inner-margin";
	public static final String IDENTIFIER_OUTER_MARGIN = "outer-margin";
	
	public final static double DEFAULT_MARGIN = 10.0;
	
	public final static double MAXIMUM_ALLOWABLE_MARGIN = 30.0;
	
	/**
	 * Generally positive.
	 */
	public double getMarginInPoints();
	
	/**
	 * Generally positive.
	 */
	public double getInnerMarginInPoints();
	
	/**
	 * Generally negative.
	 */
	public double getOuterMarginInPoints();
	
	/**
	 * Generally positive.
	 */
	public void setMarginInPoints(double designatedMargin);
}
