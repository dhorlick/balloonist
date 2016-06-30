/**
Copyleft 2009 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import java.util.List;
import javax.swing.undo.UndoableEdit;

public class MysteryAddEdit extends PresentableEdit
{
	private Object creation;
	private List parent;

	public MysteryAddEdit(List designatedParent, Object designatedCreation)
	{
		super(MENU_TEXT.getString("createLabel"));
		parent = designatedParent;
		creation = designatedCreation;
	}

	public boolean execute()
	{
		if (hasEffect()) {
			parent.add(creation);
			return true;
		}

		return false;
	}

	public boolean backout()
	{
		if (hasEffect()) {
			return parent.remove(creation);
		}

		return false;
	}

	public boolean hasEffect()
	{
		if (creation != null && parent != null) {
			return true;
		}

		return false;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		return false;
	}
}
