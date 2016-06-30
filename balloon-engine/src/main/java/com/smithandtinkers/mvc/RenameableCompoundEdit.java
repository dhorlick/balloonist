/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.Named;
import javax.swing.UIManager;
import javax.swing.undo.CompoundEdit;

/**
 *
 * @author dhorlick
 */
public class RenameableCompoundEdit extends CompoundEdit implements Named
{
	private String name;
	
	public RenameableCompoundEdit()
	{
		super();
	}
	
	public RenameableCompoundEdit(String designatedName)
	{
		this();
		setName(designatedName);
	}

    public String getRedoPresentationName()
	{
		StringBuffer text = new StringBuffer();
		text.append(UIManager.getString("AbstractUndoableEdit.redoText"));

		if (getPresentationName()!=null && getPresentationName().length()>0)
		{
			text.append(" "); // TODO i8n; not all languages use spaces
			text.append(getPresentationName());
		}
			
		return text.toString();
	}

	public String getUndoPresentationName()
	{

		StringBuffer text = new StringBuffer();
		text.append(UIManager.getString("AbstractUndoableEdit.undoText"));

		if (getPresentationName()!=null && getPresentationName().length()>0)
		{
			text.append(" "); // TODO i8n; not all languages use spaces
			text.append(getPresentationName());
		}
			
		return text.toString();
	}

	public String getPresentationName()
	{
		return name;
	}

	public void setName(String designatedName)
	{
		name = designatedName;
	}

	public String getName()
	{
		return name;
	}
}
