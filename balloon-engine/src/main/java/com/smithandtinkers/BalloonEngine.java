/**
 * Copyleft by Dave Horlick
 */

package com.smithandtinkers;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.geom.ConcreteStem;
import com.smithandtinkers.geom.SuperEllipsePerch;
import com.smithandtinkers.layout.Balloon;
import com.smithandtinkers.svg.SvgFlavor;
import com.smithandtinkers.svg.SvgOutputter;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * A utility class for creating word and thought balloons.
 *
 * @author dhorlick
 */
public class BalloonEngine
{
	public static final double DEFAULT_FOCUS_STEM_INCLINATION_IN_RADIANS = 0.2;
	public static final double DEFAULT_ROOT_STEM_INCLINATION_IN_RADIANS = 0.25;

	private final static double DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT = 130.0;

	public static void main(String [] args) throws FileNotFoundException
	{
		final BalloonEngine balloonEngine = new BalloonEngine();

		if (args.length==0)
		{
			balloonEngine.printUsage();
			return;
		}

		String outputFilePath = null;
		double x = 0.0, y = 0.0, w = 0.0, h = 0.0;

		int actionsTaken = 0;

		for (int i=0; i<args.length; i++)
		{
			final String arg = args[i];

			if (arg.startsWith("-"))
			{
				final String paramStr = arg.substring(1);
				final int colonLoc = paramStr.indexOf(':');
				if (colonLoc!=paramStr.lastIndexOf(':'))
					throw new IllegalArgumentException("Encountered multiple colons in parameter string: "+paramStr);
				final String param = paramStr.substring(0, colonLoc);
				final String valueStr = paramStr.substring(colonLoc+1);

				if ("o".equals(param))
					outputFilePath = valueStr;
				else
				{
					final double value = Double.parseDouble(valueStr);

					if ("x".equals(param))
						x = value;
					else if ("y".equals(param))
						y = value;
					else if ("w".equals(param))
						w = value;
					else if ("h".equals(param))
						h = value;
					else
					{
						throw new UnsupportedOperationException("Unrecognized parameter: "+param);
					}
				}
			}
			else if ("word".equals(arg))
			{
				balloonEngine.printParams(x,y,w,h);
				balloonEngine.svgStraightStemWordBalloon(outputFilePath, 1.0f, x, y, w, h);
				actionsTaken++;
			}
			else
			{
				throw new UnsupportedOperationException("Command not recognized: "+arg);
			}
		}

		switch (actionsTaken)
		{
			case 0:
				System.out.println("No actions taken.");
				break;
			case 1:
				break;
			default:
				System.out.print(actionsTaken);
				System.out.println(" actions taken.");
				break;
		}
	}

	private void printUsage()
	{
		System.out.println("Welcome to libballoon!\n");

		System.out.println("java -jar libballoon.jar -x:100 -y:100 -w:70 -h:50 -o:out.svg word");
	}

	public void svgStraightStemWordBalloon(final String outputFilePath, final float svgVersion,
			final double centerX, final double centerY, final double width, final double height) throws FileNotFoundException
	{
		final SvgFlavor svgFlavor;
		
		if (svgVersion==1.0 || svgVersion==1.1)
			svgFlavor = SvgFlavor.SVG_1_0;
		else if (svgVersion==1.2)
			svgFlavor = SvgFlavor.SVG_1_2;
		else
			throw new IllegalArgumentException("Unrecognized SVG version: "+svgVersion+". Try 1.0 or 1.2");

		final Balloon balloon = instantiateStraightStemWordBalloon(centerX, centerY, width, height);
		final File outputFile = new File(outputFilePath);
		final FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		final SvgOutputter svgOutputter = new SvgOutputter();
		svgOutputter.outputSvg(fileOutputStream, balloon, true, svgFlavor);
	}

	public Balloon instantiateStraightStemWordBalloon(final double centerX, final double centerY, final double width, final double height)
	{
		return instantiateStraightStemWordBalloon(centerX, centerY, width, height, centerX, centerY + DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT);
	}

	public Balloon instantiateStraightStemWordBalloon(final double centerX, final double centerY, final double width, final double height,
			final double stemFocusX, final double stemFocusY)
	{
		final Balloon balloon = new Balloon();

		SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
		superEllipsePerch.setHeinParameter(
				BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
		balloon.add(superEllipsePerch);
		ConcreteStem stem = new ConcreteStem();
		Point2D.Double focus = new Point2D.Double(stemFocusX, stemFocusY);
		stem.setFocus(focus);
		superEllipsePerch.add(stem);

		return balloon;
	}

	public Balloon instantiateCurvedStemWordBalloon(final double centerX, final double centerY, final double width, final double height)
	{
		return instantiateCurvedStemWordBalloon(centerX, centerY, width, height, centerX, centerY + DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT);
	}

	public Balloon instantiateCurvedStemWordBalloon(final double centerX, final double centerY, final double width, final double height,
			final double stemFocusX, final double stemFocusY)
	{
		final Balloon balloon = new Balloon();

		SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
		superEllipsePerch.setHeinParameter(
				BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
		balloon.add(superEllipsePerch);
		ConcreteStem stem = new ConcreteStem();
		stem.setFocusStemInclinationInRadians(DEFAULT_FOCUS_STEM_INCLINATION_IN_RADIANS);
		stem.setRootStemInclinationInRadians(DEFAULT_ROOT_STEM_INCLINATION_IN_RADIANS);
		Point2D.Double focus = new Point2D.Double(stemFocusX, stemFocusY);
		stem.setFocus(focus);
		superEllipsePerch.add(stem);

		return balloon;
	}

	public Balloon instantiateThoughtBalloon(final double centerX, final double centerY, final double width, final double height)
	{
		return instantiateThoughtBalloon(centerX, centerY, width, height, centerX, centerY + DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT);
	}

	public Balloon instantiateThoughtBalloon(final double centerX, final double centerY, final double width, final double height,
			final double stemFocusX, final double stemFocusY)
	{
		return instantiateThoughtBalloon(centerX, centerY, width, height, stemFocusX, stemFocusY,
				DEFAULT_FOCUS_STEM_INCLINATION_IN_RADIANS, DEFAULT_ROOT_STEM_INCLINATION_IN_RADIANS,
				DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT,
				BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
	}

	public Balloon instantiateThoughtBalloon(final double centerX, final double centerY, final double width, final double height,
			final double stemFocusX, final double stemFocusY,
			final double focusStemInclinationInRadians, final double rootStemInclinationInRadians,
			final double verticalStemFocusDisplacement,
			final double heinParameter)
	{
		final Balloon balloon = new Balloon();

		final SuperEllipsePerch tb = new SuperEllipsePerch();
		tb.getPunctedSuperEllipse().rufflePrettily();
		tb.getPunctedSuperEllipse().setHeinParameter(heinParameter);
		final ConcreteStem stem = new ConcreteStem();
		stem.setType(AbstractStem.BUBBLED_TYPE);
		stem.setFocusStemInclinationInRadians(DEFAULT_FOCUS_STEM_INCLINATION_IN_RADIANS);
		stem.setRootStemInclinationInRadians(DEFAULT_ROOT_STEM_INCLINATION_IN_RADIANS);
		final Point2D.Double focus = new Point2D.Double(centerX, centerY + DEFAULT_VERTICAL_STEM_FOCUS_DISPLACEMENT);
		stem.setFocus(focus);
		tb.add(stem);
		balloon.add(tb);

		return balloon;
	}

	private void printParams(final double x, final double y, final double w, final double h)
	{
		System.out.print("x: ");
		System.out.println(x);
		System.out.print("y: ");
		System.out.println(y);
		System.out.print("w: ");
		System.out.println(w);
		System.out.print("h: ");
		System.out.println(h);
	}
}
