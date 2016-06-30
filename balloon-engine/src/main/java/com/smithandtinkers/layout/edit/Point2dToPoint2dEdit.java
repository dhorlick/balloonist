/**
 Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

public abstract class Point2dToPoint2dEdit extends SelectionEdit
{
	public Point2dToPoint2dEdit(String designatedPresentationName)
	{
		super(designatedPresentationName);
	}

	protected double oldX, oldY, newX, newY;
	
	public double getNewX()
	{
		return newX;
	}

	public void setNewX(double designatedNewX)
	{
		newX = designatedNewX;
	}

	public double getNewY()
	{
		return newY;
	}

	public void setNewY(double designatedNewY)
	{
		newY = designatedNewY;
	}

	public double getOldX()
	{
		return oldX;
	}

	public void setOldX(double designatedOldX)
	{
		oldX = designatedOldX;
	}

	public double getOldY()
	{
		return oldY;
	}

	public void setOldY(double designatedOldY)
	{
		oldY = designatedOldY;
	}

	private double computeDeltaX()
	{
		return newX - oldX;
	}

	private double computeDeltaY()
	{
		return newY - oldY;
	}
	
	public boolean hasEffect()
	{
		if (computeDeltaX()==0 && computeDeltaY()==0)
			return false;
		else
			return true;
	}
}
