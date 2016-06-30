/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.lang.ref.WeakReference;
import java.util.Collection;


/**
 * @author dhorlick
 */
public abstract class AbstractKid implements Kid
{
	private transient WeakReference parent;
	
	public AbstractKid()
	{
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
	
	public Collection getParent()
	{
		if (parent==null)
			return null;
		else
			return (Collection) parent.get();
	}
	
	public void setParent(Collection designatedParent)
	{
		if (parent==null && designatedParent==null)
			return;
		
		Logger.print("designated parent: ");
		Logger.println(designatedParent);
		
		if ((parent==null && designatedParent!=null) || parent.get()!=designatedParent)
		{
			if (designatedParent==null)
				parent=null;
			else
			{
				parent = new WeakReference(designatedParent);
				adopted(designatedParent);
			}
				
			Logger.println("New parent set.");
		}
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
	
	/**
	 Override this if you want something to happen when this 
	 */
	public void adopted(Collection newParent)
	{
		Logger.print("Adopted.");
	}

	/**
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineAncestry()
	{
		if (getParent()==null || (!(getParent() instanceof KidTypesafeList)))
			return new Object[] {this};
		else
			return ((KidTypesafeList)getParent()).determineAncestry(this);
	}
}
