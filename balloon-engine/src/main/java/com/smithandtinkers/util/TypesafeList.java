/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.util.*;

/**
 * A list that enforces runtime type-safety, without requiring Java 5.0.
 *
 * Since it doesn't rely on type erasure, you can easily discover its constituent type. This
 * turns out to be pretty useful for binding GUI controls to it.
 *
 * @author dhorlick
 */
public class TypesafeList extends AbstractList implements List
{
	private List items = new ArrayList();

	private final Class constituentType;
	
	public TypesafeList(Class designatedConstituentType)
	{
		super();
		constituentType = designatedConstituentType;
	}

	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index)
	{
		return items.get(index);
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size()
	{
		return items.size();
	}
	
	public Class getConstituentType()
	{
		return constituentType;
	}
	
	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element)
	{
		verifyConstituent(element);
		
		items.add(index, element);
	}
	
	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index)
	{
		return items.remove(index);
	}
	
	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public Object set(int index, Object element)
	{
		verifyConstituent(element);

		return items.set(index, element);
	}
	
	/**
	 * throws IllegalArgumentException if passed a constituent that is not assignment compatible with
	 * the constituent type.
	 */
	protected void verifyConstituent(Object designatedConstituent)
	{
		if (designatedConstituent!=null && !(constituentType.isInstance(designatedConstituent)))
			throw new IllegalArgumentException(generateClassCastExceptionMessage(this.getClass(), designatedConstituent.getClass(), constituentType));
	}
	
	private static String generateClassCastExceptionMessage(Class proposedContainer, Class designatedConstituentType, Class appropriateConstituentType)
	{
		StringBuffer message = new StringBuffer(120);
		
		message.append(
				"The designated consituent of type ").append(
				designatedConstituentType.getName()).append(
				" is invalid for ");
		
		message.append(proposedContainer.getName());
		
		message.append(". Additions must have type ").append(
				appropriateConstituentType.getName());
		
		return (message.toString());
	}
	
	public boolean equals(Object o)
	{
		if (o==null || (!(o instanceof TypesafeList)))
			return false;
		
		return super.equals(o);
	}
	
	public int hashCode()
	{
		return 37 + super.hashCode();
	}
	
	/**
	 * Makes a copy of this type safe list to the designated destination. Any child elements that
	 * implement {@link com.smithandtinkers.util.PubliclyCloneable} will be deep copied, if possible.
	 */
	public void deeperCopyTo(Collection destination)
	{
		Iterator walk = iterator();

		try
		{
			while (walk.hasNext())
			{
				Object item = walk.next();

				if (item instanceof PubliclyCloneable)
					item = ((PubliclyCloneable)item).clone();
				
				destination.add(item);
			}
		}
		catch (CloneNotSupportedException exception)
		{
			throw new BugException(exception);
			
			// This should never happen because PubliclyCloneable extends Cloneable,
			// and any Object that implementing Cloneable can by definition be cloned.
		}
	}
}
