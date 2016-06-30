/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.*;
import javax.swing.text.StyledDocument;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.text.StyledDocumentStraw;
import com.smithandtinkers.util.*;


/**
 * Models a word balloon from comics.
 *
 * Most of the good stuff is actually implemented in its super class, {@link TextShape}.
 *
 * @author Dave Horlick
 */
public class Balloon extends TextShape implements Named, PubliclyCloneable
{
	private Point2D mostRecentlyRecordedPreferredLocation = new Point2D.Double(0.0, 0.0);

	private String name;
	
	private static final Random RANDOM = new Random();

	public static final String IDENTIFIER_BALLOON = "balloon"; 
	
	public Balloon()
	{
		super();
		setName(AbstractNamed.NAMES_TEXT.getString("balloonLabel"));
	}
	
	public Balloon(StyledDocument doc, Crowd designatedParent)
	{
		super(doc, designatedParent);
		setName(AbstractNamed.NAMES_TEXT.getString("balloonLabel"));
	}
	
	public Point2D suggestCenter()
	{
		Sill parentSill = (Sill) findForebear(Sill.class);
		Aperture ap = parentSill.getAperture();
		
		if (ap == null)
			return mostRecentlyRecordedPreferredLocation;
		
		Crowd crowd = (Crowd) findForebear(Crowd.class);
		int position = crowd.indexOf(this);
		
		int row = (position % 6);
		int column = (position % 2);
		
		Rectangle2D rect = ap.enclose();
		
		double goodX = rect.getX() + (1+column*2)*rect.getWidth()/4;
		double goodY = rect.getY() + (2+row)*rect.getHeight()/7;
		mostRecentlyRecordedPreferredLocation = new Point2D.Double(goodX, goodY);
		return mostRecentlyRecordedPreferredLocation;
	}
	
	public void arrange()
	{
		// System.out.println("arranging...");
		
		setCenter(suggestCenter());
		
		// TODO pick a good width, height, and stem location
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, com.smithandtinkers.io.ArchiveContext)
	 */
	public void save(org.w3c.dom.Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element balloonElement = doc.createElement(Balloon.IDENTIFIER_BALLOON);
		parent.appendChild(balloonElement);
		applyAttributes(doc, balloonElement, archiveContext);
	}
	
	public void open(Object parent, Node node, ArchiveContext archiveContext)
			throws NumberFormatException
	{
		extractAttributesAndSubnodes( (Element) node,  archiveContext.getMissingFontFamilies());
		
		if (archiveContext.getListenerToNotifyAboutUndoableEdits()!=null)
			getText().addUndoableEditListener(archiveContext.getListenerToNotifyAboutUndoableEdits());
		else
			System.err.println("not undoable edit listener set in archive context");
	}
	
	public double getArclength()
	{
		return getSuperEllipse().getArclength();
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String designatedName)
	{
		if (name != designatedName && (designatedName==null || !designatedName.equals(name)))
		{
			name = designatedName;
			
			SingleThreadedChangeSupport stcs = getChangeSupport();
		
			if (stcs!=null)
			{
				stcs.fireChange(new NameChangeEvent(this));
			}
		}
	}
	
	public String toString()
	{
		if (getName()!=null)
			return getName();
		else
			return IDENTIFIER_BALLOON;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		Balloon cloned = new Balloon();
		cloned.setParent(null);
		
		cloned.setName(getName());
		cloned.setFillColor(getFillColor());
		cloned.setOutlineColor(getOutlineColor());
		cloned.setLineThickness(getLineThickness());
		
		deeperCopyTo(cloned);
		
		cloned.straw = (StyledDocumentStraw) straw.clone();
		cloned.connectStraw();
		
		return cloned;
	}
}
