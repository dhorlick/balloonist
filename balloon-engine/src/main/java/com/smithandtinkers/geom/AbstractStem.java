/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.layout.Colorful;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.PerimeterSegment;
import com.smithandtinkers.layout.Resizeable;
import com.smithandtinkers.layout.Selectable;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.*;


/**
 * @author dhorlick
 */
public abstract class AbstractStem extends AbstractGodKid implements Stem, Selectable, Named
{
	private Point2D.Double focus;
	private double leadingEdgePositionAsSideFraction = 0.3;
	private double rootWidthInPoints = 12.0;
	
	private double leadingEdgeX = 0.0;
	private double leadingEdgeY = 0.0;
	private double trailingEdgeX = 0.0;
	private double trailingEdgeY = 0.0;
	
	/**
	 * The distance in points between bubbles.
	 */
	private double bubblePeriod = 14.0;
	
	private String name;
	
	/**
	 * relevant to the bubbled case
	 */
	private double rootBubbleRotationInRadians = 0.0;
	
	private double rootStemInclinationInRadians = 0.0;
	
	private double focusStemInclinationInRadians = 0.0;
	
	private double bendiness = 1.0;
	
	private Type type = ICICLE_TYPE;
	
	final protected ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public static final Type ICICLE_TYPE = new Type("icicle", true);
	public static final Type BUBBLED_TYPE = new Type("bubbled", false);
	public static final Type LOLLIPOP_TYPE = new Type("lollipop", false);
	
	public static final int DEFAULT_BUBBLED_BUBBLE_COUNT = 5;
	public static final int DEFAULT_BUBBLE_HEIGHT = 6;
	
	public static final String IDENTIFIER_STEM = "stem";
		private static final String IDENTIFIER_ROOT_WIDTH_IN_POINTS = "root-width";
		private static final String IDENTIFIER_LEADING_EDGE_POSITION_AS_SIDE_FRACTION = "leading-edge-position-as-perimeter-fraction";
		private static final String IDENTIFIER_FOCUS_Y = "focus-y";
		private static final String IDENTIFIER_FOCUS_X = "focus-x";
		// private static final String IDENTIFIER_BUBBLE_COUNT = "bubbles";
		private static final String IDENTIFIER_BUBBLE_PERIOD = "bubble-period";
		protected static final String IDENTIFIER_TYPE = "type";
	public static final String IDENTIFIER_ROOT_STEM_INCLINATION_IN_RADIANS = "root-stem-inclination-in-radians";
	public static final String IDENTIFIER_FOCUS_STEM_INCLINATION_IN_RADIANS = "focus-stem-inclination-in-radians";
	public static final String IDENTIFIER_RIGIDITY = "rigidity";
	
	public static final double MINIMUM_BUBBLE_DENSITY = 0.03;
	public static final double MAXIMUM_BUBBLE_DENSITY = 0.09;
	
	private static final Stroke THIN_STROKE = new BasicStroke(1.0f);

	
	/**
	 * @see com.smithandtinkers.geom.Stem#getFocus()
	 */
	public Point2D.Double getFocus()
	{
		if (focus==null)
			return null;
			
		return new Point2D.Double(focus.getX(), focus.getY());
	}

	/**
	 * @see com.smithandtinkers.geom.Stem#getLeadingEdgePositionAsPerimeterFraction()
	 */
	public double getLeadingEdgePositionAsPerimeterFraction()
	{
		return leadingEdgePositionAsSideFraction;
	}

	/**
	 * @see com.smithandtinkers.geom.Stem#getRootWidthInPoints()
	 */
	public double getRootWidthInPoints()
	{
		return rootWidthInPoints;
	}
	
	public void setFocus(Point2D.Double designatedFocus)
	{
		focus = designatedFocus;
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setLeadingEdgePositionAsSideFraction(double designatedLeadingEdgePositionAsSideFraction)
	{
		if (designatedLeadingEdgePositionAsSideFraction<0.0 || designatedLeadingEdgePositionAsSideFraction>1.0)
		{
			throw new IllegalArgumentException("Must be between 0 and 1. Was: "+designatedLeadingEdgePositionAsSideFraction);
		}
		
		leadingEdgePositionAsSideFraction = designatedLeadingEdgePositionAsSideFraction;
	}
	
	/**
	 * @see com.smithandtinkers.geom.Stem#setRootWidthInPoints(double)
	 */
	public void setRootWidthInPoints(double designatedWidth)
	{
		if (!wouldProspectiveRootWidthInPointsBeAllowed(designatedWidth))
		{
			throw new IllegalArgumentException("designated width: " + designatedWidth + " must be within range: "+minAllowableRootWidthInPoints() + " thru " + maxAllowableRootWidthInPoints());
		}
		
		if (rootWidthInPoints != designatedWidth)
		{
			rootWidthInPoints = designatedWidth;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
			
			// System.out.println("set root width in points to: "+designatedWidth);
		}
	}
	
	public Stemmed getParentStemmed()
	{
		return (Stemmed) getParent();
	}
	
	public void setParentStemmed(Stemmed designatedParent)
	{
		setParent((Collection)designatedParent); // TODO horrible
	}
	
	public void setParent(Collection designatedParent)
	{
		if (designatedParent!=null && !(designatedParent instanceof Stemmed))
			throw new ClassCastException();
		
		super.setParent(designatedParent);
	}
	
	/*public void applyAttributes(org.w3c.dom.Document doc, Element element)
	{
		Element stemElement = doc.createElement(IDENTIFIER_STEM);
		
		stemElement.setAttribute(IDENTIFIER_ROOT_WIDTH_IN_POINTS, String.valueOf(rootWidthInPoints));
		stemElement.setAttribute("place", String.valueOf(leadingEdgePositionAsSideFraction));
		
		element.setAttribute(IDENTIFIER_FOCUS_X, String.valueOf(focus.getX()));
		element.setAttribute(IDENTIFIER_FOCUS_Y, String.valueOf(focus.getY()));
		
		element.setAttribute(IDENTIFIER_ROOT_STEM_INCLINATION_IN_RADIANS, 
				String.valueOf(getRootStemInclinationInRadians()));
		element.setAttribute(IDENTIFIER_FOCUS_STEM_INCLINATION_IN_RADIANS,
				String.valueOf(getFocusStemInclinationInRadians()));
		
		element.appendChild(stemElement);
	} */
	
	/**
	 * @see com.smithandtinkers.geom.Stem#getLeadingEdgeX()
	 */
	public double getLeadingEdgeX()
	{
		return leadingEdgeX;
	}

	/**
	 * @see com.smithandtinkers.geom.Stem#getLeadingEdgeY()
	 */
	public double getLeadingEdgeY()
	{
		return leadingEdgeY;
	}
	
	/**
	 * Allows for the caching of the most-recently computed leading edge x.
	 * 
	 * @param designatedLeadingEdgeX
	 */
	void setLeadingEdgeX(double designatedLeadingEdgeX)
	{
		if (leadingEdgeX != designatedLeadingEdgeX)
		{
			leadingEdgeX = designatedLeadingEdgeX;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	/**
	 * Allows for the caching of the most-recently computed leading edge y.
	 * 
	 * @param designatedLeadingEdgeY
	 */
	void setLeadingEdgeY(double designatedLeadingEdgeY)
	{
		leadingEdgeY = designatedLeadingEdgeY;
	}
	
	public double getTrailingEdgeX()
	{
		return trailingEdgeX;
	}
	
	public double getTrailingEdgeY()
	{
		return trailingEdgeY;
	}
	
	void setTrailingEdgeX(double designatedTrailingEdgeX)
	{
		trailingEdgeX = designatedTrailingEdgeX;
	}
	
	void setTrailingEdgeY(double designatedTrailingEdgeY)
	{
		trailingEdgeY = designatedTrailingEdgeY;
	}
	
	public boolean contains(double x, double y)
	{
		return AbstractStem.triangleContains(leadingEdgeX, leadingEdgeY, focus.getX(), focus.getY(), trailingEdgeX, trailingEdgeY, x, y);
	}
	
	/**
	 * Determines whether or not a point is inside a triangle.
	 * 
	 * see http://groups.google.com/groups?hl=en&lr=&safe=off&threadm=3ptcff%24krp%40olympus.net&rnum=1&prev=/groups%3Fq%3Dtriangle%2B%2522contained%2Bwithin%2522%2Bpoint%26hl%3Den%26lr%3D%26safe%3Doff%26selm%3D3ptcff%2524krp%2540olympus.net%26rnum%3D1
	 * 
	 * Originated by Paul K. Holmberg, modified by Dave Horlick.
	 *
	 * @param v1x The x coordinate of the triangle's first vertex.
	 * @param v1y The y coordinate of the triangle's first vertex.
	 * @param v2x The x coordinate of the triangle's second vertex.
	 * @param v2y The y coordinate of the triangle's second vertex.
	 * @param v3x The x coordinate of the triangle's third vertex.
	 * @param v3y The y coordinate of the triangle's third vertex.
	 * @param x   The x coordinate of the point to test for containment within the triangle.
	 * @param y   The y coordinate of the point to test for containment within the triangle.
	 */
	public static boolean triangleContains(double v1x, double v1y, double v2x, double v2y, double v3x, double v3y, double x, double y)
	{
		boolean right = false;
		boolean  left = false;
		
		/*
			A simple method is, for each edge that has endpoints above & below point.y,
			find the x-intercept on line point.y. If it is to the left of point.x,
			set LEFT=TRUE, else set RIGHT=TRUE.
			after doing this for all relevant edges, the point is inside if both LEFT and
			RIGHT is TRUE.
		 */
		
		if ((y>v1y && y<v2y) || (y<v1y && y>v2y))
		{
			double xIntercept = AbstractStem.determineXIntercept(v1x, v1y, v2x, v2y, y);
			
			// System.out.println("1) x intercept="+xIntercept);
			
			if (x < xIntercept)
				left = true;
			else if (x > xIntercept)
				right = true;
		}
		
		if ((y>v2y && y<v3y) || (y<v2y && y>v3y))
		{
			double xIntercept = AbstractStem.determineXIntercept(v2x, v2y, v3x, v3y, y);
			
			// System.out.println("2) x intercept="+xIntercept);
			
			if (x < xIntercept)
				left = true;
			else if (x > xIntercept)
				right = true;
		}
		
		if ((y>v3y && y<v1y) || (y<v3y && y>v1y))
		{
			double xIntercept = AbstractStem.determineXIntercept(v3x, v3y, v1x, v1y, y);
			
			// System.out.println("3) x intercept="+xIntercept);
			
			if (x < xIntercept)
				left = true;
			else if (x > xIntercept)
				right = true;
		}
		
		// System.out.println("left: "+left);
		// System.out.println("right: "+right);
		
		return left && right;
	}
	
	public static double determineXIntercept(double x0, double y0, double x1, double y1, double y)
	{
		if (x1==x0) // infinite slope
		{
			return x0;
		}
		
		double m = (y1-y0)/(x1-x0);
		double b = y0 - m*x0;
		return (y-b)/m;
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element stemElement = doc.createElement(IDENTIFIER_STEM);
		applyAttributesAndSubElements(doc, stemElement);
		
		parent.appendChild(stemElement);
	}

	protected void applyAttributesAndSubElements(Document doc, Element stemElement)
	{
		stemElement.setAttribute(IDENTIFIER_FOCUS_X, String.valueOf(focus.getX()));
		stemElement.setAttribute(IDENTIFIER_FOCUS_Y, String.valueOf(focus.getY()));
		stemElement.setAttribute(IDENTIFIER_LEADING_EDGE_POSITION_AS_SIDE_FRACTION, String.valueOf(leadingEdgePositionAsSideFraction));
		stemElement.setAttribute(IDENTIFIER_ROOT_WIDTH_IN_POINTS, String.valueOf(rootWidthInPoints));
		// stemElement.setAttribute(IDENTIFIER_BUBBLE_COUNT, String.valueOf(bubbleCount));
		stemElement.setAttribute(IDENTIFIER_BUBBLE_PERIOD, String.valueOf(getBubblePeriod()));
		
		if (getType()!=null)
			stemElement.setAttribute(IDENTIFIER_TYPE, type.getCode());
		
		if (getRootStemInclinationInRadians()!=0)
		{
			stemElement.setAttribute(IDENTIFIER_ROOT_STEM_INCLINATION_IN_RADIANS, 
					String.valueOf(getRootStemInclinationInRadians()));
		}
		
		if (getFocusStemInclinationInRadians()!=0)
		{
			stemElement.setAttribute(IDENTIFIER_FOCUS_STEM_INCLINATION_IN_RADIANS,
					String.valueOf(getFocusStemInclinationInRadians()));
		}
		
		if (determineRigidity()!=0.0)
		{
			stemElement.setAttribute(IDENTIFIER_RIGIDITY, String.valueOf(determineRigidity()));
		}
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		Element element = (Element) node;
		
		if (focus==null)
			setFocus(new Point2D.Double());
		
		if (element.hasAttribute(IDENTIFIER_FOCUS_X) && element.hasAttribute(IDENTIFIER_FOCUS_Y))
			focus.setLocation(Double.parseDouble(element.getAttribute(IDENTIFIER_FOCUS_X)), Double.parseDouble(element.getAttribute(IDENTIFIER_FOCUS_Y)));
		
		if (element.hasAttribute(IDENTIFIER_LEADING_EDGE_POSITION_AS_SIDE_FRACTION))
			setLeadingEdgePositionAsSideFraction(Double.parseDouble(element.getAttribute(IDENTIFIER_LEADING_EDGE_POSITION_AS_SIDE_FRACTION)));
		
		if (element.hasAttribute(IDENTIFIER_ROOT_WIDTH_IN_POINTS))
			setRootWidthInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_ROOT_WIDTH_IN_POINTS)));
		
		if (element.hasAttribute(IDENTIFIER_BUBBLE_PERIOD))
		{
			setBubblePeriod(Double.parseDouble(element.getAttribute(IDENTIFIER_BUBBLE_PERIOD)));
		}
		
		if (element.hasAttribute(IDENTIFIER_TYPE))
		{
			setType((Type)Type.find(element.getAttribute(IDENTIFIER_TYPE)));
		}
		
		if (element.hasAttribute(IDENTIFIER_FOCUS_STEM_INCLINATION_IN_RADIANS))
		{
			setFocusStemInclinationInRadians(Double.parseDouble(element.getAttribute(IDENTIFIER_FOCUS_STEM_INCLINATION_IN_RADIANS)));
		}
		
		if (element.hasAttribute(IDENTIFIER_ROOT_STEM_INCLINATION_IN_RADIANS))
		{
			setRootStemInclinationInRadians(Double.parseDouble(element.getAttribute(IDENTIFIER_ROOT_STEM_INCLINATION_IN_RADIANS)));
		}
		
		if (element.hasAttribute(IDENTIFIER_RIGIDITY))
		{
			setRigidity(Double.parseDouble(element.getAttribute(IDENTIFIER_RIGIDITY)));
		}
		
		if (parent!=null && parent instanceof Collection)
		{
			Collection parentCollection = (Collection) parent;
			setParent(parentCollection);
			parentCollection.add(this);
		}
	}
	
	/**
	 * Construct a portion of a balloon's curve that is occupied by this stem.
	 * 
	 * @param generalPath
	 */
	public void stampSegment(GeneralPath generalPath, PlottingContext guideSlate)
	{
		if (getBubbleCount()==0 && generalPath!=null)
		{
			if (!determineWhetherCurved())
			{
				rule(generalPath);
			}
			else
			{
				// find intersection of angles

				final double rootX = ( getTrailingEdgeX()+getLeadingEdgeX() )/2.0;
				final double rootY = ( getTrailingEdgeY()+getLeadingEdgeY() )/2.0;
				
				if (!onSide())
				{
					if (Math.abs(rootX - getFocus().getX()) < (getRootWidthInPoints()/2.0))
					{
						rule(generalPath);
						return;
					}
				}
				else
				{
					if (Math.abs(rootY - getFocus().getY()) < (getRootWidthInPoints()/2.0))
					{
						rule(generalPath);
						return;
					}
				}
				
				final double OQ = PerimeterSegment.computeDistance(rootX, rootY, getFocus().getX(), getFocus().getY());
				
				kinkCurve(generalPath, getLeadingEdgeX(), getLeadingEdgeY(), getFocus().getX(), getFocus().getY(), Boolean.FALSE,
						getRootStemInclinationInRadians(), getFocusStemInclinationInRadians(), guideSlate);
				kinkCurve(generalPath, getTrailingEdgeX(), getTrailingEdgeY(), getFocus().getX(), getFocus().getY(), Boolean.TRUE, 
						getRootStemInclinationInRadians(), getFocusStemInclinationInRadians(), guideSlate);
			}
		}
	}
	
	private void rule(GeneralPath generalPath)
	{
		generalPath.lineTo((float)getFocus().getX(), (float)getFocus().getY());
		generalPath.lineTo((float)getTrailingEdgeX(), (float)getTrailingEdgeY());
	}
	
	private void kinkCurve(GeneralPath generalPath, double x0, double y0, double x3, double y3, Boolean backwards,
			double theta_O, double theta_Q, PlottingContext guideSlate)
	{
		final double OQ = PerimeterSegment.computeDistance(x0, y0, x3, y3);

		final double OP = OQ * Math.sin(Math.abs(theta_Q)) 
				/ Math.sin(Math.PI - Math.abs(theta_O) - Math.abs(theta_Q));
		
		double slantAngle = 0;
		
		if (!onSide())
		{
			slantAngle = Math.abs( Math.atan(
					(x3-x0) / 
					(y3-y0) ) );
		}
		else
		{
			slantAngle = Math.abs( Math.atan(
					(y3-y0) ) /
					(x3-x3));
		}

		final double relativeSlantAngle = Math.asin(Math.abs(
				PerimeterSegment.computeDistance(getTrailingEdgeX(), getTrailingEdgeY(),
				getLeadingEdgeX(), getLeadingEdgeY())/2.0)/OQ);
		
		double theAngle = 0.0;
		
		// if (!backwards)
		if (backwards==Boolean.FALSE)
			theAngle = -slantAngle+getRootStemInclinationInRadians()+relativeSlantAngle;
		else if (backwards==Boolean.TRUE)
			theAngle = -slantAngle+getRootStemInclinationInRadians()+relativeSlantAngle; // TODO should TRUE and FALSE yield different results? if not, this can be simplified
		else
			theAngle = -slantAngle+getRootStemInclinationInRadians();
		
		double P_x, P_y, P1_x, P1_y, P2_x, P2_y;
		
		if (!onSide())
		{
			if (x3 < x0)
				P_x = x0 + OP * Math.sin(theAngle);
			else
				P_x = x0 - OP * Math.sin(theAngle);

			if (y3 > y0)
				P_y = y0 + OP * Math.cos(theAngle);
			else
				P_y = y0 - OP * Math.cos(theAngle);
		}
		else
		{
			if (x3 < x0)
				P_x = x0 - OP * Math.cos(theta_O);
			else
				P_x = x0 + OP * Math.cos(theta_O);
			
			if (y3 < y0)
				P_y = y0 - OP * Math.sin(theta_O);
			else 
				P_y = y0 + OP * Math.sin(theta_O);
			
			// TODO support right side
		}

		P1_x = x0 + getBendiness() * (2.0/3.0) * (P_x-x0);
		P1_y = y0 + getBendiness() * (2.0/3.0) * (P_y-y0);
		P2_x = x3 - getBendiness() * (2.0/3.0) * (x3-P_x);
		P2_y = y3 - getBendiness() * (2.0/3.0) * (y3-P_y);
		
		/*
		CP1 = QP0 + 2/3 *(QP1-QP0)
		CP2 = CP1 + 1/3 *(QP2-QP0)
		*/
		
		/*
		System.out.println("r0: ("+x0+","+y0+")");
		System.out.println("diff1: "+(P_x-x0));
		System.out.println("Pq = ("+P_x + ", "+P_y+")");
		System.out.println("P1 = ("+P1_x + ", " + P1_y+ ")");
		System.out.println("P2 = ("+P2_x + ", " + P2_y+ ")");
		*/
		
		if (guideSlate!=null && guideSlate.getSelected().isSelected(this))
		{
			final Stroke OLD_STROKE = guideSlate.getStroke();
			guideSlate.setStroke(THIN_STROKE);
			
			if (getBendiness()==1.0)
			{
				guideSlate.drawLine(x0, y0, P_x, P_y, Resizeable.TRANSLUCENT_BLUE);
				guideSlate.plotDot(P_x, P_y, Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, false);
				guideSlate.drawLine(P_x, P_y, getFocus().getX(), getFocus().getY(), Resizeable.TRANSLUCENT_BLUE);
			}
			else
			{
				guideSlate.drawLine(x0, y0, P1_x, P1_y, Resizeable.TRANSLUCENT_BLUE);
				guideSlate.drawLine(P1_x, P1_y, P2_x, P2_y, Resizeable.TRANSLUCENT_BLUE);
				guideSlate.drawLine(P2_x, P2_y, getFocus().getX(), getFocus().getY(), Resizeable.TRANSLUCENT_BLUE);
				guideSlate.plotDot(P1_x, P1_y, Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, false);
				guideSlate.plotDot(P2_x, P2_y, Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, false);
			}
			
			guideSlate.setStroke(OLD_STROKE);
		}
		
		if (backwards!=Boolean.TRUE)
			generalPath.curveTo((float)P1_x, (float)P1_y, 
					(float)P2_x, (float)P2_y,
					(float)getFocus().getX(), (float)getFocus().getY());
		else
			generalPath.curveTo((float)P2_x, (float)P2_y, 
					(float)P1_x, (float)P1_y,
					(float)x0, (float)y0);
	}
	
	public int getBubbleCount()
	{
		if (type==BUBBLED_TYPE)
			return (int) (determineStemLength()/getBubblePeriod());
		else
			return 0;
	}
	
	public Object findForebear(Class requestedClass)
	{
		if (getParent()!=null && requestedClass.isInstance(getParent()))
			return getParent();
		else
		{
			if (!(getParent() instanceof Kid))
				return null;
			else
				return ((Kid)getParent()).findForebear(requestedClass);
		}
	}
	
	public Object findProgenitor()
	{
		Object generation = this;
		
		while (true)
		{
			if (generation instanceof Kid)
			{
				Kid generationAsKid = (Kid) generation;

				if (generationAsKid.getParent()==null)
					return generation;
				else
					generation = generationAsKid.getParent();
			}
			else
			{
				return generation;
			}
		}		
	}
	
	public abstract void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selected);
	
	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}
	
	public static double minAllowableRootWidthInPoints()
	{
		return 5.0;
	}
	
	public static double maxAllowableRootWidthInPoints()
	{
		return 30.0;
	}
	
	public static boolean wouldProspectiveRootWidthInPointsBeAllowed(double prospectiveValue)
	{
		if (prospectiveValue < minAllowableRootWidthInPoints() || prospectiveValue > maxAllowableRootWidthInPoints())
			return false;
		
		return true;
	}

	public void setName(String designatedName)
	{
		if (name != designatedName && (designatedName==null || !designatedName.equals(name)))
		{
			name = designatedName;
			SingleThreadedChangeSupport.fireChangeEvent(this, new NameChangeEvent(this));
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		if (name==null)
			return AbstractNamed.NAMES_TEXT.getString("stemLabel");
		else
			return name;
	}
	
	public static class Type
	{
		private String code;
		private boolean inline;
	
		private static Map indexByCode = new LinkedHashMap();
		
		private Type(String designatedCode, boolean designatedInline)
		{
			code = designatedCode;
			inline = designatedInline;
			indexByCode.put(designatedCode, this);
		}
		
		public String toString()
		{
			String i8ndName = determineInternationalizedName();
			
			if (i8ndName==null)
				return code;
			else
				return i8ndName;
		}

		public static Type find(String requestedCode)
		{
			if (!indexByCode.containsKey(requestedCode))
				return null;

			return (Type) indexByCode.get(requestedCode);
		}

		public Object readResolve() throws ObjectStreamException
		{
			return find(code);
		}

		public String getCode()
		{
			return code;
		}
		
		public static WidgetedTypesafeList getWidgetedIndex()
		{
			WidgetedTypesafeList wtl = new WidgetedTypesafeList(AbstractStem.Type.class);
			wtl.addAll(indexByCode.values());
			
			return wtl;
		}
		
		public String determineInternationalizedName()
		{
			String key = code + "StemTypeLabel";
			return AbstractNamed.NAMES_TEXT.getString(key);
		}
		
		public boolean isInline()
		{
			return inline;
		}
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type designatedType)
	{
		type = designatedType;
		
		// if (type==BUBBLED_TYPE && getBubbleCount()==0)
		// 	setBubbleCount(DEFAULT_BUBBLED_BUBBLE_COUNT);
	}
	
	public void draw(DrawingContext drawingContext)
	{
		if (getLeadingEdgeX()==0 && getLeadingEdgeY()==0
							&& getTrailingEdgeX()==0 && getTrailingEdgeY()==0)
		{
			return;
		}
		
		double rootX = (getLeadingEdgeX()+getTrailingEdgeX())/2.0;
		double rootY = (getLeadingEdgeY()+getTrailingEdgeY())/2.0;

		if (getType()==BUBBLED_TYPE)
		{
			GeneralPath shape = new GeneralPath();
			
			shape.moveTo((float)rootX, (float)rootY);
			kinkCurve(shape, rootX, rootY, getFocus().getX(), getFocus().getY(), null, 
				 		getRootStemInclinationInRadians(), getFocusStemInclinationInRadians(), drawingContext);
			final PathIterator pathIterator = shape.getPathIterator(null);
			final double [] coords = new double[6]; // we could probably cache this without too much risk
			
			while (!pathIterator.isDone() && pathIterator.currentSegment(coords)!=PathIterator.SEG_CUBICTO)
				pathIterator.next();
			
			double length = determineStemLength();
			// System.out.println("stem length="+length);
			double theta = Math.atan2(rootY-getFocus().getY(), rootX-getFocus().getX()); // TODO deal with vert lines

			double inching = getRootWidthInPoints() - DEFAULT_BUBBLE_HEIGHT;
			if (inching<0.0)
				inching = 0.0;
			
			int bubbleCount = getBubbleCount();

			boolean tall = onSide();
			double ruffleMargin = 0.0;
			
			if (getParent()!=null && getParent() instanceof PunctuatedSuperEllipse)
			{
				PunctuatedSuperEllipse dad = (PunctuatedSuperEllipse) getParent();
				RuffleableSuperEllipse ruffled = dad.getRuffledSuperEllipse();
				
				if (ruffled.isRuffled())
				{
					RuffleDie tallest = ruffled.getRuffles().findTallest();
					
					if (tallest!=null)
					{
						ruffleMargin = tallest.getHeightInPoints();
					}
				}
			}
			
			final Point2D point = new Point2D.Double();
			
			double progress = 0.0;
			double previousX=0.0, previousY=0.0;
			
			for (int bubbleLoop=0; 
					bubbleLoop<=bubbleCount-1; 
					bubbleLoop++)
			{
				progress = (double)bubbleLoop/(double)bubbleCount;
				// System.out.println("coords="+coords[0]+","+coords[1]+","+coords[2]+","+coords[3]+","+coords[4]+","+coords[5]);
				double bubbleWidth = (inching*progress) + DEFAULT_BUBBLE_HEIGHT;
				
				ShapeEvaluator.evaluateCubicBezier(1.0-progress, coords, rootX, rootY, point);

				// are we far enough from the last bubble and from the root to justify another one?
				
				if (progress==0.0 
						|| (PerimeterSegment.computeDistance(point.getX(), point.getY(), previousX, previousY) > DEFAULT_BUBBLE_HEIGHT
						&& (PerimeterSegment.computeDistance(point.getX(), point.getY(), rootX, rootY)) > ruffleMargin))
				{
					double ovalWidth = 0.0;
					double ovalHeight = 0.0;

					if (tall)
					{
						ovalWidth = DEFAULT_BUBBLE_HEIGHT;
						ovalHeight = bubbleWidth;
					}
					else
					{
						ovalWidth = bubbleWidth;
						ovalHeight = DEFAULT_BUBBLE_HEIGHT;
					}

					// System.out.println("adorn: ("+x+", "+y+")");

					Color outlineColor = Color.black;
					Color fillColor = Color.white;

					Colorful colorfulGrandad = (Colorful) findForebear(Colorful.class);

					if (colorfulGrandad!=null)
					{					
						outlineColor = colorfulGrandad.getOutlineColor();
						fillColor = colorfulGrandad.getFillColor();
					}

					// double bubbleRotation = getRootRotationInRadians() + Math.PI/2.0;

					drawingContext.drawFilledOval(point.getX()-ovalWidth/2.0, point.getY()-ovalHeight/2.0, ovalWidth, ovalHeight, outlineColor, fillColor);

					previousX = point.getX();
					previousY = point.getY();
				}
			}
		}
		else if (getType()==LOLLIPOP_TYPE)
		{
			drawingContext.setColor(Color.black); // TODO store this color somewhere
			
			if (!determineWhetherCurved())
			{
				drawingContext.drawLine((int)rootX, (int)rootY, (int)getFocus().getX(), (int)getFocus().getY());
			}
			else
			{
				final GeneralPath shape = new GeneralPath(); // TODO I like that this isn't cached. Change AbstractSuperEllipse to refrain, the same.
				
				shape.moveTo((float)rootX, (float)rootY);
				kinkCurve(shape, rootX, rootY, getFocus().getX(), getFocus().getY(), null, 
				 		getRootStemInclinationInRadians(), getFocusStemInclinationInRadians(), drawingContext);
				
				drawingContext.draw(shape); // TODO figure out how to decouple color from outline color.
			}
		}
			
		// drawingContext.plotDot(istem.getFocus().getX(), istem.getFocus().getY(), Color.green, 7, false);
		// drawingContext.plotDot(istem.getLeadingEdgeX(), istem.getLeadingEdgeY(), Color.orange, 7, false);
	}
	
	public double getAverageRootX()
	{
		return (getLeadingEdgeX()+getTrailingEdgeX()) / 2.0;
	}
	
	public double getAverageRootY()
	{
		return (getLeadingEdgeY()+getTrailingEdgeY()) / 2.0;
	}
	
	public double determineStemLength()
	{
		return PerimeterSegment.computeDistance(getAverageRootX(), getAverageRootY(),
				getFocus().getX(), getFocus().getY());
	}

	public double getBubblePeriod()
	{
		return bubblePeriod;
	}

	public void setBubblePeriod(double designatedBubblePeriod)
	{
		if (designatedBubblePeriod!=bubblePeriod)
		{
			if (designatedBubblePeriod<0.0)
				throw new IllegalArgumentException("bubble period must be positive: "+designatedBubblePeriod);
			
			// System.out.println("new bubble period: "+bubblePeriod);
			// System.out.println("\tcorresponding density: "+determineBubbleDensity());
			
			if (bubblePeriod != designatedBubblePeriod)
			{
				bubblePeriod = designatedBubblePeriod;			
				SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
			}
		}
	}
	
	public static WidgetedTypesafeList getWidgetedTypeIndex()
	{
		return Type.getWidgetedIndex();
	}
	
	public double determineBubbleDensity()
	{
		if (bubblePeriod==0.0)
			return 0.0;
		
		return 1.0/bubblePeriod;
	}
	
	public boolean isInline()
	{
		if (type==null)
			return false;
		
		return type.isInline();
	}

	public double getRootBubbleRotationInRadians()
	{
		return rootBubbleRotationInRadians;
	}
	
	public void setRootBubbleRotationInRadians(double designatedRootBubbleRotationInRadians) //TODO rename this method something more helpful?
	{
		if (rootBubbleRotationInRadians != designatedRootBubbleRotationInRadians)
		{
			rootBubbleRotationInRadians = designatedRootBubbleRotationInRadians;
			// don't fire a change event because this is really just a descriptive property?
		}
	}
	
	public double getRootStemInclinationInRadians()
	{
		return rootStemInclinationInRadians;
	}
	
	public void setRootStemInclinationInRadians(double designatedRootStemInclinationInRadians)
	{
		if (rootStemInclinationInRadians != designatedRootStemInclinationInRadians)
		{
			rootStemInclinationInRadians = designatedRootStemInclinationInRadians;
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	public double getFocusStemInclinationInRadians()
	{
		return focusStemInclinationInRadians;
	}

	public void setFocusStemInclinationInRadians(double designatedFocusStemInclinationInRadians)
	{
		if (focusStemInclinationInRadians != designatedFocusStemInclinationInRadians)
		{
			focusStemInclinationInRadians = designatedFocusStemInclinationInRadians;
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	/**
	 * A measure of how "bendy" the (curved) stem is. A setting of one results in a purely quadratic
	 * curve, which represents the maximum bendiness possible.
	 *
	 * If allowed, a setting of zero would result in a pure, uncurved line.
	 *
	 * This setting has no effect unless stem or root inclinations are also set.
	 */
	public double getBendiness()
	{
		return bendiness;
	}
	
	/**
	 * @throws IllegalArgumentException if the designated bendiness is not in the range zero (exclusive)
	 * thru 1 (inclusive)
	 */
	public void setBendiness(double designatedBendiness)
	{
		if (bendiness != designatedBendiness)
		{
			if (designatedBendiness<0.0)
				throw new IllegalArgumentException("Bendiness must be positive or zero: "+designatedBendiness);
			
			if (designatedBendiness>1.0)
				throw new IllegalArgumentException("Bendiness cannot exceed one: "+designatedBendiness);
			
			bendiness = designatedBendiness;
			// System.out.println("new bendiness: "+designatedBendiness);
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	public double determineRigidity()
	{
		return 1.0 - getBendiness();
	}
	
	/**
	 * Sets the bendiness equals to one minus the provided value. See {@link #setBendiness(double)}
	 */
	public void setRigidity(double designtatedRigidity)
	{
		setBendiness(1.0 - designtatedRigidity);
	}

	/**
	 * @return true, if the stem's root is deemed fixed to the left or right side of the balloon.
	 * false, if it is on the top or the bottom.
	 */
	public boolean onSide()
	{
		if (getRootBubbleRotationInRadians()<Math.PI/6.0 || getRootBubbleRotationInRadians()>11*Math.PI/6.0
				|| (getRootBubbleRotationInRadians()>5*Math.PI/6 && getRootBubbleRotationInRadians()<7*Math.PI/6))
		{
			return true;
		}
		
		return false;
	}

	public boolean determineWhetherCurved()
	{
		return (getRootBubbleRotationInRadians()!=0.0 || getFocusStemInclinationInRadians()!=0.0);
	}
}
