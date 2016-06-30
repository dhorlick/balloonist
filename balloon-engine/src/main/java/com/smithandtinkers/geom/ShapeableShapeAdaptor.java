/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.PlottingContext;
import java.awt.Shape;

/**
 * @author dhorlick
 */
public class ShapeableShapeAdaptor implements Shapeable
{
	private Shape shape;
	
	public ShapeableShapeAdaptor(Shape designatedShape)
	{
		setShape(designatedShape);
	}
	
	public void setShape(Shape designatedShape)
	{
		shape = designatedShape;
	}
	
	public Shape getShape()
	{
		return shape;
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return shape;
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		// TODO Auto-generated method stub
	}

}
