/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.util;

import com.smithandtinkers.geom.Perch;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;

import com.smithandtinkers.util.Logger;


/**
 * A typesafe list that implements the Kid interface.
 * 
 * When a Kid is added to this List, if it's parent type is equal to the type of this container,
 * its parent will be populated with a reference to this container.
 * 
 * @author dhorlick
 */
public class KidTypesafeList extends TypesafeList implements Kid
{
	private boolean acceptGodChildren = false;
	
	private transient WeakReference parent;
	
	private Class parentType = Collection.class;
	
	/**
	 * @param designatedConstituentType
	 */
	public KidTypesafeList(Class designatedConstituentType)
	{
		super(designatedConstituentType);
	}
	
	/**
	 * @see com.smithandtinkers.util.Kid#getParent()
	 */
	public Collection getParent()
	{
		if (parent==null)
			return null;
			
		return (Collection) parent.get();
	}

	/**
	 * @see com.smithandtinkers.util.Kid#setParent(java.util.Collection)
	 */
	public void setParent(Collection designatedParent)
	{
		parent = new WeakReference(designatedParent);
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findForebear(java.lang.Class)
	 */
	public Object findForebear(Class requestedClass)
	{
		Object generation = this;
		
		while (generation!=null && !(requestedClass.isInstance(generation)))
		{
			Logger.println("got " + generation.getClass().getName());
			if (generation instanceof Kid)
			{
				Kid generationAsKid = (Kid) generation;
				generation = generationAsKid.getParent();
			}
			else
			{
				Logger.println("ran out of parents with " +  generation.getClass().getName());
				return null;
			}
		}
		
		if (generation!=null)
		{
			Logger.print("returning generation ");
			Logger.println(generation.getClass().getName());
		}
		else
		{
			Logger.println("Got to top, but found null there.");
		}
		
		return generation;
	}

	/**
	 * @see com.smithandtinkers.util.Kid#findProgenitor()
	 */
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

	/**
	 * @see com.smithandtinkers.util.Kid#adopted(java.util.Collection)
	 */
	public void adopted(Collection newParent)
	{
		Logger.println("Adopted.");
	}
	
	public void add(int index, Object designatedConstituent)
	{
		super.add(index, designatedConstituent);
		
		if (designatedConstituent!=null && (designatedConstituent instanceof Kid))
		{
			Logger.print("child type: ");
			Logger.println(designatedConstituent.getClass().getName());
			Logger.print("\ttarget child type: ");
			Logger.println(getConstituentType());
				
			Kid designatedConstituentKid = (Kid) designatedConstituent;
			
			Logger.print("Establishing lineage. New child: ");
			Logger.println(designatedConstituent);
			
			designatedConstituentKid.setParent(this);
		}
	}
		
	public Object set(int index, Object element)
	{
		Object result = super.set(index, element);
		
		if (element!=null && (element instanceof Kid))
		{
			Logger.println("Establishing lineage.");
			((Kid)element).setParent(this);
		}
		
		return result;
	}
	
	public boolean equals(Object o)
	{
		if (o==null || !(o instanceof KidTypesafeList))
			return false;
		
		KidTypesafeList other = (KidTypesafeList) o;
		
		if (!EqualsFriend.equals(parentType, other.parentType))
			return false;
		
		// if (parent!=other.parent)
		// 	return false;
		
		if (acceptGodChildren!=other.acceptGodChildren)
			return false;
		
		return super.equals(o);
	}
	
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return super.hashCode();
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
	
	/**
	 @return an array of Object identifying the path to the root of the modified subtree, where the first
	 	element of the array is the object stored at the root node and the last element is the object stored
	 	at the changed node
	 */
	public Object[] determineAncestry(Kid kid)
	{
		int generations = 1;
		
		for (Kid generation=kid; generation!=null && generation.getParent() instanceof Kid; generation=(Kid)((Kid)generation).getParent())
		{
			generations++;
		}
		
		// System.out.println(kid+" has "+generations+" generations.");
		
		Object [] objArray = new Object[generations];
		
		Object generation=kid;
		
		for (int loop=0; generation!=null; generation=((Kid)generation).getParent())
		{
			// System.out.print("\t");
			// System.out.print(index);
			// System.out.print(") ");
			
			objArray[generations-loop-1] = generation;
			
			// System.out.println(generation.getClass());
			// System.out.print("\t\t");
			// System.out.println(generation);
			
			loop++;
		}
		
		// System.out.println("ancestry = " + java.util.Arrays.asList(objArray));
		
		return objArray;
	}
	
	/**
	 * Determines the Object's "official" ancestry. If items in the hierarchy
	 * decorated, it will use the decoration instead of the recorded item itself.
	 * 
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineDecoratedAncestry() // TODO break the decorated methods out into a new DecoratedKidTypesafeList class
	{
		if (getParent()==null || (!(getParent() instanceof KidTypesafeList)))
			return new Object[] {this};
		else
			return ((KidTypesafeList)getParent()).determineDecoratedAncestry(this);
	}
	
	/**
	 * Compile the provided kid's ancestry, swapping in decorated hierarchical
	 * ancestors when present.
	 * 
	 * Makes the basic assumption that decorations don't alter the ordinal positions
	 * of child elements.
	 *
	 * @return an array of Object identifying the path to the root of the modified subtree, where the first
	 * element of the array is the object stored at the root node and the last element is the object stored
	 * at the changed node
	 */
	public Object[] determineDecoratedAncestry(Kid kid)
	{
		int generations = 1;
		
		for (Kid generation=kid; generation!=null && generation.getParent() instanceof Kid; generation=(Kid)((Kid)generation).getParent())
		{
			generations++;
		}
		
		// System.out.println(kid+" has "+generations+" generations.");
		
		// Object [] objArray = new Object[generations];
		int [] objOrdinalPositionArray = new int[generations-1];
		
		Object generation=kid;
		Object previousGeneration=null;
		
		for (int loop=0; generation!=null; generation=((Kid)generation).getParent())
		{
			// System.out.print("\t");
			// System.out.print(index);
			// System.out.print(") ");
			
			// objArray[generations-loop-1] = generation;
			
			Kid generationAsKid = (Kid)generation;
			
			if ( generationAsKid.getParent()!=null )
			{
				List parentAsList = (List) generationAsKid.getParent();
				int theIndex = decorationIndexOf(parentAsList, generationAsKid);
				
				if (theIndex==-1)
				{
					return determineAncestry(kid);
				}
				else // by far our preference
				{
					objOrdinalPositionArray[objOrdinalPositionArray.length-1-loop] = theIndex;
				}
			}
			
			// System.out.println(generation.getClass());
			// System.out.print("\t\t");
			// System.out.println(generation);
			
			loop++;
			previousGeneration = generation;
		}
		
		// System.out.println("lime. ancestry = " + java.util.Arrays.asList(objArray));
		
		Object [] objArray = new Object[generations];
		objArray[0] = previousGeneration;
		
		// System.out.println("objArray.length="+objArray.length);
		// System.out.println("objOrdinalPositionArray.length="+objOrdinalPositionArray.length);
		
		for (int loop=0; loop<=objOrdinalPositionArray.length-1; loop++)
		{
			// System.out.println("\tloop: "+loop);
			// System.out.println("\tobjOrdinalPositionArray[loop]="+objOrdinalPositionArray[loop]);
			List list = (List)objArray[loop];
			// System.out.println("\tlist.size(): "+list.size());
			objArray[loop+1]=list.get(objOrdinalPositionArray[loop]);
		}

		return objArray;
	}
	
	public static int decorationIndexOf(List list, Kid item)
	{
		for (int loop=0; loop<=list.size()-1; loop++)
		{
			if (list.get(loop)==item)
				return loop;
			
			Object bastard = list.get(loop);
			
			while (bastard instanceof Perch)
			{
				Perch perchedBastard = (Perch) bastard;
				
				if (perchedBastard.getUnperched()==item)
					return loop;
				
				bastard = perchedBastard.getUnperched();
			}
		}
		
		// We tried.
		
		return -1;
	}

	public Object remove(int index)
	{
		Object removed;
		
		removed = super.remove(index);
		
		if (removed!=null && removed instanceof Kid)
		{
			Kid asKid = (Kid) removed;
	
			if (asKid.getParent()==this)
				asKid.setParent(null);
		}
		
		return removed;
	}
}
