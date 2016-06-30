/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;
import java.awt.geom.Point2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.layout.Resizeable;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.util.PubliclyCloneable;


/**
 * A simple, pointy Stem implementation.
 * 
 * @author dhorlick
 */
public class ConcreteStem extends AbstractStem implements Stem, PubliclyCloneable
{
	public ConcreteStem()
	{
		setFocus(new Point2D.Double());
	}
	
	public ConcreteStem(Point2D.Double designatedFocus)
	{
		setFocus(designatedFocus);
	}
	
	public double getArcLengthToLeadingEdge()
	{
		// return arcLengthToRoot;
		return super.getLeadingEdgePositionAsPerimeterFraction() * getParentStemmed().getArclength(); // TODO cache?
	}
	
	public double getArcLengthAtStart()
	{
		return getArcLengthToLeadingEdge();
	}
	
	public double getArcLengthAtEnd()
	{
		return getArcLengthToLeadingEdge() + getRootWidthInPoints();
	}
	
	public boolean occupiesArclength(double arclengthToHere)
	{
		return (getArcLengthAtStart()<=arclengthToHere && getArcLengthAtEnd()>=arclengthToHere);
	}
	
	public boolean leadingEdgePreceedsArcLength(double arclengthToHere)
	{
		return (getArcLengthAtStart()<=arclengthToHere);
	}
	
	public boolean trailingEdgePreceedsArcLength(double arclengthToHere)
	{
		return (getArcLengthAtEnd()<=arclengthToHere);
	}
	
	public void paintSelectionIfAppropriate(DrawingContext d, Selection selected)
	{
		if (isSelected(selected))
		{
			d.plotDot(getFocus().getX(), getFocus().getY(), Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, true);
			d.plotDot(getLeadingEdgeX(), getLeadingEdgeY(), Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, true);
			d.plotDot(getTrailingEdgeX(), getTrailingEdgeY(), Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, true);
		}
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		ConcreteStem cloned = (ConcreteStem) super.clone();
		Point2D.Double clonedFocus = new Point2D.Double();
		clonedFocus.setLocation(getFocus());
		cloned.setFocus(clonedFocus);
		
		return cloned;
	}	
}
