/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.geom.Point2D;

import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.geom.Stemmed;


public class TranslateEdit extends MultipleEdit
{
	private PerimeterSegment perimeterSegment;
	private int deltaX;
	private int deltaY;
	
	public TranslateEdit(PerimeterSegment designatedPerimeterSegment, int designatedDeltaX, int designatedDeltaY)
	{
		super(MENU_TEXT.getString("relocateLabel"));
		setDeltaX(designatedDeltaX);
		setDeltaY(designatedDeltaY);
		setPerimeterSegment(designatedPerimeterSegment);
	}
	
	public boolean execute(Object item)
	{
		return go(item, true);
	}
	
	private boolean go(Object item, boolean execute)
	{
		// System.out.println("executing on: "+item);
		int sign = 1;
		if (!execute)
			sign = -1;
		
		if (item instanceof Relocateable && perimeterSegment==null)
		{
			Relocateable relocateable = (Relocateable) item;
			
			// System.out.println("moving: "+relocateable);
			
			relocateable.translate(sign*deltaX, sign*deltaY);
			return true;
		}
		else if (item instanceof Stem
				&& (perimeterSegment==PerimeterSegment.HOT_SPOT || perimeterSegment==null))
		{
			Stem stem = (Stem) item;

			if (stem!=null)
			{
				Point2D.Double focus = stem.getFocus();
				focus.x += (sign*deltaX);
				focus.y += (sign*deltaY);

				stem.setFocus(focus);

				return true;
			}
		}
		else if (perimeterSegment==PerimeterSegment.STEM_BODY)
		{
			Stem stem = null;
			Resizeable parent = null;
			
			if (item instanceof Stem)
			{
				stem = (Stem) item;
				if (stem.getParent()!=null && stem.getParent() instanceof Resizeable)
					parent = (Resizeable) stem.getParent();
			}
		}
		
		return false;
	}

	private double censorRootPosition(double newRootPos)
	{
		if (newRootPos<=0.0)
		{
			newRootPos = 0.99;
		}
		else if (newRootPos>=1.0)
		{
			newRootPos = 0.01;
		}
		return newRootPos;
	}
	
	public boolean backout(Object item) throws CannotUndoException
	{
		boolean result = go(item, false);
		
		if (result)
		{
			Crowd.reLayoutCrowdsOf(item);
		}
		
		return result;
	}
	
	public int getDeltaX()
	{
		return deltaX;
	}

	public int getDeltaY()
	{
		return deltaY;
	}

	private void setDeltaX(int designatedDeltaX)
	{
		deltaX = designatedDeltaX;
	}

	private void setDeltaY(int designatedDeltaY)
	{
		deltaY = designatedDeltaY;
	}
	
	public boolean addEdit(UndoableEdit anEdit)
	{
		if (!isIsolate() && anEdit instanceof TranslateEdit)
		{
			TranslateEdit newerTranslateEdit = (TranslateEdit) anEdit;
			
			if (newerTranslateEdit.getPerimeterSegment()==getPerimeterSegment())
			{
				deltaX += newerTranslateEdit.getDeltaX();
				deltaY += newerTranslateEdit.getDeltaY();
				
				return true;
			}
		}
		
		return false;
	}
	
	public PerimeterSegment getPerimeterSegment()
	{
		return perimeterSegment;
	}

	public void setPerimeterSegment(PerimeterSegment designatedPerimeterSegment)
	{
		perimeterSegment = designatedPerimeterSegment;
	}
	
	public boolean hasEffect()
	{
		if (deltaX==0 && deltaY==0)
			return false;
		
		return true;
	}

	public boolean reExecute(Object item)
	{
		boolean result;
		
		result = super.reExecute(item);
		
		if (item instanceof Relocateable)
			Crowd.reLayoutCrowdsOf(item);
		
		return result;
	}
}
