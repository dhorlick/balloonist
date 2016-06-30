/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

/**
 * @author dhorlick
 */
public abstract class AbstractLabeledComponent extends Box implements ChangeListener
{
	protected JLabel label = null;
	private boolean vertical;
	
	public AbstractLabeledComponent(JLabel designatedLabel)
	{
		this(designatedLabel, false);
	}
	
	public AbstractLabeledComponent(JLabel designatedLabel, boolean designatedVertical)
	{
		super(designatedVertical?BoxLayout.Y_AXIS:BoxLayout.X_AXIS);
		setVertical(designatedVertical);
		initialize();
		setLabel(designatedLabel);	
	}
	
	private void setVertical(boolean designatedVertical)
    {
	    vertical = designatedVertical;
    }
	
	public boolean isVertical()
	{
		return vertical;
	}

	private void initialize()
	{
		setBackground(null);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAlignmentY(Component.CENTER_ALIGNMENT);
		
		final int PADDING = determinePadding();
		
		setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
	}
		
	private void setLabel(JLabel designatedLabel)
	{
		label = designatedLabel;
		
		if (isVertical())
		{
			label.setAlignmentX(Component.CENTER_ALIGNMENT);
			label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		}
		else
		{
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			label.setHorizontalAlignment(JLabel.RIGHT);	
		}
		
		add(label);
		add(Box.createHorizontalStrut(determinePadding())); // TODO work on sequence
	}
	
	public JLabel getLabel()
	{
		return label;
	}
	
	public void setFontSize(float designatedFontSize)
	{
		label.setFont(label.getFont().deriveFont(designatedFontSize));
	}
	
	public int determinePadding()
	{
		if (label==null)
			return 4;
		else
			return label.getFont().getSize()/2;
	}
}
