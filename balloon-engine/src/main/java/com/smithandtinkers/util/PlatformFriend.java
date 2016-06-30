/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;


/**
 * Provides an Exception-resistant gateway to platform-divergent or platform-specific information and functions.
 *
 * @author dhorlick
 */
public abstract class PlatformFriend
{
	public static final boolean RUNNING_ON_MAC = (System.getProperty("mrj.version") != null || "Apple Computer".equals("java.vendor")
			|| (System.getProperty("os.name", "").equalsIgnoreCase("Mac OS X")));
	public static final boolean RUNNING_ON_WINDOWS = (System.getProperty("os.name") !=null && System.getProperty("os.name").startsWith("Windows"));
	
	public static final VersionNumber JAVA_VERSION = VersionNumber.parse(System.getProperty("java.version"));
	public static final VersionNumber OS_VERSION = VersionNumber.parse(System.getProperty("os.version"));

	private static final VersionNumber JAVA_VERSION_1_4 = VersionNumber.parse("1.4");
	private static final VersionNumber JAVA_VERSION_1_5 = VersionNumber.parse("1.5");
	private static final VersionNumber JAVA_VERSION_1_6 = VersionNumber.parse("1.6");
	private static final VersionNumber OS_VERSION_10_4 = VersionNumber.parse("10.4");
	
	// public static final boolean RUNNING_ON_JAVA_14 = (System.getProperty("java.version") != null) && (System.getProperty("java.version").startsWith("1.4"));
	public static final boolean RUNNING_ON_JAVA_14_OR_HIGHER = JAVA_VERSION.isGreaterThanOrEqualTo(JAVA_VERSION_1_4);
	public static final boolean RUNNING_ON_JAVA_60_OR_HIGHER = JAVA_VERSION.isGreaterThanOrEqualTo(JAVA_VERSION_1_6);
	
	public static final boolean TEXTLAYOUT_BOUNDS_UNRELIABLE = PlatformFriend.RUNNING_ON_MAC 
			&& (JAVA_VERSION.isGreaterThanOrEqualTo(JAVA_VERSION_1_4) && JAVA_VERSION_1_5.compareTo(JAVA_VERSION)==Boolean.TRUE)
			&& (OS_VERSION_10_4.compareTo(OS_VERSION)==Boolean.TRUE);
	
	public static final boolean LIVE_RESIZE = "true".equals(System.getProperty("com.apple.mrj.application.live-resize"));
	public static final boolean USE_SCREEN_MENUBAR = "true".equals(System.getProperty("com.apple.macos.useScreenMenuBar"));
	// public static final boolean FORCE_SAFE_POSITIONING = (System.getProperty(FORCE_SAFE_POSITIONING_KEY)==null || "true".equals(System.getProperty(FORCE_SAFE_POSITIONING_KEY)));
	
	public static final TextAttribute KERNING_TEXT_ATTRIBUTE = findKerningTextAttribute();
	public static final Integer KERNING_ON = new Integer(1);
	
	static
	{
		System.out.println("java.version: "+System.getProperty("java.version"));
		System.out.println("os.name: " + System.getProperty("os.name"));
		System.out.println("os.version: " + System.getProperty("os.version"));
		System.out.println("mrj.version: " + System.getProperty("mrj.version"));
		System.out.println("java.vendor: " + System.getProperty("java.vendor"));
		// System.out.println("RUNNING_ON_JAVA_14_OR_HIGHER="+RUNNING_ON_JAVA_14_OR_HIGHER);
	}
	
	public static int getMenuShortcutKey()
	{
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			// thanks to Steve Roy
	}
	
	public static Color pickAppropriateTextHiliteColor()
	{
		final Color DEFAULT_COLOR = Color.lightGray;
		
		Color color = SystemColor.textHighlight;
		
		if (color==null)
			return DEFAULT_COLOR;
		
		double brightness = PlatformFriend.determineBrightness(color);
		
		if (brightness==0.0)
			return DEFAULT_COLOR;
		
		return color;
	}

	/**
	 * Determines how bright a the provided color is.
	 * 
	 * @return a double between zero and one, zero being the darkest, and one being the brightest.
	 */
	private static double determineBrightness(Color color)
	{
		int total = color.getBlue()+color.getRed()+color.getGreen();
		return (double)total/(255.0*3.0);
	}

	public static boolean titleBarsShouldContainIconImages()
	{
		if (RUNNING_ON_MAC)
			return false;
		else
			return true;
	}
	
	/**
	 * Will typically launch a web browser with the requested URL.
	 * 
	 * Will only work on Mac OS X or Windows systems that aren't messed up.
	 *
	 * @return true if successful
	 */
	public static boolean openUrl(String requestedUrl)
	{
		String openCmd = null;

		try
		{
			if (RUNNING_ON_MAC)
			{
				openCmd = "open";
				String [] cmdArray = new String[] {openCmd, requestedUrl};
				Runtime.getRuntime().exec(cmdArray);
				
				return true;
			}
			else if (RUNNING_ON_WINDOWS)
			{
				openCmd = "cmd.exe /c start";
				Runtime.getRuntime().exec(openCmd + " " + requestedUrl);
				
				return true;
			}
			else
			{
				// Linux? Solaris?
				
				// TODO use which to check for several different browsers
				
				openCmd = "konqueror";
				String [] cmdArray = new String[] {openCmd, requestedUrl};
				Runtime.getRuntime().exec(cmdArray);
			}
		}
		catch (IOException exception)
		{
			System.out.println("Couldn't follow link: "+exception.getMessage());
		}
		
		return false;	
	}
	
	public static boolean openUrl(final String requestedUrl, final Map paramValues)
	{
		return openUrl(buildUrl(requestedUrl, paramValues));
	}
	
	public static String buildUrl(final String requestedUrl, final Map paramValues)
	{
		if (paramValues==null || paramValues.size()==0)
			return requestedUrl;
		
		final StringBuffer improvedUrl = new StringBuffer();
		improvedUrl.append(requestedUrl);
		
		// if last character is a slash, remove it
		
		if (improvedUrl.charAt(improvedUrl.length()-1)=='/')
			improvedUrl.setLength(improvedUrl.length()-1);
		
		if (improvedUrl.indexOf("?")==-1)
			improvedUrl.append("?");
		
		Iterator walk = paramValues.entrySet().iterator();
		
		while (walk.hasNext())
		{
			Map.Entry entry = (Map.Entry) walk.next();
			improvedUrl.append(entry.getKey());
			improvedUrl.append("=");
			
			final String entryString = String.valueOf(entry.getValue());
			
			try
			{
				improvedUrl.append(URLEncoder.encode(entryString, "UTF-8"));
			}
			catch (UnsupportedEncodingException exception)
			{
				throw new BugException(exception); // Come on, it's UTF-8!
			}
			
			if (walk.hasNext())
			{
				if (!RUNNING_ON_WINDOWS)
					improvedUrl.append("&");
				else
					improvedUrl.append("^&");
			}
		}
		
		return improvedUrl.toString();
	}
	
	public static boolean openUrl(URL requestedUrl)
	{
		return openUrl(requestedUrl.toString());
	}
	
	/**
	 * @return the kerning TextAttribute if running under Java 6 or highter.
	 * Otherwise, null.
	 */
	public static TextAttribute findKerningTextAttribute()
	{
		if (RUNNING_ON_JAVA_60_OR_HIGHER)
		{
			try
			{
				final Field textAttributeField = TextAttribute.class.getField("KERNING");
				// System.out.println("TextAttribute.KERNING_ON="+TextAttribute.KERNING_ON);
				final TextAttribute found = (TextAttribute) textAttributeField.get(TextAttribute.class);
				// System.out.println("found="+found);
				return found;
			}
			catch (NoSuchFieldException exception)
			{
				exception.printStackTrace();
				return null;
			}
			catch (IllegalAccessException exception)
			{
				throw new BugException(exception); // Bug
			}
		}
		else
		{
			return null;
		}
	}
}
