/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.util.Kid;

/**
 * A platform for "perching" an inner and outer shapeable around a shapeable.
 * 
 * This is used to implement margins for styled text that is fit within a complex
 * geometry.
 *
 * @author dhorlick
 */
public interface Perch extends Kid
{
	public Shapeable getInner();
	
	public Shapeable getOuter();
	
	public Shapeable getUnperched();
	
	public void setMarginal(Marginal designatedMarginal);
}
