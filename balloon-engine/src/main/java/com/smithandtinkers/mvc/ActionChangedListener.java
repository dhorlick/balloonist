/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;


/**
 <p>Adapted from the inner class in JMenu. Ideally this would be public.</p>
 
 @author Timothy Prinzing
 */
public class ActionChangedListener implements PropertyChangeListener
{
	AbstractButton button;
	
	public ActionChangedListener(AbstractButton mi)
	{
		super();
		this.button = mi;
	}
	
	public void propertyChange(PropertyChangeEvent e)
	{
		String propertyName = e.getPropertyName();
		if (e.getPropertyName().equals(Action.NAME))
		{
			String text = (String) e.getNewValue();
			button.setText(text);

			if (button instanceof JButton)
			{
				Logger.println("repainting JButton...");
				button.setName(text);
				button.repaint();
			}
		}
		else if (propertyName.equals("enabled"))
		{	
			Boolean enabledState = (Boolean) e.getNewValue();
			
			Logger.println("set button " + button + " to " + enabledState);
			
			button.setEnabled(enabledState.booleanValue());

		}
	}
}

