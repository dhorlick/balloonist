/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.LabeledPercentageField;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.layout.edit.Scale2dEdit;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 *
 * @author dhorlick
 */
public class ScaleBox extends Box
{	
	private double widthScalePercentage = 100.0;
	private double heightScalePercentage = 100.0;
	
	public ScaleBox()
	{
		super(BoxLayout.Y_AXIS);
		
		LabeledPercentageField widthLabeledPercentageField = new LabeledPercentageField(new JLabel(PossibleAction.DIALOG_TEXT.getString("widthLabel")), new JTextField());
		widthLabeledPercentageField.setModel(new PrimitiveDoubleHolder()
		{
			public void setDouble(double designatedDouble)
			{
				widthScalePercentage = designatedDouble;
			}

			public double getDouble()
			{
				return widthScalePercentage;
			}
		});
		add(widthLabeledPercentageField);
		
		LabeledPercentageField heightLabeledPercentageField = new LabeledPercentageField(new JLabel(PossibleAction.DIALOG_TEXT.getString("heightLabel")), new JTextField());
		heightLabeledPercentageField.setModel(new PrimitiveDoubleHolder()
		{
			public void setDouble(double designatedDouble)
			{
				heightScalePercentage = designatedDouble;
			}

			public double getDouble()
			{
				return heightScalePercentage;
			}
		});
		add(heightLabeledPercentageField);
	}
	
	public String toString()
	{
		return "com.smithandtinkers.balloonist.ScaleBox {"
			 + "widthScalePercentage = " + widthScalePercentage + ", "
			 + "heightScalePercentage = " + heightScalePercentage
		+ "}";
	}
	
	public Scale2dEdit toEdit()
	{
		if (widthScalePercentage==0.0 || heightScalePercentage==0.0)
			return null;
		
		return new Scale2dEdit(Math.abs(widthScalePercentage/100.0), Math.abs(heightScalePercentage/100.0));
	}
}
