/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;


/**
 *
 * @author dhorlick
 */
public abstract class MysteryHandle extends JPanel
{
	public MysteryHandle()
	{
		super();
		// super(BoxLayout.Y_AXIS);
		final Dimension MIN_DIM = new Dimension(18, 13);
		
		setMinimumSize(MIN_DIM);
		// setSize(MIN_DIM);
		setPreferredSize(new Dimension(22, 19));
		
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
		// setBounds(0, 0, 18, 13);
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Rectangle bounds = getBounds();
		g.setColor(Color.lightGray);
		g.fillRect(0,0, (int)bounds.getWidth(), (int)bounds.getHeight());
		g.setColor(Color.darkGray);
		
		String indexAsString = String.valueOf(getPresentableIndex());
		
		int x = ((int)bounds.getWidth()-g.getFontMetrics().stringWidth(indexAsString))/2;
		int y = ((int)bounds.getHeight()+g.getFontMetrics().getMaxAscent())/2;
		
		g.drawString(indexAsString, x, y);
	}
	
	public abstract int getPresentableIndex();
}
