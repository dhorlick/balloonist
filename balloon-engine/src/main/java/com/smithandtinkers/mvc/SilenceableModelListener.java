/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import javax.swing.event.*;


/**
 * A selectably deactivatble wrapper for change listeners. 
 *
 * @author dhorlick
 */
public class SilenceableModelListener implements ChangeListener
{
	private boolean silent;
	private ChangeListener changeListener;
	
	public SilenceableModelListener()
	{
	}
	
	public SilenceableModelListener(ChangeListener designatedChangeListener)
	{
		setChangeListener(designatedChangeListener);
	}
	
	public void stateChanged(ChangeEvent e)
	{
		if (!isSilent())
			getChangeListener().stateChanged(e);
    }

	public boolean isSilent()
	{
		return silent;
	}

	public void setSilent(boolean designatedSilentness)
	{
		silent = designatedSilentness;
	}

	public ChangeListener getChangeListener()
	{
		return changeListener;
	}

	public void setChangeListener(ChangeListener designatedChangeListener)
	{
		changeListener = designatedChangeListener;
	}
	
	
}
