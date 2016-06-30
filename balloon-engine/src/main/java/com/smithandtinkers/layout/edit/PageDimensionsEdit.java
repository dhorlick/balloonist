/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author dhorlick
 */
public class PageDimensionsEdit extends StatefulMultipleEdit
{
	public PageDimensionsEdit(Artwork designatedArtwork, Dimension designatedNewDimension)
	{
		super(MENU_TEXT.getString("changePageDimensionsLabel"), designatedNewDimension);
		add(designatedArtwork);
	}

	public Object setState(Object thing, Object value)
	{
		if (thing instanceof Artwork && value instanceof Dimension)
		{
			Artwork artwork = (Artwork) thing;
			
			Dimension oldDim = new Dimension();
			Dimension dim = (Dimension) value;
			
			oldDim.setSize(artwork.getEnclosure());
			artwork.setEnclosure(dim);

			
			// re-shape layout
			
			artwork.getLayout().setBounds(new Rectangle(0, 0, 
				(int)(dim.getWidth()-2.0*artwork.getLayout().getHorizontalPageMarginInPoints()), 
				(int)(dim.getHeight()-2.0*artwork.getLayout().getVerticalPageMarginInPoints())));
			
			
			return oldDim;
		}
		
		return NO_EFFECT;
	}
}
