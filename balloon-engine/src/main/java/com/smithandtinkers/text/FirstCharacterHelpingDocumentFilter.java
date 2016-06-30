/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;

/**
 *
 * @author dhorlick
 */
public class FirstCharacterHelpingDocumentFilter extends DocumentFilter
{
	public FirstCharacterHelpingDocumentFilter(AbstractDocument designatedDefaultStyledDocument)
	{
		designatedDefaultStyledDocument.setDocumentFilter(this);
	}
	
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws BadLocationException
	{
		// System.out.println("inserting \""+string+"\" w. attr: "+attr + " @ "+offset);

		attr = fix(attr, offset);
		super.insertString(fb, offset, string, attr);
	}

	public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
	{
		// System.out.println("replacing \""+text+"\" w. attrs: "+attrs + " @ "+offset);

		attrs = fix(attrs, offset);
		super.replace(fb, offset, length, text, attrs);
	}

	/**
	 * Solves a highly-annoying problem with DefaultStyledDocument
	 */
	private AttributeSet fix(AttributeSet attrs, int offset)
	{
		if (offset==0)
		{
			if (attrs!=null)
				attrs = new SimpleAttributeSet(attrs);
			else
				attrs = new SimpleAttributeSet();
		}

		return attrs;
	}
}
