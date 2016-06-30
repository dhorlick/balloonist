/*
 Copyleft Feb 28, 2005 by Dave Horlick
 */
package com.smithandtinkers.gui;

import java.awt.Dimension;
import java.awt.event.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Toolkit;

import javax.swing.*;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;

import com.smithandtinkers.mvc.PrimitiveDoubleHolder;
import com.smithandtinkers.util.WidgetedTypesafeList;


/**
 * @author dhorlick
 */
public class LabeledQuantityField extends AbstractLabeledFieldComponent
{
	private JComboBox dropdown;
	
	private PrimitiveDoubleHolder model;
	
	private int figuresAfterDecimalPoint;
	
	private boolean blanksOkay = false;
	private Double minimumValue = new Double(-2500.0);
	private Double maximumValue = new Double( 2500.0);
	
	public LabeledQuantityField(JLabel designatedLabel, JTextField designatedTextField)
	{
		super(designatedLabel, designatedTextField);
	}
	
	public LabeledQuantityField(JLabel designatedLabel, JTextField designatedTextField, UnitChoiceList ucl)
	{
		this(designatedLabel, designatedTextField);
		
		if (ucl!=null)
		{
			buildDropdown(ucl);
		}
	}

	public void setField(JTextField designatedField)
	{
		super.setField(designatedField);
		
		field.setInputVerifier(new NumberVerifier());
		
		// TODO if there was another field before, remove any NumberVerfier, remove this is a document listener, and remove it from this
		
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
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
				if (!e.isTemporary() && !(e.getOppositeComponent() instanceof RearrangeableTree))
				{
					postChange();
				}
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
	
	public int getFiguresAfterDecimalPoint()
	{
		return figuresAfterDecimalPoint;
	}
	
	public void setFiguresAfterDecimalPoint(int designatedFigures)
	{
		figuresAfterDecimalPoint = designatedFigures;
	}
	
	public void buildDropdown(UnitChoiceList ucl)
	{
		add(Box.createHorizontalStrut(determinePadding()));
		dropdown = new JComboBox(ucl);
		add(dropdown);
		dropdown.addItemListener(new ItemListener()
			{
				public void itemStateChanged(ItemEvent e)
				{
					stateChanged();
				}
			}
		);
		dropdown.setMaximumSize(new Dimension(100, 24));
	}
	
	public JComboBox getDropdown()
	{
		return dropdown;
	}
	
	public Double objectDouble()
	{
		if (field==null)
			return null;
		
		double doubleVal = Double.parseDouble(field.getText());
		return new Double(doubleVal);
	}
	
	public Long objectLong()
	{
		if (field==null)
			return null;
		
		long longVal = Long.parseLong(field.getText());
		return new Long(longVal);
	}
	
	private class NumberVerifier extends InputVerifier
	{
		/**
		 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
		 */
		public boolean verify(JComponent input)
		{
			try
			{
				JTextField inputAsTextField = (JTextField) input;
				
				if (areBlanksOkay() && (inputAsTextField==null || inputAsTextField.getText()==null || inputAsTextField.getText().length()==0))
					return true; // blank is okay
				
				double parsed = expressInBaseUnits();
				
				// System.out.println("considering: "+parsed);
				
				if ((maximumValue!=null && parsed>maximumValue.doubleValue()) ||
						(minimumValue!=null && parsed<minimumValue.doubleValue()) )
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
	
	public static class UnitChoice
	{
		private String name;
		private double amount = 1.0;
		
		public UnitChoice(String designatedName, double designatedAmount)
		{
			setName(designatedName);
			setAmount(designatedAmount);
		}
		
		public double getAmount()
		{
			return amount;
		}
		
		public void setAmount(double designatedName)
		{
			amount = designatedName;
		}
		
		public String getName()
		{
			return name;
		}
		
		public void setName(String designatedName)
		{
			name = designatedName;
		}
		
		public String toString()
		{
			return name;
		}
	}
	
	public static class UnitChoiceList extends WidgetedTypesafeList
	{
		public UnitChoiceList()
		{	
			super(UnitChoice.class);
		}
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		stateChanged();
	}
	
	/**
	 * Called when the the model is changed. Proceeds to updates the View (the JTextField.)
	 */
	public void stateChanged()
	{
		if (model!=null)
		{
			double value = model.getDouble();
			field.setText(String.valueOf(expressInAppropriateUnits(value)));
		}
	}

	public double expressInAppropriateUnits(double points)
	{
		double theValue = 0.0;
		
		if (dropdown==null)
		{
			theValue = model.getDouble();
		}
		else
		{
			UnitChoice uc = (UnitChoice) dropdown.getSelectedItem();
			theValue = points / uc.getAmount();
		}
		
		// return round(theValue);
		return theValue;
	}
	
	public PrimitiveDoubleHolder getModel()
	{
		return model;
	}
	
	public void setModel(PrimitiveDoubleHolder designatedModel)
	{
		if (designatedModel!=model)
		{
			model = designatedModel;
			
			if (model!=null)
			{
				stateChanged();
			}
		}
	}

	public void setFontSize(float designatedFontSize)
	{
		super.setFontSize(designatedFontSize);
		
		if (dropdown!=null)
		{
			dropdown.setFont(dropdown.getFont().deriveFont(designatedFontSize));
		}
	}
	
	/**
	 * Called internally when the user types a new value into the JTextField or changes the drop down setting.
	 * Updates the Model accordingly.
	 */
	private void postChange()
	{
		if (model==null)
			throw new IllegalStateException("Model isn't set.");
		
		model.setDouble(expressInBaseUnits());
		
		stateChanged();
	}
	
	/**
	 * @return the value, adjusted to default units (regardless of whether those are still selected)
	 */
	public double expressInBaseUnits()
	{
		if (dropdown==null)
		{
			if (field.getText()!=null && field.getText().length()>0)
				return Double.parseDouble(field.getText());
			else
				return 0.0;
		}
		else
		{
			UnitChoice uc = (UnitChoice) dropdown.getSelectedItem();
			
			if (field.getText()!=null && field.getText().length()>0)
			{
				return Double.parseDouble(field.getText()) * uc.getAmount();
			}
			else
			{
				return 0.0;
			}
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
		
	public void setMaximumValue(Double designatedMaximumValue)
	{
		maximumValue = designatedMaximumValue;
	}
	
	public Double getMaximumValue()
	{
		return maximumValue;
	}
	
	public void setMinimumValue(Double designatedMiminumValue)
	{
		minimumValue = designatedMiminumValue;
	}
	
	public Double getMinimumValue()
	{
		return minimumValue;
	}
	
	public void cancelChange()
	{
		stateChanged();
	}
}
