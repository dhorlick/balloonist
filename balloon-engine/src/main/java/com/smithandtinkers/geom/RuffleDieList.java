/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

import com.smithandtinkers.mvc.GodKidListModelTypesafeList;
import java.util.Iterator;


/**
 *
 * @author dhorlick
 */
public class RuffleDieList extends GodKidListModelTypesafeList
{
	public RuffleDieList()
	{
		super(RuffleDie.class);
	}

	public RuffleDie findTallest()
	{
		RuffleDie tallest = null;
		
		Iterator walk = iterator();
		
		while (walk.hasNext())
		{
			RuffleDie ruffle = (RuffleDie) walk.next();
			if (tallest == null || ruffle.getHeightInPoints() > tallest.getHeightInPoints())
				tallest = ruffle;
		}
		
		return tallest;
	}
}
