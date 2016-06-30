/**
 * Copyleft 2006 by Dave Horlick

 */
 
package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Named;


public class RenameEdit extends StatefulMultipleEdit
{
	public RenameEdit(String designatedName)
	{
		super(MENU_TEXT.getString("renameLabel"), designatedName);
		setIsolate(true);
	}
	
	public Object setState(Object thing, Object value)
	{
		if (thing instanceof Named)
		{
			Named named = (Named) thing;
			String newName = null;
			
			if (value instanceof String)
				newName = (String) value;
			else if (value==null)
				newName = null;
			else
				newName = String.valueOf(value);

			String oldName = named.getName();
			
			named.setName(newName);
			return oldName;
		}
		
		return NO_EFFECT;
	}
}