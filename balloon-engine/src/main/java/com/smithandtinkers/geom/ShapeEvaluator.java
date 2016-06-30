/**
 Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.geom;

import java.awt.geom.*;

/**
 * Evaluates the location of points along cubic bezier curves.
 *
 * This class is decidedly not thread safe.
 */
public class ShapeEvaluator
{
	/**
	 <h3>Parametric B&eacute;zier Curve</h3>
	 <p>(from Foley, van Dam page 489)</p>

<pre>
              3               2        2              3
Q(t) = (1 - t)  P  + 3t(1 - t)  P  + 3t (1 - t) P  + t P
                 1               2               3      4
</pre>

	 @param t  the time / parameter at which to evaluate the curve. I think this should be in the range
	 0 to 1, inclusive.
	 @param coords  three point pairs in sequence (P1.x, P1.y, P2.x, P2.y, P3.x, P3.y) that define the
	 shape of the curve.
	 @param controlX The x coordinate of the starting point.
	 @param controlY The y coordinate of the starting point.
	 @param output An optional point to store the result in. If none is provided,
	 one will be instantiated.
	 */
	public static Point2D evaluateCubicBezier(double t, double [] coords, double controlX, double controlY, Point2D output)
	{

		if (output == null)
		{
			output = new Point2D.Double();
		}

		output.setLocation(  Math.pow(1.0 - t,3.0)      * controlX
					 + 3.0*t*Math.pow(1.0 - t,2.0)      * coords[0]
					 + 3.0 * Math.pow(t,2.0) *(1.0 - t) * coords[2]
					 + Math.pow(t,3.0)                  * coords[4]  ,

		                     Math.pow(1.0 - t,3.0)      * controlY
					 + 3.0*t*Math.pow(1.0 - t,2.0)      * coords[1]
					 + 3.0 * Math.pow(t,2.0) *(1.0 - t) * coords[3]
					 + Math.pow(t,3.0)                  * coords[5]   );

		return output;
	}
}