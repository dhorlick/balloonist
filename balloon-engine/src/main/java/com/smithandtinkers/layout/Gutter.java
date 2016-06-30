/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.mvc.SingleThreadedChangeSupport;

import java.awt.Color;
import java.awt.geom.*;
import javax.swing.event.ChangeEvent;


/**
 * An unordered pair of neighboring square sills that are separated by a special channel.
 *
 * @author dhorlick
 */
public class Gutter implements Relocateable, Targetable
{
	private Sill north;
	private Sill east;
	private Sill south;
	private Sill west;
	
	private transient Rectangle2D mostRecentBounds;
	
	// final protected ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public final int FRECKLE_SPACING = 6;
	public final int FRECKLE_SIZE = 5;
	
	public final Color FRECKLE_TOP_COLOR = Color.white;
	public final Color FRECKLE_BOTTOM_COLOR = new Color(175, 155, 135);
	
	private final static String NO_SPACE_MESSAGE = "There is no space between the sills.";
	private final static String NO_APERTURES_MESSAGE = "Parameter sills must have apertures.";
	
	public Gutter()
	{
	}
	
	/**
	 * Both params must either be null or non-null.
	 *
	 * If the params are non-null, they must both have rectangular 2D apertures.
	 */
	public void setNorthAndSouth(Sill designatedNorth, Sill designatedSouth)
	{
		validate(designatedNorth, designatedSouth);
		
		if (designatedNorth!=null)
		{
			if (designatedNorth.getAperture()==null || designatedSouth.getAperture()==null)
				throw new IllegalArgumentException("Parameter sills must have apertures.");
			
			if (designatedNorth.getLocation().getY()+designatedNorth.getHeight() >= designatedSouth.getLocation().getY())
				throw new IllegalArgumentException(NO_SPACE_MESSAGE);
		}
		
		north = designatedNorth;
		south = designatedSouth;
	}

	public Sill getNorth()
	{
		return north;
	}

	public Sill getEast()
	{
		return east;
	}

	public Sill getSouth()
	{
		return south;
	}

	public Sill getWest()
	{
		return west;
	}

	public void setWestAndEast(Sill designatedWest, Sill designatedEast)
	{
		validate(designatedWest, designatedEast);
		
		if (designatedWest!=null)
		{
			if (designatedWest.getAperture()==null || designatedEast.getAperture()==null)
				throw new IllegalArgumentException(NO_APERTURES_MESSAGE);
			
			if (designatedWest.getLocation().getX()+designatedWest.getWidth() >= designatedEast.getLocation().getX())
				throw new IllegalArgumentException(NO_SPACE_MESSAGE);
		}
		
		west = designatedWest;
		east = designatedEast;
	}
	
	public Rectangle2D determineBoundary()
	{
		Rectangle2D boundary = new Rectangle2D.Double();
		
		if (north!=null && south!=null)
		{
			double gutterTop = north.getLocation().getY()+north.getAperture().getHeight();
			
			boundary.setRect(north.getLocation().getX(), 
					gutterTop,
					north.getWidth(), // TODO fix width
					south.getLocation().getY()-gutterTop);
		}
		
		if (west!=null && east!=null)
		{
			double gutterLeft = west.getLocation().getX()+west.getAperture().getWidth();
			
			boundary.setRect(gutterLeft, 
					west.getLocation().getY(), 
					east.getLocation().getX() - gutterLeft,
					west.getHeight()); // TODO fix height
		}
		
		return boundary;
	}
	
	public void removeChangeListener(javax.swing.event.ChangeListener designatedChangeListener)
	{
		// changes will be handled by the companion Sills
	}

	public void addChangeListener(javax.swing.event.ChangeListener designatedListener)
	{
		// changes will be handled by the companion Sills
	}

	public void setLocation(java.awt.geom.Point2D location)
	{
	}

	public void translate(double dx, double dy)
	{
		if (dx!=0 || dy!=0)
		{
			int deltaX = (int) dx;
			int deltaY = (int) dy;
			
			if (dx!=0 && west!=null)
			{
				west.resize(PerimeterSegment.RIGHT_SIDE, 0, 0, deltaX, 0);
				east.resize(PerimeterSegment.LEFT_SIDE, 0, 0, deltaX, 0);
				
				mostRecentBounds=null;
			}
			
			if (dy!=0 && north!=null)
			{
				north.resize(PerimeterSegment.BOTTOM_SIDE, 0, 0, 0, deltaY);
				south.resize(PerimeterSegment.TOP_SIDE, 0, 0, 0, deltaY);
				
				mostRecentBounds=null;
			}
			
			// changeSupport.fireChange(CHANGE_EVENT);
		}
	}

	public void setLocation(double newX, double newY)
	{	
	}

	public Rectangle2D getResizeableBounds2D()
	{
		return determineBoundary();
	}

	public Point2D getLocation()
	{
		Rectangle2D bounds = getResizeableBounds2D();
		Point2D center = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
		return center;
	}

	public int draw(com.smithandtinkers.graphics.DrawingContext drawingContext)
	{
		final int FRECKLE_RADIUS = FRECKLE_SIZE / 2;
		final int HALF_FRECKLE_SPACING = FRECKLE_SPACING / 2;
		
		Rectangle2D bounds = getResizeableBounds2D();
		
		int row = 1;

		for (int y=FRECKLE_RADIUS; row<=2 || y<=(int)bounds.getHeight()-FRECKLE_RADIUS; y+=FRECKLE_SPACING)
		{
			boolean even = ((row % 2) == 0);
			
			for (int x=(even?HALF_FRECKLE_SPACING:0); x<=(int)bounds.getWidth(); x+=FRECKLE_SPACING)
			{
				drawingContext.plotDot((int)bounds.getX()+x, (int)bounds.getY()+y, FRECKLE_BOTTOM_COLOR, FRECKLE_SIZE, true);
				drawingContext.plotDot((int)bounds.getX()+x, (int)bounds.getY()+y, FRECKLE_TOP_COLOR, FRECKLE_SIZE-1, true);
			}
			
			row++;
		}
		
		return 0;
	}
	
	public String toString()
	{
		StringBuffer desc = new StringBuffer();
		desc.append("Gutter [ ");
		
		/* Rectangle2D bounds = getResizeableBounds2D();
		if (bounds!=null)
		{
			desc.append(bounds);
		}
		
		desc.append(" ]"); */
		desc.append("north=");
		desc.append(north);
		desc.append(", east=");
		desc.append(east);
		desc.append(", south=");
		desc.append(south);
		desc.append(", west=");
		desc.append(west);
		
		return desc.toString();
	}

	public boolean sameAs(Gutter otherGutter)
	{	
		if ((north==otherGutter.north) && (east==otherGutter.east) && (south==otherGutter.south) && (west==otherGutter.west))
			return true;
		
		return false;
	}

	public boolean isTargeted(ShootingRange shootingRange)
	{
		if (shootingRange!=null && shootingRange.getTarget()!=null)
		{
			double x = shootingRange.getTarget().getX();
			double y = shootingRange.getTarget().getY();
			
			Sill parentSill = getParentSill();
			if (parentSill!=null) // TODO this ignores the possibility of grand parents
			{
				x = parentSill.mapOutsideX(x);
				y = parentSill.mapOutsideY(y);
			}
			
			return (getBoundaryUsingCacheIfPossible().contains(x, y));
		}
		
		return false;
	}
	
	public Rectangle2D getBoundaryUsingCacheIfPossible()
	{
		if (mostRecentBounds == null)
			mostRecentBounds = determineBoundary();
		
		return mostRecentBounds;
	}
	
	private void validate(Sill sill1, Sill sill2)
	{
		if ((sill1==null || sill2==null) && (sill1!=sill2))
			throw new IllegalArgumentException("If one side is null, the other must be too.");
		
		if (sill1!=null && sill2!=null && sill1.getParent()!=sill2.getParent())
			throw new IllegalArgumentException("Sills must have the same parent sill.");
	}
	
	private Sill getParentSill()
	{
		if (north!=null)
			return (Sill) north.getParent();
		else if (west!=null)
			return (Sill) west.getParent();
		
		return null;
	}
	
	public boolean overlaps(Gutter otherGutter)
	{
		return getBoundaryUsingCacheIfPossible().intersects(otherGutter.getBoundaryUsingCacheIfPossible());
	}
	
	public void absorb(Gutter otherGutter)
	{
		// TODO figure out how to represent this
	}
	
	/**
	 * Substracts the light rectangle from the heavy one, and then returns the largest
	 * rectangle that will fit inside the result.
	 */
	public static Rectangle2D subtrasect(Rectangle2D heavy, Rectangle2D light)
	{
		throw new UnsupportedOperationException();
		// TODO
	}
}
