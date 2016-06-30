/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.graphics;

import com.smithandtinkers.geom.Shapeable;
import com.smithandtinkers.geom.ShapingContext;
import com.smithandtinkers.layout.Drawable;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.ShootingRange;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.layout.ExportProfile;
import com.smithandtinkers.text.OrientableTextLayout;
import com.smithandtinkers.text.StyledDocumentStraw;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import org.w3c.dom.Element;

/**
 * An interface thru which stuff can be drawn to various targets.
 * 
 * You can get an instance of one from {@link com.smithandtinkers.graphics.DrawingContextFactory}
 *
 * @author dhorlick
 */
public interface DrawingContext extends PlottingContext, ShapingContext
{
	public static final String IDENTIFIER_X = "x";
	public static final String IDENTIFIER_Y = "y";
	public static final String IDENTIFIER_GROUP = "g";
	public static final String IDENTIFIER_ID = "id";
	public static final String IDENTIFIER_RECT = "rect";
	public static final String IDENTIFIER_WIDTH = "width";
	public static final String IDENTIFIER_HEIGHT = "height";
	public static final String IDENTIFIER_SVG = "svg";
	public static final String IDENTIFIER_TRANSFORM = "transform";
	public static final String IDENTIFIER_STROKE = "stroke";
	public static final String IDENTIFIER_FILL = "fill";
	public static final String IDENTIFIER_NONE = "none";
	public static final String IDENTIFIER_PATH = "path";
	public static final String IDENTIFIER_DESCRIPTION = "d";
	public static final String IDENTIFIER_VERSION = "version";
	public static final String IDENTIFIER_CLIP_PATH_ELEM = "clipPath";
	public static final String IDENTIFIER_CLIP_PATH_ATTR = "clip-path";
	public static final String IDENTIFIER_DEFS = "defs";
	public static final String IDENTIFIER_ELLIPSE = "ellipse";
	public static final String IDENTIFIER_CENTER_X = "cx";
	public static final String IDENTIFIER_CENTER_Y = "cy";
	public static final String IDENTIFIER_RADIUS_X = "rx";
	public static final String IDENTIFIER_RADIUS_Y = "ry";
	public static final String IDENTIFIER_STROKE_WIDTH = "stroke-width";
	public static final String IDENTIFIER_LINE = "line";
	public static final String IDENTIFIER_X1 = "x1";
	public static final String IDENTIFIER_X2 = "x2";
	public static final String IDENTIFIER_Y1 = "y1";
	public static final String IDENTIFIER_Y2 = "y2";
	
	public static final String IDENTIFIER_VERT_ALIGN_ATTR = "vertical-align";
	public static final String IDENTIFIER_VERT_ALIGN_MIDDLE_VAL = "middle";
	public static final String IDENTIFIER_TEXT_ALIGN = "text-align";
	public static final String IDENTIFIER_ALIGN_CENTER_VAL = "center";
	
	public static final String IDENTIFIER_WRITING_MODE = "writing-mode";
		public static final String IDENTIFIER_WRITING_MODE_TOP_TO_BOTTOM = "tb";
	
		public static final String IDENTIFIER_GLYPH_ORIENTATION_VERTICAL = "glyph-orientation-vertical";
	
	public static final String KEYWORD_CLIP = "clip";
	
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
	
	public void antiAliasLines();

	/**
	 * Adds an XML comment, if an SVG document is in progress.
	 */
	public void comment(String designatedString);

	public void draw(OrientableTextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed);

	public void draw(Shape designatedShape);

	public void draw(Shapeable shapeable);

	/**
	 * Draws/outputs an entire styled document, without breaking it up into
	 * lines. This is useful for laying down SVG 1.2 flow text.
	 * 
	 * @return true, if it actually did anything
	 */
	public boolean draw(StyledDocumentStraw straw, Shape designatedShape, int sectionIndex);

	public void drawFilledOval(int x, int y, int width, int height, Color designatedOutlineColor, Color designatedFillColor);
	
	public void drawFilledOval(double x, double y, double width, double height, Color designatedOutlineColor, Color designatedFillColor);
	
	public void drawImage(Image img, int x, int y, ImageObserver observer);

	public void drawImage(Image img, int x, int y, int width, int height, ImageObserver observer);

	public void drawRect(int x, int y, int width, int height);

	public void drawString(String str, float x, float y);

	public void drawString(String str, int x, int y);

	/**
	 * Stop adding new drawables to the current group, and untranslate if necessary.
	 */
	public void endGroup();

	public void fill(Shape designatedShape);

	public void fill(Shape designatedShape, int x, int y);

	public void fillOval(int x, int y, int width, int height);

	public void fillPolygon(final int[] xCoords, final int[] yCoords, int numberOfCoords);

	public void fillRect(int x, int y, int width, int height);

	public Element getBottomElement();

	public Shape getClip();

	public Color getColor();

	public DrawingFilter getDrawingFilter();

	public FontMetrics getFontMetrics();

	public FontRenderContext getFontRenderContext();

	public Paint getPaint();

	public ShapingContext getShapingContext();

	public boolean isAntiAliasingLinesAndImages();

	public boolean isVisible(Drawable drawable);

	public void pretendToDraw(OrientableTextLayout layout, float x, float y, StyledDocumentStraw straw, int startCharacterOffset, Sill coordinateFrameUsed);

	public void setColor(Color designatedColor);

	public void setPaint(Paint designatedPaint);

	public void startGroup();

	public void startGroup(String name);

	public void translate(double dx, double dy);

	public void setSelected(Selection selection);

	public void setIntermediate(boolean b);

	public void setClip(Shape oldClip);

	public ShootingRange getShootingRange();

	public void setTransform(AffineTransform affineTransform);

	public void clip(Shape temporaryClippingShape);

	public boolean isIntermediate();

	void setExportProfile(ExportProfile transmog);
	
	public void scale(double s);
	
	/**
	 * @return an informational indicator of how much the graphics implementation associated with
	 * this drawing context is "scaled". You typically shouldn't need to know this... in other words,
	 * it isn't your responsibility to draw things bigger or smaller based on this factor. It occasionally
	 * might be helpful to know for a peripheral rendering issue. The one it was added to support is
	 * knowing how much to blur edges during smooth scaling.
	 */
	public double getScaleFactor();

	public void drawGlyphVector(GlyphVector glyphVector, float x, float y);

	public boolean isTargetingPdf();

	public void setTargetingPdf(final boolean designatedTargetingPdf);
}
