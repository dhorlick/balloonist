/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import java.awt.Point;


/**
 * Allows gutters to be targeted without being formally selected.
 * 
 * @author dhorlick
 */
public class ShootingRange
{
	private Alley alley = new Alley();
	private Point target;
	
	public ShootingRange()
	{
	}

	public Alley getAlley()
	{
		return alley;
	}

	public Point getTarget()
	{
		return target;
	}

	public void setTarget(Point designatedTarget)
	{
		if (designatedTarget!=target)
		{
			target = designatedTarget;
		}
	}
	
	/**
	 * Requests that the crowds around targeted items' be re-layed-out.
	 */
	public void reLayoutCrowdsOfTargetedItems()
	{
		// System.out.println("invoked reLayoutCrowdsOfTargetedItems(): target="+target);
		
		for (int loop=0; loop<=getAlley().size()-1; loop++)
		{
			Targetable targetable = (Targetable) getAlley().get(loop);

			if (targetable.isTargeted(this)) // TODO performance enhancement: only do this if a single targetable would be affected.
			{
				Crowd.reLayoutCrowdsOf(targetable);
			}
		}
	}
}
