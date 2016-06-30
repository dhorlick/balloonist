/**
 * Copyleft 2006 by Dave Horlick

 */

package com.smithandtinkers.io;

import com.smithandtinkers.util.TypesafeList;

import java.io.File;

/**
 *
 * @author dhorlick
 */
public class FileList extends TypesafeList
{
	public FileList()
	{
		super(File.class);
	}
}
