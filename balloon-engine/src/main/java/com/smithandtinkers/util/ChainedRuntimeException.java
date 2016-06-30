/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.io.PrintStream;

/**
 <p>A Java 1.3 version of Java 1.4's chainable RuntimeException.</p>
 
 @author Dave Horlick
 */
public class ChainedRuntimeException extends RuntimeException
{
	private Exception nestedException = null;

	public ChainedRuntimeException()
	{
		super();
	}

	public ChainedRuntimeException(String designatedMessage)
	{
		super(designatedMessage);
	}

	public ChainedRuntimeException(Exception designatedNestedException)
	{
		super(designatedNestedException.getMessage());
		setNestedException(designatedNestedException);
	}
	
	public ChainedRuntimeException(String designatedMessage, Exception designatedNestedException)
	{
		super(designatedMessage);
		setNestedException(designatedNestedException);
	}

	public void setNestedException(Exception designatedNestedException)
	{
		nestedException = designatedNestedException;
	}

	public void printStackTrace()
	{
		printStackTrace(System.out);
	}

	public void printStackTrace(PrintStream ps)
	{
		super.printStackTrace(ps);
		
		if (nestedException!=null)
		{
			ps.println("----- Stacktrace of original, nested Exception -----");
			nestedException.printStackTrace(ps);
			ps.println("----- ---------- -- --------- ------ --------- -----");
		}
	}
}
