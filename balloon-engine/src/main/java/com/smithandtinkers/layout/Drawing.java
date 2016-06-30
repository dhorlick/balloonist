/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.graphics.DrawingContext;

/**
 * A drawing context turned inside out
 * 
 * @author dhorlick
 */
public interface Drawing
{
	public abstract void drawOnto(DrawingContext drawingContext);
}
