/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.gui;

/**
 * @author dhorlick
 */
public class CursorMode
{
	private String name;
	private boolean involvesCreation;
	private Class subject;
	
	public CursorMode(String designatedName, boolean designatedInvolvesCreation, Class designatedSubject)
	{
		setName(designatedName);
		setInvolvesCreation(designatedInvolvesCreation);
		setSubject(designatedSubject);
	}
	
	public CursorMode(String designatedName, boolean designatedInvolvesCreation)
	{
		this(designatedName, designatedInvolvesCreation, null);
	}
	
	/**
	 * @param designatedInvolvesCreation
	 */
	private void setInvolvesCreation(boolean designatedInvolvesCreation)
	{
		involvesCreation = designatedInvolvesCreation;
	}

	public String getName()
	{
		return name;
	}
	
	private void setName(String designatedName)
	{
		name = designatedName;
	}
	
	public boolean isInvolvesCreation()
	{
		return involvesCreation;
	}
	
	public Class getSubject()
	{
		return subject;
	}

	public void setSubject(Class designatedSubject)
	{
		subject = designatedSubject;
	}

	public String toString()
	{
		return "CursorMode [ name="+name
			+ ", involves creation="+involvesCreation
			+ ", subject=" + subject
			+" ]";
	}
}
