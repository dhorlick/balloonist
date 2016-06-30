/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.gui.ColorPicker;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.gui.SimpleLabeledComponent;
import com.smithandtinkers.gui.TwiceLabeledQuantityField;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.average.LineThicknessAverager;
import com.smithandtinkers.layout.edit.LineThicknessEdit;
import com.smithandtinkers.layout.edit.MarginEdit;
import com.smithandtinkers.layout.average.MarginAverager;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;

import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;


/**
 *
 * @author dhorlick
 */
public class SillPropsPanel extends ResizeablePropsPanel
{
	private Averager marginAverager = new MarginAverager();
	private SilenceableSlider marginSilenceableSlider;
	private TwiceLabeledQuantityField lineThicknessLabeledQuantityField;
	private ColorPicker colorPicker;
	
	public SillPropsPanel()
	{
		super();
	}

	protected void initialize()
	{
		super.initialize();
		
		Box line1 = Box.createHorizontalBox();
		line1.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		colorPicker = new ColorPicker();
		SimpleLabeledComponent labeledColorPicker = new SimpleLabeledComponent(new JLabel(DrawableDialog.DIALOG_TEXT.getString("colorsLabel")), colorPicker);
		labeledColorPicker.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		labeledColorPicker.add(Box.createHorizontalGlue());
		colorPicker.setMaximumSize(new Dimension(28, 22));
		colorPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
		colorPicker.setColorfulModel(generateColorfulModel());
		line1.add(labeledColorPicker);
		
		final JTextField lineThicknessTextField = new JTextField();
		lineThicknessTextField.setColumns(5);
		lineThicknessLabeledQuantityField = new TwiceLabeledQuantityField(
				new JLabel(getDrawableDialog().DIALOG_TEXT.getString("thicknessLabel")),
				lineThicknessTextField,
				new JLabel(DrawableDialog.POINTS.getName()));
		lineThicknessLabeledQuantityField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		lineThicknessLabeledQuantityField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		lineThicknessLabeledQuantityField.setMinimumValue(new Double(0.2));
		lineThicknessLabeledQuantityField.setMaximumValue(new Double(5.0));
		lineThicknessLabeledQuantityField.setModel(new PrimitiveDoubleHolder()
		{
			public double getDouble()
			{
				if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
				{
					return 0.0f;
				}
				
				LineThicknessAverager averager = new LineThicknessAverager();
				return averager.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
			}

			public void setDouble(double designatedLineThicknessInPoints)
			{
				if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
					return;
				
				LineThicknessEdit lineThicknessEdit = new LineThicknessEdit(designatedLineThicknessInPoints);
				lineThicknessEdit.addItemsFrom(getDrawableDialog().getSelection().getSelectedItems());
				
				if (lineThicknessEdit.execute())
				{
					getDrawableDialog().announceEdit(SillPropsPanel.this, lineThicknessEdit);
				}
			}
		});
		line1.add(lineThicknessLabeledQuantityField);
		
		add(line1);
		
		marginSilenceableSlider = new SilenceableSlider(0, (int)Marginal.MAXIMUM_ALLOWABLE_MARGIN);
		LabeledSlider marginLabeledSlider = new LabeledSlider(new JLabel(DrawableDialog.DIALOG_TEXT.getString("textMarginLabel")), null, marginSilenceableSlider);
		marginLabeledSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(marginLabeledSlider);
		
		marginSilenceableSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent e)
			{
				MarginEdit marginEdit = new MarginEdit(marginSilenceableSlider.getValue());
				marginEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				
				if (marginEdit.execute())
				{
					getDrawableDialog().announceEdit(this, marginEdit);
					
					if (getChangeListener()!=null)
						getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		});
	}
	
	protected void stateChanged()
	{
		super.stateChanged();
		
		lineThicknessLabeledQuantityField.stateChanged();
		colorPicker.stateChanged();
		
		marginSilenceableSlider.setSilent(true);
		marginAverager.averageTo(getDrawableDialog().getSelection().iterateOverSelectedItems(), marginSilenceableSlider, 0, Marginal.MAXIMUM_ALLOWABLE_MARGIN); 
		marginSilenceableSlider.setSilent(false);
	}	
}
