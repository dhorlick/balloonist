/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.WidgetedTypesafeList;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;


/**
 *
 * @author dhorlick
 */
public class FontSizeComboBox extends JComboBox
{
	public FontSizeComboBox()
	{
		super(generateFontSizesWidgetedList());
		init();
	}
	
	private void init()
	{
		setMaximumSize(new Dimension(500, 22));
		setPreferredSize(new Dimension(92, 22));
		// setPreferredSize(getPreferredSize()); // somehow, this freezes the combo box's size
		setEditable(true);
		
		if (getEditor().getEditorComponent() instanceof JTextField)
		{
			final JTextField jTextField = (JTextField) getEditor().getEditorComponent();
			
			jTextField.setInputVerifier(new InputVerifier()
			{
				public boolean verify(JComponent jComponent)
				{
					if (jComponent!=null && jComponent instanceof JTextField)
					{
						JTextField jTextField = (JTextField) jComponent;
						
						try
						{
							int asInt = Integer.parseInt(jTextField.getText());
							if (asInt<=0)
								return false;
						}
						catch (NumberFormatException exception)
						{
							return false;
						}
					}

					return true;
				}	
			});
		}
	}
	
	public static WidgetedTypesafeList generateFontSizesWidgetedList()
	{
		final WidgetedTypesafeList fontSizesWidgetedList = new WidgetedTypesafeList(Integer.class);
		final List fontSizes = Arrays.asList(new Integer [] {new Integer(8), new Integer(9), new Integer(10), new Integer(12), new Integer(13), new Integer(14), new Integer(16), new Integer(18)});
		fontSizesWidgetedList.addAll(fontSizes);
		
		return fontSizesWidgetedList;
	}
	
	public WidgetedTypesafeList getWidgetedTypesafeList()
	{
		return (WidgetedTypesafeList) getModel();
	}
	
	public void setFontSizeSelection(int designatedFontSize)
	{
		Integer fontSizeAsObjectInteger = new Integer(designatedFontSize);
		
		int newIndex = getWidgetedTypesafeList().indexOf(fontSizeAsObjectInteger);
						
		if (newIndex!=-1)
		{
			// System.out.println("caret. new index:"+newIndex+"\n");
			setSelectedIndex(newIndex);
		}
		else
		{						
			setSelectedItem(fontSizeAsObjectInteger);
		}
	}
	
	public int getFontSizeSelection()
	{
		if (getSelectedItem() instanceof Integer)
		{
			Integer selectedIntegerObj = (Integer) getSelectedItem();
			return selectedIntegerObj.intValue();
		}
		else
		{
			return 14;
		}
	}
}
