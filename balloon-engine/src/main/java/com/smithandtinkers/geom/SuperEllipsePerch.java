/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.event.ChangeListener;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.Alignable;
import com.smithandtinkers.layout.PerimeterSegment;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.VisiblySelectable;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.*;


/**
 * Represents a Punctuated Super Ellipse perched around a normal Super Ellipse at a 
 * specified margin.
 * 
 * @author dhorlick
 */
public class SuperEllipsePerch extends AbstractNamed implements SuperEllipse, Perch, List, Kid, PubliclyCloneable, VisiblySelectable, Alignable, Stemmed
{
	private TrimmedSuperEllipse inner;
	private TrimmedSuperEllipse outer;
	
	/**
	 * used by the centerer
	 */
	private double gapBetweenMarginAndText = 0.0;
	
	public SuperEllipsePerch(PunctuatedSuperEllipse designatedPunctedSuperEllipse)
	{
		inner = new TrimmedSuperEllipse();
		inner.setInner(true);
		
		outer = new TrimmedSuperEllipse();
		outer.setInner(false);		
		
		setPunctedSuperEllipse(designatedPunctedSuperEllipse);
		setName(AbstractNamed.NAMES_TEXT.getString("ovalLabel"));
	}

	public SuperEllipsePerch()
	{
		this(new PunctuatedSuperEllipse());
	}
	
	public SuperEllipsePerch(PunctuatedSuperEllipse designatedPunctedSuperEllipse, Marginal designatedMarginal)
	{
		this(designatedPunctedSuperEllipse);
		setMarginal(designatedMarginal);
	}
		
	/**
	 * @see com.smithandtinkers.geom.Perch#getInner()
	 */
	public Shapeable getInner()
	{
		return inner;
	}
	
	/**
	 * @see com.smithandtinkers.geom.Perch#getOuter()
	 */
	public Shapeable getOuter()
	{
		return outer;
	}

	/**
	 * @see com.smithandtinkers.geom.Perch#getUnperched()
	 */
	public Shapeable getUnperched()
	{
		return getPunctedSuperEllipse();
	}	
	
	/**
	 * @param designatedPunctedSuperEllipse
	 */
	private void setPunctedSuperEllipse(PunctuatedSuperEllipse designatedPunctedSuperEllipse)
	{
		inner.setSuperEllipse(designatedPunctedSuperEllipse);
		outer.setSuperEllipse(designatedPunctedSuperEllipse);
	}
	
	public PunctuatedSuperEllipse getPunctedSuperEllipse()
	{
		return (PunctuatedSuperEllipse) inner.getSuperEllipse();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getArclength()
	 */
	public double getArclength()
	{
		return getPunctedSuperEllipse().getArclength();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getHeinParameter()
	 */
	public double getHeinParameter()
	{
		return getPunctedSuperEllipse().getHeinParameter();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMajorAxis()
	 */
	public double getSemiMajorAxis()
	{
		return getPunctedSuperEllipse().getSemiMajorAxis();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMajorAxis(double)
	 */
	public void setSemiMajorAxis(double designatedSemiMajorAxis)
	{
		getPunctedSuperEllipse().setSemiMajorAxis(designatedSemiMajorAxis);
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMinorAxis()
	 */
	public double getSemiMinorAxis()
	{
		return getPunctedSuperEllipse().getSemiMinorAxis();
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMinorAxis(double)
	 */
	public void setSemiMinorAxis(double designatedSemiMinorAxis)
	{
		setSemiMinorAxis(designatedSemiMinorAxis);		
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return getPunctedSuperEllipse().toShape(shapingContext, plottingContext);
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		getPunctedSuperEllipse().reshape(oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Drawable#draw(com.smithandtinkers.graphics.DrawingContext)
	 */
	public void draw(DrawingContext drawingContext)
	{
		getPunctedSuperEllipse().draw(drawingContext);
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		getPunctedSuperEllipse().save(doc, parent, archiveContext);
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, org.w3c.dom.Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		getPunctedSuperEllipse().open(parent, node, archiveContext);
		
		// make sure this is the parent of any stems that were added
		
		Iterator walk = getPunctedSuperEllipse().iterator();
		
		while (walk.hasNext())
		{
			Object newItem = walk.next();
			if (newItem instanceof GodKid)
			{
				GodKid newGodKid = (GodKid) newItem;
				if (newGodKid.getGodParent()!=this)
				{
					// System.out.println("baptizing: "+newGodKid);
					
					newGodKid.setGodParent(this);
				}
			}
		}
	}

	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(java.awt.Point)
	 */
	public boolean onPerimeter(Point thePoint)
	{
		return getPunctedSuperEllipse().onPerimeter(thePoint);
	}

	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(double, double)
	 */
	public boolean onPerimeter(double x, double y)
	{
		return getPunctedSuperEllipse().onPerimeter(x, y);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getLocation()
	 */
	public Point2D getLocation()
	{
		return getPunctedSuperEllipse().getLocation();
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#setLocation(java.awt.geom.Point2D)
	 */
	public void setLocation(Point2D location)
	{
		getPunctedSuperEllipse().setLocation(location);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#translate(double, double)
	 */
	public void translate(double dx, double dy)
	{
		getPunctedSuperEllipse().translate(dx, dy);
	}

	/**
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		return getPunctedSuperEllipse().getResizeableBounds2D();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		return getPunctedSuperEllipse().resize(designatedSide, oldX, oldY, newX, newY);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#setLocation(double, double)
	 */
	public void setLocation(double newX, double newY)
	{
		getPunctedSuperEllipse().setLocation(newX, newY);
	}

	/**
	 * @see com.smithandtinkers.mvc.ChangeIssuer#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener designatedListener)
	{
		getPunctedSuperEllipse().addChangeListener(designatedListener);
	}

	/**
	 * @see com.smithandtinkers.mvc.ChangeIssuer#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener designatedChangeListener)
	{
		getPunctedSuperEllipse().removeChangeListener(designatedChangeListener);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#setWidth(double)
	 */
	public void setWidth(double designatedDouble)
	{
		getPunctedSuperEllipse().setWidth(designatedDouble);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#setHeight(double)
	 */
	public void setHeight(double designatedHeight)
	{
		getPunctedSuperEllipse().setHeight(designatedHeight);
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getPreferredBounds()
	 */
	public Rectangle2D getPreferredBounds()
	{
		return getPunctedSuperEllipse().getPreferredBounds();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return getPunctedSuperEllipse().getWidth();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return getPunctedSuperEllipse().getHeight();
	}
	
	/**
	 * @see java.util.List#size()
	 */
	public int size()
	{
		return getPunctedSuperEllipse().size();
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear()
	{
		getPunctedSuperEllipse().clear();
	}

	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty()
	{
		return getPunctedSuperEllipse().isEmpty();
	}

	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray()
	{
		return getPunctedSuperEllipse().toArray();
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index)
	{
		return getPunctedSuperEllipse().get(index);
	}

	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index)
	{
		return getPunctedSuperEllipse().remove(index);
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element)
	{
		getPunctedSuperEllipse().add(index, element);
		
		if (element instanceof GodKid)
		{
			((GodKid)element).setGodParent(this);
		}
	}

	/**
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o)
	{
		return getPunctedSuperEllipse().indexOf(o);
	}

	/**
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o)
	{
		return getPunctedSuperEllipse().lastIndexOf(o);
	}

	/**
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Object o)
	{
		boolean result = getPunctedSuperEllipse().add(o);
		
		if (o instanceof GodKid)
		{
			((GodKid)o).setGodParent(this);
		}
		
		return result;
	}

	/**
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o)
	{
		return getPunctedSuperEllipse().contains(o);
	}

	/**
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		return getPunctedSuperEllipse().remove(o);
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c)
	{
		// TODO godparenting
		
		return getPunctedSuperEllipse().addAll(index, c);
	}

	/**
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c)
	{
		// TODO godparenting
		
		return getPunctedSuperEllipse().addAll(c);
	}

	/**
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c)
	{
		return getPunctedSuperEllipse().containsAll(c);
	}

	/**
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c)
	{
		return getPunctedSuperEllipse().removeAll(c);
	}

	/**
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c)
	{
		return getPunctedSuperEllipse().retainAll(c);
	}

	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator iterator()
	{
		return getPunctedSuperEllipse().iterator();
	}

	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int fromIndex, int toIndex)
	{
		return getPunctedSuperEllipse().subList(fromIndex, toIndex);
	}

	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator()
	{
		return getPunctedSuperEllipse().listIterator();
	}

	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index)
	{
		return getPunctedSuperEllipse().listIterator(index);
	}

	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element)
	{
		Object result = getPunctedSuperEllipse().set(index, element);
		
		if (element instanceof GodKid)
		{
			((GodKid)element).setGodParent(this);
		}
		
		return result;
	}

	/**
	 * @see java.util.List#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a)
	{
		return getPunctedSuperEllipse().toArray(a);
	}

	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setHeinParameter(double)
	 */
	public void setHeinParameter(double designatedHeinParameter)
	{
		getPunctedSuperEllipse().setHeinParameter(designatedHeinParameter);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#getParent()
	 */
	public Collection getParent()
	{
		return getPunctedSuperEllipse().getParent();
	}

	/**
	 * @see com.smithandtinkers.util.Kid#setParent(java.util.Collection)
	 */
	public void setParent(Collection designatedParent)
	{
		if (designatedParent instanceof Marginal)
		{
			Marginal marginal = (Marginal) designatedParent;
			inner.setMarginal(marginal);
			outer.setMarginal(marginal);
		}
		
		getPunctedSuperEllipse().setParent(designatedParent);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findForebear(java.lang.Class)
	 */
	public Object findForebear(Class requestedClass)
	{
		return getPunctedSuperEllipse().findForebear(requestedClass);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findProgenitor()
	 */
	public Object findProgenitor()
	{
		return getPunctedSuperEllipse().findProgenitor();
	}

	/**
	 * @see com.smithandtinkers.util.Kid#adopted(java.util.Collection)
	 */
	public void adopted(Collection newParent)
	{
		getPunctedSuperEllipse().adopted(newParent);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineAncestry()
	{
		// To reach accord between composite & decorator patterns, we need to tinker with result here
		
		Object[] determinedAncestry = getPunctedSuperEllipse().determineAncestry();
		determinedAncestry[determinedAncestry.length-1]=this;
		return determinedAncestry;
	}

	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selected)
	{
		getPunctedSuperEllipse().paintSelectionIfAppropriate(drawingContext, selected);
	}

	public boolean wouldProspectiveHeinParameterBeAllowed(double prospectiveHeinParameter)
	{
		return getPunctedSuperEllipse().wouldProspectiveHeinParameterBeAllowed(prospectiveHeinParameter);
	}

	public double minAllowableHeinParameter()
	{
		return getPunctedSuperEllipse().minAllowableHeinParameter();
	}

	public double maxAllowableHeinParameter()
	{
		return getPunctedSuperEllipse().maxAllowableHeinParameter();
	}

	public Object clone() throws CloneNotSupportedException
	{
		PunctuatedSuperEllipse clonedSuperEllipse = (PunctuatedSuperEllipse) getPunctedSuperEllipse().clone();
		SuperEllipsePerch cloned = new SuperEllipsePerch(clonedSuperEllipse, inner.getMarginal());
			// ^^ TODO this ignores the possibility that the trimmed super ellipses might have been altered to have different marginals
		
		return cloned;
	}
	
	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}

	public void paintSelection(DrawingContext drawingContext)
	{
		// handled elsewhere
	}

	public void drawAdornments(DrawingContext drawingContext, Selection selected)
	{
		getPunctedSuperEllipse().drawAdornments(drawingContext, selected);
	}
	
	public void setMarginal(Marginal designatedMarginal)
	{
		setParent(designatedMarginal);
	}

	public double getGapBetweenMarginAndText()
	{
		return gapBetweenMarginAndText;
	}

	public void setGapBetweenMarginAndText(double designatedGapBetweenMarginAndText)
	{
		gapBetweenMarginAndText = designatedGapBetweenMarginAndText;
	}

	public Stem getStem(int index)
	{
		return getPunctedSuperEllipse().getStem(index);
	}

	public int stemCount()
	{
		return getPunctedSuperEllipse().stemCount();
	}
	
	public void setName(String designatedName)
	{
		if (getName()!=designatedName && (designatedName==null || !designatedName.equals(getName())))
		{
			super.setName(designatedName);
			
			SingleThreadedChangeSupport.fireChangeEvent(this, new NameChangeEvent(this));
		}
	}
}
