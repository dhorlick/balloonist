/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author dhorlick
 */
public abstract class XmlFriend
{
	private static final SimpleDateFormat XS_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	/**
	 * Searches one level deep for a node of the desired name.
	 * 
	 * @return First matching node at topmost level, or null if none is found.
	 */
	public static Node excavateImmediateSubnode(Node parent, String desiredName)
	{
		// System.out.println("just before excavation: "+nodeToString(outermost));
		
		for (int loop=0; loop<=parent.getChildNodes().getLength()-1; loop++)
		{
			Node sub = parent.getChildNodes().item(loop);
			String nodeName = sub.getNodeName();
			
			if (nodeName!=null && nodeName.equalsIgnoreCase(desiredName))
			{
				// System.out.println(inner.getNodeName() + ", loop="+loop + " with " + inner.getChildNodes().getLength() + " kids.");
				return sub;
			}
		}
		
		return null;
	}

	/**
	 * Searches one level deep for nodes of the desired name.
	 * 
	 * @return A list containing any matching nodes at the topmost level.
	 */
	public static java.util.List excavateEveryImmediateSubnode(Node parent, String desiredName)
	{
		java.util.List found = new TypesafeList(Node.class);
		
		for (int loop=0; loop<=parent.getChildNodes().getLength()-1; loop++)
		{
			Node sub = parent.getChildNodes().item(loop);
			String nodeName = sub.getNodeName();
			
			if (nodeName!=null && nodeName.equalsIgnoreCase(desiredName))
			{
				// System.out.println(inner.getNodeName() + ", loop="+loop + " with " + inner.getChildNodes().getLength() + " kids.");
				found.add(sub);
			}
		}
		
		return found;
	}
	
	/**
	 * Searches as deeply as possible for the first incidence of the desired node.
	 * 
	 * @return First matching node, or null if none is found.
	 */
	public static Node excavateSubnode(Node parent, String desiredName)
	{
		// System.out.println("just before excavation: "+nodeToString(outermost));
		
		for (int loop=0; loop<=parent.getChildNodes().getLength()-1; loop++)
		{
			Node sub = parent.getChildNodes().item(loop);
			String nodeName = sub.getNodeName();
			
			if (nodeName!=null && nodeName.equals(desiredName))
			{
				// System.out.println(inner.getNodeName() + ", loop="+loop + " with " + inner.getChildNodes().getLength() + " kids.");
				return sub;
			}
			else
			{
				Node excavated = excavateSubnode(sub, desiredName);
				if (excavated!=null)
					return excavated;
			}
		}
		
		return null;
	}
	
	public static String javaDateToXsDate(Date javaDate)
	{
		return XS_DATE_FORMATTER.format(javaDate);
	}
	
	public static Date xsDateToJavaDate(String xsDate) throws ParseException
	{
		return XS_DATE_FORMATTER.parse(xsDate);
	}
	
	public static String uncoverText(Node requestedNode)
	{
		if (requestedNode.getNodeType()==Node.TEXT_NODE)
			return requestedNode.getNodeValue();
		
		StringBuffer text = new StringBuffer();
		
		if (requestedNode.hasChildNodes() && requestedNode.getChildNodes().item(0).getNodeType()==Node.TEXT_NODE)
		{
			for (int loop=0; loop<=requestedNode.getChildNodes().getLength()-1; loop++)
			{
				text.append(requestedNode.getChildNodes().item(loop).getNodeValue());
			}
		}
		
		return text.toString();
	}
	
	public static Element createTextElement(Document doc, String requestedName, String requestedText)
	{
		Element element = doc.createElement(requestedName);
		element.appendChild(doc.createTextNode(requestedText));
		
		return element;
	}
	
	public static void print(Document doc)
	{
		try
		{
			Transformer autobot = TransformerFactory.newInstance().newTransformer();
			autobot.transform(new DOMSource(doc), new StreamResult(System.out));
			System.out.flush();
		}
		catch (TransformerException exception)
		{
		}
	}
}
