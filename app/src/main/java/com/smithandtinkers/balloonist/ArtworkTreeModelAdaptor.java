/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.mvc.ListTreeModelAdaptor;

/**
 * @author dhorlick
 */
public class ArtworkTreeModelAdaptor extends ListTreeModelAdaptor implements TreeModel
{
	private Artwork artwork;

	private TreeModelListener apertureFixingTreeModelListener = new TreeModelListener() {

		public void treeNodesChanged(TreeModelEvent treeModelEvent)
		{
			ArtworkTreeModelAdaptor.this.applyApertureQuantityFromBaseSill();
		}

		public void treeNodesInserted(TreeModelEvent treeModelEvent)
		{
			ArtworkTreeModelAdaptor.this.applyApertureQuantityFromBaseSill();
		}

		public void treeNodesRemoved(TreeModelEvent treeModelEvent)
		{
			ArtworkTreeModelAdaptor.this.applyApertureQuantityFromBaseSill();
		}

		public void treeStructureChanged(TreeModelEvent treeModelEvent)
		{
			ArtworkTreeModelAdaptor.this.applyApertureQuantityFromBaseSill();
		}
	};
	
	public ArtworkTreeModelAdaptor()
	{	
		getTreeModelSupport().addTreeModelListener(apertureFixingTreeModelListener);			
	}
	
	public ArtworkTreeModelAdaptor(Artwork designatedArtwork)
	{
		// System.out.println("ATMA(Artwork): starting");
		this();
		setArtwork(designatedArtwork);
		// System.out.println("ATMA(Artwork): ending");
	}
	
	public void setArtwork(Artwork designatedArtwork)
	{
		if (designatedArtwork!=artwork)
		{
			Artwork oldArtwork = artwork;
			artwork = designatedArtwork;
			// System.out.println("ATMA.setArtwork: ending");
			
			if (artwork!=null)
			{		
				artwork.getSill().setTreeModelSupport(getTreeModelSupport());
			}
			
			if (oldArtwork!=null && oldArtwork.getSill()!=null)
			{
				oldArtwork.getSill().setTreeModelSupport(null);
			}
		}
	}
	
	public Artwork getArtwork()
	{
		return artwork;
	}
	
	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot()
	{
		// System.out.println("getRoot: starting");
		
		if (artwork==null)
		{
			// System.out.println("getRoot: returning null");
			return null;
		}
		
		// System.out.print("getRoot: returning: ");
		// System.out.println(artwork.getSill());
		return artwork.getSill();
	}

	private void applyApertureQuantityFromBaseSill()
	{
		if (getArtwork()!=null && getArtwork().getLayout()!=null && getArtwork().getSill()!=null)
			artwork.getLayout().setApertureQuantity(artwork.getSill().size());
	}
}
