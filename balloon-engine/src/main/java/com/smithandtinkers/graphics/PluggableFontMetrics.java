/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.graphics;

import java.awt.FontMetrics;

/**
 * Lets you swap back-and-forth between advertised and determined font metrics.
 *
 * @author dhorlick
 */
public class PluggableFontMetrics
{
	private FontMetrics supposedFontMetrics;
	private EmpiricalFontMetrics empiricalFontMetrics;
	
	static final String NOT_BOTH_ERROR_MESSAGE = "Can't be supposed AND empirical.";
	
	public PluggableFontMetrics()
	{
	}

	public FontMetrics getSupposedFontMetrics()
	{
		return supposedFontMetrics;
	}

	public void setSupposedFontMetrics(FontMetrics designatedSupposedFontMetrics)
	{
		if (designatedSupposedFontMetrics!=null && getEmpiricalFontMetrics()!=null)
			throw new IllegalArgumentException(NOT_BOTH_ERROR_MESSAGE);
		
		supposedFontMetrics = designatedSupposedFontMetrics;
	}

	public EmpiricalFontMetrics getEmpiricalFontMetrics()
	{
		return empiricalFontMetrics;
	}

	public void setEmpiricalFontMetrics(EmpiricalFontMetrics designatedEmpiricalFontMetrics)
	{
		if (designatedEmpiricalFontMetrics!=null && getSupposedFontMetrics()!=null)
			throw new IllegalArgumentException(NOT_BOTH_ERROR_MESSAGE);
		
		empiricalFontMetrics = designatedEmpiricalFontMetrics;
	}

	/**
	 * @see java.awt.FontMetrics#getAscent
	 * @throws NullPointerException if no font metrics implementation has been chosen.
	 */
	public int getAscent()
	{
		if (getSupposedFontMetrics()!=null)
			return getSupposedFontMetrics().getAscent();
		else
			return getEmpiricalFontMetrics().getAscent();
	}

	/**
	 * @see java.awt.FontMetrics#getDescent
	 * @throws NullPointerException if no font metrics implementation has been chosen.
	 */
	public int getDescent()
	{
		if (getSupposedFontMetrics()!=null)
			return getSupposedFontMetrics().getDescent();
		else
			return getEmpiricalFontMetrics().getDescent();
	}

	/**
	 * @see java.awt.FontMetrics#getLeading
	 * @throws NullPointerException if no font metrics implementation has been chosen.
	 */
	public int getLeading()
	{
		if (getSupposedFontMetrics()!=null)
			return getSupposedFontMetrics().getLeading();
		else
			return getEmpiricalFontMetrics().getLeading();
	}
}
