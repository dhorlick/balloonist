/**
Copyleft 2009 by Dave Horlick

 */
package com.smithandtinkers.layout.edit;

import java.util.List;

public class MysteryDeleteEdit extends StatefulMultipleEdit
{
	private List listToRemoveFrom;

	public MysteryDeleteEdit(List designatedListToRemoveFrom)
	{
		super(PresentableEdit.MENU_TEXT.getString("clearLabel"), null);
		listToRemoveFrom = designatedListToRemoveFrom;
	}

	public Object setState(Object thing, Object value)
	{
		if (value == null) // deletion
		{
			int formerIndex = listToRemoveFrom.indexOf(thing);
			if (formerIndex != -1) {
				listToRemoveFrom.remove(thing);
				return new Integer(formerIndex);
			}
		} else if (value instanceof Integer) // un-deletion
		{
			Integer formerIndex = (Integer) value;
			listToRemoveFrom.add(formerIndex.intValue(), thing);
		}

		return NO_EFFECT;
	}
}
