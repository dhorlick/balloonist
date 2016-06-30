/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.TypesafeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SegmentCatalog implements Comparator
{
	private List segments = new TypesafeList(Segment.class);
	
	public SegmentCatalog()
	{
		super();
	}
	
	/**
	 * @return the index of the aborbing or newly-added segment
	 */
	public int add(Segment designatedSegment)
	{
		if (designatedSegment==null)
			return -1;
	
		int absorbed = -1;
		
		for (int loop=0; loop<=segments.size()-1; loop++)
		{
			Segment existingSegment = (Segment) segments.get(loop);
			
			if (existingSegment.overlaps(designatedSegment))
			{
				existingSegment.absorb(designatedSegment);
				absorbed = loop;
				Logger.println("tl: overlaps index "+absorbed);
			}
		}
		
		if (absorbed==-1)
		{
			Logger.println("tl: adding");
			segments.add(designatedSegment);
		}
			
		// re-sort
		
		Collections.sort(segments, this);
		
		Logger.println("re-sorted: "+segments);
		
		if (absorbed>-1)
			return absorbed;
		else
			return segments.size()-1;
	}
	
	/**
	 * Performs a comparison based on the front edge.
	 *
	 * Note that this should produce the same result as comparing
	 * the rear edge would, except for the adjoining case.
	 */
	public int compare(Object o1, Object o2)
	{
		Segment segment1 = (Segment) o1;
		Segment segment2 = (Segment) o2;
		
		return (int) (segment1.getFront()-segment2.getFront());
	}
	
	public int size()
	{
		return segments.size();
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Thinland {"
				+ segments
				+ "}";
	}
}