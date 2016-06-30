/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.average.HeinParameterAverager;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.util.Iterator;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.Box;

import com.smithandtinkers.geom.AbstractSuperEllipse;
import com.smithandtinkers.geom.PunctuatedSuperEllipse;
import com.smithandtinkers.geom.RuffleableSuperEllipse;
import com.smithandtinkers.geom.SuperEllipsePerch;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.Crowd;
import com.smithandtinkers.layout.edit.HeinParameterEdit;
import com.smithandtinkers.util.ResourceFriend;


/**
 * @author dhorlick
 */
public class SuperEllipsePerchPropsPanel extends ResizeablePropsPanel
{
	private HeinParameterSlider heinParameterLabeledSlider;
	// private MysteryStrip ruffleStrip;
	private RuffleContainer ruffleContainer;
	private JTabbedPane tabbedPane;
	
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	private static final ImageIcon RECTANGLE_ICON = ResourceFriend.retrieveImageIcon("resources/icons/rectangle.png");
	private static final ImageIcon OVAL_ICON = ResourceFriend.retrieveImageIcon("resources/icons/oval.png");
	
	
	protected void initialize()
	{
		super.initialize();
		
		tabbedPane = new JTabbedPane();
		
		heinParameterLabeledSlider = new HeinParameterSlider();
		
		heinParameterLabeledSlider.getSlider().addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					double newHeinParam = heinParameterLabeledSlider.associatedHeinParameter();
					
					if (AbstractSuperEllipse.wouldProspectiveHeinParameterBeAllowed(newHeinParam))
					{
						HeinParameterEdit heinParameterEdit = new HeinParameterEdit(newHeinParam);
						heinParameterEdit.addSelectablesFrom(getDrawableDialog().getSelection());
						heinParameterEdit.execute();
						
						getDrawableDialog().announceEdit(this, heinParameterEdit);
					}

					if (getChangeListener()!=null)
						getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		);
		// add(heinParameterLabeledSlider);
		heinParameterLabeledSlider.getSlider().addMouseListener(new MouseAdapter()
			{
				public void mouseReleased(MouseEvent e)
				{
					Crowd.reLayoutItemsWithin(getDrawableDialog().getSelection().getSelectedItems());
				}
			}
		);
		
		JPanel heinBox = new JPanel();
		heinBox.setLayout(new BorderLayout());
		heinBox.add(heinParameterLabeledSlider, BorderLayout.CENTER);
		tabbedPane.addTab(getDrawableDialog().DIALOG_TEXT.getString("rectangularnessTitle"), heinBox);
		
		ruffleContainer = new RuffleContainer();
		ruffleContainer.addUndoableEditListener(new UndoableEditListener()
		{
			public void undoableEditHappened(javax.swing.event.UndoableEditEvent e)
			{
				if (getDrawableDialog().getListenerToNotifyAboutUndoableEdits()!=null)
					getDrawableDialog().getListenerToNotifyAboutUndoableEdits().undoableEditHappened(e);
			}
		});
		ruffleContainer.addContainerLevelChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent changeEvent)
			{
				if (getDrawableDialog().getChangeListener()!=null)
					getDrawableDialog().getChangeListener().stateChanged(changeEvent);
			}			
		});
		
		Box rufflesBox = Box.createVerticalBox();
		
		JPanel labelBox = new JPanel();
		labelBox.setLayout(new GridLayout(0,2));
		// labelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel preferredWidthLabel = new JLabel(getDrawableDialog().DIALOG_TEXT.getString("preferredWidthLabel"));
		JLabel heightLabel = new JLabel(getDrawableDialog().DIALOG_TEXT.getString("heightLabel"));
		
		// preferredWidthLabel.setHorizontalAlignment(SwingConstants.LEFT);
		// heightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		labelBox.add(preferredWidthLabel);
		labelBox.add(heightLabel);
		rufflesBox.add(labelBox);
		
		// box.setMaximumSize(new Dimension(250, 220));
		rufflesBox.add(ruffleContainer);
		// box.add(ruffleTable);
		tabbedPane.addTab(getDrawableDialog().DIALOG_TEXT.getString("rufflesTitle"), rufflesBox);
		tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		add(tabbedPane);
	}

	/**
	 * @see com.smithandtinkers.balloonist.AbstractPropsPanel#revealed()
	 */
	public void revealed()
	{
		super.revealed();
	}
	
	public boolean anyOfTheSelectedItemsHaveRuffles()
	{
		Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			
			if (item instanceof SuperEllipsePerch)
			{
				SuperEllipsePerch asPerch = (SuperEllipsePerch) item;
				item = asPerch.getPunctedSuperEllipse();
			}
			
			if (item instanceof PunctuatedSuperEllipse)
			{
				PunctuatedSuperEllipse puncted = (PunctuatedSuperEllipse) item;
					
				if (puncted.isRuffled())
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected void stateChanged() // TODO pass event source so we can ignore ones that this control precipitated
	{
		super.stateChanged();
		
		if (getDrawableDialog().getSelection()==null)
			return;
		
		((SilenceableSlider)heinParameterLabeledSlider.getSlider()).setSilent(true);
		heinParameterLabeledSlider.associateHeinParameter(getDrawableDialog().getSelection());
		((SilenceableSlider)heinParameterLabeledSlider.getSlider()).setSilent(false);
		
		RuffleableSuperEllipse ruffled = findFirstSelectedRuffledSuperEllipse();
		
		if (ruffled!=null)
		{
			// ruffleStrip.setModel(ruffled.getRuffles());
			
			if (ruffleContainer.getModel()!=ruffled.getRuffles())
			{
				ruffleContainer.setRuffledSuperEllipse(ruffled);
			}
			else
			{
				ruffleContainer.stateChanged();
			}
			
			getDrawableDialog().getSelection().setAuxiliarySelectedItems(ruffleContainer.selectedModelItems());
		}
	}
	
	public SuperEllipsePerch findFirstSelectedSuperEllipsePerch()
	{
		if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
			return null;
		
		return (SuperEllipsePerch)getDrawableDialog().getSelection().findFirst(SuperEllipsePerch.class);
	}
	
	public RuffleableSuperEllipse findFirstSelectedRuffledSuperEllipse()
	{
		SuperEllipsePerch perch = findFirstSelectedSuperEllipsePerch();

		if (perch==null)
			return null;
		
		// System.out.println("findFirstSelectedRuffledSuperEllipse: perch.getUnperched()="+perch.getUnperched().getClass().getName());

		if (perch.getUnperched() instanceof PunctuatedSuperEllipse)
		{
			PunctuatedSuperEllipse puncted = (PunctuatedSuperEllipse) perch.getUnperched();
			return puncted.getRuffledSuperEllipse();
		}
		else
		{
			return null;
		}
	}
	
	public static class HeinParameterSlider extends LabeledSlider
	{
		private HeinParameterAverager averager = new HeinParameterAverager();
		
		public HeinParameterSlider()
		{
			super(new JLabel(RECTANGLE_ICON), new JLabel(OVAL_ICON), new SilenceableSlider());
		}
		
		/**
		 * Determines and returns the hein parameter associated with the slider's current value.
		 */
		public double associatedHeinParameter()
		{
			return Averager.mapValueFromSlider(getSlider(), AbstractSuperEllipse.minAllowableHeinParameter(), AbstractSuperEllipse.maxAllowableHeinParameter());
		}
		
		/**
		 * Sets the hein parameter from a selection.
		 */
		public void associateHeinParameter(Selection selection)
		{
			averager.averageTo(selection.iterateOverSelectedItems(), getSlider(), AbstractSuperEllipse.minAllowableHeinParameter(), AbstractSuperEllipse.maxAllowableHeinParameter());
		}

		public void associateHeinParameter(double designatedDefaultHeinParameter)
		{
			Averager.mapValueToSlider(designatedDefaultHeinParameter, getSlider(), AbstractSuperEllipse.minAllowableHeinParameter(), AbstractSuperEllipse.maxAllowableHeinParameter());
		}
	}
}
