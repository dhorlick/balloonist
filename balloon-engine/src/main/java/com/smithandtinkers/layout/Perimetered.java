/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.Point;

/**
 * Any shape that has an interrogatable perimeter.
 * 
 * @author dhorlick
 */
public interface Perimetered
{
	/**
	 * @param thePoint in outside coordinates
	 */
	public boolean onPerimeter(Point thePoint);
	
	/**
	 * @param x in outside coordinates
	 * @param y in outside coordinates
	 */
	public boolean onPerimeter(double x, double y);
}
