/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

/**
 * @author dhorlick
 */
public class NullHatingTypesafeList extends TypesafeList
{
	/**
	 * @param designatedConstituentType
	 */
	public NullHatingTypesafeList(Class designatedConstituentType)
	{
		super(designatedConstituentType);
	}

	public void add(int index, Object element)
	{
		if (element!=null)
			super.add(index, element);
	}
	
	/**
	 * @see java.util.List#set(int, java.lang.Object)
	 * @throws NullPointerException if a request is received to set an element to null.
	 */
	public Object set(int index, Object element)
	{
		if (element!=null)
			return super.set(index, element);
		else
			throw new NullPointerException();
	}
}
