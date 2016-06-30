/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.mvc.SilenceableModelListener;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;


/**
 *
 * @author dhorlick
 */
public class SilenceableCheckBox extends JCheckBox
{
	public SilenceableCheckBox()
	{
	}
	
	public SilenceableCheckBox(String text)
	{
		super(text);
	}

	protected ChangeListener createChangeListener()
	{
		return new SilenceableModelListener(super.createChangeListener());
	}
	
	/**
	 * @return the SilenceableModelListener, or null if a non-silenceable change listener has been set,
	 * or removed entirely.
	 */
	protected SilenceableModelListener getSilenceableModelListener() // TODO pull all this nonsense out into an abstract base class
	{
		if (super.changeListener==null)
			return null;
		
		if (!(super.changeListener instanceof SilenceableModelListener))
		{
			// outside forces have changed the change listener to one that isn't silenceable
			
			return null;
		}
		
		return (SilenceableModelListener) super.changeListener;
	}
	
	/**
	 * @throws IllegalStateException if there is no silenceable change listener set.
	 */
	public void setSilent(boolean designatedSilentness)
	{
		SilenceableModelListener silenceableChangeListener = getSilenceableModelListener();
		
		if (silenceableChangeListener==null)
			throw new IllegalStateException("No silenceable change listener set.");
		
		silenceableChangeListener.setSilent(designatedSilentness);
	}
	
	public boolean isSilent()
	{
		SilenceableModelListener silenceableChangeListener = getSilenceableModelListener();
		
		if (silenceableChangeListener==null)
			return false;

		return silenceableChangeListener.isSilent();
	}
}
