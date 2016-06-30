/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import java.awt.geom.Point2D;
import junit.framework.TestCase;

/**
 *
 * @author dhorlick
 */
public class RelocateableTest extends TestCase
{
	public void test()
	{
		Sill sillA = new Sill();
		sillA.setContentOrigin(new Point2D.Double(10, 15));
		
		Sill sillB = new Sill();
		sillB.setContentOrigin(new Point2D.Double(0, 5));
		
		sillA.add(sillB);
		
		double mapped = sillB.mapOutsideY(27);
		
		if (mapped!=7)
			throw new IllegalStateException(String.valueOf(mapped));
	}
}
