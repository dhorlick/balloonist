/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

public class ColorChart extends JPanel
{
	private Color selectedColor;
	
	public ColorChart()
	{
		initialize();
	}

	private void initialize()
	{
		// System.out.println("generating color chart...");
		
		// colorPopup = new JWindow(getFrame());
		// colorPopup.setFocusableWindowState(false);
		GridLayout gridLayout = new GridLayout(0, 16);
		setLayout(gridLayout);
		
		for (int red=0; red<256; red+=51)
		{
			for (int green=0; green<256; green+=51)
			{
				for (int blue=0; blue<256; blue+=51)
				{
					Color color = new Color(red, green, blue);
					// System.out.print(Integer.toHexString(color.));
					// System.out.print(color.toString());
					// System.out.print(" ");
					add(new ColorTile(color));
				}
			}
		}
		
		add(new ColorTile(null));
		
		addMouseMotionListener(new MouseMotionListener()
		{
			private Component lastHit;
		
			public void mouseDragged(MouseEvent e)
			{
				// System.out.println("ColorChart: dragged");
				mouseMovedOrDragged(e);
			}

			public void mouseMoved(MouseEvent e)
			{
				// System.out.println("ColorChart: moved");
				mouseMovedOrDragged(e);
			}
			
			private void mouseMovedOrDragged(MouseEvent mouseEvent)
			{
				// System.out.println("ColorChart: moved or dragged");
				
				if (isVisible())
				{
					Component hit = getComponentAt(mouseEvent.getPoint());
					
					if (hit!=lastHit)
					{
						// System.out.println("ColorChart: new component "+hit);
						
						if (lastHit instanceof ColorTile)
						{
							ColorTile lastHitTile = (ColorTile) lastHit;
							lastHitTile.setSelected(false);
							lastHitTile.repaint();
						}
						
						if (hit instanceof ColorTile)
						{
							ColorTile hitTile = (ColorTile) hit;
							hitTile.setSelected(true);
							hitTile.repaint();
							setSelectedColor(hitTile.getColor());
						}
						
						lastHit = hit;
					}
				}
			}
		});
		
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		setBorder(border);
	}
	
	public Color getSelectedColor()
	{
		return selectedColor;
	}
	
	private void setSelectedColor(Color designatedColor)
	{
		selectedColor = designatedColor;
	}
}
