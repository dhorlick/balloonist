/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import javax.swing.AbstractAction;
import javax.swing.Action;


/**
 *
 * @author dhorlick
 */
public abstract class NamableAbstractAction extends AbstractAction
{
	public NamableAbstractAction()
	{
		super();
	}
	
	public NamableAbstractAction(String designatedName)
	{
		super(designatedName);
	}
	
	public void setName(String designatedName)
	{
		putValue(Action.NAME, designatedName);
	}
	
	public String getName()
	{
		return (String) getValue(Action.NAME);
	}
}