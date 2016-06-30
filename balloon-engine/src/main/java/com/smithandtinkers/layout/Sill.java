/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.graphics.DrawingContext;
import java.awt.*;
import java.awt.geom.*;
import java.util.Iterator;
import java.util.ResourceBundle;
import javax.swing.event.ChangeEvent;

import com.smithandtinkers.geom.*;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.mvc.TreeTypesafeList;
import com.smithandtinkers.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Used to represent panels and pages. Basically, a container for all artwork elements, including other sills.
 * Optional border and background coloring is implemented thru a composited {@link #AperturePerch} object, a
 * reference to which can be obtained by invoking {@link #getAperture}.
 *
 * @author dhorlick
 */
public class Sill extends TreeTypesafeList implements Drawable, Saveable, Relocateable, Named, Resizeable,
		PubliclyCloneable, Marginal, Thick, Colorful
{
	private static final String IDENTIFIER_Y = "y";
	private static final String IDENTIFIER_X = "x";
	private static final String IDENTIFIER_NAME = "name";
	public static final String IDENTIFIER_SILL = "sill";
	
	public final static Color SLIGHTLY_TRANSLUCENT_BLUE = new Color(0.2f,0.2f,1.0f,.2f);
	
	private AperturePerch aperture;
	
	private Point2D.Double contentOrigin;

	private String name;
	private double margin = BalloonEngineState.determineDefaultMargin();
	
	private BasicStroke stroke = new BasicStroke();
	
	private static final ResourceBundle DIALOG_TEXT = ResourceBundle.getBundle("resources/text/dialog");
	private static final Stroke DEFAULT_STROKE = new BasicStroke();
	
	private ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public Sill()
	{
		super(Drawable.class);
	}
	
	public void setAperture(AperturePerch designatedAperture)
	{
		aperture = designatedAperture;
	}
	
	public AperturePerch getAperture()
	{
		return aperture;
	}
	
	public void setContentOrigin(Point2D.Double designatedContentOrigin)
	{
		contentOrigin = designatedContentOrigin;
	}
	
	public String toString()
	{
		// System.out.println("Sill.toString: name = \"" + name + "\".");
		
		if (name!=null)
			return name;
		else
			return DIALOG_TEXT.getString("unnamedLabel");
	}
	
	/**
	 * Does what toString would if it hadn't been appropriated by the needs of TreeModel
	 */
	public String describe()
	{
		StringBuffer desc = new StringBuffer(160);
		
		desc.append("Sill {");
		
		desc.append(super.toString());
		desc.append(", ");
		
		desc.append("name = ");
		desc.append(name);
		desc.append(", aperture = ");
		desc.append(aperture);
		desc.append(", origin = ");
		desc.append(contentOrigin);
		
		if (getParent()==null)
			desc.append(", no parent");
		else
		{
			desc.append(", parent=");
			desc.append(getParent().getClass().getName());
		}
		
		desc.append("}.");
		
		return desc.toString();
	}
	
	private final int [] CORNER_POLYGON_X = new int [3];
	private final int [] CORNER_POLYGON_Y = new int [3];
	
	public void draw(DrawingContext drawingContext)
	{
		Selection selected = drawingContext.getSelected();
		
		drawingContext.startGroup(toString());
		drawingContext.translate(getContentOrigin().x, getContentOrigin().y);
		
		if (drawingContext.isVisible(this))
		{
			if (aperture!=null)
				aperture.drawBack(drawingContext);
		
			drawGutters(drawingContext);
		}
		
		drawKids(drawingContext);
		
		if (drawingContext.isVisible(this))
		{
			if (aperture!=null)
			{
				final Stroke OLD_STROKE = drawingContext.getStroke();
				drawingContext.setStroke(stroke);
				aperture.drawFront(drawingContext);
				drawingContext.setStroke(OLD_STROKE);
			}
		
			paintSelectionIfAppropriate(drawingContext, selected);
		}
		
		drawingContext.translate(-1.0*contentOrigin.getX(), -1.0*contentOrigin.getY());
		drawingContext.endGroup();
	}
	
	private static void constructCornerPolygon(int [] cornerPolygonX, int [] cornerPolygonY, Rectangle rect, int x, int y, boolean down, boolean right)
	{
		final int WIDTH_FRACTION = 8;
		final int HEIGHT_FRACTION = 8;

		cornerPolygonX[0] = x;
		cornerPolygonY[0] = y;
		
		if (right)
			cornerPolygonX[1] = x + rect.width/WIDTH_FRACTION;
		else
			cornerPolygonX[1] = x - rect.width/WIDTH_FRACTION;
		
		cornerPolygonY[1] = cornerPolygonY[0];

		cornerPolygonX[2] = x;
		
		if (down)
			cornerPolygonY[2] = y + rect.height/HEIGHT_FRACTION;
		else
			cornerPolygonY[2] = y - rect.height/HEIGHT_FRACTION;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String designatedName)
	{
		if (designatedName!=name && (designatedName==null || !designatedName.equals(name)))
		{
			name = designatedName;
			
			SingleThreadedChangeSupport foundChangeSupport = getChangeSupport();
		
			if (foundChangeSupport!=null)
				foundChangeSupport.fireChange(new NameChangeEvent(this));
		}
	}
	
	public void drawKids(DrawingContext drawingContext)
	{
		Shape oldClip = null;
		
		if (aperture!=null)
		{
			oldClip = drawingContext.getClip();
			drawingContext.startGroup();
			aperture.enforceClippingBoundary(drawingContext);
		}
		
		for (int loop=size()-1; loop>=0; loop--)
		{
			Drawable drawable = (Drawable) get(loop);	
			drawable.draw(drawingContext);
		}

		if (aperture!=null)
		{
			drawingContext.setClip(oldClip);
			drawingContext.endGroup();
		}
	}
	
	public Point2D.Double getContentOrigin()
	{
		if (contentOrigin==null)
			contentOrigin = new Point2D.Double();
		
		return contentOrigin;
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element sillElement = doc.createElement(IDENTIFIER_SILL);
		
		if (name!=null)
			sillElement.setAttribute(IDENTIFIER_NAME, name);
		
		sillElement.setAttribute(IDENTIFIER_X, String.valueOf(contentOrigin.x));
		sillElement.setAttribute(IDENTIFIER_Y, String.valueOf(contentOrigin.y));
		
		sillElement.setAttribute(Marginal.IDENTIFIER_INNER_MARGIN, String.valueOf(margin));
		sillElement.setAttribute(Marginal.IDENTIFIER_OUTER_MARGIN, String.valueOf(-margin));
		
		if (aperture!=null)
		{
			aperture.save(doc, sillElement, archiveContext);
		}
		
		Iterator walkDrawables = iterator();
		
		while (walkDrawables.hasNext())
		{
			Drawable drawable = (Drawable) walkDrawables.next();
			drawable.save(doc, sillElement, archiveContext);
		}
		
		if (getLineThickness()!=1.0)
		{
			sillElement.setAttribute(IDENTIFIER_LINE_THICKNESS, String.valueOf(getLineThickness()));
		}
		
		parent.appendChild(sillElement);
	}
	
	public void open(Object parent, Node node, ArchiveContext archiveContext)
			throws NumberFormatException
	{
		// System.out.println("Sill.open: Starting");
		
		Element element = (Element) node;
		
		Crowd.openDrawables(this, element, archiveContext);
		
		if (element.hasAttribute(IDENTIFIER_NAME))
			setName(element.getAttribute(IDENTIFIER_NAME));
		
		if (element.hasAttribute(IDENTIFIER_X) && (element.hasAttribute(IDENTIFIER_Y)))
		{
			if (contentOrigin==null)
				contentOrigin=new Point2D.Double();
			
			contentOrigin.x = Double.parseDouble(element.getAttribute(IDENTIFIER_X));
			contentOrigin.y = Double.parseDouble(element.getAttribute(IDENTIFIER_Y));
			
			Logger.println("parsed content origin: "+contentOrigin);
		}
		else
		{
			Logger.println("originless: "+element.hasAttribute(IDENTIFIER_X) +"|"+ element.hasAttribute(IDENTIFIER_Y));
		}
		
		if (element.hasAttribute(Marginal.IDENTIFIER_INNER_MARGIN))
		{
			String marginAsString = element.getAttribute(Marginal.IDENTIFIER_INNER_MARGIN);
			
			try
			{
				margin = Double.parseDouble(marginAsString);
			}
			catch (NumberFormatException exception)
			{
				Logger.println("Non-numeric margin:" + marginAsString);
			}
		}
		
		Node apertureNode = XmlFriend.excavateImmediateSubnode(element, Aperture.IDENTIFIER_APERTURE);
		
		if (apertureNode!=null)
		{
			setAperture(new AperturePerch(this));
			getAperture().open(this, apertureNode, archiveContext);
		}
		
		if (element.hasAttribute(IDENTIFIER_LINE_THICKNESS))
			setLineThickness(Double.parseDouble(element.getAttribute(IDENTIFIER_LINE_THICKNESS)));
		
		// System.out.println("Sill.open: completing.");
	}
	
	public double mapOutsideX(double outsideX) // TODO rename
	{
		outsideX -= getContentOrigin().getX();
		
		if (getParent()!=null && getParent() instanceof Sill)
		{
			Sill sill = (Sill) getParent();
			outsideX = sill.mapOutsideX(outsideX);
		}
		
		return outsideX;
	}
	
	public double mapOutsideY(double outsideY) // TODO rename
	{
		outsideY -= getContentOrigin().getY();
		
		if (getParent()!=null && getParent() instanceof Sill)
		{
			Sill sill = (Sill) getParent();
			outsideY = sill.mapOutsideY(outsideY);
		}
		
		return outsideY;
	}
	
	public Sill smallestTargetSill(Point point, Sill smallest)
	{
		if ( getAperture()!=null && getAperture().getShape()!=null
					&& getAperture().getShape().contains(mapOutsideX(point.x), mapOutsideY(point.y))
					&& ( smallest == null || smallest.getAperture()==null || smallest.getAperture().getShape()==null
					     || ShapeFriend.computeArea(getAperture().getShape()) < ShapeFriend.computeArea(smallest.getAperture().getShape())) )
			smallest = this;
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Sill)
			{
				smallest = ((Sill) get(loop)).smallestTargetSill(point, smallest);
			}
		}
		
		return smallest;
	}
	
	public Sill smallestTargetSill(Point point)
	{
		Sill smallest = smallestTargetSill(point, null);
		if (smallest == null)
			return this;
		
		return smallest;
	}

	public Point2D getLocation()
	{
		return contentOrigin;
	}

	public void setLocation(Point2D location)
	{
		// TODO switch content origin to 2D
		
		if (location==null)
		{
			contentOrigin = null;
			return;
		}
		
		if (contentOrigin==null)
			contentOrigin = new Point2D.Double();
		
		contentOrigin.x = location.getX();
		contentOrigin.y = location.getY();
	}
	
	public void setLocation(double newX, double newY)
	{
		if (contentOrigin==null)
			contentOrigin = new Point2D.Double();
		
		contentOrigin.x = newX;
		contentOrigin.y = newY;
	}
	
	public void translate(double dx, double dy)
	{
		if (dx!=0 || dy!=0)
		{
			if (contentOrigin==null)
				contentOrigin = new Point2D.Double();

			contentOrigin.x += dx;
			contentOrigin.y += dy;
			
			fireChange();
		}
	}

	/**
	 * @return a Rectangle2D expressed in internal (relative) coordinates.
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		if (aperture==null)
			return null;
		
		return (Rectangle2D) aperture.getShape(); // TODO generalize to non-rectangular shapes
	}
	
	public Crowd firstContainedCrowd()
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Crowd)
				return (Crowd) get(loop);
		}
		
		return null;
	}
	
	public void add(int designatedIndex, Object designatedConstituent)
	{
		super.add(designatedIndex, designatedConstituent);
		
		if (designatedConstituent instanceof AbstractRelocateable)
		{
			((AbstractRelocateable) designatedConstituent).setParent(this);
		}
	}

	public void setWidth(double designatedWidth)
	{
		if (aperture!=null && designatedWidth!=aperture.getWidth())
		{
			aperture.setWidth(designatedWidth);
			
			fireChange();
		}
	}

	public void setHeight(double designatedHeight)
	{
		if (aperture!=null && aperture.getHeight()!=designatedHeight)
		{
			aperture.setHeight(designatedHeight);
			
			fireChange();
		}
	}
	
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		if (aperture!=null)
		{	
			if (aperture.resize(designatedSide, oldX, oldY, newX, newY))
			{
				if (designatedSide==PerimeterSegment.TOP_SIDE)
				{			
					ShapeFriend.translateY(contentOrigin, newY-oldY);
				}
				else if (designatedSide==PerimeterSegment.LEFT_SIDE)
				{
					ShapeFriend.translateX(contentOrigin, newX-oldX);
				}
				else if (designatedSide==PerimeterSegment.TOP_LEFT_CORNER)
				{			
					ShapeFriend.translate(contentOrigin, newX-oldX, newY-oldY);
				}
				else if (designatedSide==PerimeterSegment.BOTTOM_LEFT_CORNER)
				{
					ShapeFriend.translateX(contentOrigin, newX-oldX);
				}
				else if (designatedSide==PerimeterSegment.TOP_RIGHT_CORNER)
				{
					ShapeFriend.translateY(contentOrigin, newY-oldY);
		
				}
				
				fireChange();
				
				return true;
			}
		}
		return false;
	}

	public double getWidth()
	{
		Rectangle2D bounds = getPreferredBounds();
		
		if (bounds==null)
			return 0.0;
		else
			return bounds.getWidth();
	}
	
	/**
	 * If the aperture shape is a rectangle, simply returns that shape casted to a rectangle.
	 *
	 * Otherwise, queries the non-rectangular shape for its bounds.
	 *
	 * @return a Rectangle2D expressed in internal (relative) coordinates.
	 */
	public Rectangle2D getPreferredBounds()
	{
		if (aperture==null)
			return null;
		
		Rectangle2D rect = null;
		
		Shape apertureShape = aperture.getShape();
				
		if (apertureShape instanceof Rectangle2D)
			rect = (Rectangle2D) apertureShape;
		else
			rect = apertureShape.getBounds();
		
		// make defensive copy
		
		Rectangle2D rectCopy = new Rectangle2D.Double();
		rectCopy.setRect(rect);
		
		return rectCopy;
	}

	public double getHeight()
	{
		Rectangle2D bounds = getPreferredBounds();
		
		if (bounds==null)
			return 0.0;
		else
			return bounds.getHeight();
	}

	public Object clone() throws CloneNotSupportedException
	{
		Sill clonedSill = new Sill();
		
		// System.out.println("cloning Sill...");
		
		if (aperture!=null)
		{
			clonedSill.aperture = new AperturePerch(clonedSill);
			
			Shape clonedShape = null;
			if (aperture.getShape() instanceof Rectangle2D)
			{
				Rectangle2D clonedRect2d = new Rectangle2D.Double();
				clonedRect2d.setRect((Rectangle2D)aperture.getShape());
				
				// System.out.println("newly-minted clonedRect2d: "+clonedRect2d);
				
				clonedShape = clonedRect2d;
			}
			else
			{
				clonedShape = new GeneralPath(aperture.getShape());
			}
			
			clonedSill.aperture.setShape(clonedShape);
		}
		
		if (contentOrigin!=null)
		{
			clonedSill.setContentOrigin((Point2D.Double)contentOrigin.clone());
		}
		
		clonedSill.setMarginInPoints(margin);
		
		// System.out.println("starting deep copy...");
		
		deeperCopyTo(clonedSill);
		
		// System.out.println("deep copy complete");
		
		clonedSill.setParent(null);
		
		final String COPY_OF_LABEL = AbstractNamed.NAMES_TEXT.getString("copyOfLabel");
		
		if (getName()!=null)
		{
			if (!getName().startsWith(COPY_OF_LABEL))
				clonedSill.setName(COPY_OF_LABEL+" "+getName());
			else
				clonedSill.setName(getName());
		}
		
		return clonedSill;
	}
	
	public Shape actualPerchedShape(Crowdable designatedCrowdable, ShapingContext shapingContext)
	{
		if (designatedCrowdable==null)
			return null;
		
		if (aperture==null || aperture.getShape()==null)
			return designatedCrowdable.preferredPerchedInnerShape(null);
		
		Area remainingArea = new Area(designatedCrowdable.preferredPerchedInnerShape(null));
		
		if (getAperture() instanceof Perch)
		{
			Perch perch = (Perch) getAperture();
			remainingArea.intersect(new Area(perch.getInner().toShape(shapingContext, null)));
		}
		else
		{
			remainingArea.intersect(new Area(getAperture().getShape()));
		}
		
		return remainingArea;
	}
	
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selection)
	{
		if (selection!=null && selection.isSelected(this))
		{
			final Color oldColor = drawingContext.getColor();
			drawingContext.setColor(Resizeable.TRANSLUCENT_BLUE);
			
			if (aperture!=null && aperture.getShape()!=null)
			{
				Shape apertureShape = aperture.getShape();

				Rectangle rect = null;
				
				if (apertureShape instanceof Rectangle)
					rect = (Rectangle) apertureShape;
				else
					rect = apertureShape.getBounds();
				
				// graphics.fill(apertureShape);
				
				constructCornerPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, rect, 0, 0, true, true);
				drawingContext.fillPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, 3);
				
				constructCornerPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, rect, rect.width, 0, true, false);
				drawingContext.fillPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, 3);
			
				constructCornerPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, rect, 0, rect.height, false, true);
				drawingContext.fillPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, 3);
				
				constructCornerPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, rect, rect.width, rect.height, false, false);
				drawingContext.fillPolygon(CORNER_POLYGON_X, CORNER_POLYGON_Y, 3);
				
				drawingContext.setStroke(TextShape.DASHED_STROKE);
				drawingContext.setColor(Color.red);
				drawingContext.draw(aperture.getInner());
				drawingContext.setStroke(DEFAULT_STROKE);
				drawingContext.setColor(Color.black);
			}
			else
			{
				drawingContext.setColor(SLIGHTLY_TRANSLUCENT_BLUE);
				drawingContext.fill(drawingContext.getClip());
			}
			
			drawingContext.setColor(oldColor);
		}
	}

	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}

	public void setMarginInPoints(double designatedMargin)
	{
		if (margin==designatedMargin)
			return;
		
		margin = designatedMargin;
		
		if (aperture!=null)
			aperture.updateTrim();
	}

	public double getOuterMarginInPoints()
	{
		return -margin;
	}

	public double getMarginInPoints()
	{
		return margin;
	}

	public double getInnerMarginInPoints()
	{
		return margin;
	}
	
	private Alley gutters = new Alley();
	
	public void drawGutters(DrawingContext drawingContext)
	{
		if (countChildSills()==0 || drawingContext.getShootingRange()==null)
			return;
		
		harvestGutters(gutters);
		
		if (getParent()==null)
		{
			drawingContext.getShootingRange().getAlley().clear();
		}
		
		drawingContext.getShootingRange().getAlley().addAll(gutters);
		
		for (int gutterLoop=0; gutterLoop<=gutters.size()-1; gutterLoop++)
		{
			Gutter gutter = (Gutter) gutters.get(gutterLoop);
			gutter.draw(drawingContext);
			// System.out.println(gutter);
		}
	}
	
	public void harvestGutters(Alley gutters)
	{
		if (gutters.size()>0)
			gutters.clear();
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			// System.out.print("@");
			
			Drawable drawable = (Drawable) get(loop);
			
			if (drawable instanceof Sill)
			{
				Sill childSill = (Sill) drawable;
				
				// find uncatalogued neighbors
				
				int indexOfClosestNeighborToNorth = -1;
				double gapWithClosestNeighborToNorth =  0;
				
				int indexOfClosestNeighborToSouth = -1;
				double gapWithClosestNeighborToSouth =  0;

				int indexOfClosestNeighborToWest  = -1;
				double gapWithClosestNeighborToWest  =  0;
				
				int indexOfClosestNeighborToEast  = -1;
				double gapWithClosestNeighborToEast  =  0;
				
				// for (int neighborLoop=loop+1; neighborLoop<=size()-1; neighborLoop++)
				for (int neighborLoop=0; neighborLoop<=size()-1; neighborLoop++)
				{
					Drawable drawableNeighbor = (Drawable) get(neighborLoop);
					
					// System.out.print("+");
					
					if (loop!=neighborLoop && drawableNeighbor instanceof Sill)
					{
						Sill neighborSill = (Sill) drawableNeighbor;
						
						double childTopY = childSill.getLocation().getY();
						double childBottomY = childTopY + childSill.getAperture().getHeight();
						double childLeftX = childSill.getLocation().getX();
						double childRightX = childLeftX + childSill.getAperture().getWidth();
						
						// System.out.println("child: [ " + childTopY +" "+childBottomY+ " ] ");
						
						double neighborTopY = neighborSill.getLocation().getY();
						double neighborBottomY = neighborTopY + neighborSill.getAperture().getHeight();
						double neighborLeftX = neighborSill.getLocation().getX();
						double neighborRightX = neighborLeftX + neighborSill.getAperture().getWidth();
						
						// System.out.println("neighbor: [ " + neighborTopY +" "+neighborBottomY+ " ] ");
						
						// same row?
						
						if ( (childTopY>=neighborTopY && childTopY<=neighborBottomY) ||
							 (childBottomY>=neighborTopY && childBottomY<=neighborBottomY) )
						{
							// System.out.println("*** same row");
							
							// is neighbor west of child?
							
							if (neighborRightX < childLeftX)
							{
								// and closer than others?
								
								double gap = childLeftX - neighborRightX;
								
								if (gapWithClosestNeighborToWest==0 || gap<gapWithClosestNeighborToWest)
								{
									gapWithClosestNeighborToWest = gap;
									indexOfClosestNeighborToWest = neighborLoop;
								}
							}

							// is neighbor east of child?
							
							if (neighborLeftX > childRightX)
							{
								double gap = neighborLeftX - childRightX;
								
								if (gapWithClosestNeighborToEast==0 || gap<gapWithClosestNeighborToEast)
								{
									gapWithClosestNeighborToEast = gap;
									indexOfClosestNeighborToEast = neighborLoop;
								}
							}

							
						}						
						
						// same column?
						
						if ( (childLeftX>=neighborLeftX && childLeftX<=neighborRightX) ||
							 (childRightX>=neighborLeftX && childRightX<=neighborRightX) )
						{
							
							// System.out.println("*** same column");

							// is neighbor north of child? 
							
							if (neighborBottomY < childTopY )
							{
								// and closer than others?
								
								double gap = childTopY-neighborBottomY;
								// System.out.println("gap="+gap);
								
								if (gapWithClosestNeighborToNorth==0 || gap<gapWithClosestNeighborToNorth)
								{
									gapWithClosestNeighborToNorth = gap;
									indexOfClosestNeighborToNorth = neighborLoop;
								}
							}

							// is neighbor south of child?
							
							if (neighborTopY > childBottomY)
							{
								// and closer than others?
								
								double gap = neighborTopY - childBottomY;
								
								if (gapWithClosestNeighborToSouth==0 || gap<gapWithClosestNeighborToSouth)
								{
									gapWithClosestNeighborToSouth = gap;
									indexOfClosestNeighborToSouth = neighborLoop;
								}
							}
						}
					}
				}
				
				if (indexOfClosestNeighborToNorth > -1 ||
						indexOfClosestNeighborToSouth > -1 ||
						indexOfClosestNeighborToWest > -1 ||
						indexOfClosestNeighborToEast > -1)
				{
					// System.out.println("--at least one");
					
					if (indexOfClosestNeighborToNorth > -1)
					{
						Gutter gutter = new Gutter();
						gutter.setNorthAndSouth( (Sill) get(indexOfClosestNeighborToNorth), childSill );
						gutters.add(gutter); // TODO make sure this addition isn't redundant
					}
					
					if (indexOfClosestNeighborToSouth > -1)
					{
						Gutter gutter = new Gutter();
						gutter.setNorthAndSouth( childSill, (Sill) get(indexOfClosestNeighborToSouth) );
						gutters.add(gutter);
					}
					
					if (indexOfClosestNeighborToWest> -1)
					{
						Gutter gutter = new Gutter();
						gutter.setWestAndEast( (Sill) get(indexOfClosestNeighborToWest), childSill );
						gutters.add(gutter);
					}
					
					if (indexOfClosestNeighborToEast > -1)
					{
						Gutter gutter = new Gutter();
						gutter.setWestAndEast( childSill, (Sill) get(indexOfClosestNeighborToEast) );
						gutters.add(gutter);
					}
				}
			}
		}
		
		trimGutters();
	}
	
	private int countChildSills()
	{
		int theCount = 0;
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Sill)
				theCount++;
		}
		
		return theCount;
	}
	
	/**
	 * Determines a rectangle that could contain this sill and all its child sills.
	 *
	 * @return a Rectangle2D expressed in internal (relative) coordinates.
	 */
	public Rectangle2D frame() // TODO generalize to exquisitely-nested sills
	{
		Rectangle2D framed = getPreferredBounds();
		
		if (framed==null)
		{
			for (int loop=0; loop<=size()-1; loop++)
			{
				Rectangle2D childPrefBounds = null;
				
				if (get(loop) instanceof Sill)
				{
					Sill childSill = (Sill) get(loop);
					childPrefBounds = childSill.frame();

					if (childSill.contentOrigin!=null)
					{
						// represent in coords of this
						
						childPrefBounds.setRect(
							childPrefBounds.getX() + childSill.contentOrigin.getX(),
							childPrefBounds.getY() + childSill.contentOrigin.getY(),
							childPrefBounds.getWidth(),
							childPrefBounds.getHeight()
							);
					}
					
					// System.out.println("meringue "+childPrefBounds);
				}
								
				if (childPrefBounds!=null)
				{
					if (framed==null)
						framed = childPrefBounds;
					else
						framed = framed.createUnion(childPrefBounds);
				}
			}
		}

		// System.out.println("Sill.frame(): retuning "+framed);
		
		return framed;
	}
	
	/**
	 * Maps a coordinate x represented in an the old coordinate frame and expresses
	 * it as a coordinate x in the new reference frame.
	 * 
	 * If the Sills provided are not in the same hierarchy, this method won't do anything.
	 *
	 * @param oldX the coordinate representing a position along the horizontal axis of the
	 *          old coordinate frame
	 * @param oldCoordinateFrame A Sill hosting the old coordinate frame, or null
	 *                           if the old x is represented in absolute coordinates.
	 * @param newCoordinateFrame A Sill hosting the new coordinate frame, or null
	 *                           if the new x should be represented in absolute
	 *                           coordinates.
	 * @return The new x, represented in the new coordinate frame (or in absolute
	 *         coordinates if the new reference frame was null.)
	 */
	public static double mapX(double oldX, Sill oldCoordinateFrame, Sill newCoordinateFrame)
	{
		if (oldCoordinateFrame==null && newCoordinateFrame==null)
			return oldX;
		
		// System.out.println("mapX: oldX="+oldX+", oldCoordinateFrame="+oldCoordinateFrame
		// 	+", newCoordinateFrame="+newCoordinateFrame);
		
		double xShift = 0.0f;
		
		Sill sill = oldCoordinateFrame;

		while (sill!=null)
		{
			xShift += sill.getContentOrigin().getX();
				
			if (sill==newCoordinateFrame)
			{
				return oldX + xShift;
			}
			
			if (sill.getParent() instanceof Sill)
				sill = (Sill) sill.getParent();
			else
				sill = null;
			
			if (sill==null && newCoordinateFrame==null)
			{
				return oldX + xShift;
			}
		}
		
		sill = newCoordinateFrame;
		xShift = 0.0;
		
		while (sill!=null)
		{
			xShift += sill.getContentOrigin().getX();
			
			if (sill==oldCoordinateFrame)
			{
				double result = oldX - xShift;
				// System.out.println("result="+result);
			}
			
			if (sill.getParent() instanceof Sill)
				sill = (Sill) sill.getParent();
			else
				sill = null;
			
			if (sill==null && oldCoordinateFrame==null)
			{
				double result = oldX - xShift;
				// System.out.println("result="+result);
			}
		}
		
		// System.out.println("no go");
		
		return oldX;
	}
	
	/**
	 * Maps a coordinate y represented in an the old coordinate frame and eypresses
	 * it as a coordinate y in the new reference frame.
	 *
	 * If the Sills provided are not in the same hierarchy, this method won't do anything.
	 *
	 * @param oldY the coordinate representing a position along the horizontal ayis of the
	 *          old coordinate frame
	 * @param oldCoordinateFrame A Sill hosting the old coordinate frame, or null
	 *                           if the old y is represented in absolute coordinates.
	 * @param newCoordinateFrame A Sill hosting the new coordinate frame, or null
	 *                           if the new y should be represented in absolute
	 *                           coordinates.
	 * @return The new y, represented in the new coordinate frame (or in absolute
	 *         coordinates if the new reference frame was null.)
	 */
	public static double mapY(double oldY, Sill oldCoordinateFrame, Sill newCoordinateFrame)
	{
		if (oldCoordinateFrame==null && newCoordinateFrame==null)
			return oldY;
		
		// System.out.println("mapY: oldY="+oldY+", oldCoordinateFrame="+oldCoordinateFrame
		// 	+", newCoordinateFrame="+newCoordinateFrame);
		
		double yShift = 0.0f;
		
		Sill sill = oldCoordinateFrame;

		while (sill!=null)
		{
			yShift += sill.getContentOrigin().getY();
				
			if (sill==newCoordinateFrame)
			{
				double result = oldY + yShift;
				System.out.println("result="+result);
				return result;
			}
			
			if (sill.getParent() instanceof Sill)
				sill = (Sill) sill.getParent();
			else
				sill = null;
				
			if (sill==null && newCoordinateFrame==null)
			{
				return oldY + yShift;
			}
		}
		
		sill = newCoordinateFrame;
		yShift = 0.0;
		
		while (sill!=null)
		{
			yShift += sill.getContentOrigin().getY();
			
			if (sill==oldCoordinateFrame)
			{
				return oldY - yShift;
			}
			
			if (sill.getParent() instanceof Sill)
				sill = (Sill) sill.getParent();
			else
				sill = null;
		}
		
		// System.out.println("no go");
		
		return oldY;
	}
	
	/**
	 * @return the Sill of the designated Kid, or null if none could be found.
	 */
	public static Sill findParentSillOf(Kid designatedKid)
	{
		for (Object potentialKid=designatedKid;
				potentialKid instanceof Kid;
				potentialKid = ((Kid)potentialKid).getParent())
		{
			if (potentialKid instanceof Sill)
			{
				// System.out.println("findParentSillOf: found one! "+potentialKid);
				return (Sill) potentialKid;
			}
		}
		
		// System.out.println("findParentSillOf: gave up");
		
		return null;
	}
	
	public static double mapAbsoluteXIntoCoordinateFrameOf(double absoluteX, Object designatedPotentialKid)
	{
		double x = absoluteX;
		
		for (Object potentialKid=designatedPotentialKid; 
				potentialKid instanceof Kid; 
				potentialKid = ((Kid)potentialKid).getParent() )
		{
			if (potentialKid instanceof Sill)
			{
				Sill sill = (Sill) potentialKid;
				x -= sill.getContentOrigin().getX();
			}
		}
		
		return x;
	}
	
	public static double mapAbsoluteYIntoCoordinateFrameOf(double absoluteY, Object designatedPotentialKid)
	{
		double y = absoluteY;
		
		for (Object potentialKid=designatedPotentialKid; 
				potentialKid instanceof Kid; 
				potentialKid = ((Kid)potentialKid).getParent() )
		{
			if (potentialKid instanceof Sill)
			{
				Sill sill = (Sill) potentialKid;
				y -= sill.getContentOrigin().getY();
			}
		}
		
		return y;
	}
	
	/**
	 * Produces a Rectangle that can comfortably fit the provided drawables.
	 * 
	 * @param drawablesList a list of Drawables
	 */
	public static Rectangle2D frame(java.util.List drawablesList)
	{
		if (drawablesList==null)
			return null;
		
		Rectangle2D framed = null;
		
		for (int loop=0; loop<=drawablesList.size()-1; loop++)
		{
			if (drawablesList.get(loop) instanceof Drawable)
			{
				Rectangle2D childBounds = null;
				Drawable drawable = (Drawable) drawablesList.get(loop);

				if (drawable instanceof Sill)
				{
					Sill sill = (Sill) drawable;
					
					childBounds = sill.frame();
					
					// System.out.println("shifting-->"+sill.getContentOrigin());
					
					childBounds.setRect(childBounds.getX()+sill.getContentOrigin().getX(),
							childBounds.getY()+sill.getContentOrigin().getY(),
							childBounds.getWidth(),
							childBounds.getHeight()
						);
					
					// System.out.println("shifted childBounds="+childBounds);
				}
				else if (drawable instanceof TextShape)
				{
					TextShape textShape = (TextShape) drawable;
					childBounds = textShape.getBounds2D();
				}
				else if (drawable instanceof Shapeable)
				{
					Shapeable shapeable = (Shapeable) drawable;
					Shape shaped = shapeable.toShape(null, null);
					
					if (shaped!=null)
						childBounds = shaped.getBounds2D();
				}
				else if (drawable instanceof Resizeable)
				{
					Resizeable resizeable = (Resizeable) drawable;
					
					childBounds = resizeable.getPreferredBounds();
				}
				else if (drawable instanceof Relocateable)
				{
					Relocateable relocateable = (Relocateable) drawable;
					childBounds = relocateable.getResizeableBounds2D();
				}
				
				if (childBounds!=null)
				{
					if (framed==null)
						framed = childBounds;
					else
						framed = framed.createUnion(childBounds);
				}
			}
		}
		
		// System.out.println("Clipping.frame()="+framed);
		
		return framed;
	}
	
	public void fireChange()
	{
		SingleThreadedChangeSupport foundChangeSupport = getChangeSupport();
		
		if (foundChangeSupport!=null)
			foundChangeSupport.fireChange(CHANGE_EVENT);
	}
	
	private final TypesafeList trash = new TypesafeList(Gutter.class);
	
	/**
	 * Make sure there is no overlap between the gutters.
	 */
	private void trimGutters()
	{
		trash.clear();
		
		for (int outerloop=0; outerloop<=gutters.size()-1; outerloop++)
		{
			Gutter gutter = (Gutter) gutters.get(outerloop);
			
			for (int innerloop=outerloop+1; innerloop<=gutters.size()-1; innerloop++)
			{
				Gutter otherGutter = (Gutter) gutters.get(innerloop);
				Rectangle2D otherGutterBounds = otherGutter.getBoundaryUsingCacheIfPossible();
				// gutter.trimMostRecentBoundsWith(otherGutter);
				
				
				if (gutter.getBoundaryUsingCacheIfPossible().intersects(
						otherGutterBounds.getX(), otherGutterBounds.getY(),
						otherGutterBounds.getWidth(), otherGutterBounds.getHeight()))
				{
					gutters.remove(otherGutter);
					// System.out.println("new count: "+gutters.size());
					trash.add(gutter);
				}
			}
		}
		
		gutters.removeAll(trash);
	}
	
	public double getLineThickness()
	{
		if (aperture==null)
			return 1.0;
		
		return aperture.getBorderWidthInPoints();
	}
	
	public void setLineThickness(double designatedLineThickness)
	{
		if (designatedLineThickness!=getLineThickness())
		{
			// System.out.println("changing line thickness to: "+designatedLineThickness);
			aperture.setBorderWidthInPoints(designatedLineThickness);
			final BasicStroke replacement = new BasicStroke((float)designatedLineThickness);
			stroke = replacement;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}

	public Color getFillColor()
	{
		if (aperture==null)
			return null;
		
		return aperture.getFillColor();
	}

	public void setFillColor(Color designatedColor)
	{
		if (aperture==null)
			return;
		
		if (designatedColor!=aperture.getFillColor())
		{			
			aperture.setFillColor(designatedColor);
			SingleThreadedChangeSupport.fireChangeEvent(this);
		}
	}

	public Color getOutlineColor()
	{
		if (aperture==null)
			return null;
		
		return aperture.getOutlineColor();
	}

	public void setOutlineColor(Color designatedColor)
	{
		if (aperture==null)
			return;
		
		if (designatedColor!=aperture.getOutlineColor())
		{
			aperture.setOutlineColor(designatedColor);
			SingleThreadedChangeSupport.fireChangeEvent(this);
		}
	}
}
