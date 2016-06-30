/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.io.File;
import java.io.IOException;

/**
 * @author dhorlick
 */
public class OsXPlatformStrategy implements PlatformStrategy
{
	private int creatorCode;

	public OsXPlatformStrategy(final int designatedCreatorCode)
	{
		setCreatorCode(designatedCreatorCode);
	}

	public boolean takeOwnershipOfFile(File file) throws IOException
	{
		com.apple.eio.FileManager.setFileCreator(file.getAbsoluteFile().toString(), creatorCode); // TODO test this under Java 1.3
		return true;
	}
	
	public void setCreatorCode(final int designatedCreatorCode)
	{
		creatorCode = designatedCreatorCode;
	}

	public int getCreatorCode()
	{
		return creatorCode;
	}
}
