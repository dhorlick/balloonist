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
public class RuffleWidthEdit extends StatefulMultipleEdit
{
	public RuffleWidthEdit(double newWidthInPoints)
	{
		super(MENU_TEXT.getString("changeRuffleWidthLabel"), new Double(newWidthInPoints));
	}

	public Object setState(Object thing, Object value)
	{
		RuffleDie ruffleDie = (RuffleDie) thing;
		double oldWidth = ruffleDie.getPreferredWidthInPoints();
		double newWidth = ((Number)value).doubleValue();
		ruffleDie.setPreferredWidthInPoints(newWidth);
		return new Double(oldWidth);
	}
}
