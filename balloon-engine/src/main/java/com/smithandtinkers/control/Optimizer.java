/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.control;

import java.util.Arrays;


/**
 * Performs one-dimensional control optimizations.
 *
 * @author dhorlick
 */
public class Optimizer
{
	private double minX = 0.0;
	private double maxX = 10.0;
	private double stepX = 1.0;
	
	private double [] tolerance = new double[1];
	
	/**
	 * records the lowest error encountered for each channel
	 */
	private double [] lowestError;
	
	public Optimizer()
	{
	}
	
	public Optimizer(double designatedMinX, double designatedMaxX, double designatedStepX)
	{
		this();
		setMinX(designatedMinX);
		setMaxX(designatedMaxX);
		setStepX(designatedStepX);
	}
	
	
	public double getMaxX()
	{
		return maxX;
	}
	
	public void setMaxX(double maxX)
	{
		this.maxX = maxX;
	}
	
	public double getMinX()
	{
		return minX;
	}
	
	public void setMinX(double minX)
	{
		this.minX = minX;
	}
	
	public double getStepX()
	{
		return stepX;
	}
	
	public void setStepX(double stepX)
	{
		this.stepX = stepX;
	}
	
	/**
	 * Given a one-dimensional controllable, find the value x that minimizes
	 * error signal.
	 */
	public double optimize(Controllable controllable)
	{
		// long startTime = System.currentTimeMillis();
		reset();
		double bestX = 0.0;
		
		double x=minX = 0.0;
		
		int usable = 0;
		
		for (x=minX; x<=maxX; x+=stepX)
		{
			controllable.input(x);
			
			// double err = Math.abs(controllable.getError());
			final double [] errorMeasurement = controllable.getError();
			
			boolean wayside = false;
			
			for (int index = 0; index<=tolerance.length-1 && !wayside; index++ )
			{
				final double err = Math.abs(errorMeasurement[index]);
				
				// System.out.println("err="+err+ " @ x="+x);
				
				if (x==minX || err<=lowestError[index] || (err<=tolerance[index] && index<tolerance.length))
				{
					if (x==minX || err<=lowestError[index])
					{
						lowestError[index] = err;
						// System.out.println("index: "+index+" Setting lowestErr to " + err + " @ x="+x);
					}
				}
				else
				{
					// System.out.println("falling; x: "+x+", err: "+err+ ", lowestError["+index+"]="+lowestError[index]);
					wayside = true;
				}
			}
			
			if (!wayside)
			{
				bestX = x;
				// System.out.println("bester x: "+bestX);
			}

			// System.out.println(System.currentTimeMillis()+" usable="+usable);
		}
		
		// System.out.println("bestX="+bestX+", lowestErr="+lowestError[0]+"\n");
		
		// long durationInMillis = System.currentTimeMillis() - startTime;
		// System.out.println("optmization took "+durationInMillis+" ms.");
		
		if (x!=bestX)
			controllable.input(bestX);
		
		return bestX;
	}

	/**
	 * @return double[] an array specify the (positive) tolerances allowable/desirable for
	 * each error channel.
	 */
	public double[] getTolerance()
	{
		return tolerance;
	}
	
	/**
	 * @param designatedTolerance an array specify the (positive) tolerances allowable/desirable
	 * for each error channel.
	 */
	public void setTolerance(final double[] designatedTolerance)
	{
		tolerance = designatedTolerance;
	}
	
	private void reset()
	{
		if (lowestError==null)
		{
			if (tolerance!=null)
			{
				lowestError = new double[tolerance.length];
			}
		}
		else
		{
			Arrays.fill(lowestError, 0.0);
		}
	}
}
