/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.layout.Colorful;
import java.awt.Color;

/**
 *
 * @author dhorlick
 */
public class AbstractColorful implements Colorful
{
	private Color fillColor = Color.white;
	private Color outlineColor = Color.black;
	
	public AbstractColorful()
	{
	}

	public Color getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(Color fillColor)
	{
		this.fillColor = fillColor;
	}

	public Color getOutlineColor()
	{
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor)
	{
		this.outlineColor = outlineColor;
	}
}
