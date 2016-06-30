/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout.average;

import com.smithandtinkers.geom.SuperEllipsePerch;
import com.smithandtinkers.layout.Averager;


/**
 * @author dhorlick
 */
public class HeinParameterAverager extends Averager
{
	public HeinParameterAverager()
	{
		super(SuperEllipsePerch.class);
	}

	public double tally(Object object)
	{
		SuperEllipsePerch asPerch = (SuperEllipsePerch) object;
		return asPerch.getHeinParameter();
	}
}
