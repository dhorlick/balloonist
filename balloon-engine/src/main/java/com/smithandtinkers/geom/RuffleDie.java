/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractKid;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.PubliclyCloneable;
import com.smithandtinkers.util.Saveable;
import java.util.Collection;

import javax.swing.event.ChangeEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A template that can be used for constructing ruffles.
 *
 * @author dhorlick
 */
public class RuffleDie extends AbstractKid implements Saveable, Kid, PubliclyCloneable
{
	private double preferredWidthInPoints = 27.0;
	private double heightInPoints = 5.0;
	
	private Collection parent;
	
	private ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public final static String IDENTIFIER_RUFFLE_DIE = "ruffle-die";
		private final static String IDENTIFIER_PREFERRED_WIDTH = "preferred-width";
		private final static String IDENTIFIER_HEIGHT = "height";
	
	public RuffleDie()
	{
	}

	public double getPreferredWidthInPoints()
	{
		return preferredWidthInPoints;
	}

	public void setPreferredWidthInPoints(double designatedPreferredWidthInPoints)
	{
		if (preferredWidthInPoints != designatedPreferredWidthInPoints)
		{
			preferredWidthInPoints = designatedPreferredWidthInPoints;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
			// System.out.println("setPreferredWidthInPoints: event issued? "+result);
		}
	}

	public double getHeightInPoints()
	{
		return heightInPoints;
	}

	public void setHeightInPoints(double designatedHeightInPoints)
	{
		if (heightInPoints!=designatedHeightInPoints)
		{
			heightInPoints = designatedHeightInPoints;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	public String toString()
	{
		return "com.smithandtinkers.geom.RuffleDie {"
			 + "preferredWidthInPoints = " + preferredWidthInPoints + ", "
			 + "heightInPoints = " + heightInPoints
		+ "}";
	}

	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element ruffleDieElement = doc.createElement(IDENTIFIER_RUFFLE_DIE);
		parent.appendChild(ruffleDieElement);
		
		ruffleDieElement.setAttribute(IDENTIFIER_PREFERRED_WIDTH, String.valueOf(preferredWidthInPoints));
		ruffleDieElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(heightInPoints));
	}

	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		Element element = (Element) node;
		
		if (element.hasAttribute(IDENTIFIER_PREFERRED_WIDTH))
		{
			setPreferredWidthInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_PREFERRED_WIDTH)));
		}
		
		if (element.hasAttribute(IDENTIFIER_HEIGHT))
		{
			setHeightInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_HEIGHT)));
		}
	}

	public void setParent(Collection designatedParent)
	{
		parent = designatedParent;
	}

	public Collection getParent()
	{
		return parent;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return (RuffleDie) super.clone();
	}
}
