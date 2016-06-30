/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

/**
 * A way to specify an additional parent for purposes other than hierarchy.
 * 
 * Attempts to unite decorators & composites.
 * 
 * @author dhorlick
 */
public interface GodKid extends FamilyMember
{
	public Object getGodParent();
	
	public void setGodParent(Object designatedGodParent);
}
