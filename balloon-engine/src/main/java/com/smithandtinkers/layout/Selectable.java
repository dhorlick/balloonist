/*
 *  Copyleft May 8, 2005 by Dave Horlick
 */
package com.smithandtinkers.layout;

import com.smithandtinkers.util.Kid;

/**
 * @author dhorlick
 */
public interface Selectable extends Kid
{
	/**
	 * Determines, conceptually, whether this item is selected. Generally this is as simple
	 * as scanning the provided selection classes. However, in some circumstances where
	 * decorators are used this can be more complicated.
	 */
	public boolean isSelected(Selection selection);
}
