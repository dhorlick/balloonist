/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Saveable;

/**
* A sort of rounded rectangle discovered by Danish polymath Piet Hein
*
* <pre>
*              n
* x(t) = r  cos t
*         x
*              n
* y(t) = r  sin t
*         y
* </pre>
 *
* @author dhorlick
*/
public interface SuperEllipse extends Shapeable, Drawable, Perimetered, Relocateable, Saveable, Resizeable, Interactive, Adorned
{
	public static final String IDENTIFIER_SUPER_ELLIPSE = "super-ellipse";
	
	/**
	 * Only available after invoking {@link #toShape}.
	 */
	public double getArclength();
	
	/**
	 * @return Returns the heinParameter.
	 */
	public double getHeinParameter();
	
	/**
	 * @param designatedHeinParameter A positive double.
	 */
	public void setHeinParameter(double designatedHeinParameter);
	
	/**
	 * @return Returns the semiMajorAxis.
	 */
	public double getSemiMajorAxis();
	
	/**
	 * @param designatedSemiMajorAxis The semiMajorAxis to set.
	 */
	public void setSemiMajorAxis(double designatedSemiMajorAxis);
	
	/**
	 * @return Returns the semiMinorAxis.
	 */
	public double getSemiMinorAxis();
	
	/**
	 * @param designatedSemiMinorAxis The semiMinorAxis to set.
	 */
	public void setSemiMinorAxis(double designatedSemiMinorAxis);
}
