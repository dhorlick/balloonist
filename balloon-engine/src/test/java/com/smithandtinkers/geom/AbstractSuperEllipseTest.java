/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

import java.awt.geom.Point2D;
import java.util.Random;

import com.smithandtinkers.layout.PerimeterSegment;
import junit.framework.TestCase;

/**
 *
 * @author dhorlick
 */
public class AbstractSuperEllipseTest extends TestCase
{
	public void testFindCenterOfCircle()
    {
	    Point2D result = AbstractSuperEllipse.findCenterOfCircle(1, 0, 2, 1, 1, 2);
	    // System.out.println(result);
	    if (PerimeterSegment.computeDistance(1.0, 1.0, result.getX(), result.getY()) > 0.1)
	    	throw new IllegalStateException(result.toString());
	    
	    final Random RANDOM = new Random();
	    
	    final double x1 = RANDOM.nextInt(10);
	    final double y1 = RANDOM.nextInt(10);
	    final double x2 = 10+RANDOM.nextInt(10);
	    final double y2 = 10+RANDOM.nextInt(10);
	    final double x3 = 20+RANDOM.nextInt(10);
	    final double y3 = 20+RANDOM.nextInt(10);
	    
	    Point2D result2 = AbstractSuperEllipse.findCenterOfCircle(
	    		x1, y1, x2, y2, x3, y3);
	    
	    double radius1 = PerimeterSegment.computeDistance(x1, y1, result2.getX(), result2.getY());
	    double radius2 = PerimeterSegment.computeDistance(x2, y2, result2.getX(), result2.getY());
	    double radius3 = PerimeterSegment.computeDistance(x3, y3, result2.getX(), result2.getY());
	    
	    System.out.println("radii measures: "+radius1+" | "+radius2+" | "+radius3);
		
	    if (Math.abs(radius1-radius2)>1.0)
	    	throw new IllegalStateException("radius 1 of "+radius1+"!= radius 2 of "+radius2);
	    
	    if (Math.abs(radius3-radius2)>1.0)
	    	throw new IllegalStateException("radius 2 of "+radius2+"!= radius 3 of "+radius3);
    }

	public void testBeanness()
	{
		PunctuatedSuperEllipse pse = new PunctuatedSuperEllipse();
		pse.setHeinParameter(0.8);
		pse.setSemiMajorAxis(80);
		pse.setSemiMinorAxis(70);
		System.out.println("bruteForceArcLength: "+pse.bruteForceArcLength());
	}
}
