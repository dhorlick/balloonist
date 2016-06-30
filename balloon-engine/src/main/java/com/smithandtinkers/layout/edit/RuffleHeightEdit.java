/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.geom.RuffleDie;
import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class RuffleHeightEdit extends StatefulMultipleEdit
{
	public RuffleHeightEdit(double newHeight)
	{
		super(MENU_TEXT.getString("changeRuffleHeightLabel"), new Double(newHeight));
	}

	public Object setState(Object thing, Object value)
	{
		RuffleDie ruffleDie = (RuffleDie) thing;
		double oldHeight = ruffleDie.getHeightInPoints();
		double newHeight = ((Number)value).doubleValue();
		ruffleDie.setHeightInPoints(newHeight);
		
		return new Double(oldHeight);
	}
}
