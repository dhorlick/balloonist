/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.svg;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

/**
 * Provides some ImageIO-like features for folks operating in legacy Java virtual machines.
 * 
 * @author dhorlick
 */
public final class BudgetImageIO
{
	public static final String FORMAT_PORTABLE_NETWORK_GRAPHICS = "png";
	public static final String FORMAT_JOINT_PHOTO_EXPERTS_GROUP = "jpg";
	public static final String FORMAT_GRAPHICS_INTERCHANGE_FORMAT = "gif";
	
	public static final String [] READER_FORMAT_NAMES = {FORMAT_GRAPHICS_INTERCHANGE_FORMAT, FORMAT_JOINT_PHOTO_EXPERTS_GROUP, FORMAT_PORTABLE_NETWORK_GRAPHICS};
	
	public static String[] getReaderFormatNames()
	{
		return READER_FORMAT_NAMES;
	}

	public static Image read(File designatedFile)
	{
		return Toolkit.getDefaultToolkit().getImage(designatedFile.getPath());
	}
	
	public static Image read(URL designatedURL)
	{
		return Toolkit.getDefaultToolkit().getImage(designatedURL);
	}
}
