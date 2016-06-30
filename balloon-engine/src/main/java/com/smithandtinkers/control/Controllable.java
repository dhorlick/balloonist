/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.control;

/**
 * Models a one-dimensional system.
 *
 * @author Dave Horlick
 */
public interface Controllable
{
	/**
	 Returns a number that quantifies how far off the controller
	 is from its target.
	 */
	public double [] getError();
	public void input(double designatedInput);
}