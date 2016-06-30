/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import java.util.Iterator;
import javax.swing.JSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author dhorlick
 */
public abstract class Averager
{
	private Class targetClass;
	
	public Averager(Class designatedTargetClass)
	{
		setTargetClass(designatedTargetClass);
	}
	
	private void setTargetClass(Class designatedTargetClass)
	{
		targetClass = designatedTargetClass;
	}

	/**
	 * @param iterator an Iterator over Objects
	 */
	public double average(Iterator iterator)
	{
		int tallied = 0;
		double runningTotal = 0.0;
		
		while (iterator.hasNext())
		{
			Object item = iterator.next();
			
			if (item!=null && targetClass.isAssignableFrom(item.getClass()))
			{
				runningTotal += tally(item);
				tallied++;
			}
		}
		
		if (tallied==0)
			return 0.0;
		else
			return runningTotal/((double)tallied);
	}
	
	/**
	 * @param iterator an Iterator over Objects
	 */
	public void averageTo(Iterator iterator, JSlider slider, double minimumPossibleValue, double maximumPossibleValue)
	{
		validateRange(minimumPossibleValue, maximumPossibleValue);
		
		if (slider==null)
			throw new IllegalArgumentException("Slider not provided.");
		
		double average = average(iterator);
		mapValueToSlider(average, slider, minimumPossibleValue, maximumPossibleValue);
	}
	
	public static void mapValueToSlider(double newValue, JSlider slider, double minimumPossibleValue, double maximumPossibleValue)
	{		
		int sliderRange = slider.getMaximum() - slider.getMinimum();
		double selectableRange = maximumPossibleValue - minimumPossibleValue;
		
		double normalizedAverage = (newValue-minimumPossibleValue)/selectableRange;
		double newSliderValue = sliderRange*normalizedAverage + slider.getMinimum();
		slider.setValue( (int)newSliderValue );
	}
	
	public static void validateRange(double minimumPossibleValue, double maximumPossibleValue)
	{
		if (maximumPossibleValue<minimumPossibleValue)
			throw new IllegalArgumentException("minimum: "+minimumPossibleValue+" exceeds maximum: "+maximumPossibleValue);
		
		if (maximumPossibleValue==minimumPossibleValue)
			throw new IllegalArgumentException("minimum and maximum values are the same: "+maximumPossibleValue);
	}
	
	/**
	 * Given a JSlider, determines the slider position fraction
	 *
	 * @return a value in the range 0.0 to 1.0
	 */
	public static double determineSliderFraction(JSlider slider)
	{
		int sliderRange = (slider.getMaximum() - slider.getMinimum());
		int dimensionalPosition = (slider.getValue()-slider.getMinimum());
		return (double)dimensionalPosition/(double)sliderRange;
	}
	
	public static void setSliderFraction(JSlider slider, double designatedFraction)
	{
		if (slider==null)
			return;
		
		final int sliderRange = (slider.getMaximum() - slider.getMinimum());
		
		slider.setValue(slider.getMinimum() + (sliderRange/2));
	}
	
	public static double mapValueFromSlider(JSlider slider, double minimumPossibleValue, double maximumPossibleValue)
	{
		validateRange(minimumPossibleValue, maximumPossibleValue);
		
		double valueAsFraction = determineSliderFraction(slider);
		double range = maximumPossibleValue - minimumPossibleValue;
		
		return minimumPossibleValue + valueAsFraction * range;
	}
	
	/**
	 * @param iterator an Iterator over Objects
	 */
	public double total(Iterator iterator)
	{
		double runningTotal = 0.0;
		
		while (iterator.hasNext())
		{
			Object item = iterator.next();
			
			if (item!=null && targetClass.isAssignableFrom(item.getClass()))
			{
				runningTotal += tally(item);
			}
		}
		
		return runningTotal;
	}
	
	public abstract double tally(Object object);
}