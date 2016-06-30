/*
 Copyleft 2004 by Dave Horlick
*/

package com.smithandtinkers;

import java.io.File;

import com.smithandtinkers.io.FileList;
import com.smithandtinkers.io.FileOpener;
import com.smithandtinkers.text.ITextStyleStrategy;
import com.smithandtinkers.util.*;


public class Balloonist
{
	private static Balloonist INSTANCE = new Balloonist();

	private static final int CREATOR_CODE = 0x424C384E;

	public Balloonist()
	{
		if (PlatformFriend.RUNNING_ON_MAC)
			BalloonEngineState.getInstance().setPlatformStrategy(new OsXPlatformStrategy(CREATOR_CODE));

		BalloonEngineState.getInstance().setStyleStrategy(new ITextStyleStrategy());
	}

	public static Balloonist getInstance()
	{
		return INSTANCE;
	}

	public static void main(final String [] args)
	{
		if ((args.length==1) && ("-?".equals(args[0]) || "/?".equals(args[0]) || "-help".equals(args[0]) || "-h".equals(args[0])))
		{
			System.out.println("Run Balloonist with no arguments to run start with new artwork, or provide paths to existing artwork as parameters.");
			System.exit(0);
		}

		System.out.println("\nBalloonist Starting. "+new java.util.Date());
		
		try
		{
			Class appClass = Class.forName("com.smithandtinkers.balloonist.BalloonistApplication");
			final FileOpener fileOpener = (FileOpener) appClass.newInstance();
			
			final FileList fileList = new FileList();
			
			for (int index=0; index<=args.length-1; index++)
				fileList.add(new File(args[index]));
			
			fileOpener.open(fileList);
		}
		catch (Exception ex)
		{
			throw new BugException(ex);
		}
		
	}
}
