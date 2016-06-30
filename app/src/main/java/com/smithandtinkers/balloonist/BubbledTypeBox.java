/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.edit.BubblePeriodEdit;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author dhorlick
 */
public class BubbledTypeBox extends Box
{
	private StemPropsPanel parentStemPropsPanel;
	private SilenceableSlider bubbleDensitySlider;
	
	public BubbledTypeBox(StemPropsPanel designatedParentStemPropsPanel)
	{
		super(BoxLayout.Y_AXIS);
		
		if (designatedParentStemPropsPanel==null)
			throw new IllegalArgumentException("Stem Props Panel parameter cannot be null.");
		
		parentStemPropsPanel = designatedParentStemPropsPanel;
		
		bubbleDensitySlider = new SilenceableSlider();
		bubbleDensitySlider.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e)
			{
				double bubblePeriod = 0.0;
				
				double bubbleDensity = Averager.mapValueFromSlider(
						bubbleDensitySlider, AbstractStem.MINIMUM_BUBBLE_DENSITY, AbstractStem.MAXIMUM_BUBBLE_DENSITY);
				
				if (bubbleDensity<=0 || bubbleDensity>1.0)
				{
					System.err.println("invalid bubble density: "+bubbleDensity);
					return;
				}

				bubblePeriod = 1.0/bubbleDensity;
				
				BubblePeriodEdit bubblePeriodEdit = new BubblePeriodEdit(bubblePeriod);
				bubblePeriodEdit.addItemsFrom(parentStemPropsPanel.getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (bubblePeriodEdit.execute())
				{
					parentStemPropsPanel.getDrawableDialog().announceEdit(this, bubblePeriodEdit);
					
					// if (getChangeListener()!=null)
					// 	getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		});
		
		LabeledSlider labeledBubbleDensitySlider = new LabeledSlider(
				new JLabel(DrawableDialog.DIALOG_TEXT.getString("bubbleDensityLabel")), 
				null, bubbleDensitySlider);
		labeledBubbleDensitySlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(labeledBubbleDensitySlider);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
	}
	
	public SilenceableSlider getBubbleDensitySlider()
	{
		return bubbleDensitySlider;
	}
}
