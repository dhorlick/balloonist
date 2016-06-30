/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.svg;

import java.awt.Shape;

import com.smithandtinkers.util.TypesafeList;

/**
 * @author dhorlick
 */
public class ShapeList extends TypesafeList
{
	public ShapeList()
	{
		super(Shape.class);
	}

}
