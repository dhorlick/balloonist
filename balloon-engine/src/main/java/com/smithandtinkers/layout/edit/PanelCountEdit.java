/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

/**
 *
 * @author dhorlick
 */
public class PanelCountEdit extends PresentableEdit
{
	private Artwork artwork;
	
	private int newPanelCount;
	private int oldPanelCount;
	
	public PanelCountEdit(Artwork designatedArtwork, int designatedNewPanelCount)
	{
		super(MENU_TEXT.getString("changePanelCountLabel"));
		artwork = designatedArtwork;
		newPanelCount = designatedNewPanelCount;
	}

	public boolean hasEffect()
	{
		if (artwork!=null && artwork.getLayout()!=null && newPanelCount!=0)
		{
			return true;
		}
		
		return false;
	}

	public boolean execute()
	{
		if (hasEffect())
		{
			oldPanelCount = artwork.getLayout().getApertureQuantity();
			artwork.getLayout().setApertureQuantity(newPanelCount);
			// artwork.enclose();
			
			return true;
		}
		
		return false;
	}

	public boolean backout()
	{
		if (hasEffect())
		{
			artwork.getLayout().setApertureQuantity(oldPanelCount);
			// artwork.enclose();
			
			return true;
		}
		
		return false;
	}
}
