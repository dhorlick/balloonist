/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author dhorlick
 */
public class LabeledPercentageField extends LabeledQuantityField
{	
	public LabeledPercentageField(JLabel designatedLabel, JTextField designatedTextField)
	{
		super(designatedLabel, designatedTextField);
		designatedTextField.setHorizontalAlignment(JTextField.RIGHT);
		add(new JLabel(" %"));
		
		// TODO add validation & stuff
	}
}
