/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.TypesafeList;

import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Set the order of text flow between sections based on spatial geometry.
 * This is often (but not always) helpful.
 *
 * @author dhorlick
 */
public class OrderSectionsSpatiallyEdit extends StatefulMultipleEdit
{
	public OrderSectionsSpatiallyEdit()
	{
		super(MENU_TEXT.getString("orderSectionsSpatiallyLabel"), null);
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof TextShape)
		{
			TextShape thingAsTextShape = (TextShape) thing;
			
			if (value==null)
			{
				// execute
				
				// save old sequence
				
				List oldSequence = new TypesafeList(Perch.class);
				oldSequence.addAll(thingAsTextShape);
				
				if (!thingAsTextShape.isVertical())
					orderHorizontalText(thingAsTextShape);
				else
					orderVerticalText(thingAsTextShape);
				
				return Collections.unmodifiableList(oldSequence);
			}
			else
			{
				// backout
				
				if (value instanceof List)
				{
					List asList = (List) value;
					
					thingAsTextShape.clear();
					thingAsTextShape.addAll(asList);
					
					return null;
				}
				else
				{
					return NO_EFFECT;
				}
			}
		}
		else
		{
			return NO_EFFECT;
		}
	}
	
	public void orderHorizontalText(TextShape thingAsTextShape)
	{
		// 1. clump into horizontal strips
		
		Logger.println("clumping into horizontal strips");
		
		SegmentCatalog thinland = new SegmentCatalog();
		
		for (int loop=0; loop<=thingAsTextShape.size()-1; loop++)
		{
			Shape yolk = thingAsTextShape.actualPerchedInnerShape(loop);
			Rectangle2D yolkBounds = yolk.getBounds2D();
			
			thinland.add(new Segment(yolkBounds.getY(), yolkBounds.getY()+yolkBounds.getHeight()));
		}
		
		// 2. sort left edges from left to right within strips
		
		Logger.println("2. ----------------------------");
		Logger.println("sorting left edges from left to right within strips");
		
		SortedMap [] strips = new SortedMap[thinland.size()];
		
		for (int perchIndex=0; perchIndex<=thingAsTextShape.size()-1; perchIndex++)
		{
			Shape yolk = thingAsTextShape.actualPerchedInnerShape(perchIndex);
			Rectangle2D yolkBounds = yolk.getBounds2D();
			
			int stripIndex = thinland.add(new Segment(yolkBounds.getY(), yolkBounds.getY()+yolkBounds.getHeight()));
			
			Logger.println("stripIndex="+stripIndex);
			
			if (strips[stripIndex]==null)
				strips[stripIndex] = new TreeMap();
			
			strips[stripIndex].put(new Double(yolkBounds.getX()), new Integer(perchIndex));
			
			// ^^ slow, but hopefully reliable
		}
		
		List newSequence = new TypesafeList(Integer.class);
		
		for (int stripIndex=0; stripIndex<=strips.length-1; stripIndex++)
		{
			if (strips[stripIndex]!=null)
				newSequence.addAll(strips[stripIndex].values());
			else
				Logger.println("strips[stripIndex]=null @ index="+stripIndex); // TO DO is this okay?
		}
		
		Logger.println("newSequence: "+newSequence);
		
		final TypesafeList intermediate = new TypesafeList(Perch.class);
		
		for (int loop=0; loop<=newSequence.size()-1; loop++)
		{
			Integer asInteger = (Integer) newSequence.get(loop);
			int asInt = asInteger.intValue();
			intermediate.add(thingAsTextShape.get(asInt));
		}
		
		thingAsTextShape.clear();
		thingAsTextShape.addAll(intermediate);
	}
	
	public void orderVerticalText(TextShape thingAsTextShape)
	{
		// 1. clump into vertical strips
		// 2. sort top edges top to bottom within strips
						
		Logger.println("clumping into vertical strips");
		
		SegmentCatalog thinland = new SegmentCatalog();
		
		for (int loop=0; loop<=thingAsTextShape.size()-1; loop++)
		{
			Shape yolk = thingAsTextShape.actualPerchedInnerShape(loop);
			Rectangle2D yolkBounds = yolk.getBounds2D();
			
			thinland.add(new Segment(yolkBounds.getX(), yolkBounds.getX()+yolkBounds.getWidth()));
		}
		
		// 2. sort top edges from top to bottom within vertical strips
		
		Logger.println("2. ----------------------------");
		Logger.println("sorting left edges from left to right within strips");
		
		SortedMap [] strips = new SortedMap[thinland.size()];
		
		for (int perchIndex=0; perchIndex<=thingAsTextShape.size()-1; perchIndex++)
		{
			Shape yolk = thingAsTextShape.actualPerchedInnerShape(perchIndex);
			Rectangle2D yolkBounds = yolk.getBounds2D();
			
			int stripIndex = thinland.add(new Segment(yolkBounds.getX(), yolkBounds.getX()+yolkBounds.getWidth()));
			
			Logger.println("stripIndex="+stripIndex);
			
			if (strips[stripIndex]==null)
				strips[stripIndex] = new TreeMap();
			
			strips[stripIndex].put(new Double(yolkBounds.getY()), new Integer(perchIndex));
			
			// ^^ slow, but hopefully reliable
		}
		
		List newSequence = new TypesafeList(Integer.class);
		
		for (int stripIndex=strips.length-1; stripIndex>=0; stripIndex--)
			newSequence.addAll(strips[stripIndex].values());
		
		Logger.println("newSequence: "+newSequence);
		
		final TypesafeList intermediate = new TypesafeList(Perch.class);
		
		for (int loop=0; loop<=newSequence.size()-1; loop++)
		{
			Integer asInteger = (Integer) newSequence.get(loop);
			int asInt = asInteger.intValue();
			intermediate.add(thingAsTextShape.get(asInt));
		}
		
		thingAsTextShape.clear();
		thingAsTextShape.addAll(intermediate);
	}
}
