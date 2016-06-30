/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

public class AbstractGodKid extends AbstractKid implements GodKid
{
	private Object godParent;
	
	public Object getGodParent()
	{
		return godParent;
	}

	public void setGodParent(Object designatedGodParent)
	{
		godParent = designatedGodParent;
	}
	
	/**
	 * @see com.smithandtinkers.util.Kid#determineAncestry()
	 */
	public Object[] determineAncestry()
	{
		Object [] ancestry = super.determineAncestry();
		
		if (getGodParent()!=null && ancestry.length>1)
		{
			// System.out.println("Overloading ancestry!");
			
			ancestry[ancestry.length-2]=getGodParent();
		}
		
		return ancestry;
	}
}
