/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.smithandtinkers.geom.Parallelogram;
import com.smithandtinkers.geom.ParallelogramPerch;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.Crowd;
import com.smithandtinkers.layout.edit.InclinationEdit;
import com.smithandtinkers.util.ResourceFriend;


/**
 *
 * @author dhorlick
 */
public class ParallelogramPerchPropsPanel extends InteractivePerchPropsPanel
{
	private static final ImageIcon REVERSE_PARALLELOGRAM_ICON = ResourceFriend.retrieveImageIcon("resources/icons/parallelogram-reverse.png");
	private static final ImageIcon PARALLELOGRAM_ICON = ResourceFriend.retrieveImageIcon("resources/icons/parallelogram.png");
	
	private LabeledSlider inclinationSlider;
	private SilenceableSlider silenceableSlider;
	private boolean startNewInclinationEdit;
	
	public ParallelogramPerchPropsPanel()
	{
		super();
	}
	
	protected void initialize()
	{
		super.initialize();
		
		silenceableSlider = new SilenceableSlider();
		silenceableSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				reLayoutInclineds();
				startNewInclinationEdit = true;
			}
		});
		inclinationSlider = new LabeledSlider(new JLabel(REVERSE_PARALLELOGRAM_ICON), new JLabel(PARALLELOGRAM_ICON), silenceableSlider);
		inclinationSlider.getSlider().addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					for (int loop=0; loop<=getDrawableDialog().getSelection().getSelectedItemsCount()-1; loop++)
					{
						Object item = getDrawableDialog().getSelection().getSelectedItem(loop);
						
						Parallelogram parallelogram = null;
						
						if (item instanceof ParallelogramPerch)
						{
							ParallelogramPerch itemAsParallelogramPerch = (ParallelogramPerch) item;
							parallelogram = itemAsParallelogramPerch.getParallelogram();
						}
						else if (item instanceof Parallelogram)
						{
							parallelogram = (Parallelogram) item;
						}
						
						if (parallelogram != null)
						{
							double sliderFraction = (double)(inclinationSlider.getSlider().getMaximum()-inclinationSlider.getSlider().getValue())
							/(double)(inclinationSlider.getSlider().getMaximum()-inclinationSlider.getSlider().getMinimum());
							
							double newinclination = parallelogram.maxAllowableInclination() - sliderFraction * (parallelogram.maxAllowableInclination()-parallelogram.minAllowableInclination());
							
							// System.out.println("newinclination="+newinclination);
							
							if (parallelogram.wouldProspectiveInclinationBeAllowed(newinclination))
							{
								InclinationEdit inclinationEdit = new InclinationEdit(newinclination);
								inclinationEdit.addSelectablesFrom(getDrawableDialog().getSelection());
								
								if (startNewInclinationEdit)
								{
									inclinationEdit.setIsolate(true);
									// System.out.println("isolating new inclination edit: "+inclinationEdit.isIsolate());
									startNewInclinationEdit = false;
								}
								
								if (inclinationEdit.execute())
								{
									getDrawableDialog().announceEdit(this, inclinationEdit);
									
									if (getChangeListener()!=null)
										getChangeListener().stateChanged(CHANGE_EVENT);
								}
							}
						}
					}
				}
			}
		);
		
		add(inclinationSlider);
		
		Box straightenerBox = Box.createHorizontalBox();
		javax.swing.Action straightenAction = new AbstractAction(PossibleAction.DIALOG_TEXT.getString("straightenLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				InclinationEdit inclinationEdit = new InclinationEdit(0);
				
				inclinationEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				
				if (inclinationEdit.execute())
				{
					getDrawableDialog().announceEdit(ParallelogramPerchPropsPanel.this, inclinationEdit);
					reLayoutInclineds();
				}
				
				Averager.setSliderFraction(inclinationSlider.getSlider(), 0.0);
					// ^^ TODO this shouldn't be necessary... the parallelogram is supposed to broadcast its changes to this panel as a listener
			}
		};
		final JButton straightenerButton = new JButton(straightenAction);
		straightenerBox.add(Box.createHorizontalGlue());
		straightenerBox.add(straightenerButton);
		straightenerBox.add(Box.createHorizontalGlue());
		straightenerBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		straightenerBox.setAlignmentY(Component.TOP_ALIGNMENT);
		add(straightenerBox);
	}

	protected void stateChanged() // TODO pass event source so we can ignore ones that this control precipitated
	{
		super.stateChanged();
		
		int parallelograms = 0;
		double totalInclination = 0.0;
		
		Parallelogram mostRecentParallelogram = null;
		
		for (int loop=0; loop<=getDrawableDialog().getSelection().getSelectedItemsCount()-1; loop++)
		{
			Parallelogram parallelogram = null;
			
			if (getDrawableDialog().getSelection().getSelectedItem(loop) instanceof ParallelogramPerch)
			{
				ParallelogramPerch parallelogramPerch = (ParallelogramPerch) getDrawableDialog().getSelection().getSelectedItem(loop);
				parallelogram = parallelogramPerch.getParallelogram();
			}
			else if (getDrawableDialog().getSelection().getSelectedItem(loop) instanceof Parallelogram)
			{
				parallelogram = (Parallelogram) getDrawableDialog().getSelection().getSelectedItem(loop);
			}
			
			if (parallelogram!=null)
			{
				totalInclination += parallelogram.getInclination();
				parallelograms++;
				mostRecentParallelogram = parallelogram;
			}	
		}
		
		if (parallelograms==0)
			return;
		
		double averageInclination = totalInclination / (double) parallelograms;
		
		double sliderFraction = ( mostRecentParallelogram.maxAllowableInclination() - averageInclination ) /  (mostRecentParallelogram.maxAllowableInclination()-mostRecentParallelogram.minAllowableInclination());
		
		// double sliderFraction = (double)(inclinationSlider.getSlider().getMaximum()-inclinationSlider.getSlider().getValue())
		// /(double)(inclinationSlider.getSlider().getMaximum()-inclinationSlider.getSlider().getMinimum());
		
		double sliderRange = (double)(inclinationSlider.getSlider().getMaximum()-inclinationSlider.getSlider().getMinimum());
		double newSliderValue = (double)(inclinationSlider.getSlider().getMaximum()-sliderFraction*sliderRange);
		silenceableSlider.setSilent(true); // decidedly not multi-threaded
		inclinationSlider.getSlider().setValue( (int)newSliderValue );
		silenceableSlider.setSilent(false);
	}

	public void revealed()
	{
		super.revealed();
	}
	
	private void reLayoutInclineds()
	{
		Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();
		while (walk.hasNext())
		{
			Crowd.reLayoutCrowdsOf(walk.next());
		}
	}
}
