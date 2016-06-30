/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.PlottingContext;

import java.awt.Shape;


/**
 * Something that can be represented as a {@link java.awt.Shape}.
 * 
 * @author dhorlick
 */
public interface Shapeable
{
	/**
	 * @param shapingContext An object with which the creation of the shape can be
	 * coordinated in a target-agnostic manner.
	 * @param guideSlate An optional object with which plots can be coordinated
	 * in a target-agnostic manner. This is useful for displaying guide dots when
	 * objects are selected.
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext guideSlate);
	
	public void reshape(double oldX, double oldY, double newX, double newY);
}