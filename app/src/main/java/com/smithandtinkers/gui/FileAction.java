/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.TypesafeList;

import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;
import java.util.List;


/**
 * An abstract implementation of an action. When triggered, a file dialog will come up with the
 * requested title and the user will be prompted to choose a file. If the user makes a selection,
 * the selected file will be passed to the overridden {@link #processFile} method.
 *
 * 
 * @author dhorlick
 */
public abstract class FileAction extends PossibleAction
{
	private boolean saveMode = false;
	private List optionalFileExtensions = new TypesafeList(String.class);
		
	/**
	 * @param designatedTitle We'll add the trailing ellipsis if you don't.
	 * @param designatedSaveMode false: load, 
	 *        true: save
	 */
	public FileAction(String designatedTitle, boolean designatedSaveMode)
	{
		super(designatedTitle);
		setSaveMode(designatedSaveMode);
	}
	
	/**
	 * @param designatedTitle We'll add the trailing ellipsis if you don't.
	 * @param designatedSaveMode false: load, 
	 *        true: save
	 */
	public FileAction(String designatedTitle, Frame designatedFrame, boolean designatedSaveMode)
	{
		super(designatedTitle, designatedFrame);
		setSaveMode(designatedSaveMode);
	}
	
	public FileAction(String designatedTitle, Frame designatedFrame, boolean designatedSaveMode,
			String optionalFileExtension)
	{
		this(designatedTitle, designatedFrame, designatedSaveMode);
		setOptionalFileExtension(optionalFileExtension);
	}
	
	/**
	 @param designatedSaveMode false: load,
	         true: save
	 */
	public void setSaveMode(boolean designatedSaveMode)
	{
		saveMode = designatedSaveMode;
	}
	
	public boolean isSaveMode()
	{
		return saveMode;
	}
	
	public void process(int modifiers) throws Exception
	{	
		Frame frame = getOrDetermineFrame();
		
		Cursor originalCursor = null;
		
		if (frame!=null)
			originalCursor = frame.getCursor();
		
		BetterFileDialog fileDialog = new BetterFileDialog(frame);
		
		if (saveMode)
			fileDialog.setMode(FileDialog.SAVE);
		else
			fileDialog.setMode(FileDialog.LOAD);
		
		fileDialog.setTitle(getTitle());
		
		if ((modifiers & ActionEvent.ALT_MASK)!=ActionEvent.ALT_MASK)
		{
			// System.out.println("installing file filter");
			
			Iterator walk = optionalFileExtensions.iterator();
			while (walk.hasNext())
			{
				String optionalFileExtension = (String) walk.next();
				fileDialog.addOptionalFileExtension(optionalFileExtension);
			}
		}
		else
		{
			// System.out.println("skipping file filter; alt/option key is down");
		}
		
		fileDialog.setVisible(true);
		File theFile = fileDialog.getSelectedFile();

		if (theFile!=null)
		{
			try
			{
				if (frame!=null)
					frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // base class should take care of setting it back
				
				processFile(theFile);
				// customize error message maybe?
			}
			finally
			{
				if (frame!=null)
					frame.setCursor(originalCursor);
			}
		}
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
	}
	
	public void addOptionalFileExtension(String designatedFileExtension)
	{
		optionalFileExtensions.add(designatedFileExtension);
	}
	
	public List getOptionalFileExtensions()
	{
		return optionalFileExtensions;
	}
	
	/**
	 * Override this to do something useful with the file.
	 * 
	 * @param theFile The file the user chose.
	 */
	public abstract void processFile(File theFile) throws Exception;
}
