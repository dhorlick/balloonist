package com.smithandtinkers.util;

import com.smithandtinkers.geom.Parallelogram;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class KidTypesafeListTest extends TestCase
{
	public void test()
	{
		KidTypesafeList ktlGen1 = new KidTypesafeList(List.class);
		KidTypesafeList ktlGen2 = new KidTypesafeList(List.class);
		KidTypesafeList ktlGen3 = new KidTypesafeList(Parallelogram.class);
		ktlGen1.add(ktlGen2);
		ktlGen2.add(ktlGen3);

		Parallelogram greatGrandkid1 = new Parallelogram();
			greatGrandkid1.setName("Gustav");
		Parallelogram greatGrandkid2 = new Parallelogram();
			greatGrandkid2.setName("Ezekial");
		Parallelogram greatGrandkid3 = new Parallelogram();
			greatGrandkid3.setName("Desmond");

		ktlGen3.add(greatGrandkid1);
		ktlGen3.add(greatGrandkid2);
		ktlGen3.add(greatGrandkid3);

		Object [] det1 = ktlGen3.determineAncestry(greatGrandkid3);
		String det1Strung = Arrays.asList(det1).toString();

		Object [] det2 = ktlGen3.determineDecoratedAncestry(greatGrandkid3);
		String det2Strung = Arrays.asList(det2).toString();

		if (!det1Strung.equals(det2Strung))
		{
			throw new IllegalStateException(det1Strung+"!="+det2Strung);
		}
	}
}
