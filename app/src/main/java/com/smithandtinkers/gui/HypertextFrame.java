/**
 * Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.StyleConstants;


public class HypertextFrame extends JFrame
{
	public HypertextFrame(String designatedTitle, HypertextPane designatedHypertextPane)
	{
		super(designatedTitle);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // since this sucker could take up a lot of memory
		
		boolean frames = usesHtmlFrames((HTMLDocument)designatedHypertextPane.getDocument());
		
		// System.out.println("frames="+frames);
		
		if (!frames)
		{
			JScrollPane htmlScrollPane = new JScrollPane(designatedHypertextPane);
			htmlScrollPane.setVerticalScrollBarPolicy(
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getContentPane().add(htmlScrollPane);
		}
		else
		{
			getContentPane().add(designatedHypertextPane);
		}
	}
	
	public static boolean usesHtmlFrames(HTMLDocument doc)
	{
		// TODO ensure that page has fully loaded, somehow. this is surprisingly hard to do statelessly
		
		ElementIterator walk = new ElementIterator(doc);
		Element item = null;
	
		while ((item = walk.next()) != null)
		{
			Object nameTag = item.getAttributes().getAttribute(StyleConstants.NameAttribute);
			
			// System.out.println("nameTag="+nameTag);
			
			if (HTML.Tag.FRAME==nameTag || HTML.Tag.FRAMESET==nameTag)
				return true;
		}
		
		return false;
	}
}