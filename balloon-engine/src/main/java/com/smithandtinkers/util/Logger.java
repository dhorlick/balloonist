/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.util;

import java.io.PrintStream;
import java.util.regex.Pattern;

/**
 * A placeholder.
 *
 * @author dhorlick
 */
public class Logger
{
	private static PrintStream out = System.out;
	
	private static Object pattern;
		// typed as Object to avoid ClassNotFoundException under Java 1.3
	
	private static boolean atNewline = true;
	
	private final static Throwable stackSource = new Throwable();

    private static boolean logEverything = false;
	
    static
	{
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		{
			final String rlogclassAsString = System.getProperty("rlogclass");
			
			if (rlogclassAsString!=null && rlogclassAsString.length()>0)
			{
				System.out.println("rlogclass="+rlogclassAsString);			
				pattern = Pattern.compile(rlogclassAsString);
			}
		}
	}	
	
	public static void println()
	{
		if (isOn())
		{
			out.println();
			atNewline = true;
		}
	}
	
	public static void println(double val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}
	
	public static void println(int val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}

	public static void println(float val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}

	public static void println(boolean val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}

	public static void println(char val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}

	public static void println(String val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}

	public static void println(Object val)
	{
		if (isOn())
		{
			out.println(val);
			atNewline = true;
		}
	}
	
	public static void print(double val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}
	
	public static void print(int val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}

	public static void print(float val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}

	public static void print(boolean val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}

	public static void print(char val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}
	
	public static void print(String val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}
	
	public static void print(Object val)
	{
		if (isOn())
		{
			out.print(val);
			atNewline = false;
		}
	}
	
	public static boolean isOn()
	{
		if (isLogEverything())
            return true;

        if (pattern!=null)
		{
			stackSource.fillInStackTrace();
			
			if ("com.smithandtinkers.util.Logger".equals(stackSource.getStackTrace() [1].getClassName()))
			{
				final StackTraceElement origin = stackSource.getStackTrace() [2];
				
				// System.out.println("st="+java.util.Arrays.asList(stackTrace));
				// System.out.println("origin="+origin);
				
				final Pattern asPattern = (Pattern) pattern;
				final boolean match = (asPattern.matcher(origin.getClassName()).matches());
				
				if (match & atNewline)
				{
					System.out.print(origin.toString());
					System.out.print(": ");
				}
				
				return match;
			}
		}
		
		return false;
	}
	
	public static void print(PrintStream ps, float [] array)
	{
		if (isOn())
		{
			ps.print("float [");
			
			for (int index=0; index<=array.length-1; index++)
			{
				ps.print(array[index]);
				if (index<array.length-1)
					ps.print(", ");
			}
			
			ps.print("]");
			atNewline = false;
		}
	}
	
	public static void println(PrintStream ps, float [] array)
	{
		if (isOn())
		{
			print(ps, array);
			ps.println();
			atNewline = true;
		}
	}
	
	public static void print(float [] array)
	{
		if (isOn())
		{
			print(out, array);
			atNewline = false;
		}
	}
	
	public static void println(float [] array)
	{
		if (isOn())
		{
			println(out, array);
			atNewline = true;
		}
	}
	
	public static void print(PrintStream ps, double [] array)
	{
		if (isOn())
		{
			ps.print("double [");
			
			for (int index=0; index<=array.length-1; index++)
			{
				ps.print(array[index]);
				if (index<array.length-1)
					ps.print(", ");
			}
			
			ps.print("]");
			
			atNewline = false;
		}
	}
	
	public static void println(PrintStream ps, double [] array)
	{
		if (isOn())
		{
			print(ps, array);
			ps.println();
			atNewline = true;
		}
	}
	
	public static void print(double [] array)
	{
		if (isOn())
			print(out, array);
	}
	
	public static void println(double [] array)
	{
		if (isOn())
			println(out, array);
	}

    public static boolean isLogEverything()
    {
        return logEverything;
    }

    public static void setLogEverything(final boolean designatedLogEverything)
    {
        logEverything = designatedLogEverything;
    }
}
