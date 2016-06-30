/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

/**
 * Indicates that a document's structure isn't valid.
 * 
 * @author dhorlick
 */
public class StructureException extends Exception
{
	private String identifier;
	
	public StructureException()
	{
		super();
	}

	public StructureException(String message)
	{
		super(message);
	}

	public StructureException(String message, String designatedIdentifier)
	{
		super(message);
		setIdentifier(identifier);
	}

	public String getIdentifier()
	{
		return identifier;
	}
	
	public void setIdentifier(String designatedIdentifier)
	{
		identifier = designatedIdentifier;
	}
}
