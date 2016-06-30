/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.util;

/**
 * An abstract class providing an explicit mechanism for describing objects with Strings.
 * 
 * Helpful for wrapping complex objects added to a JList.
 * 
 * @author dhorlick
 */
public abstract class Sticker
{
	protected Object object;
	
	public Sticker(Object designatedObject)
	{
		setObject(designatedObject);
	}
	
	/**
	 * @return Returns the object.
	 */
	public Object getObject()
	{
		return object;
	}
	
	/**
	 * @param object The object to set.
	 */
	public void setObject(Object object)
	{
		this.object = object;
	}
	public abstract String getText();
	
	public String toString()
	{
		return getText();
	}
}
