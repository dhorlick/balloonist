/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.text;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;


/**
 * A vertical, simplified version of LineBreakMeasurer.
 *
 * Unlike LineBreakMeasurer, it ignores spaces between characters.
 *
 * @author dhorlick
 */
public class VerticalLineBreakMeasurer extends AbstractLineBreakMeasurer
{
	public VerticalLineBreakMeasurer(AttributedCharacterIterator designatedAttributedCharacterIterator, FontRenderContext designatedFontRenderContext)
	{
		setAttributedCharacterIterator(designatedAttributedCharacterIterator);
		setFontRenderContext(designatedFontRenderContext);
	}
	
	public AbstractTextLayout nextLayout(float requestedWrappingHeight)
	{
		return nextLayout(requestedWrappingHeight, -1);
	}
	
	public AbstractTextLayout nextLayout(float requestedWrappingHeight, int requestedEndIndex)
	{
		// System.out.println("nextLayout requestedWrappingHeight="+requestedWrappingHeight+", requestedEndIndex="+requestedEndIndex);
		
		final char[] CHAR_HOLDER = new char[1];
		
		VerticalTextLayout vtl = new VerticalTextLayout();
		
		int accumulatedHeight = 0;
		int characters = 0;
		
		int initialIndex = attributedCharacterIterator.getIndex();
		
		float maxCharWidth = 16.0f; // TODO no
		boolean roomy = true;
		
		char theChar = 0;
		
		for (theChar = attributedCharacterIterator.current(); 
				theChar!=AttributedCharacterIterator.DONE && roomy && (requestedEndIndex==-1 || attributedCharacterIterator.getIndex()<requestedEndIndex);
				theChar=attributedCharacterIterator.next())
		{
			Font theFont = Font.getFont(attributedCharacterIterator.getAttributes()); // TODO only do this at end of run
			
			// System.out.println("theFont="+theFont+ "theChar="+theChar);
			
			CHAR_HOLDER[0]=theChar;
			LineMetrics lm = theFont.getLineMetrics(CHAR_HOLDER, 0, 1, fontRenderContext);
			
			if (accumulatedHeight + lm.getHeight() <requestedWrappingHeight)
			{
				if (lm.getAscent()>vtl.getAscent())
					vtl.setAscent(lm.getAscent());

				if (lm.getDescent()>vtl.getDescent())
					vtl.setDescent(lm.getDescent());

				if (lm.getLeading()>vtl.getLeading())
					vtl.setLeading(lm.getLeading());

				// TODO check width

				accumulatedHeight += lm.getHeight();

				characters++; // TODO redundant to ACI.getCharacterCount() ?
			}
			else
				roomy = false;
		}
		
		// System.out.println("accumulatedHeight="+accumulatedHeight);
		// System.out.println("characters="+characters);
		
		if (accumulatedHeight==0 || characters==0)
			return null;
		
		vtl.setAttributedCharacterIterator(attributedCharacterIterator);
		vtl.setBeginIndex(initialIndex);
		
		if (theChar==AttributedCharacterIterator.DONE)
		{
			vtl.setEndIndex(attributedCharacterIterator.getIndex());
		}
		else
			vtl.setEndIndex(attributedCharacterIterator.getIndex()+1);
		
		vtl.setCharacterCount(vtl.getEndIndex()-vtl.getBeginIndex()); // TODO is this right?
		
		vtl.setBounds(new Rectangle2D.Float(0.0f, 0.0f, maxCharWidth, accumulatedHeight)); // TODO ?
		
		return vtl;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.text.VerticalLineBreakMeasurer {"
			 + "fontRenderContext = " + fontRenderContext + ", "
			 + "attributedCharacterIterator = " + attributedCharacterIterator + ", "
			 + "(position = "+getPosition()+") "
		+ "}";
	}
}
