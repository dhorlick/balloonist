/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.geom.ShapeFriend;
import java.awt.Font;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Persists and loads application preferences, as well as the product license
 * and whether or not the user has agreed to the terms and conditions.
 * 
 * @author dhorlick
 */
public abstract class PreferencesPlumb
{
	private static final String PREFS_PATH = "/com/smithandtinkers/Balloonist";
	
	private static final String DEFAULT_FONT_FAMILY_KEY = "defaultFontFamily";
	private static final String DEFAULT_FONT_SIZE_KEY = "defaultFontSize";
	
	private static final String PRESERVE_ACCURACY_KEY = "preserveAccuracy";
	private static final String IGNORE_FONT_KERNING_KEY = "ignoreFontKerning";
	private static final String AGREED_TO_TERMS_KEY = "agreedToTerms";
	private static final String DEFAULT_HEIN_PARAMETER_KEY = "defaultHeinParameter";
	private static final String DEFAULT_INNER_MARGIN_IN_POINTS = "defaultInnerMarginInPoints";
	private static final String DEFAULT_LINE_THICKNESS_IN_POINTS = "defaultLineThicknessInPoints";
	
	private static final String DEFAULT_FILL_COLOR = "defaultFillColor";
	private static final String DEFAULT_OUTLINE_COLOR = "defaultOutlineColor";
	
	public static Preferences lookForPreferences()
	{
		try
		{
			if (Preferences.userRoot().nodeExists(PREFS_PATH))
			{
				return Preferences.userRoot().node(PREFS_PATH);
			}
		}
		catch (BackingStoreException exception)
		{
			// oh well
		}
		
		return null;
	}
	
	public static boolean loadAgreedToTerms()
	{
		boolean agreedToTerms = false;
		
		Preferences preferences = lookForPreferences();
		// System.out.println("preferences="+preferences);
		
		if (preferences!=null)
		{
			agreedToTerms = preferences.getBoolean(AGREED_TO_TERMS_KEY, false);			
		}
		
		return agreedToTerms;
	}
	
	public static void saveAgreedToTerms(boolean designatedAgreedToTerms)
	{
		Preferences preferences = Preferences.userRoot().node(PREFS_PATH);
		
		if (preferences!=null)
		{
			preferences.putBoolean(AGREED_TO_TERMS_KEY, designatedAgreedToTerms);
		}
	}
	
	/**
	 * Loads application preferences for the persistent store and superimposes them
	 * over the provided BalloonistPreferences object. Defaults within this object
	 * will be preserved in absense of explicit user choices.
	 */
	public static void load(BalloonistPreferences designatedBalloonistPreferences)
	{
		Preferences persistentPreferences = lookForPreferences();
		// System.out.println("preferences="+preferences);
		
		if (persistentPreferences!=null)
		{
			Font defaultFont = null;
		
			String defaultFontFamily = persistentPreferences.get(DEFAULT_FONT_FAMILY_KEY, null);
			int defaultFontSize = persistentPreferences.getInt(DEFAULT_FONT_SIZE_KEY, 0);
			
			// System.out.println("defaultFontFamily: "+defaultFontFamily);
			// System.out.println("defaultFontSize: "+defaultFontSize);
			
			if (defaultFontFamily!=null && defaultFontSize!=0)
			{
				designatedBalloonistPreferences.setDefaultFont(new Font(defaultFontFamily, Font.PLAIN, defaultFontSize));
			}
			
			designatedBalloonistPreferences.setPreserveAccuracyOverEditability(
					persistentPreferences.getBoolean(PRESERVE_ACCURACY_KEY, 
					designatedBalloonistPreferences.isPreserveAccuracyOverEditability()));
			
			designatedBalloonistPreferences.setIgnoreFontKerning(
					persistentPreferences.getBoolean(IGNORE_FONT_KERNING_KEY,
					designatedBalloonistPreferences.isIgnoreFontKerning()));
			
			designatedBalloonistPreferences.setDefaultHeinParameter(
					persistentPreferences.getDouble(DEFAULT_HEIN_PARAMETER_KEY, 
					designatedBalloonistPreferences.getDefaultHeinParameter()));
			
			designatedBalloonistPreferences.setDefaultInnerMarginInPoints(
					persistentPreferences.getDouble(DEFAULT_INNER_MARGIN_IN_POINTS,
					designatedBalloonistPreferences.getDefaultInnerMarginInPoints()));
			
			String defaultFillColorHexString = persistentPreferences.get(DEFAULT_FILL_COLOR, null);
			String defaultOutlineColorHexString = persistentPreferences.get(DEFAULT_OUTLINE_COLOR, null);
			
			if (defaultFillColorHexString!=null || defaultOutlineColorHexString!=null)
			{
				final Colorful colorful = new AbstractColorful();
				colorful.setFillColor(ShapeFriend.parseColor(defaultFillColorHexString));
				colorful.setOutlineColor(ShapeFriend.parseColor(defaultOutlineColorHexString));
				designatedBalloonistPreferences.setDefaultColorfulness(colorful);
			}
			
			designatedBalloonistPreferences.setDefaultLineThicknessInPoints(
					persistentPreferences.getDouble(DEFAULT_LINE_THICKNESS_IN_POINTS, 
					designatedBalloonistPreferences.getDefaultLineThicknessInPoints()));
		}
	}
	
	public static void save(BalloonistPreferences balloonistPreferences)
	{
		final Preferences persistentPreferences = Preferences.userRoot().node(PREFS_PATH);
		
		if (persistentPreferences!=null)
		{
			persistentPreferences.put(DEFAULT_FONT_FAMILY_KEY, balloonistPreferences.getDefaultFont().getFamily());
			persistentPreferences.putInt(DEFAULT_FONT_SIZE_KEY, balloonistPreferences.getDefaultFont().getSize());
			persistentPreferences.putBoolean(PRESERVE_ACCURACY_KEY, balloonistPreferences.isPreserveAccuracyOverEditability());
			persistentPreferences.putBoolean(IGNORE_FONT_KERNING_KEY, balloonistPreferences.isIgnoreFontKerning());
			persistentPreferences.putDouble(DEFAULT_HEIN_PARAMETER_KEY, balloonistPreferences.getDefaultHeinParameter());
			persistentPreferences.putDouble(DEFAULT_INNER_MARGIN_IN_POINTS, balloonistPreferences.getDefaultInnerMarginInPoints());
			
			if (balloonistPreferences.getDefaultColorfulness()!=null)
			{
				persistentPreferences.put(DEFAULT_OUTLINE_COLOR, ShapeFriend.describe(balloonistPreferences.getDefaultColorfulness().getOutlineColor()));
				persistentPreferences.put(DEFAULT_FILL_COLOR, ShapeFriend.describe(balloonistPreferences.getDefaultColorfulness().getFillColor()));
			}
			
			persistentPreferences.putDouble(DEFAULT_LINE_THICKNESS_IN_POINTS, balloonistPreferences.getDefaultLineThicknessInPoints());
		}
	}
}
