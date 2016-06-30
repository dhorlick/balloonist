/*
 Copyleft Feb 26, 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.event.*;
import javax.swing.undo.UndoableEdit;

import com.smithandtinkers.geom.*;
import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.gui.LabeledQuantityField.UnitChoice;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Balloon;
import com.smithandtinkers.layout.GraphicResizeable;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.mvc.SelfContainedTreeSelectionEvent;
import com.smithandtinkers.mvc.SelfContainedTreeSelectionListener;
import com.smithandtinkers.util.LocaleFriend;
import com.smithandtinkers.util.Named;

/**
 * @author dhorlick
 */
public class DrawableDialog extends JPanel implements SelfContainedTreeSelectionListener, ChangeListener
{
	private Artwork artwork;
	private Selection selection;
	
	private LayoutPropsPanel layoutPropsPanel;
	private GraphicResizeablePropsPanel graphicResizeablePropsPanel;
	private BalloonPropsPanel balloonPropsPanel;
	private SuperEllipsePerchPropsPanel superEllipsePerchPropsPanel;
	private StemPropsPanel stemPropsPanel;
	private ParallelogramPerchPropsPanel parallelogramPerchPropsPanel;
	private NamedAbstractPropsPanel namePropPanel;
	private SillPropsPanel sillPropsPanel;
	private PagePropsPanel pagePropsPanel;
	
	/**
	 * A change listener to listen to this dialog's Controls.
	 */
	private ChangeListener changeListener;
	
	private UndoableEditListener listenerToNotifyAboutUndoableEdits;
	
	public final static ResourceBundle DIALOG_TEXT = ResourceBundle.getBundle("resources/text/dialog");
	
	public final static UnitChoice POINTS = new LabeledQuantityField.UnitChoice("points", 1.0);
	public final static UnitChoice PICAS = new LabeledQuantityField.UnitChoice("picas", 12.0);
	public final static UnitChoice CM = new LabeledQuantityField.UnitChoice("cm", 72.0/2.54);
	public final static UnitChoice INCHES = new LabeledQuantityField.UnitChoice("inches", 72.0);
	
	public final static LabeledQuantityField.UnitChoiceList UNITS = new LabeledQuantityField.UnitChoiceList();
	private JPanel cards;
	
	static final int FIGURES_AFTER_DECIMAL_POINT = 4;
	static final int TEXT_FIELD_COLUMNS = 4;
	static final int WIDGET_FONT_SIZE = LocaleFriend.determineAppropriateLabelFontSize();
	
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	static
	{
		UNITS.add(POINTS);
		UNITS.add(PICAS);
		UNITS.add(CM);
		UNITS.add(INCHES);
		
		UNITS.setSelectedItem(POINTS);
		
		UNITS.addListDataListener(new ListDataListener()
		{
			public void intervalAdded(ListDataEvent listDataEvent)
			{
			}

			public void intervalRemoved(ListDataEvent listDataEvent)
			{
			}

			public void contentsChanged(ListDataEvent listDataEvent)
			{
				// TODO jog rulers
			}			
		});
	}

	public void invalidate()
	{
	}
	
	public DrawableDialog()
	{
		initialize();
	}
	
	private void initialize()
	{
		cards = new JPanel(new CardLayout());
		
		layoutPropsPanel = new LayoutPropsPanel();
		layoutPropsPanel.setDrawableDialog(this);
		cards.add(layoutPropsPanel, LayoutPropsPanel.class.getName());
		
		balloonPropsPanel = new BalloonPropsPanel();
		balloonPropsPanel.setDrawableDialog(this);
		cards.add(balloonPropsPanel, BalloonPropsPanel.class.getName());
		
		graphicResizeablePropsPanel = new GraphicResizeablePropsPanel();
		graphicResizeablePropsPanel.setDrawableDialog(this);
		cards.add(graphicResizeablePropsPanel, GraphicResizeablePropsPanel.class.getName());
		
		superEllipsePerchPropsPanel = new SuperEllipsePerchPropsPanel();
		superEllipsePerchPropsPanel.setDrawableDialog(this);
		cards.add(superEllipsePerchPropsPanel, SuperEllipsePerchPropsPanel.class.getName());
		
		stemPropsPanel = new StemPropsPanel();
		stemPropsPanel.setDrawableDialog(this);
		cards.add(stemPropsPanel, StemPropsPanel.class.getName());
		
		parallelogramPerchPropsPanel = new ParallelogramPerchPropsPanel();
		parallelogramPerchPropsPanel.setDrawableDialog(this);
		cards.add(parallelogramPerchPropsPanel, ParallelogramPerchPropsPanel.class.getName());
		
		namePropPanel = new NamedAbstractPropsPanel();
		namePropPanel.setDrawableDialog(this);
		cards.add(namePropPanel, NamedAbstractPropsPanel.class.getName());
		
		sillPropsPanel = new SillPropsPanel();
		sillPropsPanel.setDrawableDialog(this);
		cards.add(sillPropsPanel, SillPropsPanel.class.getName());
		
		pagePropsPanel = new PagePropsPanel();
		pagePropsPanel.setDrawableDialog(this);
		cards.add(pagePropsPanel, PagePropsPanel.class.getName());
		
		add(cards);
		
		setVisible(true);
	}
	
	public CardLayout getCardLayout()
	{
		return (CardLayout) cards.getLayout();
	}

	public Selection getSelection()
	{
		return selection;
	}
	
	public void setSelection(Selection designatedSelection)
	{
		selection = designatedSelection;
	}
	
	public Artwork getArtwork()
	{
		return artwork;
	}
	
	public void setArtwork(Artwork designatedArtwork)
	{
		if (artwork==designatedArtwork)
			return;
		
		artwork = designatedArtwork;
		
		stateChanged();
	}
	
	private void stateChanged()
	{
		if (layoutPropsPanel!=null)
			layoutPropsPanel.stateChanged();
		
		if (balloonPropsPanel!=null)
			balloonPropsPanel.stateChanged();
		
		if (graphicResizeablePropsPanel!=null)
			graphicResizeablePropsPanel.stateChanged();
		
		if (superEllipsePerchPropsPanel!=null)
			superEllipsePerchPropsPanel.stateChanged();
		
		if (stemPropsPanel!=null)
			stemPropsPanel.stateChanged();
		
		if (parallelogramPerchPropsPanel!=null)
			parallelogramPerchPropsPanel.stateChanged();
		
		if (namePropPanel!=null)
			namePropPanel.stateChanged();
		
		if (sillPropsPanel!=null)
			sillPropsPanel.stateChanged();
		
		if (pagePropsPanel!=null)
			pagePropsPanel.stateChanged();
		
		// System.out.println("widest: "+determineWidestCard());
		// System.out.println("highest: "+determineHighestCard());
	}

	public LayoutPropsPanel getLayoutPropsPanel()
	{
		return layoutPropsPanel;
	}
	
	public BalloonPropsPanel getBalloonPropsPanel()
	{
		return balloonPropsPanel;
	}

	/**
	 * @param designatedChangeListener
	 */
	public void setChangeListener(ChangeListener designatedChangeListener) // TODO only message the active pane
	{
		changeListener = designatedChangeListener;
		
		if (layoutPropsPanel!=null)
			layoutPropsPanel.setChangeListener(designatedChangeListener);
		
		if (balloonPropsPanel!=null)
			balloonPropsPanel.setChangeListener(designatedChangeListener);
		
		if (graphicResizeablePropsPanel!=null)
			graphicResizeablePropsPanel.setChangeListener(designatedChangeListener);
		
		if (superEllipsePerchPropsPanel!=null)
			superEllipsePerchPropsPanel.setChangeListener(designatedChangeListener);
		
		if (stemPropsPanel!=null)
			stemPropsPanel.setChangeListener(designatedChangeListener);
		
		if (parallelogramPerchPropsPanel!=null)
			parallelogramPerchPropsPanel.setChangeListener(designatedChangeListener);
		
		if (namePropPanel!=null)
			namePropPanel.setChangeListener(designatedChangeListener);
		
		if (sillPropsPanel!=null)
			sillPropsPanel.setChangeListener(designatedChangeListener);
		
		if (pagePropsPanel!=null)
			pagePropsPanel.setChangeListener(designatedChangeListener);
	}
	
	public void revealAppropriateCard()
	{
		AbstractPropsPanel mostAppropriateCard = determineMostAppropriateCard();
		mostAppropriateCard.stateChanged();
		mostAppropriateCard.revealed();
		Class mostAppropriate = mostAppropriateCard.getClass();
		// System.out.println("mostAppropriate="+mostAppropriate);
		getCardLayout().show(cards, mostAppropriate.getName());
		
		if (changeListener!=null)
		 	changeListener.stateChanged(CHANGE_EVENT);
		
		cards.invalidate();
		repaint();
	}
	
	/**
	 * @return the Class of an AbstractPropsPane implementation appropriate to the most
	 * general selected type.
	 */
	private AbstractPropsPanel determineMostAppropriateCard()
	{
		if (selection!=null)
		{
			Class mostGeneralClass = selection.mostGeneralConstituentType();
			
			// System.out.println("mostGeneralClass="+mostGeneralClass);
			
			if (mostGeneralClass==null)
				return layoutPropsPanel;
			else if (Balloon.class.isAssignableFrom(mostGeneralClass))
				return balloonPropsPanel;
			else if (GraphicResizeable.class.isAssignableFrom(mostGeneralClass))
				return graphicResizeablePropsPanel;
			else if (SuperEllipsePerch.class.isAssignableFrom(mostGeneralClass))
				return superEllipsePerchPropsPanel;
			else if (Stem.class.isAssignableFrom(mostGeneralClass))
				return stemPropsPanel;
			else if (ParallelogramPerch.class.isAssignableFrom(mostGeneralClass))
				return parallelogramPerchPropsPanel;
			else if (Sill.class.isAssignableFrom(mostGeneralClass))
			{
				if (selection.getSelectedItemsCount()==1 && ((Sill)selection.getSelectedItem(0)).getParent()==null )
					return pagePropsPanel;
				
				return sillPropsPanel;
			}
			else if (Named.class.isAssignableFrom(mostGeneralClass))
				return namePropPanel;
			else
				return layoutPropsPanel;
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see com.smithandtinkers.mvc.SelfContainedTreeSelectionListener#valueChanged(com.smithandtinkers.mvc.SelfContainedTreeSelectionEvent)
	 */
	public void valueChanged(SelfContainedTreeSelectionEvent e)
	{
		revealAppropriateCard();
	}
	
	/**
	 * @return Something that listens to this dialog's controls.
	 * 
	 * Note that this dialog is in turn also itself a change listener. It listens to model changes that
	 * could affect the View of this dialog. These View changes should not continue propagating.
	 */
	public ChangeListener getChangeListener()
	{
		return changeListener;
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		stateChanged();
	}

	public UndoableEditListener getListenerToNotifyAboutUndoableEdits()
	{
		return listenerToNotifyAboutUndoableEdits;
	}

	public void setListenerToNotifyAboutUndoableEdits(UndoableEditListener designatedListenerToNotifyAboutUndoableEdits)
	{
		listenerToNotifyAboutUndoableEdits = designatedListenerToNotifyAboutUndoableEdits;
	}
	
	public void announceEdit(Object source, UndoableEdit theEdit)
	{
		if (source==null)
			source=this;
		
		if (getListenerToNotifyAboutUndoableEdits()!=null)
			getListenerToNotifyAboutUndoableEdits().undoableEditHappened(new UndoableEditEvent(source, theEdit));
	}
	
	/**
	 * useful for debugging
	 */
	public Component determineWidestCard()
	{
		double maxWidth = 0.0;
		Component widest = null;
		
		// System.out.println("cards.getComponentCount()="+cards.getComponentCount());
		
		for (int loop=0; loop<=cards.getComponentCount()-1; loop++)
		{
			double currentWidth = cards.getComponent(loop).getPreferredSize().getWidth();
			
			// System.out.println("currentWidth="+currentWidth);
			
			if (currentWidth > maxWidth)
			{
				// System.out.println("\twider");
				
				maxWidth = currentWidth;
				widest = cards.getComponent(loop);
			}
		}
		
		return widest;
	}

	/**
	 * useful for debugging
	 */
	public Component determineHighestCard()
	{
		double maxHeight = 0.0;
		Component highest = null;
		
		// System.out.println("cards.getComponentCount()="+cards.getComponentCount());
		
		for (int loop=0; loop<=cards.getComponentCount()-1; loop++)
		{
			double currentHeight = cards.getComponent(loop).getPreferredSize().getHeight();
			
			// System.out.println("currentWidth="+currentWidth);
			
			if (currentHeight > maxHeight)
			{
				// System.out.println("\twider");
				
				maxHeight = currentHeight;
				highest = cards.getComponent(loop);
			}
		}
		
		return highest;
	}

}
