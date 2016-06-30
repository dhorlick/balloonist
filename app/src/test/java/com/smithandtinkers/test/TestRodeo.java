/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.test;

import com.smithandtinkers.util.BugException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dhorlick
 */
public abstract class TestRodeo
{
	public static boolean test(String className)
	{
		try
		{
			test(Class.forName(className));
			return true;
		}
		catch (Exception exception)
		{
			System.err.println(exception.getMessage());
			exception.printStackTrace();
			return false;
		}
	}
	
	public static void test(Class myClass) throws Exception
	{
        if (!Runnable.class.isAssignableFrom(myClass))
            throw new IllegalAccessException("Input class does not extend Runnable: "+myClass);
        
        Object object = myClass.newInstance();

        Runnable runnable = (Runnable) object;
        runnable.run();
	}
	
	public static void main(String [] args)
	{
		if (args.length==0)
		{
			printUsage();
			return;
		}
		
		boolean packageMode = false;
        int testsRun = 0;
		
		for (int loop=0; loop<=args.length-1; loop++)
		{	
            if ("-help".equals(args[loop]))
			{
				printUsage();
				return;
			}
			else if (args[loop].startsWith("-"))
			{
				throw new IllegalArgumentException("Unrecognized option: " + args[loop]);
			}
			else
			{
                if (test(args[loop]))
                    System.out.println("Success.");
                else
                    throw new IllegalStateException("Failure.");

                testsRun++;
			}
		}

        if (testsRun==0)
        {
            StringBuffer problem = new StringBuffer();
            problem.append("Must provide ");

            if (packageMode)
                problem.append("package");
            else
                problem.append("class");

            problem.append(" name.");

            throw new IllegalArgumentException(problem.toString());
        }
	}
	
	public static void printUsage()
	{
		System.out.println("java com.smithandtinkers.test.TestRodeo [-c | p] test-class-or-package-name");
		System.out.println("\twhere -c indicates the use of a class name (the default).");
		System.out.println("\t  and -p indicates the use of a package name.");
	}
	
	public static class TestResults implements Serializable
	{
		private List successes = new ArrayList();
		private Map failures = new HashMap();
		
		TestResults()
		{
		}
		
		public List getSuccesses()
		{
			return Collections.unmodifiableList(successes);
		}
		
		/**
		 @return a Map with Class keys and Exception values 
		 */
		public Map getFailures()
		{
			return Collections.unmodifiableMap(failures);
		}
		
		public void registerFailure(String theRunnableClass, Exception problem)
		{
			// makeSureImplementsRunnable(theRunnableClass);
			
			failures.put(theRunnableClass, problem);
		}
		
		public void registerSuccess(String theRunnableClass)
		{
			// makeSureImplementsRunnable(theRunnableClass);
			
			successes.add(theRunnableClass);
		}
		
		public final void makeSureImplementsRunnable(Class theRunnableClass)
		{
			if (!theRunnableClass.isAssignableFrom(Runnable.class))
			{
				throw new BugException("class argument does not implement runnable: " + theRunnableClass.getName());
			}
		}
		
		public int total()
		{
			return successes.size() + failures.size();
		}
		
		public String grade()
		{
			int total = total();
			
			if (total==0)
				return "No tests found.";
			
			int score = 100 * successes.size() / total();
			
			String letterGrade = null;
			
			if (score>=97)
				letterGrade = "A+";
			else if (score>=93)
				letterGrade = "A";
			else if (score>=90)
				letterGrade = "A-";
			else if (score>=87)
				letterGrade = "B+";
			else if (score>=83)
				letterGrade = "B";
			else if (score>=80)
				letterGrade = "B-";
			else if (score>=77)
				letterGrade = "C+";
			else if (score>=73)
				letterGrade = "C";
			else if (score>=70)
				letterGrade = "C-";
			else if (score>=67)
				letterGrade = "D+";
			else if (score>=63)
				letterGrade = "D";
			else if (score>=60)
				letterGrade = "D-";
			else
				letterGrade = "F";
				
			return score + "% " + letterGrade;
		}
		
		public String describe()
		{
			StringBuffer desc = new StringBuffer();
			
			desc.append("Ran ");
			desc.append(total());
			desc.append(" tests. ");
			desc.append(successes.size());
			desc.append(" successes, ");
			desc.append(failures.size());
			desc.append(" failures. ");
			
			desc.append(grade());
			
			if (failures.size() > 0)
			{
				desc.append("\n Failure detail: [ ");
				
				Iterator walkFailures = failures.entrySet().iterator();
				
				while (walkFailures.hasNext())
				{
					Map.Entry entry = (Map.Entry) walkFailures.next();
					
					desc.append("\tclass: ");
					desc.append(entry.getKey());
					desc.append(", problem: ");
					
					Exception exception = (Exception) entry.getValue();
					desc.append(exception.getClass().getName());
					desc.append(" | ");
					desc.append(exception.getMessage());
					
					if (walkFailures.hasNext())
					{
						desc.append("\n");
					}
				}
				desc.append(" ]");

			}			

			return desc.toString();
		}
	}
}