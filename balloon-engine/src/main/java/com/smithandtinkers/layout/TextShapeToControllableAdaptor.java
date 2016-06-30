/**
 Copyleft Aug 16, 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

import java.awt.Shape;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.control.Controllable;

/**
 * Permits {@link TextShape}s to be layed-out by the application of system optimization.
 *
 * @author dhorlick
 */
public class TextShapeToControllableAdaptor implements Controllable
{
	private TextShape textShape;
	private DrawingContext drawingContext;
	private Shape actual; // TODO it's kind of messy to have this *and* retrieveAlignable(). can they be consolidated without sacrificing efficiency?
	private int sectionIndex;
	
	private double [] misalignment = new double[2];
	
	public TextShapeToControllableAdaptor()
	{
	}
		
	public TextShape getTextShape()
	{
		return textShape;
	}
	
	public void setTextShape(TextShape designatedTextShape)
	{
		textShape = designatedTextShape;
	}
	
	public DrawingContext getDrawingContext()
	{
		return drawingContext;
	}
	
	public void setDrawingContext(DrawingContext designatedDrawingContext)
	{
		drawingContext = designatedDrawingContext;
	}
	
	/**
	 * @return Returns the actual shape.
	 */
	public Shape getActual()
	{
		return actual;
	}
	
	/**
	 * @param designatedActual The actual shape to set.
	 */
	public void setActual(Shape designatedActual)
	{
		actual = designatedActual;
	}
	
	/**
	 * @see com.smithandtinkers.control.Controllable#getError()
	 */
	public double[] getError()
	{
		zeroOutMisalignment();
		textShape.drawText(drawingContext, false, actual, misalignment, null, sectionIndex);
		return misalignment; // TODO consider both elements
	}

	/* (non-Javadoc)
	 * @see com.smithandtinkers.control.Controllable#input(double)
	 */
	public void input(double designatedInput)
	{
		retrieveAlignable().setGapBetweenMarginAndText( designatedInput );
	}

	public int getSectionIndex()
	{
		return sectionIndex;
	}

	public void setSectionIndex(int designatedSectionIndex)
	{
		sectionIndex = designatedSectionIndex;
	}
	
	public Alignable retrieveAlignable()
	{
		if (textShape.get(sectionIndex)!=null && textShape.get(sectionIndex) instanceof Alignable)
			return (Alignable) textShape.get(sectionIndex);
		else
			return null;
	}
	
	private void zeroOutMisalignment()
	{
		for (int index=0; index<=misalignment.length-1; index++)
			misalignment[index] = 0;
	}
}
