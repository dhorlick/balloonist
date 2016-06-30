/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author dhorlick
 */
public class ColorTile extends JPanel
{
	private Color color;
	private boolean selected;
	
	private static final Dimension PREFERRED_SIZE = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize");
	
	public ColorTile()
	{
	}
	
	public ColorTile(Color designatedColor)
	{
		this();
		setColor(designatedColor);
	}
	
	protected void paintComponent(java.awt.Graphics g)
	{
		super.paintComponent(g);
		Color originalColor = g.getColor();
		
		if (color!=null)
		{
			g.setColor(color);
			g.fillRect(0,0, getWidth(), getHeight());
		}
		else
		{
			g.setColor(Color.white);
			g.fillRect(0,0, getWidth(), getHeight());
			g.setColor(Color.red);
			g.drawLine(getWidth(), 0, 0, getHeight());
		}
		
		if (isSelected())
			g.setColor(Color.yellow);
		else
			g.setColor(Color.black);
		
		g.drawRect(0,0, getWidth(), getHeight());
		
		g.setColor(originalColor);
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color designatedColor)
	{
		color = designatedColor;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public Dimension getPreferredSize()
	{
		return PREFERRED_SIZE;
	}
}
