/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.layout.Colorful;
import com.smithandtinkers.layout.edit.ColorfulEdit;
import com.smithandtinkers.layout.Layout;
import java.awt.Color;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;


/**
 * @author dhorlick
 */
public abstract class AbstractPropsPanel extends Box
{
	private DrawableDialog drawableDialog;
	private ChangeListener changeListener;
	
	protected final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	
	public AbstractPropsPanel()
	{
		super(BoxLayout.Y_AXIS);
		add(Box.createVerticalGlue());
		initialize();
		add(Box.createVerticalGlue());
		Dimension sizeTarget = new Dimension(343, 282);
		setMaximumSize(sizeTarget);
		setPreferredSize(sizeTarget);
	}
	
	protected abstract void initialize();
	
	protected abstract void stateChanged();
	
	public Layout getSillLayout()
	{
		if (getDrawableDialog()==null || getDrawableDialog().getArtwork()==null)
			return null;
		
		return getDrawableDialog().getArtwork().getLayout();
	}

	public DrawableDialog getDrawableDialog()
	{
		return drawableDialog;
	}
	
	public void setDrawableDialog(DrawableDialog designatedDrawableDialog)
	{
		drawableDialog = designatedDrawableDialog;
	}
	
	protected void fireChangeEvent()
	{
		/*
		if (recomputeEnclosure)
		{
			getDrawableDialog().getArtwork().enclose();
			getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill());
		}
		*/
		
		if (changeListener!=null)
			changeListener.stateChanged(CHANGE_EVENT);
	}
	
	public void setChangeListener(ChangeListener designatedChangeListener)
	{
		changeListener = designatedChangeListener;
	}
	
	public ChangeListener getChangeListener()
	{
		return changeListener;
	}
	
	public Colorful generateColorfulModel()
	{
		return new Colorful()
		{
			private Color getColor(boolean outline)
			{
				switch (getDrawableDialog().getSelection().getSelectedItemsCount())
				{
					case 0: return null;
					case 1: 
						if (getDrawableDialog().getSelection().getSelectedItem(0) instanceof Colorful)
						{
							Colorful colorfulOne = (Colorful) getDrawableDialog().getSelection().getSelectedItem(0);

							if (outline)
								return colorfulOne.getOutlineColor();
							else
								return colorfulOne.getFillColor();
						}
				}

				int red = 0;
				int green = 0;
				int blue = 0;

				int items = 0;

				Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();

				while (walk.hasNext())
				{
					Object item = walk.next();
					if (item instanceof Colorful)
					{
						Colorful colorful = (Colorful) item;

						Color theColor = null;

						if (outline)
							theColor = colorful.getOutlineColor();
						else
							theColor = colorful.getFillColor();
						
						if (theColor!=null)
						{
							red += theColor.getRed();						
							blue += theColor.getBlue();
							green += theColor.getGreen();

							items++;
						}
					}
				}

				if (items==0)
					return null;

				return new Color(red/items, green/items, blue/items);
			}

			public void setOutlineColor(Color designatedColor)
			{
				ColorfulEdit colorfulEdit = new ColorfulEdit(true, designatedColor);
				colorfulEdit.setIsolate(true);
				colorfulEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				boolean didSomething = colorfulEdit.execute();
				
				if (didSomething)
				{
					getDrawableDialog().announceEdit(this, colorfulEdit);
					fireChangeEvent();
				}
			}

			public void setFillColor(Color designatedColor)
			{
				ColorfulEdit colorfulEdit = new ColorfulEdit(false, designatedColor);
				colorfulEdit.setIsolate(true);
				colorfulEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				boolean didSomething = colorfulEdit.execute();
				
				if (didSomething)
				{
					getDrawableDialog().announceEdit(this, colorfulEdit);
					fireChangeEvent();
				}
			}

			public Color getOutlineColor()
			{
				return getColor(true);
			}

			public Color getFillColor()
			{
				return getColor(false);
			}
		};
	}
	
	public abstract void revealed();
}
