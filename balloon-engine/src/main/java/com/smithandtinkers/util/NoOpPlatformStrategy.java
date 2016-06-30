/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author dhorlick
 */
public class NoOpPlatformStrategy implements PlatformStrategy
{
	public boolean takeOwnershipOfFile(File file) throws IOException
	{
		return false;
	}
}
