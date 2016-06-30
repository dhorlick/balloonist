/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.average;

import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.Resizeable;


/**
 *
 * @author dhorlick
 */
public class WidthAverager extends Averager
{
	public WidthAverager()
	{
		super(Resizeable.class);
	}

	public double tally(Object object)
	{
		Resizeable resizeable = (Resizeable) object;
		return resizeable.getWidth();
	}
}
