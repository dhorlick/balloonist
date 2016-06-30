/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

/**
 *
 * @author dhorlick
 */
public class CharacterizedFontFamily
{
	private String name;
	private boolean editablyPasteable;
	private boolean logical;
	
	public CharacterizedFontFamily()
	{
	}
	
	public CharacterizedFontFamily(String designatedName, boolean designatedEditablyPasteable)
	{
	}

	public String getName()
	{
		return name;
	}

	public void setName(String designatedName)
	{
		name = designatedName;
	}

	public boolean isEditablyPasteable()
	{
		return editablyPasteable;
	}

	public void setEditablyPasteable(boolean designatedEditablyPasteable)
	{
		editablyPasteable = designatedEditablyPasteable;
	}
	
	/**
	 * Will be called by the combo box
	 */
	public String toString()
	{
		return name;
	}

	/**
	 * @return true, if this font family is "logical" (i.e. fake). Examples of logical
	 * font families include "Monospaced" and "SansSerif"
	 */
	public boolean isLogical()
	{
		return logical;
	}
	
	public void setLogical(boolean designatedLogical)
	{
		logical = designatedLogical;
	}
}
