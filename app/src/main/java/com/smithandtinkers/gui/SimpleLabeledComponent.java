/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;

/**
 *
 * @author dhorlick
 */
public class SimpleLabeledComponent extends AbstractLabeledComponent
{
	private Component component;
	
	public SimpleLabeledComponent(JLabel designatedLabel, Component designatedComponent)
	{
		super(designatedLabel);
		setComponent(designatedComponent);
	}
	
	public void stateChanged(javax.swing.event.ChangeEvent e)
	{
	}

	public Component getComponent()
	{
		return component;
	}

	public void setComponent(Component designatedComponent)
	{
		if (designatedComponent==component)
			return;
		
		if (component!=null)
			remove(component);
		
		component = designatedComponent;
		
		if (component!=null)
		{
			add(Box.createHorizontalStrut(determinePadding()));
			add(component);
		}
	}
}
