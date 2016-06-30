/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.io.File;
import java.io.IOException;

/**
 * @author dhorlick
 */
public interface PlatformStrategy
{
	/**
	 * Takes ownership of the file for this application on platforms that feature such a
	 * concept.
	 *
	 * @return if a request to assume ownership was made. Does not check to determine whether
	 * the file was already owned.
	 */
	public boolean takeOwnershipOfFile(final File file) throws IOException;
}
