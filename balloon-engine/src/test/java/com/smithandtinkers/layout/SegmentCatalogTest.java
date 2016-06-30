/**
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class SegmentCatalogTest extends TestCase
{
	public void test()
	{
		Segment seg1 = new Segment(12.4, 53.4);
		Segment seg2 = new Segment(17.4, 65.1);

		SegmentCatalog thinland = new SegmentCatalog();
		if (thinland.compare(seg1, seg2)>=0)
			throw new IllegalStateException("SegmentCatalog.compare(Segment, Segment) doesn't work");
	}
}