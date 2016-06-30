/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.util.Collection;

/**
 * @author dhorlick
 */
public interface Kid extends FamilyMember
{
	/**
	 <p>@return  Most recent TypesafeArrayList of the specified parent type
	 that this TypesafeArrayList was added to.</p>
	 
	 <p>If this TypesafeArrayList has never been added to anything, returns null.</p>
	 */
	public Collection getParent(); // TODO change this to java.util.List so we don't get class cast exceptions when determining ancestry
	
	public void setParent(Collection designatedParent);
	
	/**
	 <p>Attempt to find compositional ancestor of the requested type.</p>
	 */
	public Object findForebear(Class requestedClass);
	
	public Object findProgenitor();
	
	public void adopted(Collection newParent);
	
	/**
	 * @return An ancestry object array, suitable for constructing a {@link javax.swing.tree.TreePath} around.
	 */
	public Object[] determineAncestry();
}
