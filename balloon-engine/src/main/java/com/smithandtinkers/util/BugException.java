/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;


/**
 * @author dhorlick
 */
public class BugException extends ChainedRuntimeException
{
	private Exception nestedException = null;

	public BugException()
	{
		super();
	}

	public BugException(String designatedMessage)
	{
		super(designatedMessage);
	}

	public BugException(Exception designatedNestedException)
	{
		super(designatedNestedException.getMessage());
		setNestedException(designatedNestedException);
	}
	
	public BugException(String designatedMessage, Exception designatedNestedException)
	{
		super(designatedMessage, designatedNestedException);
	}
}
