/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

/**
 * Something that can be oriented vertically or horizontally. This is a useful
 * distinction for internationalized text.
 *
 * @author dhorlick
 */
public interface Orientable
{
	public static String IDENTIFIER_VERTICAL = "vertical";
	
	public boolean isVertical();
	public void setVertical(boolean designatedVerticality);
}
