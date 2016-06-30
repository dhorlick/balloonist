/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.net.URL;

import javax.swing.ImageIcon;


/**
 @author Dave Horlick
 */
public abstract class ResourceFriend
{
	/**
	 <p>Looks for resource first in jar, then on local path.</p>
	 */
	public static ImageIcon retrieveImageIcon(String requestedPath)
	{
		ImageIcon icon = null;
		URL iconUrl = ClassLoader.getSystemResource(requestedPath);
		
		if (iconUrl == null)
			return new ImageIcon(requestedPath);
		else
			return new ImageIcon(iconUrl);
	}
}
