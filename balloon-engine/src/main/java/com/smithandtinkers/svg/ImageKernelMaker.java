/**
 Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.svg;

import java.text.DecimalFormat;


/**
 * Produces matrices suitable for use with {@link java.awt.image.Kernel}
 * 
 * @author dhorlick
 */
public class ImageKernelMaker
{
	/**
	 * @param w an integer of 1 or higher
	 * @param h an integer of 1 or higher
	 */
	public static float [] makeKernel(int w, int h, Function fn)
	{
		if (w<=0)
			throw new IllegalArgumentException(String.valueOf(w));
			
		if (h<=0)
			throw new IllegalArgumentException(String.valueOf(h));
		
		float [] kernel = new float[w*h];
		
		final float middleXIndex = (float)(w-1)/2f;
		final float middleYIndex = (float)(h-1)/2f;
		
		float unnormalizedMagnitude = 0f;
		
		for (int j=0; j<=h-1; j++)
		{
			for (int i=0; i<=w-1; i++)
			{
				final int correspondingIndex = flattenIndices(i,j,w);
				kernel[correspondingIndex] = fn.f(Math.abs(middleXIndex-i),
						Math.abs(middleYIndex-j));
				unnormalizedMagnitude += kernel[correspondingIndex];
			}
		}
		
		for (int index=0; index<=kernel.length-1; index++)
			kernel[index] = kernel[index] / unnormalizedMagnitude;
			
		return kernel;
	}
	
	public static interface Function
	{
		public float f(float r_x, float r_y);
	}
	
	public static abstract class AbstractFunction implements Function
	{
		private float maximumRadiusX;
		private float maximumRadiusY;
		
		public AbstractFunction(float designatedMaximumRadiusX, float designatedMaximumRadiusY)
		{
			setMaximumRadiusX(designatedMaximumRadiusX);
			setMaximumRadiusY(designatedMaximumRadiusY);
		}
		
		public float getMaximumRadiusX()
		{
			return maximumRadiusX;
		}
		
		public float getMaximumRadiusY()
		{
			return maximumRadiusY;
		}
		
		public void setMaximumRadiusX(float designatedMaximumRadiusX)
		{
			if (designatedMaximumRadiusX<=0)
				throw new IllegalArgumentException("must be greater than zero: "+designatedMaximumRadiusX);
			
			maximumRadiusX = designatedMaximumRadiusX;
		}
		
		public void setMaximumRadiusY(float designatedMaximumRadiusY)
		{
			if (designatedMaximumRadiusY<=0)
				throw new IllegalArgumentException("must be greater than zero: "+designatedMaximumRadiusY);
			
			maximumRadiusY = designatedMaximumRadiusY;
		}
		
		public abstract float f(float r_x, float r_y);
	}
	
	/**
	 * 
	 * @param w an integer of 1 or higher 
	 */
	public static int flattenIndices(int i, int j, int w)
	{
		return i + j*w;
	}
	
	public static class BoxFunction extends AbstractFunction
	{
		public BoxFunction(float designatedMaximumRadiusX, float designatedMaximumRadiusY)
		{
			super(designatedMaximumRadiusX, designatedMaximumRadiusY);
		}
		
		public float f(float r_x, float r_y)
		{
			if (r_x<=getMaximumRadiusX() && r_x>=0f
					&& r_y<=getMaximumRadiusY() && r_y>=0f)
				return 1f;
			else
				return 0f;
		}
	};
	
	public static class ConeFunction extends AbstractFunction
	{
		public ConeFunction(float designatedMaximumRadiusX, float designatedMaximumRadiusY)
		{
			super(designatedMaximumRadiusX, designatedMaximumRadiusY);
		}
		
		public float f(float r_x, float r_y)
		{
			if (r_x>=0 && r_x<=getMaximumRadiusX()
					&& r_y>=0 && r_y<=getMaximumRadiusY())
			{
				double deltaXSquared = Math.pow(getMaximumRadiusX()-r_x, 2);
				double deltaYSquared = Math.pow(getMaximumRadiusY()-r_y, 2);
				
				return (float)Math.sqrt(deltaXSquared + deltaYSquared);
			}
			else
				return 0f;
		}
	};
	
	private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	
	public static void print(float [] kernel, int w)
	{
		for (int index=0; index<=kernel.length-1; index++)
		{
			System.out.print(DECIMAL_FORMAT.format(kernel[index]));
			
			if ((index+1) % w==0)
				System.out.println();
			else
				System.out.print("  ");
		}
	}
	
	public static void print(double [] matrix, int w)
	{
		for (int index=0; index<=matrix.length-1; index++)
		{
			System.out.print(DECIMAL_FORMAT.format(matrix[index]));
			
			if ((index+1) % w==0)
				System.out.println();
			else
				System.out.print("  ");
		}
	}
}