/*
Copyleft 2004 by Dave Horlick
*/

package com.smithandtinkers.geom;

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;


/**
 * Implements core support for super ellipses.
 *
 * If permitted, coordination of perimeter ruffling (if any) is delegated to subclasses.
 *
 * @author dhorlick
 */
public abstract class AbstractSuperEllipse extends AbstractRelocateable implements SuperEllipse, VisiblySelectable
{
	private static final int QUICK_BABY_STEPS = 20;
	private static final int DEFAULT_UNRUFFLED_UNSTEMMED_PARAMETERIZATION_STEPS = 25;
	
	private static final String IDENTIFIER_HEIN_PARAMETER = "hein-parameter";
	private static final String IDENTIFIER_RADIUS_X = "rx";
	private static final String IDENTIFIER_RADIUS_Y = "ry";
	
	private static final Stroke FAT_STROKE = new BasicStroke(7.0f);
	
	/**
	 * r
	 *  x
	 */
	private double semiMajorAxis = 1.0;
	
	/**
	 * r
	 *  y
	 */
	private double semiMinorAxis = 1.0;
	
	/**
	 * where 0 < n < 1 --> a rounded rectange
	 *		   n = 1 --> a traditional ellipse
	 *		   n > 1 --> increasingly diamondy ovals of no particular interest to comics letterers
	 */
	private double heinParameter = 0.6;
	
	private double arclength = -1.0;
	
	public final static double TWICE_PI = 2.0*Math.PI;
	
	public static final int HEIN_PARAMETER_TRADITIONAL_ELLIPSE = 1;
	
	public AbstractSuperEllipse()
	{
	}
	
	public AbstractSuperEllipse(double designatedSemiMajorAxis, double designatedSemiMinorAxis, double designatedHeinParameter)
	{
		setSemiMajorAxis(designatedSemiMajorAxis);
		setSemiMinorAxis(designatedSemiMinorAxis);
		setHeinParameter(designatedHeinParameter);
	}
	
	/**
	 * @return Returns the heinParameter.
	 */
	public double getHeinParameter()
	{
		return heinParameter;
	}
	
	/**
	 * @param designatedHeinParameter The heinParameter to set.
	 */
	public void setHeinParameter(double designatedHeinParameter)
	{
		if (!wouldProspectiveHeinParameterBeAllowed(designatedHeinParameter))
		{
			throw new IllegalArgumentException(String.valueOf(designatedHeinParameter));
		}
		
		double oldHeinParameter = heinParameter;
		
		heinParameter = designatedHeinParameter;
		
		if (oldHeinParameter!=heinParameter)
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	/**
	 * @return Returns the semiMajorAxis.
	 */
	public double getSemiMajorAxis()
	{
		return semiMajorAxis;
	}
	
	/**
	 * @param designatedSemiMajorAxis The semiMajorAxis to set.
	 */
	public void setSemiMajorAxis(double designatedSemiMajorAxis)
	{
		semiMajorAxis = designatedSemiMajorAxis;
	}
	
	/**
	 * @return Returns the semiMinorAxis.
	 */
	public double getSemiMinorAxis()
	{
		return semiMinorAxis;
	}
	
	/**
	 * @param designatedSemiMinorAxis The semiMinorAxis to set.
	 */
	public void setSemiMinorAxis(double designatedSemiMinorAxis)
	{
		this.semiMinorAxis = designatedSemiMinorAxis;
	}
	
	public double computeX(double angleInRadians)
	{
		double theCosine = Math.cos(angleInRadians);
		double raised = getSemiMajorAxis() * Math.pow(Math.abs(theCosine), getHeinParameter());
		
		if (theCosine < 0)
			return getLocation().getX() - raised;
		else
			return getLocation().getX() + raised;
	}
	
	public double computeY(double angleInRadians)
	{
		double theSine = Math.sin(angleInRadians);
		double raised = getSemiMinorAxis() * Math.pow(Math.abs(theSine), getHeinParameter());
		
		if (theSine < 0)
			return getLocation().getY() - raised;
		else
			return getLocation().getY() + raised;
	}
	
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return toShapeRoughlyEqualSegments(shapingContext, plottingContext);
	}
	
	private final GeneralPath shape = new GeneralPath();
	private final StemConstructionContext stemConstructionContext = new StemConstructionContext();
	
	public Shape toShapeRoughlyEqualSegments(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		// final long START = System.currentTimeMillis();
		// GeneralPath shape = new GeneralPath();
		// shape.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		shape.reset();
		
		float x = 0.0f, y=0.0f, lastX=0.0f, lastY=0.0f;
		
		// the term "last" here refers to the little steps within notches
		
		double lastU = 0.0;
		float lastTangentX = 0.0f;
		float lastTangentY = 0.0f;

		double bruteForcedArcLength = bruteForceArcLength();
		
		int requestedNotches = pickAppropriateNotchQuantity(bruteForcedArcLength);
		
		final double NORMAL_STEP = bruteForcedArcLength/requestedNotches;
		
		double arclengthToHere = 0.0;
		
		float P1_x = 0.0f, P1_y = 0.0f;
		
		// the term "previous" here refers to the big steps between notches
		
		double previousTheta = 0.0;
		
		if (isRuffled())
			previousTheta = Math.PI;
		else
			previousTheta = Math.PI / 2.0;
		
		float previousTangentX = 0f;
		float previousTangentY = 500.0f; // these are used only for ruffling
		
		stemConstructionContext.reset();
		
		int babyStepsToTake = 0;
		
		if (shapingContext!=null && shapingContext.isIntermediate())
			babyStepsToTake = QUICK_BABY_STEPS;
		else
		{
			if (getHeinParameter()<0.6)
			{			
				babyStepsToTake = (int)
						( -140 * Math.pow(getHeinParameter(),2.0)
						  -110 * getHeinParameter()
						  +390 );
			}
			else
			{
				babyStepsToTake = (int)
						( -250 * getHeinParameter()
						  +270 );
			}
		}

		if (babyStepsToTake < QUICK_BABY_STEPS)
			babyStepsToTake = QUICK_BABY_STEPS;
		
		// System.out.println("babyStepsToTake="+babyStepsToTake);
		
		final double INCREMENT = TWICE_PI/requestedNotches/babyStepsToTake;
		
		double u=0.0;
		int notchesStamped=0;
		
		for (int loop=0; u<=TWICE_PI; loop++)
		{
			u = loop * INCREMENT;
			
			x = (float)computeX(u);
			y = (float)computeY(u);
			
			if (!Float.isNaN(x) && !Float.isNaN(y))
			{
				float tangentX = calcTangentComponent(x, lastX, u, lastU);
				float tangentY = calcTangentComponent(y, lastY, u, lastU);
				
				if (u>0)
				{
					double babyStepDistance = PerimeterSegment.computeDistance(lastX, lastY, x, y);
					arclengthToHere += babyStepDistance;
					
					checkForStem(stemConstructionContext, shape, arclengthToHere, x, y, u);
					
					if (stemConstructionContext.getDrawingState()==StemConstructionContext.TRAILING_EDGE)
					{
						if (stampStem(stemConstructionContext, shape, plottingContext))
						{
							P1_x = x;
							P1_y = y;
						}
					}
					
					if ((stemConstructionContext.getStem()!=null && stemConstructionContext.getStem().isInline() && stemConstructionContext.getDrawingState()==StemConstructionContext.LEADING_EDGE) || 
							((stemConstructionContext.getDrawingState()!=StemConstructionContext.IN_STEM || (stemConstructionContext.getStem()!=null && !stemConstructionContext.getStem().isInline()) ) && notchesStamped<requestedNotches && (arclengthToHere >= computeNotchStart(notchesStamped, requestedNotches, bruteForcedArcLength) || u>=TWICE_PI)))
					{
						/*
						       1
						 P  = --- R  + P 
						  2    3   1    1
						  
						             1
						 P  =  P  - --- R
						  3     4    3   4
						  
						 derived from Foley, van Dam 11.24, page 489
						 */
						
						double theta = 0.0;
						
						if (!isRuffled())
						{
							theta = Math.atan2(tangentY, tangentX);
						}
						else
						{
							float normalX = calcTangentComponent(tangentX, lastTangentX, u, lastU);
							float normalY = calcTangentComponent(tangentY, lastTangentY, u, lastU);
							
							// TODO use  |  b |
							//           | -a | instead? might be faster
							
							theta = Math.atan2(normalY, normalX);
						}
						
						float R1_x = 0f;
						float R1_y = 0f;
						float R4_x = 0f;
						float R4_y = 0f;
						
						if (isRuffled())
						{
							// float magnificationMagnification = 1.25f;
							float magnificationMagnification = pickAppropriateRuffleMagnification(notchesStamped);
							
							float magnification = (float) (magnificationMagnification*NORMAL_STEP); // TODO change this
							
							R1_x = -1 * magnification * (float)Math.cos(previousTheta);
							R1_y = -1 * magnification * (float)Math.sin(previousTheta);
							R4_x = magnification * (float)Math.cos(theta);
							R4_y = magnification * (float)Math.sin(theta);
						}
						else
						{
							final float notchDistance = (float)PerimeterSegment.computeDistance(P1_x, P1_y, x, y);
							final float magnitude = 0.88f * notchDistance;
							
							R1_x = magnitude * (float)Math.cos(previousTheta);
							R1_y = magnitude * (float)Math.sin(previousTheta);
							R4_x = magnitude * (float)Math.cos(theta);
							R4_y = magnitude * (float)Math.sin(theta);
						}
						
						float p2_x =  P1_x + R1_x/3.0f;
						float p2_y =  P1_y + R1_y/3.0f;
						
						float p3_x =     x - R4_x/3.0f;
						float p3_y =     y - R4_y/3.0f;
						
						
						//if (u>0.2 && u<1.6)
						// if (isRuffled())
						// {
							// System.out.println("P1: " + shape.getCurrentPoint()+ ", P2: ("+p2_x+","+p2_y+"), P3: ("+p3_x+","+p3_y+"), P4: ("+x+","+y+") @ u="+u);
							// System.out.println("R1: ("+R1_x+","+R1_y+") @ u="+u);
							// System.out.println("R4: ("+R4_x+","+R4_y+") @ u="+u);
							
							// System.out.println("||R1||: "+PerimeterSegment.computeDistance(0.0,0.0,R1_x,R1_y)+" @ u="+u);
							// System.out.println("||R4||: "+PerimeterSegment.computeDistance(0.0,0.0,R4_x,R4_y)+" @ u="+u);
							
							// System.out.println(" o o o o o o o o");
						// }
						
						if ( !isRuffled() )
						{
							shape.curveTo(p2_x, p2_y, p3_x, p3_y, x, y);
						}
						else
						{
							float midX = (x+P1_x)/2.0f;
							float midY = (y+P1_y)/2.0f;
							
							// calc the unit normal
							
							// | r1 |   | 0 |   |  r2 |
							// | r2 | X | 0 | = | -r1 |
							// | 0  |   | 1 |   |  0  |
							
							
							float avgTangentX = 0.0f;
							float avgTangentY = 0.0f;
							
							avgTangentX = (tangentX+previousTangentX)/2.0f;
							avgTangentY = (tangentY+previousTangentY)/2.0f;
							
							float avgTangentMagnitude = (float)Math.sqrt(Math.pow(avgTangentX,2) + Math.pow(avgTangentY,2));
							float avgUnitNormalX = avgTangentY / avgTangentMagnitude;
							float avgUnitNormalY = - avgTangentX / avgTangentMagnitude;
							
							RuffleableSuperEllipse thisAsRuffled = (RuffleableSuperEllipse) this; // TODO rework inheritance to avoid casting acrobatics
							final float K = (float)thisAsRuffled.getHeight(notchesStamped);
							float apexX = midX + K*avgUnitNormalX;
							float apexY = midY + K*avgUnitNormalY;
							
							/* if (plottingContext!=null)
							{
							 	plottingContext.plotDot(apexX, apexY, Color.blue, 4, true);
							 	plottingContext.plotDot(midX, midY, Color.green, 4, true);
							 	plottingContext.plotDot(x, y, Color.yellow, 5, true);
							 	plottingContext.plotDot(lastX, lastY, Color.red, 3, true);
							} */
							
							if (((RuffleableSuperEllipse)this).getType()==RuffleableSuperEllipse.JAGGED_TYPE)
							{
								shape.lineTo(apexX, apexY);
								shape.lineTo(x, y);
							}
							else
							{
								// shape.quadTo(apexX, apexY, x, y);
								
								final Point2D circleCenter = findCenterOfCircle(P1_x, P1_y, apexX, apexY, x, y);
								double radius = PerimeterSegment.computeDistance(apexX, apexY, circleCenter.getX(), circleCenter.getY());
								// double radius2 = PerimeterSegment.computeDistance(P1_x, P1_y, circleCenter.getX(), circleCenter.getY());
								// double radius3 = PerimeterSegment.computeDistance(x, y, circleCenter.getX(), circleCenter.getY());
								
								// System.out.println("radii measures: "+radius1+" | "+radius2+" | "+radius3);
								// System.out.println("jeep: "+circleCenter);
								
								// TODO find start points using arctangent
								
								double startPhi = Math.atan2(P1_y-circleCenter.getY(), 
										P1_x-circleCenter.getX());							
								double endPhi = Math.atan2(y-circleCenter.getY(), 
										x-circleCenter.getX());
								
								// System.out.println("phi: "+startPhi+","+endPhi);
								
								if (endPhi<startPhi)
								{
									endPhi += TWICE_PI;
									// System.out.println("--> phi: "+startPhi+","+endPhi);
								}
								final double deltaPhi = endPhi-startPhi;

								final Arc2D arc = new Arc2D.Double();
								arc.setArcByCenter(circleCenter.getX(), circleCenter.getY(),
										radius, 
										360.0*(TWICE_PI-startPhi)/TWICE_PI,  
										-360.0*(deltaPhi)/TWICE_PI,
										Arc2D.OPEN);
								shape.append(arc, true);
								
								if (plottingContext!=null && plottingContext.getSelected()!=null 
									&& isSelected(plottingContext.getSelected())
									&& plottingContext.getSelected().getAuxiliarySelectedItems()!=null)
								{
									RuffleDie die = thisAsRuffled.getRuffleDie(notchesStamped);

									if (plottingContext.getSelected().getAuxiliarySelectedItems().contains(die))
									{					
										final Stroke ORIGINAL_STROKE = plottingContext.getStroke();
										plottingContext.setStroke(FAT_STROKE);
										plottingContext.drawFilled(arc, AbstractResizeable.TRANSLUCENT_BLUE, null);
										plottingContext.setStroke(ORIGINAL_STROKE);
									}
								}
							}
							
							previousTangentX = tangentX;
							previousTangentY = tangentY;
						}
						
						P1_x = x;
						P1_y = y;
						previousTheta = theta;
						notchesStamped++;
					}
				}
				else
				{
					shape.moveTo(x, y);
					P1_x = x;
					P1_y = y;
				}
				
				lastX = x;
				lastY = y;
				lastU = u;
				lastTangentX = tangentX;
				lastTangentY = tangentY;
			}
		}
		
		// System.out.println("END ---------");
		
		if (!Float.isNaN(x) && !Float.isNaN(y))
		{
			if (stemConstructionContext.getDrawingState()==StemConstructionContext.IN_STEM && stemConstructionContext.getStem()!=null)
			{
				stemConstructionContext.getStem().setTrailingEdgeX(x);
				stemConstructionContext.getStem().setTrailingEdgeY(y);
				
				stampStem(stemConstructionContext, shape, plottingContext);
			}
			// System.out.println("closing path: x="+x+", y="+y);
			shape.closePath();
		}
		
		setArclength(arclengthToHere);
		// System.out.println();
		// System.out.println("ase: steps="+DrawingContext.countSteps(shape));
		
		// final long DURATION = System.currentTimeMillis() - START;
		// System.out.println("took "+DURATION+" millis for "+babyStepsToTake+" baby steps");
		
		return shape;
	}
	
	protected float calcTangentComponent(float w, float lastW, double u, double lastU)
	{
		return (float) ( ( w - lastW ) / (u-lastU) );
	}
	
	public void draw(DrawingContext drawingContext)
	{
		drawingContext.draw(this);
	}

	/** 
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, org.w3c.dom.Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element superEllipseElement = doc.createElement(IDENTIFIER_SUPER_ELLIPSE);
		
		applyAttributesAndSubElements(doc, superEllipseElement);
		
		parent.appendChild(superEllipseElement);
	}
	
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		Element element = (Element) node;
		
		if (element.hasAttribute(IDENTIFIER_RADIUS_X))
			setSemiMajorAxis(Double.parseDouble(element.getAttribute(IDENTIFIER_RADIUS_X)));
		
		if (element.hasAttribute(IDENTIFIER_RADIUS_Y))
			setSemiMinorAxis(Double.parseDouble(element.getAttribute(IDENTIFIER_RADIUS_Y)));
		
		if (element.hasAttribute(IDENTIFIER_HEIN_PARAMETER))
			setHeinParameter(Double.parseDouble(element.getAttribute(IDENTIFIER_HEIN_PARAMETER)));
		
		extractAttributesAndSubElements(parent, element);
		
		// System.out.println("when all is said/done: "+this);
	}
	
	public void extractAttributesAndSubElements(Object parent, Element element)
	{
		super.extractAttributesAndSubElements(parent, element);
	}
	
	public void applyAttributesAndSubElements(Document doc, Element element)
	{
		super.applyAttributesAndSubElements(doc, element);
		element.setAttribute(IDENTIFIER_RADIUS_X, String.valueOf(semiMajorAxis));
		element.setAttribute(IDENTIFIER_RADIUS_Y, String.valueOf(semiMinorAxis));
		element.setAttribute(IDENTIFIER_HEIN_PARAMETER, String.valueOf(heinParameter));
	}
	
	public Rectangle2D getResizeableBounds2D()
	{
		return getPreferredBounds();
	}
	
	public boolean onPerimeter(Point thePoint)
	{
		return onPerimeter(thePoint.getX(), thePoint.getY());
	}
	
	public boolean onPerimeter(double pointX, double pointY)
	{
		pointX -= getLocation().getX();
		pointY -= getLocation().getY();
		
		double r = 2.0/heinParameter;
		double approxDistanceFromEdge = Math.abs( Math.pow(Math.abs(pointX/getSemiMajorAxis()), r) + Math.pow(Math.abs(pointY/getSemiMinorAxis()), r) - 1.0 );
		
		// System.out.println("approxDistanceFromEdge = "+approxDistanceFromEdge);
		
		return ( approxDistanceFromEdge < 0.08 );
	}
	
	private static boolean pointIsOnLine(double pointX, double pointY, int x1, int y1, int x2, int y2)
	{
		if (pointX < x1 || pointX > x2
				|| pointY < y1 || pointY > y2)
		{
			return false;
		}
		
		if (x1==x2)
		{
			return (pointX==x1);
			// to avoid the infinite slope problem
		}
		
		double slope = (double)(y2-y1) / (double)(x2-x1);
		
		return (x1 + slope*pointX == pointY);
	}

	public String toString()
	{
		StringBuffer desc = new StringBuffer();
		desc.append("SuperEllipse [semiMajorAxis=");
		desc.append(getSemiMajorAxis());
		desc.append(", semiMinorAxis=");
		desc.append(getSemiMinorAxis());
		desc.append(", heinParameter=");
		desc.append(getHeinParameter());
		
		if (location!=null)
		{
			desc.append(",x=");
			desc.append(location.getX());
			desc.append(",y=");
			desc.append(location.getY());
		}
		
		desc.append(" ]");
		
		return desc.toString();
	}
	
	public double bruteForceArcLength()
	{
		return bruteForceArcLength(162);
	}
	
	public double bruteForceArcLength(final int STEPS)
	{
		double x = 0, y=0, lastX=0, lastY=0;
		double arcLength = 0.0;
		
		for (double angle=0; angle<=TWICE_PI; angle+=TWICE_PI/STEPS)
		{
			x = computeX(angle);
			y = computeY(angle);
							
			if (angle>0)
			{
				arcLength += Math.sqrt( Math.pow(lastX-x,2) + Math.pow(lastY-y,2));
			}
			
			lastX = x;
			lastY = y;
			
			// System.out.println("loop="+angle);
		}
		
		return arcLength;
	}
	
	/**
	 * Attempts to change the hein parameter to fit the new coordinates, then calls toShape.
	 * 
	 * @see com.smithandtinkers.geom.Shapeable#reshape(double, double, double, double)
	 */
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		// System.out.println("unmapped new point = ("+newX+","+newY+")");
		
		double mappedX = newX - getLocation().getX(); // TODO use mapOustideX() instead
		double mappedY = newY - getLocation().getY();
		
		// System.out.println("\tmapped new point = ("+mappedX+","+mappedY+")");
		double distance = Math.sqrt ( (mappedX*mappedX) + (mappedY*mappedY) ) ;
		// System.out.println("\tdistance = " + babyStepDistance);
		
		double t2 = Math.atan2(mappedY, mappedX);
		
		//System.out.println("\tangle="+t2/TWICE_PI*360.0+" degrees");
		//System.out.println("back to point = (" + babyStepDistance*Math.cos(t2) + "," + babyStepDistance*Math.sin(t2) + ")");
		//System.out.println("\tr(angle) = (" + computeX(t2) +","+ computeY(t2) + ")");
		
		double mappedXForEllipse = getSemiMajorAxis() * Math.cos(t2);
		double mappedYForEllipse = getSemiMinorAxis() * Math.sin(t2);
		double distanceForEllipse = Math.sqrt ( (mappedXForEllipse*mappedXForEllipse) + (mappedYForEllipse*mappedYForEllipse) ) ;
		
		double distanceForRectangle;
		
		if (t2 <= Math.PI/4.0 && t2 >= Math.PI/-4.0)
		{
			distanceForRectangle = getSemiMajorAxis() / Math.cos(t2); 
		}
		else
		{
			distanceForRectangle = getSemiMinorAxis() / Math.abs(Math.sin(t2));
		}
		
		
		double newN;
		
		newN = (distanceForRectangle-distance) / (distanceForRectangle-distanceForEllipse);
			
		if (wouldProspectiveHeinParameterBeAllowed(newN))
			setHeinParameter(newN);
		
	}

	/**
	 * @see com.smithandtinkers.layout.AbstractResizeable#resizeBoundsTo(java.awt.geom.Rectangle2D)
	 */
	public void resizeBoundsTo(Rectangle2D rect)
	{
		// TODO this should behave differently from resizePreferredBoundsTo
		
		setLocation(new Point2D.Double(rect.getCenterX(), rect.getCenterY()));
		setSemiMajorAxis(rect.getWidth()/2.0);
		setSemiMinorAxis(rect.getHeight()/2.0);
		
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}

	public void resizePreferredBoundsTo(Rectangle2D rect)
	{
		setLocation(new Point2D.Double(rect.getCenterX(), rect.getCenterY()));
		setSemiMajorAxis(rect.getWidth()/2.0);
		setSemiMinorAxis(rect.getHeight()/2.0);
		
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setHeight(double designatedWidth)
	{
		setSemiMinorAxis(designatedWidth/2.0);
		
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	public void setWidth(double designatedHeight)
	{
		setSemiMajorAxis(designatedHeight/2.0);
		
		SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		if (designatedSide==PerimeterSegment.TOP_SIDE)
		{			
			// superEllipse.setSemiMinorAxis(superEllipse.getSemiMinorAxis() + (preferredBounds.getY() - mapOutsideY(designatedCoordinate)) );
			setSemiMinorAxis(getSemiMinorAxis() + (oldY - newY)/2.0);
			setLocation(new Point2D.Double(getLocation().getX(), getLocation().getY() - (oldY-newY)/2.0));
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_SIDE)
		{
			setSemiMinorAxis(getSemiMinorAxis() - (oldY - newY)/2.0);
			setLocation(new Point2D.Double(getLocation().getX(), getLocation().getY() - (oldY-newY)/2.0));
		}
		else if (designatedSide==PerimeterSegment.LEFT_SIDE)
		{
			setSemiMajorAxis(getSemiMajorAxis() + (oldX - newX)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY()));
		}
		else if (designatedSide==PerimeterSegment.RIGHT_SIDE)
		{
			setSemiMajorAxis(getSemiMajorAxis() - (oldX - newX)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY()));
		}
		else if (designatedSide==PerimeterSegment.TOP_LEFT_CORNER)
		{			
			setSemiMinorAxis(getSemiMinorAxis() + (oldY - newY)/2.0);
			setSemiMajorAxis(getSemiMajorAxis() + (oldX - newX)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY() - (oldY-newY)/2.0));
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_LEFT_CORNER)
		{
			setSemiMajorAxis(getSemiMajorAxis() + (oldX - newX)/2.0);
			setSemiMinorAxis(getSemiMinorAxis() - (oldY - newY)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY() - (oldY-newY)/2.0));
		}
		else if (designatedSide==PerimeterSegment.BOTTOM_RIGHT_CORNER)
		{
			setSemiMinorAxis(getSemiMinorAxis() - (oldY - newY)/2.0);
			setSemiMajorAxis(getSemiMajorAxis() - (oldX - newX)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY() - (oldY-newY)/2.0 ));
		}
		else if (designatedSide==PerimeterSegment.TOP_RIGHT_CORNER)
		{
			setSemiMajorAxis(getSemiMajorAxis() - (oldX - newX)/2.0);
			setSemiMinorAxis(getSemiMinorAxis() + (oldY - newY)/2.0);
			setLocation(new Point2D.Double(getLocation().getX() - (oldX-newX)/2.0, getLocation().getY() - (oldY-newY)/2.0));
		}
		
		return true;
	}
	
	/**
	 * Fast but probably wrong.
	 */
	public double guessArcLength()
	{
		// System.out.println("*** GUESSING ARCLENGTH ***");
		
		return ( 4.0 - heinParameter * ( 4.0 - Math.PI ) ) * (getSemiMajorAxis() + getSemiMinorAxis());
	}
	
	/**
	 * Only available after invoking {@link #toShape}.
	 */
	public double getArclength()
	{
		if (arclength<=0)
			setArclength(bruteForceArcLength());
			
		return arclength;
	}
	
	protected void setArclength(double designatedArclength)
	{
		arclength = designatedArclength;
	}
	
	final private Rectangle2D preferredBounds = new Rectangle2D.Double(); // TODO secure this
	
	public Rectangle2D getPreferredBounds()
	{
		preferredBounds.setRect(getLocation().getX()-getSemiMajorAxis(), getLocation().getY()-getSemiMinorAxis(), 2.0 * getSemiMajorAxis(), 2.0 * getSemiMinorAxis());
		
		return preferredBounds;
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getWidth()
	 */
	public double getWidth()
	{
		return 2.0 * getSemiMajorAxis();
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#getHeight()
	 */
	public double getHeight()
	{
		return 2.0 * getSemiMinorAxis();
	}
	
	public void paintSelection(DrawingContext drawingContext)
	{
		drawingContext.plotCornerDots(getResizeableBounds2D(), Resizeable.TRANSLUCENT_BLUE, Resizeable.DOT_DIAMETER, true);
	}

	public void drawAdornments(DrawingContext drawingContext, Selection selected)
	{
	}
	
	public static double maxAllowableHeinParameter()
	{
		return 1.0; // unfortunately, Java won't allow class-level methods to be virtual.
	}
	
	public static double minAllowableHeinParameter()
	{
		return 0.50; // unfortunately, Java won't allow class-level methods to be virtual.
	}
	
	/**
	 * This method would be static if Java permitted virtual class methods.
	 */
	public static boolean wouldProspectiveHeinParameterBeAllowed(double designatedPropsectiveHeinParameter)
	{
		if (designatedPropsectiveHeinParameter>maxAllowableHeinParameter())
			return false;
		
		if (designatedPropsectiveHeinParameter<minAllowableHeinParameter())
			return false;
		
		return true;
	}
	
	public boolean isSelected(Selection selection)
	{
		if (selection==null)
			return false;
		if (selection.isSelected(this))
			return true;
		
		for (int loop=0; loop<=selection.getSelectedItemsCount()-1; loop++)
		{
			if (selection.getSelectedItem(loop)==this)
				return true;
			
			if (selection.getSelectedItem(loop) instanceof SuperEllipsePerch)
			{
				SuperEllipsePerch sep = (SuperEllipsePerch)selection.getSelectedItem(loop);
				
				if (sep.getPunctedSuperEllipse().isEngine(this))
					return true;
			}
		}
		
		return false;
	}
	
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selected)
	{
	}
	
	public int pickAppropriateNotchQuantity(double designatedArcLength)
	{
		if (isRuffled())
			return determineRuffleQuantity(designatedArcLength);
		else
			return DEFAULT_UNRUFFLED_UNSTEMMED_PARAMETERIZATION_STEPS;
	}
	
	/**
	 * @param u the rotation at this point. This won't be used to determine the return
	 *        value, but might be kept for reference.
	 *
	 * @param designatedStemConstructionContext a characterization of where we are in the
	 * draw loop with respect to stems
	 */
	protected void checkForStem(StemConstructionContext designatedStemConstructionContext,
			GeneralPath generalPath, double arclength, float x, float y, double u)
	{
		designatedStemConstructionContext.setDrawingState(StemConstructionContext.ON_PERIMETER);
	}
	
	/**
	 * Draws the stem, if appropriate.
	 *
	 * This needs to get called even if there is no stamp to draw, so that the stem search can
	 * be reset.
	 * 
	 * @return true, if something was drawn
	 */
	protected boolean stampStem(StemConstructionContext designatedStemConstructionContext,
			GeneralPath generalPath, PlottingContext guideSlate)
	{
		return false;
	}
	
	public double computeNotchStart(int notchIndex, int totalNotches, double totalArcLength)
	{
		return (notchIndex+1)*totalArcLength/totalNotches;
	}
	
	public static Point2D findCenterOfCircle(double x1, double y1, double x2, double y2, double x3, double y3)
	{
		// return findCenterOfCircleMcrae(x1,y1,x2,y2,x3,y3);
		return findCenterOfCircleWolfram(x1,y1,x2,y2,x3,y3);
	}
	
	public static double determinate3(
			double a, double b, double c,
			double d, double e, double f,
			double g, double h, double i)
	{
		return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
	}
	
	public static Point2D findCenterOfCircleWolfram(double x1, double y1, double x2, double y2, double x3, double y3)
	{
		final double a = determinate3(
				x1, y1, 1,
				x2, y2, 1, 
				x3, y3, 1);
		final double d = -1.0 * determinate3(
				Math.pow(x1,2.0)+Math.pow(y1,2.0), y1, 1,
				Math.pow(x2,2.0)+Math.pow(y2,2.0), y2, 1,
				Math.pow(x3,2.0)+Math.pow(y3,2.0), y3, 1);
		final double e = determinate3(
				Math.pow(x1,2.0)+Math.pow(y1,2.0), x1, 1,
				Math.pow(x2,2.0)+Math.pow(y2,2.0), x2, 1,
				Math.pow(x3,2.0)+Math.pow(y3,2.0), x3, 1);
		final double f = -1.0 * determinate3(
				Math.pow(x1,2.0)+Math.pow(y1,2.0), x1, y1,
				Math.pow(x2,2.0)+Math.pow(y2,2.0), x2, y2,
				Math.pow(x3,2.0)+Math.pow(y3,2.0), x3, y3);
		
		final double x = -d/(2*a);
		final double y = -e/(2*a);
		
		return new Point2D.Double(x,y);
	}	
	
	public abstract boolean isRuffled();
	
	public abstract int determineRuffleQuantity(double designatedArcLength);
	
	public abstract float pickAppropriateRuffleMagnification(int index); // TODO eliminate any mention of ruffledness from this, the abstract base class
}
