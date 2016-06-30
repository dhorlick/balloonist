/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.graphics.FontFriend;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates user preferences for the Balloonist Application.
 *
 * @author dhorlick
 */
public class BalloonistPreferences
{
	private Font defaultFont = determineSanSerifFont();

	/**
	 * if false, preserves editability
	 */
	private boolean preserveAccuracyOverEditability;
	private boolean ignoreFontKerning;
	private double defaultHeinParameter = 0.6;
	private double defaultInnerMarginInPoints = Marginal.DEFAULT_MARGIN;
	private Colorful defaultColorfulness;
	private double defaultLineThicknessInPoints = 1.0;

	public Font getDefaultFont()
	{
		return defaultFont;
	}

	public void setDefaultFont(Font defaultFont)
	{
		this.defaultFont = defaultFont;
	}

	public boolean isPreserveAccuracyOverEditability()
	{
		return preserveAccuracyOverEditability;
	}

	public void setPreserveAccuracyOverEditability(boolean preserveAccuracyOverEditability)
	{
		this.preserveAccuracyOverEditability = preserveAccuracyOverEditability;
	}

	public double getDefaultHeinParameter()
	{
		return defaultHeinParameter;
	}

	public void setDefaultHeinParameter(double defaultHeinParameter)
	{
		this.defaultHeinParameter = defaultHeinParameter;
	}
	
	public static Font determineSanSerifFont()
	{
		Font finding = null;
		
		Map fontProps = new HashMap();
		fontProps.put(TextAttribute.FAMILY, FontFriend.IDENTIFIER_SANS_SERIF);

		finding = new Font(fontProps);


		return finding;
	}

	public double getDefaultInnerMarginInPoints()
	{
		return defaultInnerMarginInPoints;
	}

	public void setDefaultInnerMarginInPoints(double defaultInnerMarginInPoints)
	{
		this.defaultInnerMarginInPoints = defaultInnerMarginInPoints;
	}

	public Colorful getDefaultColorfulness()
	{
		return defaultColorfulness;
	}

	public void setDefaultColorfulness(Colorful defaultColorfulness)
	{
		this.defaultColorfulness = defaultColorfulness;
	}

	public double getDefaultLineThicknessInPoints()
	{
		return defaultLineThicknessInPoints;
	}
	
	public void setDefaultLineThicknessInPoints(double designatedDefaultLineThickness)
	{
		defaultLineThicknessInPoints = designatedDefaultLineThickness;
	}
	
	public boolean isIgnoreFontKerning()
	{
		return ignoreFontKerning;
	}
	
	public void setIgnoreFontKerning(final boolean designatedIgnoreFontKerning)
	{
		ignoreFontKerning = designatedIgnoreFontKerning;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.BalloonistPreferences {"
			 + "defaultFont = " + defaultFont + ", "
			 + "preserveAccuracyOverEditability = " + preserveAccuracyOverEditability + ", "
			 + "ignoreFontKerning = " + isIgnoreFontKerning() + ", "
			 + "defaultHeinParameter = " + defaultHeinParameter + ", "
			 + "defaultInnerMarginInPoints = " + defaultInnerMarginInPoints + ", "
			 + "defaultColorfulness = " + defaultColorfulness + ", "
			 + "defaultLineThicknessInPoints = " + defaultLineThicknessInPoints
		+ "}";
	}
}
