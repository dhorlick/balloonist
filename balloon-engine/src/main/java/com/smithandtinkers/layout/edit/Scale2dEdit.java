/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.Iterator;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoableEdit;

public class Scale2dEdit extends SelectionEdit
{
	private double widthFactor = 1.0;
	private double heightFactor = 1.0;
	
	private Scale2dEdit()
	{
		super(MENU_TEXT.getString("scaleLabel"));
	}
	
	public Scale2dEdit(double designatedFactor)
	{
		this();
		setFactor(designatedFactor);
	}
	
	public Scale2dEdit(double designatedWidthFactor, double designatedHeightFactor)
	{
		this();
		setWidthFactor(designatedWidthFactor);
		setHeightFactor(designatedHeightFactor);
	}
	
	public void setFactor(double designatedFactor)
	{
		setWidthFactor(designatedFactor);
		setHeightFactor(designatedFactor);
	}

	public double getWidthFactor()
	{
		return widthFactor;
	}

	public double getHeightFactor()
	{
		return heightFactor;
	}

	/**
	 * @throws IllegalArgumentException if the designated factor is 0.0.
	 */
	public void setHeightFactor(double designatedHeightFactor)
	{
		if (designatedHeightFactor==0.0)
			throw new IllegalArgumentException();
		
		heightFactor = designatedHeightFactor;
	}

	/**
	 * @throws IllegalArgumentException if the designated factor is 0.0.
	 */
	public void setWidthFactor(double designatedWidthFactor)
	{
		if (designatedWidthFactor==0.0)
			throw new IllegalArgumentException();
		
		widthFactor = designatedWidthFactor;
	}

	public boolean execute(Selectable selectable)
	{
		if (selectable instanceof Resizeable)
		{
			Resizeable resizeable = (Resizeable) selectable;
			resizeable.setWidth(getWidthFactor()*resizeable.getWidth());
			resizeable.setHeight(getHeightFactor()*resizeable.getHeight());
			
			Crowd.reLayoutCrowdsOf(resizeable);
			
			return true;
		}
		
		return false;
	}

	public boolean backout(Selectable selectable)
	{
		if (selectable instanceof Resizeable)
		{
			Resizeable resizeable = (Resizeable) selectable;
			resizeable.setWidth(resizeable.getWidth()/getWidthFactor());
			resizeable.setHeight(resizeable.getHeight()/getHeightFactor());
			
			Crowd.reLayoutCrowdsOf(resizeable);
			
			return true;
		}
		
		return false;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		if (!isIsolate() && anEdit instanceof Scale2dEdit)
		{
			Scale2dEdit otherScaleEdit = (Scale2dEdit) anEdit;
			widthFactor *= otherScaleEdit.getWidthFactor();
			heightFactor *= otherScaleEdit.getHeightFactor();
		}
		
		return false;
	}

	public boolean hasEffect()
	{
		if (getWidthFactor()==1.0 && getHeightFactor()==1.0)
			return false;
		else
			return true;
	}
}
