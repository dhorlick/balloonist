/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;

import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractKid;
import com.smithandtinkers.util.Named;


/**
 * @author dhorlick
 */
public abstract class AbstractRelocateable extends AbstractKid implements Relocateable, Named, Selectable
{
	protected Point2D location;
	
	protected static final String IDENTIFIER_Y = "y";
	protected static final String IDENTIFIER_X = "x";
	
	private String name;
	
	final protected ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	/**
	 * @return Returns the location.
	 */
	public Point2D getLocation()
	{
		if (location==null)
		{
			location = new Point2D.Double();
		}
		
		Point2D returnable = new Point2D.Double();
		returnable.setLocation(location);
		return returnable;
	}
	
	/**
	 * @param designatedLocation The location to set.
	 */
	public void setLocation(Point2D designatedLocation)
	{
		location = designatedLocation;
		
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void translate(double dx, double dy)
	{
		location.setLocation(location.getX() + dx, location.getY() + dy);
		
		if (dx!=0 || dy!=0)
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void applyAttributesAndSubElements(Document doc, Element element)
	{
		element.setAttribute(IDENTIFIER_X, String.valueOf(getLocation().getX()));
		element.setAttribute(IDENTIFIER_Y, String.valueOf(getLocation().getY()));
	}

	public void extractAttributesAndSubElements(Object parent2, Element element)
	{
		if (element.hasAttribute(IDENTIFIER_X) && element.hasAttribute(IDENTIFIER_Y))
		{
			setLocation(Double.parseDouble(element.getAttribute(IDENTIFIER_X)), Double.parseDouble(element.getAttribute(IDENTIFIER_Y)));
		}
	}
	
	public void setLocation(double newX, double newY)
	{
		double oldX = getLocation().getX();
		double oldY = getLocation().getY();
		
		if (location==null)
		{
			location = new Point2D.Double();
		}
		
		location.setLocation(newX, newY);
		
		// System.out.println("New location: "+getLocation());
		
		if (oldX!=newX || oldY!=newY)
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
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
			SingleThreadedChangeSupport.fireChangeEvent(this, new NameChangeEvent(this));
		}
	}
	
	public boolean isSelected(Selection selection)
	{
		if (selection==null)
			return false;
		
		for (int loop=0; loop<=selection.getSelectedItemsCount()-1; loop++)
		{
			if (selection.getSelectedItem(loop)==this)
				return true;
			
			if (selection.getSelectedItem(loop) instanceof Perch)
			{
				Perch perch = (Perch)selection.getSelectedItem(loop);
				
				if (perch.getUnperched()==this)
					return true;
			}
		}
		
		return false;
	}
    
    public void setX(double newX)
	{
		setLocation(newX, getLocation().getY());
	}
	
	public void setY(double newY)
	{
		setLocation(getLocation().getX(), newY);
	}
}
