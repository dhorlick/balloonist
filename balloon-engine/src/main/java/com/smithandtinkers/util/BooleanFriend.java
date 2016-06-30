/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.io.PrintStream;

/**
 * @author dhorlick
 */
public abstract class BooleanFriend
{
	public static final String [] TRUE_LABELS = {"true", "yes", "on", "y", "1"};
	public static final String [] FALSE_LABELS = {"false", "no", "off", "n", "0"};
	
	/**
	 * @return the boolean value corresponding to the provided String, as resolved by performing
	 * a case-insensitive containment check on the true and false label constants.
	 * 
	 * @throws NumberFormatException if none match.
	 */
	public static boolean parseBoolean(String unparsed) throws NumberFormatException
	{
		if (arrayContainsStringIgnoreCase(TRUE_LABELS, unparsed))
			return true;
		
		if (arrayContainsStringIgnoreCase(FALSE_LABELS, unparsed))
			return false;
		
		throw new NumberFormatException("Unrecognized boolean value: \""+unparsed+"\". Try ");
	}	
	
	public static boolean arrayContainsStringIgnoreCase(String [] theArray, String theString)
	{
		for (int loop=0; loop<=theArray.length-1; loop++)
		{
			if (theArray[loop]==null && theString==null)
				return true;
			
			if (theArray[loop]!=null && theArray[loop].equalsIgnoreCase(theString))
				return true;				
		}
		
		return false;
	}
	
	public static void print(PrintStream ps, boolean [] boolArray)
	{
		ps.print("[");
		
		for (int loop=0; loop<=boolArray.length-1; loop++)
		{
			ps.print(boolArray[loop]);
			if (loop<boolArray.length-1)
				ps.print(", ");
		}
		
		ps.print("]");
	}
	
	public static void println(PrintStream ps, boolean [] boolArray)
	{
		print(ps, boolArray);
		ps.println();
	}
}
