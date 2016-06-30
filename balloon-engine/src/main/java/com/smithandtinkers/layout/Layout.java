/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.layout.edit.RebuildSillEdit;
import com.smithandtinkers.layout.edit.Translate2dEdit;
import com.smithandtinkers.layout.edit.TrimExcessItemsEdit;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.CompoundEdit;

import org.w3c.dom.*;

import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.edit.MysteryAddEdit;
import com.smithandtinkers.mvc.ChangeIssuer;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.*;


/**
 * An arrangement of {@link Sill}s that can be arranged so as to fill an area optimally subject
 * to specified constraints.
 *
 * @author dhorlick
 */
public class Layout implements Serializable, Saveable, ChangeIssuer, PubliclyCloneable, PanelLayoutPolicy
{
	private static final String IDENTIFIER_HEIGHT = "height";
	private static final String IDENTIFIER_WIDTH = "width";
	private static final String IDENTIFIER_Y = "y";
	private static final String IDENTIFIER_X = "x";
	public static final String IDENTIFIER_LAYOUT = "layout";
	
	private static final String IDENTIFIER_VMARGIN = "vmargin";
	private static final String IDENTIFIER_HMARGIN = "hmargin";
	private static final String IDENTIFIER_APERTURES = "apertures";
	private static final String IDENTIFIER_ASPECT_RATIO = "aspect-ratio";
	private static final String IDENTIFIER_VGAP = "vgap";
	private static final String IDENTIFIER_HGAP = "hgap";
	private static final String IDENTIFIER_NAME = "name";
	
	private static final String IDENTIFIER_POLICY = "policy";
		private static final String IDENTIFIER_POLICY_AUTOMATIC = "automatic";
		private static final String IDENTIFIER_POLICY_MANUAL = "manual";
	
	private double verticalGapInPoints = 10.0;
	private double horizontalGapInPoints = 10.0;
	private double aspectRatio = 1.0;
	private Rectangle bounds = new Rectangle(); // TODO use Rectangle2D.Double instead
	private int apertureQuantity;
	
	private double verticalPageMarginInPoints = 40.0;
	private double horizontalPageMarginInPoints = 40.0;
	
	private String name;
	private boolean manualLayoutPolicy;
	
	private SingleThreadedChangeSupport changeSupport = new SingleThreadedChangeSupport();
	private ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public Layout()
	{
	}
	
	public void setVerticalGapInPoints(double designatedGap)
	{
		if (verticalGapInPoints!=designatedGap)
		{
			verticalGapInPoints = designatedGap;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public void setHorizontalGapInPoints(double designatedGap)
	{
		if (horizontalGapInPoints!=designatedGap)
		{
			horizontalGapInPoints = designatedGap;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public void setGapInPoints(double designatedGap)
	{
		setVerticalGapInPoints(designatedGap);
		setHorizontalGapInPoints(designatedGap);
	}
	
	public double getVerticalGapInPoints()
	{
		return verticalGapInPoints;
	}
	
	public double getHorizontalGapInPoints()
	{
		return horizontalGapInPoints;
	}
	
	/**
	 * @param designatedBounds a Rectangle with an upper-left corner located at the origin.
	 */
	public void setBounds(Rectangle designatedBounds)
	{
		if (bounds!=designatedBounds)
		{
			if (designatedBounds.getX()!=0 || designatedBounds.getY()!=0)
				throw new IllegalArgumentException("Upper-lefthand corner must be at origin: "+designatedBounds);
			
			bounds = designatedBounds;
			// System.out.println("Set bounds: "+bounds);
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public void setApertureQuantity(int designatedApertureQuantity)
	{
		if (apertureQuantity!=designatedApertureQuantity)
		{
			apertureQuantity = designatedApertureQuantity;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public Sill toSill()
	{
		Sill sill = new Sill();
		syncSill(sill);
		return sill;
	}
	
	/**
	 * @param sill The sill to be sycn'd.
	 */
	public CompoundEdit syncSill(Sill sill)
	{
		if (sill.getName()==null)
			sill.setName(AbstractNamed.NAMES_TEXT.getString("pageLabel"));
		
		SillReconstruction sillReconstruction = new SillReconstruction();
		CompoundEdit compoundEdit = new CompoundEdit();

		if (isManual())
		{
			compoundEdit.end();
				// i.e. do nothing
			
			return compoundEdit;
		}
		
		double biggest = 0;
		
		Logger.println("bounds = "+bounds);
		
		Logger.println("Checking for valid N_x, N_y...");
		
		final int initialApertureQuantity = apertureQuantity;
		
		for (int N_y=1; N_y<=initialApertureQuantity; N_y++)
		{
			for (int N_x=1; N_x<=initialApertureQuantity; N_x++)
			{
				double Hprime = determineHprime(N_x, N_y);
				
				// Logger.println("trying Hprime=" +Hprime);
				
				if (inequalitiesHold(Hprime, N_x, N_y))
				{
					Logger.println("Solution! HPrime = " + Hprime + ", N_x = " + N_x + ", N_y = " + N_y);
					
					double h = determineApertureHeight(Hprime, N_y);
					double w = h*aspectRatio;
					
					Logger.println("(w, h) = ("+w+", "+h+")");
					Logger.println("resultant total width = " + (horizontalGapInPoints*(N_x-1) + w*N_x));
					
					double area = h*w;
					
					if (area>biggest)
					{
						// TODO clear out compound edit here? when I try, it kills Undo.
						int loop=0;
						
						for (int y=1; y<=N_y; y++)
						{
							for (int x=1; (x<=N_x) && ((y-1)*N_x+x<=initialApertureQuantity) ; x++)
							{					
								Sill panelSill = null;
								fillIn(loop, sill, compoundEdit);
								
								if (sill.get(loop) instanceof Sill)
								{
									panelSill = (Sill) sill.get(loop);
									reshape(x, y, w, h, panelSill, sillReconstruction);
								}
								
								loop++;
							}
						}
						
						trimFat(sill, compoundEdit);
						
						biggest = area;
					}
				}
			}
		}
		
		// System.out.println("sillReconstruction="+sillReconstruction);
		
		boolean initialResult = biggest>0;
		
		Logger.print("initialResult=");
		Logger.println(initialResult);
		
		if (!initialResult && initialApertureQuantity > 0)
		{
			// trivial case... center panels horizontally in a single row
			
			double h = bounds.getHeight();
			double w = h * getAspectRatio();
			final double rowW 
					= w * (initialApertureQuantity) 
					+ (initialApertureQuantity-1) * getHorizontalGapInPoints();
			
			biggest = w * h;
			// reshape(1, 1, w, h, (Sill)sill.get(0), sillReconstruction);
			
			fillIn(initialApertureQuantity-1, sill, compoundEdit); // too much?
			
			for (int x = 1; x <= initialApertureQuantity; x++)
			{
				final Sill panelSill = (Sill)sill.get(x-1);
				AperturePerch ap = new AperturePerch(panelSill);

				Rectangle2D shape = new Rectangle2D.Double();
				shape.setRect(0.0, 0.0, round(w,3), round(h,3));
				ap.setShape(shape);

				Point2D.Double point = new Point2D.Double();
				point.setLocation( round((bounds.getWidth()-rowW)/2.0 + (x-1)*(w+horizontalGapInPoints),3), 0.0);

				RebuildSillEdit rebuildSillEdit = new RebuildSillEdit(panelSill, point, ap);
				sillReconstruction.update(rebuildSillEdit);

				rebuildSillEdit.execute();
			}
								
			trimFat(sill, compoundEdit);
		}	
		
		sillReconstruction.addAllTo(compoundEdit);
		Translate2dEdit relocateEdit = new Translate2dEdit(sill.getContentOrigin().getX(), sill.getContentOrigin().getY(),
				horizontalPageMarginInPoints, verticalPageMarginInPoints);
		relocateEdit.add(sill);
		
		if (relocateEdit.execute())
			compoundEdit.addEdit(relocateEdit);
		
		compoundEdit.end();
		
		return compoundEdit;
	}

	/**
	 * This is called as part of {@link #syncSill} to reshape a constituent panel.
	 */
	private void reshape(final int x, final int y, final double w, final double h, 
			final Sill panelSill, final SillReconstruction sillReconstruction)
	{
		AperturePerch ap = new AperturePerch(panelSill);
		ap.setBorderWidthInPoints(
				BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultLineThicknessInPoints());
		
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setRect(0.0, 0.0, round(w,3), round(h,3));
		ap.setShape(shape);

		Point2D.Double point = new Point2D.Double();
		point.setLocation( round((x-1)*(w+horizontalGapInPoints),3),
				round((y-1)*(h+verticalGapInPoints),3) );

		RebuildSillEdit rebuildSillEdit = new RebuildSillEdit(panelSill, point, ap);
		sillReconstruction.update(rebuildSillEdit);

		rebuildSillEdit.execute();
		
		// compoundEdit.addEdit(rebuildSillEdit);
	}

	/**
	 * This is called as part of {@link #syncSill} to add needed panels undoably.
	 */
	private void fillIn(final int loop, final Sill sill, final CompoundEdit compoundEdit)
	{
		if (loop>sill.size()-1)
		{
			Sill addition = new Sill();
			addition.setName(AbstractNamed.NAMES_TEXT.getString("panelLabel")+" "+(loop+1));
			
			MysteryAddEdit createEdit = new MysteryAddEdit(sill, addition);
			createEdit.execute();
			compoundEdit.addEdit(createEdit);
		}
	}

	/**
	 * This is called as part of {@link #syncSill} to remove unneeded panels undoably.
	 */
	private void trimFat(final Sill sill, final CompoundEdit compoundEdit)
	{		
		if (sill.size()>=apertureQuantity) // TODO move this outside optimization loop
		{
			TrimExcessItemsEdit trimEdit = new TrimExcessItemsEdit(sill, apertureQuantity);
			trimEdit.execute();
			compoundEdit.addEdit(trimEdit);
		}
	}
	
	private boolean inequalitiesHold(double Hprime, int N_x, int N_y)
	{
		// Logger.println("checking HPrime = " + Hprime + ", N_x = " + N_x + ", N_y = " + N_y);
		
		if (Hprime > bounds.height)
		{
			// Logger.println("\tfailed Hprime > bounds.height");
			return false;
		}
		
		double h = determineApertureHeight(Hprime, N_y);
		/*
		// This doesn't seem to be very constructive.
		
		if (bounds.height >= verticalGapInPoints*(N_y + 2) + h*(N_y + 1))
		{
			Logger.println("\tfailed bounds.height >= verticalGapInPoints*(N_y + 2) + h*(N_y + 1)");
			return false;
		}
		*/
		
		if (apertureQuantity <= N_x*N_y - N_x)
		{
			// Logger.println("\tfailed apertureQuantity <= N_x*N_y - N_x");
			return false;
		}
		
		if (apertureQuantity > N_x*N_y)
		{
			// Logger.println("\tfailed apertureQuantity > N_x*N_y");
			return false;
		}
		
		return true;
	}
	
	private double determineHprime(int N_x, int N_y)
	{
		return (verticalGapInPoints*N_y*aspectRatio*N_x + bounds.width*N_y + horizontalGapInPoints*N_y - horizontalGapInPoints*N_x*N_y - verticalGapInPoints*aspectRatio*N_x)
				/ (aspectRatio * N_x);
		
		/*
		double N_x_double = (double) N_x;
		double N_y_double = (double) N_y;
		double bounds_width_double = (double) bounds.width;
		double bounds_height_double = (double) bounds.height;
		
		return (verticalGapInPoints*N_y_double*aspectRatio*N_x_double + bounds_width_double*N_y_double + horizontalGapInPoints*N_y_double - horizontalGapInPoints*N_x*N_y - verticalGapInPoints*aspectRatio*N_x_double)
				/ (aspectRatio * N_x_double);

		*/	
			// casting the int params to double doesn't appear to affect the result any 
	}
	
	public void setAspectRatio(double designatedAspectRatio)
	{
		if (designatedAspectRatio!=aspectRatio)
		{
			aspectRatio = designatedAspectRatio;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	private double determineApertureHeight(double Hprime, int N_y)
	{
		return (Hprime + verticalGapInPoints*(1 - N_y)) / N_y;
	}
	
	public void setHorizontalPageMarginInPoints(double designatedMargin)
	{
		if (horizontalPageMarginInPoints != designatedMargin)
		{
			validateNonNegative(designatedMargin);
		
			horizontalPageMarginInPoints = designatedMargin;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public void setVerticalPageMarginInPoints(double designatedMargin)
	{
		if (designatedMargin!=verticalPageMarginInPoints)
		{
			validateNonNegative(designatedMargin);
		
			verticalPageMarginInPoints = designatedMargin;
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public Dimension enclose()
	{
		final double w = bounds.width + 2*horizontalPageMarginInPoints;
		final double h = bounds.height + 2*verticalPageMarginInPoints;
		Dimension enclosure = new Dimension((int)w, (int)h);
		
		enclosure.setSize(w, h);
			// ^^ Annoyingly, this doesn't do anything in Java 1.3 
		
		return enclosure;
	}
	
	public void validateNonNegative(double amount)
	{
		if (amount<0.0)
		{
			StringBuffer problem = new StringBuffer(100);
			problem.append("Quantities must be positive. The designated value of ");
			problem.append(amount);
			problem.append(" is not valid");
			
			throw new IllegalArgumentException(problem.toString());
		}
	}
	
	public Rectangle getBounds() // TODO make defensive copy?
	{
		return bounds;
	}
	
	public double getHorizontalPageMarginInPoints()
	{
		return horizontalPageMarginInPoints;
	}
	
	public double getVerticalPageMarginInPoints()
	{
		return verticalPageMarginInPoints;
	}
	
	/**
	 @param size desired width/height of the crosshair
	 */
	public static void drawCrossHairs(Graphics g, int x, int y, int size)
	{
		Color oldColor = g.getColor();
		// g.setColor(Color.blue);
		
		int halfSize = size / 2;
		g.drawLine(x-halfSize, y, x+halfSize, y);
		g.drawLine(x, y-halfSize, x, y+halfSize);
		
		g.setColor(oldColor);
	}

	/**
	 @param size desired side length of the square
	 */
	public static void drawHandle(Graphics g, int x, int y, int size)
	{
		Color oldColor = g.getColor();
		g.setColor(Color.white);
		int halfSize = size / 2;
		
		g.fillRect(x-halfSize, y-halfSize, size, size);
		
		g.setColor(oldColor);
		g.drawRect(x-halfSize, y-halfSize, size, size);		
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element layoutElement = doc.createElement(IDENTIFIER_LAYOUT);
		layoutElement.setAttribute(IDENTIFIER_HGAP, String.valueOf(horizontalGapInPoints));
		layoutElement.setAttribute(IDENTIFIER_VGAP, String.valueOf(verticalGapInPoints));
		layoutElement.setAttribute(IDENTIFIER_ASPECT_RATIO, String.valueOf(aspectRatio));
		layoutElement.setAttribute(IDENTIFIER_APERTURES, String.valueOf(apertureQuantity));
		
		layoutElement.setAttribute(IDENTIFIER_HMARGIN, String.valueOf(horizontalPageMarginInPoints));
		layoutElement.setAttribute(IDENTIFIER_VMARGIN, String.valueOf(verticalPageMarginInPoints));
		
		if (bounds!=null)
		{
			layoutElement.setAttribute(IDENTIFIER_X, String.valueOf(bounds.x));
			layoutElement.setAttribute(IDENTIFIER_Y, String.valueOf(bounds.y));
			layoutElement.setAttribute(IDENTIFIER_WIDTH, String.valueOf(bounds.width));
			layoutElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(bounds.height));
		}
		
		if (name!=null)
			layoutElement.setAttribute(IDENTIFIER_NAME, name);
		
		if (isManual())
			layoutElement.setAttribute(IDENTIFIER_POLICY, IDENTIFIER_POLICY_MANUAL);
		else
			layoutElement.setAttribute(IDENTIFIER_POLICY, IDENTIFIER_POLICY_AUTOMATIC);
		
		parent.appendChild(layoutElement);
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#open(java.lang.Object, Node, ArchiveContext)
	 */
	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		// System.out.println("Layout.open: hi");
		
		Element element = (Element) node;
		
		if (element.hasAttribute(IDENTIFIER_HGAP))
			setHorizontalGapInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_HGAP)));
		
		if (element.hasAttribute(IDENTIFIER_VGAP))
			setVerticalGapInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_VGAP)));
		
		if (element.hasAttribute(IDENTIFIER_ASPECT_RATIO))
			setAspectRatio(Double.parseDouble(element.getAttribute(IDENTIFIER_ASPECT_RATIO)));
		
		if (element.hasAttribute(IDENTIFIER_APERTURES))
			setApertureQuantity(Integer.parseInt(element.getAttribute(IDENTIFIER_APERTURES)));
		
		if (element.hasAttribute(IDENTIFIER_HMARGIN))
			setHorizontalPageMarginInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_HMARGIN)));
		
		if (element.hasAttribute(IDENTIFIER_VMARGIN))
			setVerticalPageMarginInPoints(Double.parseDouble(element.getAttribute(IDENTIFIER_VMARGIN)));
		
		if (element.hasAttribute(IDENTIFIER_X) && element.hasAttribute(IDENTIFIER_Y) && element.hasAttribute(IDENTIFIER_WIDTH) && element.hasAttribute(IDENTIFIER_HEIGHT))
		{
			if (bounds==null)
				bounds = new Rectangle();
			
			bounds.x = Integer.parseInt(element.getAttribute(IDENTIFIER_X));
			bounds.y = Integer.parseInt(element.getAttribute(IDENTIFIER_Y));
			bounds.width = Integer.parseInt(element.getAttribute(IDENTIFIER_WIDTH));
			bounds.height = Integer.parseInt(element.getAttribute(IDENTIFIER_HEIGHT));
		}
		
		if (element.hasAttribute(IDENTIFIER_NAME))
			setName(element.getAttribute(IDENTIFIER_NAME));
		
		if (element.hasAttribute(IDENTIFIER_POLICY))
		{
			if (IDENTIFIER_POLICY_MANUAL.equalsIgnoreCase(element.getAttribute(IDENTIFIER_POLICY)))
				setManual(true);
			else if (IDENTIFIER_POLICY_AUTOMATIC.equalsIgnoreCase(element.getAttribute(IDENTIFIER_POLICY)))
				setManual(false);
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the ratio of the width to the height
	 */
	public double getAspectRatio()
	{
		return aspectRatio;
	}
	
	public int getApertureQuantity()
	{
		return apertureQuantity;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Layout {"
			 + "verticalGapInPoints = " + verticalGapInPoints + ", "
			 + "horizontalGapInPoints = " + horizontalGapInPoints + ", "
			 + "aspectRatio = " + aspectRatio + ", "
			 + "bounds = " + bounds + ", "
			 + "apertureQuantity = " + apertureQuantity + ", "
			 + "verticalPageMarginInPoints = " + verticalPageMarginInPoints + ", "
			 + "horizontalPageMarginInPoints = " + horizontalPageMarginInPoints + ", "
			 + "name = " + name + ", "
			 + "manualLayoutPolicy = " + manualLayoutPolicy
		+ "}";
	}
	
	public boolean equals(Object obj)
	{
		if (obj==null || (!(obj instanceof Layout)))
			return false;
		
		Layout other = (Layout) obj;
		
		if (verticalGapInPoints!=other.verticalGapInPoints)
			return false;
		
		if (horizontalGapInPoints!=other.horizontalGapInPoints)
			return false;
		
		if (aspectRatio!=other.aspectRatio)
			return false;
		
		if (apertureQuantity!=other.apertureQuantity)
			return false;
		
		if (verticalPageMarginInPoints!=other.verticalPageMarginInPoints)
			return false;
		
		if (horizontalPageMarginInPoints!=other.horizontalPageMarginInPoints)
			return false;
		
		if (!EqualsFriend.equals(name, other.name))
			return false;
		
		if (!EqualsFriend.equals(bounds, other.bounds))
			return false;
		
		if (manualLayoutPolicy!=other.manualLayoutPolicy)
			return false;
		
		return true;
	}
	
	public int hashCode()
	{
		int result = 37;
		
		result += 17*EqualsFriend.hashCode(verticalGapInPoints);
		result += 17*EqualsFriend.hashCode(horizontalGapInPoints);
		result += 17*EqualsFriend.hashCode(aspectRatio);
		result += 17*EqualsFriend.hashCode(bounds);
		result += 17*apertureQuantity;
		result += 17*EqualsFriend.hashCode(verticalPageMarginInPoints);
		result += 17*EqualsFriend.hashCode(horizontalPageMarginInPoints);
		result += 17*EqualsFriend.hashCode(name);
		
		if (manualLayoutPolicy)
			result += 17;
		
		return result;
	}

	public void removeChangeListener(ChangeListener designatedChangeListener)
	{
		changeSupport.removeChangeListener(designatedChangeListener);
	}

	public void addChangeListener(ChangeListener designatedListener)
	{
		changeSupport.addChangeListener(designatedListener);
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	public static double round(double theValue, int figuresAfterDecimalPoint)
	{
		// TODO implement some kind of flag to turn rounding off
		
		double factor = Math.pow(10.0, figuresAfterDecimalPoint);
		return Math.round(theValue*factor)/factor;
	}

	public boolean isManual()
	{
		return manualLayoutPolicy;
	}

	public void setManual(boolean designatedManualness)
	{
		if (designatedManualness!=manualLayoutPolicy)
		{
			manualLayoutPolicy = designatedManualness;
			
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
}
