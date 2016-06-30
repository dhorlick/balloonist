/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * A {@link javax.swing.text.PlainDocument} implementation that will not add runs of whitespace
 * to itself. If an attempt is made to insert more than one whitespace characters, they will be
 * collapsed into a single whitespace character.
 *
 * @author dhorlick
 */
public class SpacelessDocument extends PlainDocument
{
	public SpacelessDocument()
	{
		super();
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
	{
		str = removeSpaces(str);
		
		super.insertString(offs, removeSpaces(str), a);
	}
	
	public static String removeSpaces(String theString)
	{
		StringBuffer spaceless = new StringBuffer();
		
		for (int loop=0; loop<=theString.length()-1; loop++)
		{
			if (!Character.isWhitespace(theString.charAt(loop))) // TODO or was there some problem with this method?
				spaceless.append(theString.charAt(loop));
		}
		
		return spaceless.toString();
	}
	
}
