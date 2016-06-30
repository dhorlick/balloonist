/*
 * PlottingCotext.java
 *
 * Created on December 10, 2006, 9:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.smithandtinkers.graphics;

import com.smithandtinkers.layout.Selection;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * An interface thru which stuff can be plotted.
 *
 * This is useful for depicting selected artwork elements in a GUI.
 *
 * @author dhorlick
 */
public interface PlottingContext
{
	public void plotCornerDots(Rectangle2D rectangle2D, Color designatedColor, int dotDiameter, boolean b);

	public void plotDot(int x, int y, Color designatedColor, int designatedSize, boolean fill);
	
	public void plotDot(double x, double y, Color color, double diameter, boolean fill);
	
	public void drawLine(int x1, int y1, int x2, int y2);

	public void drawLine(double x1, double y1, double x2, double y2, Color color);
	
	public void drawFilled(Shape designatedShape, Color designatedOutlineColor, Color designatedFillColor);
	
	public Stroke getStroke();

	public void setStroke(Stroke designatedStroke);
	
	public Selection getSelected();
}
