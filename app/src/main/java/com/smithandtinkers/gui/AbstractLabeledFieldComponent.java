/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 *
 * @author dhorlick
 */
public abstract class AbstractLabeledFieldComponent extends AbstractLabeledComponent
{
	protected JTextField field;
	
	public AbstractLabeledFieldComponent(JLabel designatedLabel, JTextField designatedField)
	{
		super(designatedLabel);
		setField(designatedField);
		setMaximumSize(new Dimension(500, 32)); // see "High Art" of Swing remark, page 344 of O'Reilly's Java Swing book
	}
	
	public JTextField getField()
	{
		return field;
	}

	public void setField(JTextField designatedField)
	{
		field = designatedField;
		
		if (label!=null)
			label.setLabelFor(field);
		
		field.setAlignmentX(Component.LEFT_ALIGNMENT);
		field.setAlignmentY(Component.CENTER_ALIGNMENT);
		field.setMaximumSize(new Dimension(300, 18)); // see "High Art" of Swing remark, page 344 of O'Reilly's Java Swing book
		
		// TODO if there was another field before, remove any NumberVerifier, remove this is a document listener, and remove it from this
		
		add(field);
	}
	
	public void setFontSize(float designatedFontSize)
	{
		// label.setFont(label.getFont().deriveFont(designatedFontSize));
		
		super.setFontSize(designatedFontSize);
		
		field.setFont(field.getFont().deriveFont(designatedFontSize));
		
		/* if (dropdown!=null)
		{
			dropdown.setFont(dropdown.getFont().deriveFont(designatedFontSize));
		} */
	}
}
