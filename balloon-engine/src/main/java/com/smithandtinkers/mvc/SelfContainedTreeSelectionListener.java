/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import java.util.EventListener;

public interface SelfContainedTreeSelectionListener extends EventListener
{
	/** 
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
   void valueChanged(SelfContainedTreeSelectionEvent e);
}
