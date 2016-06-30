/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.PlottingContext;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;


/**
 * A two-dimensional curve that can be obtained by taking a cross section of a three-dimensional
 * cone. These can be an ellipse, a circle, a parabola, or a hyperbola.
 *
 * @author dhorlick
 */
public class ConicSection implements Shapeable // Drawable, Indigent
{
	private double eccentricity;
	private double periapsisLength;
	
	private Point2D location;
	
	public ConicSection()
	{
	}
	
	public ConicSection(double requestedEccentricity, double requestedPeriapsisLength)
	{	
		if ( eccentricity < 0 )
		{
			throw new IllegalArgumentException("the requested eccentricity of " + requestedEccentricity + " is negative.");
		}
		
		if ( requestedPeriapsisLength < 0 )
		{
			throw new IllegalArgumentException("the requested periapsis of " + requestedPeriapsisLength + " is negative.");
		}
		
		setEccentricity(requestedEccentricity);
		setPeriapsis(requestedPeriapsisLength);
	}
	
	public void setEccentricity(double requestedEccentricity)
	{
		eccentricity = requestedEccentricity;
	}
	
	public void setPeriapsis(double requestedPeriapsisLength)
	{
		periapsisLength = Math.abs(requestedPeriapsisLength);
	}
	
	public boolean isEllipse()
	{
		if (eccentricity>0 && eccentricity<=1)
			return true;
		else
			return false;
	}
	
	public boolean isLine()
	{
		if (eccentricity==0)
			return true;
		else
			return false;
	}
	
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		if (isEllipse())
		{
			Ellipse2D.Double ellipse = new Ellipse2D.Double();
			
			double a = determineSemiMajorAxis();
			ellipse.width = 2 * a;
			
			ellipse.height = determineSemiMinorAxis();
			//ellipse.x = -14.0;
			//ellipse.x = -12.0;
			
			if (location != null)
			{
				ellipse.x = location.getX();
				ellipse.y = location.getY();
			}
			
			return ellipse;
		}
		else if (isLine())
		{
			return null;
		}
		else
		{
			return null;
		}
	}
	
	public double determineSemiMajorAxis()
	{
		//        p
		// r  = ----- = a (1 - e) -->
		//  p   1 + e
		
		//         2 r
		//            p
		// 2 a  = -----
		//        1 - e
			
		return periapsisLength / (1 - eccentricity);
	}

	public double determineSemiMinorAxis()
	{
		double a = determineSemiMajorAxis();
		return determineSemiMinorAxis(a);
	}
	
	private double determineSemiMinorAxis(double designatedSemiMajorAxis)
	{
		double a = designatedSemiMajorAxis;
		
		//       ________
		//      / 2    2             c
		// b = � a  - c    and  e = --- -->
		//                           a 
									 
		//       ___________
		//      / 2       2
		// b = � a ( 1 - e ) -->

		//       ___________________
		//      /   r            
		// b = /     p   2         2
		//    / ( ----- )   ( 1 - e )
		//   �    1 - e
		
		return Math.sqrt( Math.pow(a,2.0) * (1 - Math.pow(eccentricity,2.0)) );
	}
	
	public double determineVerticalCoordinateAtHorizontalCoordinate(double x)
	{
		//  2    2
		// x    y 
		// -- + -- = 1
		//  2    2
		// a    b
		
		//  2   2     2   2      2   2
		// b   x  +  a   y   =  a   b
		
		//          ________________          ________________
		//         /  2  2    2  2           /        2   2
		//        /  a  b  - b  x           /   2    b   x
		// y =   / -----------------  =    /   b  - --------
		//      /            2            /             2  
		//     �            a            �             a
		
		//           _________________
		//          /            2
		//         /  2         x
		// y =    /  b  ( 1 - ------ )
		//       /               2
		//      �               a
		
		double a = determineSemiMajorAxis();
		double b = determineSemiMinorAxis(a);
		
		return Math.sqrt( b*b * (1.0 - (x*x/ (a*a) ) ) );
	}
	
	public double determineHorizontalCoordinateAtVerticalCoordinate(double y)
	{
		//          ________________          ________________
		//         /  2  2    2  2           /        2   2
		//        /  a  b  - a  y           /   2    a   y
		// x =   / -----------------  =    /   a  - --------
		//      /            2            /             2  
		//     �            b            �             b
		
		//           _________________
		//          /            2
		//         /  2         y
		// x =    /  a  ( 1 - ------ )
		//       /               2
		//      �               b
		
		double a = determineSemiMajorAxis();
		double b = determineSemiMinorAxis(a);
		
		return Math.sqrt( a*a * (1.0 - (y*y/(b*b) )) );
	}
	
	public double determineWidthAtVerticalCoordinate(double y)
	{
		double x = determineHorizontalCoordinateAtVerticalCoordinate(y);
		return 2.0 * x;
	}
	
	public void setLocation(Point2D designatedLocation)
	{
		location = designatedLocation;
	}
	
	public Point2D getLocation()
	{
		return location;
	}
	
	public void setCenter(Point2D designatedCenter)
	{
		double a = determineSemiMajorAxis();
		double b = determineSemiMinorAxis(a);
		
		setLocation(new Point2D.Double(designatedCenter.getX() - a, designatedCenter.getY() - b));
	}
	
	public Point2D getCenter()
	{
		double a = determineSemiMajorAxis();
		double b = determineSemiMinorAxis(a);

		if (location==null)
			return new Point2D.Double(a,b);
		
		return new Point2D.Double(location.getX() + a, location.getY() + b);
	}
	
	public double getPeriapsis()
	{
		return periapsisLength;
	}
	
	public String toString()
	{
		Map map = new HashMap();
		map.put("eccentricity", new Double(eccentricity));
		map.put("periapsis", new Double(periapsisLength));
		
		return map.toString();
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		// TODO Auto-generated method stub
		
		throw new UnsupportedOperationException();
	}
}
