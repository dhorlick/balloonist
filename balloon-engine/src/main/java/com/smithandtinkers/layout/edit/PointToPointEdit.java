/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;

public abstract class PointToPointEdit extends SelectionEdit
{
	public PointToPointEdit(String designatedPresentationName)
	{
		super(designatedPresentationName);
	}

	protected int oldX, oldY, newX, newY;
	
	public int getNewX()
	{
		return newX;
	}

	public void setNewX(int designatedNewX)
	{
		newX = designatedNewX;
	}

	public int getNewY()
	{
		return newY;
	}

	public void setNewY(int designatedNewY)
	{
		newY = designatedNewY;
	}

	public int getOldX()
	{
		return oldX;
	}

	public void setOldX(int designatedOldX)
	{
		oldX = designatedOldX;
	}

	public int getOldY()
	{
		return oldY;
	}

	public void setOldY(int designatedOldY)
	{
		oldY = designatedOldY;
	}

	private int computeDeltaX()
	{
		return newX - oldX;
	}

	private int computeDeltaY()
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
