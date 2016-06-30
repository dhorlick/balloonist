/**
 Copyleft by Dave Horlick
 */


package com.smithandtinkers.gui;

import java.awt.*;
import java.awt.geom.*;


/**
 * Draws shapes optimally for the hosting platform. Works around bugs & peculiarities.
 * Uses anti-aliasing when feasible.
 *
 * This class is decidedly not thread safe.
 */
public class ShapeDrawer
{
	private final static double TWICE_PI = 2.0*Math.PI;
	
	private final static double coords[] = new double [6];
	private final static Point2D point = new Point2D.Double();
	private final static Line2D.Double line = new Line2D.Double();
	private final static CubicCurve2D cubicCurve2D = new CubicCurve2D.Double();
	
	private final static double TOLERANCE = 0.35;
	
	private static boolean up;
	
	/**
	 * Draws a shape while working around deficiencies in Mac OS X's Java2D support
	 */
	public static void draw(Graphics2D g2, Shape shape)
	{
		if (shape==null)
			return;
		
		if (shape instanceof Rectangle2D
				&& (g2.getStroke()==null
				|| (g2.getStroke() instanceof BasicStroke
					&& ((BasicStroke)g2.getStroke()).getDashArray()==null)))
		{					
			// drawRect2dMacOsX(g2, (Rectangle2D)shape);
			Rectangle2D asRect2d = (Rectangle2D) shape;
			
			g2.drawRect(Math.round((int)asRect2d.getX()), (int)Math.round(asRect2d.getY()),
						Math.round((int)asRect2d.getWidth()),
						Math.round((int)asRect2d.getHeight()));
			
			// System.out.println("compensating: drawRect");
		}
		else
		{
			drawShapeSmoothly(g2, shape);
		}
	}
	
	/**
	 * Draws a Rectangle2D while mostly working around OS X's off-by-1/2 pixel problem
	 * 
	 * Still off by about 1/10 mm when printing.
	 */
	private static void drawRect2dMacOsX(Graphics2D g2, Rectangle2D rect)
	{
		Rectangle2D offset = new Rectangle2D.Double(
				rect.getX()-0.5, rect.getY()-0.5,
				rect.getWidth(), rect.getHeight());

		g2.draw(offset);
	}
	
	private static void drawShapeSmoothly(Graphics2D g2, Shape shape)
	{
		Shape stroked = g2.getStroke().createStrokedShape(shape);
		// ^^ probably slower, but it avoids gaps on Mac OS X, and
		//    weird, line-like joints on Windows XP
		
		g2.fill(stroked);
	}
	
	/**
	 * Clips a shape while working around deficiencies in Mac OS X's Java2D support
	 */
	public static void clip(Graphics2D g2, Shape shape)
	{
		if (shape==null || g2==null)
			return;
		
		if (shape instanceof Rectangle2D)
		{
			// need to workaround the fiendish off by 0.5 bug
			
			Rectangle2D asRect2d = (Rectangle2D) shape;
			
			g2.clipRect(Math.round((int)asRect2d.getX()), (int)Math.round(asRect2d.getY()),
					(int)Math.round(asRect2d.getWidth()),
					(int)Math.round(asRect2d.getHeight()));
			
			// System.out.println("compensating: clipRect");
		}
		else
		{
			g2.clip(shape);
		}
	}
	
	public static void translate(Graphics2D g2, double dx, double dy)
	{
		if (g2==null)
			return;
		
		g2.translate((int)dx, (int)dy);
	}
}