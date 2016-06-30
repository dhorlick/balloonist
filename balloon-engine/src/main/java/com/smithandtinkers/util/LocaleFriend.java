/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.awt.ComponentOrientation;
import java.util.Locale;
import javax.swing.BoxLayout;

/**
 *
 * @author dhorlick
 */
public abstract class LocaleFriend
{
	public static final String LANGUAGE_JAPANESE = "ja";
	public static final String LANGUAGE_CHINESE = "zh";
	public static final String LANGUAGE_THAI = "th";
	
	public static int getPageAxisConstant()
	{
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			return BoxLayout.PAGE_AXIS;
		}
		else
		{
			ComponentOrientation componentOrientation = ComponentOrientation.getOrientation(Locale.getDefault());
			
			if (componentOrientation.isHorizontal())
				return BoxLayout.Y_AXIS;
			else
				return BoxLayout.X_AXIS;
		}
	}
	
	public static int getLineAxisConstant()
	{
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			return BoxLayout.LINE_AXIS;
		}
		else
		{
			ComponentOrientation componentOrientation = ComponentOrientation.getOrientation(Locale.getDefault());
			
			if (componentOrientation.isHorizontal())
				return BoxLayout.X_AXIS;
			else
				return BoxLayout.Y_AXIS;
		}
	}
	
	public static boolean wordSpacingAppropriate()
	{
		String localLanguage = Locale.getDefault().getLanguage();
		
		if (LANGUAGE_JAPANESE.equals(localLanguage)
				|| LANGUAGE_CHINESE.equals(localLanguage)
				|| LANGUAGE_THAI.equals(localLanguage)) // Korean?
		{
			return false;
		}
		
		return true;
	}
	
	public static void appendSpaceIfAppropriate(StringBuffer designatedStringBuffer)
	{
		if (wordSpacingAppropriate())
			designatedStringBuffer.append(" ");
	}
	
	public static boolean isLanguageJapanese()
	{
		return LANGUAGE_JAPANESE.equals(Locale.getDefault().getLanguage());
	}
	
	public static boolean isLanguageChinese()
	{
		return LANGUAGE_CHINESE.equals(Locale.getDefault().getLanguage());
	}
	
	public static int determineAppropriateLabelFontSize()
	{
		if (isLanguageJapanese() || isLanguageChinese())
			return 12;
		else
			return 9;
	}
}
