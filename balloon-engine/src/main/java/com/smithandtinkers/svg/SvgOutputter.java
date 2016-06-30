/**
 Copyleft 2009 by Dave Horlick
 */

package com.smithandtinkers.svg;

import com.smithandtinkers.graphics.DrawingContextFactory;
import com.smithandtinkers.graphics.awt.DefaultDrawingContext;

import com.smithandtinkers.layout.Drawable;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.SimpleExportProfile;
import com.smithandtinkers.util.BugException;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * @author dhorlick
 */
public class SvgOutputter
{
	public void outputSvg(OutputStream outstream, List drawablesList, boolean includeHeaders,
			final SvgFlavor svgFlavor)
	{
		if (drawablesList == null || outstream == null) {
			return;
		}
		Iterator drawableIterator = drawablesList.iterator();
		try {
			boolean useCSS = true;
			// we want to use CSS style attribute
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			SimpleExportProfile transmog = new SimpleExportProfile(svgFlavor);
			transmog.setSvgFlavor(svgFlavor);
			if (transmog.getSvgFlavor() == SvgFlavor.ADOBE_SVG_VIEWER_60P1_SVG_1_1 || transmog.getSvgFlavor() == SvgFlavor.SVG_1_2) {
				transmog.setIncludeFlowingText(true);
			}
			DefaultDrawingContext drawingContext = (DefaultDrawingContext) DrawingContextFactory.getDrawingContext();
			drawingContext.setGraphics(createDummyGraphics2D());
			drawingContext.setSvgDocument(doc);
			drawingContext.setSelected(new Selection());
			drawingContext.setExportProfile(transmog);
			while (drawableIterator.hasNext()) {
				Object item = drawableIterator.next();
				if (item instanceof Drawable) {
					((Drawable) item).draw(drawingContext);
				}
			}
			Source source = new DOMSource(doc);
			Result result = new StreamResult(outstream);
			Transformer autobot = TransformerFactory.newInstance().newTransformer();
			autobot.setOutputProperty(OutputKeys.INDENT, "yes");
			if (includeHeaders) {
			} else {
				autobot.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			}
			autobot.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
			autobot.transform(source, result);
			outstream.close();
			drawingContext.getGraphics().dispose();
		} catch (RuntimeException exception) {
			throw exception;
		} catch (Exception exception) {
			throw new BugException(exception);
		}
	}

	public static final BufferedImage DUMMY_IMAGE = GraphicsEnvironment.getLocalGraphicsEnvironment(
				).getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1);

	public static Graphics2D createDummyGraphics2D()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(DUMMY_IMAGE);
	}
}
