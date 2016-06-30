/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.svg;

import com.smithandtinkers.BalloonEngineState;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingUtilities;

import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.BugException;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.PlatformFriend;


/**
 * @author dhorlick
 */
public class GraphicalContent extends AbstractNamed
{
	// private BufferedImage image;
	private SoftReference image;
	
	private URL source;
	private boolean linked;
	private File compressed;
	private boolean broken;
	private boolean refreshmentInProgress;
	private boolean tooBig;
	private String formatName;
	private int widthFromFile;
	private int heightFromFile;
	
	private SingleThreadedChangeSupport changeSupport = new SingleThreadedChangeSupport();
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	/**
	 * The largest image size that this class will permit; one-fourth the all the memory
	 * that was ever available to the JVM on which this was written.
	 */
	private static final long MAX_IMAGE_SIZE_IN_KB = Runtime.getRuntime().maxMemory() / 4000L;
	
	public GraphicalContent()
	{
	}
	
	/**
	 * @param designatedSource an Optional file source. Note that calling this method does not
	 * initialize graphical content. For that, use {@link #read(File)} instead.
	 */
	public void setSource(File designatedSource)
	{
		source = fileToURL(designatedSource);
	}
	
	public void setSource(URL designatedSource)
	{
		if (source != designatedSource)
		{
			source = designatedSource;
			changeSupport.fireChange(CHANGE_EVENT);
			// TODO set image to null?
		}
	}
	
	public void setSource(String designatedSourceAsString) throws MalformedURLException
	{
		source = new URL(designatedSourceAsString);
	}
	
	public URL getSource()
	{
		return source;
	}
	
	public BufferedImage getImage()
	{
		// return image;
		
		if (image==null || image.get()==null)
		{
			if (compressed!=null && !isRefreshmentInProgress())
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							refreshFromFileCacheIfNecessary();
						}
						catch (IOException exception)
						{
							System.err.println("Image file cache appears to have been damaged");
							exception.printStackTrace();
						}
					}
				});
			}
			
			return null;
		}
		else
		{
			return (BufferedImage) image.get();
		}
	}
	
	public void setImage(BufferedImage designatedImage)
	{
		if (designatedImage!=getImage())
		{
			image = new SoftReference(designatedImage);
			// Logger.println("GraphicalContent.setImage: firing change, changeSupport="+changeSupport);
			changeSupport.fireChange(CHANGE_EVENT);
		}
	}
	
	public boolean isLinked()
	{
		return linked;
	}
	
	public void setLinked(boolean designatedLinked)
	{
		if (linked!=designatedLinked)
		{
			linked = designatedLinked;
			// TODO issue a change event, but rework the lazy image scaling chain first so it isn't called at the wrong time
		}
	}
	
	public void read(File designatedFile) throws IOException
	{
		read(designatedFile.getName(), new FileInputStream(designatedFile));
		setSource(designatedFile);
	}
	
	public void read(String name, InputStream inputStream) throws IOException
	{
		try
		{
			Logger.println("reading from file or component: "+name);
			
			String actualSuffix = GraphicalContent.determineFileExtension(name);
			
			compressed = File.createTempFile("balloonist-image-", null);
			compressed.deleteOnExit();
			
			FileOutputStream fileOutputStream = new FileOutputStream(compressed);
			
			for (int theChar=-1; (theChar = inputStream.read()) != -1; )
			{
				fileOutputStream.write(theChar);
			}
			fileOutputStream.close();
			
			refreshFromFileCache();
			
			setName(name);
			setBroken(false);
		}
		catch (IOException exception)
		{
			setBroken(true);
			throw exception;
		}
	}
		
	public static boolean hasFileExtension(File theFile, String designatedExtension)
	{
		String extension = determineFileExtension(theFile.getName());
		
		if (designatedExtension==null)
		{
			if (extension==null)
				return true;
			else
				return false;
		}
		
		return (designatedExtension.equalsIgnoreCase(extension));
	}
	
	/**
	 * @return The undotted file extension, or null if there isn't one.
	 */
	public static String determineFileExtension(String filename)
	{
		if (filename==null)
			return null;
		
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex == -1)
			return null;
		
		return filename.substring(dotIndex+1, filename.length());
	}
	
	public static BufferedImage imageToBufferedImage(Image image)
	{
		BufferedImage bufferedImage = null;
		
		if (image instanceof BufferedImage)
			bufferedImage = (BufferedImage) image;
		else
		{
			// TODO make sure image is done loading?
			ImageObserver imageObserver = null;
			
			bufferedImage = new BufferedImage(image.getWidth(imageObserver), image.getHeight(imageObserver), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2 = bufferedImage.createGraphics();
			g2.drawImage(image, 0, 0, imageObserver);
			bufferedImage.flush();
			g2.dispose();
		}
		
		return bufferedImage;
	}
	
	public int getHeight()
	{
		BufferedImage buffered = getImage();
		
		if (buffered==null)
			return heightFromFile;
		else
			return buffered.getHeight();
	}

	public int getWidth()
	{
		BufferedImage buffered = getImage();
		
		if (buffered==null)
			return widthFromFile;
		else
			return buffered.getWidth();
	}

	public String getImageType()
	{
		if (getSource()!=null)
			return determineFileExtension(getSource().toString());
		else
			return BudgetImageIO.FORMAT_PORTABLE_NETWORK_GRAPHICS;
	}

	public String toString()
	{
		if (source==null)
			return AbstractNamed.NAMES_TEXT.getString("graphicLabel");
		
		String fullname = source.getPath();
		
		int lastSeparator = fullname.lastIndexOf('/');
		
		if (lastSeparator==-1 || lastSeparator>=fullname.length()-1)
			return fullname;
		else
			return fullname.substring(lastSeparator+1);
	}
	
	/**
	 * @return The undotted potion of the file name that precedes the extension, or null if there isn't one.
	 */
	public static String determineFileBase(String filename)
	{
		if (filename==null)
			return null;
		
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex == -1)
			return null;
		
		return filename.substring(0, dotIndex);
	}
	
	public static URL fileToURL(File designatedFile)
	{
		if (designatedFile==null)
			return null;
		
		URL theURL = null;
		
		try
		{
			if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
				theURL = designatedFile.toURI().toURL();
			else
				theURL = designatedFile.toURL();

			return theURL;
		}
		catch (MalformedURLException exception)
		{
			throw new BugException(exception);
		}
	}
	
	/**
	 * If this object is linked, refreshes content from the provided source.
	 * 
	 * @return true, if this graphical content object is linked to a valid image, or is unlinked.
	 *         false, if this graphical content objects is linked to a non-existent or
	 *                invalid image.
	 */
	public boolean refreshFromLinkIfAppropriate()
	{
		Logger.println("refreshFromLinkIfAppropriate: isLinked()="+isLinked());
		
		if (!isLinked())
			return true;
		else
			return refreshFromLink();
	}
	
	/**
	 * Refreshes content from the previously-specified source.
	 * 
	 * @return true, if this graphical content object is pointed to a valid image.
	 *         false, if this graphical content objects is pointed to a non-existent or
	 *                invalid image.
	 */
	public boolean refreshFromLink()
	{
		Logger.println("GraphicalContent.refreshFromLink() invoked");
		
		if (getSource()==null)
		{
			setImage(null);
			changeSupport.fireChange(CHANGE_EVENT);
			return false;
		}
		else try
		{
			read(getSource());
			
			if (image!=null)
			{
				Logger.println("width before="+getWidth());
				Logger.println("new width="+getWidth());
				changeSupport.fireChange(CHANGE_EVENT);
			}
			
			return true;
		}
		catch (IOException exception)
		{
			System.err.println(exception);
			setImage(null);
			changeSupport.fireChange(CHANGE_EVENT);
			return false;
		}
	}
	
	public SingleThreadedChangeSupport getChangeSupport()
	{
		return changeSupport;
	}
	
	/**
	 * @return the ratio of height to width
	 */ 
	public double computeAspectRatio()
	{
		if (getWidth()==0)
			return 0.0; // TODO revisit?
		
		return (double)getHeight()/(double)getWidth();
	}
	
	public void read(URL designatedUrl) throws IOException
	{
		if (designatedUrl==null)
			return;
		
		InputStream inputStream = designatedUrl.openStream();
		String name = designatedUrl.getPath();
		
		int slashPos = name.lastIndexOf("/");
		if (slashPos!=-1 && slashPos<name.length()-1)
			name = name.substring(slashPos+1);
		
		read(name, inputStream);
		setSource(designatedUrl);
	}
	
	/**
	 * If an IOException is thrown, the broken flag will be raised
	 */
	private void refreshFromFileCache() throws IOException
	{
		if (compressed!=null && !refreshmentInProgress)
		{
			try
			{
				refreshmentInProgress = true; // TODO synchronize this effectively
				
				Logger.println("refreshing image content from file cache: "+compressed);
				
				ImageInputStream imageInputStream = ImageIO.createImageInputStream(compressed);
				Iterator walkImageReaders = ImageIO.getImageReaders(imageInputStream);
				
				if (!walkImageReaders.hasNext())
					throw new IOException(BalloonEngineState.DIALOG_TEXT.getString("invalidImageMessage"));
				
				ImageReader imageReader = (ImageReader) walkImageReaders.next();
				setFormatName(imageReader.getFormatName());
				
				imageReader.setInput(imageInputStream, true, true);
				
				widthFromFile = imageReader.getWidth(0);
				heightFromFile = imageReader.getHeight(0);
			
				Logger.println("dimensions as read from file: "+widthFromFile+ ", " + heightFromFile);
				
				int predicatedSizeInKilobytes = heightFromFile * widthFromFile * 4 / 1000; // assuming 4 bytes per pixel
				
				Logger.println("predictedSizeInKilobytes="+predicatedSizeInKilobytes);
				
				final long usedKilobytes = (Runtime.getRuntime().totalMemory() - 
						Runtime.getRuntime().freeMemory()) / 1000L;
				final long availableKilobytes = ( Runtime.getRuntime().maxMemory() / 1000L )
						- usedKilobytes;
						
				Logger.println("availableKilobytes="+availableKilobytes);
				
				if (predicatedSizeInKilobytes > MAX_IMAGE_SIZE_IN_KB)
				{
					setTooBig(true);
				}
				else if (availableKilobytes - predicatedSizeInKilobytes < 50L)
				{
					setTooBig(true); // TODO start some kind of timer to try loading this image again later
				}
				else
				{
					ImageReadParam param = imageReader.getDefaultReadParam();
					BufferedImage theImage = imageReader.read(0, param);
					setImage(theImage);
					
					if (theImage!=null)
					{
						setTooBig(false);
						setBroken(false);
					}
					else
					{
						throw new IOException(BalloonEngineState.DIALOG_TEXT.getString("invalidImageMessage"));
					}
				}
				
				imageInputStream.close();
				imageReader.dispose();

				refreshmentInProgress = false;
			}
			catch (IOException exception)
			{
				setBroken(true);
				refreshmentInProgress = false;
				throw exception;
			}
		}
	}
	
	public void writeTo(OutputStream outputStream, boolean closeWhenDone) throws IOException
	{
		if (compressed!=null)
		{
			FileInputStream fileInputStream = new FileInputStream(compressed);
			
			for (int theChar=-1; (theChar = fileInputStream.read()) != -1; )
			{
				outputStream.write(theChar);
			}
			
			if (closeWhenDone)
				outputStream.close();
		}
	}
	
	/**
	 * Returns true if this object has been dedicated to serve up an image.
	 * Note that this image may not necessarily be available at all times.
	 */
	public boolean isDedicated()
	{
		return (compressed!=null);
	}
	
	public boolean isBroken()
	{
		return broken;
	}
	
	private void setBroken(boolean designatedBroken)
	{
		broken = designatedBroken;
	}
	
	public boolean isRefreshmentInProgress()
	{
		return refreshmentInProgress;
	}
	
	/**
	 * If an IOException is thrown, the broken flag will be raised
	 */
	public void refreshFromFileCacheIfNecessary() throws IOException
	{
		if (getImage()==null && !isTooBig())
			refreshFromFileCache();
	}
	
	private void setTooBig(boolean designatedTooBig)
	{
		tooBig = designatedTooBig;
	}
	
	public boolean isTooBig()
	{
		return tooBig;
	}
	
	public void setFormatName(String designatedFormatName)
	{
		if (formatName!=designatedFormatName)
		{
			formatName = designatedFormatName;
		}
	}
	
	public String getFormatName()
	{
		return formatName;
	}
	
	public boolean isJpeg()
	{
		return ("JPEG".equalsIgnoreCase(getFormatName()));
	}

	/**
	 * @return the ratio of the width to the height
	 */	
	public double determineAspectRatio()
	{
		return (double)getWidth() / (double)getHeight();
	}
}
