/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.Rectangle;
import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class SillTest extends TestCase
{
	public void test()
	{
		Layout layout1 = new Layout();
		layout1.setVerticalGapInPoints(11);
		layout1.setAspectRatio(1.374633);
		Rectangle rect = new Rectangle();
		rect.setRect(0.0, 0.0, 726.696, 540.001);
		layout1.setHorizontalPageMarginInPoints(60.0);
		layout1.setVerticalPageMarginInPoints(50.0);
		layout1.setBounds(rect);
		layout1.setApertureQuantity(4);
		Sill sill1 = layout1.toSill();
		
		if (!sill1.equals(sill1))
			throw new IllegalStateException();
		
		Layout layout2 = new Layout();
		layout2.setVerticalGapInPoints(9);
		layout2.setAspectRatio(1.374633);
		layout2.setHorizontalPageMarginInPoints(60.0);
		layout2.setVerticalPageMarginInPoints(50.0);
		layout2.setBounds(rect);
		layout2.setApertureQuantity(4);
		Sill sill2 = layout2.toSill();
		
		Layout layout3 = new Layout();
		layout3.setVerticalGapInPoints(9);
		layout3.setAspectRatio(1.374633);
		layout3.setHorizontalPageMarginInPoints(60.0);
		layout3.setVerticalPageMarginInPoints(50.0);
		layout3.setBounds(rect);
		layout3.setApertureQuantity(4);
		Sill sill3 = layout3.toSill();
		
		if (layout1.equals(layout2))
			throw new IllegalStateException();
		
		if (layout2.equals(layout1))
			throw new IllegalStateException();
		
		if (!layout2.equals(layout3))
			throw new IllegalStateException();
		
		if (!layout3.equals(layout2))
			throw new IllegalStateException();
		
		if (layout1.hashCode()==layout2.hashCode())
			throw new IllegalStateException();
		
		if (layout2.hashCode()!=layout3.hashCode())
			throw new IllegalStateException();
	}
}
