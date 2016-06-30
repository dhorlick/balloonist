/**
 * Copyleft 2007 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.PlatformFriend;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

/**
 *
 * @author dhorlick
 */
public class BackgroundRespectingRadioButton extends JRadioButton
{
	public BackgroundRespectingRadioButton(ImageIcon designatedImageIcon)
	{
		super(designatedImageIcon);
	}
	
	protected void paintComponent(Graphics g)
	{
		if (PlatformFriend.RUNNING_ON_MAC)
		{
			final Graphics2D g2 = (Graphics2D) g;
			final Color oldColor = g2.getColor();
			g2.setColor(getBackground());
			g2.fillRect(0,0,getWidth(),getHeight()); // since Mac OS X Leopard ignores the background color of buttons
			g2.setColor(oldColor);
		}		
		super.paintComponent(g);
	}
}
