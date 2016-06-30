/**
 Copyleft Jul 18, 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 * @author dhorlick
 */
public interface Resizeable extends Relocateable
{
	public final static Color TRANSLUCENT_BLUE = new Color(0.2f,0.2f,1.0f,.5f);
	public final static int DOT_DIAMETER = 6;
    
    /**
     * A Rectangle in local Graphics2D coordinates. Typically not zero-centered.
     */
	public Rectangle2D getPreferredBounds();
	
	/**
	 * @return The actual boundary, minus stems, etc.
	 */
	public Rectangle2D getResizeableBounds2D(); // TODO make sure this isn't redundant to getPreferredBounds, now that Crowd does most of its own work
	
	/**
	 * Resize shape such that the designated side becomes located at the appropriate designated coordinate(s).
	 * 
	 * @return whether or not the operation was successful.
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY);

	/**
	 * @param designatedWidth The new preferred width.
	 */
	public void setWidth(double designatedWidth);
	
	/**
	 * @param designatedHeight The new preferred height.
	 */
	public void setHeight(double designatedHeight);

	public double getWidth();
	
	public double getHeight();
}
