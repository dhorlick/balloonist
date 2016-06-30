/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.text;

import java.awt.Font;
import java.awt.font.*;
import java.text.*;
import java.util.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

import com.smithandtinkers.graphics.FontFriend;
import com.smithandtinkers.layout.Orientable;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.*;

import com.smithandtinkers.BalloonEngineState;


/**
 * Presents information from StyledDocuments in forms that are palatable to other packages like java.text 
 * and org.w3c.dom.
 * 
 * @author dhorlick
 */
public class StyledDocumentStraw implements PubliclyCloneable, Orientable
{
	private StyledDocument styledDocument;
	private AttributedString attributedString;
	
	private boolean vertical = false;
	private final SingleThreadedChangeSupport changeSupport = new SingleThreadedChangeSupport();
	
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	private final static List fontFamilyNames = FontFriend.determineFontFamilyNames();
	
	public static final String IDENTIFIER_TEXT = "text";
	public static final String IDENTIFIER_TSPAN = "tspan";
	private static final String IDENTIFIER_STYLE_ATTRIBUTE = "style";
	private static final char IDENTIFIER_LET = ':';
	private static final char IDENTIFIER_DELIMIT = ';';
	
	private static final String IDENTIFIER_WEIGHT = "font-weight";
		private static final String IDENTIFIER_WEIGHT_BOLD = "bold";
	
	private static final String IDENTIFIER_STYLE = "font-style";
		private static final String IDENTIFIER_STYLE_ITALICS = "italics";
		
	private static final String IDENTIFIER_TEXT_DECORATION = "text-decoration";
		private static final String IDENTIFIER_DECORATION_UNDERLINE = "underline";
	
	private static final String IDENTIFIER_FAMILY = "font-family";
	private static final String IDENTIFIER_SIZE = "font-size";
	
	public static final String IDENTIFIER_MONOSPACE = "monospace";
	public static final String IDENTIFIER_SERIF = "serif";
	public static final String IDENTIFIER_SANS_SERIF = "sans-serif";
		
	public StyledDocumentStraw()
	{
	}
	
	public StyledDocumentStraw(StyledDocument designatedStyledDocument)
	{
		setStyledDocument(designatedStyledDocument);
	}

	public void setStyledDocument(StyledDocument designatedStyledDocument)
	{
		styledDocument = designatedStyledDocument;
		
		refresh();
	}
	
	public StyledDocument getStyledDocument()
	{
		return styledDocument;
	}
	
	public AttributedString toAttributedString()
	{
		if (styledDocument==null)
			return null;
		
		try
		{
			attributedString = new AttributedString(styledDocument.getText(0, styledDocument.getLength()));
			
			if (styledDocument.getLength()>0 && PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER  
					&& !BalloonEngineState.getInstance().isIgnoreFontKerning())
			{
				attributedString.addAttribute(PlatformFriend.KERNING_TEXT_ATTRIBUTE, PlatformFriend.KERNING_ON);
			}	
		}
		catch (BadLocationException e)
		{
			throw new BugException(e);
		}
		
		// System.out.println("styledDocument.getEndPosition()="+styledDocument.getEndPosition());

		javax.swing.text.Element swingRootElement = styledDocument.getDefaultRootElement();

		// Logger.print("element count = ");
		// Logger.println(appropriateElementTree.getElement(0).getElementCount());

		for (int paragraphLoop=0; paragraphLoop<=swingRootElement.getElementCount()-1; paragraphLoop++)
		{
			javax.swing.text.Element paragraph = swingRootElement.getElement(paragraphLoop);

			for (int loop=0; loop<=paragraph.getElementCount()-1; loop++)
			{
				javax.swing.text.Element currentSwingElement = paragraph.getElement(loop);
				javax.swing.text.Document swingDoc = currentSwingElement.getDocument();
				AttributeSet attributes = currentSwingElement.getAttributes();

				applySwingAttributesToAttributeString(attributes, attributedString, currentSwingElement.getStartOffset(), currentSwingElement.getEndOffset(), null);
			}
		}
		
		return attributedString;
	}
	
	public void save(org.w3c.dom.Document doc, Node parent)
	{
		save(doc, parent, -1 , -1, null);
	}
	
	/**
	 * @param requestedStartCharacterOffset optional. to withhold, provide zero.
	 */
	public void save(org.w3c.dom.Document doc, Node parent, int requestedStartCharacterOffset, int requestedLength, String designatedStyledTextElementName, int sectionIndex)

	{
		AttributedString attrString = toAttributedString(sectionIndex);
		StyledDocumentStraw segmented = new StyledDocumentStraw();
		segmented.load(attrString);
		segmented.save(doc, parent, requestedStartCharacterOffset, requestedLength, designatedStyledTextElementName);
	}

	public void save(org.w3c.dom.Document doc, Node parent, int requestedStartCharacterOffset, int requestedLength, String designatedStyledTextElementName)
	{
		int requestedEndCharacterOffset = -1;
		boolean[] encounteredFirstLine = new boolean[1];
		encounteredFirstLine[0] = false;
		if (requestedStartCharacterOffset > -1) {
			// System.out.println("\nrequestedStartCharacterOffset: "+requestedStartCharacterOffset);
			// System.out.println("requestedLength: "+requestedLength);
			// System.out.println("styledDocument.getLength(): "+styledDocument.getLength());
			requestedEndCharacterOffset = requestedStartCharacterOffset + requestedLength;
		}
		if (styledDocument != null) {
			/**
			 * Every time we encounter a new attribute set in the DOM, we will create a new element for
			 * it and that element here key on the attribute set.
			 *
			 * If the attribute set is ever encountered again, we will be able to look up the old element
			 * and create any new sub-elements inside of that.
			 */
			Map elementCorrespondingToAttributeSet = new HashMap();
			elementCorrespondingToAttributeSet.put(new SimpleAttributeSet(), parent);
			// org.w3c.dom.Element cursor = (org.w3c.dom.Element)parent;
			javax.swing.text.Element appropriateElementTree = getAppropriateElementTree();
			try {
				save(doc, parent, requestedStartCharacterOffset, requestedEndCharacterOffset, designatedStyledTextElementName, requestedLength, appropriateElementTree, encounteredFirstLine, elementCorrespondingToAttributeSet);
			} catch (OutOfTextException exception) {
				// fine
			}
		}
	}

	private void save(final org.w3c.dom.Document doc, final Node parent, final int requestedStartCharacterOffset,
			final int requestedEndCharacterOffset, final String designatedStyledTextElementName, final int requestedLength, final Element element, boolean [] encounteredFirstLine,
			final Map elementCorrespondingToAttributeSet)
			throws OutOfTextException
	{
		
		int startElementIndex = 0;
			
		if (requestedStartCharacterOffset > 0)
		{
			startElementIndex = element.getElementIndex(requestedStartCharacterOffset);
			// System.out.println("start element Index: "+startElementIndex);
		}
		
		/* if (requestedStartCharacterOffset > 0)
		{
			System.out.print("appropriateElementTree's element count = ");
			System.out.println(appropriateElementTree.getElementCount());
		} */
		
		
		for (int loop=startElementIndex; loop<=element.getElementCount()-1; loop++)
		{
			/* if (requestedStartCharacterOffset > 0)
			{
				System.out.println("loop="+loop);
			} */

			javax.swing.text.Element currentSwingElement = element.getElement(loop);
			
			if (requestedLength>0 && currentSwingElement.getStartOffset()>requestedStartCharacterOffset+requestedLength)
			{
				/*
				System.out.println("this line falls outside parameters... skipping");
				System.out.println("\tcurrentSwingElement.getStartOffset()="+currentSwingElement.getStartOffset());
				System.out.println("\trequestedStartCharacterOffset="+requestedStartCharacterOffset);
				System.out.println("\trequestedLength="+requestedLength);
				*/
				throw new OutOfTextException();
			}
			
			AttributeSet attributes = excavateAttributes(currentSwingElement);
			
			org.w3c.dom.Element elem = null;
			elem = findCorrespondingElementForAttributeSet(elementCorrespondingToAttributeSet, attributes);

			if (doc!=null && elem==null)
			{
				elem = createElement(doc, attributes, (designatedStyledTextElementName==null)?IDENTIFIER_TSPAN:designatedStyledTextElementName);
				parent.appendChild(elem);
				
				// System.out.print("created new element with attributes: ");
				// print(attributes);
				
				// TODO cache new element into elementCorrespondingToAttributeSet?
			}

			String plainText =  null;
			int length = 0;
			int startCharacterOffset = 0;
			int endCharacterOffset = -1;
			
			if (!encounteredFirstLine[0] && requestedStartCharacterOffset>-1 && requestedStartCharacterOffset<=currentSwingElement.getEndOffset())
			{
				startCharacterOffset = requestedStartCharacterOffset;
				encounteredFirstLine[0] = true;
			}
			else
				startCharacterOffset = currentSwingElement.getStartOffset();

			if (requestedEndCharacterOffset>-1 && requestedEndCharacterOffset<currentSwingElement.getEndOffset())
				endCharacterOffset = requestedEndCharacterOffset;
			else
				endCharacterOffset = currentSwingElement.getEndOffset();
			
			length = endCharacterOffset-startCharacterOffset;

			
			// from http://java.sun.com/j2se/1.3/docs/api/javax/swing/text/Element.html#getEndOffset()
			//      "AbstractDocument models an implied break at the end of the document."
			// so...

			if (loop==element.getElementCount()-1 && length>0 && element==getAppropriateElementTree())
				length--;
			
			if (length<0)
			{
				/*
				System.out.println("length is less than zero: "+length);
				System.out.println("\tcurrentSwingElement.getStartOffset()="+currentSwingElement.getStartOffset());
				System.out.println("\trequestedStartCharacterOffset="+requestedStartCharacterOffset);
				System.out.println("\trequestedLength="+requestedLength);
				*/
				throw new OutOfTextException();
			}
			
			if (currentSwingElement.isLeaf() || currentSwingElement.getElementCount()==0)
			{
				try
				{
					plainText = styledDocument.getText(startCharacterOffset, length);
				}
				catch (BadLocationException exception)
				{
					throw new BugException("There may be a bug in the code that converts styled text to marked up plain text.", exception);
				}

				if (plainText!=null && plainText.length()>0)
				{
					// System.out.println("Applying: *"+plainText+"*");

					if (doc!=null)
					{
						Node newXmlTextElement = doc.createTextNode(plainText);
						elem.appendChild(newXmlTextElement);
					}
				}
			}
			else
			{
				save(doc, parent, startCharacterOffset, endCharacterOffset, 
						designatedStyledTextElementName, length, currentSwingElement, 
						encounteredFirstLine, elementCorrespondingToAttributeSet);
			}
		}
	}
	
	private Element getAppropriateElementTree()
	{
		// Accordingly to O'Reilly's Java Swing, Edition 2:
		
		// "Actually, Document supports the existence of multiple Element trees,
		//  though typically there is only one."
		
		// That one is the one we want.
		
		Element element = styledDocument.getDefaultRootElement();
		
		if (element.getElementCount()==1)
		{
			element = element.getElement(0);
		}
		
		return element;
	}
	
	/**
	 * @return The corresponding Element, or null if none could be found.
	 */
	private org.w3c.dom.Element findCorrespondingElementForAttributeSet(Map elementCorrespondingToAttributeSet, AttributeSet attributes)
	{
		Iterator walk = elementCorrespondingToAttributeSet.entrySet().iterator();
		
		while (walk.hasNext())
		{
			Map.Entry entry = (Map.Entry) walk.next();
			AttributeSet current = (AttributeSet) entry.getKey();
			if (current.isEqual(attributes) || attributes.isEqual(current))
			{
				return (org.w3c.dom.Element) entry.getValue();
			}
		}
		
		return null;
	}

	private AttributeSet excavateAttributes(Element currentSwingElement)
	{
		AttributeSet asset = currentSwingElement.getAttributes();
		
		if (asset==null)
			return asset;
		
		while (asset instanceof AbstractDocument.AbstractElement)
		{
			AttributeSet innerAsset = ((AbstractDocument.AbstractElement)asset).getAttributes();
			
			if (innerAsset==asset)
				return asset;
			else
				asset = innerAsset;
		}
		
		return asset;
	}

	public org.w3c.dom.Element createElement(Document doc, AttributeSet swingAttrs, String styledTextAttributeName)
	{
		final List styles = new ArrayList();
		
		applySwingDocAttributeSetToCssStyleStringsList(swingAttrs, styles);
		org.w3c.dom.Element elem = doc.createElement(styledTextAttributeName);
		
		if (styles.size()>0)
			elem.setAttribute(IDENTIFIER_STYLE_ATTRIBUTE, delimit(styles, IDENTIFIER_DELIMIT));
		
		return elem;
	}
	
	private void applySwingDocAttributeSetToCssStyleStringsList(AttributeSet swingAttrs, List styles)
	{
		if (swingAttrs==null || styles==null)
			return;
		
		Enumeration walk = swingAttrs.getAttributeNames();
		
		while (walk.hasMoreElements())
		{
			final Object item = walk.nextElement();
			final Object value = swingAttrs.getAttribute(item);

			if (item == StyleConstants.FontConstants.Family)
			{
				// attrStr.addAttribute(TextAttribute.FAMILY, swingAttrs.getAttribute(StyleConstants.FontConstants.Family), beginIndex, endIndex);

				styles.add(IDENTIFIER_FAMILY+IDENTIFIER_LET
					+javaFontFamilyToSvg( (String) value));
			}
			else if (item == StyleConstants.CharacterConstants.Size)
			{
				Integer intSize = (Integer) value;

				// TODO append "pt" units?

				styles.add(IDENTIFIER_SIZE + IDENTIFIER_LET + intSize.intValue());
			}
			else if (item == StyleConstants.CharacterConstants.Bold)
			{
				// attrStr.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, beginIndex, endIndex);
				
				if (value!=Boolean.FALSE)
					styles.add(IDENTIFIER_WEIGHT + IDENTIFIER_LET + IDENTIFIER_WEIGHT_BOLD);
				
				// TODO else un-bold tendrils?
			}
			else if (item == StyleConstants.CharacterConstants.Italic)
			{
				// attrStr.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, beginIndex, endIndex);
				
				if (value!=Boolean.FALSE)
					styles.add(IDENTIFIER_STYLE+IDENTIFIER_LET+IDENTIFIER_STYLE_ITALICS);
			}
			else if (item == StyleConstants.CharacterConstants.Underline)
			{
				// attrStr.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, beginIndex, endIndex);
				
				if (value!=Boolean.FALSE)
					styles.add(IDENTIFIER_TEXT_DECORATION + IDENTIFIER_LET + IDENTIFIER_DECORATION_UNDERLINE);
			}
			else if (item == AttributeSet.ResolveAttribute && value instanceof AttributeSet)
			{
				applySwingDocAttributeSetToCssStyleStringsList((AttributeSet)value, styles);

				// TODO look at resolveParent field, too?
			}
			else
			{
				// System.out.println("unrecog: " + item);
			}
		}
	}
	
	public static String delimit(List list, final char delimiter)
	{
		return delimit(list, String.valueOf(delimiter));
	}
	
	/**
	 * @return A String representation of the provided list, separated by the provided delimiter.
	 * Null entries will be ignored.
	 */
	public static String delimit(List list, final String delimiter)
	{
		StringBuffer delimited = new StringBuffer();
		
		Iterator walk = list.iterator();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			
			if (item!=null)
				delimited.append(String.valueOf(item));
			
			if (walk.hasNext())
			{
				delimited.append(delimiter);
			}
		}
		
		return delimited.toString();
	}

	public void refresh()
	{
		toAttributedString();
	}
	
	public AttributedString getMostRecentAttributedString()
	{
		return attributedString;
	}
	
	/**
	 * Applies a given Swing Attribute Set to an attributed string and/or a Map (suitable for deriving a new Font).
	 * @param beginIndex the start index for attributed string application
	 * @param endIndex the end index for attributed string application
	 */
	public void applySwingAttributesToAttributeString(AttributeSet swingAttrs, AttributedString attrStr, int beginIndex, int endIndex, Map map)
	{
		final int docEnd = styledDocument.getEndPosition().getOffset();
		
		if (endIndex>=docEnd)
		{
			if (docEnd>=1)
				endIndex = docEnd-1;
			else
				endIndex = 0;
		}
		
		if (beginIndex==endIndex)
			return;
		
		Enumeration walk = swingAttrs.getAttributeNames();
		
		while (walk.hasMoreElements())
		{
			final Object item = walk.nextElement();
			final Object value = swingAttrs.getAttribute(item);
			
			if (item == StyleConstants.FontConstants.Family) // note: won't work in Java 1.3 OS X
			{
				applyAttribute(attrStr, map, TextAttribute.FAMILY, value, beginIndex, endIndex);
			}
			else if (item == StyleConstants.CharacterConstants.Size)
			{
				Integer intSize = (Integer) value;
				
				// System.out.println("intSize: "+intSize);
				
				if (intSize!=null)
					applyAttribute(attrStr, map, TextAttribute.SIZE, new Float(intSize.floatValue()), beginIndex, endIndex);
			}
			else if (item == StyleConstants.CharacterConstants.Bold && value!=Boolean.FALSE)
			{
				applyAttribute(attrStr, map, TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, beginIndex, endIndex);
			}
			else if (item == StyleConstants.CharacterConstants.Italic && value!=Boolean.FALSE)
			{
				applyAttribute(attrStr, map, TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, beginIndex, endIndex);
			}
			else if (item == StyleConstants.CharacterConstants.Underline && value!=Boolean.FALSE)
			{
				applyAttribute(attrStr, map, TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, beginIndex, endIndex);
			}
			else
			{
				// System.out.println("unrecog: " + item);
			}
		}
	}
	
	public static void applyAttribute(AttributedString designatedAttributedString, Map map, TextAttribute designatedTextAttribute, Object value, int beginIndex, int endIndex)
	{
		/* System.out.println("\tbeginIndex="+beginIndex);
		System.out.println("\tendIndex="+endIndex);
		System.out.println("\tvalue="+value);
		System.out.println("\tdesignatedAttributedString="+designatedAttributedString); */
		
		if (designatedAttributedString!=null)
			designatedAttributedString.addAttribute(designatedTextAttribute, value, beginIndex, endIndex);
		if (map!=null)
			map.put(designatedTextAttribute, value);
	}
	
	public AttributedCharacterIterator getIterator()
	{
		if (styledDocument==null)
			return null;

		return toAttributedString().getIterator();
	}

	/**
	 * @param invalidFontNames a List to which any invalid font names encountered
	 *                         should be added
	 */
	public void load(org.w3c.dom.Element tElem, List invalidFontNames)
	{
		load(tElem, null, invalidFontNames);
	}
	
	/**
	 * @param invalidFontNames a List to which any invalid font names encountered
	 *                         should be added
	 */
	private void load(org.w3c.dom.Element tElem, SimpleAttributeSet sattrset, List invalidFontNames)
	{
		if (styledDocument==null)
			styledDocument = new DefaultStyledDocument();
		
		for (int loop=0; loop<=tElem.getChildNodes().getLength()-1; loop++)
		{
			Node subnode = (Node) tElem.getChildNodes().item(loop);
			
			if (subnode.getNodeType()==Node.TEXT_NODE)
			{
				try
				{
					final String textToInsert = prune(subnode.getNodeValue());
					
					if (textToInsert.length()>0 && (!isEntirelyWhiteSpace(textToInsert) || !StyledDocumentStraw.IDENTIFIER_TEXT.equals(tElem.getNodeName())))
					{
						// final int insertPos = styledDocument.getEndPosition().getOffset();
						final int insertPos = styledDocument.getLength();
						
						// System.out.println("inserting: \""+textToInsert+"\" @ "+insertPos);
					
						styledDocument.insertString(insertPos, textToInsert, sattrset);
						// styledDocument.insertString(0, textToInsert, new SimpleAttributeSet());
					}
				}
				catch (BadLocationException exception)
				{
					throw new BugException(exception);
				}
			}
			else if (subnode instanceof org.w3c.dom.Element)
			{
				SimpleAttributeSet sattrsetLocal = null;
				
				if (sattrset==null)
					sattrsetLocal = new SimpleAttributeSet();
				else
					sattrsetLocal = new SimpleAttributeSet(sattrset);
				
				if (IDENTIFIER_TEXT.equals(subnode.getNodeName()) || IDENTIFIER_TSPAN.equals(subnode.getNodeName()))
				{
					org.w3c.dom.Element subelem = (org.w3c.dom.Element) subnode;
					
					if (subelem.hasAttribute(IDENTIFIER_STYLE_ATTRIBUTE))
					{
						final String [] styleAttrs = split(subelem.getAttribute(IDENTIFIER_STYLE_ATTRIBUTE), IDENTIFIER_DELIMIT);
						for (int styleLoop=0; styleLoop<=styleAttrs.length-1; styleLoop++)
						{
							if (styleAttrs[styleLoop]!=null)
							{
								String attr = styleAttrs[styleLoop].trim();
								applyStyle(sattrsetLocal, attr, invalidFontNames);
							}
						}
					}
				}
				
				load((org.w3c.dom.Element)subnode, sattrsetLocal, invalidFontNames);
			}
		}
		
		// System.out.println("end result: "+determineTextAsString());
		// ((AbstractDocument) styledDocument).dump(System.out);
	}
	
	/**
	 * @param simpleAttrSet the style constants corresponding to the attribute label.
	 * @param invalidFontNames a {@link java.util.List} to which any invalid font names encountered
	 *                         should be added
	 */
	public void applyStyle(SimpleAttributeSet simpleAttrSet, String label, List invalidFontNames)
	{
		if (label==null || simpleAttrSet==null)
			return;
		
		String [] pair = split(label, IDENTIFIER_LET);
		
		if (pair.length<2)
			return;
		
		if (IDENTIFIER_WEIGHT.equals(pair[0]))
		{
			if (IDENTIFIER_WEIGHT_BOLD.equals(pair[1]))
				StyleConstants.setBold(simpleAttrSet, true);
		}
		else if (IDENTIFIER_STYLE.equals(pair[0]))
		{
			if (IDENTIFIER_STYLE_ITALICS.equals(pair[1]))
				StyleConstants.setItalic(simpleAttrSet, true);
		}
		else if (IDENTIFIER_TEXT_DECORATION.equals(pair[0]))
		{
			if (IDENTIFIER_DECORATION_UNDERLINE.equals(pair[1]))
				StyleConstants.setUnderline(simpleAttrSet, true);
		}
		else if (label.startsWith(IDENTIFIER_SIZE))
		{
			StyleConstants.setFontSize(simpleAttrSet, Integer.parseInt(pair[1].trim())); // TODO screen invalid font sizes
			// TODO scoopt off the "pt" suffix
		}
		else if (label.startsWith(IDENTIFIER_FAMILY))
		{
			final String familyAsSvg = pair[1].trim();
			final String familyAsJava = svgFontFamilyToJava(familyAsSvg);
			
			if (invalidFontNames!=null && !fontFamilyNames.contains(familyAsJava))
			{
				invalidFontNames.add(familyAsJava);
				StyleConstants.setFontFamily(simpleAttrSet, BalloonEngineState.getInstance().getDefaultFont().getFamily());
			}
			else
			{
				StyleConstants.setFontFamily(simpleAttrSet, familyAsJava);
			}
		}
	}
	
	public static String [] split(String unsplit, char stake)
	{
		if (unsplit==null)
			return null;
		
		List pieces = new ArrayList();
		
		int lastBreak = -1;
		
		for (int loop=0; loop<=unsplit.length()-1; loop++)
		{
			if (unsplit.charAt(loop)==stake)
			{
				pieces.add(unsplit.substring(lastBreak+1, loop).trim());
				lastBreak = loop;
			}
			else if (loop==unsplit.length()-1)
			{
				pieces.add(unsplit.substring(lastBreak+1).trim());
			}
		}
		
		return (String []) pieces.toArray(new String [pieces.size()]);
	}
	
	public String determineTextAsString()
	{
		if (styledDocument==null)
			return null;
		
		try
		{
			return styledDocument.getText(0, styledDocument.getLength());
		}
		catch (BadLocationException exception)
		{
			System.err.println(exception.getMessage());
			throw new BugException("There may be a bug in the code that converts styled text to plain text in speech.", exception);
		}
	}
	
	public String getPlainText(int characterOffset, int length)
	{
		if (styledDocument==null)
			return null;
		
		try
		{
			return styledDocument.getText(characterOffset, length);
		}
		catch (BadLocationException exception)
		{
			throw new BugException("There may be a bug in the code that converts styled text to marked up plain text.", exception);
		}
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		try
		{
			StyledDocumentStraw cloned = new StyledDocumentStraw(new DefaultStyledDocument());
			cloned.vertical = vertical;
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			org.w3c.dom.Element bottomElement = doc.createElement(IDENTIFIER_TEXT);
			save(doc, bottomElement);
				// inefficient, but reliable
			cloned.load(bottomElement, null);
			
			return cloned;
		}
		catch (RuntimeException exception)
		{
			throw exception;
		}
		catch (Exception exception)
		{
			throw new BugException(exception);
		}
	}
	
	/**
	 * Preserves single spaces and non-collapsible space characters, as defined in
	 * {@link #isCollapsableSpace(char)}.
	 * 
	 * Runs of whitespace accompanying at least one non-whitespace character will be
	 * mapped to a single space.
	 *
	 * Runs of whitespace not accompanying at least one non-whitespace character will be
	 * removed.
	 *
	 * Whitespace is defined according to the convention in {@link java.lang.String#trim}
	 *
	 * Be warned that unlike with trim, the parameter is not changed in any way.
	 *
	 * Nulls will be returned as zero-length strings.
	 */
	public static String prune(String text)
	{
		if (text==null)
			return "";
		
		StringBuffer result = new StringBuffer(text.length());
		
		boolean lastCharWasCollapsableSpace = false;
		
		for (int loop=0; loop<=text.length()-1; loop++)
		{
			char theChar = text.charAt(loop);
			
			if (!isCollapsableSpace(theChar))
			{
				result.append(theChar);
			}
			else if (!lastCharWasCollapsableSpace)
			{
				result.append(" ");
			}
			
			lastCharWasCollapsableSpace = isCollapsableSpace(theChar);
		}
		
		/* System.out.println("pruned to: *"+result+"*, length="+result.length());
		
		System.out.print("\tchar int values: ");
		for (int loop2=0; loop2<=result.length()-1; loop2++)
			System.out.print((int)result.charAt(loop2)+" ");
		System.out.println(); */
		
		return result.toString();
	}
	
	public static boolean isEntirelyWhiteSpace(final String text)
	{

		for (int loop=0; loop<=text.length()-1; loop++)
		{
			char theChar = text.charAt(loop);
			
			if (!isWhiteSpace(theChar))
				return false;
		}
		
		return true;
	}
			
	public void addChangeListener(final ChangeListener designatedChangeListener)
	{
		styledDocument.addDocumentListener(new DocumentListener()
		{
			public void removeUpdate(javax.swing.event.DocumentEvent e)
			{
				designatedChangeListener.stateChanged(CHANGE_EVENT);
			}

			public void insertUpdate(javax.swing.event.DocumentEvent e)
			{
				designatedChangeListener.stateChanged(CHANGE_EVENT);
			}

			public void changedUpdate(javax.swing.event.DocumentEvent e)
			{
				designatedChangeListener.stateChanged(CHANGE_EVENT);
			}
		});
		
		changeSupport.addChangeListener(designatedChangeListener);
	}

	public boolean isVertical()
	{
		return vertical;
	}

	public void setVertical(boolean desigantedVerticality)
	{
		if (desigantedVerticality!=vertical)
		{
			vertical = desigantedVerticality;
			
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	/**
	 * Maps java-style logical font names to SVG-style logical font names.
	 * 
	 * Leaves physical font names alone.
	 */
	public static String javaFontFamilyToSvg(String requestedJavaFontFamily)
	{
		if (FontFriend.IDENTIFIER_SANS_SERIF.equals(requestedJavaFontFamily)
				|| FontFriend.IDENTIFIER_DIALOG.equals(requestedJavaFontFamily)
				|| FontFriend.IDENTIFIER_DIALOG_INPUT.equals(requestedJavaFontFamily))
		{
			return IDENTIFIER_SANS_SERIF;
		}
		
		if (FontFriend.IDENTIFIER_SERIF.equals(requestedJavaFontFamily))
		{
			return IDENTIFIER_SERIF;
		}
		
		if (FontFriend.IDENTIFIER_MONOSPACED.equals(requestedJavaFontFamily))
		{
			return IDENTIFIER_MONOSPACE;
		}
		
		return requestedJavaFontFamily;
	}
	
	/**
	 * Maps SVG-style logical font names to Java-style logical font names.
	 * 
	 * Leaves physical font names alone.
	 */
	public static String svgFontFamilyToJava(String requestedSvgFontFamily)
	{
		if (IDENTIFIER_SANS_SERIF.equals(requestedSvgFontFamily))
			return FontFriend.IDENTIFIER_SANS_SERIF;
		
		if (IDENTIFIER_SERIF.equals(requestedSvgFontFamily))
			return FontFriend.IDENTIFIER_SERIF;
		
		if (IDENTIFIER_MONOSPACE.equals(requestedSvgFontFamily))
			return FontFriend.IDENTIFIER_MONOSPACED;
		
		return requestedSvgFontFamily;
	}
	
	/**
	 * @return true if the character provided is not white space, according to the
	 * convention defined in {@link java.lang.Character#isWhitespace(char)} .
	 */
	public static boolean isWhiteSpace(char theChar)
	{
		return !(theChar>' ' || theChar=='\t'); // TODO remove exemption for tab?
	}

	/**
	 * @return true if the character provided is not a formal space, nor any of the
	 * weird characters ahead of it in Unicode other than tab or newline.
	 */
	public static boolean isCollapsableSpace(char theChar)
	{
		return !(theChar>' ' || theChar=='\t' || theChar=='\n');
	}
	
	public AttributedString toAttributedString(int requestedSection)
	{
		if (styledDocument==null || requestedSection<0 || styledDocument.getLength()==0)
			return null;
		
		int startPos = -1;
		int endPos = -1;
		
		StringBuffer currentSegment = new StringBuffer();
		
		try
		{			
			String entireText = styledDocument.getText(0, styledDocument.getLength());
			
			int currentSegmentIndex = 0;
			
			for (int charLoop=0; (charLoop<=entireText.length()-1) && currentSegmentIndex<=requestedSection; charLoop++)
			{
				if ('\t'==entireText.charAt(charLoop))
				{
					if (currentSegmentIndex==requestedSection)
					{
						endPos = charLoop - 1;
						
						if (startPos == endPos)
							return null;
					}
						
					currentSegmentIndex++;
				}
				else if (currentSegmentIndex==requestedSection)
				{
					if (startPos==-1)
						startPos = charLoop;
					
					currentSegment.append(entireText.charAt(charLoop));
				}
			}
			
			if (startPos==-1)
				return null;
			
			if (endPos==-1)
				endPos = entireText.length()-1;
					
			attributedString = new AttributedString(currentSegment.toString());
			
			if (currentSegment.length()>0 && PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER && 
					!BalloonEngineState.getInstance().isIgnoreFontKerning())
			{
				attributedString.addAttribute(PlatformFriend.KERNING_TEXT_ATTRIBUTE, PlatformFriend.KERNING_ON);
			}
		}
		catch (BadLocationException e)
		{
			throw new BugException(e);
		}
		
		// System.out.println("styledDocument.getEndPosition()="+styledDocument.getEndPosition());
	
		javax.swing.text.Element swingRootElement = styledDocument.getDefaultRootElement();
		
		// Logger.print("element count = ");
		// Logger.println(appropriateElementTree.getElement(0).getElementCount());

		for (int elementLoop=0; elementLoop<=swingRootElement.getElementCount()-1; elementLoop++) // does this actually iterate over anything?
		{
			javax.swing.text.Element paragraph = swingRootElement.getElement(elementLoop);
			
			int startElement = paragraph.getElementIndex(startPos);
			int endElement = paragraph.getElementIndex(endPos);

			if (startElement==-1 || endElement==-1)
			{
				System.out.println("couldn't find element");
				return null;
			}

			int startAttrStrPos = 0;
			int endAttrStrPos = 0;
			
			for (int loop=startElement; loop<=endElement; loop++)
			{
				javax.swing.text.Element currentSwingElement = paragraph.getElement(loop);
				// javax.swing.text.Document swingDoc = currentSwingElement.getDocument();
				AttributeSet attributes = currentSwingElement.getAttributes();
				
				if (currentSwingElement.getStartOffset()>startPos)
					startAttrStrPos = currentSwingElement.getStartOffset()-startPos;
				else
					startAttrStrPos = 0;
				
				endAttrStrPos = currentSwingElement.getEndOffset()-startPos;
				if (endAttrStrPos>currentSegment.length())
					endAttrStrPos = currentSegment.length();
				
				try // TODO remove
				{
					if (endAttrStrPos>=startAttrStrPos)
					{
						applySwingAttributesToAttributeString(attributes, attributedString, startAttrStrPos, endAttrStrPos, null);
					}
					else
					{
						// most likely an orphaned newline character (issue #47)
						
						// TODO prevent this from happening
					}
					
				}
				catch (IllegalArgumentException e)
				{
					System.out.println("startPos="+startPos);
					System.out.println("endPos="+endPos);
					System.out.println("currentSegment="+currentSegment);
					System.out.println("currentSegment.length="+currentSegment.length());
					System.out.println("currentSwingElement.getStartOffset()="+currentSwingElement.getStartOffset());
					System.out.println("currentSwingElement.getEndOffset()="+currentSwingElement.getEndOffset());
					System.out.println("startAttrStrPos="+startAttrStrPos);
					System.out.println("endAttrStrPos="+endAttrStrPos);
					throw e;
				}
				
				
			}
		}
		
		return attributedString;
	}
	
	public AttributedCharacterIterator getIterator(int requestedSection)
	{
		if (styledDocument==null)
			return null;
		
		AttributedString attrString = toAttributedString(requestedSection);
		
		if (attrString==null)
			return null;
		
		return attrString.getIterator();
	}
	
	public static int determineLength(Element requestedElement)
	{
		if (requestedElement==null)
			return 0;
		
		return requestedElement.getEndOffset()-requestedElement.getStartOffset();
	}
	
	public void absorb(StyledDocumentStraw other)
	{
		if (other==null)
			return;
			
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			org.w3c.dom.Element bottomElement = doc.createElement(IDENTIFIER_TEXT);
			other.save(doc, bottomElement);
				// inefficient. reliable?
			
			if (styledDocument.getLength()>0)
				styledDocument.insertString(styledDocument.getLength(), "\t", new SimpleAttributeSet());
			
			load(bottomElement, null);
		}
		catch (RuntimeException exception)
		{
			throw exception;
		}
		catch (Exception exception)
		{
			throw new BugException(exception);
		}
	}
	
	public void load(AttributedString attrString)
	{
		if (attrString==null)
			return;
		
		if (styledDocument==null)
			styledDocument = new DefaultStyledDocument();
		
		AttributedCharacterIterator attributedCharacterIterator = attrString.getIterator();
		
		try
		{		
			for (char theChar = attributedCharacterIterator.current();
					theChar != AttributedCharacterIterator.DONE;
					theChar = attributedCharacterIterator.next())
			{
				styledDocument.insertString(styledDocument.getLength(),
						String.valueOf(theChar),
						javaTextAttributesToSwingAttributes(attributedCharacterIterator.getAttributes()));
			}
		}
		catch (BadLocationException exception)
		{
			throw new BugException(exception);
		}
	}
	
	public AttributeSet javaTextAttributesToSwingAttributes(Map javaTextAttributes)
	{
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		
		Iterator walkJavaTextAttributes = javaTextAttributes.keySet().iterator();
		
		while (walkJavaTextAttributes.hasNext())
		{
			TextAttribute textAttribute = (TextAttribute) walkJavaTextAttributes.next();
			Object value = javaTextAttributes.get(textAttribute);
			
			if (textAttribute==TextAttribute.FAMILY)
			{
				StyleConstants.setFontFamily(attrSet, String.valueOf(value));
			}
			else if (textAttribute==TextAttribute.SIZE)
			{
				StyleConstants.setFontSize(attrSet, (int) ((Float)value).floatValue() );
			}
			else if (textAttribute==TextAttribute.WEIGHT && value==TextAttribute.WEIGHT_BOLD)
			{
				StyleConstants.setBold(attrSet, true);
			}
			else if (textAttribute==TextAttribute.POSTURE && value==TextAttribute.POSTURE_OBLIQUE)
			{
				StyleConstants.setItalic(attrSet, true);
			}
			else if (textAttribute==TextAttribute.UNDERLINE && value==TextAttribute.UNDERLINE_ON)
			{
				StyleConstants.setUnderline(attrSet, true);
			}
		}
		
		return attrSet;
	}
	
	/**
	 * Sets the font for the entire straw.
	 */
	public void setFont(final Font designatedFont)
	{
		final SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setFontFamily(attributes, designatedFont.getFontName());	// TODO verify font name/family appropriateness
		StyleConstants.setFontSize(attributes, designatedFont.getSize());
		getStyledDocument().setCharacterAttributes(0, getStyledDocument().getLength()-1, attributes, true);
	}

	/**
	 * @return a Font generated from the attributes of the first character in the styled document,
	 * or null if there aren't any.
	 */
	public Font getFirstFont()
	{
		if (getStyledDocument().getLength()==0)
		{
			return null;
			// TODO check if its possible that even a zero-length document might have style
		}
		
		final AttributeSet attributes = getStyledDocument().getCharacterElement(0).getAttributes();
		// TODO use logical style instead?
		
		final String fontFamily = StyleConstants.getFontFamily(attributes);
		final int fontSize = StyleConstants.getFontSize(attributes);
		
		return new Font(fontFamily, Font.PLAIN, fontSize);
	}
		
	/**
	 * Prints attribute descriptions to the console. Helpful for debugging.
	 */
	public static void print(AttributeSet attributes)
	{
		Enumeration enumeration = attributes.getAttributeNames();
		int index = 0;
		
		while (enumeration.hasMoreElements())
		{
			Object key = enumeration.nextElement();
			Object value = attributes.getAttribute(key);
			System.out.print("Element #");
			System.out.print(++index);
			System.out.print(": (");
			System.out.print(key);
			System.out.print(", ");
			System.out.print(value);
			System.out.println(")");
		}
	}
}
