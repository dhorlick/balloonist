/*
 Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.edit.FocusInclinationEdit;
import com.smithandtinkers.layout.edit.RigidityEdit;
import com.smithandtinkers.layout.edit.RootInclinationEdit;
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
public class StemCurvingBox extends Box
{
	private StemPropsPanel parentStemPropsPanel;
	
	private SilenceableSlider rootInclinationSlider;
	private SilenceableSlider focusInclinationSlider;
	private SilenceableSlider rigiditySlider;
	
	public StemCurvingBox(StemPropsPanel designatedParentStemPropsPanel)
	{
		super(BoxLayout.Y_AXIS);
		
		if (designatedParentStemPropsPanel==null)
			throw new IllegalArgumentException("Stem Props Panel parameter cannot be null.");
		
		parentStemPropsPanel = designatedParentStemPropsPanel;
		
		rootInclinationSlider = new SilenceableSlider();
		getRootInclinationSlider().addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e)
			{
				final double newValue = Averager.mapValueFromSlider(
						rootInclinationSlider, 0.0, Math.PI/2.0);
				
				RootInclinationEdit rootInclinationEdit = new RootInclinationEdit(newValue);
				
				rootInclinationEdit.addItemsFrom(parentStemPropsPanel.getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (rootInclinationEdit.execute())
				{
					parentStemPropsPanel.getDrawableDialog().announceEdit(this, rootInclinationEdit);
					
					// if (getChangeListener()!=null)
					// 	getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		});
		
		focusInclinationSlider = new SilenceableSlider();
		getFocusInclinationSlider().addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e)
			{
				final double newValue = Averager.mapValueFromSlider(
						focusInclinationSlider, 0.0, Math.PI/2.0);
				
				final FocusInclinationEdit focusInclinationEdit = new FocusInclinationEdit(newValue);
				focusInclinationEdit.addItemsFrom(parentStemPropsPanel.getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (focusInclinationEdit.execute())
				{
					parentStemPropsPanel.getDrawableDialog().announceEdit(this, focusInclinationEdit);
					
					// if (getChangeListener()!=null)
					// 	getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		});
		
		LabeledSlider labeledRootInclinationSlider = new LabeledSlider(
				new JLabel(DrawableDialog.DIALOG_TEXT.getString("rootStemInclinationLabel")), 
				null, getRootInclinationSlider() );
		labeledRootInclinationSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(labeledRootInclinationSlider);
		
		LabeledSlider labeledFocusInclinationSlider = new LabeledSlider(
				new JLabel(DrawableDialog.DIALOG_TEXT.getString("focusStemInclinationLabel")), 
				null, getFocusInclinationSlider() );
		labeledFocusInclinationSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(labeledFocusInclinationSlider);
		
		rigiditySlider = new SilenceableSlider();
		getRigiditySlider().addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				final double newValue = Averager.mapValueFromSlider(
						rigiditySlider, 0.0, 1.0);
				
				final RigidityEdit rigidityEdit = new RigidityEdit(newValue);
				
				rigidityEdit.addItemsFrom(parentStemPropsPanel.getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (rigidityEdit.execute())
				{
					parentStemPropsPanel.getDrawableDialog().announceEdit(this, rigidityEdit);
				}
			}
			
		});
		LabeledSlider labeledRigiditySlider = new LabeledSlider(
				new JLabel(DrawableDialog.DIALOG_TEXT.getString("rigiditySliderInclinationLabel")),
				null, getRigiditySlider());
		labeledRigiditySlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(labeledRigiditySlider);
		
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
	}

	public StemPropsPanel getParentStemPropsPanel()
	{
		return parentStemPropsPanel;
	}

	public SilenceableSlider getRootInclinationSlider()
	{
		return rootInclinationSlider;
	}

	public SilenceableSlider getFocusInclinationSlider()
	{
		return focusInclinationSlider;
	}
	
	public SilenceableSlider getRigiditySlider()
	{
		return rigiditySlider;		
	}
}
