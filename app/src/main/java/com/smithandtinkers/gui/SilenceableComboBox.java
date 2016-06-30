/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;
import javax.swing.ComboBoxModel;

import javax.swing.JComboBox;



/**
 *
 * @author dhorlick
 */
public class SilenceableComboBox extends JComboBox
{
	private boolean silent;
	
	public SilenceableComboBox()
	{
		super();
	}
	
	public SilenceableComboBox(ComboBoxModel designatedComboBoxModel)
	{
		super(designatedComboBoxModel);
	}
	
	/**
	 * @throws IllegalStateException if there is no silenceable change listener set.
	 */
	public void setSilent(boolean designatedSilentness)
	{
		silent = designatedSilentness;
	}
	
	public boolean isSilent()
	{
		return silent;
	}

	protected void fireActionEvent()
	{
		if (!silent)
			super.fireActionEvent();
	}
}
