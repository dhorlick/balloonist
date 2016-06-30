/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.net.URL;


/**
 *
 * @author dhorlick
 */
public class SourceEdit extends StatefulMultipleEdit
{
	public SourceEdit(URL designatedNewSource)
	{
		super(MENU_TEXT.getString("changeSourceLabel"), designatedNewSource);
	}
	
	public Object setState(Object thing, Object value)
	{
		if (thing==null || value==null || !(value instanceof URL) || !(thing instanceof GraphicResizeable))
			return NO_EFFECT;
		
		GraphicResizeable graphicResizeable = (GraphicResizeable) thing;
		URL url = (URL) value;
		
		URL oldSource = graphicResizeable.getGraphicalContent().getSource();
		graphicResizeable.getGraphicalContent().setSource(url);
		graphicResizeable.getGraphicalContent().refreshFromLinkIfAppropriate();
		
		return oldSource;
	}
}
