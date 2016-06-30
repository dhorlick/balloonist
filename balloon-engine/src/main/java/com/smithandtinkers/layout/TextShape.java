/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.graphics.PlottingContext;
import com.smithandtinkers.text.TextChum;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.control.Optimizer;
import com.smithandtinkers.geom.*;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.mvc.TreeTypesafeList;
import com.smithandtinkers.text.StyledDocumentStraw;
import com.smithandtinkers.text.VerticalLineBreakMeasurer;
import com.smithandtinkers.util.*;
import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.graphics.PaintStore;
import com.smithandtinkers.text.OrientableLineBreakMeasurer;
import com.smithandtinkers.text.OrientableTextLayout;


/**
 * A region defined by one or more {@link Perch}es amongst which styled text can
 * be flowed.
 *
 * @author dhorlick
 */
public abstract class TextShape extends TreeTypesafeList
		implements Crowdable, Drawable, Resizeable, Perimetered, Relocateable, Shapeable,
			Layoutable, Stemmed, Colorful, Marginal, Orientable, Thick
{
	private static final String IDENTIFIER_FILL_COLOR = "fill-color";
	private static final String IDENTIFIER_OUTLINE_COLOR = "outline-color";
	
	private static final String IDENTIFIER_EDITION = "edition";
		
	
	private BasicStroke stroke = new BasicStroke();
	
	public static final Stroke DASHED_STROKE = new BasicStroke(1,
		    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
		    new float[] { 5, 4 }, 0);
	
	/**
	 <p>The preferred boundary of this TextShape. If null, will attempt to
	 determine a boundary that fits the stated preferences.</p>
	 */
	protected Shape shape;
	
	protected StyledDocumentStraw straw = new StyledDocumentStraw(); 
	
	private Drawable annotates;
	
	private boolean layedOut = true;
	private VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
	
	private Color fillColor = Color.white;
	private Color outlineColor = Color.black;
	
	private double innerMarginInPoints = BalloonEngineState.determineDefaultMargin();
	
	private final ShapingContext shapingContext = new ConcreteShapingContext();
	
	private int charIndex; // this probably should be elsewhere
	
	private double lineThickness = 1.0;
	private boolean stemInBack;
	
	public final static Color TRANSLUCENT_RED  = new Color(1.0f,0.2f,0.2f,.5f);
	public final static Color LIGHT_BLUE = new Color(0.75f,0.75f,1.0f);
	
	final protected ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	public TextShape(StyledDocument doc)
	{
		super(Perch.class);
		
		if (PlatformFriend.RUNNING_ON_JAVA_14_OR_HIGHER && doc instanceof AbstractDocument)
			new com.smithandtinkers.text.FirstCharacterHelpingDocumentFilter((AbstractDocument)doc);
	
		setText(doc);
		connectStraw();
		centerer.setTolerance(new double[] {0.90, 0.0}); // TODO try and get tolerance #0 down some more
	}
	
	public TextShape()
	{
		this(new DefaultStyledDocument());
	}
	
	public TextShape(StyledDocument doc, Crowd designatedParent)
	{
		this(doc);
		setParent(designatedParent);
	}

	public Shape preferredShape(PlottingContext plottingContext)
	{ 
		switch (size())
		{
			case 0: return null;
			case 1: 
				Shapeable onlyShapeable = (Shapeable)get(0);
				return onlyShapeable.toShape(shapingContext, plottingContext); // TODO: wasteful
			default:
				Shapeable firstShapeable = (Shapeable)get(0);
				Area area = new Area(firstShapeable.toShape(shapingContext, plottingContext));
				
				for (int loop=1; loop<=size()-1; loop++)
				{
					Shapeable shapeable = (Shapeable) get(loop);
					area.add(new Area(shapeable.toShape(shapingContext, plottingContext))); // TODO: extremely wasteful
				}
				
				return area;
		}	
	}
	
	public Shape preferredPerchedShape(boolean inner, PlottingContext plottingContext)
	{
		switch (size())
		{
			case 0: return null;
			case 1: 
				Shapeable onlyShapeable = (Shapeable)get(0);
				
				if (onlyShapeable instanceof Perch)
				{
					Perch perch = (Perch) onlyShapeable;
					
					if (inner)
						return perch.getInner().toShape(shapingContext, plottingContext);
					else
						return perch.getOuter().toShape(shapingContext, plottingContext);
				}
				else
				{
					return onlyShapeable.toShape(shapingContext, plottingContext); // TODO: wasteful
				}
				
			default:
				Shapeable firstShapeable = (Shapeable)get(0);
				
				Area area = null;
				
				if (firstShapeable instanceof Perch)
				{
					Perch firstPerch = (Perch) firstShapeable; 
					
					if (inner)
						area = new Area(firstPerch.getInner().toShape(shapingContext, plottingContext));
					else
						area = new Area(firstPerch.getOuter().toShape(shapingContext, plottingContext));
				}
				else
				{
					area = new Area(firstShapeable.toShape(shapingContext, plottingContext));
				}
				
				for (int loop=1; loop<=size()-1; loop++)
				{
					Shapeable shapeable = (Shapeable) get(loop);
					
					if (shapeable instanceof Perch)
					{
						Perch perch = (Perch) shapeable;
						
						if (inner)
							area.add(new Area(perch.getInner().toShape(shapingContext, plottingContext)));
						else
							area.add(new Area(perch.getOuter().toShape(shapingContext, plottingContext)));
					}
					else
					{
						area.add(new Area(shapeable.toShape(shapingContext, plottingContext))); // TODO: extremely wasteful
					}
				}
				
				return area;
		}
	}
	
	public Shape preferredPerchedInnerShape(PlottingContext plottingContext)
	{
		return preferredPerchedShape(true, plottingContext);
	}
	
	public Shape preferredPerchedOuterShape(PlottingContext plottingContext)
	{
		return preferredPerchedShape(false, plottingContext);
	}
	
	public void draw(DrawingContext drawingContext)
	{
		boolean showGuides = false;
		
		shapingContext.setIntermediate(drawingContext.isIntermediate()
				&& drawingContext.getSelected()!=null
				&& !drawingContext.getSelected().containsInstanceOf(Stem.class)); // TODO use drawingContext.getShapingContext() instead
		
		Selection selected = drawingContext.getSelected();
		
		if (selected!=null && selected.isSelected(this))
			showGuides = true;
		
		shape = toShape(shapingContext, drawingContext);
		
		if (shape==null)
		{
			// there isn't anything to draw.
			
			return;
		}
		// System.out.println("newly-forged shape: "+shape);
		
		if (selected!=null)
		{
			if (selected.isDirty())
			{
				if (selected.getRectChoice()!=null)
				{
					// don't try this at home
					
					double originalX = selected.getRectChoice().getX();
					double originalY = selected.getRectChoice().getY();
					
					selected.getRectChoice().setRect(
						// mapOutsideX(originalX), 
						Sill.mapAbsoluteXIntoCoordinateFrameOf(originalX, this), 
						// mapOutsideY(originalY), 
						Sill.mapAbsoluteYIntoCoordinateFrameOf(originalY, this),
						selected.getRectChoice().getWidth(), 
						selected.getRectChoice().getHeight());
					
					if (shape.intersects(selected.getRectChoice()))
					{
						selected.registerSelection(this);
						// clearDirtyFlag = true;
					}
					
					selected.getRectChoice().setRect(originalX, originalY, selected.getRectChoice().getWidth(), selected.getRectChoice().getHeight());
				}
				if (selected.getPointChoice()!=null)
				{
					// double localX = mapOutsideX(selected.getPointChoice().getX());
					double localX = Sill.mapAbsoluteXIntoCoordinateFrameOf(selected.getPointChoice().getX(), this);
					// double localY = mapOutsideY(selected.getPointChoice().getY());
					double localY = Sill.mapAbsoluteYIntoCoordinateFrameOf(selected.getPointChoice().getY(), this);
					if (shape.contains(localX, localY))
					{
						selected.registerSelection(this);
						// selected.setDirty(false, this);
					}
				}
			}
			
		}

		Rectangle2D resizeableBounds = null;
		
		if (showGuides)
		{
			drawingContext.setColor(Resizeable.TRANSLUCENT_BLUE);
			
			Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			
			final Paint originalTexture = drawingContext.getPaint();
			
			if (focusOwner instanceof javax.swing.JTextPane)
			{
				drawingContext.setPaint(PaintStore.getInstance().getTurbulentWaterTexture());
			}
			
			resizeableBounds = getResizeableBounds2D();
			drawingContext.fill(resizeableBounds);
			
			if (focusOwner instanceof javax.swing.JTextPane)
			{
				drawingContext.setPaint(originalTexture);
			}
			
			// TODO iterate over shapeables and call a new public Rectangle2D paintSelection() method in Drawable on each.
		}

		drawingContext.setColor(getFillColor());
		
		final Stroke OLD_STROKE = drawingContext.getStroke();
		drawingContext.setStroke(stroke);
		
		if (isStemInBack())
			drawAdornments(drawingContext, selected);
		
		drawingContext.drawFilled(shape, outlineColor, fillColor);
		drawingContext.setStroke(OLD_STROKE);
				
		if (reasonablyBig(resizeableBounds))
		{
			// Shape actualPerched = actualPerchedShape(drawingContext);

			if (getText()!= null && getText().getLength()>0)
			{
				charIndex = 0;
				
				for (int section=0; section<=size()-1; section++)
				{
					drawTextAligned(drawingContext, showGuides, section, selected);
					charIndex++; // skip over tab character
				}
				
				if (!isLayedOut())
					setLayedOut(true);
			}
		}
		
		if (showGuides)
			AbstractResizeable.drawAfter(drawingContext, resizeableBounds);
		
		if (!isStemInBack())
		{
			drawingContext.setStroke(stroke);
			drawAdornments(drawingContext, selected);
		}
		
		drawingContext.setStroke(OLD_STROKE);
	}
	
	private boolean reasonablyBig(Rectangle2D bounds)
	{
		// TODO make sure bounds are being computed for unselected text shapes so we can make this evaluation?
		
		return (bounds==null || (bounds.getWidth()>25 && bounds.getHeight()>20));
	}
	
	private Optimizer centerer = new Optimizer(); // not multithreaded!
	private TextShapeToControllableAdaptor tstcAdaptor = new TextShapeToControllableAdaptor(); // not multithreaded!
	
	private void drawTextAligned(DrawingContext drawingContext, boolean showGuides, int sectionIndex, Selection selection)
	{	
		Perch sectionPerch = (Perch) get(sectionIndex);
		Shape textPerimeter = actualPerchedInnerShape(sectionIndex);
		
		if (showGuides)
		{
			final Stroke OLD_STROKE = drawingContext.getStroke();
			drawingContext.setStroke(DASHED_STROKE);
			drawingContext.setColor(Color.red);
			drawingContext.draw(textPerimeter);
			drawingContext.setStroke(OLD_STROKE);
		}
		
		if (verticalAlignment==VerticalAlignment.CENTER &&!drawingContext.isIntermediate() && !isLayedOut())
		{
			// System.out.println("aligning...");
			
			Rectangle2D bounds = getResizeableBounds2D(textPerimeter); // TODO keep this for later. also: use textPerimeter.getBounds() instead?
			
			double maxU = 0.0;
			
			if (straw.isVertical())
				maxU  = bounds.getWidth()/2.0;
			else
				maxU = bounds.getHeight()/2.0;
			
			if (maxU>0.0)
			{
				tstcAdaptor.setDrawingContext(drawingContext);
				tstcAdaptor.setActual(textPerimeter);
				tstcAdaptor.setTextShape(this);
				tstcAdaptor.setSectionIndex(sectionIndex);
				
				centerer.setMinX(0.0);
				
				centerer.setMaxX( maxU );
				centerer.setStepX(1.0); // TODO move this into constructor
				centerer.optimize(tstcAdaptor);
				
				// System.out.println("*** centerered! ***");
			}
			else
			{
				// System.out.println("Setting maxX="+maxU+" to zero & skipping optimize.");
			}			
		}
		
		drawText(drawingContext, showGuides, textPerimeter, null, selection, sectionIndex);
	}
	
	/**
	 * @param showGuides if true, will draw gray line rules
	 * @param textPerimeter The shape in which the text lines should be circumscribed.
	 * @param misalignment an optional array. if present, will populate this with the
	 *        following measurements:
	 *           1) vertical axis misalignment
	 *           2) Number of characters that wouldn't fit.
	 *
	 *        Zero is the best possible value for these. Assumes that the array elements
	 *        are passed in as zero.
	 */
	public void drawText(DrawingContext drawingContext, boolean showGuides, Shape textPerimeter, final double [] misalignment, Selection selected, int sectionIndex)
	{
		charIndex = 0;
		
		if (drawingContext.draw(straw, textPerimeter, sectionIndex))
			return; // if this was handled as SVG 1.2 output, we can stop now
		
		boolean invisible = (misalignment!=null);
		
		Alignable alignable = (Alignable) get(sectionIndex);
		double gapBetweenMarginAndText = alignable.getGapBetweenMarginAndText();
		
		float penAlongLineAxis = 0.0f; // was pen.x before i8n
		float penAlongPageAxis = 0.0f;
		
		float lastPenAlongPageAxis = penAlongPageAxis;
		
		double textStartAlongPageAxis = -1;
		double textEndAlongPageAxis = -1;
		
		// Logger.println("drawingContext.getFontRenderContext().isAntiAliased()="+drawingContext.getFontRenderContext().isAntiAliased());
		// FontRenderContext pointlessFontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
		
		AttributedCharacterIterator bridged = straw.getIterator(sectionIndex);
		
		if (bridged==null)
			return;
		
		final StringBuffer bridgedCopy = TextChum.characterIteratorToStringBuffer((AttributedCharacterIterator)bridged.clone()); // TODO is cloning really necessary?
		// System.out.println("traversing: "+bridgedCopy);
		
		FontRenderContext pointlessFontRenderContext = drawingContext.getFontRenderContext(); // TODO truly pointless? Should the be varied thruout the loop?
		
		OrientableLineBreakMeasurer breaker19 = new OrientableLineBreakMeasurer();
		
		if (straw.isVertical())
		{
			breaker19.setVerticalLineBreakMeasurer(new VerticalLineBreakMeasurer(bridged, pointlessFontRenderContext));
		}
		else
		{
			breaker19.setHorizontalLineBreakMeasurer(new LineBreakMeasurer(bridged, pointlessFontRenderContext));
		}
		
		boolean center = true;
		
		Rectangle2D textPerimeterBounds = textPerimeter.getBounds2D();
		
		if (straw.isVertical())
			penAlongPageAxis = (float)(textPerimeterBounds.getWidth()-gapBetweenMarginAndText);
		else
			penAlongPageAxis = (float)gapBetweenMarginAndText;
		
		for (int lineNumber=1; 
					straw.isVertical()?
						 (breaker19.getPosition() < bridged.getEndIndex() && penAlongPageAxis >= 0)
						:(breaker19.getPosition() < bridged.getEndIndex() && penAlongPageAxis <= textPerimeterBounds.getHeight()); 
					lineNumber++)
		{
			// Logger.println("pen = " + pen);
			lastPenAlongPageAxis = penAlongPageAxis;
			
			Rectangle2D rule = null;
			
			if (straw.isVertical())
				rule = new Rectangle2D.Float((float) textPerimeterBounds.getX()+penAlongPageAxis, (float)textPerimeterBounds.getY(), drawingContext.getFontMetrics().getAscent(), (float)textPerimeterBounds.getHeight());
			else
				rule = new Rectangle2D.Float((float) textPerimeterBounds.getX(), (float)textPerimeterBounds.getY()+penAlongPageAxis, (float)textPerimeterBounds.getWidth(), drawingContext.getFontMetrics().getAscent());
			
			// TODO ^^ bring these heights in line with the pen increments later down
			
			// System.out.println("textPerimeterBounds="+textPerimeterBounds);
			// System.out.println("rule="+rule);
			
			Area sliver = new Area(rule);

			sliver.intersect(new Area(textPerimeter));
			// System.out.println("sliver.getBounds()="+sliver.getBounds());
			// drawingContext.drawFilled(rule, null, Color.yellow);
			// drawingContext.drawFilled(sliver, null, Color.orange);
			
			Rectangle2D inscribed = TextShape.inscribeRectangle2DWithinArea(sliver, straw.isVertical());
			
			/* if (layedOut && showGuides)
			{
				// System.out.println("inscribed="+inscribed);
				drawingContext.setColor(Color.green);
				drawingContext.fill(inscribed);
			} */
			
			drawingContext.setColor(Color.black);
			
			float wrappingFootprint = 0.0f;
			
			if (straw.isVertical())
			{
				penAlongLineAxis = (float) inscribed.getY() + drawingContext.getFontMetrics().getAscent(); // TODO this isn't exactly right
				wrappingFootprint = (float) inscribed.getHeight() - drawingContext.getFontMetrics().getAscent();
				
				/* if (layedOut)
				{
					drawingContext.setColor(Color.yellow);
					drawingContext.fillRect((int)inscribed.getX(), (int)penAlongLineAxis, (int)inscribed.getWidth(), (int)wrappingFootprint);
				} */
			}
			else
			{
				penAlongLineAxis = (float) inscribed.getX();
				wrappingFootprint = (float) inscribed.getWidth();
			}
			
			if (wrappingFootprint < 0)
				wrappingFootprint = 0;
			
			OrientableTextLayout layout = null;
			
			// System.out.println("wrappingFootprint="+wrappingFootprint);
			
			if (wrappingFootprint>0 && bridged.getEndIndex()!=-1)
			{
				int endIndex = bridged.getEndIndex();
				final int indexOfClosingNewline = bridgedCopy.indexOf("\n", charIndex+1);
				
				if (indexOfClosingNewline!=-1)
				{
					/*
					System.out.println("found newline: "+indexOfNewline);
					System.out.println("bridged.getIndex()="+bridged.getIndex());
					System.out.println("charIndex = "+charIndex);
					System.out.println(" o o o o o o o o");
					*/
					endIndex = indexOfClosingNewline;
				}
				
				if (bridgedCopy.charAt(charIndex)=='\n' && endIndex>(charIndex+1))
				{
					// discard current newline character
					
					layout = breaker19.nextLayout(wrappingFootprint, ++charIndex);
					// TODO there was probably a more efficient way to do this
				}
				
				layout = breaker19.nextLayout(wrappingFootprint, endIndex);
			}
			
			if (layout!=null)
			{
				Rectangle2D bounded = layout.getBounds();
				
				float leftover = 0.0f;
				
				if (straw.isVertical())
					leftover = wrappingFootprint - (float)bounded.getHeight();
				else
					leftover = wrappingFootprint - (float)bounded.getWidth();
				
				float centeringShift = 0.0f;
				
				if (center)
					centeringShift = leftover/2.0f;
				
				float layoutX = 0.0f, layoutY = 0.0f;
				
				final float heightIncrement = layout.getAscent();
				final float betweenLines = layout.getDescent() + layout.getLeading();
				final boolean drawRules = !invisible && selected!=null && selected.isSelected(this);
				
				if (straw.isVertical())
				{
					layoutX = (float)textPerimeterBounds.getX() + penAlongPageAxis;
					layoutY = penAlongLineAxis + centeringShift;
					
					if (textStartAlongPageAxis==-1)
					{
						textStartAlongPageAxis = layoutX + heightIncrement; // inscribed.getWidth();
						
						if (drawRules)
						{
							// drawingContext.setColor(Color.magenta);
							drawingContext.setColor(Color.gray);
							drawingContext.drawLine((int)textStartAlongPageAxis, (int) (inscribed.getY()),
									(int)textStartAlongPageAxis, (int) (inscribed.getY()+inscribed.getHeight()) );
						}
					}
					
					penAlongPageAxis -= heightIncrement; // TODO might using character width instead of ascent be better?
					
					textEndAlongPageAxis = layoutX;
					
					if (drawRules)
					{
						drawingContext.setColor(LIGHT_BLUE);
						drawingContext.drawLine((int)inscribed.getX(), (int) (inscribed.getY()),
								(int)inscribed.getX(), (int) (inscribed.getY()+inscribed.getHeight()) ); // TODO use textEndAlongPageAxis as x coord instead
					}
					
					penAlongPageAxis -= betweenLines;
				}
				else
				{
					layoutX = penAlongLineAxis + centeringShift;
					layoutY = (float)textPerimeterBounds.getY() + penAlongPageAxis+layout.getAscent(); // TODO is this ascent part correct?

					if (textStartAlongPageAxis==-1)
					{
						textStartAlongPageAxis = inscribed.getY();
						
						/*
						if (drawRules)
						{
							drawingContext.setColor(Color.magenta);
							drawingContext.drawLine((int)penAlongLineAxis, (int)textStartAlongPageAxis,
									(int)(penAlongLineAxis + wrappingFootprint), (int)textStartAlongPageAxis);
						}
						*/
					}
					// else
					if (drawRules)
					{
						drawingContext.setColor(Color.gray);
						final int lineY = (int) (textPerimeterBounds.getY() + penAlongPageAxis);
						drawingContext.drawLine((int)penAlongLineAxis, lineY,
								(int)(penAlongLineAxis + wrappingFootprint), lineY);
					}
					
					penAlongPageAxis += heightIncrement;
					
					textEndAlongPageAxis = layoutY; // inscribed.getY() + inscribed.getHeight();
					
					if (drawRules)
					{
						drawingContext.setColor(LIGHT_BLUE);
						drawingContext.drawLine((int)penAlongLineAxis, (int)textEndAlongPageAxis,
								(int)(penAlongLineAxis + wrappingFootprint), (int)textEndAlongPageAxis);			
					}

					penAlongPageAxis += betweenLines;
				}
				
				drawingContext.setColor(Color.black);
								
				if (!invisible)
				{
					drawingContext.draw(layout, layoutX, layoutY, straw, charIndex, Sill.findParentSillOf(this));
				}
				else if (isVertical())
				{
					// we still need to go thru the motions 
					
					drawingContext.pretendToDraw(layout, layoutX, layoutY, straw, charIndex, Sill.findParentSillOf(this));
				}
				
				charIndex += layout.getCharacterCount();
			}
			else
			{
				// System.out.println(lineNumber+") Layout is null.");
				FontMetrics mets = drawingContext.getFontMetrics();
				final float guessedPageAxisDelta = mets.getAscent() + mets.getDescent() + mets.getLeading();
				
				if (straw.isVertical())
					penAlongPageAxis -= guessedPageAxisDelta;
				else
					penAlongPageAxis += guessedPageAxisDelta;
			}
			
			// System.out.println("breaker19="+breaker19);
			// Logger.println("textAsString="+textAsString);
		}
		
		if (textStartAlongPageAxis==-1 || textEndAlongPageAxis==-1)
			return;
		
		// double misalignment = 0.0;
		
		if (misalignment!=null)
		{
			if (straw.isVertical())
			{
				double desiredCenterX = textPerimeterBounds.getCenterX();
				double actualCenterX = (textStartAlongPageAxis + textEndAlongPageAxis) / 2.0;

				misalignment[0] = desiredCenterX - actualCenterX;
			}
			else
			{
				misalignment[0] = (2.0*textPerimeterBounds.getY() + textPerimeterBounds.getHeight() - textEndAlongPageAxis - textStartAlongPageAxis) / 2.0;

				// textStartAlongPageAxis-textPerimeterBounds.getY()) + (textFootprintAlongPageAxis - textPerimeterBounds.getHeight()
			}

			// if (!invisible)
			// 	System.out.println("\ty-axis misalignment: "+misalignment[0]);
			
			// run out attributed character iterator
			
			// System.out.println("    end index: "+bridged.getEndIndex());
			// System.out.println("    charIndex: "+charIndex);
			
			misalignment[1] = bridged.getEndIndex() - charIndex;
				
			if (misalignment[1]<0)
			{
				System.out.println("bad misalignment calc: "+misalignment[1]+"; reverting it to zero.");
				misalignment[1] = 0;
			}
			
			// System.out.println("leftover chars: "+misalignment[1]);
			
		}
		
		/* if (isVertical())
		{
			drawingContext.setColor(Color.magenta);
			drawingContext.drawLine((int)textStartAlongPageAxis, (int) textPerimeterBounds.getY(), (int)textStartAlongPageAxis, (int) ( textPerimeterBounds.getY()+textPerimeterBounds.getWidth() ) );
			drawingContext.setColor(Color.green);
			drawingContext.drawLine((int)textEndAlongPageAxis, (int) textPerimeterBounds.getY(), (int)textEndAlongPageAxis, (int) ( textPerimeterBounds.getY()+textPerimeterBounds.getWidth() ) );
		} */
	}

	public String determineTextAsString()
	{
		try
		{
			return getText().getText(0, getText().getLength());
		}
		catch (BadLocationException exception)
		{
			System.err.println(exception.getMessage());
			throw new BugException("There may be a bug in the code that converts styled text to plain text in speech.", exception);
		}
	}

	private void setText(StyledDocument designatedText)
	{
		straw.setStyledDocument(designatedText);
		setLayedOut(false);
		
		// TODO tear down associations on old styled document
	}
	
	public void setText(String designatedTextAsString)
	{
		try
		{
			if (getText().getLength() > 0)
				getText().remove(0, getText().getLength());
			
			// System.out.println("Setting text: "+designatedTextAsString);
			
			SimpleAttributeSet sas = new SimpleAttributeSet();
			final Font DEFAULT_FONT = BalloonEngineState.getInstance().getDefaultFont();
			StyleConstants.setFontFamily(sas, DEFAULT_FONT.getFamily());
			StyleConstants.setFontSize(sas, DEFAULT_FONT.getSize());
			
			getText().insertString(0, designatedTextAsString, sas);
		}
		catch (BadLocationException exception)
		{
			throw new RuntimeException(exception.getMessage());
		}
	}
	
	public StyledDocument getText()
	{
		return straw.getStyledDocument();
	}
	
	public void setShape(Shape designatedShape)
	{
		shape = designatedShape;
		
		setLayedOut(false);
	}
	
	public void ensureNoShapeSet()
	{
		if (shape!=null)
		{
			StringBuffer problem = new StringBuffer(150);
			problem.append("Shape has already been set to ");
			problem.append(shape);
			problem.append(".");
			
			throw new IllegalStateException(problem.toString());
		}
	}

	public Shape getShape()
	{
		return shape;
	}
	
	final String HASH_CODE = String.valueOf(hashCode());
	
	public String toString()
	{
		return HASH_CODE;
	}
	
	public String describe()
	{
		StringBuffer desc = new StringBuffer(42);
		desc.append("TextShape \"");
		String textAsString = null;
		
		if (getText()!=null)
		{
			try
			{
				textAsString = getText().getText(0, getText().getLength());
				desc.append(textAsString);
			}
			catch (BadLocationException exception)
			{
				throw new BugException("There may be a bug in the code that converts styled text to plain text in speech.", exception);
			}
			
		}
		
		desc.append("\" ");
		
		desc.append("shape=");
		desc.append(shape);
		
		return desc.toString();
	}
	
	public static Rectangle2D inscribeRectangle2DWithinArea(Area designatedArea, boolean vertical)
	{
		// Rectangle2D result = inscribeRectangle2DWithinAreaByBreakingItInTwo(designatedArea, vertical);
		Rectangle2D result = inscribeRectangle2DWithinAreaByTracingAroundIt(designatedArea, vertical);
		
		// System.out.println("result="+result);
		
		if (result==null)
			return new Rectangle2D.Double(); // TODO would rather return null
		
		return result;
	}
	
	public void setCenter(Point2D designatedCenter)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			Interactive shapeableRelocateable = (Interactive) get(loop);
			shapeableRelocateable.setLocation(designatedCenter);
		}
	}
	
	public void setSize(float designatedSize)
	{
		if (getSuperEllipse()==null)
			return;
		
		getSuperEllipse().setSemiMajorAxis(designatedSize);
		getSuperEllipse().setSemiMinorAxis(designatedSize);
		
		// TODO probably not the best
	}
	
	public float getSize()
	{
		if (getSuperEllipse()==null)
			return 0.0f;
		
		return (float)getSuperEllipse().getSemiMajorAxis();
		
		// TODO probably not the best
	}
	
	public Drawable getAnnotates()
	{
		return annotates;
	}
	
	public void setAnnotates(Drawable designatedAnnotates)
	{
		annotates = designatedAnnotates;
	}
	
	public void applyAttributes(org.w3c.dom.Document doc, Element element, ArchiveContext archiveContext)
	{
		Element editionElement = doc.createElement(IDENTIFIER_EDITION); // as yet unused
		element.appendChild(editionElement);
		
		Element textElement = doc.createElement(StyledDocumentStraw.IDENTIFIER_TEXT);
		// textElement.appendChild(doc.createTextNode(textAsString));
		straw.save(doc, textElement);
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			Interactive shapeableRelocateable = (Interactive) get(loop);
			
			if (shapeableRelocateable instanceof Saveable)
			{
				Saveable saveable = (Saveable) shapeableRelocateable;
				saveable.save(doc, editionElement, archiveContext);
			}
		}
				
		// TODO Drawable annotates
		
		if (fillColor!=null)
		{
			element.setAttribute(IDENTIFIER_FILL_COLOR, ShapeFriend.describe(fillColor));
		}
		
		if (outlineColor!=null)
		{
			element.setAttribute(IDENTIFIER_OUTLINE_COLOR, ShapeFriend.describe(outlineColor));
		}
		
		element.setAttribute(Marginal.IDENTIFIER_INNER_MARGIN, String.valueOf(getInnerMarginInPoints()));
		element.setAttribute(Marginal.IDENTIFIER_OUTER_MARGIN, String.valueOf(getOuterMarginInPoints()));
		
		element.setAttribute(Orientable.IDENTIFIER_VERTICAL, String.valueOf(isVertical()));
		
		if (getLineThickness()!=1.0)
		{
			element.setAttribute(IDENTIFIER_LINE_THICKNESS, String.valueOf(getLineThickness()));
		}
		
		editionElement.appendChild(textElement);
	}
	
	public void extractAttributesAndSubnodes(Element element, java.util.List missingFontFamilies)
	{
		Node editionElement = XmlFriend.excavateSubnode(element, IDENTIFIER_EDITION);
		if (editionElement==null)
			editionElement=element;
		
		java.util.List textElements = XmlFriend.excavateEveryImmediateSubnode(editionElement, StyledDocumentStraw.IDENTIFIER_TEXT);
		if (textElements.size() > 0)
		{
			Element subelement = (Element) textElements.get(0); // TODO allow multiple language sets
			
			straw.load(subelement, missingFontFamilies);
		}
		
		for (int loop=0; loop<=editionElement.getChildNodes().getLength()-1; loop++)
		{
			Node subnode = editionElement.getChildNodes().item(loop);
			Shapeable newShapeable = parse(this, element, subnode);
			
			if (newShapeable!=null)
				add(newShapeable);
		}
		
		if (element.hasAttribute(IDENTIFIER_FILL_COLOR))
			setFillColor(ShapeFriend.parseColor(element.getAttribute(IDENTIFIER_FILL_COLOR)));
		else
			setFillColor(null);
		
		if (element.hasAttribute(IDENTIFIER_OUTLINE_COLOR))
			setOutlineColor(ShapeFriend.parseColor(element.getAttribute(IDENTIFIER_OUTLINE_COLOR)));
		else
			setOutlineColor(null);

		if (element.hasAttribute(Marginal.IDENTIFIER_INNER_MARGIN))
		{
			try
			{
				String innerMarginAsString = element.getAttribute(Marginal.IDENTIFIER_INNER_MARGIN);
				setInnerMarginInPoints(Double.parseDouble(innerMarginAsString));
			}
			catch (NumberFormatException exception) {}
		}

		/* if (element.hasAttribute(Perch.IDENTIFIER_OUTER_MARGIN))
		{
			try
			{
				String outerMarginAsString = element.getAttribute(Marginal.IDENTIFIER_OUTER_MARGIN);
				setOuterMarginInPoints(Double.parseDouble(outerMarginAsString));
			}
			catch (NumberFormatException exception) {}
		} */
		
		if (element.hasAttribute(Orientable.IDENTIFIER_VERTICAL))
			setVertical(BooleanFriend.parseBoolean(element.getAttribute(Orientable.IDENTIFIER_VERTICAL)));
		
		if (element.hasAttribute(IDENTIFIER_LINE_THICKNESS))
			setLineThickness(Double.parseDouble(element.getAttribute(IDENTIFIER_LINE_THICKNESS)));
		
		setLayedOut(false);
	}
	
	public static Shapeable parse(Object parent, Element element, Node subnode)
	{
		Shapeable shapeable = null;
		
		if (SuperEllipse.IDENTIFIER_SUPER_ELLIPSE.equals(subnode.getNodeName()))
		{
			shapeable = new SuperEllipsePerch();
		}
		else if (Parallelogram.IDENTIFIER_PARALLELOGRAM.equals(subnode.getNodeName()))
		{
			shapeable = new ParallelogramPerch();
		}
		// TODO else circle, etc.
		
		if (shapeable instanceof Saveable)
		{
			Saveable saveable = (Saveable) shapeable;
			saveable.open(parent, subnode, null);
		}
		
		return shapeable;
	}
	
	public boolean isLayedOut()
	{
		return layedOut;
	}
	
	public void setLayedOut(boolean designatedLayedOutness)
	{
		if (designatedLayedOutness!=layedOut)
		{
			layedOut = designatedLayedOutness;
			
			// System.out.println("set layed out: "+designatedLayedOutness);
			
			if (!layedOut)
				straw.refresh(); // TODO ditch?
		}
	}
	
	public VerticalAlignment getVerticalAlignment()
	{
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}
		
	/**
	 * @see com.smithandtinkers.layout.Resizeable#getResizeableBounds2D()
	 * 
	 * @return the boundary of the shape-portions of the object. Will not
	 * include any stems.
	 */
	public Rectangle2D getResizeableBounds2D()
	{
		if (size()==0)
			return null;
		
		Rectangle2D rect = null;
		
		if (size()==1)
		{
			Resizeable resizeable = (Resizeable) get(0);
			rect = resizeable.getResizeableBounds2D();
		}
		else // TODO couldn't we get this from textPerimeter.getBounds() instead?
		{
			rect = ((Resizeable)get(0)).getResizeableBounds2D();
		
			for (int loop=1; loop<=size()-1; loop++)
			{
				Resizeable resizeable = (Resizeable) get(loop);
				Rectangle2D.union(rect, resizeable.getResizeableBounds2D(), rect);
			}
		}
		
		if (1==1) // TODO make this a choice
		{
			Sill grandad = (Sill) findForebear(Sill.class);
			
			if (grandad!=null && grandad.getAperture()!=null && grandad.getAperture().getShape() instanceof Rectangle2D)
				Rectangle2D.intersect(rect, (Rectangle2D)grandad.getAperture().getShape(), rect);
		}
		
		return rect;
	}
	
	/**
	 * @return The textPerimeter boundary, minus stems.
	 * @see com.smithandtinkers.layout.Relocateable#getResizeableBounds2D()
	 */
	private Rectangle2D getResizeableBounds2D(Shape actual)
	{
		if (size()<1)
			return null;
		
		Rectangle2D fullRect = actual.getBounds2D(); // TODO cache
		
		Interactive firstShapeable = (Interactive) get(0);
		Rectangle2D shapeablesRect = firstShapeable.getResizeableBounds2D();
		
		for (int loop=1; loop<=size()-1; loop++)
		{
			Interactive shapeableRelocateable = (Interactive) get(loop);
			shapeablesRect = shapeablesRect.createUnion(shapeableRelocateable.getResizeableBounds2D()); // TODO wasteful
		}
		
		double ovalBottomY = shapeablesRect.getY()+shapeablesRect.getHeight();
		if (ovalBottomY < fullRect.getY()+fullRect.getHeight())
		{
			fullRect.setRect(fullRect.getX(), fullRect.getY(),  fullRect.getWidth(), ovalBottomY-fullRect.getY() );
		}
		
		if (shapeablesRect.getX() > fullRect.getX())
		{
			fullRect.setRect(shapeablesRect.getX(), fullRect.getY(),  fullRect.getWidth()-(shapeablesRect.getX()-fullRect.getX()), fullRect.getHeight() );
		}
		
		if (shapeablesRect.getWidth() < fullRect.getWidth())
		{
			fullRect.setRect(fullRect.getX(), fullRect.getY(), shapeablesRect.getWidth(), fullRect.getHeight() );
		}
		
		return fullRect;
	}

	/**
	 * @see com.smithandtinkers.layout.Resizeable#resize(com.smithandtinkers.layout.PerimeterSegment, double, double, double, double)
	 */
	public boolean resize(PerimeterSegment designatedSide, double oldX, double oldY, double newX, double newY)
	{
		// is this a real side or a clipped side?
		
		// Rectangle2D preferredBounds = superEllipse.getBounds2D();
		// Rectangle2D actualBounds = actualShape().getBounds2D(); // TODO make faster
		
		// Logger.print("designated side = ");
		// Logger.println(designatedSide);
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Resizeable)
			{
				Resizeable resizeable = (Resizeable) get(loop);
				resizeable.resize(designatedSide, oldX, oldY, newX, newY);
			}
		}
		
		return true;
	}
	
	public boolean onPerimeter(Point thePoint)
	{
		return onPerimeter(thePoint.getX(), thePoint.getY());
	}
	
	public boolean onPerimeter(double x, double y)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Perimetered)
			{
				Perimetered perimetered = (Perimetered) get(loop);
				return perimetered.onPerimeter(x, y); // TODO not ideal
			}
		}
		
		return false;
	}
	
	public Point2D getLocation()
	{
		if (size()>0)
		{
			Interactive firstShapeable = (Interactive) get(0);
			return firstShapeable.getLocation();
		}	
		else
		{
			return null;
		}
	}
	
	public void setLocation(Point2D designatedLocation)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			Relocateable relocateable = (Relocateable) get(loop);
			relocateable.setLocation(designatedLocation);
		}
	}
	
	/**
	 * @see com.smithandtinkers.layout.Resizeable#setLocation(double, double)
	 */
	public void setLocation(double newX, double newY)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			Relocateable relocateable = (Relocateable) get(loop);
			relocateable.setLocation(newX, newY);
		}
	}
	
	public void translate(double dx, double dy)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			Interactive shapeableRelocateable = (Interactive) get(loop);
			shapeableRelocateable.translate(dx, dy);
		}
	}
	
	public int stemCount()
	{
		if (!(getSuperEllipse() instanceof PunctuatedSuperEllipse))
			return 0;
		
		PunctuatedSuperEllipse pse = (PunctuatedSuperEllipse) getSuperEllipse();
		
		if (pse==null)
			return 0;
		else
			return pse.stemCount();
	}
	
	public Stem getStem(int index)
	{
		if (!(getSuperEllipse() instanceof PunctuatedSuperEllipse))
			return null;
		
		PunctuatedSuperEllipse pse = (PunctuatedSuperEllipse) getSuperEllipse();
		
		if (pse==null)
			return null;
		else
			return pse.getStem(index);
	}
	
	public void reshape(double oldX, double oldY, double newX, double newY)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Shapeable)
			{
				Shapeable shapeable = (Shapeable) get(loop);
				shapeable.reshape(
					// mapOutsideX(oldX), 
					Sill.mapAbsoluteXIntoCoordinateFrameOf(oldX, this), 
					// mapOutsideY(oldY), 
					Sill.mapAbsoluteYIntoCoordinateFrameOf(oldY, this), 
					// mapOutsideX(newX), 
					Sill.mapAbsoluteXIntoCoordinateFrameOf(newX, this), 
					// mapOutsideY(newY)
					Sill.mapAbsoluteYIntoCoordinateFrameOf(newY, this)
				);
			}
		}
	}
	
	public Shape toShape(ShapingContext shapingContext, PlottingContext plottingContext)
	{
		return preferredShape(plottingContext);
	}
	
	/**
	 * @return The first contained superellipse, or null if none could be found.
	 */
	public SuperEllipse getSuperEllipse()
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof SuperEllipse)
			{
				SuperEllipse superEllipse = (SuperEllipse) get(loop);
				
				if (superEllipse instanceof SuperEllipsePerch)
				{
					SuperEllipsePerch perched = (SuperEllipsePerch) superEllipse;
					return perched.getPunctedSuperEllipse();
				}
				else
				{
					return superEllipse;
				}
			}
		}
		
		return null;
	}
	
	public void setHeight(double designatedHeight)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Shapeable)
			{
				Resizeable resizable = (Resizeable) get(loop);
				resizable.setHeight(designatedHeight);
			}
		}
	}
	
	public void setWidth(double designatedWidth)
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Shapeable)
			{
				Resizeable resizable = (Resizeable) get(loop);
				resizable.setWidth(designatedWidth);
			}
		}
	}
	
	public Rectangle2D getPreferredBounds() // TODO take into account multiple shapes
	{
		SuperEllipse sEllipse = getSuperEllipse();
		
		if (sEllipse==null)
			return null;
		
		return sEllipse.getPreferredBounds();
	}
	
	public double getHeight()
	{
		double tally = 0.0;
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Shapeable)
			{
				Resizeable resizable = (Resizeable) get(loop);
				tally += resizable.getHeight();
			}
		}
		
		return tally / (double) size();
	}
	
	public double getWidth()
	{
		double tally = 0.0;
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof Shapeable)
			{
				Resizeable resizable = (Resizeable) get(loop);
				tally += resizable.getWidth();
			}
		}
		
		return tally / (double) size();
	}
	
	public Color getFillColor()
	{
		return fillColor;
	}

	public void setFillColor(Color designatedFillColor)
	{
		if (fillColor == designatedFillColor)
			return;
		
		fillColor = designatedFillColor;
		
		SingleThreadedChangeSupport stcs = getChangeSupport();
		
		if (stcs!=null)
		{
			stcs.fireChange(CHANGE_EVENT);
		}
	}

	public Color getOutlineColor()
	{
		return outlineColor;
	}

	public void setOutlineColor(Color designatedOutlineColor)
	{
		if (outlineColor == designatedOutlineColor)
			return;
		
		outlineColor = designatedOutlineColor;
		
		SingleThreadedChangeSupport stcs = getChangeSupport();
		
		if (stcs!=null)
			stcs.fireChange(CHANGE_EVENT);
	}
	
	public boolean isSelected(Selection selection)
	{
		return (selection!=null && selection.isSelected(this));
	}

	public double getInnerMarginInPoints()
	{
		return innerMarginInPoints;
	}

	public void setInnerMarginInPoints(double designatedInnerMarginInPoints)
	{
		if (designatedInnerMarginInPoints!=innerMarginInPoints)
		{
			innerMarginInPoints = designatedInnerMarginInPoints;
			
			SingleThreadedChangeSupport stcs = getChangeSupport();
			if (stcs!=null)
				stcs.fireChange(CHANGE_EVENT);
		}
	}
	
	public double getOuterMarginInPoints()
	{
		return -innerMarginInPoints;
	}
	
	public void setMarginInPoints(double designatedMarginInPoints)
	{
		setInnerMarginInPoints(designatedMarginInPoints);
	}
	
	public double getMarginInPoints()
	{
		return innerMarginInPoints;
	}
	
	protected void connectStraw()
	{
		straw.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				TextShape.this.setLayedOut(false);
				SingleThreadedChangeSupport stcs = getChangeSupport();
				
				if (stcs!=null)
				{
					// System.out.println("anon change listener: stcs="+stcs);
					stcs.fireChange(CHANGE_EVENT);
				}
			}
		});
	}
	
	public boolean isVertical()
	{
		return straw.isVertical();
	}

	public void setVertical(boolean designatedVerticality)
	{
		straw.setVertical(designatedVerticality);
		setLayedOut(false);
		resetGaps();
	}
	
	public void resetGaps()
	{
		for (int loop=0; loop<=size()-1; loop++)
		{
			Alignable alignable = (Alignable) get(loop);
			alignable.setGapBetweenMarginAndText(0.0);
		}
	}
	
	public Shape actualPerchedInnerShape(int sectionIndex)
	{	
		Perch perch = (Perch) get(sectionIndex);
		Area remainingArea = null;
		
		remainingArea = new Area(perch.getInner().toShape(shapingContext, null));
		
		// subtract neighbors within this text area
		
		for (int reverseLoop=sectionIndex-1; reverseLoop>=0; reverseLoop--)
		{
			Perch neighborPerch = (Perch) get(reverseLoop);
			remainingArea.subtract(new Area(neighborPerch.getInner().toShape(shapingContext, null)));
		}
		
		// TODO below: poor code reuse with Crowd.actualPerchedInnerShape(Crowdable)
		
		Sill sillDad = (Sill)findForebear(Sill.class);
		if (sillDad!=null && sillDad.getAperture()!=null)
		{
			if (sillDad.getAperture() instanceof Perch) // TODO can this be consolidated with Sill.actualPerchedInnerShape() ?
			{
				Perch otherPerch = (Perch) sillDad.getAperture();
				remainingArea.intersect(new Area(otherPerch.getInner().toShape(shapingContext, null)));
			}
			else if (sillDad.getAperture().getShape()!=null)
			{
				remainingArea.intersect(new Area(sillDad.getAperture().getShape()));
			}
		}
		
		if (getParent()!=null && getParent() instanceof Crowd)
		{
			Crowd parentCrowd = (Crowd) getParent();
			
			Crowdable neighbor = null;
			Shape theShape = null;

			for (int loop=parentCrowd.indexOf(this)-1; loop>=0; loop--)
			{
				// System.out.println("\tloop= "+loop);

				if (parentCrowd.get(loop) instanceof Shape)
				{
					theShape = (Shape) get(loop);
				}
				else if (parentCrowd.get(loop) instanceof TextShape)
				{
					neighbor = (Crowdable) parentCrowd.get(loop);
					theShape = neighbor.preferredPerchedOuterShape(null);
				}
				else
				{
					Logger.print("Didn't know how to shape ");
					Logger.print(parentCrowd.get(loop).getClass());
				}

				if (theShape!=null && neighbor!=this)
				{
					/*
					Logger.print("\tsubtracting ");
					Logger.print(theShape);
					Logger.println("...");
					*/
					remainingArea.subtract(new Area(theShape));
				}
			}
		}
		
		// Logger.println("doned");
		
		return remainingArea;
	}
	
	/**
	 * @return the bounds of the entire object, including stems.
	 */
	public Rectangle2D getBounds2D()
	{
		Shape asShape = toShape(shapingContext, null);
		
		if (asShape==null)
			return null;
		
		Rectangle2D bounds2D = asShape.getBounds2D();
		
		for (int perchIndex=0; perchIndex<=size()-1; perchIndex++)
		{
			if (get(perchIndex) instanceof Stemmed)
			{
				Stemmed stemmed = (Stemmed) get(perchIndex);
				
				for (int stemIndex=0; stemIndex<=stemmed.stemCount()-1; stemIndex++)
				{
					Stem stem = (Stem) stemmed.getStem(stemIndex);
					
					Point2D point = stem.getFocus();
					
					if (stem instanceof AbstractStem
							&& ((AbstractStem)stem).getType()==AbstractStem.BUBBLED_TYPE )
					{
						// need to account for height of bubble at focus
						
						final double HALF_BUBBLE = AbstractStem.DEFAULT_BUBBLE_HEIGHT/2.0;
						
						if (point.getY()<bounds2D.getY())
							point.setLocation(point.getX(), point.getY()-HALF_BUBBLE);
						else
							point.setLocation(point.getX(), point.getY()+HALF_BUBBLE);
						
						if (point.getX()<bounds2D.getX())
							point.setLocation(point.getX()-HALF_BUBBLE, point.getY());
						else
							point.setLocation(point.getX()+HALF_BUBBLE, point.getY());
						
						// TODO move this code into AbstractStem?
					}
					
					union(bounds2D, point, bounds2D);
				}
			}
		}
		
		return bounds2D;
	}
	
	/**
	 * Determines the smallest rectangle that could encompass the rectangle
	 * and point provided. If a destination rectangle is provided, it will
	 * be updated to reflect the result.
	 *
	 * @return the result
	 */
	public static Rectangle2D union(Rectangle2D rect, Point2D point, Rectangle2D dest)
	{
		if (dest==null)
			dest = new Rectangle2D.Double();
			
		double x, y, width, height;
		
		if (rect.getX()<point.getX())
			x = rect.getX();
		else
			x = point.getX();
			
		if (rect.getY()<point.getY())
			y = rect.getY();
		else
			y = point.getY();
		
		if (rect.getX()+rect.getWidth()>point.getX())
			width = rect.getWidth();
		else
			width = point.getX()-x;
		
		if (rect.getY()+rect.getHeight()>point.getY())
			height = rect.getHeight();
		else
			height = point.getY()-y;
		
		dest.setRect(x, y, width, height);
		
		return dest;
	}
	
	public StyledDocumentStraw getStraw()
	{
		return straw; // is this safe?
	}
	
	private final static double coords[] = new double[6];
	
	/**
	 * Attempts to inscribe the largest possible rectangle inside one of the provided sub-paths by tracing around
	 * them.
	 *
	 * Works the best out of any approach yet, but still suffers from the problem of ignoring small bites taken
	 * from the top or bottom of an otherwise intact sub-path.
	 */
	private static Rectangle2D inscribeRectangle2DWithinAreaByTracingAroundIt(Area area, boolean vertical) // TODO return a List of Rectangle2D's?
	{
		if (area==null)
			return null;
			
		Rectangle2D bounds = area.getBounds2D();
		
		PathIterator pathIterator = area.getPathIterator(null);
		
		boolean single = area.isSingular();
		
		/*
		if (!single)
		{
			System.out.println("\nbounds="+bounds);
			System.out.println("irwabtai: iterating...");
		}
		*/
		
		List rectList = new TypesafeList(Rectangle2D.class);
		
		while (!pathIterator.isDone())
		{
			Rectangle2D rect = traceOne(pathIterator, bounds, vertical, false);
			
			if (rect!=null)
				rectList.add(rect);
		}
		
		switch (rectList.size()) 
		{
			case 0:
				return null;
				
			case 1:
				return (Rectangle2D) rectList.get(0);
			
			default:
				int indexOfBiggest = -1;
				double largestArea = 0;
				
				for (int index=0; index<=rectList.size()-1; index++)
				{
					Rectangle2D rect = (Rectangle2D) rectList.get(index);
					double areaMagnitude = rect.getWidth() * rect.getHeight(); // TODO negatives?
					if (areaMagnitude > largestArea)
					{
						largestArea = areaMagnitude;
						indexOfBiggest = index;
					}
				}
				
				if (indexOfBiggest==-1)
					return null;
				else
					return (Rectangle2D)rectList.get(indexOfBiggest);
		}
	}
	
	private static Rectangle2D traceOne(PathIterator pathIterator, Rectangle2D bounds, boolean vertical, boolean debugInfoToConsole)
	{
		List advanceList = new TypesafeList(List.class);
		List rearList = new TypesafeList(List.class);
		
		List advance = null;
		List rear = null;
		
		double lastX=-1, lastY=-1;
		
		final double TOLERANCE = 0.1;
		boolean noMore = false;
		
		final int ON_ADVANCE = 1;
		final int ON_REAR = 2;
		final int IN_MIDDLE = 3;
		
		int firstSegment = IN_MIDDLE;
		
		for (int index=0; !pathIterator.isDone() && !noMore; index++)
		{
			int type = pathIterator.currentSegment(coords);
			
			double x=-1,y=-1;
			
			switch (type)
			{
				case PathIterator.SEG_LINETO:
				case PathIterator.SEG_MOVETO:
					x = coords[0];
					y = coords[1];
					break;
				
				case PathIterator.SEG_QUADTO:
					x = coords[2];
					y = coords[3];
					break;
					
				case PathIterator.SEG_CUBICTO:
					x = coords[4];
					y = coords[5];
					break;
				
				case PathIterator.SEG_CLOSE:
					noMore = true;
					break;
			}
			
			if (debugInfoToConsole)
			{
				System.out.print("type = ");
				System.out.print(type);
				System.out.print(", coords = ");
				// Logger.println(System.out, coords);
				System.out.println(x+","+y);
			}
			
			if (type!=PathIterator.SEG_CLOSE)
			{
				if (!vertical)
				{
					if (Math.abs(y-bounds.getY())<TOLERANCE)
					{
						if (advance==null)
						{
							if (index>0 && firstSegment==ON_ADVANCE)
							{
								// side-door case
								
								advance = (List) advanceList.get(0);
							}
							else
							{
								advance = new TypesafeList(Double.class);
								advanceList.add(advance);
							}
						}
						
						advance.add(new Double(x));
						
						rear = null;
						
						if (index==0)
							firstSegment = ON_ADVANCE;
					}
					else if (Math.abs(y-(bounds.getY()+bounds.getHeight()))<TOLERANCE)
					{
						if (rear==null)
						{
							if (index>0 && firstSegment==ON_REAR) //  haven't actually seen this case yet
							{
								rear = (List) rearList.get(0);
							}
							else
							{
								rear = new TypesafeList(Double.class);
								rearList.add(rear);
							}
						}
						
						rear.add(new Double(x));
						advance = null;
						
						if (index==0)
							firstSegment = ON_REAR;
					}
					else
					{
						rear = null;
						advance = null;
						
						if (debugInfoToConsole)
							System.out.println("unrecognized coord: ("+x+", "+y+")");
							
						if (index==0)
							firstSegment = IN_MIDDLE;
					}
				}
				else
				{
					if (Math.abs(x-(bounds.getX()+bounds.getWidth()))<TOLERANCE)
					{
						if (advance==null)
						{
							// TODO support the side-door case
							
							advance = new TypesafeList(Double.class);
							advanceList.add(advance);
						}
						
						advance.add(new Double(y));
						rear = null;
						
						if (index==0)
							firstSegment = ON_ADVANCE;
					}
					else if (Math.abs(x-bounds.getX())<TOLERANCE)
					{
						if (rear==null)
						{
							// TODO support the side-door case
							
							rear = new TypesafeList(Double.class);
							rearList.add(rear);
						}
						
						rear.add(new Double(y));
						advance = null;
						
						if (index==0)
							firstSegment = ON_REAR;
					}
					else
					{
						rear = null;
						advance = null;
						
						if (debugInfoToConsole)
							System.out.println("unrecognized coord: ("+x+", "+y+")");
						
						if (index==0)
							firstSegment = IN_MIDDLE;
					}
				}
			}
			
			if (!pathIterator.isDone())
				pathIterator.next();
		}
		
		if (advanceList.size()==0 || rearList.size()==0)
			return null;
		
		if (debugInfoToConsole)
		{
			System.out.println("advanceList="+advanceList);
			System.out.println("rearList="+rearList);
		}
		
		int indexOfBestAdvance = -1;
		int indexOfBestRear = -1;
		double widestAdvanceFootprint = TOLERANCE;
		double widestRearFootprint = TOLERANCE;
		
		// TODO workaround for the "side door" problem
		
		for (int index=0; index<=advanceList.size()-1; index++)
		{
			advance = (List) advanceList.get(index);
			Collections.sort(advance);
			Double hi = (Double) advance.get(advance.size()-1);
			Double lo = (Double) advance.get(0);
			
			double footprint = hi.doubleValue() - lo.doubleValue();
			// System.out.println("testing advance footprint: "+footprint);
			
			if (footprint>widestAdvanceFootprint)
			{
				widestAdvanceFootprint = footprint;
				indexOfBestAdvance = index;
			}
		}
		
		for (int index=0; index<=rearList.size()-1; index++)
		{
			rear = (List) rearList.get(index);
			Collections.sort(rear);
			Double hi = (Double) rear.get(rear.size()-1);
			Double lo = (Double) rear.get(0);
			
			double footprint = hi.doubleValue() - lo.doubleValue();
			// System.out.println("testing rear footprint: "+footprint);
			
			if (footprint>widestRearFootprint)
			{
				widestRearFootprint = footprint;
				indexOfBestRear = index;
			}
		}
		
		if (debugInfoToConsole)
		{
			System.out.println("indexOfBestAdvance="+indexOfBestAdvance);
				System.out.println("\tits footprint: "+widestAdvanceFootprint);
			System.out.println("indexOfBestRear="+indexOfBestRear);
				System.out.println("\tits footprint: "+widestRearFootprint);
		}
		
		if (indexOfBestAdvance==-1 || indexOfBestRear==-1)
		{
			if (debugInfoToConsole)
			{
				System.err.println("indexOfBestAdvance="+indexOfBestAdvance);
				System.err.println("indexOfBestRear="+indexOfBestRear);
			}
			
			return null;
		}
		
		advance = (List) advanceList.get(indexOfBestAdvance);
		rear = (List) rearList.get(indexOfBestRear);
		
		if (debugInfoToConsole)
		{
			System.out.println("advance="+advance);
			System.out.println("rear="+rear);
		}
		
		if (advance.size()<2 || rear.size()<2)
			return null;
		
		double advance1 = ((Double) advance.get(0)).doubleValue();
		double advance2 = ((Double) advance.get(advance.size()-1)).doubleValue();
		
		double rear1 = ((Double) rear.get(0)).doubleValue();
		double rear2 = ((Double) rear.get(rear.size()-1)).doubleValue();
		
		if (!vertical)
		{
			double left = -1;
			
			if (advance1>rear1)
				left = advance1;
			else
				left = rear1;
				
			double right = -1;
			
			if (advance2<rear2)
				right = advance2;
			else
				right = rear2;
			
			return new Rectangle2D.Double(
				left, bounds.getY(), right-left, bounds.getHeight());
		}
		else
		{
			double top = -1;
			
			if (advance1>rear1)
				top = advance1;
			else
				top = rear1;
			
			double bottom = -1;
			
			if (advance2<rear2)
				bottom = advance2;
			else
				bottom = rear2;
			
			return new Rectangle2D.Double(
				bounds.getX(), top, bounds.getWidth(), bottom-top);
		}
	}
	
	public double getLineThickness()
	{
		return lineThickness;
	}
	
	public void setLineThickness(double designatedLineThickness)
	{
		if (designatedLineThickness!=lineThickness)
		{
			// System.out.println("changing line thickness to: "+designatedLineThickness);
			lineThickness = designatedLineThickness;
			final BasicStroke replacement = new BasicStroke((float)designatedLineThickness);
			stroke = replacement;
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
	
	/**
	 * Draws any adornments. These are probably stems.
	 */
	private void drawAdornments(DrawingContext drawingContext, Selection selected)
	{
		for (int shapeLoop=0; shapeLoop<=size()-1; shapeLoop++)
		{
			if (get(shapeLoop) instanceof Adorned)
			{
				Adorned adorned = (Adorned) get(shapeLoop);
				adorned.drawAdornments(drawingContext, selected);
			}
			
			if (get(shapeLoop) instanceof VisiblySelectable)
			{
				VisiblySelectable visiblySelectable = (VisiblySelectable) get(shapeLoop);
				visiblySelectable.paintSelectionIfAppropriate(drawingContext, selected);
			}
		}
	}

	public boolean isStemInBack()
	{
		return stemInBack;
	}
	
	public void setStemInBack(boolean designatedStemInBack)
	{
		if (designatedStemInBack!=stemInBack)
		{
			stemInBack = designatedStemInBack;
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}
}
