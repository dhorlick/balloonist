/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;


import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.util.Saveable;


public interface Drawable extends Saveable, Selectable
{
	/**
	 Renders this artwork element as vector art on the drawing context.
	 */
	public void draw(DrawingContext drawingContext);
}
