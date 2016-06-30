/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContextFactory;
import com.smithandtinkers.graphics.awt.DefaultDrawingContext;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.PlatformFriend;
import com.smithandtinkers.util.WidgetedTypesafeList;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;

import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.smithandtinkers.BalloonEngineState;


/**
 *
 * @author dhorlick
 */
public abstract class PdfFriend
{
	private static DefaultFontMapper defaultFontMapper;
	
	/**
	 * @return a FontMapper that has had typical font directories registered with it.
	 */
	public static DefaultFontMapper getFontMapper()
	{
		if (defaultFontMapper==null)
		{
			long startTime = System.currentTimeMillis();
			
			defaultFontMapper = new DefaultFontMapper();
			
			int registered = registerFontDirectories(defaultFontMapper);
			
			long duration = (System.currentTimeMillis() - startTime);
			Logger.println("duration="+duration);
			Logger.println("fonts registered="+registered);
			
			// Logger.println("\taliases: "+defaultFontMapper.getAliases());
			// Logger.println("\tmapper: "+defaultFontMapper.getMapper());
			
			int changed = setEmbedding(false, defaultFontMapper);
			Logger.print("unembedded ");
			Logger.print(changed);
			Logger.println(" font(s).");
		}
		
		return defaultFontMapper;
	}
	
	public static int registerFontDirectories(DefaultFontMapper designatedFontMapper)
	{
		int count = 0;
		
		final long START = System.currentTimeMillis();
		
		if (PlatformFriend.RUNNING_ON_WINDOWS)
		{
			count += registerWindowsFontDirectories(designatedFontMapper);
		}
		else if (PlatformFriend.RUNNING_ON_MAC)
		{
			count += registerMacOSXFontDirectories(designatedFontMapper);
		}
		else
		{
			count += insertDirectory("/usr/X/lib/X11/fonts/TrueType", designatedFontMapper);
			count += insertDirectory("/usr/openwin/lib/X11/fonts/TrueType", designatedFontMapper);
			count += insertDirectory("/usr/share/fonts/default/TrueType", designatedFontMapper);
			count += insertDirectory("/usr/X11R6/lib/X11/fonts/ttf", designatedFontMapper);
		}
		
		final long DURATION = (System.currentTimeMillis() - START);
		
		Logger.println("registerFontDirectories took: "+((double)DURATION/1000.0)+ " sec(s)");
			
		return count;
	}
	
	public static int registerWindowsFontDirectories(DefaultFontMapper designatedFontMapper)
	{
		int count = 0;

		count += insertDirectory("c:/windows/fonts", designatedFontMapper);
		count += insertDirectory("c:/winnt/fonts", designatedFontMapper);
		count += insertDirectory("d:/windows/fonts", designatedFontMapper);
		count += insertDirectory("d:/winnt/fonts", designatedFontMapper);
		
		return count;
	}
	
	public static int registerMacOSXFontDirectories(DefaultFontMapper designatedFontMapper)
	{
		int count = 0;
		
		count += insertDirectory("/System/Library/Fonts/", designatedFontMapper);
		count += insertDirectory("/Library/Fonts/", designatedFontMapper);
		
		String userFontDir = System.getProperty("user.home")+"/Library/Fonts/";
		Logger.println("Checking user font dir: "+userFontDir);
		count += insertDirectory(userFontDir, designatedFontMapper);
		
		Logger.print("Registered ");
		Logger.print(count);
		Logger.println(" Mac OS X font(s).");
		
		return count;
	}
	
	/**
	 * @return the number of settings that were changed.
	 */
	public static int setEmbedding(boolean designatedEmbedding, DefaultFontMapper defaultFontMapper)
	{
		int changed = 0;
		
		Iterator walk = defaultFontMapper.getMapper().values().iterator();
		
		while (walk.hasNext())
		{
			DefaultFontMapper.BaseFontParameters params = (DefaultFontMapper.BaseFontParameters) walk.next();
			
			if (params.embedded!=designatedEmbedding)
			{
				params.embedded=designatedEmbedding;
				changed++;
			}
		}
		
		return changed;
	}
	
	public static WidgetedTypesafeList determineEditablyPasteableFontFamilyNames()
	{
		getFontMapper(); // makes sure FontFactory has been initialized
		WidgetedTypesafeList list = new WidgetedTypesafeList(String.class);
		
		list.addAll(FontFactory.getRegisteredFamilies());
		Collections.sort(list);
		
		return list;
	}
	
	private static int insertDirectory(String requestedDirectory,
			DefaultFontMapper defaultFontMapper)
	{
		int ffCount = FontFactory.registerDirectory(requestedDirectory);
		
		int fmCount = 0;
		
		if (defaultFontMapper!=null)
		{
			fmCount = defaultFontMapper.insertDirectory(requestedDirectory);
		}
		
		if (ffCount>fmCount)
			return ffCount;
		else
			return fmCount;
	}
	
	public static void writeToPortableDocumentFormat(Drawing drawing, OutputStream outputStream, float drawingWidth, float drawingHeight, float pageWidth, float pageHeight) throws DocumentException
	{
		com.lowagie.text.Document document = new com.lowagie.text.Document();
		
		document.setPageSize(new com.lowagie.text.Rectangle(pageWidth, pageHeight));
		
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		
		document.open();
		
		PdfContentByte cb = writer.getDirectContent();
		
		PdfTemplate tp = cb.createTemplate(drawingWidth, drawingHeight);
		
		DefaultFontMapper mapper = PdfFriend.getFontMapper();
		final DefaultDrawingContext drawingContext = (DefaultDrawingContext) DrawingContextFactory.getDrawingContext();
		Graphics2D g2 = null;
		
		if (BalloonEngineState.getInstance().isPreserveAccuracy())
			g2 = tp.createGraphicsShapes(drawingWidth, drawingHeight);
		else
			g2 = tp.createGraphics(drawingWidth, drawingHeight, mapper);
		
		drawingContext.setGraphics(g2);
		drawingContext.setTargetingPdf(true);
		drawingContext.setSelected(new Selection());
		drawingContext.setExportProfile(new SimpleExportProfile());
		
		drawing.drawOnto(drawingContext);
		
		Dimension dimension = new Dimension((int)drawingWidth, (int)drawingHeight);

		g2.dispose();
		cb.addTemplate(tp, 0, 0);
		document.close();
	}
}
