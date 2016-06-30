/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class ResetAspectRatioEdit extends StatefulMultipleEdit
{	
	private boolean targetWidth;
	
	private final static Object RESET = new Object();
	
	public ResetAspectRatioEdit(String designatedTitle, boolean designatedTargetWidth)
	{
		super(designatedTitle, RESET);
		targetWidth = designatedTargetWidth;
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof GraphicResizeable)
		{
			GraphicResizeable thingAsGraphicResizeable = (GraphicResizeable) thing;
			
			if (value==RESET)
			{
				final double aspectRatio = thingAsGraphicResizeable.getGraphicalContent().computeAspectRatio();
				
				if (targetWidth)
				{
					double oldWidth = thingAsGraphicResizeable.getWidth();
					thingAsGraphicResizeable.setWidth(thingAsGraphicResizeable.getHeight()/aspectRatio);
					
					return new Double(oldWidth);
				}
				else
				{
					double oldHeight = thingAsGraphicResizeable.getHeight();
					thingAsGraphicResizeable.setHeight(thingAsGraphicResizeable.getWidth()*aspectRatio);
					
					return new Double(oldHeight);
				}
			}
			else if (value instanceof Number && value!=null)
			{
				Number valueAsNumber = (Number) value;
				
				if (targetWidth)
				{
					thingAsGraphicResizeable.setWidth(valueAsNumber.doubleValue());
				}
				else
				{
					thingAsGraphicResizeable.setHeight(valueAsNumber.doubleValue());
				}
				
				return RESET;
			}
		}
		
		return NO_EFFECT;
	}
	
	public boolean isTargetWidth()
	{
		return targetWidth;
	}
}
