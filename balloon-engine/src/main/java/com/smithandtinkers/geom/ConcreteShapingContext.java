/*
 * ConcreteShapingContext.java
 *
 * Created on December 11, 2006, 8:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.smithandtinkers.geom;

/**
 * A simple implementation of {@link ShapingContext}.
 *
 * @author dhorlick
 */
public class ConcreteShapingContext implements ShapingContext
{	
	private boolean intermediate;
	
	public boolean isIntermediate()
	{
		return intermediate;
	}
	
	public void setIntermediate(boolean designatedIntermediate)
	{
		intermediate = designatedIntermediate;
	}
}
