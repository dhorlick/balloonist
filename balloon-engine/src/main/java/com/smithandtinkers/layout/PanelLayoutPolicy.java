/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.layout;

/**
 * @author dhorlick
 */
public interface PanelLayoutPolicy
{
	public boolean isManual();
	
	public void setManual(boolean designatedManualness);
}
