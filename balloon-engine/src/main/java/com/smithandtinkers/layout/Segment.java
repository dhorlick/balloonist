/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.util.Logger;


/**
 * Models a one-dimensional object.
 */
public class Segment
{
	private double front;
	private double back;
	
	/**
	 * @param designatedFront
	 * @param designatedBack
	 */
	public Segment(double designatedFront, double designatedBack)
	{
		setFrontAndBack(designatedFront, designatedBack);
	}
	
	public void setFrontAndBack(double designatedFront, double designatedBack)
	{
		if (designatedFront!=front || designatedBack!=back)
		{
			if (designatedFront>designatedBack)
				throw new IllegalArgumentException("Front must be less than back: "+designatedFront+", "+designatedBack);
			
			front = designatedFront;
			back = designatedBack;
		}
	}
	
	public double getFront()
	{
		return front;
	}
		
	public double getBack() // Get back, Loretta!
	{
		return back;
	}
	
	/**
	 * @return true if the other segment overlaps or adjoins this one.
	 */
	public boolean overlaps(Segment otherSegment)
	{
		Logger.println("s.overlaps: this="+this+", otherSegment="+otherSegment);
		
		if (otherSegment==null)
			return false;
			
		if (otherSegment.getFront()>=getFront() && otherSegment.getFront()<=getBack())
		{
			Logger.println("\tit overlaps");
			return true;
		}
			
		if (otherSegment.getBack()>=getFront() && otherSegment.getBack()<=getBack())
		{
			Logger.println("\tit overlaps");
			return true;
		}
		
		Logger.println("\tit does NOT overlap");
		
		return false;
	}
	
	public void absorb(Segment otherSegment)
	{
		if (!overlaps(otherSegment))
			return;
			
		if (otherSegment.getFront()<getFront())
			front = otherSegment.getFront();
			
		if (otherSegment.getBack()>getBack())
			back = otherSegment.getBack();
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Segment {"
			 + "front = " + front + ", "
			 + "back = " + back
		+ "}";
	}
}