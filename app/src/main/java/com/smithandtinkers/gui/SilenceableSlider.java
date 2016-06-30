/*
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import javax.swing.event.ChangeListener;
import javax.swing.JSlider;

import com.smithandtinkers.mvc.SilenceableModelListener;


/**
 * A silenceable JSlider. This is useful to prevent MVC feedback loops.
 *
 * @author dhorlick
 */
public class SilenceableSlider extends JSlider
{
	public SilenceableSlider()
	{
		super();
	}
	
	public SilenceableSlider(int designatedMininumValue, int designatedMaximumValue)
	{
		super(designatedMininumValue, designatedMaximumValue);
	}
	
	public SilenceableSlider(int designatedMininumValue, int designatedMaximumValue, boolean vertical, int initialValue)
	{
		super(vertical?VERTICAL:HORIZONTAL, designatedMininumValue, designatedMaximumValue, initialValue);
	}
	
	protected ChangeListener createChangeListener()
	{
		return new SilenceableModelListener(super.createChangeListener());
	}
	
	/**
	 * @return the SilenceableModelListener, or null if a non-silenceable change listener has been set,
	 * or removed entirely.
	 */
	protected SilenceableModelListener getSilenceableModelListener()
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
