/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout;


/**
 * Refers to an object that can be acted on merely by positioning the mouse over it.
 *
 * Does not require formal selection.
 * 
 * @author dhorlick
 */
public interface Targetable
{
	public boolean isTargeted(ShootingRange shootingRange);
}
