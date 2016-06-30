/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.mvc.TreeTypesafeList;
import com.smithandtinkers.util.*;

/**
 * A super ellipse with specially-marked segments along its perimeter to which
 * stems may be attached.
 *
 * @author dhorlick
 */
public class PunctuatedSuperEllipse extends TreeTypesafeList implements SuperEllipse, Stemmed, PubliclyCloneable, Adorned
{
	private TypesafeList stemsEncounteredWhileDrawing = new TypesafeList(Stem.class); // TODO move this into StemConstructionContext
	
	public static final int DEFAULT_UNRUFFLED_STEMMED_PARAMETERIZATION_STEPS = 120;
	
	private final RuffleableSuperEllipse engine = new RuffleableSuperEllipse()
	{
		public void drawAdornments(DrawingContext drawingContext, Selection selected)
		{
			super.drawAdornments(drawingContext, selected);
			
			// System.out.println("Standy. Adorning...");
			
			for (int stemLoop=0; stemLoop<=stemCount()-1; stemLoop++)
			{
				ConcreteStem istem = (ConcreteStem) get(stemLoop);
				// System.out.println("drawAdornments. istem.getBubbleCount()="+istem.getBubbleCount());
				
				istem.draw(drawingContext);
			}
		}
		
		protected void checkForStem(StemConstructionContext designatedStemConstructionContext,
				GeneralPath generalPath, double arclengthToHere, float x, float y, double u)
		{
			for (int stemLoop=0; designatedStemConstructionContext.getStem()==null && stemLoop<=stemCount()-1; stemLoop++)
			{
				ConcreteStem istem = (ConcreteStem) get(stemLoop);
				
				if (istem.leadingEdgePreceedsArcLength(arclengthToHere) && !stemsEncounteredWhileDrawing.contains(istem))
				{
					/* System.out.print("leading: (");
					System.out.print(x);
					System.out.print(", ");
					System.out.print(y);
					System.out.println(")"); */
					
					istem.setLeadingEdgeX(x);
					istem.setLeadingEdgeY(y);
					istem.setRootBubbleRotationInRadians(u);
					
					stemsEncounteredWhileDrawing.add(istem);
					
					designatedStemConstructionContext.setDrawingState(StemConstructionContext.LEADING_EDGE);
					// System.out.println("found leading edge @ u="+u);
					designatedStemConstructionContext.setStem(istem);
					return;
				}
			}
			
			if (designatedStemConstructionContext.getStem()!=null)
			{
				if (designatedStemConstructionContext.getStem().trailingEdgePreceedsArcLength(arclengthToHere))
				{
					designatedStemConstructionContext.getStem().setTrailingEdgeX(x);
					designatedStemConstructionContext.getStem().setTrailingEdgeY(y);
					
					/* System.out.print("trailing: (");
					System.out.print(x);
					System.out.print(", ");
					System.out.print(y);
					System.out.println(")"); */
					
					designatedStemConstructionContext.setDrawingState(StemConstructionContext.TRAILING_EDGE);
					return;
				}
				else
				{
					designatedStemConstructionContext.setDrawingState(StemConstructionContext.IN_STEM);
					return;
				}
			}
			
			designatedStemConstructionContext.setDrawingState(StemConstructionContext.ON_PERIMETER);
		}
		
		public boolean stampStem(StemConstructionContext designatedStemConstructionContext,
				GeneralPath generalPath, PlottingContext guideSlate)
		{
			boolean drewSomething = false;
			
			if (designatedStemConstructionContext.getStem()!=null 
					&& designatedStemConstructionContext.getStem().isInline())
			{
				designatedStemConstructionContext.getStem().stampSegment(generalPath, guideSlate);
				drewSomething = true;
			}
			
			designatedStemConstructionContext.setStem(null);
			
			return drewSomething;
		}
		
		public void applyAttributesAndSubElements(Document doc, Element element)
		{
			super.applyAttributesAndSubElements(doc, element);
			Iterator walkStems = PunctuatedSuperEllipse.this.iterator();
		
			while (walkStems.hasNext())
			{
				Stem stem = (Stem) walkStems.next();
				
				if (stem instanceof AbstractStem)
				{
					AbstractStem astem = (AbstractStem) stem;
					astem.save(doc, element, null);
				}
			}
		}
		
		public void extractAttributesAndSubElements(Object parent, Element element, ArchiveContext archiveContext)
		{
			super.extractAttributesAndSubElements(parent, element);
			java.util.List nodeList = XmlFriend.excavateEveryImmediateSubnode(element,AbstractStem.IDENTIFIER_STEM);
		
			for (int loop=0; loop<=nodeList.size()-1; loop++)
			{
				ConcreteStem istem = new ConcreteStem();
				istem.open(this, (Node)nodeList.get(loop), archiveContext);
				addIcicleStem(istem);
			}
		}
	};
	
	private GeneralPath shape = new GeneralPath(); // this class is decidedly NOT thread-safe.
	
	public PunctuatedSuperEllipse()
	{
		super(ConcreteStem.class);
	}
	
	/**
	 * Establishes the given stem as a child.
	 *
	 * If this punctuated super ellipse is perched, don't forget to set the perch
	 * as a god parent to the new stem.
	 */
	public void addIcicleStem(ConcreteStem designatedIcicleStem)
	{
		add(designatedIcicleStem);
	}
	
	public int stemCount()
	{
		return size();
	}
	
	/**
	 * @see com.smithandtinkers.geom.Stemmed#getStem(int)
	 */
	public Stem getStem(int index)
	{
		return (ConcreteStem) get(index);
	}
	
	public void applyAttributesAndSubElements(Document doc, Element element, ArchiveContext archiveContext)
	{
		engine.applyAttributesAndSubElements(doc, element);
	}
	
	public void extractAttributesAndSubElements(Object parent, Element element, ArchiveContext archiveContext)
	{
		engine.extractAttributesAndSubElements(parent, element);	
	}
	
	public void draw(DrawingContext drawingContext)
	{
		drawingContext.draw(this);
	}
	
	public String toString()
	{
		return "Punctuated Ellipse " + super.toString();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getArclength()
	 */
	public double getArclength()
	{
		return engine.getArclength();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getHeinParameter()
	 */
	public double getHeinParameter()
	{
		return engine.getHeinParameter();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMajorAxis()
	 */
	
	public double getSemiMajorAxis()
	{
		return engine.getSemiMajorAxis();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMajorAxis(double)
	 */
	public void setSemiMajorAxis(double designatedSemiMajorAxis)
	{
		engine.setSemiMajorAxis(designatedSemiMajorAxis);	
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#getSemiMinorAxis()
	 */
	public double getSemiMinorAxis()
	{
		return engine.getSemiMinorAxis();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setSemiMinorAxis(double)
	 */
	public void setSemiMinorAxis(double designatedSemiMinorAxis)
	{
		engine.setSemiMinorAxis(designatedSemiMinorAxis);
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		stemsEncounteredWhileDrawing.clear();
		
		return engine.toShape(shapingContext, plottingContext);
	}
	
	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		engine.reshape(oldX, oldY, newX, newY);
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		engine.save(doc, parent, null);
		Node saved = parent.getFirstChild();
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, org.w3c.dom.Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		engine.open(parent, node, null);
		
		for (int loop=0; loop<=node.getChildNodes().getLength()-1; loop++)
		{
			Node childNode = node.getChildNodes().item(loop);
			
			if (AbstractStem.IDENTIFIER_STEM.equals(childNode.getNodeName()))
			{
				ConcreteStem istem = new ConcreteStem();
				istem.open(this, childNode, null);
			}
		}
	}
	
	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(java.awt.Point)
	 */
	public boolean onPerimeter(Point thePoint)
	{
		return engine.onPerimeter(thePoint);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(double, double)
	 */
	public boolean onPerimeter(double x, double y)
	{
		return engine.onPerimeter(x, y);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Relocateable#getLocation()
	 */
	public Point2D getLocation()
	{
		return engine.getLocation();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Relocateable#setLocation(java.awt.geom.Point2D)
	 */
	public void setLocation(Point2D location)
	{
		engine.setLocation(location);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Relocateable#setLocation(double, double)
	 */
	public void setLocation(double newX, double newY)
	{
		engine.setLocation(newX, newY);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Relocateable#translate(double, double)
	 */
	public void translate(double dx, double dy)
	{
		engine.translate(dx, dy);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		return engine.getResizeableBounds2D();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getPreferredBounds()
	 */
	public Rectangle2D getPreferredBounds()
	{
		return engine.getPreferredBounds();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		return engine.resize(designatedSide, oldX, oldY, newX, newY);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#setWidth(double)
	 */
	public void setWidth(double designatedWidth)
	{
		engine.setWidth(designatedWidth);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#setHeight(double)
	 */
	public void setHeight(double designatedHeight)
	{
		engine.setHeight(designatedHeight);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return engine.getWidth();
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return engine.getHeight();
	}
	
	/**
	 * @see com.smithandtinkers.geom.SuperEllipse#setHeinParameter(double)
	 */
	public void setHeinParameter(double designatedHeinParameter)
	{
		engine.setHeinParameter(designatedHeinParameter);
	}
	
	public void rufflePrettily()
	{
		engine.rufflePrettily();
	}
	
	public void setParent(Collection designatedParent)
	{
		super.setParent(designatedParent);
		engine.setParent(designatedParent);
	}
	
	public void paintSelection(DrawingContext drawingContext)
	{
		engine.paintSelection(drawingContext);
	}
	
	public boolean isSelected(Selection selection)
	{
		for (int loop=0; loop<=selection.getSelectedItemsCount()-1; loop++)
		{
			if (selection.getSelectedItem(loop)==this)
				return true;
			
			if (selection.getSelectedItem(loop) instanceof SuperEllipsePerch)
			{
				SuperEllipsePerch sep = (SuperEllipsePerch)selection.getSelectedItem(loop);
				
				if (sep.getPunctedSuperEllipse()==this)
					return true;
			}
		}
		
		return false;
	}
	
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selected)
	{
		// System.out.println("floo!");
		
		if (isSelected(selected))
			paintSelection(drawingContext);
		else
		{		
			for (int stemLoop=0; stemLoop<=stemCount()-1; stemLoop++)
			{
				ConcreteStem istem = (ConcreteStem) get(stemLoop);
				
				istem.paintSelectionIfAppropriate(drawingContext, selected);
			}
		}
	}

	public boolean wouldProspectiveHeinParameterBeAllowed(double prospectiveHeinParameter)
	{
		return engine.wouldProspectiveHeinParameterBeAllowed(prospectiveHeinParameter);
	}

	public double minAllowableHeinParameter()
	{
		return engine.minAllowableHeinParameter();
	}

	public double maxAllowableHeinParameter()
	{
		return engine.maxAllowableHeinParameter();
	}
	
	public boolean equals(Object obj)
	{
		return (this == obj); // need to override the overeager TypesafeList.equals implementation
	}
	
	public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	/**
	 * Provided for testing purposes.
	 */
	public double bruteForceArcLength()
	{
		return engine.bruteForceArcLength();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		PunctuatedSuperEllipse cloned = new PunctuatedSuperEllipse();
		
		cloned.setHeinParameter(getHeinParameter());
		cloned.setSemiMajorAxis(getSemiMajorAxis());
		cloned.setSemiMinorAxis(getSemiMinorAxis());
		cloned.setLocation(getLocation());
		
		cloned.engine.setType(engine.getType());
		engine.getRuffles().deeperCopyTo(cloned.engine.getRuffles());
		
		deeperCopyTo(cloned);
		
		return cloned;
	}

	public void drawAdornments(DrawingContext drawingContext, Selection selected)
	{
		engine.drawAdornments(drawingContext, selected);
	}
	
	public boolean isRuffled()
	{
		return engine.isRuffled();
	}
	
	public RuffleableSuperEllipse getRuffledSuperEllipse()
	{
		return engine;
	}
	
	public boolean isEngine(AbstractSuperEllipse possibility)
	{
		return (engine==possibility);
		
		// Bleah! The need for this method indicates that it is time to rework the super ellipse class hierarchy.
	}
}
