/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Lots of libraries call InputStream.close inappropriately.
 * 
 * This class allows you to prevent that.
 *
 * see http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html
 * 
 * @author dhorlick
 */
public class ProtectableInputStream extends FilterInputStream
{
	private boolean permitClosing = false;
	
	public ProtectableInputStream(InputStream designatedInputStream)
	{
		super(designatedInputStream);
	}
	
	public boolean isPermitClosing()
	{
		return permitClosing;
	}

	public void setPermitClosing(boolean designatedPermitClosing)
	{
		permitClosing = designatedPermitClosing;
	}

	public void close() throws IOException
	{
		if (isPermitClosing())
			in.close();
	}
}
