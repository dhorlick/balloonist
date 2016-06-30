/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.geom.Rectangle2D;
import junit.framework.TestCase;


/**
 * @author dhorlick
 */
public class ShapeFriendTest extends TestCase
{
	public void test()
	{
		Rectangle2D.Double rect = new Rectangle2D.Double();
		rect.setRect(10.0, 20.0, 40.0, 30.0);
		
		if (ShapeFriend.computeArea(rect)!=1200.0)
		{
			throw new IllegalStateException();
		}
	}

}
