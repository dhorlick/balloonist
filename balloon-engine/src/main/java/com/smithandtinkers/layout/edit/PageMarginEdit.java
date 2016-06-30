/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.Rectangle;

/**
 *
 * @author dhorlick
 */
public class PageMarginEdit extends StatefulMultipleEdit
{
	public PageMarginEdit(Artwork designatedArtwork, double designatedNewHorizontalPageMargin, double designatedNewVerticalPageMargin)
	{
		super(MENU_TEXT.getString("changePageMarginLabel"), new GapEdit.Flat(designatedNewHorizontalPageMargin, designatedNewVerticalPageMargin));
		add(designatedArtwork);
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof Artwork && value instanceof GapEdit.Flat)
		{
			Artwork artwork = (Artwork) thing;
			GapEdit.Flat flat = (GapEdit.Flat) value;
			
			GapEdit.Flat oldFlat = new GapEdit.Flat(artwork.getLayout().getHorizontalPageMarginInPoints(),
					artwork.getLayout().getVerticalPageMarginInPoints());
			
			artwork.getLayout().setHorizontalPageMarginInPoints(flat.getHorizontalValue());
			artwork.getLayout().setVerticalPageMarginInPoints(flat.getVerticalValue());
			
			
			// resize layout to maintain overall page size
			
			artwork.getLayout().setBounds(new Rectangle(0, 0, 
					(int)(artwork.getEnclosure().getWidth()-2.0*flat.getHorizontalValue()), 
					(int)(artwork.getEnclosure().getHeight()-2.0*flat.getVerticalValue())));
			
			return oldFlat;
		}
		
		return NO_EFFECT;
	}
}
