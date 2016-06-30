/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.layout.BalloonistPreferences;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author dhorlick
 */
public class Ruler
{
	private double length = 100.0;
	
	private boolean vertical = false;
	private Point2D origin = new Point2D.Double();
	
	private int smallTickSpacingInPoints = 10;
	private int bigTickSpacingInPoints = 50;
	
	private double smallTickLengthInPoints = 4.0;
	private double bigTickLengthInPoints = 8.0;
	
	private Font font;
	
	private double scale = 1.0;
	
	private static AffineTransform rotateNinetyDegrees;
	{
		rotateNinetyDegrees = new AffineTransform();
			rotateNinetyDegrees.setToRotation( Math.PI / 2.0 );
	}
	
	public Ruler()
	{
		font = BalloonistPreferences.determineSanSerifFont().deriveFont(6.0f);		
	}

	public int draw(DrawingContext drawingContext)
	{
		// draw big tick marks
		
		final Line2D line = new Line2D.Double();
		drawingContext.setColor(Color.darkGray);
		
		double units = 0.0;
		
		final double littleSpacing = getSmallTickSpacingInPoints();
		final double bigSpacing = littleSpacing * 5.0;
		
		for (double points=0; points<=getLength(); points+=bigSpacing*getScale())
		{
			if (isVertical())
				line.setLine(getOrigin().getX(), getOrigin().getY()+points, 
						getOrigin().getX()+getBigTickLengthInPoints(), getOrigin().getY()+points);
			else
				line.setLine(getOrigin().getX()+points, getOrigin().getY(), 
						getOrigin().getX()+points, getOrigin().getY()+getBigTickLengthInPoints());
			
			drawingContext.draw(line);
			
			// set an appropriately small font
			
			final GlyphVector glyphVector 
					= font.createGlyphVector(drawingContext.getFontRenderContext(), 
					String.valueOf(units));
			
			if (!isVertical())
			{
				setGlyphTransform(glyphVector, rotateNinetyDegrees);
			
				drawingContext.drawGlyphVector(glyphVector, 
						(float)(getOrigin().getX()+points), 
						(float)(getOrigin().getY()+1.5f*getBigTickLengthInPoints()));
			}
			else
			{
				drawingContext.drawGlyphVector(glyphVector, 
						(float)(getOrigin().getX()+1.5f*getBigTickLengthInPoints()), 
						(float)(getOrigin().getY()+points));
			}
			
			units += bigSpacing;
		}
		
		// draw small tick marks
		
		for (double u=0; u<=getLength(); u+=littleSpacing*getScale())
		{
			if (u % getBigTickSpacingInPoints() != 0)
			{			
				if (isVertical())
					line.setLine(getOrigin().getX(), getOrigin().getY()+u, getOrigin().getX()+getSmallTickLengthInPoints(), getOrigin().getY()+u);
				else
					line.setLine(getOrigin().getX()+u, getOrigin().getY(), getOrigin().getX()+u, getOrigin().getY()+getSmallTickLengthInPoints());

				drawingContext.draw(line);
			}
		}
		
		return 0;
	}

	public double getLength()
	{
		return length;
	}

	public void setLength(double designatedLength)
	{
		if (designatedLength!=length)
		{
			if (designatedLength<0)
				throw new IllegalArgumentException("Must be positive: "+designatedLength);
			
			length = designatedLength;
		}
	}

	public boolean isVertical()
	{
		return vertical;
	}

	public void setVertical(boolean vertical)
	{
		this.vertical = vertical;
	}

	public Point2D getOrigin()
	{
		return origin;
	}

	public void setOrigin(Point2D origin)
	{
		this.origin = origin;
	}

	public double getSmallTickSpacingInPoints()
	{
		double prospectiveSmallTicksSpacing = 10.0 / getScale();
		
		// System.out.println("prospectiveSmallTicksSpacing="+prospectiveSmallTicksSpacing);
		
		double usableSmallTicksSpacing = 10 * ((int)(prospectiveSmallTicksSpacing / 10));
		if (usableSmallTicksSpacing==0.0)
			usableSmallTicksSpacing = 1.0;
		
		// System.out.println("\tusableSmallTicksSpacing="+usableSmallTicksSpacing);
		
		return usableSmallTicksSpacing;
	}

	public double getBigTickSpacingInPoints()
	{
		return 5.0 * getSmallTickSpacingInPoints();
	}
	
	public double getSmallTickLengthInPoints()
	{
		return smallTickLengthInPoints;
	}

	public void setSmallTickLengthInPoints(double designatedSmallTickLength)
	{
		smallTickLengthInPoints = designatedSmallTickLength;
	}

	public double getBigTickLengthInPoints()
	{
		return bigTickLengthInPoints;
	}

	public void setBigTickLengthInPoints(double designatedBigTickLength)
	{
		bigTickLengthInPoints = designatedBigTickLength;
	}
	
	public static void setGlyphTransform(GlyphVector glyphVector, AffineTransform affineTransform)
	{
		for (int index=0; index<=glyphVector.getNumGlyphs()-1; index++)
		{
			glyphVector.setGlyphTransform(index, affineTransform);
		}
	}

	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}
}
