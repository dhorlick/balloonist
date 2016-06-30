/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;


/**
 * @author dhorlick
 */
public class ApertureTest extends TestCase
{
	public void test()
	{
		Aperture aperture1 = new Aperture();
		
		aperture1.setShape(new Rectangle2D.Double(10.0, 10.0, 20.0, 25.0));
	}
}
