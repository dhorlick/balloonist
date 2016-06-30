package com.smithandtinkers.text;

import junit.framework.TestCase;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.util.XmlFriend;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dhorlick
 */
public class StyledDocumentStrawTest extends TestCase
{
	public void testSplit()
	{
		final String JOINED = "frozen-drink:Margarita";
		final String [] SPLITTED = StyledDocumentStraw.split(JOINED, ':');

		if (SPLITTED.length!=2)
			throw new IllegalStateException(String.valueOf(SPLITTED.length));

		if (!"frozen-drink".equals(SPLITTED[0]))
			throw new IllegalStateException(SPLITTED[0]);

		if (!"Margarita".equals(SPLITTED[1]))
			throw new IllegalStateException(SPLITTED[1]);

		final String JOINED2 = "hippo; elephant;rhino";
		final String [] SPLITTED2 = StyledDocumentStraw.split(JOINED2, ';');

		if (SPLITTED2.length!=3)
			throw new IllegalStateException(String.valueOf(SPLITTED2.length));

		if (!"hippo".equals(SPLITTED2[0]))
			throw new IllegalStateException(SPLITTED2[0]);

		if (!"elephant".equals(SPLITTED2[1]))
			throw new IllegalStateException(SPLITTED2[1]);

		if (!"rhino".equals(SPLITTED2[2]))
			throw new IllegalStateException(SPLITTED2[2]);
	}

	public void testPrune()
	{
		String result0 = StyledDocumentStraw.prune("I'd like a Clark bar.");

		if (!"I'd like a Clark bar.".equals(result0))
			throw new IllegalStateException(result0);

		String result1 = StyledDocumentStraw.prune("    I'd   like a   Clark bar.  ");

		if (!" I'd like a Clark bar. ".equals(result1))
			throw new IllegalStateException(result1);

		String result2 = StyledDocumentStraw.prune("\n   \f I'd   li\nke a  \r\r Clark\n\r bar.  \r");

		if (!"\n I'd li\nke a Clark\n bar. ".equals(result2))
			throw new IllegalStateException(result2+"*");
	}

	public void testClone()
	{
		final String brokenLine = "I like corn dogs\nbecause\nthey're covered with corn.";
		final String unbrokenLine = "Feeling hot, hot, hot";

		testClone(unbrokenLine);
		testClone(brokenLine);
	}

	public void testClone(final String text)
	{
		SimpleAttributeSet sas = new SimpleAttributeSet();
		final Font DEFAULT_FONT = BalloonEngineState.getInstance().getDefaultFont();
		StyleConstants.setFontFamily(sas, DEFAULT_FONT.getFamily());
		StyleConstants.setFontSize(sas, DEFAULT_FONT.getSize());

		final StyledDocumentStraw straw = new StyledDocumentStraw(new DefaultStyledDocument());
		StyledDocumentStraw strawClone = null;

		try
		{
			straw.getStyledDocument().insertString(0, text, sas);

			strawClone = (StyledDocumentStraw) straw.clone();

			Element theElement = strawClone.getStyledDocument().getDefaultRootElement();
			while (theElement instanceof AbstractDocument.BranchElement)
			{
				theElement = theElement.getElement(0);
			}

			final AttributeSet sasOut = theElement.getAttributes();
			final String fontFamilyOut = StyleConstants.getFontFamily(sasOut);

			if (!DEFAULT_FONT.getFamily().equals(fontFamilyOut))
			{
				throw new IllegalStateException(fontFamilyOut);
			}
		}
		catch (Exception exception)
		{
			if (strawClone!=null)
				((AbstractDocument)strawClone.getStyledDocument()).dump(System.out);

			throw new RuntimeException(exception);
		}
	}

	public void testSvgExport()
	{
		final String string1 =
					"You were twenty thousand underneath the sea";
			//       01234567890123456789012345678901234567890123456789012345678901234567890
			//       0         1         2         3         4         5         7         8
			//                bbbbbbbbbbbbbbbbbbbbbbbbbb
			//                       uuuuuuuu

		final String string2 = string1.replaceAll(" ", "\n");

		testSvgExport(string1);
		testSvgExport(string2);
	}

	private void testSvgExport(String text)
	{
		final Font DEFAULT_FONT = BalloonEngineState.getInstance().getDefaultFont();

		final StyledDocument styledDoc = new DefaultStyledDocument();
		final StyledDocumentStraw straw = new StyledDocumentStraw(styledDoc);

		Document svgDoc = null;

		try
		{
			final SimpleAttributeSet sasDefault = new SimpleAttributeSet();
			final SimpleAttributeSet sasBold = new SimpleAttributeSet();
			final SimpleAttributeSet sasUnderline = new SimpleAttributeSet();

			svgDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			straw.getStyledDocument().insertString(0, text, sasDefault);

			StyleConstants.setBold(sasBold, true);
			StyleConstants.setFontFamily(sasDefault, DEFAULT_FONT.getFamily());
			StyleConstants.setFontSize(sasDefault, DEFAULT_FONT.getSize());

			StyleConstants.setUnderline(sasUnderline, true);

			styledDoc.setCharacterAttributes( 9, 34- 9+1, sasBold, false);
			styledDoc.setCharacterAttributes(16, 23-16+1, sasUnderline, false);

			final org.w3c.dom.Element testElement = svgDoc.createElement("text");
			straw.save(svgDoc, testElement);
			svgDoc.appendChild(testElement);
			XmlFriend.print(svgDoc);

			NodeList nodeList = testElement.getElementsByTagName("tspan");
			Node underlinedTspanElement = null;

			for (int index=0; index<nodeList.getLength(); index++)
			{
				if (nodeList.item(index).hasAttributes())
				{
					final org.w3c.dom.Element tspanElement = (org.w3c.dom.Element) nodeList.item(index);

					if (tspanElement.hasAttribute("style") && tspanElement.getAttribute("style").indexOf("underline")!=-1)
						underlinedTspanElement = tspanElement;
				}
			}

			if (underlinedTspanElement==null)
				throw new IllegalStateException("Couldn't find an underlined tspan element.");

			final String underlinedText = XmlFriend.uncoverText(underlinedTspanElement);

			if (!"thousand".equals(underlinedText.trim()))
			{
				show(styledDoc);
				((AbstractDocument)styledDoc).dump(System.out);
				throw new IllegalStateException(underlinedText+"!=\"thousand\"");
			}
		}
		catch (Exception exception)
		{
			XmlFriend.print(svgDoc);

			throw new RuntimeException(exception);
		}
	}

	public static void show(StyledDocument styledDoc)
	{
		final JFrame frame = new JFrame("Styled Document");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final JTextPane pane = new JTextPane(styledDoc);
		frame.getContentPane().add(pane);
		frame.setSize(450, 400);
		frame.setVisible(true);
	}
}
