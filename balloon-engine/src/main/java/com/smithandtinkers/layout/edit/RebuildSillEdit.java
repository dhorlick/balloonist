/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.awt.geom.Point2D;

/**
 *
 * @author dhorlick
 */
public class RebuildSillEdit extends PresentableEdit
{
	private Sill sill;
	
	private Point2D newLocation;
	private AperturePerch newAperturePerch;
	
	private Point2D oldLocation;
	private AperturePerch oldAperturePerch;
	
	public RebuildSillEdit(Sill designatedSill)
	{
		super("Rebuild Panel"); // TODO i8n
		setSill(designatedSill);
	}
	
	public RebuildSillEdit(Sill designatedSill, Point2D designatedNewLocation, AperturePerch designatedNewAperturePerch)
	{
		this(designatedSill);
		
		setNewLocation(designatedNewLocation);
		setNewAperturePerch(designatedNewAperturePerch);
		
		setOldAperturePerch(getSill().getAperture());
		setOldLocation(getSill().getLocation());
	}

	public boolean hasEffect()
	{
		if (getSill()!=null && getNewAperturePerch()!=null && getNewLocation()!=null
				&& (getNewAperturePerch()!=getSill().getAperture() || getNewLocation()!=getSill().getLocation()))
		{
			return true;
		}
		
		return false;
	}

	public boolean execute()
	{
		getSill().setAperture(getNewAperturePerch());
		getSill().setLocation(getNewLocation());
		
		// System.out.println("execute: for "+getSill()+" new loc= "+getNewLocation());
		reLayout();
		
		return true;
	}

	public boolean backout()
	{
		getSill().setAperture(getOldAperturePerch());
		getSill().setLocation(getOldLocation());
		
		// System.out.println("backed out location for "+getSill()+" from "+getNewLocation() + " to " +getOldLocation());
		reLayout();
		
		return true;
	}

	public Point2D getOldLocation()
	{
		return oldLocation;
	}

	private void setOldLocation(Point2D designatedOldLocation)
	{
		// System.out.println("setting old location to: "+designatedOldLocation);
		
		if (designatedOldLocation==null)
			oldLocation = null;
		else
			oldLocation = new Point2D.Double(designatedOldLocation.getX(), designatedOldLocation.getY());
	}

	public AperturePerch getOldAperturePerch()
	{
		return oldAperturePerch;
	}

	private void setOldAperturePerch(AperturePerch designatedOldAperturePerch)
	{
		oldAperturePerch = designatedOldAperturePerch;
	}

	public Point2D getNewLocation()
	{
		return newLocation;
	}

	public void setNewLocation(Point2D designatedNewLocation)
	{
		newLocation = designatedNewLocation;
	}

	public AperturePerch getNewAperturePerch()
	{
		return newAperturePerch;
	}

	public void setNewAperturePerch(AperturePerch designatedNewAperturePerch)
	{
		newAperturePerch = designatedNewAperturePerch;
	}

	public Sill getSill()
	{
		return sill;
	}
	
	private void setSill(Sill designatedSill)
	{
		sill = designatedSill;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.RebuildSillEdit {"
			// + "super = " + super.toString()
			 + "sill = " + sill + ", "
			 + "newLocation = " + newLocation + ", "
			 + "newAperturePerch = " + newAperturePerch + ", "
			 + "oldLocation = " + oldLocation + ", "
			 + "oldAperturePerch = " + oldAperturePerch
		+ "}";
	}
	
	public void reLayout()
	{
		if (sill!=null)
		{
			for (int loop=0; loop<=sill.size()-1; loop++)
			{
				if (sill.get(loop) instanceof Crowd)
				{
					Crowd.reLayoutCrowdsOf(sill.get(loop));
				}
			}
		}
	}
}
