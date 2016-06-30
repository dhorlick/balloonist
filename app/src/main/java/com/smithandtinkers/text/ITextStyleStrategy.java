/**
 * Copyleft 2009 by Dave Horlick

 */

package com.smithandtinkers.text;

import com.lowagie.text.pdf.PdfGraphics2D;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A style strategy that filters out things which can't be handled in PDF.
 *
 * @author dhorlick
 */
public class ITextStyleStrategy implements StyleStrategy
{
	/**
	 * Produces a filtered Map, replacing text attributes in a Map that would be unpalatable to
	 * the PDFGraphics2D.drawString()
	 * method in iText, which only accepts FONT, SIZE, UNDERLINE, and SUPERSCRIPT
	 */
	public Map replaceUnsavoryAttributes(final Graphics2D graphics2D, Map requestedAttributesMap)
	{
		if (requestedAttributesMap==null)
			return null;

		if (graphics2D instanceof PdfGraphics2D)
		{
			// System.out.println("requestedAttributesMap.getClass().getName()="+requestedAttributesMap.getClass().getName());

			Font replacement = null;

			Map filtered = new HashMap();

			Iterator walk = requestedAttributesMap.keySet().iterator();

			// System.out.println("requestedAttributesMap before filtering: "+requestedAttributesMap);

			while (walk.hasNext())
			{
				TextAttribute textAttribute = (TextAttribute) walk.next();
				if (textAttribute == TextAttribute.FAMILY)
				{
					Font font = new Font(requestedAttributesMap);
				}

				if (textAttribute == TextAttribute.FONT || textAttribute == TextAttribute.SIZE
						|| textAttribute == TextAttribute.UNDERLINE || textAttribute == TextAttribute.SUPERSCRIPT)
				{
					filtered.put(textAttribute, requestedAttributesMap.get(textAttribute));
				}
			}

			// System.out.println("requestedAttributesMap after removal: "+requestedAttributesMap);

			if (replacement!=null)
			{
				// System.out.println("setting font replacement...");
				filtered.put(TextAttribute.FONT, replacement);
			}

			// System.out.println("requestedAttributesMap after filtering: "+requestedAttributesMap);

			return filtered;
		}
		else
		{
			return requestedAttributesMap;
		}
	}
}
