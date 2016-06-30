/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.graphics.DrawingContext;


/**
 * An artwork element whose normal draw method can be augmented with additional drawing.
 * This is used to depict stems that are not rendered contiguously with a punctuated
 * perimeter.
 * 
 * Currently, this is used for bubbled and lollipop stems. Icicle stems are drawn inline
 * as part of the normal draw method, and do not use this interface.
 *
 * @author dhorlick
 */
public interface Adorned
{
	public void drawAdornments(DrawingContext drawingContext, Selection selected);
}
