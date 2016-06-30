/*
 Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * @author dhorlick
 */
public class TwiceLabeledQuantityField extends LabeledQuantityField
{
	private JLabel trailingLabel;
	
	public TwiceLabeledQuantityField(JLabel designatedLabel, JTextField designatedTextField, JLabel designatedTrailingLabel)
	{
		super(designatedLabel, designatedTextField);
		setTrailingLabel(designatedTrailingLabel); // TODO unset this later to permit garbage collection?
		add(Box.createHorizontalStrut(5)); // TODO improve
		add(trailingLabel);
	}
	
	public void setFontSize(float designatedFontSize)
	{
		super.setFontSize(designatedFontSize);
		
		if (trailingLabel!=null)
		{
			trailingLabel.setFont(trailingLabel.getFont().deriveFont(designatedFontSize));
		}
	}
	
	public JLabel getTrailingLabel()
	{
		return trailingLabel;
	}
	
	private void setTrailingLabel(JLabel designatedTrailingLabel)
	{
		if (trailingLabel != designatedTrailingLabel)
		{
			trailingLabel = designatedTrailingLabel;
		
			if (trailingLabel!=null)
				trailingLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		}
	}
}