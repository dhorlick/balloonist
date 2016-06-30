/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.Point;
import java.awt.Shape;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.PubliclyCloneable;


/**
 * Models a four-sided shape with parallel opposing sides of equal length.
 *
 * This is useful for depicting narration in a comic.
 *
 * @author dhorlick
 */
public class Parallelogram extends AbstractResizeable implements Drawable, Shapeable, Interactive, Perimetered, PubliclyCloneable
{
	private static final int TOLERANCE = 4;
	private double inset = 12.0;
	
	public static final String IDENTIFIER_PARALLELOGRAM = "parallelogram";
	private static final String IDENTIFIER_INSET = "inset";
	
	private ParallelogramShaper parallelogramShaper = new ParallelogramShaper();
	
	public Parallelogram()
	{
		setSize(1, 1);
	}

	/**
	 * @see com.smithandtinkers.layout.Drawable#draw(com.smithandtinkers.graphics.DrawingContext)
	 */
	public void draw(DrawingContext drawingContext)
	{
		if (getBoundingRectangle()!=null)
		{
			if (inset>=0)
			{
				drawingContext.drawLine((int)getBoundingRectangle().getX()+(int)inset, (int)getBoundingRectangle().getY(), 
						(int)getBoundingRectangle().getX(), (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight());
				drawingContext.drawLine((int)getBoundingRectangle().getX(), (int)getBoundingRectangle().getY()+(int)(int)getBoundingRectangle().getHeight(), 
						(int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth(), (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight());
				drawingContext.drawLine((int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth()-(int)inset, (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight(),
						(int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth(), (int)getBoundingRectangle().getY());
				drawingContext.drawLine((int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth(), (int)getBoundingRectangle().getY(),
						(int)getBoundingRectangle().getX()+(int)inset, (int)getBoundingRectangle().getY());
			}
			else
			{
				drawingContext.drawLine((int)getBoundingRectangle().getX(), (int)getBoundingRectangle().getY(), 
						(int)getBoundingRectangle().getX()-(int)inset, (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight());
				drawingContext.drawLine((int)getBoundingRectangle().getX()-(int)inset, (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight(), 
						(int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth(), (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight());
				drawingContext.drawLine((int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth(), (int)getBoundingRectangle().getY()+(int)getBoundingRectangle().getHeight(),
						(int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth()-(int)inset, (int)getBoundingRectangle().getY());
				drawingContext.drawLine((int)getBoundingRectangle().getX()+(int)getBoundingRectangle().getWidth()-(int)inset, (int)getBoundingRectangle().getY(),
						(int)getBoundingRectangle().getX()+(int)inset, (int)getBoundingRectangle().getY());
			}
		}
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element parallelogramElement = doc.createElement(IDENTIFIER_PARALLELOGRAM);
		
		applyAttributesAndSubElements(doc, parallelogramElement);
		
		parallelogramElement.setAttribute(IDENTIFIER_INSET, String.valueOf(inset));
		
		parent.appendChild(parallelogramElement);
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, org.w3c.dom.Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		Element element = (Element) node;
		
		extractAttributesAndSubElements(parent, element);
		if (element.hasAttribute(IDENTIFIER_INSET))
		{
			inset = Double.parseDouble(element.getAttribute(IDENTIFIER_INSET));
		}
	}
		
	public double getInset()
	{
		return inset;
	}
	
	public void setInset(double designatedTopLeftInset)
	{
		if (!wouldProspectiveInsetBeAllowed(designatedTopLeftInset))
		{
			throw new IllegalArgumentException("designated Top Left Inset: " + designatedTopLeftInset +
					" must fall within range: " + minAllowableInset() + " thru " + maxAllowableInset());
		}

		if (inset!=designatedTopLeftInset)
		{
			inset = designatedTopLeftInset;
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#toShape(ShapingContext, PlottingContext)
	 */
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return parallelogramShaper.shapen(getPreferredBounds(), inset);
	}

	/**
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		inset += (newX-oldX);
	}

	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(java.awt.Point)
	 */
	public boolean onPerimeter(Point thePoint)
	{
		return onPerimeter(thePoint.getX(), thePoint.getY());
	}

	/**
	 * @see com.smithandtinkers.layout.Perimetered#onPerimeter(double, double)
	 */
	public boolean onPerimeter(double x, double y)
	{
		x -= getLocation().getX();
		y -= getLocation().getY();
		
		if (onLeftSide(x,y))
			return true;
		
		if (onRightSide(x,y))
			return true;
		
		return false;
	}
	
	/**
	 * @param x in inside coordinates
	 * @param y in inside coordinates
	 */
	public boolean onLeftSide(double x, double y)
	{
		double dist = PerimeterSegment.computeDistance(x, y, x, computeLefthandY(x));
		// System.out.println("ldist="+dist);
		
		return (dist<TOLERANCE);	
	}
	
	/**
	 * @param x in inside coordinates
	 * @param y in inside coordinates
	 */
	public boolean onRightSide(double x, double y)
	{
		double dist = PerimeterSegment.computeDistance(x, y, x, computeRighthandY(x));
		// System.out.println("rdist="+dist);
		
		return (dist<TOLERANCE);
	}
	
	public double computeSlope()
	{
		if (inset==0)
			return Double.MAX_VALUE; // close enough
			
		return getHeight() / inset;
	}
	
	public double computeLefthandY(double x)
	{
		double m = computeSlope();
		
		// remember, Java2D coords are inverted from paper coords: y0 - mx;
		
		return getHeight() - m*x;
	}
	
	public double computeRighthandY(double x)
	{
		double m = computeSlope();
		
		// remember, Java2D coords are inverted from paper coords: y0 - mx;
		
		return m*(getWidth()-x);
	}
	
	public double maxAllowableInset()
	{
		return maxAllowableInclination()*getWidth();
	}
	
	public double minAllowableInset()
	{
		return minAllowableInclination()*getWidth();
	}
	
	public boolean wouldProspectiveInsetBeAllowed(double propsectiveInset)
	{
		if (propsectiveInset<minAllowableInset() || propsectiveInset>maxAllowableInset())
			return false;
		
		return true;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		Parallelogram cloned = new Parallelogram();
		cloned.setLocation(getLocation());
        cloned.setSize(getWidth(), getHeight());
		cloned.setInset(getInset());
		return cloned;
	}
	
	/**
	 * @return the inset divided by the width. For the parallelogram to be useful as a 
	 * text container, this value should be maintained between -0.5 and 0.5
	 *
	 * @throws IllegalStateException if the width is zero
	 */
	public double getInclination()
	{
		if (getWidth()==0.0)
			throw new IllegalStateException("Width is zero.");
		
		return inset/getWidth();
	}
	
	/**
	 * Changes the inset to observe the designated inclination.
	 *
	 * see {@link #getInclination}
	 */
	public void setInclination(double designatedInclination)
	{
		if (!wouldProspectiveInclinationBeAllowed(designatedInclination))
			throw new IllegalArgumentException("Invalid inclination: "+designatedInclination+". Must be in range "+minAllowableInclination()+" thru "+maxAllowableInclination());
		
		setInset(designatedInclination*getWidth());
	}
	
	public static double minAllowableInclination()
	{
		return -0.5;
	}
	
	public static double maxAllowableInclination()
	{
		return 0.5;
	}
	
	public static boolean wouldProspectiveInclinationBeAllowed(double propsectiveInclination)
	{
		if (propsectiveInclination > maxAllowableInclination() || propsectiveInclination < minAllowableInclination())
			return false;
		
		return true;
	}
}
