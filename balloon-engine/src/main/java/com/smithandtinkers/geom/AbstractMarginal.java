/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

import java.util.AbstractList;

/**
 *
 * @author dhorlick
 */
public class AbstractMarginal extends AbstractList implements Marginal
{
	public Object get(int index)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		throw new UnsupportedOperationException();
	}

	public double getOuterMarginInPoints()
	{
		return -Marginal.DEFAULT_MARGIN;
	}

	public double getInnerMarginInPoints()
	{
		return Marginal.DEFAULT_MARGIN;
	}

	public void setMarginInPoints(double designatedMargin)
	{
	}

	public double getMarginInPoints()
	{
		return Marginal.DEFAULT_MARGIN;
	}
	
	public static boolean isMarginAllowable(double prospectiveMargin)
	{
		return true;
	}
}
