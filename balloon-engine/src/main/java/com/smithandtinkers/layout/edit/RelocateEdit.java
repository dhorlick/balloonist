/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.geom.Point2D;
import javax.swing.undo.UndoableEdit;

import com.smithandtinkers.geom.Stem;


/**
 *
 * @author dhorlick
 */
public class RelocateEdit extends StatefulSelectionEdit
{
	private PerimeterSegment perimeterSegment;
	private boolean targetsY;
	
	public RelocateEdit(double designatedCoordinate, boolean designatedTargetsY, PerimeterSegment designatedPerimeterSegment)
	{
		super(MENU_TEXT.getString("relocateLabel"), new Double(designatedCoordinate));
		targetsY = designatedTargetsY;
		perimeterSegment = designatedPerimeterSegment;
	}
	
	/**
	 * @return false if this edit concerns the x coordinate.
	 *         true  if this edit concerns the y coordinate.
	 */
	public boolean isTargetsY()
	{
		return targetsY;
	}
	
	public PerimeterSegment getPerimeterSegment()
	{
		return perimeterSegment;
	}
	
	public Object setState(Selectable selectable, Object value)
	{
		if (value instanceof Number)
		{
			Number newCoordinateAsNumber = (Number) value;
			double newCoordinate = newCoordinateAsNumber.doubleValue();

			if (perimeterSegment==null && selectable instanceof Relocateable)
			{
				Relocateable relocateable = (Relocateable) selectable;
	
				Point2D oldPosition = relocateable.getLocation();
				
				Point2D newPosition = new Point2D.Double();
				
				if (targetsY)
					newPosition.setLocation(oldPosition.getX(), newCoordinate);
				else
					newPosition.setLocation(newCoordinate, oldPosition.getX());
				
				relocateable.setLocation(newPosition);
				
				Crowd.reLayoutCrowdsOf(relocateable);
				
				if (targetsY)
					return new Double(oldPosition.getY());
				else
					return new Double(oldPosition.getX());
			}
			else if (selectable instanceof Stem)
			{
				Stem stem = (Stem) selectable;
				
				if (perimeterSegment==PerimeterSegment.HOT_SPOT)
				{
					Point2D oldPosition = new Point2D.Double();
					oldPosition.setLocation(stem.getFocus());
					
					if (targetsY)
						stem.setFocus(new Point2D.Double(oldPosition.getX(), newCoordinate));
					else
						stem.setFocus(new Point2D.Double(newCoordinate, oldPosition.getY()));
					
					if (targetsY)
						return new Double(oldPosition.getY());
					else
						return new Double(oldPosition.getX());
				}
			}
		}
		return NO_EFFECT;
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		/* if (anEdit instanceof RelocateEdit)
		{
			RelocateEdit otherRelocateEdit = (RelocateEdit) anEdit;
			
			if (otherRelocateEdit.isTargetsY()!=isTargetsY())
				return false;
			
			if (otherRelocateEdit.getPerimeterSegment()!=getPerimeterSegment())
				return false;
		}
		
		return super.addEdit(anEdit); */
		
		return false;
	}
	
	
}
