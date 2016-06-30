/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.gui.RearrangeableTree;


/**
 * @author dhorlick
 */
public class ArtworkTreePanel extends JPanel
{
	private RearrangeableTree rearrangeableTree;
	
	public ArtworkTreePanel()
	{
		super();
		initialize();
	}

	private void initialize()
	{
		setLayout(new java.awt.BorderLayout());
		rearrangeableTree = new RearrangeableTree();
		rearrangeableTree.setShowsRootHandles(true);
		rearrangeableTree.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); // TODO better: use the gray or stripedness of the surrounding region
		JScrollPane scrollPane = new JScrollPane(rearrangeableTree);
		add(scrollPane);
		// add(scrollPane, java.awt.BorderLayout.CENTER);
		final Dimension DIMENSION = new Dimension(349, 600);
		scrollPane.setMaximumSize(DIMENSION);
		scrollPane.setPreferredSize(DIMENSION);
	}
	
	public void setArtwork(Artwork designatedArtwork)
	{
		ArtworkTreeModelAdaptor adaptor = new ArtworkTreeModelAdaptor(designatedArtwork);
		setArtworkTreeModelAdaptor(adaptor);
	}

	private void setArtworkTreeModelAdaptor(ArtworkTreeModelAdaptor adaptor)
	{
		// System.out.println("ATP.setArtworkTreeModelAdaptor: starting");
		rearrangeableTree.setModel(adaptor);
		// System.out.println("ATP.setArtworkTreeModelAdaptor: done");
	}

	public RearrangeableTree getTree()
	{
		// if (rearrangeableTree!=null)
		// 	System.out.println("rearrangeableTree.getSize()="+rearrangeableTree.getSize());
		return rearrangeableTree;
	}
}
