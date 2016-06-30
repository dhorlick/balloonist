/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.util;

import java.util.List;

/**
 * Models the numbering system used by many software makers.
 * 
 * @author dhorlick
 */
public class VersionNumber
{
	private List integers = new TypesafeList(Integer.class);
	
	private static final Integer ZERO = new Integer(0);
	
	public VersionNumber()
	{
	}
	
	/**
	 * @param text A sequence of positive integers separated by non-digit characters.
	 */
	public static VersionNumber parse(String text)
	{
		// System.out.println("Parsing \""+text+"\"...");
		// String [] segments = text.split("[\\.\\-_]");
		
		VersionNumber vn = new VersionNumber();
		
		if (text==null)
			return vn;
		
		text=text.trim();
		
		if (text.length()==0)
			return vn;
		
		// System.out.println("*"+text+"*");
		
		StringBuffer strungInteger = new StringBuffer();
		
		for (int loop=0; loop<=text.length()-1; loop++)
		{
			if (Character.isDigit(text.charAt(loop)))
				strungInteger.append(text.charAt(loop));
			else if (strungInteger.length()>0) 
			{
				int newInt = Integer.parseInt(strungInteger.toString());
				vn.integers.add(new Integer(newInt));
				strungInteger.setLength(0);
			}
		}
		
		if (strungInteger.length()>0)
		{
			int newInt = Integer.parseInt(strungInteger.toString());
			vn.integers.add(new Integer(newInt));			
		}
				
		return vn;
	}
	
	public String toString()
	{
		StringBuffer desc = new StringBuffer();
		for (int loop=0; loop<=integers.size()-1; loop++)
		{
			desc.append(integers.get(loop));
			if (loop<integers.size()-1)
				desc.append(".");
		}
		
		return desc.toString();
	}

	/**
	 @return Boolean.TRUE, if this version number is greater than the other
	         Boolean.FALSE, if this version number is less than the other
	         null, if both version numbers are equivalent in magnitude
	 */
	public Boolean compareTo(VersionNumber other)
	{
		for (int loop=0; loop<=components()-1; loop++)
		{
			int thisInt = getDottedComponent(loop).intValue();
			
			int otherInt = 0;
			
			if (other.components()>=loop+1)
				otherInt = other.getDottedComponent(loop).intValue();
			
			if (thisInt > otherInt)
			{
				return Boolean.TRUE;
			}
			
			if (thisInt < otherInt)
			{
				return Boolean.FALSE;
			}
		}
		
		for (int loop=components(); loop<=other.components()-1; loop++)
		{
			if (other.getDottedComponent(loop).intValue()>0)
				return Boolean.FALSE;
		}
		
		return null;
	}
	
	public boolean isGreaterThanOrEqualTo(VersionNumber other)
	{
		Boolean result = compareTo(other);
		
		if (result==Boolean.TRUE || result==null)
			return true;
		else
			return false;
	}
	
	public int components()
	{
		return integers.size();
	}
	
	public Integer getDottedComponent(int index)
	{
		if (index<=components()-1)
			return (Integer) integers.get(index);
		else
			return new Integer(0);
	}
	
	/**
	 * The value of this version number minus the other. Non-numeric 
	 * dotted components will be evaluated as "zero".
	 */
	public double subtract(VersionNumber other)
	{
		double difference = 0.0;
		
		final int thisComponents = components();
		final int otherComponents = other.components();

		int components = 0;
		
		if (thisComponents>otherComponents)
			components = thisComponents;
		else
			components = otherComponents;
			
		for (int loop=0; loop<=components-1; loop++)
		{
			int thisInt = getDottedComponent(loop).intValue();
			
			int otherInt = 0;
			
			if (other.components()>=loop+1)
				otherInt = other.getDottedComponent(loop).intValue();
			
			final long thisDigits = determineNumberOfDigits(thisInt);
			final long otherDigits = determineNumberOfDigits(otherInt);
			
			long digits = 0;
			
			if (thisDigits>otherDigits)
				digits = thisDigits;
			else
				digits = otherDigits;
			
			double thisDifference = (thisInt - otherInt)/Math.pow(10.0, digits-1);
			
			/*
			System.out.println("index: "+loop);
			System.out.println("\tthisInt = " + thisInt);
			System.out.println("\totherInt = " + otherInt);
			System.out.println("\tdigits["+loop+"]="+digits);
			System.out.println("\tthisDifference = "+thisDifference);
			*/
			
			difference += (thisDifference/(Math.pow(10.0, loop)));
		}
		
		return round(difference, components);
	}
	
	public static long determineNumberOfDigits(final int number)
	{
		if (number==0)
		{
			return 1;
		}
		
		double absolutedNumber = Math.abs(number);
		double logarithm = log10(absolutedNumber);
		return 1L + (long) Math.floor(logarithm);
	}
	
	public static double log10(double x)
	{
		return Math.log(x)/Math.log(10);
	}
	
	public static double round(double theValue, int figuresAfterDecimalPoint)
	{
		// System.out.println("figuresAfterDecimalPoint="+figuresAfterDecimalPoint);
		
		double factor = Math.pow(10.0, figuresAfterDecimalPoint);
		return Math.round(theValue*factor)/factor;
	}
}
