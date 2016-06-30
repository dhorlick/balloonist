/*
 Copyleft 2005 by Dave Horlick
*/

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.Named;

import javax.swing.event.ChangeEvent;


public class NameChangeEvent extends ChangeEvent
{
	public NameChangeEvent(Named source)
	{
		super(source);
	}
}