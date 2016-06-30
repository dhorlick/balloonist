/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.geom;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Performs useful operations on shapes and colors.
 * 
 * @author dhorlick
 */
public abstract class ShapeFriend
{
	public static class ShapeStats implements Serializable
	{
		private double area;
		private Point2D center;
		
		public double getArea()
		{
			return area;
		}
		
		public Point2D getCenter()
		{
			return center;
		}
		
		public void setArea(double designatedArea)
		{
			area = designatedArea;
		}
		
		public void setCenter(Point2D designatedCenter)
		{
			center = designatedCenter;
		}
		
		public String toString()
		{
			Map map = new HashMap();

			map.put("area", new Double(area));
			map.put("center", center);
			
			return map.toString();
		}
	}
	
	/**
	 @param dx        tile width
	 @param dy        tile height
	 @param recycled  An optional ShapeStats instance to use for a return reference.
	                  Providing this will make the method run faster.
	 @return  a ShapeFriend.ShapeStats describing the shape's center and area.
	 */
	public static ShapeStats describe(Shape designatedShape, double dx, double dy, ShapeStats recycled)
	{
		ShapeStats stats = null;
		
		if (recycled==null)
			stats = new ShapeStats();
		else
			stats = recycled;
		
		// TODO if the shape is a rectangle, none of this is really necessary
		
		Rectangle2D bounds = designatedShape.getBounds2D();
		
		//      M
		// _     y
		// x = ---
		//      M

		//      M
		// _     x
		// y = ---
		//      M


		double tileArea = dx * dy;
		double moment_x = 0.0;
		double totalArea = 0.0;	// will be equivalent to total mass
		
		for (double x = bounds.getX(); x<=bounds.getWidth() ; x+=dx )
		{
			double vertStripArea = 0.0;
			
			for (double y = bounds.getY(); y<=bounds.getHeight() ; y+=dy )
			{
				if (designatedShape.contains(x, y, dx, dy))
				{
					vertStripArea += tileArea;
				}
				
				totalArea += vertStripArea;
				moment_x += (vertStripArea * x);
				
				// System.out.print("o ");
			}

			// System.out.println("");
		}
		
		double moment_y = 0.0;
		double totalAreaAlt = 0.0;
		
		for (double y = bounds.getY(); y <= bounds.getHeight() ; y+=dy)
		{
			double horizStripArea = 0.0;
			
			for (double x = bounds.getX(); x<=bounds.getWidth() ; x+=dx )
			{
				if (designatedShape.contains(x, y, dx, dy))
				{
					horizStripArea += tileArea;
				}
				
				totalAreaAlt += horizStripArea;
					// we already have totalArea, we don't *really* need to resum it.

				moment_y += (horizStripArea * y);
			}
		}
		
		stats.setCenter(new Point2D.Double( moment_x / totalArea , moment_y / totalAreaAlt ));
		stats.setArea((totalArea+totalAreaAlt)/2.0);
		
		return stats;
	}
	
	public static ShapeStats describe(Shape designatedShape, double delta, ShapeStats recycled)
	{
		return describe(designatedShape, delta, delta, recycled);
	}
	
	/**
	 <p>Convenience method; runs slowly. Consider using pithier alternatives.</p>
	 */
	public static ShapeStats describe(Shape designatedShape, double delta)
	{
		return describe(designatedShape, delta, new ShapeStats());
	}
	
	public static double computeArea(Shape designatedShape, double dx, double dy)
	{
		if (designatedShape instanceof Rectangle2D)
		{
			return ((Rectangle2D)designatedShape).getWidth() * ((Rectangle2D)designatedShape).getHeight();
		}
		
		double tileArea = dx * dy;
		Rectangle2D bounds = designatedShape.getBounds2D();
		
		double totalArea = 0.0;	// will be equivalent to total mass
		
		for (double x = bounds.getX(); x<=bounds.getWidth() ; x+=dx )
		{
			double vertStripArea = 0.0;
			
			for (double y = bounds.getY(); y<=bounds.getHeight() ; y+=dy )
			{
				if (designatedShape.contains(x, y, dx, dy))
				{
					vertStripArea += tileArea;
				}
				
				totalArea += vertStripArea;
			}
		}
		
		return totalArea;
	}
	
	public static double computeArea(Shape designatedShape)
	{
		double deltaX = 1.0;
		double deltaY = 1.0;
		
		if (!(designatedShape instanceof Rectangle2D))
		{
			// We can't get away with a bad guess
			
			deltaX = designatedShape.getBounds2D().getWidth() / 10.0;
			deltaY = designatedShape.getBounds2D().getWidth() / 10.0;
		}
		
		return computeArea(designatedShape, deltaX, deltaY);
	}
	
	public static void plotDot(int x, int y, Color color, int diameter, Graphics g, boolean fill)
	{
		final Color OLD_COLOR = g.getColor();
		
		if (OLD_COLOR!=color)
			g.setColor(color);
		
		if (fill)
			g.fillOval(x-diameter/2, y-diameter/2, diameter, diameter);
		else
			g.drawOval(x-diameter/2, y-diameter/2, diameter, diameter);
		
		if (OLD_COLOR!=color)
			g.setColor(OLD_COLOR);
	}

	public static void plotDot(double x, double y, Color color, double diameter, Graphics g, boolean fill)
	{
		final Color OLD_COLOR = g.getColor();
		final double radius = diameter / 2.0;
		
		if (OLD_COLOR!=color)
			g.setColor(color);
		
		final Graphics2D g2d = (Graphics2D) g;
		
		Ellipse2D oval = new Ellipse2D.Double(x-radius,y-radius,diameter,diameter);
		if (fill)
			g2d.fill(oval);
		else
			g2d.draw(oval);
		
		if (OLD_COLOR!=color)
			g.setColor(OLD_COLOR);
	}

	public static void plotCornerDots(Rectangle2D rect, Color color, int diameter, Graphics g, boolean fill)
	{
		plotDot(rect.getX(), rect.getY(), color, diameter, g, fill);
		plotDot(rect.getX()+rect.getWidth(), rect.getY(), color, diameter, g, fill);
		plotDot(rect.getX(), rect.getY()+rect.getHeight(), color, diameter, g, fill);
		plotDot(rect.getX()+rect.getWidth(), rect.getY()+rect.getHeight(), color, diameter, g, fill);
	}
	
	public static void setX(Rectangle2D rect, double newX) 
	{
		rect.setRect(newX, rect.getY(), rect.getWidth(), rect.getHeight());
	}
	
	public static void setX(Point2D point, double newX) 
	{
		point.setLocation(newX, point.getY());
	}
	
	public static void translateX(Point2D point, double deltaX)
	{
		point.setLocation(point.getX()+deltaX, point.getY());
	}
	
	public static void setY(Rectangle2D rect, double newY)
	{
		rect.setRect(rect.getX(), newY, rect.getWidth(), rect.getHeight());
	}
	
	public static void setY(Point2D point, double newY)
	{
		point.setLocation(point.getX(), newY);
	}
	
	public static void translateY(Point2D point, double deltaY)
	{
		point.setLocation(point.getX(), point.getY()+deltaY);
	}
	
	public static void translate(Point2D point, double deltaX, double deltaY)
	{
		point.setLocation(point.getX()+deltaX, point.getY()+deltaY);
	}
	
	public static void setWidth(Rectangle2D rect, double newWidth)
	{
		rect.setRect(rect.getX(), rect.getY(), newWidth, rect.getHeight());
	}
	
	public static void setHeight(Rectangle2D rect, double newHeight)
	{
		rect.setRect(rect.getX(), rect.getY(), rect.getWidth(), newHeight);
	}
	
	public static String describe(Color designatedColor)
	{
		if (designatedColor==null)
			return "none"; // TODO i8n
		
		String desc = Integer.toHexString(designatedColor.getRGB());
		
		/* if (desc.length()>6)
			desc = desc.substring(2).toUpperCase();
		else
			desc = fill(desc, '0', 6); */
		
		desc = "#" + fill(desc, '0', 8);
		
		return desc;
	}
	
	/**
	 * @param designatedColor a non-null Color
	 */
	public static String describeWithoutAlpha(Color designatedColor)
	{
		if (designatedColor==null)
			return "none";
		
		String desc = Integer.toHexString(designatedColor.getRGB());
		desc = "#" + fill(desc, '0', 8).substring(2);
		return desc;
	}
	
	public static String fill(String theString, char theChar, int desiredLength)
	{
		if (theString==null)
			return null;
		
		while (theString.length()<desiredLength)
		{
			theString = theChar + theString;
		}
		
		return theString;
	}
	public static int integerFromHexString(String hexString)
	{
		long answerAsPositiveLong = Long.parseLong(hexString, 16);
		
		if (answerAsPositiveLong<=Integer.MAX_VALUE)
			return (int) answerAsPositiveLong;
		else
			return Integer.MIN_VALUE + (int) (answerAsPositiveLong - Integer.MAX_VALUE) - 1;
	}
	
	/**
	 * Attempts to parse the color from an eight or nine character hex string in the forms,
	 * <pre>
	 *     ff0011cc   -- or --
	 *    #FF0011CC
	 *     ^ ^ ^ ^
	 *     a r g b
	 *     l e r l
	 *     p d e u
	 *     h   e e
	 *     a   n
	 * </pre>
	 *
	 * returning null on failures.
	 */
	public static Color parseColor(String designatedHexString) throws NumberFormatException
	{
		try
		{
			if (designatedHexString==null)
				return null;

			designatedHexString = designatedHexString.trim();

			if (designatedHexString.startsWith("#"))
				designatedHexString = designatedHexString.substring(1);

			if (designatedHexString.length()<=6)
				designatedHexString = "FF" + designatedHexString; // prepend opaqueness

			// System.out.println("decoding: "+designatedHexString);
			long colorAsLong = Long.parseLong(designatedHexString, 16);

			return new Color((int)colorAsLong);
		}
		catch (NumberFormatException exception) 
		{
			// Oh well
			return null;
		}
	}
}
