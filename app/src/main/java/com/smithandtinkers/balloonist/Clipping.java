/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.layout.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.svg.SvgFlavor;
import com.smithandtinkers.svg.SvgOutputter;
import com.smithandtinkers.util.*;


/**
 *
 * @author dhorlick
 */
public class Clipping extends TypesafeList implements Transferable
{
	private static final DataFlavor[] FLAVORS;
	
	static
	{
		List flavors = new ArrayList();
		
		flavors.add(TasteSpoon.BALLOONIST_CLIPPING_FLAVOR);
		flavors.add(TasteSpoon.SVG_FLAVOR);
		// flavors.add(TasteSpoon.OLD_SVG_FLAVOR);
		flavors.add(TasteSpoon.X_PDF_FLAVOR);
		flavors.add(TasteSpoon.PDF_FLAVOR);
		
		// if (PlatformFriend.RUNNING_ON_MAC && !PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER)
		// 	flavors.add(TasteSpoon.MAC_JAVA13_PDF_FLAVOR);
		
		flavors.add(TasteSpoon.MAC_JAVA11_PDF_FLAVOR);
		// TODO add support for image content
		flavors.add(DataFlavor.stringFlavor);
		
		FLAVORS = (DataFlavor []) flavors.toArray(new DataFlavor[flavors.size()]);
	};
	
	public Clipping()
	{
		super(Selectable.class);
	}

	/**
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors()
	{
		return (DataFlavor[]) FLAVORS.clone();
	}

	/**
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		Logger.println("is data flavor supported: " + flavor);
		
		for (int loop = 0; loop < FLAVORS.length; loop++)
		{
			if (flavor.equals(FLAVORS[loop]))
			{
				// System.out.println("returning true");
				return true;
			}
		}
		
		Logger.println("returning false");
		return false;
	}

	/**
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		Logger.println("get transfer data as: " + flavor);
		
		if (TasteSpoon.PDF_FLAVOR.isMimeTypeEqual(flavor) || TasteSpoon.X_PDF_FLAVOR.isMimeTypeEqual(flavor) || TasteSpoon.MAC_JAVA11_PDF_FLAVOR.isMimeTypeEqual(flavor) 
				// || TasteSpoon.MAC_JAVA13_PDF_FLAVOR.isMimeTypeEqual(flavor)
				)
		{
			// System.out.println("* * * Supplying PDF...");
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			try
			{
				writeToPortableDocumentFormat(byteArrayOutputStream);
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
				return byteArrayInputStream;
			}
			catch (DocumentException e)
			{
				throw new IOException(e.getMessage());
			}
		}
		else if (DataFlavor.stringFlavor.isMimeTypeEqual(flavor))
		{
			return textToString();
		}
		else if (TasteSpoon.SVG_FLAVOR.isMimeTypeEqual(flavor))
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			writeToScalableVectorGraphicsFormat(byteArrayOutputStream);
			byte [] bytes = byteArrayOutputStream.toByteArray();
			
			// System.out.println("bytes="+new String(bytes));
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			return byteArrayInputStream;
		}
		else if (TasteSpoon.BALLOONIST_CLIPPING_FLAVOR.isMimeTypeEqual(flavor))
		{
			return this;
		}
		else
		{
			throw new UnsupportedFlavorException(flavor);
		}
	}	
	
	public void copy(Selection selection) throws DocumentException
	{
		clear();
		
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		for (int loop=0; loop<=selection.getSelectedItemsCount()-1; loop++)
		{
			Selectable selectable = selection.getSelectedItem(loop);
			// System.out.println("selectable to copy: "+selectable);
			
			if (selectable instanceof PubliclyCloneable)
			{
				PubliclyCloneable cloneable = (PubliclyCloneable) selectable;
				// System.out.println("clone: "+loop);
				
				try
				{
					selectable = (Selectable) cloneable.clone();
					selectable.setParent(null);
				}
				catch (CloneNotSupportedException exception)
				{
					throw new BugException(exception);
				}
			}
			
			add(selectable);
		}
		
		ClipboardOwner owner = new ClipboardOwner() {

			public void lostOwnership(Clipboard clipboard, Transferable contents)
			{
				Logger.println("Balloonist clipboard lost ownership: "+clipboard.getName());
				// TODO clear(); ?
			}
			
		};
		clipboard.setContents(this, owner); // use clipboard.setContents(this.clone(), owner); instead?
	}
	
	/**
	 * @return the clipping on the clipboard, or null if there isn't one.
	 */
	public static Clipping getClippingFromClipboard()
	{
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		
		if (!transferable.isDataFlavorSupported(TasteSpoon.BALLOONIST_CLIPPING_FLAVOR))
		{
			return null;
		}
		
		try
		{
			return (Clipping) transferable.getTransferData(TasteSpoon.BALLOONIST_CLIPPING_FLAVOR);
		}
		catch (RuntimeException exception)
		{
			throw exception;
		}
		catch (Exception exception)
		{
			throw new BugException(exception);
		}
	}
		
	/**
	 * @throws DocumentException
	 */
	public void writeToPortableDocumentFormat(OutputStream outputStream) throws DocumentException
	{
		final Rectangle2D framed = frame();
		
		if (framed==null)
			return;
		
		Document document = new Document();
		
		final double totalWidth = (framed.getX()+framed.getWidth());
		final double totalHeight = (framed.getY()+framed.getHeight());
		
		final int w = (int) Math.round(totalWidth) + 5;
		final int h = (int) Math.round(totalHeight) + 5;
		
		PdfFriend.writeToPortableDocumentFormat(new Drawing()
		{
			public void drawOnto(DrawingContext drawingContext)
			{
				Drawable drawable = null;
				
				for (int loop=0; loop<=size()-1; loop++)
				{
					if (get(loop) instanceof Drawable)
					{
						drawable = (Drawable) get(loop);
						drawingContext.setClip(null);
						drawingContext.setTransform(new AffineTransform());
						drawable.draw(drawingContext);
					}
					else if (get(loop)!=null)
					{
						Logger.println("Could not copy "+get(loop).getClass().getName()+ " to PDF; it is not drawable.");
					}
				}
			}
			
		}, outputStream, (float)w, (float)h, (float)w, (float)h);
	}	
	
	public void writeToScalableVectorGraphicsFormat(OutputStream outputStream) throws IOException
	{
		final SvgOutputter svgOutputter = new SvgOutputter();
		svgOutputter.outputSvg(outputStream, this, true, SvgFlavor.SVG_1_0);
		outputStream.close();
	}

	public String textToString()
	{
		StringBuffer asString = new StringBuffer();
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			Object item = get(loop);
			
			if (item instanceof TextShape)
			{
				TextShape textShape = (TextShape) item;
				
				String theText = textShape.determineTextAsString();
				
				if (theText==null && theText.length()==0)
					theText = item.toString();

				if (asString.length()>0)
				{
					asString.append(" | ");
				}
				
				asString.append(textShape.determineTextAsString());
			}
			else
			{
				if (asString.length()>0)
				{
					asString.append(" | ");
				}
				
				asString.append(item.toString());
			}
		}
		
		if (asString.length()>0)
			return asString.toString();
		else
			return "(empty Balloonist clipping)"; // this should never happen anyway
	}
		
	public Rectangle2D frame()
	{
		return Sill.frame(this);
	}
	
	public boolean couldBePastedTo(TypesafeList requestedParent)
	{
		Iterator walk = iterator();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			if (!requestedParent.getConstituentType().isAssignableFrom(item.getClass()))
				return false;
				
			if (!(requestedParent instanceof TextShape) && item instanceof Perch)
			{
				return false;
			}
		}
		
		return true;
	}
}
