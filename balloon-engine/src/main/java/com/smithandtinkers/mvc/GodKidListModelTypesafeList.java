/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.GodKid;


/**
 * Evidence that my reputation for long class names is richly deserved.
 *
 * @author dhorlick
 */
public class GodKidListModelTypesafeList extends ListModelTypesafeList implements GodKid
{	
	private Object godParent;
	
	public GodKidListModelTypesafeList(Class designatedType)
	{
		super(designatedType);
	}

	public void setGodParent(Object designatedGodParent)
	{
		godParent = designatedGodParent;
	}

	public Object getGodParent()
	{
		return godParent;
	}
}
