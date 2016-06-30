/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

/**
 * Helps keep track of progress while constructing stems.
 *
 * @author dhorlick
 */
public class StemConstructionContext
{
	private ConcreteStem stem;
	private DrawingState drawingState = ON_PERIMETER;
	
	public static final DrawingState ON_PERIMETER = new DrawingState();
	public static final DrawingState LEADING_EDGE = new DrawingState();
	public static final DrawingState IN_STEM = new DrawingState();
	public static final DrawingState TRAILING_EDGE = new DrawingState();
	
	public StemConstructionContext()
	{
	}
	
	protected static class DrawingState
	{
	}

	public DrawingState getDrawingState()
	{
		return drawingState;
	}

	public void setDrawingState(DrawingState designatedDrawingState)
	{
		drawingState = designatedDrawingState;
	}

	public ConcreteStem getStem()
	{
		return stem;
	}

	public void setStem(ConcreteStem designatedStem)
	{
		stem = designatedStem;
	}
	
	public void reset()
	{
		stem = null;
		drawingState = ON_PERIMETER;
	}
}
