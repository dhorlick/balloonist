/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * @author dhorlick
 */
public class LabeledSlider extends AbstractLabeledComponent
{
	private JLabel otherLabel;
	
	private JSlider slider;
	private JLabel readout;
	private String readoutPrefix;

	public LabeledSlider(JLabel designatedLabel, JLabel designatedOtherLabel, JSlider designatedSlider)
	{
		this(designatedLabel, designatedOtherLabel, designatedSlider, false, false);
	}
	
	public LabeledSlider(JLabel designatedLabel, JLabel designatedOtherLabel, JSlider designatedSlider, boolean vertical, boolean designatedReadouted)
	{
		super(designatedLabel, vertical);
		setSlider(designatedSlider);
		setOtherLabel(designatedOtherLabel);
		
		if (designatedReadouted)
		{
			readout = new JLabel();
			
			if (vertical)
			{
				readout.setAlignmentX(Component.CENTER_ALIGNMENT);
				readout.setAlignmentY(Component.TOP_ALIGNMENT);
			}
			
			slider.addChangeListener(new ChangeListener() {
				
				public void stateChanged(ChangeEvent changeEvent)
				{
					updateReadout();
				}
				
			});
			readout.setFont(readout.getFont().deriveFont(8.0f));
			add(Box.createVerticalStrut(5));
			add(readout);
			updateReadout();
		}
	}
	
	/**
	 * @param designatedSlider
	 */
	private void setSlider(JSlider designatedSlider)
	{
		if (designatedSlider==slider)
			return;
		
		if (slider!=null)
			remove(slider);
		
		slider = designatedSlider;
		
		if (slider!=null)
		{
			add(Box.createHorizontalStrut(determinePadding()));
			add(slider);
			// slider.setAlignmentX(Component.LEFT_ALIGNMENT);
			slider.setAlignmentY(Component.CENTER_ALIGNMENT);
		}
	}
	
	public JSlider getSlider()
	{
		return slider;
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		
	}
	
	public JLabel getOtherLabel()
	{
		return otherLabel;
	}
	
	private void setOtherLabel(JLabel designatedOtherLabel)
	{
		otherLabel = designatedOtherLabel;
		
		if (otherLabel!=null)
		{
			add(Box.createHorizontalStrut(determinePadding()));
			
			if (isVertical())
			{
				otherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);			
				otherLabel.setAlignmentY(Component.TOP_ALIGNMENT);
			}
			
			add(otherLabel);
		}
	}
	
	public boolean isReadouted()
	{
		return (getReadout()!=null);
	}
	
	public void setReadoutPrefix(String designatedReadoutPrefix)
	{
		if (designatedReadoutPrefix!=getReadoutPrefix())
		{
			readoutPrefix = designatedReadoutPrefix;
			updateReadout();
		}
	}
	
	public String getReadoutPrefix()
	{
		return readoutPrefix;
	}
	
	private void updateReadout()
	{
		if (getReadoutPrefix()==null)
			readout.setText(String.valueOf(slider.getValue()));
		else
			readout.setText(slider.getValue()+getReadoutPrefix());
	}
	
	public JLabel getReadout()
	{
		return readout;
	}
}
