/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout.average;

import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.Thick;


/**
 *
 * @author dhorlick
 */
public class LineThicknessAverager extends Averager
{
	public LineThicknessAverager()
	{
		super(Thick.class);
	}

	public double tally(Object object)
	{
		Thick thick = (Thick) object;
		return thick.getLineThickness();
	}
}
