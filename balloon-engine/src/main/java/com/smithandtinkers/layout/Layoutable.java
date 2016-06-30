/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.layout;

/**
 * Something requiring enough effort to layout that it makes sense to economize.
 * Typical occasions include initial rendering and the release of the mouse after
 * pertinent actions.
 *
 * @author dhorlick
 */
public interface Layoutable
{
	public void setLayedOut(boolean designatedLayedOutedness);
	public boolean isLayedOut();
}
