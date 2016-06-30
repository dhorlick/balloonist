/*
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers;

import com.smithandtinkers.layout.Balloon;
import com.smithandtinkers.layout.BalloonistPreferences;
import com.smithandtinkers.layout.PreferencesPlumb;
import com.smithandtinkers.text.NoOpStyleStrategy;
import com.smithandtinkers.text.StyleStrategy;
import com.smithandtinkers.util.NoOpPlatformStrategy;
import com.smithandtinkers.util.PlatformFriend;
import com.smithandtinkers.util.PlatformStrategy;
import com.smithandtinkers.util.VersionNumber;
import java.awt.Font;
import java.util.ResourceBundle;

/**
 * Provides access to preferences and configuration for the Balloon Engine.
 * 
 * @author dhorlick
 */
public class BalloonEngineState
{
	private boolean agreedToTerms;

	private BalloonistPreferences balloonistPreferences = new BalloonistPreferences();
	private PlatformStrategy platformStrategy = new NoOpPlatformStrategy();
	private StyleStrategy styleStrategy = new NoOpStyleStrategy();

	private static BalloonEngineState INSTANCE = new BalloonEngineState();

	public static final ResourceBundle APP_PROPS = ResourceBundle.getBundle("resources/text/app");
	public static final ResourceBundle DIALOG_TEXT = ResourceBundle.getBundle("resources/text/dialog");

	public static final VersionNumber VERSION = VersionNumber.parse(APP_PROPS.getString("version"));

	private BalloonEngineState()
	{
		try
		{
			agreedToTerms = determineAgreedToTerms();
			PreferencesPlumb.load(balloonistPreferences);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public static BalloonEngineState getInstance()
	{
		return INSTANCE;
	}

	public Font getDefaultFont()
	{
		return balloonistPreferences.getDefaultFont();
	}

	public boolean isPreserveAccuracy()
	{
		return getBalloonistPreferences().isPreserveAccuracyOverEditability();
	}

	public boolean isIgnoreFontKerning()
	{
		return getBalloonistPreferences().isIgnoreFontKerning();
	}

	public boolean determineAgreedToTerms()
	{
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			return PreferencesPlumb.loadAgreedToTerms();
		}
		else
			return false;
	}

	public boolean isAgreedToTerms()
	{
		return agreedToTerms;
	}

	public void setAgreedToTerms(boolean designatedAgreedToTerms)
	{
		agreedToTerms = designatedAgreedToTerms;
	}

	/**
	 * @return The default margin as chosen in the application preferences.
	 */
	public static double determineDefaultMargin()
	{
		return getInstance().getBalloonistPreferences().getDefaultInnerMarginInPoints();
	}

	public BalloonistPreferences getBalloonistPreferences()
	{
		return balloonistPreferences;
	}

    public void applyStyleDefaultsTo(Balloon balloon)
	{
		if (balloon==null)
			return;

		if (getBalloonistPreferences().getDefaultColorfulness()!=null)
		{
			balloon.setFillColor(getBalloonistPreferences().getDefaultColorfulness().getFillColor());
			balloon.setOutlineColor(getBalloonistPreferences().getDefaultColorfulness().getOutlineColor());
		}
		balloon.setLineThickness(getBalloonistPreferences().getDefaultLineThicknessInPoints());
		balloon.setInnerMarginInPoints(getBalloonistPreferences().getDefaultInnerMarginInPoints());
	}

	public PlatformStrategy getPlatformStrategy()
	{
		return platformStrategy;
	}

	public void setPlatformStrategy(final PlatformStrategy designatedPlatformStrategy)
	{
		platformStrategy = designatedPlatformStrategy;
	}

	public StyleStrategy getStyleStrategy()
	{
		return styleStrategy;
	}

	public void setStyleStrategy(final StyleStrategy designatedStyleStrategy)
	{
		styleStrategy = designatedStyleStrategy;
	}	
}
