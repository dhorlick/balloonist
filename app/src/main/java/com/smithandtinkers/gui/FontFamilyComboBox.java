/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.balloonist.PdfFriend;
import com.smithandtinkers.graphics.FontFriend;
import com.smithandtinkers.util.Logger;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author dhorlick
 */
public class FontFamilyComboBox extends JComboBox
{
	private static String IDENTIFIER_HELVETICA = "Helvetica";
	private static String IDENTIFIER_COURIER = "Courier";
	
	private static Font HELVETICA_ALIKE = new Font(IDENTIFIER_HELVETICA, Font.PLAIN, 14); // special-case holdovers from 
	private static Font COURIER_ALIKE = new Font(IDENTIFIER_COURIER, Font.PLAIN, 14);     // early Java versions
	
	public FontFamilyComboBox(boolean designatedAllowFontsThatCantBeEditablyPasted)
	{
		super(constructAppropriateModel(designatedAllowFontsThatCantBeEditablyPasted));
	}
	
	public FontFamilyComboBox()
	{
		super(constructModel());
		setRenderer(new FontFamilyComboBoxRenderer());
		
		int initialIndex = indexOfFontFamilyName(BalloonEngineState.getInstance().getDefaultFont().getFamily());
		
		if (initialIndex!=-1)
			setSelectedIndex(initialIndex);
	}
	
	public void setFontFamilySelection(String designatedFontFamily)
	{
		int newIndex = indexOfFontFamilyName(designatedFontFamily);
		// System.out.println("new font name index: "+newIndex);

		if (newIndex==-1)
		{
			if (HELVETICA_ALIKE.getFamily().equals(designatedFontFamily))
				newIndex = indexOfFontFamilyName(IDENTIFIER_HELVETICA);
			else if (COURIER_ALIKE.getFamily().equals(designatedFontFamily))
				newIndex = indexOfFontFamilyName(IDENTIFIER_COURIER);
		}
		
		if (newIndex!=-1)
			setSelectedIndex(newIndex);
		else // still!
			Logger.println("could not find index for font family: "+designatedFontFamily);
		
		/* 
		 * According to Core Java
		 * 
		 * Prior versions of Java used the names Helvetica, TimesRoman,
		 * Courier, and ZapfDingbats as logical font names. For backward
		 * compatibility, these font names are still treated as logical
		 * font names even though Helvetica is really a font face name
		 * and TimesRoman and ZapfDingbats are not font names at all: the
		 * actual font face names are "Times Roman" and "Zapf Dingbats."
		 */
		
		// else TODO ...
	}
	
	public int indexOf(String requestedFontFamily)
	{
		// System.out.println("indexOf: "+requestedFontFamily);
		
		Object theModel = getModel();
		if (theModel==null)
			return -1;
		
		if (theModel instanceof DefaultComboBoxModel)
		{
			DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) theModel;
			return defaultComboBoxModel.getIndexOf(requestedFontFamily);
		}
		else if (theModel instanceof List)
		{
			List list = (List) theModel;
			return list.indexOf(requestedFontFamily);
		}
		else
			return -1;
	}
	
	public CharacterizedFontFamily getFontFamilySelection()
	{
		return (CharacterizedFontFamily) getSelectedObjects()[0];
	}
	
	public void setAllowFontsThatCantBeEditablyPasted(boolean designatedAllowFontsThatCantBeEditablyPasted)
	{
		setModel(constructAppropriateModel(designatedAllowFontsThatCantBeEditablyPasted));
	}
	
	/**
	 * @return ComboBoxModel<String>
	 */
	private static ComboBoxModel constructAppropriateModel(boolean designatedAllowFontsThatCantBeEditablyPasted)
	{
		if (designatedAllowFontsThatCantBeEditablyPasted)
		{
			Vector determined = FontFriend.determineFontFamilyNames();
			return new DefaultComboBoxModel(determined);
		}
		else
		{
			return PdfFriend.determineEditablyPasteableFontFamilyNames();
		}
	}
	
	/**
	 * @return ComboBoxModel<CharacterizedFontFamily>
	 */
	private static ComboBoxModel constructModel()
	{
		Vector vector = new Vector();
		
		List registeredFamilies = PdfFriend.determineEditablyPasteableFontFamilyNames();
		// System.out.println("registeredFamilies="+registeredFamilies);
		String [] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		for (int loop=0; loop<=availableFonts.length-1; loop++)
		{
			if (!FontFriend.isNonStandard(availableFonts[loop]))
			{
				CharacterizedFontFamily characterized = new CharacterizedFontFamily();
				characterized.setName(availableFonts[loop]);
				
				characterized.setEditablyPasteable(containsIgnoreCase(registeredFamilies, availableFonts[loop]));
				characterized.setLogical(FontFriend.isLogical(availableFonts[loop]));
				
				vector.addElement(characterized);
			}
		}
		
		// TODO sort?
		
		return new DefaultComboBoxModel(vector);
	}
	
	public static boolean containsIgnoreCase(final List list, final String string)
    {
	    if (list==null || string==null)
	    	return false;
		
		Iterator walk = list.iterator();
	    
	    while (walk.hasNext())
	    {
	    	Object item = walk.next();
	    	if (item!=null)
	    	{
	    		if (String.valueOf(item).equalsIgnoreCase(string))
	    			return true;
	    	}
	    }
		
		return false;
    }

	public int indexOfFontFamilyName(String requestedFontFamilyName)
	{
		if (requestedFontFamilyName==null)
			return -1;
		
		ComboBoxModel theModel = getModel();
		
		for (int loop=0; loop<=theModel.getSize(); loop++)
		{
			if (theModel.getElementAt(loop) instanceof String)
			{
				if (requestedFontFamilyName.equals(theModel.getElementAt(loop)))
					return loop;
			}
			else if (theModel.getElementAt(loop) instanceof CharacterizedFontFamily)
			{
				CharacterizedFontFamily characterized = (CharacterizedFontFamily) theModel.getElementAt(loop);
				if (requestedFontFamilyName.equals(characterized.getName()))
					return loop;
			}
		}
		
		return -1;
	}
}
