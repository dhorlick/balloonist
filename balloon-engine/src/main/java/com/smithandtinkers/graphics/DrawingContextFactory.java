/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.graphics;

/**
 * Provides a {@link com.smithandtinkers.graphics.DrawingContext} appropriate to the graphics environment of
 * your platform.
 *
 * (At the present time, this can only be Java Graphics2D. So this class isn't really all that
 * useful.)
 *
 * @author dhorlick
 */
public abstract class DrawingContextFactory
{
	public static DrawingContext getDrawingContext()
	{
		// TODO use Class.forName() instead?
		
		return new com.smithandtinkers.graphics.awt.DefaultDrawingContext();
	}
}
