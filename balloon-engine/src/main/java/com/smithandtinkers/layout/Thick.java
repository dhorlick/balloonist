/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.layout;


/**
 *
 * @author dhorlick
 */
public interface Thick
{
	public final static String IDENTIFIER_LINE_THICKNESS = "stroke-width";
	
	public double getLineThickness();
	public void setLineThickness(double designatedLineThickness);
}