/*
 Copyleft 2006 by Dave Horlick
*/

package com.smithandtinkers.balloonist;

import com.smithandtinkers.util.PlatformFriend;
import com.smithandtinkers.util.ResourceFriend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;


public class SplashScreen extends JWindow
{
	public SplashScreen()
	{
		super();
		
		ImageIcon bugIcon = ResourceFriend.retrieveImageIcon("resources/pictures/splash.png");
		JLabel label = new JLabel(bugIcon);
		if (!PlatformFriend.RUNNING_ON_MAC)
			label.setBorder(BorderFactory.createLineBorder(Color.gray));
		
		getContentPane().add(label);
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int)(dim.getWidth() - getWidth())/2;
		int y = (int)(dim.getHeight() - getHeight())/2;
		setLocation(x,y);
	}
}