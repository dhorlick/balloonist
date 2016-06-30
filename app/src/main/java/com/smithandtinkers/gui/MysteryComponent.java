/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.undo.UndoableEdit;


/**
 *
 * @author dhorlick
 */
public abstract class MysteryComponent extends Box
{	
	private Object model;
	private MysteryContainer mysteryContainer;
	
	private static final Color LIGHT_BLUE = new Color(0.9f,0.9f,1.0f);
	private static final Color DARK_BLUE = new Color(0.6f,0.6f,1.0f);
	
	public MysteryComponent()
	{
		super(BoxLayout.X_AXIS);
		
		MysteryHandle handle = new MysteryHandle()
		{
			public int getPresentableIndex()
			{
				return determinePresentableIndex();
			}
		};
		
		handle.addMouseListener(new MouseListener()
		{
			public void mouseReleased(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseClicked(MouseEvent e)
			{
				if (mysteryContainer!=null)
				{
					if (!e.isShiftDown())
					{
						// System.out.println("reseting selection");
						mysteryContainer.getSelected().clear();
					}
					
					// System.out.println("registering selection");
					mysteryContainer.getSelected().add(MysteryComponent.this);
					
					redrawContainer();
				}
			}
		});
		
		add(handle);
	}
	
	public void setModel(Object designatedModel)
	{
		model = designatedModel;
	}
	
	public Object getModel()
	{
		return model;
	}

	public MysteryContainer getMysteryContainer()
	{
		return mysteryContainer;
	}

	public void setMysteryContainer(MysteryContainer designatedMysteryContainer)
	{
		mysteryContainer = designatedMysteryContainer;
	}
	
	public void postEdit(UndoableEdit designatedEdit)
	{
		if (mysteryContainer!=null)
			mysteryContainer.postEdit(designatedEdit);
	}
	
	public abstract void stateChanged();
	
	public void paintComponent(Graphics g)
	{
		Rectangle2D bounds = getBounds();
		
		if (mysteryContainer!=null)
		{
			// System.out.println("mysteryContainer.getSelected(): "+mysteryContainer.getSelected());
			
			if (determinePresentableIndex() % 2 == 1)
			{
				g.setColor(LIGHT_BLUE);
				g.fillRect(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());
			}
		}
		
		super.paintComponent(g);
		
		if (mysteryContainer.getSelected().contains(this))
		{
			// System.out.println("is selected");
			g.setColor(DARK_BLUE);
			g.fillRect(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());
		}
	}
	
	/**
	 * @return an index, numbered up from zero, or
	 *         -1 if there is no set mystery containers
	 */
	public int determinePresentableIndex()
	{
		if (mysteryContainer==null)
			return -1;
		
		int theIndex = mysteryContainer.getModel().indexOf(model);
		
		if (theIndex==-1)
			return -1;
		
		return theIndex+1;
	}
	
	public void redrawContainer()
	{
		invalidate();
					
		if (mysteryContainer!=null)
		{
			mysteryContainer.invalidate();
			mysteryContainer.repaint();
		}
		else
		{
			repaint();
		}
	}
}
