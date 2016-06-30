/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smithandtinkers.mvc.StringHolder;


/**
 * @author dhorlick
 */
public class LabeledTextField extends AbstractLabeledFieldComponent
{
	private StringHolder model; 
	
	public LabeledTextField(JLabel designatedLabel, JTextField designatedField)
	{
		super(designatedLabel, designatedField);
	}

	public void setField(JTextField designatedField)
	{
		super.setField(designatedField);
		
		// System.out.println("wiring up field...");
		
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (model!=null)
					model.setString(field.getText());
			}
		});
		
		field.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				if (model!=null)
					model.setString(field.getText());
			}
		});
	}

	public void stateChanged(javax.swing.event.ChangeEvent e)
	{
		stateChanged();
	}
	
	public void stateChanged()
	{
		if (model!=null)
		{
			field.setText(model.getString());
		}
	}

	public StringHolder getModel()
	{
		return model;
	}

	public void setModel(StringHolder designatedModel)
	{
		model = designatedModel;
	}
}
