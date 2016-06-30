/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.graphics.DrawingContext;


/**
 * @author dhorlick
 */
public interface VisiblySelectable extends Selectable
{
	/**
	 * Will visibly demonstrate that this item has been selected.
	 *
	 * {@link com.smithandtinkers.layout.Drawable#draw} should either delegate to this,
	 * or perform functionally equivalent work.
	 * 
	 * It should only be necessary to call this method in cases where
	 * {@link com.smithandtinkers.layout.Drawable#draw} is not called on this item and
	 * because a higher-level item is responsible for drawing it. An example of this is
	 * when the item is Shapeable and has its boundaries are determine from the outside
	 * by intersecting it with other Shapeables.
	 */
	public void paintSelection(DrawingContext drawingContext);
	
	/**
	 * If this method is not drawn (maybe because it isn't Drawable) this method
	 * will invoke {@link #paintSelection}
	 *
	 * In either case, it will then delegate to paintSelectionIfAppropriate on
	 * its children.
	 */
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selection);
}
