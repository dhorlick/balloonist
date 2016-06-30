/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.awt.Graphics2D;
import java.util.Map;

/**
 * @author dhorlick
 */
public interface StyleStrategy
{
	public Map replaceUnsavoryAttributes(Graphics2D graphics2D, Map requestedAttributesMap);
}
