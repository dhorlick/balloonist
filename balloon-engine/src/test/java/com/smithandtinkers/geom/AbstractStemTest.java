/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.geom;

import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class AbstractStemTest extends TestCase
{
	public void test()
	{
		if (!AbstractStem.triangleContains(0,0, 1,0, 0,1, 0.1,0.1))
		{
			throw new IllegalStateException();
		}
		
		if (AbstractStem.triangleContains(0,0, 1,0, 0,1, 0.9,0.8))
		{
			throw new IllegalStateException();
		}
	}

}
