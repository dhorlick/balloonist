/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;


/**
 * @author dhorlick
 */
public class CursorModeAction extends AbstractAction
{
	private CursorModeManager manager;
	private CursorMode cursorMode;
	
	public final static Color MEDIUM_BLUE = new Color(0.60f,0.60f,1.0f);
	
	public CursorModeAction(String name, Icon icon, CursorModeManager cmm, CursorMode designatedMode)
	{
		super(name, icon);
		setManager(cmm);
		setCursorMode(designatedMode);
	}
	
	public CursorModeManager getManager()
	{
		return manager;
	}
	
	public void setManager(CursorModeManager cmm)
	{
		manager = cmm;
	}
	
	public CursorMode getCursorMode()
	{
		return cursorMode;
	}
	
	public void setCursorMode(CursorMode designatedMode)
	{
		this.cursorMode = designatedMode;
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt)
	{
		if (manager!=null && manager.getLastSelection()!=null)
			manager.getLastSelection().setBackground(null);

		JComponent newSelection = (JComponent) evt.getSource();
		newSelection.setBackground(MEDIUM_BLUE);
		
		if (manager!=null)
			manager.setLastSelection((AbstractButton)newSelection);
		
		if (manager!=null)
		{
			manager.setCurrentCursorMode(cursorMode);
			manager.manageCursorMode();
		}
	}
}
