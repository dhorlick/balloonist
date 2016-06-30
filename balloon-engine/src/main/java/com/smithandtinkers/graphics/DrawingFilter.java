/*
 *  Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.graphics;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.TypesafeList;


/**
 * Allows for fine-grained control over exactly what gets drawn. This can be
 * useful for providing performant drag animations.
 *
 * @author dhorlick
 */
public class DrawingFilter
{
	private TypesafeList items = new TypesafeList(Drawable.class);
	
	private FilterState filterState;
	
	public final static FilterState RETAIN = new FilterState("retain");
	public final static FilterState EXCLUDE = new FilterState("exclude");
	
	/**
	 * Add the designated drawable, unless it has already been added.
	 *
	 * @return whether or not the designated drawable was added 
	 */
	public boolean add(Drawable designatedDrawable)
	{
		if (designatedDrawable==null)
			return false;
		
		if (!items.contains(designatedDrawable))
		{
			items.add(designatedDrawable);
			return true;
		}
		
		return false;
	}
	
	public int size()
	{
		return items.size();
	}
	
	public void clear()
	{
		items.clear();
	}
	
	public FilterState getFilterState()
	{
		return filterState;	
	}
	
	/**
	 * @param designatedFilterState can be RETAIN, EXCLUDE, null.
	 */
	public void setFilterState(FilterState designatedFilterState)
	{
		filterState = designatedFilterState;
	}
	
	private static class FilterState
	{
		private String key;
		
		private FilterState(String designatedKey)
		{
			key = designatedKey;
		}
		
		public String toString()
		{
			return key;
		}
	}
	
	public boolean pass(Drawable drawable)
	{
		if (filterState==null)
			return true;
			
		if (items.contains(drawable))
			return (filterState==RETAIN);
		else
			return (filterState==EXCLUDE);
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.DrawingFilter {"
			 + "items = " + items + ", "
			 + "filterState = " + filterState
		+ "}";
	}
	
	public Object findFirst(Class requestedType) // TOOD have this class and Selection both extend the same abstract base class?
	{
		for (int loop=0; loop<=items.size()-1; loop++)
		{
			if (items.get(loop)!=null && requestedType.isAssignableFrom(items.get(loop).getClass()))
			{
				return items.get(loop);
			}
		}

		return null;
	}
	
	public boolean containsInstanceOf(Class requestedType)
	{
		return (findFirst(requestedType)!=null);
	}
}