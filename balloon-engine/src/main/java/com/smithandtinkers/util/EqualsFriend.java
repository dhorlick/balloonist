/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

/**
 * Supports override implementations of {@link Object#equals} and {@link Object#hashCode}.
 *
 * @author dhorlick
 */
public abstract class EqualsFriend
{
	/**
	 * Evaluates two objects for equalsness.
	 * 
	 * If one refers to the same object reference as two, returns true.
	 * If both are null, returns true.
	 * If one is null but not the other, returns false.
	 * If neither are null, evaluates one.equals(two)
	 * 
	 * To avoid an infinite loop condition, an object should never pass itself to this method
	 * from its own equals method.
	 */
	public static boolean equals(Object one, Object two)
	{
		if (one==two)
			return true;
		
		// if (one==null && two==null)
		// 	return true;
		
		if (one==null || two==null)
			return false;
		
		return one.equals(two);
	}
	
	/**
	 * Per suggestion in Josh Bloch's Effective Java
	 */
	public static int hashCode(double doubleValue)
	{
		return EqualsFriend.hashCode(Double.doubleToLongBits(doubleValue));
	}
	
	/**
	 * Per suggestion in Josh Bloch's Effective Java
	 */
	public static int hashCode(boolean boolValue)
	{
		if (boolValue)
			return 1;
		else
			return 0;
	}
	
	/**
	 * Per suggestion in Josh Bloch's Effective Java
	 */
	public static int hashCode(long longValue)
	{
		return (int) (longValue ^ (longValue >>> 32));
	}
	
	/**
	 * Per suggestion in Josh Bloch's Effective Java
	 */
	public static int hashCode(float floatValue)
	{
		return Float.floatToIntBits(floatValue);
	}
	
	public static int hashCode(Object object)
	{
		if (object==null)
			return 0;
		else
			return object.hashCode();
	}
}
