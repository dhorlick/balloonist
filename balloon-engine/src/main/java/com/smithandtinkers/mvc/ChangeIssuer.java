/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import javax.swing.event.ChangeListener;

/**
 * @author dhorlick
 */
public interface ChangeIssuer
{
	public void addChangeListener(ChangeListener designatedListener);
	public void removeChangeListener(ChangeListener designatedChangeListener);
}
