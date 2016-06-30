/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.util.*;


/**
 * A list of pretty-much-unique gutters.
 *
 * @author dhorlick
 */
public class Alley extends TypesafeList
{
	public Alley()
	{
		super(Gutter.class);
	}

	public void add(int index, Object element)
	{
		// System.out.println("Alley.add");
		
		if (!prettyMuchContains((Gutter)element))
			super.add(index, element);
	}
	
	public boolean prettyMuchContains(Gutter otherGutter)
	{
		// System.out.println("Alley.prettyMuchContains");
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			Gutter gutter = (Gutter)get(loop);
			if (otherGutter.sameAs(gutter))
				return true;
		}
		
		return false;
	}
}
