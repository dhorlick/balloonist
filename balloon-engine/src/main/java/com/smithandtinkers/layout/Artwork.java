/**
 Copyleft Jul 13, 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.event.UndoableEditListener;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.io.CachingArchiveContext;
import com.smithandtinkers.io.ProtectableInputStream;
import com.smithandtinkers.svg.GraphicalContent;
import com.smithandtinkers.util.*;

/**
 * A Balloonist document. A single sill, to represent a page, is associated with Artwork.
 * This sill in turn contains other sills ("panels"), which in turn contain artwork elements.
 *
 * @author dhorlick
 */
public class Artwork implements Serializable, Saveable
{
	private static final String INDEX_NAME = "index.xml";
	private static final String GRAPHICS_ARCHIVE_DIR = "graphics";
	
	private static final String IDENTIFIER_BALLOONIST_DOCUMENT = "balloonist-document";
		private static final String IDENTIFIER_VERSION = "version";
	
	private static final String IDENTIFIER_PAGE = "page"; // placeholder
		private static final String IDENTIFIER_NUMBER = "number"; // placeholder
	
	public static final String BALLOONIST_FILENAME_EXTENSION = "bna";
	private static Color ICE_COLOR = new Color(80, 120, 255, 132);
	
	private Layout layout;
	private Sill sill = new Sill();
	private File file;
	private Dimension enclosure;
	private boolean archive = true;
	private VersionNumber version;

	private ChangeListener changeListener;
	
	public Artwork()
	{
		layout = new Layout();
	}
	
	public Layout getLayout()
	{
		return layout;
	}
	
	public void setLayout(Layout designatedLayout)
	{
		layout = designatedLayout;
		// refresh();
	}
	
	public Sill getSill()
	{
		return sill;
	}
	
	public void startOver()
	{
		layout.syncSill(sill);
	}

	/**
	 * @param selectedFile
	 */
	public void save(File selectedFile) throws IOException
	{
		file = selectedFile;
		save();
	}
	
	public void save() throws IOException
	{
		CachingArchiveContext archiveContext = new CachingArchiveContext();
		
		if (archive)
		{
			FileOutputStream fileOut = new FileOutputStream(file);
			ZipOutputStream zipOut = new ZipOutputStream(fileOut);
			//zipOut.setMethod(ZipOutputStream.STORED); // this would mean "uncompressed"; but I don't think it worked for some reason
			
			zipOut.setLevel(Deflater.NO_COMPRESSION); // because any images will already be compressed
			ZipEntry artworkEntry = new ZipEntry(INDEX_NAME);
			zipOut.putNextEntry(artworkEntry);
			zipOut.setComment("This file generated by Balloonist. http://www.smithandtinkers.com");
			
			save(zipOut, archiveContext);
			
			Logger.println("archiveContext after save-harvest: "+archiveContext);
			
			if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
			{
				Iterator walk = archiveContext.getNames().iterator();
			
				while (walk.hasNext())
				{
					String graphicComponentName = (String) walk.next();
					System.out.println("storing: "+graphicComponentName);
					GraphicalContent graphicalContent = archiveContext.read(graphicComponentName);
					
					ZipEntry graphicEntry = new ZipEntry(GRAPHICS_ARCHIVE_DIR + "/" + graphicComponentName);
					zipOut.putNextEntry(graphicEntry);
					
					// String imageFileExtension = GraphicalContent.determineFileExtension(graphicComponentName);
					String imageFileExtension = graphicalContent.getImageType();
					
					if (graphicalContent.isDedicated())
					{
						// TODO make sure filename is valid?
						
						System.out.println("\tstoring image content from file cache");
						
						// ImageIO.write(graphicalContent.getImage(), imageFileExtension, zipOut);
						
						graphicalContent.writeTo(zipOut, false);
					}
				}
			}
			else
			{
				// TODO issue some kind of warning that embedded images have not been saved.
			}
			
			zipOut.flush();
			zipOut.finish();
		}
		else
		{
			save(new FileOutputStream(file), archiveContext);
		}
		
		BalloonEngineState.getInstance().getPlatformStrategy().takeOwnershipOfFile(file);
	}
	
	public void save(OutputStream designatedOutputStream, ArchiveContext archiveContext) throws IOException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			save(doc, doc.getDocumentElement(), archiveContext);
			// save(doc, doc);
			
			Transformer autobot = TransformerFactory.newInstance().newTransformer();
			autobot.setOutputProperty(OutputKeys.INDENT, "yes" );
			autobot.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
			
			autobot.transform(new DOMSource(doc), new StreamResult(designatedOutputStream)); // new StreamResult(file) doesn't work in Mac OS X Java 1.5
		}
		catch (ParserConfigurationException exception)
		{
			throw new BugException(exception);
		}
		catch (TransformerConfigurationException exception)
		{
			throw new BugException(exception);
		}
		catch (TransformerException exception)
		{
			throw new BugException(exception);
		}
	}
	
	/**
	 * @see com.smithandtinkers.util.Saveable#save(org.w3c.dom.Document, Node, ArchiveContext)
	 */
	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		Element balloonistDoc = doc.createElement(IDENTIFIER_BALLOONIST_DOCUMENT);
		balloonistDoc.setAttribute(IDENTIFIER_VERSION, BalloonEngineState.VERSION.toString());
		layout.save(doc, balloonistDoc, archiveContext);
		
		Element pageElement = doc.createElement(IDENTIFIER_PAGE); // as yet unused
		pageElement.setAttribute(IDENTIFIER_NUMBER, "1"); // also as yet unused
		balloonistDoc.appendChild(pageElement);
		
		sill.save(doc, pageElement, archiveContext);
		
		doc.appendChild(balloonistDoc);
	}

	/**
	 * @see com.smithandtinkers.util.Saveable#open(Object, Node, ArchiveContext)
	 */
	public void open(Object nothing, Node node, ArchiveContext archiveContext)
	{
		// System.out.println("node name: "+node.getNodeName());
		
		if (!IDENTIFIER_BALLOONIST_DOCUMENT.equals(node.getNodeName()))
			throw new IllegalStateException(BalloonEngineState.DIALOG_TEXT.getString("invalidDocumentMessage"));
		
		Element element = (Element) node;
		
		// System.out.println("node name again: "+balloonistDoc.getNodeName());
		
		if (element!=null && element.hasAttribute(IDENTIFIER_VERSION))
		{
			setVersion(VersionNumber.parse(element.getAttribute(IDENTIFIER_VERSION)));
			
			// System.out.println("comparing versions...");
			
			if (getVersion().subtract(BalloonEngineState.VERSION)>=0.1)
			{
				// This document was created with a more recent version of Balloonist
				
				if (getFile()!=null) // and it shouldn't be
				{
					StringBuffer newFilename = new StringBuffer();
					newFilename.append(GraphicalContent.determineFileBase(getFile().getName()));
					newFilename.append(" (");
					newFilename.append(BalloonEngineState.DIALOG_TEXT.getString("importedFromVersionLabel"));
					newFilename.append(" ");
					newFilename.append(getVersion().toString());
					newFilename.append(")");
					newFilename.append(".");
					newFilename.append(BALLOONIST_FILENAME_EXTENSION);
					
					String newFilenameAsString = newFilename.toString();
					
					System.out.println("newFilenameAsString: "+newFilenameAsString);
					
					File renamedFile = new File(getFile().getParentFile().getAbsoluteFile(), newFilenameAsString);
					setFile(renamedFile);
				}
			}
		}
		
		Element layoutElement = (Element) XmlFriend.excavateSubnode(element,Layout.IDENTIFIER_LAYOUT);
		if (layoutElement==null)
			throw new IllegalArgumentException("The document has no layout. It is probably invalid."); // TODO i8n
		
		getLayout().open(this, layoutElement, archiveContext);
		
		Element sillElement = (Element) XmlFriend.excavateSubnode(element, Sill.IDENTIFIER_SILL);
		if (sillElement==null)
			throw new IllegalArgumentException("The document has no graphic content. It is probably invalid."); // TODO i8n
		
		getSill().open(this, sillElement, archiveContext);
	}

	/**
	 * @return a List of any missing font families as Strings. Will return an empty List if all
	 *          fonts encountered were available.
	 */
	public List open(File selectedFile, UndoableEditListener designatedUndoableEditListener) throws SAXException, IOException
	{
		setFile(selectedFile);
		List missingFontFamilies = open(designatedUndoableEditListener);
		
		if (changeListener!=null)
			changeListener.stateChanged(CHANGE_EVENT);
			
		return missingFontFamilies;
	}
	
	public File getFile()
	{
		return file;
	}
	
	private void setFile(File selectedFile)
	{
		file = selectedFile;
		
		if (selectedFile==null)
			return;
		
		final String fileExtension = GraphicalContent.determineFileExtension(file.getName()); // TODO move this somewhere more helpful
		
		if (fileExtension!=null && (fileExtension.equalsIgnoreCase("zip") || fileExtension.equalsIgnoreCase(BALLOONIST_FILENAME_EXTENSION))) // TODO actually attempt to determine whether zip compression has been employed
		{
			archive = true;
		}
		else
		{
			archive = false;
		}
	}

	/**
	 * @return a List of any missing font families as Strings. Will return an empty List if all
	 *          fonts encountered were available.
	 */
	public List open(UndoableEditListener designatedUndoableEditListener) throws SAXException, IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			// factory.setCoalescing(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			Logger.println("parsing..");
			
			Document doc = null;
			CachingArchiveContext archiveContext = new CachingArchiveContext();
			archiveContext.setListenerToNotifyAboutUndoableEdits(designatedUndoableEditListener);

			if (archive)
			{
				FileInputStream fileIn = new FileInputStream(file);
				ZipInputStream zipIn = new ZipInputStream(fileIn);

				if (zipIn!=null)
				{
					ProtectableInputStream protectableInputStream = new ProtectableInputStream(zipIn);
					protectableInputStream.setPermitClosing(false);
					
					for (ZipEntry entry = zipIn.getNextEntry(); zipIn.available()!=0 && entry!=null; entry = zipIn.getNextEntry())
					{
						// System.out.println("Reading entry: "+entry.getName());
						
						if (entry.getName().equals(INDEX_NAME))
						{
							// System.out.println("found index");
							
							// Annoyingly, jaxp closes the stream when finished. we need to prevent this.
							
							doc = builder.parse(protectableInputStream);
						}
						else if (entry.getName().startsWith(GRAPHICS_ARCHIVE_DIR + "/") && entry.getName().length()>GRAPHICS_ARCHIVE_DIR.length()+1)
						{
							String componentName = entry.getName().substring(GRAPHICS_ARCHIVE_DIR.length()+1);
							Logger.println("Reading graphic: "+componentName);
							
							GraphicalContent graphicalContent = new GraphicalContent();
							graphicalContent.read(componentName, protectableInputStream);
							
							archiveContext.put(componentName, graphicalContent);
						}
					}
				}
				
				zipIn.close();
			}
			else
			{
				doc = builder.parse(file);
			}
			
			if (doc!=null)
				open(doc, doc.getDocumentElement(), archiveContext);
			else
				throw new IllegalStateException(BalloonEngineState.DIALOG_TEXT.getString("invalidDocumentMessage"));
				
			return archiveContext.getMissingFontFamilies();
		}
		catch (javax.xml.parsers.ParserConfigurationException exception)
		{
			throw new BugException(exception);
		}
	}
	
	/**
	 * Used to infer the Artwork's dimensions from a Layout, at its birth.
	 */
	public Dimension enclose()
	{
		Dimension openedEnclosure = getLayout().enclose();
		// System.out.println("Enclose!! openedEnclosure="+openedEnclosure);
		setEnclosure(openedEnclosure);
		return openedEnclosure;
	}
	
	public Dimension getEnclosure()
	{
		if (enclosure==null && layout!=null)
		{
			enclose();
		}
		
		return enclosure;
	}
	
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public void setEnclosure(Dimension designatedEnclosure)
	{
		if (enclosure != designatedEnclosure)
		{
			enclosure = designatedEnclosure;
		
			if (changeListener!=null)
				changeListener.stateChanged(CHANGE_EVENT);
		}
	}
	
	public void setChangeListener(ChangeListener designatedChangeListener) // TODO change this to addChangeListener(ChangeListener)
	{
		if (changeListener != designatedChangeListener)
		{
			final ChangeListener oldChangeListener = changeListener;
			
			changeListener = designatedChangeListener;

			layout.addChangeListener(designatedChangeListener);

			if (changeListener!=null)
				changeListener.stateChanged(CHANGE_EVENT);
			
			if (oldChangeListener!=null)
				layout.removeChangeListener(oldChangeListener);
		}
	}
	
	public ChangeListener getChangeListener()
	{
		return changeListener;
	}
	
	public boolean isArchive()
	{
		return archive;
	}

	public void setArchive(boolean designatedArchive)
	{
		this.archive = designatedArchive;
	}

	public double getWidth()
	{
		if (enclosure==null)
			return 600;
		
		return enclosure.getWidth();
	}
	
	public double getHeight()
	{
		if (enclosure==null)
			return 800;
		
		return enclosure.getHeight();
	}
	
	public void setVersion(VersionNumber designatedVersionNumber)
	{
		version = designatedVersionNumber;
	}
	
	public VersionNumber getVersion()
	{
		return version;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.layout.Artwork {"
			 + "archive = " + archive + ", "
			 + "layout = " + layout + ", "
			 + "sill = " + sill + ", "
			 + "file = " + file + ", "
			 + "enclosure = " + enclosure
			 + "version = " + version
		+ "}";
	}
	
	/**
	 * @return a Dimension object that could encompass the provided rectangle
	 *         were that Dimension's upper-left hand corner placed at the Origin
	 */
	public static Dimension frame(Rectangle2D requestedRectangle)
	{
		if (requestedRectangle==null)
			return new Dimension(0,0);
		
		return new Dimension(
				(int) ( requestedRectangle.getX()+requestedRectangle.getWidth() ),
				(int) ( requestedRectangle.getY()+requestedRectangle.getHeight() )
			);
	}
}
