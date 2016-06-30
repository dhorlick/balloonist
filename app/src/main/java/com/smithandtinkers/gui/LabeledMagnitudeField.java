/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;

import com.smithandtinkers.mvc.PrimitiveIntegerHolder;


/**
 * @author dhorlick
 */
public class LabeledMagnitudeField extends AbstractLabeledComponent
{
	private JTextField field = null;
	
	private PrimitiveIntegerHolder model;
	
	private boolean blanksOkay;
	private Integer minimumValue = new Integer(-2500);
	private Integer maximumValue = new Integer( 2500);
	
	/**
	 * @param designatedLabel
	 */
	public LabeledMagnitudeField(JLabel designatedLabel, JTextField designatedTextField)
	{
		super(designatedLabel);
		setField(designatedTextField);
	}

	private void setField(JTextField designatedField)
	{
		field = designatedField;
		
		if (label!=null)
			label.setLabelFor(field);
		
		if (!(field.getInputVerifier() instanceof WholeNumberVerifier))
		{
			field.setInputVerifier(new WholeNumberVerifier());
		}
		
		designatedField.setMaximumSize(new Dimension(300, 18)); // see "High Art" of Swing remark, page 344 of O'Reilly's Java Swing book
		
		// TODO if there was another field before, remove any NumberVerfier, remove this is a document listener, and remove it from this
		
		add(field);
		
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// System.out.println("action event! "+e);
				postChange(); // TODO verify that this isn't getting sent out in response to JTextField.setText. to avoid an infinite loop, we only want it to fire if the *user* makes a change.
			}
		});
		
		field.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				// System.out.println("Focus event! "+e);
				if (!e.isTemporary() && !(e.getOppositeComponent() instanceof RearrangeableTree))
					postChange();
			}
		});
		
		field.registerKeyboardAction( new ActionListener()
			{ 
				public void actionPerformed( ActionEvent event )
				{ 
					cancelChange();
				}
			}, KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ),
			JComponent.WHEN_FOCUSED );
	}
	
	public JTextField getField()
	{
		return field;
	}
	
	public void setModel(PrimitiveIntegerHolder designatedModel)
	{
		model = designatedModel;
	}

	public PrimitiveIntegerHolder getModel()
	{
		return model;
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource()!=this)
			stateChanged();
	}
	
	public void stateChanged()
	{
		if (model!=null)
		{
			int value = model.getInt();
			field.setText(String.valueOf(value));
		}
	}
	
	public void postChange()
	{
		if (model==null)
			throw new IllegalStateException("Model isn't set.");
		
		if (areBlanksOkay() && (field.getText()==null || field.getText().length()==0))
		{
			model.setInt(0);
		}
		else
		{
			model.setInt(Integer.parseInt(field.getText())); // this should be safe because we're using an input verifier
		}
	}
	
	public class WholeNumberVerifier extends InputVerifier
	{
		/**
		 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
		 */
		public boolean verify(JComponent input)
		{
			try
			{
				JTextField inputAsTextField = (JTextField) input;
				
				if (areBlanksOkay() && (inputAsTextField.getText()==null || inputAsTextField.getText().length()==0))
					return true;
				
				int parsed = Integer.parseInt(inputAsTextField.getText());
				
				if ((maximumValue!=null && parsed>maximumValue.intValue()) ||
						(minimumValue!=null && parsed<minimumValue.intValue()) )
				{
					Toolkit.getDefaultToolkit().beep();
					return false;
				}
				
				return true;
			}
			catch (NumberFormatException e)
			{
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
		}
	}
	
	public void setFontSize(float designatedFontSize)
	{
		super.setFontSize(designatedFontSize);
		
		if (field!=null)
		{
			field.setFont(field.getFont().deriveFont(designatedFontSize));
		}
	}
	
	public boolean areBlanksOkay()
	{
		return blanksOkay;
	}
	
	public void setBlanksOkay(boolean designatedBlanksOkay)
	{
		blanksOkay = designatedBlanksOkay;
	}
	
	public void setMaximumValue(Integer designatedMaximumValue)
	{
		maximumValue = designatedMaximumValue;
	}
	
	public Integer getMaximumValue()
	{
		return maximumValue;
	}
	
	public void setMinimumValue(Integer designatedMiminumValue)
	{
		minimumValue = designatedMiminumValue;
	}
	
	public Integer getMinimumValue()
	{
		return minimumValue;
	}
	
	public void cancelChange()
	{
		stateChanged();
	}
}
