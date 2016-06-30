/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.util.ResourceBundle;


/**
 *
 * @author dhorlick
 */
public class AbstractNamed implements Named
{
	private String name;
	
	public static ResourceBundle NAMES_TEXT = ResourceBundle.getBundle("resources/text/names");

	public void setName(String designatedName)
	{
		if (name!=designatedName && (designatedName==null || !designatedName.equals(name)))
		{
			name = designatedName;
		}
	}

	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		if (name!=null)
			return name;
		else
			return NAMES_TEXT.getString("unnamedLabel");
	}
}
