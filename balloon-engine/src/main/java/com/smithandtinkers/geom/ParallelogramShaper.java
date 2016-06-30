/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * A bounds-based strategy for producing parallelogram shapes.
 * 
 * @author dhorlick
 */
public class ParallelogramShaper
{
	public ParallelogramShaper()
	{
	}
	
	public GeneralPath shapen(Rectangle2D bounds, double inset)
	{
		GeneralPath gpath = new GeneralPath();
		
		if (inset>=0)
		{
			gpath.moveTo((float)bounds.getX()+(float)inset, (float)bounds.getY());
			gpath.lineTo((float)bounds.getX(), (float)bounds.getY()+(float)bounds.getHeight());
			gpath.lineTo((float)bounds.getX()+(float)bounds.getWidth()-(float)inset, (float)bounds.getY()+(float)bounds.getHeight());
			gpath.lineTo((float)bounds.getX()+(float)bounds.getWidth(), (float)bounds.getY());
		}
		else
		{
			gpath.moveTo((float)bounds.getX(), (float)bounds.getY());
			gpath.lineTo((float)bounds.getX()-(float)inset, (float)bounds.getY()+(float)bounds.getHeight());
			gpath.lineTo((float)bounds.getX()+(float)bounds.getWidth(), (float)bounds.getY()+(float)bounds.getHeight());
			gpath.lineTo((float)bounds.getX()+(float)bounds.getWidth()+(float)inset, (float)bounds.getY());
		}
		
		gpath.closePath();
		
		return gpath;
	}
}
