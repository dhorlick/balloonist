/**
 Copyleft 2005 by Dave Horlick
 */
 
package com.smithandtinkers.gui;

import com.smithandtinkers.util.TypesafeList;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;


/**
 <p>java.awt.FileDialog is clearly not the strongest AWT offering. But JFileChooser is
 decidedly Windows-like, so here we are.</p>
 
 @author Dave Horlick
 */
public class BetterFileDialog extends FileDialog
{
	private List optionalFileExtensions = new TypesafeList(String.class);
	
	public BetterFileDialog(Frame parent)
	{
		super(parent);
	}

	public BetterFileDialog(Frame parent, String title)
	{
		super(parent, title);
	}
	
	public BetterFileDialog(Frame parent, String title, int mode)
	{
		super(parent, title, mode);
	}
	
	public BetterFileDialog(Frame parent, String title, int mode, String designatedFileExtension)
	{
		super(parent, title, mode);
		setOptionalFileExtension(designatedFileExtension);
	}
	
	/**
	 * @return the first and preferred of the optional file extensions, or null if there aren't any.
	 */
	public String getOptionalFileExtension()
	{
		if (optionalFileExtensions.size()==0)
			return null;
		else
			return (String) optionalFileExtensions.get(0);
	}
	
	/**
	 * leading periods are unnecessary and will be removed if provided.
	 *
	 * file extension will be mapped to lower case.
	 */
	public void setOptionalFileExtension(String designatedFileExtension)
	{
		if (designatedFileExtension!=null)
		{
			while (designatedFileExtension.length()>0 && designatedFileExtension.startsWith("."))
			{
				designatedFileExtension = designatedFileExtension.substring(1);
			}
			
			if (optionalFileExtensions.size()>0)
				optionalFileExtensions.clear();
			
			optionalFileExtensions.add(designatedFileExtension.toLowerCase());
		}
		
		updateFilenameFilter();
	}
	
	private void updateFilenameFilter()
	{
		if (optionalFileExtensions.size()==0)
		{
			setFilenameFilter(null);
		}
		else
		{
			setFilenameFilter(new FilenameFilter() 
			{
				public boolean accept(File dir, String name)
				{
					if (name==null)
						return false;
					
					final String lowerCased = name.toLowerCase();
					
					// return (name!=null && name.toLowerCase().endsWith("."+optionalFileExtension));
					
					Iterator walk = optionalFileExtensions.iterator();
					
					while (walk.hasNext())
					{
						String theExtension = (String) walk.next();
						if (lowerCased.endsWith(theExtension))
							return true;
					}
					
					return false;
				}
			});
		}
	}
	
	/**
	 <p>Returns a File, or a null.</p>
	 */
	public File getSelectedFile()
	{
		String resultPath = getDirectory();
		String resultFile = getFile();
		if(resultPath != null && resultPath.length()!= 0 && resultFile != null && resultFile.length() != 0)
		{
			StringBuffer both = new StringBuffer(255);
			both.append(resultPath);
			both.append(resultFile);
			
			if (getOptionalFileExtension()!=null && getMode()==FileDialog.SAVE)
			{
				final String DOTTED_FILE_EXTENSION = "."+getOptionalFileExtension();
				
				if (!resultFile.toLowerCase().endsWith(DOTTED_FILE_EXTENSION))
				{
					both.append(DOTTED_FILE_EXTENSION);
				}
			}
			
			File file = new File(both.toString());
			return file;
		}
		else
		{
			return null;
		}
	}
	
	public void addOptionalFileExtension(String designatedFileExtension)
	{
		optionalFileExtensions.add(designatedFileExtension);
		
		updateFilenameFilter();
	}
	
	public List getOptionalFileExtensions()
	{
		return optionalFileExtensions;
	}
}
