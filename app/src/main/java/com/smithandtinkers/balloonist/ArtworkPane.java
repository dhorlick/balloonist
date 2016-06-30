/*
 Copyleft 2004 by Dave Horlick
*/

package com.smithandtinkers.balloonist;

import com.smithandtinkers.graphics.DrawingContextFactory;
import com.smithandtinkers.graphics.DrawingFilter;
import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.graphics.awt.DefaultDrawingContext;
import com.smithandtinkers.graphics.awt.TextinessSniffingGraphics2D;
import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.gui.Ruler;
import com.smithandtinkers.layout.edit.BringForwardEdit;
import com.smithandtinkers.layout.edit.BringToFrontEdit;
import com.smithandtinkers.layout.edit.CreateEdit;
import com.smithandtinkers.layout.edit.DeleteEdit;
import com.smithandtinkers.layout.edit.FlipHeightEdit;
import com.smithandtinkers.layout.edit.FlipWidthEdit;
import com.smithandtinkers.layout.edit.LeadingEdgeRotationEdit;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.layout.edit.Resize2dEdit;
import com.smithandtinkers.layout.edit.ResizeEdit;
import com.smithandtinkers.layout.edit.SelectionEdit;
import com.smithandtinkers.layout.edit.SendBackwardsEdit;
import com.smithandtinkers.layout.edit.SendToBackEdit;
import com.smithandtinkers.layout.edit.TranslateEdit;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.undo.CompoundEdit;

import com.lowagie.text.DocumentException;

import com.smithandtinkers.BalloonEngine;
import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.geom.*;
import com.smithandtinkers.gui.CursorModeManager;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.mvc.ChangeIssuer;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.BugException;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.Logger;


public class ArtworkPane extends Component implements ChangeIssuer, ChangeListener, Scaled	// TODO should this extent JComponent? We want double-buffering.
{
	private Artwork artwork;
	private Selection selected = new Selection();
	private ShootingRange shootingRange = new ShootingRange();
	private Clipping clipping = new Clipping();
	
	private ExportProfile stripTransm = new SimpleExportProfile();
	private DrawingContext drawingContext = new DefaultDrawingContext(null, selected, stripTransm, shootingRange); // TODO use factory, instead
	
	private JScrollPane scroller;
	
	private long lastMarkTime;
	private int paintsSinceLastMark;
	// private int paintsToTime = 30;
	private int paintsToTime = 0;

	
	private int lastMouseX, lastMouseY;
	private int lastMousePressX, lastMousePressY;
	private transient double scaleFactor = 1.0;
	
	private CreateEdit proposed;
	private PerimeterSegment pSeg = null;
	
	private CursorModeManager cursorModeManager;
	
	private SingleThreadedChangeSupport changeSupport = new SingleThreadedChangeSupport();
	
	private UndoableEditListener listenerToNotifyAboutUndoableEdits;
	
	private CompoundEdit compoundEdit = null;
	
	/**
	 * Targetable, Selectable, or null
	 */
	private Class typeOfThingBeingMoved = null;
	
	/**
	 * Allows user to move stems without selected them.
	 */
	private Stem targetedStem;
	
	/**
	 * Used to constrain motion when the shift key is down.
	 */
	private Boolean prevailingWindY;

	/**
	 * a dummy Drawable provided to DrawingFilter
	 */
	private final Drawable GUIDES = new com.smithandtinkers.geom.Parallelogram(); // TODO make this some other type. Misleading!
	
	private static final Stroke DOTTED_STROKE = new BasicStroke(1,
		    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
		    new float[] { 2, 2 }, 0);
	private static Stroke defaultStroke = null;
	
	private static Color VERY_LIGHT_GRAY = new Color(240, 240, 240);
	
	private static boolean maidenVoyage = true;
	
	private final static Ruler bottomRuler = new Ruler();
	private final static Ruler rightRuler = new Ruler();
	
	public ArtworkPane(CursorModeManager designatedCursorModeManager)
	{
		this();
		setCursorModeManager(designatedCursorModeManager);
	}
	
	public ArtworkPane()
	{
		addMouseListener(new MouseListener()
			{
				/**
				 * This final event issued as the result of a mouse click.
				 * 
				 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
				 */
				public void mouseClicked(MouseEvent myEvent)
				{
					Point thePoint = myEvent.getPoint();
					// System.out.println("BEFORE: myEvent.getPoint()="+thePoint);
					// scrollIfNecessary(thePoint);
					// System.out.println("AFTER: myEvent.getPoint()="+thePoint);
					// System.out.println("click");
					
					if (!myEvent.isShiftDown())
						selected.resetSelection();
					else
						selected.addToSelection();
					
					thePoint.x = (int) (thePoint.x / scaleFactor);
					thePoint.y = (int) (thePoint.y / scaleFactor);
					
					selected.setRectChoice(null);
					selected.setPointChoice(thePoint);
					// selected.setDirty(true, ArtworkPane.this);
					drawingContext.setIntermediate(false);
					stateChanged();
				}
				
				public void mouseEntered(MouseEvent myEvent)
				{		
				}
	
				public void mouseExited(MouseEvent myEvent)
				{	
				}
	
				public void mousePressed(MouseEvent myEvent)
				{	
					drawingContext.setIntermediate(true);
					
					typeOfThingBeingMoved = null;
					
					scrollIfNecessary(myEvent.getPoint());
					
					lastMousePressX = (int) (myEvent.getPoint().x/scaleFactor);
					lastMousePressY = (int) (myEvent.getPoint().y/scaleFactor);
					
					lastMouseX = lastMousePressX;
					lastMouseY = lastMousePressY;
					
					boolean dirtied = false;
					
					if (cursorModeManager==null || cursorModeManager.currentCursorMode()==ArtworkFrame.SELECT_STUFF_CURSOR_MODE)
					{
						if (selected.getSelectedItemsCount()==0)
						{
							if (selected.getRectChoice()==null)
							{
								selected.setRectChoice(new Rectangle());
							}
							
							selected.getRectChoice().setBounds(lastMousePressX, lastMousePressY, 0, 0);
							dirtied=true;
							// rubberbanding = true;
							selected.setPermitMultipleRegistrations(true);
						}
						
						maintainBufferedImage();
					}
					else if (cursorModeManager.currentCursorMode()!=null && cursorModeManager.currentCursorMode().isInvolvesCreation() && cursorModeManager.currentCursorMode().getSubject()==Balloon.class)
					{
						Point partiallyMapped = new Point((int)lastMousePressX, (int)lastMousePressY);
						
						Sill targeted = artwork.getSill().smallestTargetSill(partiallyMapped);
						
						if (targeted == null)
						{
							// Click outside any extant Sill. Forget it.
							return;
						}
						
						double mappedX = targeted.mapOutsideX(lastMousePressX);
						double mappedY = targeted.mapOutsideY(lastMousePressY);
						
						Balloon balloon = new Balloon();
						
						Crowd crowd = targeted.firstContainedCrowd();
						
						if (crowd==null)
						{
							crowd = new Crowd();
							targeted.add(0, crowd);
						}
						
						balloon.setText(AbstractNamed.NAMES_TEXT.getString("nonsensePhrase"));
						balloon.setLayedOut(true); // this should be set back to false on mouse release
						ArtworkPane.applyStyleDefaultsTo(balloon);
						
						if (getListenerToNotifyAboutUndoableEdits()!=null)
							balloon.getText().addUndoableEditListener(getListenerToNotifyAboutUndoableEdits());
						
						if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_NARRATIONS_CURSOR_MODE)
						{
							// balloon.add(new Parallelogram());
							balloon.add(InteractivePerch.perch(new Parallelogram(), balloon));
							balloon.setFillColor(Color.yellow);
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_STEMLESS_WORD_BALLOONS_CURSOR_MODE)
						{
							SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
							balloon.add(superEllipsePerch);
							superEllipsePerch.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_WORD_BALLOONS_CURSOR_MODE)						
						{
							SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
							superEllipsePerch.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							balloon.add(superEllipsePerch);
							ConcreteStem stem = new ConcreteStem();
							Point2D.Double focus = new Point2D.Double(targeted.mapOutsideX(lastMouseX), targeted.mapOutsideY(lastMouseY) + 130.0);
							stem.setFocus(focus);
							superEllipsePerch.add(stem);
							// System.out.println("borough: stem.getParent()="+stem.getParent());
							// System.out.println("country: stem.getParent().getParent()="+((Kid)stem.getParent()).getParent());
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_CURVED_WORD_BALLOONS_CURSOR_MODE)
						{
							SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
							superEllipsePerch.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							balloon.add(superEllipsePerch);
							ConcreteStem stem = new ConcreteStem();
							stem.setFocusStemInclinationInRadians(0.20);
							stem.setRootStemInclinationInRadians(0.25);
							Point2D.Double focus = new Point2D.Double(targeted.mapOutsideX(lastMouseX), targeted.mapOutsideY(lastMouseY) + 130.0);
							stem.setFocus(focus);
							superEllipsePerch.add(stem);
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_THOUGHT_BALLOONS_CURSOR_MODE)
						{
							SuperEllipsePerch tb = new SuperEllipsePerch();
							tb.getPunctedSuperEllipse().rufflePrettily();
							tb.getPunctedSuperEllipse().setHeinParameter(BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							ConcreteStem stem = new ConcreteStem();
							stem.setType(AbstractStem.BUBBLED_TYPE);
							stem.setFocusStemInclinationInRadians(0.25);
							stem.setRootStemInclinationInRadians(0.25);
							Point2D.Double focus = new Point2D.Double(targeted.mapOutsideX(lastMouseX), targeted.mapOutsideY(lastMouseY) + 130.0);
							stem.setFocus(focus);
							// System.out.println("set stem.bubbleCount()="+stem.getBubbleCount());
							// tb.getPunctedSuperEllipse().addIcicleStem(stem);
							tb.add(stem);
							// System.out.println("after add, tb.stemCount ="+tb.stemCount());
							balloon.add(tb);
							// System.out.println("after add, balloon.stemCount ="+balloon.stemCount());
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_STEMLESS_THOUGHT_BALLOONS_CURSOR_MODE)
						{
							SuperEllipsePerch tb = new SuperEllipsePerch();
							tb.getPunctedSuperEllipse().rufflePrettily();
							tb.getPunctedSuperEllipse().setHeinParameter(BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							balloon.add(tb);
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_LOLLIPOP_BALOOONS_CURSOR_MODE)
						{
							SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
							superEllipsePerch.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							balloon.add(superEllipsePerch);
							ConcreteStem stem = new ConcreteStem();
							stem.setType(AbstractStem.LOLLIPOP_TYPE);
							Point2D.Double focus = new Point2D.Double(targeted.mapOutsideX(lastMouseX), targeted.mapOutsideY(lastMouseY) + 130.0);
							stem.setFocus(focus);
							balloon.setOutlineColor(null);
							superEllipsePerch.add(stem);
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_STARBURST_CURSOR_MODE)
						{
							SuperEllipsePerch tb = new SuperEllipsePerch();
							tb.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							tb.getPunctedSuperEllipse().getRuffledSuperEllipse().setType(RuffleableSuperEllipse.JAGGED_TYPE);
							tb.getPunctedSuperEllipse().rufflePrettily();
							balloon.add(tb);
							balloon.setFillColor(Color.pink);
						}
						else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_GOUACHE_CURSOR_MODE)
						{
							SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
							superEllipsePerch.setHeinParameter(
									BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultHeinParameter());
							balloon.add(superEllipsePerch);
							balloon.setOutlineColor(null);
						}
						
						// crowd.add(balloon);
						proposed = new CreateEdit(crowd, balloon);
						proposed.execute();
						
						// System.out.println("mapped mouse point: ("+mappedX+", "+mappedY+")");
						balloon.setCenter(new Point2D.Double(Math.round(mappedX), Math.round(mappedY)));
						
						// balloon.addAllChangeListeners(changeSupport);
						
						dirtied = true;
						
						selected.setPointChoice(null);
						selected.setRectChoice(null);
						selected.resetSelection();
						
						if (proposed.getCreation() instanceof Selectable)
							selected.registerSelection((Selectable)proposed.getCreation());
						
						pSeg = PerimeterSegment.BOTTOM_RIGHT_CORNER;
						
						maintainBufferedImage();
					}
					else if (cursorModeManager.currentCursorMode()==ArtworkFrame.CREATE_PANELS_CURSOR_MODE)
					{
						Sill babySill = new Sill();
						int x = (int) (myEvent.getX()/scaleFactor);
						int y = (int) (myEvent.getY()/scaleFactor);
						
						double mappedX = artwork.getSill().mapOutsideX(x);
						double mappedY = artwork.getSill().mapOutsideY(y);
						
						babySill.setLocation(new Point2D.Double(mappedX, mappedY));
						AperturePerch ap = new AperturePerch(babySill);
								
						Rectangle shape = new Rectangle();
						shape.setRect(0.0, 0.0, 1.0, 1.0);
						ap.setShape(shape);

						babySill.setAperture(ap);
						babySill.setLineThickness(BalloonEngineState.getInstance().getBalloonistPreferences().getDefaultLineThicknessInPoints());
						
						// babySill.addAllChangeListeners(changeSupport);
						// System.out.println("new babySill="+babySill);
						
						proposed = new CreateEdit(artwork.getSill(), babySill);
						proposed.execute();
						
						dirtied = true;
						
						selected.setPointChoice(null);
						selected.setRectChoice(null);
						selected.resetSelection();
						
						if (proposed.getCreation() instanceof Selectable)
							selected.registerSelection((Selectable)proposed.getCreation());
						
						pSeg = PerimeterSegment.BOTTOM_RIGHT_CORNER;
					}
					
					if (dirtied)
					{
						stateChanged();
						dirtied = false;
					}
				}
				
				public void mouseReleased(MouseEvent myEvent)
				{	
					drawingContext.setIntermediate(false);
					typeOfThingBeingMoved = null;

					// System.out.println("mouse released");
					Point thePoint = myEvent.getPoint();
					scrollIfNecessary(thePoint);
					
					selected.reLayoutCrowdsOfSelectedItems();
					shootingRange.reLayoutCrowdsOfTargetedItems();
					
					selected.setPermitMultipleRegistrations(false);
					
					if (proposed!=null)
					{
						if (!(proposed.getCreation() instanceof Resizeable) || !wickedSmall((Resizeable)proposed.getCreation()))
						{
							queueEdit(proposed);
							
							if (proposed.getCreation() instanceof TextShape)
								((TextShape) proposed.getCreation()).setLayedOut(false);
						}
						else
						{
							selected.delete();
						}
						
						proposed = null;
					}
					
					announceEdit();
					
					stateChanged();
					pSeg = null;
					prevailingWindY = null;
					
					drawingContext.getDrawingFilter().setFilterState(null);
					drawingContext.getDrawingFilter().clear();
					
					prepareBufferedImage(); // TODO maintianBufferedImage() instead?
				}
			}
		);
		
		addMouseMotionListener(new MouseMotionListener()
			{
				public void mouseDragged(MouseEvent e)
				{
					if (cursorModeManager!=null && cursorModeManager.currentCursorMode()!=ArtworkFrame.SELECT_STUFF_CURSOR_MODE && proposed==null)
					{
						return;
					}
					
					int x = (int) (e.getX()/scaleFactor);
					int y = (int) (e.getY()/scaleFactor);
					
					boolean dirtied = false;
					drawingContext.setIntermediate(true);
					
					if (shootingRange.getTarget()!=null && shootingRange.getAlley()!=null && typeOfThingBeingMoved!=Selectable.class
							&& !isRubberbanding())
					{
						TranslateEdit translateEdit = null;

						for (int loop=0; loop<=shootingRange.getAlley().size()-1; loop++)
						{
							Targetable targetable = (Targetable) shootingRange.getAlley().get(loop);

							if (targetable.isTargeted(shootingRange))
							{
								if (translateEdit==null)
									translateEdit = new TranslateEdit(pSeg, x-lastMouseX, y-lastMouseY);

								translateEdit.add(targetable);
								typeOfThingBeingMoved = Targetable.class;
							}
						}

						if (translateEdit!=null && translateEdit.size()>1 && typeOfThingBeingMoved == Targetable.class) // prevent very complicated slides
						{
						 	return;
						}
						
						if (translateEdit!=null)
						{
							dirtied=translateEdit.execute();
							queueEdit(translateEdit);
						}
						
						Point thePoint = e.getPoint();

						thePoint.x = x;
						thePoint.y = y;
						shootingRange.setTarget(thePoint);
					}
					
					if (!dirtied)
					{
						// if (rubberbanding && selected.getRectChoice()!=null)
						if (selected.isPermitMultipleRegistrations() && selected.getRectChoice()!=null)
						{
							// selected.getRectChoice().setFrameFromDiagonal(selected.getRectChoice().getX(), selected.getRectChoice().getY(), (int)x,(int)y);

							selected.getRectChoice().setFrameFromDiagonal(lastMousePressX, lastMousePressY, lastMouseX, lastMouseY);
							selected.resetSelection();
							dirtied = true;
						}

						Boolean clickInsideAtLeastOneSelectedDrawable = null;

						if (pSeg==null &&!selected.isPermitMultipleRegistrations())
						{
							if (clickInsideAtLeastOneSelectedDrawable==null)
							{
								if (clickInsideAtLeastOneSelectedDrawable())
									clickInsideAtLeastOneSelectedDrawable = Boolean.TRUE;
								else
									clickInsideAtLeastOneSelectedDrawable = Boolean.FALSE;
							}

							if (clickInsideAtLeastOneSelectedDrawable==Boolean.TRUE)
							{
								// drag entire selection

								TranslateEdit translateEdit = null;
								
								if (!e.isShiftDown())
								{
									translateEdit = new TranslateEdit(pSeg, x-lastMouseX, y-lastMouseY);
								}
								else
								{
									if (prevailingWindY==null)
									{
										if (Math.abs(x-lastMouseX) > Math.abs(y-lastMouseY))
											prevailingWindY = Boolean.FALSE;
										else
											prevailingWindY = Boolean.TRUE;
									}
									
									if (prevailingWindY==Boolean.FALSE)
										translateEdit = new TranslateEdit(pSeg, x-lastMouseX, 0);
									else
										translateEdit = new TranslateEdit(pSeg, 0, y-lastMouseY);
								}
								
								translateEdit.addItemsFrom(selected.iterateOverSelectedItems());
								
								if (e.isMetaDown())
								{
									// add any child stems, too
									
									Iterator walk = selected.iterateOverSelectedItems();
									while (walk.hasNext())
									{
										Object item = walk.next();
										
										if (item instanceof Stemmed)
										{
											Stemmed asStemmed = (Stemmed) item;
											for (int stemIndex=0; stemIndex<=asStemmed.stemCount()-1; stemIndex++)
												translateEdit.add(asStemmed.getStem(stemIndex));
										}
									}
								}
								
								dirtied = translateEdit.execute();
								queueEdit(translateEdit);
								typeOfThingBeingMoved = Selectable.class;
								
								if (drawingContext.getDrawingFilter().size()>0 &&
									!drawingContext.getDrawingFilter().containsInstanceOf(Sill.class))
									drawingContext.getDrawingFilter().setFilterState(DrawingFilter.RETAIN);
							}
						}
						else if (pSeg!=null && pSeg.involvesResizing())
						{
							if (drawingContext.getDrawingFilter().size()>0 &&
									!drawingContext.getDrawingFilter().containsInstanceOf(Sill.class)
									&& 
										(cursorModeManager==null || cursorModeManager.currentCursorMode()!=ArtworkFrame.CREATE_PANELS_CURSOR_MODE))
							{
								drawingContext.getDrawingFilter().setFilterState(DrawingFilter.RETAIN);
							}
							
							SelectionEdit resizeEdit = null;

							if (e.isShiftDown())
							{
								double aspectRatio = selected.determineAspectRatio();
								// System.out.println("aspectRatio="+aspectRatio);
								
								if (pSeg==PerimeterSegment.BOTTOM_RIGHT_CORNER || pSeg==PerimeterSegment.TOP_LEFT_CORNER)
								{
									// resizeEdit = new Resize2dEdit(pSeg, lastMouseX, lastMouseX, x, x);
									
									resizeEdit = new Resize2dEdit(pSeg, lastMouseX, lastMouseX, x,
											lastMouseX+((double)(x-lastMouseX)/aspectRatio));
								}
								else if (pSeg==PerimeterSegment.TOP_RIGHT_CORNER || pSeg==PerimeterSegment.BOTTOM_LEFT_CORNER)
								{
									// resizeEdit = new Resize2dEdit(pSeg, lastMouseX, -lastMouseX, x, -x);
									resizeEdit = new Resize2dEdit(pSeg, lastMouseX, lastMouseX, x,
											lastMouseX-((double)(x-lastMouseX)/aspectRatio));
								}
							}

							if (resizeEdit==null)
								resizeEdit = new ResizeEdit(pSeg, lastMouseX, lastMouseY, x, y);

							resizeEdit.addSelectablesFrom(selected);
							dirtied = resizeEdit.execute();
							
							if (proposed==null)
								queueEdit(resizeEdit);
						}
						else if (pSeg==PerimeterSegment.HOT_SPOT)
						{
							TranslateEdit translateEdit = new TranslateEdit(pSeg, x-lastMouseX, y-lastMouseY); // TODO consolidate with earlier block?
							
							Iterator walk = selected.iterateOverSelectedItems();
							
							while (walk.hasNext())
							{
								Selectable selectable = (Selectable) walk.next();
								
								if (selectable instanceof Stem)
								{
									translateEdit.add(selectable);
								}
								else if (selectable instanceof Stemmed && targetedStem!=null)
								{
									translateEdit.add(targetedStem);
								}
							}
							
							dirtied = translateEdit.execute();
							queueEdit(translateEdit);
						}
						else if (pSeg==PerimeterSegment.STEM_BODY)
						{
							LeadingEdgeRotationEdit leadingEdgeRotationEdit = new LeadingEdgeRotationEdit();
							
							Iterator walk = selected.iterateOverSelectedItems();
							
							while (walk.hasNext())
							{
								Selectable selectable = (Selectable) walk.next();
								
								if (selectable instanceof Stem)
								{
									leadingEdgeRotationEdit.add(selectable);
								}
								else if (selectable instanceof Stemmed && targetedStem!=null)
								{
									leadingEdgeRotationEdit.add(targetedStem);
								}
							}
							
							leadingEdgeRotationEdit.slide(lastMouseX, lastMouseY, x, y);
							
							dirtied = leadingEdgeRotationEdit.execute();
							queueEdit(leadingEdgeRotationEdit);
						}
					}
					
					if (dirtied)
					{
						// System.out.println("ArtworkPane: dirtying...");
						// selected.setDirty(true, ArtworkPane.this);
						stateChanged();
						dirtied = false;
					}
					
					lastMouseX = x;
					lastMouseY = y;
				}

				private boolean clickInsideAtLeastOneSelectedDrawable()
				{
					Selectable selectable = null;
					
					for (int loop=0; loop<=selected.getSelectedItemsCount()-1; loop++)
					{
						selectable = selected.getSelectedItem(loop);
						
						if (selectable instanceof Relocateable)
						{
							Relocateable relocateable = (Relocateable) selectable;
							
							Rectangle2D drawableResizeableBounds = relocateable.getResizeableBounds2D();
							
							if (drawableResizeableBounds==null
								|| drawableResizeableBounds.contains(
									Sill.mapAbsoluteXIntoCoordinateFrameOf(lastMouseX, relocateable), 
									Sill.mapAbsoluteYIntoCoordinateFrameOf(lastMouseY, relocateable)))
							{
								return true;
							}
						}
					}
					
					return false;
				}

				public void mouseMoved(MouseEvent e)
				{
					if (cursorModeManager!=null && cursorModeManager.currentCursorMode()!=ArtworkFrame.SELECT_STUFF_CURSOR_MODE)
					{
						return;
					}
					
					Point thePoint = e.getPoint();
					
					int x = (int) (e.getX()/scaleFactor);
					int y = (int) (e.getY()/scaleFactor);
					
					thePoint.x = x;
					thePoint.y = y;
					shootingRange.setTarget(thePoint);

					Selectable selectable = null;
					targetedStem = null;
					
					boolean useMoveCursor = false;
						
					for (int loop=0; !useMoveCursor && loop<=selected.getSelectedItemsCount()-1; loop++)
					{
						selectable = (Selectable) selected.getSelectedItem(loop);
						
						Perimetered perimed = null;
						
						if (selectable instanceof Perimetered)
						{
							perimed = (Perimetered) selectable;
						}
						
						Resizeable resizeable = null;
						
						double mappedX = Sill.mapAbsoluteXIntoCoordinateFrameOf(x, selectable);
						double mappedY = Sill.mapAbsoluteYIntoCoordinateFrameOf(y, selectable);

						if (selectable instanceof Resizeable)
						{
							resizeable = (Resizeable) selectable;
						}
						else if (selectable instanceof Stem)
						{
							Stem stem = (Stem) selectable;
							pSeg = PerimeterSegment.identify(mappedX , mappedY, stem);
							// System.out.println("stem pSeg"+pSeg);
						}
						
						if (resizeable!=null)
						{
							pSeg = PerimeterSegment.identify( resizeable.getResizeableBounds2D(), mappedX , mappedY, perimed);
							if (targetedStem==null && (pSeg==PerimeterSegment.HOT_SPOT || pSeg==PerimeterSegment.STEM_BODY))
							{
								if (selectable instanceof Stemmed)
								{
									Stem stem = PerimeterSegment.whichStem((Stemmed)selectable, mappedX, mappedY);

									if (stem!=null)
										targetedStem = stem;
								}
							}
						}
						
						if (pSeg==PerimeterSegment.STEM_LEADING_EDGE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)); // TODO keep?
							return;
						}
						else if (pSeg==PerimeterSegment.STEM_TRAILING_EDGE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)); // TODO keep?
							return;
						}
						else if (pSeg==PerimeterSegment.TOP_SIDE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.LEFT_SIDE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.RIGHT_SIDE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.BOTTOM_SIDE)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.TOP_LEFT_CORNER)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.BOTTOM_LEFT_CORNER)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.BOTTOM_RIGHT_CORNER)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.TOP_RIGHT_CORNER)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.ACTUAL_PERIMETER || pSeg==PerimeterSegment.HOT_SPOT)
						{
							ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
							return;
						}
						else if (pSeg==PerimeterSegment.STEM_BODY)
						{
							useMoveCursor = true;
						}
						else if (resizeable!=null)
						{
							Rectangle2D bounds = resizeable.getResizeableBounds2D();

							if (bounds==null || bounds.contains(mappedX, mappedY)) // TODO perform this ID in PerimeterSegment
								useMoveCursor = true;
						}
					}
					
					if (!useMoveCursor && shootingRange.getTarget()!=null && shootingRange.getAlley()!=null)
					{
						// System.out.println("\tshootingRange.getAlley().size()="+shootingRange.getAlley().size());
						
						for (int loop=0; !useMoveCursor && loop<=shootingRange.getAlley().size()-1; loop++)
						{
							Targetable targetable = (Targetable) shootingRange.getAlley().get(loop);
							
							if (targetable.isTargeted(shootingRange))
								useMoveCursor = true;
						}
					}
					
					if (useMoveCursor)
						ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					else if (cursorModeManager==null)
						ArtworkPane.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					else
						cursorModeManager.manageCursorMode();
				}
			}
		);
		
		addChangeListener(this);
		
		requestFocus();
	}
	
	protected void stateChanged()
	{
		invalidate();
		repaint();
	}
	
	public void stateChanged(ChangeEvent changeEvent)
	{
		stateChanged();
	}

	public void paintComponent(Graphics g)
	{
		// super.paintComponent(g);
		
		if (paintsToTime>0 && paintsSinceLastMark==0)
		{
			lastMarkTime = System.currentTimeMillis();
		}
		
		Graphics2D g2 = (Graphics2D) g;
		
		if (artwork.getSill()!=null)
		{
			if (maidenVoyage)
			{
				maidenVoyage = false;
			}
			
			((DefaultDrawingContext)drawingContext).setGraphics(g2);
			drawingContext.antiAliasLines();
			
			// if (typeOfThingBeingMoved!=null || proposed!=null) // i.e. a move or drag is in progress
			// if (typeOfThingBeingMoved!=null || (pSeg!=null && pSeg.involvesResizing() && drawingContext.isIntermediate())) // i.e. a move or drag is in progress
			if (drawingContext.getDrawingFilter().getFilterState()==DrawingFilter.RETAIN)
			{
				((DefaultDrawingContext)drawingContext).getGraphics().setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				((DefaultDrawingContext)drawingContext).getGraphics().setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				// System.out.println("* "+new java.util.Date());
				
				drawingContext.drawImage(bufferedImage, 0, 0, null);
			}
			
			if (drawingContext.isVisible(GUIDES))
			{
				drawingContext.setColor(Color.lightGray);
				drawingContext.fillRect(0, 0, getWidth(), getHeight());
			}

			((DefaultDrawingContext)drawingContext).scale(scaleFactor);
			
			if (drawingContext.isVisible(GUIDES)) // TODO make Layout drawable and delegate to it here
			{
				drawingContext.setColor(Color.white);
				drawingContext.fillRect(0, 0, artwork.getEnclosure().width, artwork.getEnclosure().height);
				
				if (artwork.getLayout()!=null)
				{
					Rectangle usableArea = artwork.getLayout().getBounds();
					
					drawingContext.setColor(VERY_LIGHT_GRAY);
					
					drawingContext.fillRect((int)artwork.getLayout().getHorizontalPageMarginInPoints(), 
							(int)artwork.getLayout().getVerticalPageMarginInPoints(),
							usableArea.width, usableArea.height);
					
					drawingContext.setColor(Color.lightGray);
					
					drawingContext.drawRect((int)artwork.getLayout().getHorizontalPageMarginInPoints(), 
							(int)artwork.getLayout().getVerticalPageMarginInPoints(),
							usableArea.width, usableArea.height);
				}
			}

			drawingContext.setColor(Color.black);
			
			artwork.getSill().draw(drawingContext);
			
			if (drawingContext.isVisible(GUIDES))
			{
				LabeledQuantityField.UnitChoice uc 
						= (LabeledQuantityField.UnitChoice) DrawableDialog.UNITS.getSelectedItem();
				
				if (scaleFactor*getSize().getHeight()>artwork.getEnclosure().getHeight()+1)
				{
					bottomRuler.getOrigin().setLocation(
							0.0,
							artwork.getEnclosure().getHeight()+2);
					bottomRuler.setLength(artwork.getEnclosure().getWidth());
					bottomRuler.setVertical(false);
					bottomRuler.setScale(uc.getAmount());
					bottomRuler.draw(drawingContext);
				}
				
				if (scaleFactor*getSize().getWidth()>artwork.getEnclosure().getWidth()+1)
				{
					rightRuler.getOrigin().setLocation(artwork.getEnclosure().getWidth()+2,
							0.0);
					rightRuler.setLength(artwork.getEnclosure().getHeight());
					rightRuler.setVertical(true);
					rightRuler.setScale(uc.getAmount());
					rightRuler.draw(drawingContext);
				}
				
				drawingContext.setColor(Color.orange);
				drawingContext.drawRect(0, 0, artwork.getEnclosure().width-1, artwork.getEnclosure().height-1);
				
				// if (rubberbanding && selected.getRectChoice()!=null)
				if (selected.isPermitMultipleRegistrations()  && selected.getRectChoice()!=null)
				{
					g2.setColor(Color.gray);
	
					if (defaultStroke==null)
					{
						defaultStroke=g2.getStroke();
					}
	
					g2.setStroke(DOTTED_STROKE);
					g2.draw(selected.getRectChoice());
	
					g2.setStroke(defaultStroke);
					g2.setColor(Color.black);
				}
			}
			
			((DefaultDrawingContext)drawingContext).scale(1/scaleFactor);
		}		
		
		// System.out.println("ArtworkPane: cleaning");
		selected.setDirty(false, this);
		
		if (paintsToTime>0)
		{
			if (paintsSinceLastMark>=paintsToTime)
			{
				long averageDurationMillis = (System.currentTimeMillis()-lastMarkTime) / paintsToTime;
				System.out.println("artwork: average paint time = "+averageDurationMillis+" millis");
				paintsSinceLastMark = 0;
				// System.out.println("free memory = "+(Runtime.getRuntime().freeMemory()/1000) + "kb");
			}
			else
				paintsSinceLastMark++;
		}
	}

	public Dimension getPreferredSize()
	{
		return getScaledEnclosure();
	}
	
	public Dimension getMinimumSize()
	{
		return getScaledEnclosure();
	}

	public void setArtwork(Artwork designatedArtwork)
	{
		if (artwork != designatedArtwork)
		{
			Artwork oldArtwork = artwork;
			artwork = designatedArtwork;
			
			if (artwork!=null && artwork.getSill()!=null)
				artwork.getSill().setChangeSupport(changeSupport);
			
			if (oldArtwork!=null && oldArtwork.getSill()!=null && oldArtwork.getSill().getChangeSupport()!=null)
			{
				oldArtwork.getSill().setChangeSupport(null);
			}
			
			stateChanged();
		}
	}
	
	public void selectNone()
	{
		Logger.println();
		
		selected.setPointChoice(null);
		selected.setRectChoice(null);
		selected.resetSelection();
		selected.setDirty(false, this);
		stateChanged();
	}

	public CursorModeManager getCursorModeManager()
	{
		return cursorModeManager;
	}
	
	public void setCursorModeManager(CursorModeManager designatedCursorModeManager)
	{
		cursorModeManager = designatedCursorModeManager;
	}

	public void copySelection(Selection selection) throws DocumentException
	{
		clipping.copy(selection);
	}

	public void setScroller(JScrollPane designatedScroller)
	{
		scroller = designatedScroller;
	}
	
	public JScrollPane getScroller()
	{
		return scroller;
	}
	
	public void scrollIfNecessary(Point thePoint)
	{
		if (scroller!=null)
		{
			thePoint.x -= scroller.getHorizontalScrollBar().getValue();
			thePoint.y -= scroller.getVerticalScrollBar().getValue();
		}
	}
	
	public void paint(Graphics g)
	{
		paintComponent(g);
		
		super.paint(g);
	}

	public void deleteSelection() throws DocumentException
	{
		// selected.delete();
		
		DeleteEdit deleteEdit = new DeleteEdit();
		deleteEdit.addSelectablesFrom(selected);
		if (deleteEdit.execute())
		{
			queueEdit(deleteEdit);
			stateChanged();
			announceEdit();
		}
		selected.resetSelection();
	}

	public void bringForward()
	{
		BringForwardEdit bringForwardEdit = new BringForwardEdit();
		bringForwardEdit.addSelectablesFrom(selected);
		
		bringForwardEdit.execute();
		queueEdit(bringForwardEdit);
		
		stateChanged();
		announceEdit();
	}

	public void sendBackward()
	{
		SendBackwardsEdit sendBackwardsEdit = new SendBackwardsEdit();
		sendBackwardsEdit.addSelectablesFrom(selected);
		
		sendBackwardsEdit.execute();
		queueEdit(sendBackwardsEdit);
		
		stateChanged();
		announceEdit();
	}

	public void bringToFront()
	{
		BringToFrontEdit bringToFrontEdit = new BringToFrontEdit();
		bringToFrontEdit.addSelectablesFrom(selected);
		
		// System.out.println("selected="+selected.toString());
		
		bringToFrontEdit.execute();
		queueEdit(bringToFrontEdit);
		
		stateChanged();
		announceEdit();
	}

	public void sendToBack()
	{
		SendToBackEdit sendToBackEdit = new SendToBackEdit();
		sendToBackEdit.addSelectablesFrom(selected);
		
		// System.out.println("selected="+selected.toString());
		
		sendToBackEdit.execute();
		queueEdit(sendToBackEdit);
		
		stateChanged();
		announceEdit();
	}
	
	public Selection getSelection()
	{
		return selected;
	}
	
	public double getScaleFactor()
	{
		return scaleFactor;
	}
	
	public void setScaleFactor(double scaleFactor)
	{
		this.scaleFactor = scaleFactor;
	}

	/**
	 * @see com.smithandtinkers.mvc.ChangeIssuer#addChangeListener(javax.swing.event.ChangeListener)
	 */
	public void addChangeListener(ChangeListener designatedListener)
	{
		changeSupport.addChangeListener(designatedListener);
	}

	/**
	 * @see com.smithandtinkers.mvc.ChangeIssuer#removeChangeListener(javax.swing.event.ChangeListener)
	 */
	public void removeChangeListener(ChangeListener designatedChangeListener)
	{
		changeSupport.removeChangeListener(designatedChangeListener);
	}

	private void addStemTo(SuperEllipsePerch superEllipsePerch)
	{
		ConcreteStem istem = new ConcreteStem();
		Point2D.Double focus = new Point2D.Double(
			Sill.mapAbsoluteXIntoCoordinateFrameOf(lastMouseX, superEllipsePerch), 
			Sill.mapAbsoluteYIntoCoordinateFrameOf(lastMouseY, superEllipsePerch) + 130.0);
		istem.setFocus(focus);
		
		// TODO look at all the other stems and chose a comfortable root position for the new one.
		
		istem.setLeadingEdgePositionAsSideFraction(0.2);
		
		if (superEllipsePerch.getPunctedSuperEllipse().isRuffled())
		{
			istem.setType(AbstractStem.BUBBLED_TYPE);
			istem.setFocusStemInclinationInRadians(BalloonEngine.DEFAULT_FOCUS_STEM_INCLINATION_IN_RADIANS);
			istem.setRootStemInclinationInRadians(BalloonEngine.DEFAULT_ROOT_STEM_INCLINATION_IN_RADIANS);
		}
		
		// TODO look at other stems to determine whether we should use stem type lollipop?
		
		CreateEdit createEdit = new CreateEdit(PresentableEdit.MENU_TEXT.getString("addStemLabel"), superEllipsePerch, istem);
		if (createEdit.execute())
		{
			queueEdit(createEdit);
			announceEdit();
		}
	}

	public UndoableEditListener getListenerToNotifyAboutUndoableEdits()
	{
		return listenerToNotifyAboutUndoableEdits;
	}

	public void setListenerToNotifyAboutUndoableEdits(UndoableEditListener designatedListenerToNotifyAboutUndoableEdits)
	{
		listenerToNotifyAboutUndoableEdits = designatedListenerToNotifyAboutUndoableEdits;
	}
	
	public void queueEdit(PresentableEdit designatedEdit)
	{
		if (compoundEdit==null)
			compoundEdit = new CompoundEdit();
		
		compoundEdit.addEdit(designatedEdit);
	}
	
	public void announceEdit()
	{
		if (compoundEdit==null)
			return;
		
		for (int loop=0; loop<=selected.getSelectedItemsCount()-1; loop++)
		{
			if (selected.getSelectedItem(loop) instanceof Resizeable)
			{
				Resizeable resizeable = (Resizeable) selected.getSelectedItem(loop);
				
				if (resizeable.getWidth()<0)
				{
					FlipWidthEdit flipWidthEdit = new FlipWidthEdit();
					flipWidthEdit.add((Selectable)resizeable); // TODO make this more concise
					flipWidthEdit.execute();
					compoundEdit.addEdit(flipWidthEdit);
				}
				
				if (resizeable.getHeight()<0)
				{
					FlipHeightEdit flipHeightEdit = new FlipHeightEdit();
					flipHeightEdit.add((Selectable)resizeable); // TODO make this more concise
					flipHeightEdit.execute();
					compoundEdit.addEdit(flipHeightEdit);
				}
			}
		}
		
		compoundEdit.end();
		
		if (getListenerToNotifyAboutUndoableEdits()!=null)
			getListenerToNotifyAboutUndoableEdits().undoableEditHappened(new UndoableEditEvent(this, compoundEdit));
		
		compoundEdit = null;
	}
	
	public static boolean wickedSmall(Resizeable designatedResizeable)
	{
		double widthMagnitude = Math.abs(designatedResizeable.getWidth());
		double heightMagnitude = Math.abs(designatedResizeable.getHeight());
		
		final int MININUM_ACCEPTABLE_DIMENSION = 11;
		
		if (widthMagnitude<MININUM_ACCEPTABLE_DIMENSION && heightMagnitude<MININUM_ACCEPTABLE_DIMENSION)
		{
			return true;
		}
		
		return false;
	}
	
	public Clipping getClipping()
	{
		return clipping;
	}
	
	public void copySelection()
	{
		try
		{
			clipping.copy(selected);
		}
		catch (DocumentException exception)
		{
			throw new BugException(exception);
		}
	}
	
	public Dimension getScaledEnclosure()
	{
		if (scaleFactor==1.0)
		{
			if (artwork!=null)
				return artwork.getEnclosure();
			else
				return new Dimension(726, 540); // to tide us over until intialization is complete
		}
		
		Dimension scaledEnclosure = new Dimension();
		scaledEnclosure.setSize(scaleFactor*artwork.getEnclosure().getWidth(), scaleFactor*artwork.getEnclosure().getHeight());
		return scaledEnclosure;
	}
	
	/**
	 * @return true, if successful
	 */
	public boolean addStem()
	{
		Drawable drawable = null;
						
		for (int loop=0; loop<=selected.getSelectedItemsCount()-1; loop++)
		{
			drawable = (Drawable) selected.getSelectedItem(loop);

			if (drawable instanceof Balloon)
			{
				Balloon balloon = (Balloon) drawable;
				if (balloon.size()>0)
				{
					if (balloon.get(0) instanceof SuperEllipsePerch)
					{
						SuperEllipsePerch superEllipsePerch = (SuperEllipsePerch) balloon.get(0);

						addStemTo(superEllipsePerch);
						return true;
					}
					else
					{
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
			else if (drawable instanceof SuperEllipsePerch)
			{
				SuperEllipsePerch superEllipsePerch = (SuperEllipsePerch) drawable;

				addStemTo(superEllipsePerch);
				return true;
			}
		}
		
		return false;
	}
		
	public Artwork getArtwork()
	{
		return artwork;
	}
	
	private BufferedImage bufferedImage;
	
	private void prepareBufferedImage()
	{
		if (getWidth()==0 && getHeight()==0)
		{
			// too soon
			
			return;
		}
		
		if (bufferedImage==null || bufferedImage.getWidth()!=getWidth() || bufferedImage.getHeight()!=getHeight())
		{
			// System.out.println("refreshing image copy dimensions...");
			
			bufferedImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice(
					).getDefaultConfiguration(
					).createCompatibleImage(getWidth(), getHeight());
		}
	}
	
	private void maintainBufferedImage()
	{
		prepareBufferedImage();
		
		// System.out.println("refreshing image copy contents...");
		drawingContext.getDrawingFilter().clear();
		
		Iterator walk = drawingContext.getSelected().iterateOverSelectedItems();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			
			if (item instanceof Sill)
			{
				drawingContext.getDrawingFilter().add( (Sill) item); // this will filter out duplicates
			}
			else if (item instanceof Kid)
			{
				Crowd foundCrowd = Crowd.findCrowdOf( (Kid)item );
				// System.out.println("foundCrowd="+foundCrowd);
				drawingContext.getDrawingFilter().add(foundCrowd); // this will filter out duplicates
			}
		}
		
		// TODO suppose the drawing filter is still empty at this point?
		
		drawingContext.getDrawingFilter().setFilterState(DrawingFilter.EXCLUDE);
		
		// System.out.println("drawingContext.getDrawingFilter()="+drawingContext.getDrawingFilter());
		// System.out.println("painting to buffer...");
		paintComponent(bufferedImage.createGraphics());
		
		((DefaultDrawingContext)drawingContext).getGraphics().dispose();
		
		// TODO can something be done about this delay?
		
		drawingContext.getDrawingFilter().setFilterState(null);
		
		// System.out.println("at end: drawingContext.getDrawingFilter()="+drawingContext.getDrawingFilter());
	}
	
	public void setSize(Dimension designatedDimension)
	{
		super.setSize(designatedDimension);	
		
		prepareBufferedImage();
	}
	
	public boolean isRubberbanding()
	{
		if (selected.getRectChoice()==null)
			return false;
		
		if (selected.getRectChoice().getWidth()>0)
			return true;
			
		if (selected.getRectChoice().getHeight()>0)
			return true;
		
		return false;
	}
	
	/**
	 * Determines whether TextLayout uses {@link java.awt.Graphics2D#drawString}
	 * on the platform, which in turn determines whether iText will be able to editably
	 * export text to PDF.
	 *
	 * This also conveys the advantage of incurring the costs of loading this class
	 * up front, to improve performance later.
	 */
	public static boolean determineWhetherEditableTextExportPossible()
	{
		try
		{
			final long startTime = System.currentTimeMillis();
			DefaultStyledDocument dummyDoc = new DefaultStyledDocument();
			dummyDoc.insertString(0, "Hello to all of our friends out in Software Land!", null);
			Sill outerSill = new Sill();
			Sill sill = new Sill();
			outerSill.add(sill);
			AperturePerch perch = new AperturePerch(sill);
			Rectangle2D shape = new Rectangle2D.Double();
			shape.setRect(0.0, 0.0, 390.0, 290.0);
			perch.setShape(shape);
			sill.setAperture(perch);
			Crowd crowd = new Crowd();
			sill.add(crowd);
			Balloon dummy = new Balloon(dummyDoc, crowd);
			dummy.setName("intializer");
			dummy.setLayedOut(false);
			crowd.add(dummy);
			PunctuatedSuperEllipse punctd = new PunctuatedSuperEllipse();
			punctd.setSemiMajorAxis(260);
			punctd.setSemiMinorAxis(85);
			SuperEllipsePerch sep = new SuperEllipsePerch(punctd);
			dummy.setLocation(80, 60);
			dummy.add(sep);
			
			BufferedImage bufferedImage = GraphicsEnvironment.getLocalGraphicsEnvironment(
				).getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(400, 300);
			DefaultDrawingContext drawingContext = (DefaultDrawingContext) DrawingContextFactory.getDrawingContext();
			
			final TextinessSniffingGraphics2D tsg2 = new TextinessSniffingGraphics2D(bufferedImage.createGraphics());
			
			drawingContext.setGraphics(tsg2);
			drawingContext.setSelected(new Selection());
			
			outerSill.draw(drawingContext);
			
			// java.io.File file = new java.io.File("newimage.png");
			bufferedImage.flush();
			// javax.imageio.ImageIO.write(bufferedImage, "png", file);
			
			drawingContext.getGraphics().dispose();
			
			final long duration = System.currentTimeMillis() - startTime;
			// System.out.println("Warm up time: "+((double)duration/1000.0)+" sec(s)");
			
			return tsg2.isDrawStringInvoked();			
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			return false;
		}

		// it's not clear exactly now much this helps, but it does seem to.
	}
	
	public void setIntermediate(boolean designatedIntermediacy)
	{
		if (drawingContext!=null)
		{
			drawingContext.setIntermediate(designatedIntermediacy);
		}
	}
	
	public boolean isIntermediate()
	{
		if (drawingContext==null)
			return false; // ?
		else
			return drawingContext.isIntermediate();
	}

	/**
	 * Applies any default values for color, line thickness, and margin to a balloon.
	 *
	 * Will not change the balloon's shape or text composition.
	 */
	public static void applyStyleDefaultsTo(Balloon balloon)
	{
		BalloonEngineState.getInstance().applyStyleDefaultsTo(balloon);
	}
}
