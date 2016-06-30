/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.gui;

import javax.swing.AbstractButton;

/**
 * @author dhorlick
 */
public interface CursorModeManager
{
	/**
	 * Sets cursor as appropriate for current cursor mode.
	 */
	public void manageCursorMode();
	
	public CursorMode currentCursorMode();

	public AbstractButton getLastSelection();
	
	public void setLastSelection(AbstractButton designatedButton);
	
	// public ButtonGroup getButtonGroup();
	
	public void setCurrentCursorMode(CursorMode cursorMode);
	
	// public Component getCursorComponent();
}
