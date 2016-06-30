/*
 *  Copyleft Sep 15, 2006 by Dave Horlick

 */

package com.smithandtinkers.graphics.awt;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.ImageObserver;

import com.smithandtinkers.layout.*;
import com.smithandtinkers.geom.Shapeable;
import com.smithandtinkers.geom.ShapeFriend;
import com.smithandtinkers.geom.ShapingContext;
import com.smithandtinkers.graphics.*;
import com.smithandtinkers.svg.SvgFlavor;
import com.smithandtinkers.text.AbstractTextLayout;
import com.smithandtinkers.text.OrientableTextLayout;
import com.smithandtinkers.text.StyledDocumentStraw;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.TypesafeList;

import org.w3c.dom.Document;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.gui.ShapeDrawer;


/**
 * An implementation of {@link DrawingContext} that can target Graphics2D or SVG.
 * There is also support for selecting graphics elements thru {@link #getSelected}, and for interacting
 * with gutters (which can't be formally selected) via {@link #getShootingRange}.
 *
 * To target Graphics2D, provide one thru {@link #setGraphics}.
 *
 * To target SVG, provide an instance of {@link org.w3c.dom.Document} to {@link #setSvgDocument}.
 * 
 * @author dhorlick
 */
public class DefaultDrawingContext implements DrawingContext
{
	private Graphics2D graphics;
	
	/**
	 * An optional ImageObserver to be notified when any image loads
	 * have completed (This functionality has not yet been implemented
	 * at time of writing.)
	 */
	private ImageObserver observer;
	
	private Selection selected;
	private ShootingRange shootingRange;
	private ExportProfile exportProfile;
	private DrawingFilter drawingFilter = new DrawingFilter();
	
	private boolean intermediate;
	
	private org.w3c.dom.Document svgDocument;
	
	private Element bottomElement;
	private Element defsElement;
	
	private int svgClips = 0;
	private double scaleFactor = 1.0;
	
	private TypesafeList svgGroupNames = new TypesafeList(String.class);

	private boolean targetingPdf;
	
	public DefaultDrawingContext()
	{
	}
	
	public DefaultDrawingContext(ImageObserver designatedObserver, Selection designatedSelected, ExportProfile designatedTransmogrification, ShootingRange designatedShootingRange)
	{
		setObserver(designatedObserver);
		setSelected(designatedSelected);
		setExportProfile(designatedTransmogrification);
		setShootingRange(designatedShootingRange);
	}
	
	/**
	 * Warning: using Graphics2D directly will prevent the use of SVG and preclude the use of some
	 * PDF features.
	 */
	public Graphics2D getGraphics()
	{
		return graphics;
	}
	
	public void setGraphics(Graphics2D designatedGraphics)
	{
		graphics = designatedGraphics;
	}
	
	public ImageObserver getObserver()
	{
		return observer;
	}
	
	public void setObserver(ImageObserver designatedObserver)
	{
		observer = designatedObserver;
	}
	
	public Selection getSelected()
	{
		return selected;
	}
	
	public void setSelected(Selection designatedSelected)
	{
		selected = designatedSelected;
	}
	
	public ExportProfile getExportProfile()
	{
		return exportProfile;
	}
	
	public void setExportProfile(ExportProfile designatedExportProfile)
	{
		exportProfile = designatedExportProfile;
		
		updateSvgVersion();
	}
	
	public void setSvgDocument(Document designatedDocument)
	{
		svgDocument = designatedDocument;
		
		if (svgDocument!=null)
		{
			Element svg = svgDocument.createElement(IDENTIFIER_SVG);
			svg.setAttribute("xmlns", SVG_NAMESPACE);
			bottomElement = svg;
			
			updateSvgVersion();
			
			svgDocument.appendChild(svg);
			
			defsElement = svgDocument.createElement(IDENTIFIER_DEFS);
			bottomElement.appendChild(defsElement);
			
			svgClips = 0;
			svgGroupNames.clear();
		}
	}
	
	private void updateSvgVersion()
	{
		SvgFlavor svgFlavor = getSvgFlavor();
		
		if (getSvgFlavor()!=null && bottomElement!=null && IDENTIFIER_SVG.equals(bottomElement.getNodeName()))
		{
			bottomElement.setAttribute(IDENTIFIER_VERSION, String.valueOf(svgFlavor.getSvgVersion()));
		}
	}
	
	public org.w3c.dom.Document getSvgDocument()
	{
		return svgDocument;
	}
		
	protected void setBottomElement(Element designatedElement)
	{
		bottomElement = designatedElement;
	}
	
	public Element getBottomElement()
	{
		return bottomElement;
	}
	
	public void setClip(Shape designatedShape)
	{
		if (graphics!=null)
			graphics.setClip(designatedShape);
		
		// TODO revisit. altho we don't need to explicitly do anything here to support SVG,
		//      it's a problem if the outside *isn't* using this method to reset after clips.
	}
	
	public Shape getClip()
	{
		if (graphics==null)
			return null;
		
		return graphics.getClip();
	}
	
	public void clip(Shape designatedShape)
	{
		if (graphics!=null)
		{
			if (!isTargetingPdf())
				ShapeDrawer.clip(graphics, designatedShape);
			else
				graphics.clip(designatedShape);
		}
		
		if (bottomElement!=null && defsElement!=null && svgDocument!=null && !bottomElement.hasAttribute(IDENTIFIER_CLIP_PATH_ATTR)) // TODO make additive
		{
			Element clipPath = svgDocument.createElement(IDENTIFIER_CLIP_PATH_ELEM);
			svgClips++;
			
			final String CLIP_NAME = caulkAndUniquify(KEYWORD_CLIP+svgClips);
			
			clipPath.setAttribute(IDENTIFIER_ID, CLIP_NAME);
			
			defsElement.appendChild(clipPath);
			clipPath.appendChild(shapeToElement(designatedShape));
			
			bottomElement.setAttribute(IDENTIFIER_CLIP_PATH_ATTR, "url(#" + CLIP_NAME + ")");
		}
	}
	
	public void antiAliasLines()
	{
		if (graphics!=null)
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
	}
	
	public void setColor(Color designatedColor)
	{
		if (graphics!=null)
			graphics.setColor(designatedColor);
	}
	
	public Color getColor()
	{
		if (graphics==null)
			return null;
		
		return graphics.getColor();
	}
	
	public void draw(Shape designatedShape)
	{
		if (designatedShape==null)
			return;
		
		if (graphics!=null)
		{
			if (!isTargetingPdf())
			{
				ShapeDrawer.draw(graphics, designatedShape);
			}
			else
			{
				graphics.draw(designatedShape);
			}
		}
		
		if (bottomElement!=null)
		{
			Element shapeElement = shapeToElement(designatedShape);
			applyStyle(shapeElement, true);
			applyStrokeWidth(shapeElement);
			bottomElement.appendChild(shapeElement);
		}
	}
	
	protected Element shapeToElement(Shape designatedShape)
	{
		return shapeToElement(designatedShape, 0, 0);
	}
	
	protected Element shapeToElement(Shape designatedShape, int x, int y)
	{
		Element shapeElement = null;
		
		if (designatedShape instanceof Rectangle2D)
		{
			Rectangle2D shapeAsRect = (Rectangle2D) designatedShape;
			shapeElement = svgDocument.createElement(IDENTIFIER_RECT);
			shapeElement.setAttribute(IDENTIFIER_X, String.valueOf((double)x+shapeAsRect.getX()));
			shapeElement.setAttribute(IDENTIFIER_Y, String.valueOf((double)y+shapeAsRect.getY()));
			shapeElement.setAttribute(IDENTIFIER_WIDTH, String.valueOf(shapeAsRect.getWidth()));
			shapeElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(shapeAsRect.getHeight()));
		}
		else
		{
			StringBuffer desc = new StringBuffer();
			
			for (PathIterator pathIterator = designatedShape.getPathIterator(null);
			!pathIterator.isDone();
			pathIterator.next())
			{
				double [] coords = new double[6];
				
				switch (pathIterator.currentSegment(coords))
				{
					case PathIterator.SEG_MOVETO:
						append(desc, 'M', coords, 2);
						break;
						
					case PathIterator.SEG_LINETO:
						append(desc, 'L', coords, 2);
						break;
						
					case PathIterator.SEG_QUADTO:
						append(desc, 'Q', coords, 4);
						break;
						
					case PathIterator.SEG_CUBICTO:
						append(desc, 'C', coords, 6);
						break;
						
					case PathIterator.SEG_CLOSE:
						append(desc, 'Z', coords, 0);
						break;
						
					default:
				}
				
				if (!pathIterator.isDone())
					desc.append(" ");
			}
			
			shapeElement = svgDocument.createElement(IDENTIFIER_PATH);
			shapeElement.setAttribute(IDENTIFIER_DESCRIPTION, desc.toString());
			
			if (x!=0 || y!=0)
			{
				StringBuffer translationDef = new StringBuffer();
				translationDef.append("translate(");
				translationDef.append(x);
				translationDef.append(",");
				translationDef.append(y);
				translationDef.append(")");
				shapeElement.setAttribute(IDENTIFIER_TRANSFORM, translationDef.toString());
			}
			
			applyStrokeWidth(shapeElement);
		}
		
		return shapeElement;
	}
	
	private static void append(StringBuffer buffer, char command, double [] coords, int numberOfCoords)
	{
		buffer.append(String.valueOf(command));
		
		for (int loop=1; loop<=numberOfCoords; loop++)
		{
			buffer.append(coords[loop-1]);
			
			if (loop<numberOfCoords)
				buffer.append(" ");
		}
	}
	
	protected void applyStyle(Element designatedElement, boolean outline)
	{
		if (designatedElement==null || graphics==null)
			return;
		
		if (outline)
		{
			designatedElement.setAttribute(IDENTIFIER_STROKE, ShapeFriend.describeWithoutAlpha(getColor()));
			designatedElement.setAttribute(IDENTIFIER_FILL, IDENTIFIER_NONE);
		}
		else
		{
			designatedElement.setAttribute(IDENTIFIER_FILL, ShapeFriend.describeWithoutAlpha(getColor()));
			designatedElement.setAttribute(IDENTIFIER_STROKE, IDENTIFIER_NONE);
		}
		
		// TODO ...
	}
	
	public void fill(Shape designatedShape)
	{
		if (designatedShape==null)
			return;
		
		if (graphics!=null)
			graphics.fill(designatedShape);

		if (bottomElement!=null)
		{
			Element shapeElement = shapeToElement(designatedShape);
			applyStyle(shapeElement, false);
			bottomElement.appendChild(shapeElement);
		}
	}
	
	public Stroke getStroke()
	{
		if (graphics==null)
			return null;
		
		return graphics.getStroke();
	}
	
	public void setStroke(Stroke designatedStroke)
	{
		if (graphics!=null)
			graphics.setStroke(designatedStroke);
	}
	
	public void translate(double dx, double dy)
	{
		if (graphics!=null)
		{
			if (!isTargetingPdf())
				ShapeDrawer.translate(graphics, dx, dy);
			else
				graphics.translate(dx, dy);
		}
		
		if (bottomElement!=null)
		{
			if (!bottomElement.hasAttribute(IDENTIFIER_TRANSFORM)) // TODO make translations additive
			{
				bottomElement.setAttribute(IDENTIFIER_TRANSFORM, "translate("+String.valueOf(dx)+","+String.valueOf(dy)+")");
			}
		}
	}
	
	public void fillPolygon(final int [] xCoords, final int [] yCoords, int numberOfCoords)
	{
		if (graphics!=null)
			graphics.fillPolygon(xCoords, yCoords, numberOfCoords);
	}
	
	public FontRenderContext getFontRenderContext()
	{
		if (graphics!=null)
			return graphics.getFontRenderContext();
		
		// TODO SVG support
		
		return null;
	}
	
	public FontMetrics getFontMetrics()
	{
		if (graphics==null)
			return null;
		
		return graphics.getFontMetrics();
	}
	
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		if (graphics!=null)
			graphics.drawLine(x1, y1, x2, y2);
			
		if (svgDocument!=null && bottomElement!=null)
		{
			Element lineElement = svgDocument.createElement(IDENTIFIER_LINE);
			lineElement.setAttribute(IDENTIFIER_X1, String.valueOf(x1));
			lineElement.setAttribute(IDENTIFIER_Y1, String.valueOf(y1));
			lineElement.setAttribute(IDENTIFIER_X2, String.valueOf(x2));
			lineElement.setAttribute(IDENTIFIER_Y2, String.valueOf(y2));
			
			applyStrokeWidth(lineElement);
			applyStyle(lineElement, true);
			
			bottomElement.appendChild(lineElement);
		}
	}
	
	public void drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
	{
		if (graphics!=null)
			graphics.drawImage(img, x, y, width, height, observer);
	}
	
	public void drawImage(Image img, int x, int y, ImageObserver observer)
	{
		if (graphics!=null)
			graphics.drawImage(img, x, y, observer);
	}
	
	public void drawFilledOval(int x, int y, int width, int height, Color designatedOutlineColor, Color designatedFillColor)
	{
		if (designatedOutlineColor==null && designatedFillColor==null)
			return;
		
		if (graphics!=null)
		{
			if (designatedFillColor!=null)
			{
				graphics.setColor(designatedFillColor);
				graphics.fillOval(x, y, width, height);
			}
			
			if (designatedOutlineColor!=null)
			{
				graphics.setColor(designatedOutlineColor);
				graphics.drawOval(x, y, width, height);
			}
		}
		
		if (svgDocument!=null && bottomElement!=null)
		{
			Element ellipseElement = svgDocument.createElement(IDENTIFIER_ELLIPSE);
			ellipseElement.setAttribute(IDENTIFIER_CENTER_X, String.valueOf(x));
			ellipseElement.setAttribute(IDENTIFIER_CENTER_Y, String.valueOf(y));
			ellipseElement.setAttribute(IDENTIFIER_RADIUS_X, String.valueOf(width/2.0));
			ellipseElement.setAttribute(IDENTIFIER_RADIUS_Y, String.valueOf(height/2.0));
			
			ellipseElement.setAttribute(IDENTIFIER_STROKE, ShapeFriend.describeWithoutAlpha(designatedOutlineColor));
			ellipseElement.setAttribute(IDENTIFIER_FILL, ShapeFriend.describeWithoutAlpha(designatedFillColor));
			
			applyStrokeWidth(ellipseElement);
			
			bottomElement.appendChild(ellipseElement);
		}
	}
	
	public void drawFilledOval(double x, double y, double width, double height, Color designatedOutlineColor, Color designatedFillColor)
	{
		drawFilled(new Ellipse2D.Double(x, y, width, height), designatedOutlineColor, designatedFillColor);
	}
	
	public void fillOval(int x, int y, int width, int height)
	{
		if (graphics!=null)
			graphics.fillOval(x, y, width, height);
		
		if (svgDocument!=null)
		{
			Element ellipseElement = svgDocument.createElement(IDENTIFIER_ELLIPSE);
			ellipseElement.setAttribute(IDENTIFIER_CENTER_X, String.valueOf(x));
			ellipseElement.setAttribute(IDENTIFIER_CENTER_Y, String.valueOf(y));
			ellipseElement.setAttribute(IDENTIFIER_RADIUS_X, String.valueOf(width/2.0));
			ellipseElement.setAttribute(IDENTIFIER_RADIUS_Y, String.valueOf(height/2.0));
			
			if (graphics!=null)
			{
				ellipseElement.setAttribute(IDENTIFIER_STROKE, IDENTIFIER_NONE);
				ellipseElement.setAttribute(IDENTIFIER_FILL, ShapeFriend.describeWithoutAlpha(graphics.getBackground()));
			}
			
			bottomElement.appendChild(ellipseElement);
		}
	}
	
	public void drawString(String str, int x, int y)
	{
		if (graphics!=null)
			graphics.drawString(str, x, y);
	}
	
	public void drawString(String str, float x, float y)
	{
		if (graphics!=null)
			graphics.drawString(str, x, y);
	}
	
	public void plotDot(int x, int y, Color color, int diameter, boolean fill)
	{
		if (graphics!=null)
			ShapeFriend.plotDot(x, y, color, diameter, graphics, fill);
	}
	
	public void plotDot(double x, double y, Color color, double diameter, boolean fill)
	{
		if (graphics!=null)
			ShapeFriend.plotDot(x, y, color, diameter, graphics, fill);
	}
	
	public void plotCornerDots(Rectangle2D rect, Color color, int diameter, boolean fill)
	{
		if (graphics!=null)
			ShapeFriend.plotCornerDots(rect, color, diameter, graphics, fill);
	}
	
	public void drawLine(double x1, double y1, double x2, double y2, Color color)
	{
		final Color oldColor = getColor();
		
		setColor(color);
		graphics.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		
		setColor(oldColor);

		// TODO remove cast and make a shape out of this instead. do same to ShapeFriend.plotDots(double, ...)
	}
	
	/**
	 * This won't do anything for SVG 1.2.
	 *
	 * @param coordinateFrameUsed an optional Sill. This may be used to perform coordinate frame transformations
	 * when outputting to PDF.
	 */
	public void drawHorizontalText(TextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed)
	{
		if (graphics!=null)
		{	
			if (!isTargetingPdf() || !BalloonEngineState.getInstance().isPreserveAccuracy())
			{
				layout.draw(graphics, x, y);
			}
			else
			{
				translate(x, y);
				fill(layout.getOutline(null));
				translate(-x, -y);
			}
		}
		
		if (svgDocument!=null&& bottomElement!=null && getSvgFlavor()!=null && getSvgFlavor().getSvgVersion()<1.2 )
		{
			Element textElement = null;
			
			textElement = svgDocument.createElement(StyledDocumentStraw.IDENTIFIER_TEXT);
			
			straw.save(svgDocument, textElement, startCharacterOffset, layout.getCharacterCount(), null);
			
			textElement.setAttribute(IDENTIFIER_X, String.valueOf(x));
			textElement.setAttribute(IDENTIFIER_Y, String.valueOf(y));
			
			bottomElement.appendChild(textElement);
		}
	}
	
	public void drawVerticalText(AbstractTextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed)
	{
		if (graphics!=null)
		{
			layout.draw(graphics, x, y);
		}
		
		if (svgDocument!=null && bottomElement!=null && getSvgFlavor()!=null && getSvgFlavor().getSvgVersion()<1.2)
		{
			Element textElement = null;
			
			textElement = svgDocument.createElement(StyledDocumentStraw.IDENTIFIER_TEXT);
			
			straw.save(svgDocument, textElement, startCharacterOffset, layout.getCharacterCount(), null);
			
			textElement.setAttribute(IDENTIFIER_X, String.valueOf(x));
			textElement.setAttribute(IDENTIFIER_Y, String.valueOf(y-layout.getAscent()));
			textElement.setAttribute(IDENTIFIER_WRITING_MODE, IDENTIFIER_WRITING_MODE_TOP_TO_BOTTOM);
			textElement.setAttribute(IDENTIFIER_GLYPH_ORIENTATION_VERTICAL, "0");
			
			bottomElement.appendChild(textElement);
		}
	}

	
	/**
	 * @return the svg flavor, if one was selected.
	 */
	public SvgFlavor getSvgFlavor()
	{
		if (exportProfile==null)
			return null;
		
		return exportProfile.getSvgFlavor();
	}
	
	/**
	 * Draws/outputs an entire styled document, without breaking it up into
	 * lines. This is useful for laying down SVG 1.2 flow text.
	 *
	 * @return true, if it actually did anything
	 */
	public boolean draw(StyledDocumentStraw straw, Shape designatedShape, int sectionIndex)
	{
		SvgFlavor flavor = getSvgFlavor();
		
		if (svgDocument!=null && bottomElement!=null && flavor!=null && flavor.getSvgVersion()>=1.2)
		{
			org.w3c.dom.Element flowRoot = null;
			org.w3c.dom.Element flowDiv = null;
			org.w3c.dom.Element flowRegion = null;
			
			flowRoot = flavor.createFlowRootElement(svgDocument);
			flowDiv = flavor.createFlowDivElement(svgDocument);
			flowRegion = flavor.createFlowRegionElement(svgDocument);
			
			Logger.print("flowRegion=");
			Logger.println(flowRegion);
			
			Element path = shapeToElement(designatedShape);
			
			flowRegion.appendChild(path);
			flowRoot.appendChild(flowRegion);
			
			org.w3c.dom.Element flowPara = flavor.createFlowParaElement(svgDocument);
			
			straw.save(svgDocument, flowPara, -1, -1, flavor.getStyledTextElementName());
			flowPara.setAttribute(IDENTIFIER_VERT_ALIGN_ATTR, IDENTIFIER_VERT_ALIGN_MIDDLE_VAL); // or does this belong on flowRoot?
			flowPara.setAttribute(IDENTIFIER_TEXT_ALIGN, IDENTIFIER_ALIGN_CENTER_VAL);
			
			// Acording to Dean Jackson's October 2003 email:
			//   "My recollection is that it was the block-level elements: flowPara and flowDiv."
			
			flowDiv.appendChild(flowPara);
			
			flowRoot.appendChild(flowDiv);
			
			// System.out.println("appending: "+textAsString+ " on: " + flowRoot + " to parent: "+parent);
			
			bottomElement.appendChild(flowRoot);
			
			return true;
		}
		
		return false;
	}
	
	public void startGroup(String name)
	{
		if (bottomElement!=null)
		{
			Element group = svgDocument.createElement(IDENTIFIER_GROUP);
			
			if (name!=null)
				group.setAttribute(IDENTIFIER_ID, caulkAndUniquify(name));
					
			bottomElement.appendChild(group);
			bottomElement = group;
		}
	}
	
	public void startGroup()
	{
		startGroup(null);
	}
	
	/**
	 * Stop adding new drawables to the current group, and untranslate if necessary.
	 */
	public void endGroup()
	{
		if (bottomElement!=null)
		{
			double x=0.0;
			double y=0.0;
			
			if (bottomElement.hasAttribute(IDENTIFIER_X))
			{
				try
				{ x=Double.parseDouble(bottomElement.getAttribute(IDENTIFIER_X)); }
				catch (NumberFormatException e)
				{}
			}
			
			if (bottomElement.hasAttribute(IDENTIFIER_Y))
			{
				try
				{ y=Double.parseDouble(bottomElement.getAttribute(IDENTIFIER_Y)); }
				catch (NumberFormatException e)
				{}
			}
			
			if (x!=0.0 || y!=0.0)
				translate(-x, -y);
			
			if (bottomElement!=null)
				bottomElement = (Element) bottomElement.getParentNode();
		}
	}
	
	public void drawFilled(Shape designatedShape, Color designatedOutlineColor, Color designatedFillColor)
	{
		if (designatedShape==null)
			return;
		
		if (designatedOutlineColor==null && designatedFillColor==null)
			return;
		
		if (graphics!=null)
		{
			if (designatedFillColor!=null)
			{
				graphics.setColor(designatedFillColor);
				graphics.fill(designatedShape);
			}
			
			if (designatedOutlineColor!=null)
			{
				graphics.setColor(designatedOutlineColor);
				draw(designatedShape);
			}
		}
		
		if (bottomElement!=null)
		{
			Element shapeElement = shapeToElement(designatedShape);
			
			if (designatedFillColor!=null)
			{
				shapeElement.setAttribute(IDENTIFIER_FILL, ShapeFriend.describeWithoutAlpha(designatedFillColor));
			}
			
			if (designatedOutlineColor!=null)
			{
				shapeElement.setAttribute(IDENTIFIER_STROKE, ShapeFriend.describeWithoutAlpha(designatedOutlineColor));
			}
			
			bottomElement.appendChild(shapeElement);
		}
	}
		
	private final ShapingContext shapingContext = new ShapingContext()
	{
		public void setIntermediate(boolean designatedIntermediate)
		{
			DefaultDrawingContext.this.setIntermediate(designatedIntermediate);
		}
		
		public boolean isIntermediate()
		{
			return DefaultDrawingContext.this.isIntermediate();
		}
	};
	
	public void draw(Shapeable shapeable)
	{
		if (shapeable!=null)
		{
			draw(shapeable.toShape(shapingContext, this));
		}
	}
	
	public void setShootingRange(ShootingRange designatedShootingRange)
	{
		shootingRange = designatedShootingRange;
	}
	
	public ShootingRange getShootingRange()
	{
		return shootingRange;
	}
	
	public void draw(OrientableTextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed)
	{
		if (!layout.isVertical())
			drawHorizontalText(layout.getHorizontalTextLayout(), x, y, straw, startCharacterOffset, coordinateFrameUsed);
		else
			drawVerticalText(layout.getVerticalTextLayout(), x, y, straw, startCharacterOffset, coordinateFrameUsed);
	}
	
	public void pretendToDraw(OrientableTextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed)
	{
		if (layout.isVertical())
			layout.draw(null, x, y);
	}
	
	/**
	 * Adds an XML comment, if an SVG document is in progress.
	 */
	public void comment(String designatedString)
	{
		if (bottomElement!=null && svgDocument!=null)
		{
			Comment comment = svgDocument.createComment(designatedString);
			bottomElement.appendChild(comment);
		}
	}
	
	public void fill(Shape designatedShape, int x, int y)
	{
		if (graphics!=null)
		{
			graphics.translate(x, y);
			graphics.fill(designatedShape);
			graphics.translate(-x, -y);
		}

		if (bottomElement!=null)
		{
			Element shapeElement = shapeToElement(designatedShape, x, y);
			applyStyle(shapeElement, false);
			bottomElement.appendChild(shapeElement);
		}
	}
	
	public void fillRect(int x, int y, int width, int height)
	{
		if (graphics!=null)
		{
			graphics.fillRect(x, y, width, height);
		}
		
		if (bottomElement!=null && svgDocument!=null)
		{
			Element rectElement = svgDocument.createElement(IDENTIFIER_RECT);
			rectElement.setAttribute(IDENTIFIER_X, String.valueOf(x));
			rectElement.setAttribute(IDENTIFIER_Y, String.valueOf(y));
			rectElement.setAttribute(IDENTIFIER_WIDTH, String.valueOf(width));
			rectElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(height));

			applyStrokeWidth(rectElement);
			
			bottomElement.appendChild(rectElement);
		}
	}
	
	public void setTransform(AffineTransform designatedTransform)
	{
		if (designatedTransform==null)
			designatedTransform = new AffineTransform();
		
		if (graphics!=null)
		{
			graphics.setTransform(designatedTransform);
		}
		
		if (svgDocument!=null)
		{
			// TODO ...
		}
	}

	/**
	 * @return true when the Drawing Context is in an intermediate state.
	 */
	public boolean isIntermediate()
	{
		return intermediate;
	}

	/**
	 * @param designatedIntermediacy whether or not to put the drawing context into
	 *        an intermediate state. This is helpful for deferring expensive drawing
	 *        operations to a more convenient time, for example to after a resizing.
	 */
	public void setIntermediate(boolean designatedIntermediacy)
	{
		if (intermediate != designatedIntermediacy)
		{
			intermediate = designatedIntermediacy;
			// System.out.println("setting Intermediacy: "+intermediate);
		}
	}
	
	public static int countSteps(GeneralPath designatedGeneralPath)
	{
		PathIterator walk = designatedGeneralPath.getPathIterator(null);
				
		float [] coords = new float[6];
		int iters = 0;

		while (!walk.isDone())
		{
			int kind = walk.currentSegment(coords);
			// System.out.println(kind+"\tcoords: "+coords[0]+coords[1]+coords[2]+coords[3]+coords[4]);
			walk.next();
			iters++;
		}

		return iters;
	}

	public Paint getPaint()
	{
		if (graphics==null)
			return null;
		else
			return graphics.getPaint();
		
		// TODO support svg
	}
	
	public void setPaint(Paint designatedPaint)
	{
		if (graphics!=null)
			graphics.setPaint(designatedPaint);
			
		// TODO support svg
	}
	
	public void drawRect(int x, int y, int width, int height)
	{
		if (graphics!=null)
			graphics.drawRect(x, y, width, height);
			
		if (bottomElement!=null && svgDocument!=null)
		{
			Element rectElement = svgDocument.createElement(IDENTIFIER_RECT);
			rectElement.setAttribute(IDENTIFIER_X, String.valueOf(x));
			rectElement.setAttribute(IDENTIFIER_Y, String.valueOf(y));
			rectElement.setAttribute(IDENTIFIER_WIDTH, String.valueOf(width));
			rectElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(height));
			
			applyStrokeWidth(rectElement);
			
			bottomElement.appendChild(rectElement);
		}
	}
	
	public void scale(double s)
	{
		if (graphics!=null)
		{
			graphics.scale(s, s);		
			scaleFactor = s;
		}
	}
	
	/**
	 * @return an informational indicator of how much the graphics implementation associated with
	 * this drawing context is "scaled". You typically shouldn't need to know this... in other words,
	 * it isn't your responsiblity to draw things bigger or smaller based on this factor. It occasionally
	 * might be helpful to know for a peripheral rendering issue. The one it was added to support is
	 * knowing how much to blur edges during smooth scaling.
	 */
	public double getScaleFactor()
	{
		return scaleFactor;
	}
	
	public boolean isVisible(Drawable drawable)
	{
		return drawingFilter.pass(drawable);
	}
	
	public DrawingFilter getDrawingFilter()
	{
		return drawingFilter;
	}
	
	public ShapingContext getShapingContext()
	{
		return shapingContext;
	}
	
	public String caulkAndUniquify(String proposedGroupName)
	{
		if (proposedGroupName==null)
			return proposedGroupName;
			
		proposedGroupName = proposedGroupName.trim();
		
		proposedGroupName = proposedGroupName.replaceAll(" ", "_"); // TODO replace other nastry characters
		
		String groupName = proposedGroupName;
		
		for (int index=2; svgGroupNames.contains(groupName); index++)
		{
			groupName = proposedGroupName + index;
		}
		
		svgGroupNames.add(groupName);
		
		return groupName;
	}
	
	public void hintBilinearInterpolation()
	{
		if (graphics!=null)
		{
			graphics.getRenderingHints().put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}
	}
	
	public boolean isAntiAliasingLinesAndImages()
	{
		return (graphics.getRenderingHint(RenderingHints.KEY_ANTIALIASING)==RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	protected void applyStrokeWidth(Element element)
	{
		if (getStroke()!=null && getStroke() instanceof BasicStroke)
		{
			BasicStroke asBasicStroke = (BasicStroke) getStroke();
			if (asBasicStroke.getLineWidth()!=1.0)
			{
				element.setAttribute(IDENTIFIER_STROKE_WIDTH,
						String.valueOf(asBasicStroke.getLineWidth()));
			}
		}
	}

	public void drawGlyphVector(GlyphVector glyphVector, float x, float y)
	{
		if (graphics!=null)
			graphics.drawGlyphVector(glyphVector, x, y);
		
		// TODO SVG support
	}

	public boolean isTargetingPdf()
	{
		return targetingPdf;
	}

	public void setTargetingPdf(final boolean designatedTargetingPdf)
	{
		targetingPdf = designatedTargetingPdf;
	}
}
