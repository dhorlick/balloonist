/**
 Copyleft Jul 18, 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.geom.Stemmed;

/**
 * A simple enumerated type.
 * 
 * @author dhorlick
 */
public final class PerimeterSegment
{
	public static final PerimeterSegment ACTUAL_PERIMETER = new PerimeterSegment("Actual Perimeter", false);
	
	public static final PerimeterSegment TOP_SIDE = new PerimeterSegment("Top Side", true);
	public static final PerimeterSegment LEFT_SIDE = new PerimeterSegment("Left Side", true);
	public static final PerimeterSegment RIGHT_SIDE = new PerimeterSegment("Right Side", true);
	public static final PerimeterSegment BOTTOM_SIDE = new PerimeterSegment("Bottom Side", true);
	
	public static final PerimeterSegment TOP_LEFT_CORNER = new PerimeterSegment("Top Left Corner", true);
	public static final PerimeterSegment BOTTOM_LEFT_CORNER = new PerimeterSegment("Bottom Left Corner", true);
	public static final PerimeterSegment BOTTOM_RIGHT_CORNER = new PerimeterSegment("Bottom Right Corner", true);
	public static final PerimeterSegment TOP_RIGHT_CORNER = new PerimeterSegment("Top Right Corner", true);
	
	public static final PerimeterSegment HOT_SPOT = new PerimeterSegment("Hotspot", false);
	
	public static final PerimeterSegment STEM_BODY = new PerimeterSegment("Stem Body", false);
	public static final PerimeterSegment STEM_LEADING_EDGE = new PerimeterSegment("Stem Leading Edge", false);
	public static final PerimeterSegment STEM_TRAILING_EDGE = new PerimeterSegment("Stem Trailing Edge", false);
	
	private static final double TOLERANCE = 2.0;
	
	public String name;

	private boolean involvesResizing;
	
	private PerimeterSegment(String designatedName, boolean designatedInvolvesResizingness)
	{
		name = designatedName;
		involvesResizing = designatedInvolvesResizingness;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toName()
	{
		return getName();
	}
	
	public static PerimeterSegment identify(Rectangle2D rect, Point2D point, Perimetered perimed)
	{
		return identify(rect, point.getX(), point.getY(), perimed);
	}
	
	public static PerimeterSegment identify(Rectangle2D rect, double x, double y, Perimetered perimed)
	{
		if (rect==null)
			return null;
		
		if (perimed instanceof Stemmed)
		{
			Stemmed stemmed = (Stemmed) perimed;
			for (int index=0; index<=stemmed.stemCount()-1; index++)
			{
				Stem stem = stemmed.getStem(index);
				PerimeterSegment stemPSeg = identify(x, y, stem);
				if (stemPSeg!=null)
					return stemPSeg;
			}
		}
		
		if (x>rect.getMaxX()+1 || x<rect.getMinX()-1
				|| y>rect.getMaxY()+1 || y<rect.getMinY())
		{
			return null;
		}
		
		double distanceFromTop = rect.getY()-y;
		double distanceFromLeft = rect.getX()-x;
		
		boolean hitTop = false, hitLeft = false, hitBottom = false, hitRight = false;
		
		if (Math.abs(distanceFromTop)<=TOLERANCE)
			hitTop = true;
		if (Math.abs(distanceFromLeft)<=TOLERANCE)
			hitLeft = true;
		if (Math.abs(distanceFromTop+rect.getHeight())<=TOLERANCE)
			hitBottom = true;
		if (Math.abs(distanceFromLeft+rect.getWidth())<=TOLERANCE)
			hitRight = true;
		
		if (hitTop)
		{
			if (hitLeft)
				return TOP_LEFT_CORNER;
			
			if (hitRight)
				return TOP_RIGHT_CORNER;
				
			return TOP_SIDE;
		}
		else if (hitBottom)
		{
			if (hitLeft)
				return BOTTOM_LEFT_CORNER;
			
			if (hitRight)
				return BOTTOM_RIGHT_CORNER;
			
			return BOTTOM_SIDE;
		}
		else if (hitLeft)
			return LEFT_SIDE;
		else if (hitRight)
			return RIGHT_SIDE;
		
		// if (perimed!=null && perimed.onPerimeter(x, y))
		//	return ACTUAL_PERIMETER;
		
		return null;
	}
	
	/**
	 * @return The PerimeterSegment portion of the stem chosen, or null if none is.
	 */
	public static PerimeterSegment identify(double x, double y, Stem stem)
	{
		if (stem.getFocus()!=null)
		{
			// System.out.println("("+x+","+y+")");
			
			double dist = computeDistance(stem.getFocus().getX(), stem.getFocus().getY(), x, y);
			
			// System.out.println("dist="+dist);
			
			if (dist<=TOLERANCE)
				return HOT_SPOT;
		}
		
		/* if (computeDistance(stem.getLeadingEdgeX(), stem.getLeadingEdgeY(), x, y) <= TOLERANCE)
			return STEM_LEADING_EDGE;
		
		if (computeDistance(stem.getTrailingEdgeX(), stem.getTrailingEdgeY(), x, y) <= TOLERANCE)
			return STEM_TRAILING_EDGE; */
		
		if (stem.contains(x,y))
			return STEM_BODY;
		
		return null;
	}

	public String toString()
	{
		return name;
	}

	/**
	 * @return Whether or not this PerimeterSegment involves resizing.
	 */
	public boolean involvesResizing()
	{
		return involvesResizing;
	}
	
	public static double computeDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
	}
	
	/**
	 * Determines which stem (if any) is targeted.
	 */
	public static Stem whichStem(Stemmed stemmed, double x, double y)
	{
		for (int index=0; index<=stemmed.stemCount()-1; index++)
		{
			Stem stem = stemmed.getStem(index);
			PerimeterSegment stemPSeg = identify(x, y, stem);
			if (stemPSeg!=null)
				return stem;
		}
		
		return null;
	}
}
