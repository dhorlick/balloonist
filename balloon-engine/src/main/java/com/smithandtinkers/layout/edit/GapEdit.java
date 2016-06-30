/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.Dimension;

/**
 *
 * @author dhorlick
 */
public class GapEdit extends StatefulMultipleEdit
{
	public GapEdit(Layout designatedLayout, double designatedNewHorizontalGap, double designatedNewVerticalGap)
	{
		super(MENU_TEXT.getString("changeGapEdit"), 
				new Flat(designatedNewHorizontalGap, designatedNewVerticalGap));
		
		add(designatedLayout);
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof Layout && value instanceof Flat)
		{
			Layout layout = (Layout) thing;
			Flat flat = (Flat) value;
			
			Flat oldFlat = new Flat(layout.getHorizontalGapInPoints(), layout.getVerticalGapInPoints());
			
			layout.setHorizontalGapInPoints(flat.getHorizontalValue());
			layout.setVerticalGapInPoints(flat.getVerticalValue());
			
			return oldFlat;
		}
		
		return NO_EFFECT;
	}
	
	public static class Flat
	{
		private double horizontalValue;
		private double verticalValue;
		
		public Flat(double designatedHorizontalValue, double designatedVerticalValue)
		{
			horizontalValue = designatedHorizontalValue;
			verticalValue = designatedVerticalValue;
		}
		
		public double getHorizontalValue()
		{
			return horizontalValue;
		}

		public void setHorizontalValue(double designatedHorizontalValue)
		{
			horizontalValue = designatedHorizontalValue;
		}

		public double getVerticalValue()
		{
			return verticalValue;
		}

		public void setVerticalValue(double designatedVerticalValue)
		{
			verticalValue = designatedVerticalValue;
		}
	}
}
