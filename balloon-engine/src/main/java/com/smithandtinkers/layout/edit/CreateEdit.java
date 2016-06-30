/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.List;

import javax.swing.undo.UndoableEdit;


/**
 *
 * @author dhorlick
 */
public class CreateEdit extends PresentableEdit
{
	private Object creation;
	private List parent;
	
	public CreateEdit()
	{
		super(MENU_TEXT.getString("createLabel"));
	}
	
	public CreateEdit(String designatedTitle)
	{
		super(designatedTitle);
	}
	
	public CreateEdit(List designatedTypesafeList, Object designatedCreation)
	{
		this();
		setParent(designatedTypesafeList);
		setCreation(designatedCreation);
	}
	
	public CreateEdit(String designatedTitle, List designatedTypesafeList, Object designatedCreation)
	{
		this(designatedTitle);
		setParent(designatedTypesafeList);
		setCreation(designatedCreation);
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		if (anEdit instanceof FlipHeightEdit || anEdit instanceof FlipWidthEdit)
			return true;
		
		return false;
	}

	public boolean execute()
	{
		if (hasEffect())
		{
			parent.add(0, creation);
			return true;
		}
		
		return false;
	}

	public boolean backout()
	{
		if (hasEffect())
		{
			return parent.remove(creation);
		}
		
		return false;
	}

	public boolean hasEffect()
	{
		if (creation!=null && parent!=null)
			return true;
		
		return false;
	}

	public Object getCreation()
	{
		return creation;
	}

	private void setCreation(Object designatedCreation)
	{
		creation = designatedCreation;
	}

	public List getParent()
	{
		return parent;
	}

	public void setParent(List designatedParent)
	{
		parent = designatedParent;
	}
}
