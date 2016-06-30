/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.geom.ShapeFriend;
import javax.swing.JFrame;

public class ColorPickerTest implements Runnable
{
	public void go()
	{
		JFrame frame = new JFrame(getClass().getName());
		frame.setSize(270,270);
		ColorPicker cpick = new ColorPicker();
		
		// component = buildComponent(new PunctuatedSuperEllipse());
		
		frame.getContentPane().add(cpick);
		
		frame.setVisible(true);
	}
	
	public static void main(String [] args)
	{
		new ColorPickerTest().go();
	}
	
	public static void roundtrip(String hexString)
	{
		int AS_INT = ShapeFriend.integerFromHexString(hexString);
		String roundtripped = Integer.toHexString(AS_INT);
		roundtripped = ShapeFriend.fill(roundtripped, '0', 8);
		
		if (!roundtripped.equalsIgnoreCase(hexString))
			throw new IllegalArgumentException(hexString+"!="+roundtripped);
	}
	
	public void run()
	{
		roundtrip("FF000000");
		roundtrip("000000FF");
		roundtrip("BBAA11CC");
		roundtrip("FF008880");
	}
}
