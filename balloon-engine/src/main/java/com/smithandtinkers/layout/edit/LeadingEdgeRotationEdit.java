/*
 Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Kid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Collection;

import javax.swing.undo.UndoableEdit;


/**
 *
 * @author dave
 */
public class LeadingEdgeRotationEdit extends MultipleEdit
{	
	private int oldX;
	private int oldY;
	private int newX;
	private int newY;
	
	public LeadingEdgeRotationEdit()
	{
		super(MENU_TEXT.getString("changeLeadingEdgeAngleLabel"));
	}
	
	public void slide(int designatedOldX, int designatedOldY,
			int designatedNewX, int designatedNewY)
	{
		oldX = designatedOldX;
		oldY = designatedOldY;
		newX = designatedNewX;
		newY = designatedNewY;
	}
	
	public Point2D findCenter()
	{
		Collection punctuated = null;
		Iterator walk = affectedItems.iterator();
		
		while (walk.hasNext() && punctuated==null) // TODO handle case of stems surrounding multiple balloons
		{
			Object item = walk.next();
			
			if (item instanceof Stem)
			{
				Stem asStem = (Stem) item;
				if (asStem.getParent()!=null)
					punctuated = asStem.getParent();
			}
		}
		
		if (punctuated==null || !(punctuated instanceof Relocateable))
			return null;
		
		Relocateable asRelocateable = (Relocateable) punctuated;
			
		Point2D locationInWrongReferenceFrame = asRelocateable.getLocation();
		
		if (!(punctuated instanceof Kid))
			return locationInWrongReferenceFrame;
		
		Sill wrongReferenceFrame = Sill.findParentSillOf((Kid)punctuated);
		
		if (wrongReferenceFrame==null)
			return locationInWrongReferenceFrame;
		
		Point2D locationInCorrectReferenceFrame = new Point2D.Double(
			Sill.mapX(locationInWrongReferenceFrame.getX(), wrongReferenceFrame, null),
			Sill.mapY(locationInWrongReferenceFrame.getY(), wrongReferenceFrame, null));
			
		// System.out.println("chirp! "+locationInCorrectReferenceFrame);
		
		return locationInCorrectReferenceFrame;
	}
	
	public double computeRotationAngle()
	{
		Point2D center = findCenter();
		
		if (center==null)
			return 0.0;
		
		//              M � N
		// � = acos (-----------)
		//           ||M|| ||N||
		
		// where M � N = (Mx Nx) + (My Ny)
		
		final double Mx = oldX - center.getX();
		final double My = oldY - center.getY();
		final double Nx = newX - center.getX();
		final double Ny = newY - center.getY();
		
		final double dotProduct = (Mx * Nx) + (My * Ny);
		final double Mmag = Math.sqrt(Mx*Mx + My*My);
		final double Nmag = Math.sqrt(Nx*Nx + Ny*Ny);
		final double denominator = Mmag*Nmag;
		
		if (denominator==0)
			return 0.0;
		
		double cosineOfRotation = dotProduct/denominator;
		double rotation = 0.0;
		
		// Guard against rounding errors
		
		if (cosineOfRotation > 1.0)
			cosineOfRotation = 1.0;
		else if (cosineOfRotation < -1.0)
			cosineOfRotation = -1.0;
		
		// Determine appropriate direction of angle (thanks, Wikipedia "angle" article)
		
		if (Mx*Ny-My*Nx<0)
			rotation = -Math.acos( cosineOfRotation );
		else
			rotation =  Math.acos( cosineOfRotation );
		
		// System.out.println("rotation: "+rotation);
		return rotation;
	}
	
	public double computeSideShift()
	{
		return computeRotationAngle() / (2.0*Math.PI);
	}
	
	public boolean execute(Object item)
	{
		if (!(item instanceof Stem))
			return false;
			
		return go(item, computeSideShift());
	}
	
	/**
	 * @return Did the operation have an effect?
	 */
	public boolean backout(Object item)
	{
		if (!(item instanceof Stem))
			return false;
		
		return go(item, -computeSideShift());
	}
	
	public boolean go(Object item, double shift)
	{
		if (!(item instanceof Stem) || (shift==0.0))
			return false;
		
		Stem asStem = (Stem) item;
		double oldLeadingEdge = asStem.getLeadingEdgePositionAsPerimeterFraction();
		double newLeadingEdge = oldLeadingEdge + shift;
		
		asStem.setLeadingEdgePositionAsSideFraction(censorLeadingEdge(newLeadingEdge));
		
		return true;
	}
	
	public boolean addEdit(UndoableEdit anEdit)
	{
		return false; // TODO revisit
	}
	
	public int getOldX()
	{
		return oldX;
	}
	
	public int getOldY()
	{
		return oldY;
	}
	
	public int getNewX()
	{
		return newX;
	}
	
	public int getNewY()
	{
		return newY;
	}
	
	public boolean hasEffect()
	{
		return (size()>0);
	}
	
	/**
	 * @return a number between 0 inclusive and 1 exclusive
	 */
	public static double censorLeadingEdge(final double proposed)
	{
		double result = proposed;
		
		if (result<0.0)
		{
			result = Math.abs(1.0+result);
		}
		
		if (result>=1.0)
		{
			result -= (double) ((int)(result));
		}
		
		/* if (result!=proposed)
		{
			System.out.println("censored "+proposed+" to "+result);
		} */
		
		return result;
	}
}
