/**
 * Copyleft 2007 by Dave Horlick

 */

package com.smithandtinkers.graphics.awt;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Wraps Graphics2D and listens for an invocation of one of the drawString methods.
 *
 * @author dhorlick
 */
public class TextinessSniffingGraphics2D extends Graphics2D
{	
	private Graphics2D g2;
	private boolean drawStringInvoked = false;
	
	public TextinessSniffingGraphics2D(final Graphics2D wrappable)
	{	
		g2 = wrappable;
	}

	public void draw(Shape s)
	{	
		g2.draw(s);
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs)
	{	
		return g2.drawImage(img,xform,obs);
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y)
	{	
		g2.drawImage(img,op,x,y);
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform)
	{	
		g2.drawRenderedImage(img,xform);
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform)
	{
		g2.drawRenderableImage(img,xform);
	}

	public void drawString(String str, int x, int y)
	{
		g2.drawString(str,x,y);
		setDrawStringInvoked(true);
	}

	public void drawString(String s, float x, float y)
	{
		setDrawStringInvoked(true);
		g2.drawString(s,x,y);
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y)
	{
		setDrawStringInvoked(true);
		g2.drawString(iterator,x,y);
	}

	public void drawString(AttributedCharacterIterator iterator, float x, float y)
	{
		setDrawStringInvoked(true);
		g2.drawString(iterator,x,y);
	}

	public void drawGlyphVector(GlyphVector g, float x, float y)
	{
		g2.drawGlyphVector(g,x,y);
	}

	public void fill(Shape s)
	{	
		g2.fill(s);
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke)
	{	
		return g2.hit(rect,s,onStroke);
	}

	public GraphicsConfiguration getDeviceConfiguration()
	{	
		return g2.getDeviceConfiguration();
	}

	public void setComposite(Composite comp)
	{	
		g2.setComposite(comp);
	}

	public void setPaint(Paint paint)
	{	
		g2.setPaint(paint);
	}

	public void setStroke(Stroke s)
	{	
		g2.setStroke(s);
	}

	public void setRenderingHint(Key hintKey, Object hintValue)
	{	
		g2.setRenderingHint(hintKey,hintValue);
	}

	public Object getRenderingHint(Key hintKey)
	{	
		return g2.getRenderingHint(hintKey);
	}

	public void setRenderingHints(Map hints)
	{	
		g2.setRenderingHints(hints);
	}

	public void addRenderingHints(Map hints)
	{
		g2.addRenderingHints(hints);
	}

	public RenderingHints getRenderingHints()
	{
		return g2.getRenderingHints();
	}

	public void translate(int x, int y)
	{
		g2.translate(x,y);
	}

	public void translate(double tx, double ty)
	{
		g2.translate(tx,ty);
	}

	public void rotate(double theta)
	{
		g2.rotate(theta);
	}

	public void rotate(double theta, double x, double y)
	{
		g2.rotate(theta,x,y);
	}

	public void scale(double sx, double sy)
	{
		g2.scale(sx,sy);
	}

	public void shear(double shx, double shy)
	{
		g2.shear(shx,shy);
	}

	public void transform(AffineTransform Tx)
	{
		g2.transform(Tx);
	}

	public void setTransform(AffineTransform Tx)
	{
		g2.setTransform(Tx);
	}

	public AffineTransform getTransform()
	{
		return g2.getTransform();
	}

	public Paint getPaint()
	{
		return g2.getPaint();
	}

	public Composite getComposite()
	{
		return g2.getComposite();
	}

	public void setBackground(Color color)
	{
		g2.setBackground(color);
	}

	public Color getBackground()
	{
		return g2.getBackground();
	}

	public Stroke getStroke()
	{
		return g2.getStroke();
	}

	public void clip(Shape s)
	{
		g2.clip(s);
	}

	public FontRenderContext getFontRenderContext()
	{
		return g2.getFontRenderContext();
	}

	public Graphics create()
	{
		return g2.create();
	}

	public Color getColor()
	{
		return g2.getColor();
	}

	public void setColor(Color c)
	{
		g2.setColor(c);
	}

	public void setPaintMode()
	{
		g2.setPaintMode();
	}

	public void setXORMode(Color c1)
	{
		g2.setXORMode(c1);
	}

	public Font getFont()
	{
		return g2.getFont();
	}

	public void setFont(Font font)
	{
		g2.setFont(font);
	}

	public FontMetrics getFontMetrics(Font f)
	{
		return g2.getFontMetrics(f);
	}

	public Rectangle getClipBounds()
	{
		return g2.getClipBounds();
	}

	public void clipRect(int x, int y, int width, int height)
	{
		g2.clipRect(x,y,width,height);
	}

	public void setClip(int x, int y, int width, int height)
	{
		g2.setClip(x,y,width,height);
	}

	public Shape getClip()
	{
		return g2.getClip();
	}

	public void setClip(Shape clip)
	{
		g2.setClip(clip);
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy)
	{
		g2.copyArea(x,y,width,height,dx,dy);
	}

	public void drawLine(int x1, int y1, int x2, int y2)
	{
		g2.drawLine(x1,y1,x2,y2);
	}

	public void fillRect(int x, int y, int width, int height)
	{
		g2.fillRect(x,y,width,height);
	}

	public void clearRect(int x, int y, int width, int height)
	{
		g2.clearRect(x,y,width,height);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		g2.drawRoundRect(x,y,width,height,arcWidth,arcHeight);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		g2.fillRoundRect(x,y,width,height,arcWidth,arcHeight);
	}

	public void drawOval(int x, int y, int width, int height)
	{
		g2.drawOval(x,y,width,height);
	}

	public void fillOval(int x, int y, int width, int height)
	{
		g2.fillOval(x,y,width,height);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g2.drawArc(x,y,width,height,startAngle,arcAngle);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		g2.fillArc(x,y,width,height,startAngle,arcAngle);
	}

	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints)
	{		
		g2.drawPolyline(xPoints,yPoints,nPoints);
	}

	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{		
		g2.drawPolygon(xPoints,yPoints,nPoints);
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints)
	{		
		g2.fillPolygon(xPoints,yPoints,nPoints);
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer)
	{		
		return g2.drawImage(img, x, y, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer)
	{		
		return g2.drawImage(img, x, y, width, height, observer);
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer)
	{		
		return g2.drawImage(img, x, y, bgcolor, observer);
	}

	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer)
	{		
		return g2.drawImage(img, x, y, width, height, bgcolor, observer);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
	{		
		return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer)
	{		
		return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
	}

	public void dispose()
	{		
		g2.dispose();
	}
	
	public boolean isDrawStringInvoked()
	{
		return drawStringInvoked;
	}

	private void setDrawStringInvoked(final boolean b)
	{
		drawStringInvoked = b;
	}
}
