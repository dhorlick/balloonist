/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

/**
 * @author dhorlick
 */
public interface PubliclyCloneable extends Cloneable
{
	public Object clone() throws CloneNotSupportedException;
}
