/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.geom.ShapingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.mvc.TreeTypesafeList;
import com.smithandtinkers.util.*;


/**
 * A collection of artwork elements featuring styled texts that can be optimally
 * layed-out with respect to one another.
 *
 * Effort will be taken to prevent the styled text inside crowded elements from overlapping.
 *
 * The user will generally not create crowds directly. Whenever a crowdable artwork element is 
 * created inside a Sill, it's best to implicitly create a Crowd to contain the new element, if
 * one is not already present.
 * 
 * @author dhorlick
 */
public class Crowd extends TreeTypesafeList implements Drawable, PubliclyCloneable, Named, Relocateable
{
	public static final String IDENTIFIER_CROWD = "crowd";
	
	public static final int MAXIMUM_NUMBER_OF_OPTIMIZATIONS = 10;
	
	private String name;
	
	private static Color TRANSLUCENT_YELLOW = new Color(0.92f, 1.00f, 0.0f, 0.25f);

	public Crowd()
	{
		super(Crowdable.class);
		setName(AbstractNamed.NAMES_TEXT.getString("crowdLabel"));
	}
	
	public Shape actualPerchedInnerShape(Crowdable crowdable, ShapingContext shapingContext)
	{	
		Area remainingArea = null;
		
		remainingArea = new Area(crowdable.preferredPerchedInnerShape(null));
		
		Sill sillDad = (Sill)findForebear(Sill.class);
		if (sillDad!=null && sillDad.getAperture()!=null)
		{
			if (sillDad.getAperture() instanceof Perch) // TODO can this be consolidated with Sill.actualPerchedInnerShape() ?
			{
				Perch perch = (Perch) sillDad.getAperture();
				remainingArea.intersect(new Area(perch.getInner().toShape(shapingContext, null)));
			}
			else if (sillDad.getAperture().getShape()!=null)
			{
				remainingArea.intersect(new Area(sillDad.getAperture().getShape()));
			}
		}
		
		Crowdable neighbor = null;
		Shape theShape = null;
		
		for (int loop=indexOf(crowdable)-1; loop>=0; loop--)
		{
			// System.out.println("\tloop= "+loop);
			
			if (get(loop) instanceof Shape)
			{
				theShape = (Shape) get(loop);
			}
			else if (get(loop) instanceof TextShape)
			{
				neighbor = (Crowdable) get(loop);
				theShape = neighbor.preferredPerchedOuterShape(null);
			}
			else
			{
				Logger.print("Didn't know how to shape ");
				Logger.print(get(loop).getClass());
			}
			
			if (theShape!=null && neighbor!=crowdable)
			{
				/*
				Logger.print("\tsubtracting ");
				Logger.print(theShape);
				Logger.println("...");
				*/
				remainingArea.subtract(new Area(theShape));
			}
		}
		
		return remainingArea;
	}
	
	public void draw(final DrawingContext drawingContext)
	{
		if (drawingContext.isVisible(this))
			reallyDraw(drawingContext);
	}

	/**
	 * Does the actual work of drawing, ignoring visibility constraints.
	 */
	private void reallyDraw(DrawingContext drawingContext)
	{
		Logger.println("starting");
		
		for (int loop=size()-1; loop>=0; loop--)
		{
			Drawable drawable = (Drawable) get(loop);
			drawable.draw(drawingContext);
			/*
			Logger.print("\t\tDrawable Comfiness = ");
			Logger.println(drawableComfiness);			
			*/
		}
		/*
		Logger.print("\tCrowd comfiness = ");
		Logger.println(overallComfort);
		*/
		
		if (drawingContext.getSelected()!=null && drawingContext.getSelected().isSelected(this))
		{
			Rectangle2D frame = getResizeableBounds2D();
								
			if (frame==null)
				return;
			
			drawingContext.drawFilled(frame, Color.orange, TRANSLUCENT_YELLOW);
		}
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, com.smithandtinkers.io.ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element crowdElement = doc.createElement(IDENTIFIER_CROWD);
		
		Iterator walkCrowdables = iterator();
		
		while (walkCrowdables.hasNext())
		{
			Saveable saveable = (Saveable) walkCrowdables.next();
			saveable.save(doc, crowdElement, archiveContext);
		}
		
		parent.appendChild(crowdElement);	
	}
	
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		Crowd.openDrawables(this, node, archiveContext);
	}
	
	public static void openDrawables(List list, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		NodeList children = node.getChildNodes();
		
		// System.out.println("element.getNodeName=()"+node.getNodeName());
		// System.out.println("element.getLocalName=()"+element.getLocalName());
		
		for (int loop=0; loop<=children.getLength()-1; loop++) // this seems wrong, somehow
		{
			Node subnode = children.item(loop);
			// System.out.println("subnode.getNodeName=()"+subnode.getNodeName());
			
			if (Balloon.IDENTIFIER_BALLOON.equals(children.item(loop).getNodeName()))
			{
				Balloon balloon = new Balloon();
				balloon.open(list, subnode, archiveContext);
				list.add(balloon);
			}
			else if (Sill.IDENTIFIER_SILL.equals(children.item(loop).getNodeName())) 
			{
				Sill sill = new Sill();
				sill.open(list, subnode, archiveContext);
				list.add(sill);
			}
			else if (Crowd.IDENTIFIER_CROWD.equals(children.item(loop).getNodeName()))
			{
				Crowd crowd = new Crowd();
				list.add(crowd);
				Crowd.openDrawables(crowd, subnode, archiveContext);
			}
			else if (GraphicResizeable.IDENTIFIER_GRAPHICAL_CONTENT.equals(children.item(loop).getNodeName()))
			{
				GraphicResizeable graphical = new GraphicResizeable();
				graphical.open(list, subnode, archiveContext);
				list.add(graphical);
			}
			// ...
		}
	}
	
	public String toString()
	{
		if (name==null)
			return IDENTIFIER_CROWD;
		else
			return name;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		Crowd cloned = new Crowd();
		deeperCopyTo(cloned);
		return cloned;
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

	public String getName()
	{
		return name;
	}
	
	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}
		
	/**
	 * Requests that the specified items, or their relevant portion of their crowds if applicable,
	 * be re-layed-out.
	 *
	 * Different from setDirty, which applies to the selection itself, not the items contained within it.
	 *
	 * @param item the item to be processed, if it proves to be Layoutable, Kid, Crowd
	 * or Gutter.
	 */
	public static void reLayoutCrowdsOf(Object item)
	{
		// System.out.println("grelt "+new java.util.Date());

		if (item instanceof Layoutable)
				((Layoutable)item).setLayedOut(false);
		
		if (item instanceof Crowd)
		{
			// typically use cases for this block involve
			//    1. an outside class directly invoking Crowd.reLayoutCrowdsOf(Crowd), or
			//    2. an internal call from reLayoutItemsWithin(List)
			
			Crowd asCrowd = (Crowd) item;
			for (int loop=0; loop<=asCrowd.size()-1; loop++) // TODO make this block an instance method
			{
				if (asCrowd.get(loop) instanceof Layoutable)
				{
					Layoutable layoutableCrowdable = (Layoutable) asCrowd.get(loop);
					layoutableCrowdable.setLayedOut(false);
				}
			}
		}
		if (item instanceof Kid)
		{
			// System.out.println("jzt "+new java.util.Date());
			
			Kid kid = (Kid) item;
			
			if (kid.getParent()!=null)
			{
				if (kid.getParent() instanceof Crowd)
				{
					Crowd companionCrowd = (Crowd) kid.getParent();

					// We only need to relayout items that are *behind* the selected item.

					int indexOfSelectionWithinCompanionCrowd = companionCrowd.indexOf(item);

					for (int crowdableLoop=companionCrowd.size()-1; crowdableLoop>=indexOfSelectionWithinCompanionCrowd; crowdableLoop--)
					{
						Crowdable crowdable = (Crowdable) companionCrowd.get(crowdableLoop);
						if (crowdable instanceof Layoutable)
						{
							Layoutable layoutableCrowdable = (Layoutable) crowdable;
							layoutableCrowdable.setLayedOut(false);
						}
					}
				}
				else
				{
					// keep looking
					
					reLayoutCrowdsOf(kid.getParent());
				}
			}
		}
		else if (item instanceof Gutter)
		{
			Gutter asGutter = (Gutter) item;
			
			// System.out.println("*** relayout either side of gutter: "+item);
			
			if (asGutter.getNorth()!=null)
				reLayoutItemsWithin(asGutter.getNorth());
			if (asGutter.getWest()!=null)
				reLayoutItemsWithin(asGutter.getWest());
			if (asGutter.getEast()!=null)
				reLayoutItemsWithin(asGutter.getEast());
			if (asGutter.getSouth()!=null)
				reLayoutItemsWithin(asGutter.getSouth());
		}
	}
	
	/**
	 * Goes thru each item in the provided list and requested that it be re-layed out as soon
	 * as possible.
	 */
	public static void reLayoutItemsWithin(List designatedList)
	{
		// System.out.println("jilt");
		
		for (int loop=0; loop<=designatedList.size()-1; loop++)
		{
			reLayoutCrowdsOf(designatedList.get(loop));
		}
	}
	
	/**
	 * @return the Crowd that the designated element, or null if there isn't any
	 */
	public static Crowd findCrowdOf(Kid designatedElement)
	{
		if (designatedElement instanceof Crowd)
			return (Crowd) designatedElement;
		
		if (designatedElement.getParent()==null)
			return null;
		
		if (designatedElement.getParent() instanceof Kid)
			return findCrowdOf((Kid)designatedElement.getParent());
			
		return null;
	}
	
	public Rectangle2D getResizeableBounds2D()
	{
		Rectangle2D frame = Sill.frame(this);
			
		if (frame==null)
		{
			if (getParent()!=null && getParent() instanceof Sill)
			{
				Sill parentSill = (Sill) getParent();
				frame = parentSill.frame();
			}
		}
		
		return frame;
	}
	
	public void translate(double dx, double dy)
	{
		for (int index=0; index<=size()-1; index++)
		{
			if (get(index) instanceof Relocateable)
			{
				Relocateable asRelocateable = (Relocateable) get(index);
				asRelocateable.translate(dx, dy);
			}
		}
	}
	
	/**
	 * @return null. Not applicable for Crowds.
	 */
	public Point2D getLocation()
	{
		return null;
	}
	
	public void setLocation(Point2D location)
	{
	}
	
	public void setLocation(double newX, double newY)
	{
	}
}
