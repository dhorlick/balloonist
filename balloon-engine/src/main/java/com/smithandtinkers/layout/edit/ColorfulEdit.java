/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.Color;
import java.util.Iterator;

import com.smithandtinkers.layout.Colorful;


/**
 *
 * @author dhorlick
 */
public class ColorfulEdit extends StatefulSelectionEdit
{
	private boolean outline;
	
	public ColorfulEdit(boolean designatedOutlineness, Color designatedColor)
	{
		super(MENU_TEXT.getString("colorChangeLabel"), designatedColor);
		setOutline(designatedOutlineness);
	}
	
	private void setOutline(boolean designatedOutlineness)
	{
		outline = designatedOutlineness;
	}
	
	public boolean isOutline()
	{
		return outline;
	}
	
	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public Object setState(Selectable selectable, Object value)
	{
		Color newColor = (Color) value;
		Color oldColor = null;
		
		if (selectable instanceof Colorful)
		{
			Colorful colorful = (Colorful) selectable;

			if (outline)
			{
				oldColor = colorful.getOutlineColor();
				colorful.setOutlineColor(newColor);
			}
			else
			{
				oldColor = colorful.getFillColor();
				colorful.setFillColor(newColor);
			}
			
			return oldColor;
		}
		
		return NO_EFFECT;
	}
}
