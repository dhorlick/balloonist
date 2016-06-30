/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import java.awt.Color;

/**
 * @author dhorlick
 */
public interface Colorful
{
	public Color getFillColor();
	public void setFillColor(Color designatedColor);
	
	public Color getOutlineColor();
	public void setOutlineColor(Color designatedColor);
}
