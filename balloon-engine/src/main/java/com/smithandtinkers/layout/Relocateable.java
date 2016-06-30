/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author dhorlick
 */
public interface Relocateable
{
	/**
	 * @return The center of the shape.
	 */
	public Point2D getLocation();
	
	public void setLocation(Point2D location);
	
	public void setLocation(double newX, double newY);
	
	public void translate(double dx, double dy);
	
	/**
	 * @return The portion of the relocateable that is resizable, 
	 *         or null if the entire thing is resizeable.
	 * 
	 *         Example: The round part of a word balloon is resizeable, but
	 *                  the stem(s) is/are not.
	 */
	public Rectangle2D getResizeableBounds2D();
}
