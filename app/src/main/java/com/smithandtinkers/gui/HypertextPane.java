/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.PlatformFriend;

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * A component to display and link HTML.
 *
 * @author dhorlick
 */
public class HypertextPane extends JEditorPane
{
	public HypertextPane()
	{
		super();
		
		setEditorKit(new ImmediateLoadingEditorKit());
		
		addHyperlinkListener(new HyperlinkListener()
		{
			public void hyperlinkUpdate(HyperlinkEvent e)
			{
				String asString = urlToText(e.getURL());
				
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					if (asString.indexOf("#")==-1) // TODO no
					{
						PlatformFriend.openUrl(asString);
					}
					else
					{
						if (e instanceof HTMLFrameHyperlinkEvent)
						{
							HTMLFrameHyperlinkEvent asHTMLFrameHyperlinkEvent = (HTMLFrameHyperlinkEvent) e;
							
							((HTMLDocument)getDocument()).processHTMLFrameHyperlinkEvent(asHTMLFrameHyperlinkEvent);
						}

						if (asString.length()>1)
						{
							String numberSignLess = asString.substring(1);
							scrollToReference(numberSignLess);
						}
					}
				}
				else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
				{
					// System.out.println("hyperlink: entered");
					setToolTipText(asString);
					
					// for some reason, framed content doesn't seem to issue this event
				}
				else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
				{
					// System.out.println("hyperlink: entered");
					setToolTipText(null); // TODO figure out how to make it actually dismiss the tooltip sooner, without messing up tooltips elsewhere. ToolTipManager?
					
					// for some reason, framed content doesn't seem to issue this event
				}
				else 
				{
					// System.out.println("unmapped hyperlink event type: "+e);
				}
			}
		});

		setEditable(false);
	}
	
	public static String urlToText(URL requestedUrl)
	{
		if (requestedUrl==null)
			return null;
		
		String asString = requestedUrl.toExternalForm();
		
		if (asString.startsWith("jar:"))
		{
			// TODO verify that it's this jar file
			
			int numberSignPosition = asString.indexOf("#");
			
			if (numberSignPosition!=-1)
				return asString.substring(numberSignPosition);
		}
		
		return asString;
	}
	
	public static class ImmediateLoadingEditorKit extends HTMLEditorKit
	{
    	public Document createDefaultDocument()
    	{
    	    HTMLDocument htmlDoc = (HTMLDocument) super.createDefaultDocument();
    	    htmlDoc.setAsynchronousLoadPriority(-1);
    	    return htmlDoc;
    	} 
	}
}
