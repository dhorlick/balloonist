/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Selection;

/**
 * @author dhorlick
 */
public class BalloonistPanel extends JPanel
{
	private DrawableDialog drawableDialog;
	private ArtworkTreePanel artworkTreePanel;
	
	public BalloonistPanel()
	{
		initialize();
	}

	private void initialize()
	{
		drawableDialog = new DrawableDialog();
		artworkTreePanel = new ArtworkTreePanel();
		setLayout(new BorderLayout());
		add(drawableDialog, BorderLayout.NORTH);
		add(artworkTreePanel, BorderLayout.CENTER);
	}
	
	public void setArtwork(Artwork designatedArtwork)
	{
		drawableDialog.setArtwork(designatedArtwork);
		artworkTreePanel.setArtwork(designatedArtwork);	
	}
	
	public void setSelection(Selection designatedSelection)
	{
		drawableDialog.setSelection(designatedSelection);
	}
	
	public void setChangeListener(ChangeListener designatedChangeListener)
	{
		drawableDialog.setChangeListener(designatedChangeListener);
	}
	
	public DrawableDialog getDrawableDialog()
	{
		return drawableDialog;
	}
	
	public ArtworkTreePanel getArtworkTreePanel()
	{
		return artworkTreePanel;
	}
}
