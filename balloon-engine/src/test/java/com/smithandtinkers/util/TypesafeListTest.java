package com.smithandtinkers.util;

import junit.framework.TestCase;
import java.util.List;

/**
 * @author dhorlick
 */
public class TypesafeListTest extends TestCase
{
	public void testEquals()
	{
		TypesafeList list1 = new TypesafeList(String.class);
		list1.add("Kermit");
		list1.add("Fozzie");
		list1.add("Piggie");
		list1.add("Gonzo");

		if (!list1.equals(list1))
			throw new IllegalArgumentException("list1 doesn't equals itself.");

		TypesafeList list2 = new TypesafeList(String.class);
		list2.add("Kermit");
		list2.add("Fozzie");
		list2.add("Piggie");
		list2.add("Gonzo");

		if (!list1.equals(list2))
			throw new IllegalArgumentException("list1 doesn't equals its exact copy.");

		if (list1.hashCode()!=list2.hashCode())
		{
			throw new IllegalArgumentException("list1.hashCode: "+list1.hashCode()+" != list2.hashCode()"+list2.hashCode());
		}

		list2.add("Beaker");

		if (list1.equals(list2))
			throw new IllegalArgumentException("list1 equals modified copy.");

		if (list1.hashCode()==list2.hashCode())
			throw new IllegalArgumentException("list1 hashcode modified copy.");
	}

	public void testAdd()
	{
		List safestrings = new TypesafeList(String.class);

		try
		{
			safestrings.add("pineapple");
		}
		catch (IllegalArgumentException exception)
		{
			throw new IllegalStateException("TypesafeList wouldn't allow addition of String object after being primed for Strings.");
		}

		try
		{
			safestrings.add(new Long(6));
			throw new IllegalStateException("TypesafeList allowed addition of Long object after being primed for Strings.");
		}
		catch (IllegalArgumentException exception)
		{
			; // Good
		}

		if (safestrings.size()!=1)
			throw new IllegalStateException("Size after addition should be 1, but instead is " + safestrings.size());

		safestrings.add("pear");

		if (safestrings.size()!=2)
			throw new IllegalStateException("Size didn't increment to 2 after second addition, instead it's " + safestrings.size());
	}

	public void testSet()
	{
		List numbers = new TypesafeList(Long.class);

		numbers.add(new Long(1));
		numbers.add(new Long(5));
		numbers.add(new Long(9));

		if (numbers.size()!=3)
			throw new IllegalStateException("numbers.size should be 3. instead it's "+numbers.size());

		if (!numbers.get(0).equals(new Long(1)))
			throw new IllegalStateException("first item isn't object 1L. instead, it's "+numbers.get(0));

		if (!numbers.get(1).equals(new Long(5)))
			throw new IllegalStateException("second item isn't object 5L. instead, it's "+numbers.get(1));

		if (!numbers.get(2).equals(new Long(9)))
			throw new IllegalStateException("third item isn't object 9L");

		numbers.set(1, new Long(3));

		if (!numbers.get(0).equals(new Long(1)))
			throw new IllegalStateException("after change, first item isn't still object 1L");

		if (!numbers.get(1).equals(new Long(3)))
			throw new IllegalStateException("after change, second item isn't now object 3L");

		if (!numbers.get(2).equals(new Long(9)))
			throw new IllegalStateException("after change, third item isn't still object 9L");

	}

	public void testIndexOf()
	{
		TypesafeList list = new TypesafeList(String.class);
		list.add("frog");
		list.add("bear");
		list.add("pig");
		list.add("thing");

		if (list.indexOf("bear")!=1)
			throw new IllegalStateException("looking to find \"bear\" at index 1");
	}
}
