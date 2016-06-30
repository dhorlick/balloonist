/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;

 
/**
 * @author dhorlick
 */
public class LabeledDropDown extends AbstractLabeledComponent
{
	private JComboBox comboBox = null;
	
	public LabeledDropDown(JLabel designatedLabel, JComboBox designatedComboBox)
	{
		super(designatedLabel);
		
		setComboBox(designatedComboBox);
		
		setBackground(null);
		
		label.setHorizontalAlignment(JLabel.RIGHT);
		
		add(label);
		add(Box.createHorizontalStrut(determinePadding()));
		add(designatedComboBox);
	}
	
	public LabeledDropDown(String designatedLabelText, JComboBox designatedComboBox)
	{
		this(new JLabel(designatedLabelText), designatedComboBox);
	}
	
	private void setComboBox(JComboBox designatedComboBox)
	{
		if (designatedComboBox==comboBox)
			return;
		
		if (getLabel()!=null)
		{
			if (designatedComboBox!=null)
				label.setLabelFor(designatedComboBox);
			
			if (comboBox!=null && label.getLabelFor()==comboBox)
				label.setLabelFor(null);
		}
		
		comboBox = designatedComboBox;
		
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		// comboBox.setMaximumSize(new Dimension(90, 25));
	}
	
	public JComboBox getComboBox()
	{
		return comboBox;
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource()!=this)
			stateChanged();
	}
	
	/**
	 * If you want the selected item to be dependent on external changes, please override this with our blessing.
	 */
	public void stateChanged()
	{
	}
	
	public void setFontSize(float designatedFontSize)
	{
		super.setFontSize(designatedFontSize);
		
		if (comboBox!=null)
		{
			comboBox.setFont(comboBox.getFont().deriveFont(designatedFontSize));
		}
	}
}
