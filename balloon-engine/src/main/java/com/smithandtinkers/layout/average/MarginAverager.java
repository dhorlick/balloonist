/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.average;

import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.layout.Averager;

/**
 *
 * @author dhorlick
 */
public class MarginAverager extends Averager
{
	public MarginAverager()
	{
		super(Marginal.class);
	}
	
	public double tally(Object object)
	{
		Marginal marginal = (Marginal) object;
		return marginal.getMarginInPoints();
	}
}
