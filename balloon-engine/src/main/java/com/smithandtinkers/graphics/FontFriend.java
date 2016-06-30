/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.graphics;

import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Vector;

/**
 *
 * @author dhorlick
 */
public abstract class FontFriend
{

	public static String IDENTIFIER_SANS_SERIF = "SansSerif";
	public static String IDENTIFIER_SERIF = "Serif";
	public static String IDENTIFIER_MONOSPACED = "Monospaced";
	public static String IDENTIFIER_DIALOG = "Dialog";
	public static String IDENTIFIER_DIALOG_INPUT = "DialogInput";
	public static String IDENTIFIER_DEFAULT = "Default";
	
	public static Vector determineFontFamilyNames()
	{
		final Vector fontFamilyNames = new Vector();
		
		fontFamilyNames.addAll(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
		return fontFamilyNames;
	}
	
	public static boolean isLogical(String requestedFontFamilyName)
	{
		// Serif, SansSerif, Monospaced, Dialog, and DialogInput
		
		if (IDENTIFIER_SERIF.equals(requestedFontFamilyName))
			return true;
		
		if (IDENTIFIER_SANS_SERIF.equals(requestedFontFamilyName))
			return true;
		
		if (IDENTIFIER_MONOSPACED.equals(requestedFontFamilyName))
			return true;
		
		if (IDENTIFIER_DIALOG.equals(requestedFontFamilyName))
			return true;
		
		if (IDENTIFIER_DIALOG_INPUT.equals(requestedFontFamilyName))
			return true;
			
		if (IDENTIFIER_DEFAULT.equals(requestedFontFamilyName))
			return true; // according to me, not to Java
		
		return false;
	}
	
	/**
	 * @return true, if it's a font name that only Sun Microsystems could love
	 */
	public static boolean isNonStandard(String requestedFontFamilyName)
	{
		if (IDENTIFIER_DIALOG.equals(requestedFontFamilyName))
			return true;
		
		if (IDENTIFIER_DIALOG_INPUT.equals(requestedFontFamilyName))
			return true;
			
		if (IDENTIFIER_DEFAULT.equals(requestedFontFamilyName))
			return true;
			
		return false;
	}
}
