/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;


/**
 * A feature that can have {@link Stem}s.
 *
 * @author dhorlick
 */
public interface Stemmed
{
	public int stemCount();
	
	public Stem getStem(int index);
	
	public double getArclength();
}
