/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.util.Map;
import java.awt.Graphics2D;

/**
 * A style strategy that doesn't do anything.
 *
 * @author dhorlick
 */
public class NoOpStyleStrategy implements StyleStrategy
{
	public Map replaceUnsavoryAttributes(Graphics2D graphics2D, Map requestedAttributesMap)
	{
		return requestedAttributesMap;
	}
}
