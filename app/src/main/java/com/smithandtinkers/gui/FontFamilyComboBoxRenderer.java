/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;


/**
 *
 * @author dhorlick
 */
public class FontFamilyComboBoxRenderer extends BasicComboBoxRenderer
{
	public final static Color BROWN = new Color(180, 80, 0);
	public final static Color PURPLE = new Color(150, 00, 200);
	
	public FontFamilyComboBoxRenderer()
	{
		super();
	}
	
	public Component getListCellRendererComponent(JList list, 
                                                 Object value,
                                                 int index, 
                                                 boolean isSelected, 
                                                 boolean cellHasFocus)
    {
		Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				// ^^ returned component should be this one
		
		if (value instanceof CharacterizedFontFamily)
		{
			CharacterizedFontFamily characterized = (CharacterizedFontFamily) value;
			
			if (!characterized.isEditablyPasteable() && getFont()!=null)
			{
				setFont(getFont().deriveFont(Font.ITALIC));
				
				if (!isSelected)
				{
					if (characterized.isLogical())
						setForeground(PURPLE);
					else
						setForeground(BROWN);
				}
			}
		}
		
		return component;
	}
}
