/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.layout.edit.MergeEdit;
import com.smithandtinkers.layout.edit.OrderSectionsSpatiallyEdit;
import com.smithandtinkers.layout.edit.PartEdit;
import com.smithandtinkers.layout.edit.PasteEdit;
import com.smithandtinkers.layout.edit.Scale2dEdit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.*;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.geom.Stemmed;
import com.smithandtinkers.mvc.ActionChangedListener;
import com.smithandtinkers.mvc.NamableAbstractAction;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SelfContainedTreeSelectionEvent;
import com.smithandtinkers.mvc.SelfContainedTreeSelectionListener;
import com.smithandtinkers.io.CachingArchiveContext;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.svg.BudgetImageIO;
import com.smithandtinkers.svg.SvgFlavor;
import com.smithandtinkers.util.*;
import com.smithandtinkers.gui.*;


public class ArtworkFrame extends ApplicationFrame implements CursorModeManager, ChangeListener, UndoableEditListener
{
	private Artwork artwork;
	
	private JScrollPane scroller;
	private ArtworkPane canvas;

	private CursorMode currentCursorMode = SELECT_STUFF_CURSOR_MODE;
	
	private AbstractButton lastSelection; // TODO redundant to cursorMode?
	private JToolBar toolbar;
	
	private Action saveAsAction;
	private Action saveAction;
	private Action addStemAction;
	private Action mergeAction;
	private Action partAction;
	private Action bringForwardAction;
	private Action sendBackwardAction;
	private Action bringToFrontAction;
	private Action sendToBackAction;
	private Action clearAction;
	private Action selectNoneAction;
	private Action scaleAction;
	private Action orderSectionsSpatiallyAction;
	private static Action openAction;
	private static Action importAction;
	private static Action selectStuffAction;
	
	public static final ResourceBundle MENU_TEXT = ResourceBundle.getBundle("resources/text/menu");
	private final ButtonGroup group = new ButtonGroup();
	
	private BalloonistPanel balloonistPanel = new BalloonistPanel();
	
	private JMenu formatMenu;
	
	private UndoManager undoManager = new UndoManager();

	private NamableAbstractAction undoAction;
	private NamableAbstractAction redoAction;
	private Action revertAction;
	private Action closeAction;
	
	private WeakReference helpFrame;
	
	private JSlider zoomer;
	private ExportAsImageAction exportAsPngZoomedAction;
	private ExportAsImageAction exportAsGifZoomedAction;
	
	public static final CursorMode SELECT_STUFF_CURSOR_MODE = new CursorMode("Select Stuff", false);
	public static final CursorMode CREATE_WORD_BALLOONS_CURSOR_MODE = new CursorMode("Create Word Balloons", true, Balloon.class);
	public static final CursorMode CREATE_CURVED_WORD_BALLOONS_CURSOR_MODE = new CursorMode("Create Word Balloons", true, Balloon.class);
	public static final CursorMode CREATE_STEMLESS_WORD_BALLOONS_CURSOR_MODE = new CursorMode("Create Word Balloons", true, Balloon.class);
	public static final CursorMode CREATE_PANELS_CURSOR_MODE = new CursorMode("Create Panels", true, Sill.class);
	public static final CursorMode CREATE_THOUGHT_BALLOONS_CURSOR_MODE = new CursorMode("Create Thought Balloons", true, Balloon.class);
	public static final CursorMode CREATE_STEMLESS_THOUGHT_BALLOONS_CURSOR_MODE = new CursorMode("Create Stemless Thought Balloons", true, Balloon.class);
	public static final CursorMode CREATE_NARRATIONS_CURSOR_MODE = new CursorMode("Create Narrations", true, Balloon.class);
	public static final CursorMode CREATE_LOLLIPOP_BALOOONS_CURSOR_MODE = new CursorMode("Create Lollipop Word Balloons", true, Balloon.class);
	public static final CursorMode CREATE_STARBURST_CURSOR_MODE = new CursorMode("Create Starbursts", true, Balloon.class);
	public static final CursorMode CREATE_GOUACHE_CURSOR_MODE = new CursorMode("Create Gouache", true, Balloon.class);
	
	public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	
	public static final String WINDOW_MODIFIED = "windowModified";

	
	public ArtworkFrame(BalloonistApplication designatedBalloonistApplication)
	{
		super(BalloonEngineState.APP_PROPS.getString("name"), designatedBalloonistApplication);
		initialize();
	}
	
	protected void initialize()
	{
		final Cursor originalCursor = getCursor();
		
		initializeFrame();
		initializeCanvas();
		initializePropsPanel();
		initializeSizing();
		
		setCursor(originalCursor);
		
		// if (designatedBalloonistApplication!=null)
		// 	designatedBalloonistApplication.register(this);
		
		toFront();
	}
	
	private void initializeFrame()
	{
		setCursor(WAIT_CURSOR);
		
		BorderLayout borderLayout = new BorderLayout();
		getContentPane().setLayout(borderLayout);
		
		// layoutPanel.setLayout(new WrappingFlowLayout(FlowLayout.LEFT, 5, 5));
		
		toolbar = new JToolBar(JToolBar.VERTICAL);
		
		ImageIcon bugIcon = ResourceFriend.retrieveImageIcon("resources/icons/bug.png");
		ImageIcon wordIcon = ResourceFriend.retrieveImageIcon("resources/icons/word.png");
		ImageIcon curvedWordIcon = ResourceFriend.retrieveImageIcon("resources/icons/word-curved.png");
		ImageIcon wordStemlessIcon = ResourceFriend.retrieveImageIcon("resources/icons/word-stemless.png");
		ImageIcon stemIcon = ResourceFriend.retrieveImageIcon("resources/icons/panel.png");
		ImageIcon thoughtIcon = ResourceFriend.retrieveImageIcon("resources/icons/thought.png");
		ImageIcon thoughtStemlessIcon = ResourceFriend.retrieveImageIcon("resources/icons/thought-stemless.png");
		ImageIcon narrationIcon = ResourceFriend.retrieveImageIcon("resources/icons/narration-free.png");
		ImageIcon lollipopIcon = ResourceFriend.retrieveImageIcon("resources/icons/lollipop.png");
		ImageIcon startburstIcon = ResourceFriend.retrieveImageIcon("resources/icons/starburst.png");
		ImageIcon gouacheIcon = ResourceFriend.retrieveImageIcon("resources/icons/gouache.png");
		
		selectStuffAction = new CursorModeAction("Select Stuff", bugIcon, this, SELECT_STUFF_CURSOR_MODE);
		JRadioButton selectStuffButton = new BackgroundRespectingRadioButton(bugIcon);
		selectStuffButton.setAction(selectStuffAction);
		selectStuffButton.setText(null);
		selectStuffButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("selectionArrowLabel"));
		toolbar.add(selectStuffButton);
		
		Action createWordBalloonsModeAction = new CursorModeAction("Create Word Balloons with Straight Stems", wordIcon, this, CREATE_WORD_BALLOONS_CURSOR_MODE);
		JRadioButton createWordBalloonsModeButton = new BackgroundRespectingRadioButton(wordIcon);
		createWordBalloonsModeButton.setAction(createWordBalloonsModeAction);
		createWordBalloonsModeButton.setText(null);
		createWordBalloonsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("wordBalloonLabel"));
		toolbar.add(createWordBalloonsModeButton);
		
		Action createCurvedStemWordBalloonsModeAction = new CursorModeAction("Create Word Balloons with Curved Stems", curvedWordIcon, this, CREATE_CURVED_WORD_BALLOONS_CURSOR_MODE);
		JRadioButton createCurvedWordBalloonsModeButton = new BackgroundRespectingRadioButton(curvedWordIcon);
		createCurvedWordBalloonsModeButton.setAction(createCurvedStemWordBalloonsModeAction);
		createCurvedWordBalloonsModeButton.setText(null);
		createCurvedWordBalloonsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("curvedWordBalloonLabel"));
		toolbar.add(createCurvedWordBalloonsModeButton);
		
		Action createStemlessWordBalloonsModeAction = new CursorModeAction("Create Stemless Word Balloons", wordStemlessIcon, this, CREATE_STEMLESS_WORD_BALLOONS_CURSOR_MODE);
		JRadioButton createStemlessWordBalloonsModeButton = new BackgroundRespectingRadioButton(wordStemlessIcon);
		createStemlessWordBalloonsModeButton.setAction(createStemlessWordBalloonsModeAction);
		createStemlessWordBalloonsModeButton.setText(null);
		createStemlessWordBalloonsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("stemlessWordBalloonLabel"));
		toolbar.add(createStemlessWordBalloonsModeButton);
		
		Action createPanelsModeAction = new CursorModeAction("Create Panels", wordStemlessIcon, this, CREATE_PANELS_CURSOR_MODE);
		JRadioButton createPanelsModeButton = new BackgroundRespectingRadioButton(stemIcon);
		createPanelsModeButton.setAction(createPanelsModeAction);
		createPanelsModeButton.setText(null);
		createPanelsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("panelLabel"));
		toolbar.add(createPanelsModeButton);
				
		Action createThoughtBalloonsModeAction = new CursorModeAction("Create Thought Balloons", thoughtIcon, this, CREATE_THOUGHT_BALLOONS_CURSOR_MODE);
		JRadioButton createThoughtBalloonButton = new BackgroundRespectingRadioButton(thoughtIcon);
		createThoughtBalloonButton.setAction(createThoughtBalloonsModeAction);
		createThoughtBalloonButton.setText(null);
		createThoughtBalloonButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("thoughtBalloonLabel"));
		toolbar.add(createThoughtBalloonButton);
		
		Action createStemlessThoughtBalloonsModeAction = new CursorModeAction("Create Stemless Thought Balloons", thoughtStemlessIcon, this, CREATE_STEMLESS_THOUGHT_BALLOONS_CURSOR_MODE);
		JRadioButton createStemlessThoughtBalloonsButton = new BackgroundRespectingRadioButton(thoughtStemlessIcon);
		createStemlessThoughtBalloonsButton.setAction(createStemlessThoughtBalloonsModeAction);
		createStemlessThoughtBalloonsButton.setText(null);
		createStemlessThoughtBalloonsButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("stemlessThoughtBalloonLabel"));
		toolbar.add(createStemlessThoughtBalloonsButton);
		
		Action createFreeNarrationsModeAction = new CursorModeAction("Create Narrations", narrationIcon, this, CREATE_NARRATIONS_CURSOR_MODE);
		JRadioButton createNarrationsModeButton = new BackgroundRespectingRadioButton(narrationIcon);
		createNarrationsModeButton.setAction(createFreeNarrationsModeAction);
		createNarrationsModeButton.setText(null);
		createNarrationsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("narrationLabel"));
		toolbar.add(createNarrationsModeButton);
		
		Action createLollipopsModeAction = new CursorModeAction("Create Lollipop Word Balloons", lollipopIcon, this, CREATE_LOLLIPOP_BALOOONS_CURSOR_MODE);
		JRadioButton createLollipopsModeButton = new BackgroundRespectingRadioButton(lollipopIcon);
		createLollipopsModeButton.setAction(createLollipopsModeAction);
		createLollipopsModeButton.setText(null);
		createLollipopsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("lollipopLabel"));
		toolbar.add(createLollipopsModeButton);
		
		Action createStarburstsModeAction = new CursorModeAction("Create Starbursts", startburstIcon, this, CREATE_STARBURST_CURSOR_MODE);
		JRadioButton createStarburstsModeButton = new BackgroundRespectingRadioButton(startburstIcon);
		createStarburstsModeButton.setAction(createStarburstsModeAction);
		createStarburstsModeButton.setText(null);
		createStarburstsModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("starburstLabel"));
		toolbar.add(createStarburstsModeButton);
		
		Action createGouacheModeAction = new CursorModeAction("Create Gouache", gouacheIcon, this, CREATE_GOUACHE_CURSOR_MODE);
		JRadioButton createGouacheModeButton = new BackgroundRespectingRadioButton(gouacheIcon);
		createGouacheModeButton.setAction(createGouacheModeAction);
		createGouacheModeButton.setText(null);
		createGouacheModeButton.setToolTipText(AbstractNamed.NAMES_TEXT.getString("gouacheLabel"));
		toolbar.add(createGouacheModeButton);
		
		if (PlatformFriend.RUNNING_ON_WINDOWS)
		{
			final Insets ZERO_INSETS = new Insets(0,0,0,0);
			
			for (int index=0; index<toolbar.getComponentCount(); index++)
			{
				if (toolbar.getComponent(index) instanceof JRadioButton)
				{
					JRadioButton button = (JRadioButton) toolbar.getComponent(index);
					button.setMargin(ZERO_INSETS);
				}
			}
		}
		
		toolbar.add(new JToolBar.Separator());
		toolbar.add(new JSeparator(JSeparator.HORIZONTAL));
		
		zoomer = new SilenceableSlider(25, 200, true, 100);
		
		zoomer.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent mouseEvent)
			{
			}

			public void mousePressed(MouseEvent mouseEvent)
			{
				canvas.setIntermediate(true);
			}

			public void mouseReleased(MouseEvent mouseEvent)
			{
				canvas.setIntermediate(false);
			}

			public void mouseEntered(MouseEvent mouseEvent)
			{
			}

			public void mouseExited(MouseEvent mouseEvent)
			{
			}
			
		});
		
		zoomer.addChangeListener(new ChangeListener() 
			{
				public void stateChanged(ChangeEvent event)
			    {
					// System.out.println("new zoom factor: "+zoomer.getValue());
					final double newScaleFactor = (double)zoomer.getValue() / 100.0;
					
					if (newScaleFactor<=2.0 && newScaleFactor >= 0.25) // upper limit to avoid exhausting available memory
					{
						canvas.setScaleFactor(newScaleFactor);
						exportAsPngZoomedAction.setScaleFactor(newScaleFactor);
						exportAsGifZoomedAction.setScaleFactor(newScaleFactor);
						scroller.setViewport(scroller.getViewport());
							// ^^ jogs scrollpane scroll bars
						canvas.invalidate();
						// canvas.repaint();
					}
			    }			
			}
		);
		
		LabeledSlider labeledZoomer = new LabeledSlider(new JLabel("+"), new JLabel("-"), zoomer, true, true);
		labeledZoomer.setReadoutPrefix("%");
		JPanel labeledZoomerSweater = new JPanel();
		labeledZoomerSweater.add(labeledZoomer);
		toolbar.add(labeledZoomerSweater);
		
		for (int index=0; index<toolbar.getComponentCount(); index++)
		{
			if (toolbar.getComponent(index) instanceof JComponent)
			{
				final JComponent component = (JComponent) toolbar.getComponent(index);
				component.setAlignmentX(CENTER_ALIGNMENT);
				// component.setAlignmentY(TOP_ALIGNMENT);
			}
		}
		
		group.add(selectStuffButton);
		group.add(createWordBalloonsModeButton);
		group.add(createStemlessWordBalloonsModeButton);
		group.add(createPanelsModeButton);
		group.add(createThoughtBalloonButton);
		group.add(createNarrationsModeButton);
		group.add(createLollipopsModeButton);
		group.add(createStarburstsModeButton);
		group.add(createGouacheModeButton);
		
		getContentPane().add(toolbar, BorderLayout.WEST);
		
		JMenu fileMenu = generateFileMenu(this);
		JMenu editMenu = generateEditMenu(this);
		JMenu arrangeMenu = generateArrangeMenu(this);
		JMenu viewMenu = generateViewMenu(this);
		JMenu itemMenu = generateItemMenu(this);
		formatMenu = generateEmptyFormatMenu();
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(arrangeMenu);
		menuBar.add(viewMenu);
		menuBar.add(itemMenu);
		menuBar.add(formatMenu);
		
		menuBar.add(generateHelpMenu());
		
		setJMenuBar(menuBar);
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(true);
		
		if (PlatformFriend.titleBarsShouldContainIconImages())
			setIconImage(ResourceFriend.retrieveImageIcon("resources/icons/icon-16x16.png").getImage());
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				ArtworkFrame.this.close();
			}

			public void windowClosed(WindowEvent e)
			{
				// System.out.println("windowClosed"); // what does this accomplish?
			}
		});
	}
	
	private void initializePropsPanel()
	{
		// System.out.println("intializePropsPanel: setting selection");
		balloonistPanel.setChangeListener(this);
		// balloonistPanel.getArtworkTreePanel().getTree().addTreeSelectionListener(balloonistPanel.getDrawableDialog());
		balloonistPanel.getArtworkTreePanel().getTree().addTreeSelectionListener(new TreeSelectionListener()
			{
				public void valueChanged(TreeSelectionEvent e)
				{
					JTree tree = (JTree) e.getSource();
					
					TreePath [] tpaths = tree.getSelectionPaths();
					canvas.getSelection().resetSelection(); // TODO may not want to reset if the shift key is down.
															// The advantage of reseting is that currently there is
															// no way to select multiple stems or shapes. this greatly
															// reduces the number of command states for the app.
					
					if (tpaths!=null)
					{
						for (int pathLoop=0; pathLoop<=tpaths.length-1; pathLoop++)
						{					
							// Selectable selectable = (Selectable) tree.getLastSelectedPathComponent();
							Selectable selectable = (Selectable) tpaths[pathLoop].getLastPathComponent(); 
							
							if (selectable!=null)
							{
								canvas.getSelection().registerSelection(selectable);
							}
						}
					}
					
					canvas.getSelection().setDirty(false, ArtworkFrame.this);
					
					invalidate();
					repaint();
				}
			}
		);
		
		balloonistPanel.getDrawableDialog().setListenerToNotifyAboutUndoableEdits(this);
		balloonistPanel.getArtworkTreePanel().getTree().addUndoableEditListener(this);
		
		populateFormatMenu(formatMenu, balloonistPanel.getDrawableDialog().getBalloonPropsPanel().getJText());
				
		getContentPane().add(balloonistPanel, BorderLayout.EAST);
	}
	
	private JMenu generateHelpMenu()
	{
		JMenu helpMenu = new JMenu(MENU_TEXT.getString("helpLabel"));
		
		JMenuItem item = null;
		
		Action helpContentAction = new AbstractAction(MENU_TEXT.getString("helpContentLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					HypertextFrame theHelpFrame = getHelpFrame();
					
					if (theHelpFrame==null)
					{
						HypertextPane helpViewer = new HypertextPane();
						URL manualURL = ClassLoader.getSystemResource("manual/index.html");
						helpViewer.setPage(manualURL);
						theHelpFrame = new HypertextFrame(MENU_TEXT.getString("helpContentLabel"), helpViewer);
						setHelpFrame(theHelpFrame);
						theHelpFrame.setSize(880,710); // TODO clip this to screen dims
						theHelpFrame.setLocation(25, 30);
						theHelpFrame.setVisible(true);
					}
					else
					{
						if (!theHelpFrame.isVisible())
							theHelpFrame.setVisible(true);
						
						theHelpFrame.toFront();
					}
				}
				catch (IOException exception)
				{
					throw new BugException(exception);
				}
			}
		};
		item = new JMenuItem(helpContentAction);
		helpMenu.add(item);
		
		if (!PlatformFriend.RUNNING_ON_MAC)
		{
			helpMenu.addSeparator();
			Action aboutAction = new DocentAction(this);
			helpMenu.add(aboutAction);
		}
		
		return helpMenu;
	}

	public void applyDefaults()
	{
		// System.out.println("applying defaults...");
		
		Cursor originalCursor = getCursor();
		
		try
		{
			setCursor(WAIT_CURSOR);
			
			setArtwork(new Artwork());
			artwork.getLayout().setAspectRatio(1.35);
			artwork.setChangeListener(ArtworkFrame.this);
	
			Rectangle rect = new Rectangle();
	
			final double widthInInches = 11.0;
			final double heightInInches = 8.5;
			
			final double marginInInches = 0.5;
			
			artwork.getLayout().setHorizontalPageMarginInPoints(
					DrawableDialog.INCHES.getAmount()*marginInInches);
			artwork.getLayout().setVerticalPageMarginInPoints(
					DrawableDialog.INCHES.getAmount()*marginInInches);
			
			rect.setRect(0.0, 0.0, 
					(DrawableDialog.INCHES.getAmount() * (widthInInches - 2.0*marginInInches)), 
					(DrawableDialog.INCHES.getAmount() * (heightInInches - 2.0*marginInInches)));
				// letter sized paper
	
			artwork.getLayout().setBounds(rect);
			artwork.getLayout().setApertureQuantity(4);
	
			artwork.startOver();
			sizeCanvasAppropriately();
			balloonistPanel.getDrawableDialog().stateChanged(null);
		}
		finally
		{
			setCursor(originalCursor);
		}
	}

	private void initializeCanvas()
	{
		canvas = new ArtworkPane(this);
		scroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		canvas.setScroller(scroller);
		scroller.setViewportView(canvas);

		getScroller().setViewportView(canvas);
		canvas.setListenerToNotifyAboutUndoableEdits(this);
		
		balloonistPanel.setSelection(canvas.getSelection());
		canvas.getSelection().getSelfContainedTreeSelectionListenerSupport().addSelfContainedTreeSelectionListener(balloonistPanel.getDrawableDialog());
		canvas.addChangeListener(balloonistPanel.getDrawableDialog());
		canvas.getSelection().getSelfContainedTreeSelectionListenerSupport().addSelfContainedTreeSelectionListener(new SelfContainedTreeSelectionListener() 
			{
				public void valueChanged(SelfContainedTreeSelectionEvent e)
				{
					// selections have been made or unmade in the drawing pane. we need to tell the JTree so that it can hilite accordingly.
					
					final RearrangeableTree jtree = balloonistPanel.getArtworkTreePanel().getTree();
					jtree.setSilent(true);
					jtree.setSelectionPaths(e.getSelection());
					jtree.setSilent(false);
					
					maintainMenuItemEnablednesses();
				}
			}
		);
		canvas.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				if (event instanceof NameChangeEvent)
				{
					balloonistPanel.getArtworkTreePanel().getTree().invalidate();
					balloonistPanel.getArtworkTreePanel().getTree().repaint();
				}
				else if (event.getSource() instanceof GraphicResizeable)
				{
					balloonistPanel.getDrawableDialog().stateChanged(event);
				}
			} // TODO there has got to be a better way to accomplish this.
		});
		
		getContentPane().add(scroller, BorderLayout.CENTER);
		
		maintainMenuItemEnablednesses();
	}

	protected JScrollPane getScroller()
	{
		return scroller;
	}

	public ArtworkPane getCanvas()
	{
		return canvas;
	}

	public JMenu generateFileMenu(final ArtworkFrame hub)
	{
		JMenu fileMenu = new JMenu(MENU_TEXT.getString("fileLabel"));
		JMenuItem item = null;

		Action newAction = new AbstractAction(MENU_TEXT.getString("newLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{				
				final ArtworkFrame artworkFrame = new ArtworkFrame(getBalloonistApplication());
				artworkFrame.applyDefaults();
			}
		};
		item = fileMenu.add(newAction);
		item.setMnemonic('N');
		item.setAccelerator(KeyStroke.getKeyStroke('N', PlatformFriend.getMenuShortcutKey()));
		
		final JMenu newMenu = new JMenu(MENU_TEXT.getString("newLabel")+"...");
		
		final Action newFromLinkedImage = new NewFromExistingImageAction(this, true);
		newMenu.add(newFromLinkedImage);
		final Action newFromEmbeddedImage = new NewFromExistingImageAction(this, false);
		newMenu.add(newFromEmbeddedImage);
		
		JMenu newWithStationeryMenu = generateStationerySubmenu();
		newMenu.add(newWithStationeryMenu);
		
		fileMenu.add(newMenu);
		
		openAction = new OpenArtworkAction(hub, getBalloonistApplication());
		item = fileMenu.add(openAction);
		item.setMnemonic('O');
		item.setAccelerator(KeyStroke.getKeyStroke('O', PlatformFriend.getMenuShortcutKey()));
		
		importAction = new ImportComicsMarkupAction(hub, getBalloonistApplication());
		item = fileMenu.add(importAction);
		
		fileMenu.addSeparator();
		
		closeAction = new AbstractAction(MENU_TEXT.getString("closeLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ArtworkFrame.this.close();
			}
		};
		item = fileMenu.add(closeAction);
		item.setAccelerator(KeyStroke.getKeyStroke('W', PlatformFriend.getMenuShortcutKey()));
		
		saveAction = new AbstractAction(MENU_TEXT.getString("saveLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if (artwork.getFile()!=null)
				{
					try
					{
						artwork.save();
						saveAction.setEnabled(false);
						revertAction.setEnabled(false);
						undoManager.discardAllEdits();
						maintainUndoStates();
					}
					catch (Exception exception)
					{
						 exception.printStackTrace();
						// TODO display this in a message dialog.
					}
				}
				else
					saveAsAction.actionPerformed(e);
			}	
		};
		item = fileMenu.add(saveAction);
		item.setAccelerator(KeyStroke.getKeyStroke('S', PlatformFriend.getMenuShortcutKey()));
		item.setMnemonic('S');
		saveAction.addPropertyChangeListener(new ActionChangedListener(item));
		saveAction.setEnabled(false);
		
		saveAsAction = new FileAction(MENU_TEXT.getString("saveAsLabel"), this, true, Artwork.BALLOONIST_FILENAME_EXTENSION)
		{
			public void processFile(File theFile) throws Exception
			{
				artwork.save(theFile);
				saveAction.setEnabled(false);
				revertAction.setEnabled(false);
				undoManager.discardAllEdits();
				ArtworkFrame.this.setTitle(determineTitlebar());
				maintainUndoStates();
			}
		};
		item = fileMenu.add(saveAsAction);
		
		JMenu exportSubMenu = new JMenu(MENU_TEXT.getString("exportLabel"));
		final Action exportAsPdfAction = new ExportAsPdfAction(hub, getApplication());
		exportSubMenu.add(exportAsPdfAction);
		
		exportSubMenu.addSeparator();
		
		Action exportAsSvg10Action = new ExportAsSvgAction(MENU_TEXT.getString("exportAsSvg10Label"), hub, true, this, SvgFlavor.SVG_1_0);
		exportSubMenu.add(exportAsSvg10Action);
		
		// Action exportAsSvg11AdobeAction = new ExportAsSvgAction(menuText.getString("exportAsSvg11AdobeLabel"), hub, true, this, SvgFlavor.ADOBE_SVG_VIEWER_60P1_SVG_1_1);
		// exportSubMenu.add(exportAsSvg11AdobeAction);
		
		// Action exportAsSvg11BatikAction = new ExportAsSvgAction(menuText.getString("exportAsSvg11BatikLabel"), hub, true, this, SvgFlavor.APACHE_BATIK_1_5_SVG);
		// exportSubMenu.add(exportAsSvg11BatikAction);
			// ^^ as of 1.5, Batik does not yet support non-rectangular flow regions
		
		Action exportAsSvg12Action = new ExportAsSvgAction(MENU_TEXT.getString("exportAsSvg12Label"), hub, true, this, SvgFlavor.SVG_1_2);
		exportSubMenu.add(exportAsSvg12Action);
		
		exportSubMenu.addSeparator();
		exportSubMenu.add(new ExportAsImageAction(MENU_TEXT.getString("exportAsPng"), hub, this, BudgetImageIO.FORMAT_PORTABLE_NETWORK_GRAPHICS));
		
		if (PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER)
		{
			exportSubMenu.add(new ExportAsImageAction(MENU_TEXT.getString("exportAsGif"), hub, this, BudgetImageIO.FORMAT_GRAPHICS_INTERCHANGE_FORMAT));
		}
		
		exportAsPngZoomedAction = new ExportAsImageAction(MENU_TEXT.getString("exportAsPngZoomed"), hub, this, BudgetImageIO.FORMAT_PORTABLE_NETWORK_GRAPHICS);
		
		exportSubMenu.add(exportAsPngZoomedAction);
		
		exportAsGifZoomedAction = new ExportAsImageAction(MENU_TEXT.getString("exportAsGifZoomed"), hub, this, BudgetImageIO.FORMAT_GRAPHICS_INTERCHANGE_FORMAT);
		
		if (PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER)
		{
			exportSubMenu.add(exportAsGifZoomedAction);
		}
		
		fileMenu.add(exportSubMenu);
		
		revertAction = new PossibleAction(MENU_TEXT.getString("revertLabel"), hub)
		{
			public void process(int modifiers) throws Exception
			{
				if (artwork.getFile()==null)
					return;
				
				if (needsSaving())
				{
					StringBuffer questionText = new StringBuffer();

					questionText.append(PossibleAction.DIALOG_TEXT.getString("abandonChangesQuestion"));
					questionText.append(" ");

					if (artwork.getFile()==null)
						questionText.append(PossibleAction.DIALOG_TEXT.getString("untitledDocumentPhrase"));
					else
						questionText.append(artwork.getFile().getName());

					questionText.append("?");

					int userChoice = JOptionPane.showConfirmDialog(ArtworkFrame.this,
							questionText, 
							PossibleAction.DIALOG_TEXT.getString("abandonChangesTitle"), 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					switch (userChoice)
					{
						case JOptionPane.NO_OPTION:
							return;
					}
				}
				
				open(artwork.getFile());
			}
		};
		revertAction.setEnabled(false);
		item = fileMenu.add(revertAction);
		revertAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		fileMenu.addSeparator();
		
		JMenu placeLinkedSubmenu = new JMenu(MENU_TEXT.getString("placeLinkedLabel"));
		PossibleAction placeLinkedImageFromFileAction = new PlaceFileAction(MENU_TEXT.getString("fromFileLabel"), this, true);
		placeLinkedImageFromFileAction.setOptionalPrefix(placeLinkedSubmenu.getText());
		item = placeLinkedSubmenu.add(placeLinkedImageFromFileAction);
		PossibleAction placeLinkedImageFromUrlAction = new PlaceUrlAction(MENU_TEXT.getString("fromUrlLabel"), this, true);
		placeLinkedImageFromUrlAction.setOptionalPrefix(placeLinkedSubmenu.getText());
		item = placeLinkedSubmenu.add(placeLinkedImageFromUrlAction);
		
		fileMenu.add(placeLinkedSubmenu);
		
		JMenu placeEmbeddedSubmenu = new JMenu(MENU_TEXT.getString("placeEmbeddedLabel"));
		PossibleAction placeEmbeddedImageFromFileAction = new PlaceFileAction(MENU_TEXT.getString("fromFileLabel"), this, false);
		placeEmbeddedImageFromFileAction.setOptionalPrefix(placeEmbeddedSubmenu.getText());
		item = placeEmbeddedSubmenu.add(placeEmbeddedImageFromFileAction);
		PossibleAction placeEmbeddedImageFromUrlAction = new PlaceUrlAction(MENU_TEXT.getString("fromUrlLabel"), this, false);
		placeEmbeddedImageFromUrlAction.setOptionalPrefix(placeEmbeddedSubmenu.getText());
		item = placeEmbeddedSubmenu.add(placeEmbeddedImageFromUrlAction);
		
		fileMenu.add(placeEmbeddedSubmenu);
		
		fileMenu.addSeparator();
		
		Action printAction = new PrintArtworkAction(MENU_TEXT.getString("printLabel")+"...", this);
		item = fileMenu.add(printAction);
		
		/*
		fileMenu.add(new AbstractAction("Inspect Tree") {

			public void actionPerformed(ActionEvent e)
			{
				TreeModelTest.printTreeModel(getBalloonistPanel().getArtworkTreePanel().getTree().getModel());
			}} 
		);
		*/
		
		if (!PlatformFriend.RUNNING_ON_MAC)
		{
			fileMenu.addSeparator();
			
			Action quitAction = new AbstractAction(MENU_TEXT.getString("exitLabel"))
			{
				public void actionPerformed(ActionEvent event)
				{
					getApplication().quit();
				}
				
			};
			item = fileMenu.add(quitAction);
		}
				
		return fileMenu;
	}
	
	private JMenu generateStationerySubmenu()
	{
		JMenu stationeryMenu = new JMenu(MENU_TEXT.getString("newWithStationery"));
		
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			// factory.setCoalescing(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			Logger.println("parsing..");
			Document doc = builder.parse(ClassLoader.getSystemResourceAsStream("resources/stationery/layouts.xml"));
			Element element = doc.getDocumentElement();
			
			for (int loop=0; loop<=element.getChildNodes().getLength()-1; loop++)
			{
				Node node = element.getChildNodes().item(loop);
				
				if (Layout.IDENTIFIER_LAYOUT.equals(node.getNodeName()))
				{
					Element subelement = (Element) node;
					final Layout template = new Layout();
					CachingArchiveContext archiveContext = new CachingArchiveContext();
					archiveContext.setListenerToNotifyAboutUndoableEdits(this);
					template.open(doc, subelement, archiveContext);
					
					Action stationeryAction = new AbstractAction(template.getName())
					{
						public void actionPerformed(ActionEvent e)
						{
							try
							{
								Artwork newArtwork = new Artwork();
								newArtwork.setLayout((Layout)template.clone());
								newArtwork.startOver();
								
								ArtworkFrame newArtworkFrame = new ArtworkFrame(getBalloonistApplication());
								newArtworkFrame.setArtwork(newArtwork);
							}
							catch (CloneNotSupportedException exception)	
							{
								throw new BugException(exception);
							}
						}	
					};
					stationeryMenu.add(stationeryAction);
				}
			}
			
			return stationeryMenu;
		}
		catch (javax.xml.parsers.ParserConfigurationException exception)
		{
			throw new ChainedRuntimeException(exception);
		}
		catch (SAXException exception)
		{
			throw new ChainedRuntimeException(exception);
		}
		catch (IOException exception)
		{
			throw new ChainedRuntimeException(exception);
		}
	}
	
	protected JMenu generateViewMenu(final JFrame hub)
	{
		JMenu menu = new JMenu(MENU_TEXT.getString("viewLabel"));
		JMenuItem item = null;
		
		Action zoomInAction = new AbstractAction(MENU_TEXT.getString("zoomInLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				double newScaleFactor = canvas.getScaleFactor() * 2.0;
				
				newScaleFactor = validateProposedScaleFactor(newScaleFactor);
				
				if (canvas.getScaleFactor()!=newScaleFactor)
				{
					canvas.setScaleFactor(newScaleFactor);
					exportAsPngZoomedAction.setScaleFactor(newScaleFactor);
					exportAsGifZoomedAction.setScaleFactor(newScaleFactor);
					scroller.setViewport(scroller.getViewport());
						// ^^ jogs scrollpane scroll bars
					canvas.invalidate();
					refreshZoomer();
					canvas.repaint();
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		item = menu.add(zoomInAction);
		
		Action zoomOutAction = new AbstractAction(MENU_TEXT.getString("zoomOutLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				double newScaleFactor = canvas.getScaleFactor() / 2.0;
				
				newScaleFactor = validateProposedScaleFactor(newScaleFactor);
				
				if (canvas.getScaleFactor()!=newScaleFactor)
				{
					canvas.setScaleFactor(newScaleFactor);
					exportAsPngZoomedAction.setScaleFactor(newScaleFactor);
					exportAsGifZoomedAction.setScaleFactor(newScaleFactor);
					scroller.setViewport(scroller.getViewport());
						// ^^ jogs scrollpane scroll bars
					canvas.invalidate();
					refreshZoomer();
					canvas.repaint();
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		item = menu.add(zoomOutAction);
		
		menu.addSeparator();
		
		Action actualSizeAction = new AbstractAction(MENU_TEXT.getString("actualSizeLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				canvas.setScaleFactor(1.0);
				exportAsPngZoomedAction.setScaleFactor(1.0);
				exportAsGifZoomedAction.setScaleFactor(1.0);
				scroller.setViewport(scroller.getViewport());
					// ^^ jogs scrollpane scroll bars
				canvas.invalidate();
				refreshZoomer();
				canvas.repaint();
			}
		};
		item = menu.add(actualSizeAction);
		
		return menu;
	}
	
	/**
	 * @return a {@link java.util.List} of any missing font families as Strings. Will return
	 *          an empty List if all fonts encountered were available.
	 */
	public List open(File theFile) throws SAXException, IOException
	{
		Artwork newArtwork = new Artwork();
		setArtwork(newArtwork);
		List missingFontFamilies = newArtwork.open(theFile, this);

		// Logger.println("sizing canvas to " + artwork.getEnclosure());
		
		undoManager.discardAllEdits();
		
		newArtwork.setChangeListener(this);
		
		sizeCanvasAppropriately();
		
		invalidate();
		validate();
		
		return missingFontFamilies;
	}

	protected void setArtwork(Artwork designatedArtwork)
	{
		// System.out.println("setting artwork: "+designatedArtwork);
		final Artwork oldArtwork = artwork;
		
		artwork = designatedArtwork;
		balloonistPanel.setArtwork(artwork);
		// canvas.setSill(artwork.getSill());
		canvas.setArtwork(artwork);
		// sizeCanvasAppropriately(); not helpful when artwork is brand new
		
		if (artwork!=null)
			artwork.setChangeListener(this);
			
		if (oldArtwork!=null)
			oldArtwork.setChangeListener(null);
	}
	
	private void sizeCanvasAppropriately()
	{
		// getCanvas().setEnclosure(artwork.enclose());
		artwork.enclose();
		initializeSizing();
	}
	
	public JMenu generateEditMenu(final JFrame hub)
	{
		JMenu editMenu = new JMenu(MENU_TEXT.getString("editLabel"));
		JMenuItem item = null;
		
		undoAction = new NamableAbstractAction(UIManager.getString("AbstractUndoableEdit.undoText")) // TODO move this into com.smithandtinkers
		{
			public void actionPerformed(ActionEvent e)
			{
				if (undoManager.canUndo())
				{
					undoManager.undo();
					
					maintainUndoStates();
				}
			}
		};
		item = editMenu.add(undoAction);
		item.setAccelerator(KeyStroke.getKeyStroke('Z', PlatformFriend.getMenuShortcutKey()));
		undoAction.addPropertyChangeListener(new ActionChangedListener(item));
		undoAction.setEnabled(false);
		
		redoAction = new NamableAbstractAction(UIManager.getString("AbstractUndoableEdit.redoText"))
		{
			public void actionPerformed(ActionEvent e)
			{
				if (undoManager.canRedo())
				{
					undoManager.redo();
					
					maintainUndoStates();
				}
			}
		};
		item = editMenu.add(redoAction);
		item.setAccelerator(KeyStroke.getKeyStroke('Y', PlatformFriend.getMenuShortcutKey()));
		redoAction.addPropertyChangeListener(new ActionChangedListener(item));
		redoAction.setEnabled(false);
		
		editMenu.addSeparator();
		
		Action cutAction = new AbstractAction(MENU_TEXT.getString("cutLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
					if (focusOwner==null || !(focusOwner instanceof JTextComponent))
					{
						canvas.copySelection();
						canvas.deleteSelection();
					}
					else
					{
						JTextComponent jText = (JTextComponent) focusOwner;
						
						if (jText.getSelectionStart()!=jText.getSelectionEnd())
							jText.getActionMap().get(DefaultEditorKit.cutAction).actionPerformed(event);
					}
				}
				catch (DocumentException e)
				{
					throw new BugException(e);
				}
			}
		};
		item = editMenu.add(cutAction);
		item.setAccelerator(KeyStroke.getKeyStroke('X', PlatformFriend.getMenuShortcutKey()));
		
		Action copyAction = new AbstractAction(MENU_TEXT.getString("copyLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
				if (focusOwner==null || !(focusOwner instanceof JTextComponent))
				{				
					canvas.copySelection();
				}
				else
				{
					JTextComponent jText = (JTextComponent) focusOwner;
					jText.getActionMap().get(DefaultEditorKit.copyAction).actionPerformed(event);
				}
			}
		};
		item = editMenu.add(copyAction);
		item.setMnemonic('C');
		item.setAccelerator(KeyStroke.getKeyStroke('C', PlatformFriend.getMenuShortcutKey()));
		
		Action pasteAction = new AbstractAction(MENU_TEXT.getString("pasteLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
				if (focusOwner==null || !(focusOwner instanceof JTextComponent))
				{
					handlePaste(false);
				}
				else
				{
					JTextComponent jText = (JTextComponent) focusOwner;
					jText.getActionMap().get(DefaultEditorKit.pasteAction).actionPerformed(event);
				}
			}
		};
		item=editMenu.add(pasteAction);
		item.setAccelerator(KeyStroke.getKeyStroke('V', PlatformFriend.getMenuShortcutKey()));
		
		Action pasteBehindAction = new AbstractAction(MENU_TEXT.getString("pasteBehindLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
				if (focusOwner==null || !(focusOwner instanceof JTextComponent))
				{
					handlePaste(true);
				}
				else
				{
					JTextComponent jText = (JTextComponent) focusOwner;
					jText.getActionMap().get(DefaultEditorKit.pasteAction).actionPerformed(event);
				}
			}
		};
		item=editMenu.add(pasteBehindAction);
		item.setMnemonic('B');
		item.setAccelerator(KeyStroke.getKeyStroke('B', PlatformFriend.getMenuShortcutKey()));
		
		clearAction = new AbstractAction(MENU_TEXT.getString("clearLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
					if (focusOwner==null || !(focusOwner instanceof JTextComponent))
					{
						canvas.deleteSelection();
					}
					else
					{
						JTextComponent jText = (JTextComponent) focusOwner;
						
						if (jText.getSelectionStart()!=jText.getSelectionEnd())
						{
							jText.getActionMap().get(
									DefaultEditorKit.deletePrevCharAction).actionPerformed(event);
						}
					}
					
				}
				catch (DocumentException e)
				{
					throw new BugException(e);
				}
			}
		};
		item=editMenu.add(clearAction);
		clearAction.addPropertyChangeListener(new ActionChangedListener(item));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		
		editMenu.addSeparator();
		
		selectNoneAction = new AbstractAction(MENU_TEXT.getString("selectNoneLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{				
				Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					
				if (focusOwner==null || !(focusOwner instanceof JTextComponent))
				{
					canvas.selectNone();
				}
				else
				{
					JTextComponent jText = (JTextComponent) focusOwner;
						
					if (jText.getSelectionStart()!=jText.getSelectionEnd())
					{
						jText.getActionMap().get("unselect").actionPerformed(event);
					}
				}
			}
		};
		item = editMenu.add(selectNoneAction);
		selectNoneAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		if (!PlatformFriend.RUNNING_ON_MAC)
		{
			editMenu.addSeparator();
			Action prefsAction = new AbstractAction(MENU_TEXT.getString("preferencesLabel"))
			{
				public void actionPerformed(ActionEvent event)
				{				
					getBalloonistApplication().displayPreferences();
				}
			};
			
			item = editMenu.add(prefsAction);
		}
		
		return editMenu;
	}
	
	public JMenu generateArrangeMenu(final JFrame hub)
	{
		JMenu menu = new JMenu(MENU_TEXT.getString("arrangeLabel"));
		JMenuItem item = null;
		
		bringForwardAction = new javax.swing.AbstractAction(MENU_TEXT.getString("bringForwardLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				canvas.bringForward();
			}
		};
		item=menu.add(bringForwardAction);
		bringForwardAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		sendBackwardAction = new javax.swing.AbstractAction(MENU_TEXT.getString("sendBackwardLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				canvas.sendBackward();
			}
		};
		item=menu.add(sendBackwardAction);
		sendBackwardAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		menu.addSeparator();
		
		bringToFrontAction = new AbstractAction(MENU_TEXT.getString("bringToFrontLabel"))
		{
			public void actionPerformed(ActionEvent arg0)
			{
				canvas.bringToFront();
			}
		};
		item=menu.add(bringToFrontAction);
		bringToFrontAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		sendToBackAction = new AbstractAction(MENU_TEXT.getString("sendToBackLabel"))
		{
			public void actionPerformed(ActionEvent event)
			{
				canvas.sendToBack();
			}
		};
		item=menu.add(sendToBackAction);
		sendToBackAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		menu.addSeparator();
		
		mergeAction = new AbstractAction(MENU_TEXT.getString("mergeLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				MergeEdit mergeEdit = new MergeEdit();
				mergeEdit.addItemsFrom(canvas.getSelection().iterateOverSelectedItems());
				if (mergeEdit.execute())
				{
					canvas.queueEdit(mergeEdit);
					canvas.announceEdit();
				}
			}
		};
		item = menu.add(mergeAction);
		mergeAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		partAction = new AbstractAction(MENU_TEXT.getString("partLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				PartEdit partEdit = new PartEdit();
				partEdit.addItemsFrom(canvas.getSelection().iterateOverSelectedItems());
				if (partEdit.execute())
				{
					canvas.queueEdit(partEdit);
					canvas.announceEdit();
				};
			}
		};
		item = menu.add(partAction);
		partAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		menu.addSeparator();
		
		orderSectionsSpatiallyAction = new AbstractAction(MENU_TEXT.getString("orderSectionsSpatiallyLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				OrderSectionsSpatiallyEdit orderSectionsSpatiallyEdit = new OrderSectionsSpatiallyEdit();
				orderSectionsSpatiallyEdit.addItemsFrom(canvas.getSelection().iterateOverSelectedItems());
				
				if (orderSectionsSpatiallyEdit.execute())
				{
					canvas.queueEdit(orderSectionsSpatiallyEdit);
					canvas.announceEdit();
				}
			}
		};
		orderSectionsSpatiallyAction.addPropertyChangeListener(new ActionChangedListener(item));
		menu.add(orderSectionsSpatiallyAction);
		
		return menu;
	}
	
	public Artwork getArtwork()
	{
		return artwork;
	}

	public JMenu generateEmptyFormatMenu()
	{
		return new JMenu(MENU_TEXT.getString("formatLabel"));
	}
	
	public void populateFormatMenu(JMenu menu, final JTextPane tc)
	{
		JMenuItem item = null;
		
		/* final SimpleAttributeSet plainAttrSet = new SimpleAttributeSet();
		StyleConstants.setBold(plainAttrSet, false);
		StyleConstants.setItalic(plainAttrSet, false);
		StyleConstants.setUnderline(plainAttrSet, false); */
			
		Action plainAction = new AbstractAction(MENU_TEXT.getString("plainLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				// tc.setCharacterAttributes(plainAttr, false);
				AttributeSet attrSet = tc.getCharacterAttributes();
				final String fontFamily = StyleConstants.getFontFamily(attrSet);
				final int fontSize = StyleConstants.getFontSize(attrSet);
				
				SimpleAttributeSet plainAttrSet = new SimpleAttributeSet();
				StyleConstants.setFontFamily(plainAttrSet, fontFamily);
				StyleConstants.setFontSize(plainAttrSet, fontSize);
				tc.setCharacterAttributes(plainAttrSet, true);
			}	
		};
		item = menu.add(plainAction);
		menu.addSeparator();
		
		Action emboldenAction = tc.getActionMap().get("font-bold");
		item = menu.add(MENU_TEXT.getString("boldLabel"));
		item.addActionListener(emboldenAction);

		Action italicizeAction = tc.getActionMap().get("font-italic");
		item = menu.add(MENU_TEXT.getString("italicsLabel"));
		item.addActionListener(italicizeAction);

		Action underlineAction = tc.getActionMap().get("font-underline");
		item = menu.add(MENU_TEXT.getString("underlineLabel"));
		item.addActionListener(underlineAction);
	}
	
	public CursorMode currentCursorMode()
	{
		return currentCursorMode;
	}
	
	public Action getSaveAction()
	{
		return saveAction;
	}

	/**
	 * @see com.smithandtinkers.gui.CursorModeManager#getLastSelection()
	 */
	public AbstractButton getLastSelection()
	{
		return lastSelection;
	}

	/**
	 * @see com.smithandtinkers.gui.CursorModeManager#setLastSelection(javax.swing.AbstractButton)
	 */
	public void setLastSelection(AbstractButton designatedButton)
	{
		lastSelection = designatedButton;
	}

	/**
	 * @see com.smithandtinkers.gui.CursorModeManager#setCurrentCursorMode(com.smithandtinkers.gui.CursorMode)
	 */
	public void setCurrentCursorMode(CursorMode designatedCursorMode)
	{
		if (designatedCursorMode==null)
			throw new IllegalArgumentException("cursor mode can't be null");
		
		currentCursorMode = designatedCursorMode;
	}

	public void manageCursorMode()
	{
		if (lastSelection==null || currentCursorMode==SELECT_STUFF_CURSOR_MODE)
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		else
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() instanceof Layout || e.getSource() instanceof LayoutPropsPanel
				|| e.getSource() instanceof Artwork)
		{
			getBalloonistPanel().getDrawableDialog().stateChanged(e);
			// TODO maybe we should alwyas pass this event
		}
		
		if (e.getSource() instanceof Artwork)
		{
			setTitle(determineTitlebar());
		}
		
		invalidate();
		repaint();
	}
	
	public String determineTitlebar()
	{
		StringBuffer titlebar = new StringBuffer();
		
		if (!PlatformFriend.RUNNING_ON_MAC)
			titlebar.append("Balloonist: ");
		
		if (artwork.getFile()!=null)
			titlebar.append(artwork.getFile().getName());
		else
			titlebar.append(AbstractNamed.NAMES_TEXT.getString("untitledLabel"));
		
		return titlebar.toString();
	}
	
	public BalloonistPanel getBalloonistPanel()
	{
		return balloonistPanel;
	}

	public Sill targetSill()
	{
		Sill targetSill = null;
		
		for (int loop=0; targetSill==null && loop<=canvas.getSelection().getSelectedItemsCount()-1; loop++)
		{
			if (canvas.getSelection().getSelectedItem(loop) instanceof Sill)
			{
				targetSill = (Sill) canvas.getSelection().getSelectedItem(loop);
			}
		}
		
		if (targetSill==null && canvas.getSelection().getSelectedItemsCount()>0)
		{
			Kid kid = (Kid) canvas.getSelection().getSelectedItem(0);
			targetSill = (Sill)kid.findForebear(Sill.class);
		}
		
		if (targetSill==null)
			targetSill = artwork.getSill();
		return targetSill;
	}

	public void undoableEditHappened(UndoableEditEvent undoableEditEvent)
	{
		undoManager.addEdit(undoableEditEvent.getEdit());
		undoAction.setName(undoableEditEvent.getEdit().getUndoPresentationName());
		maintainUndoStates();
	}

	private void maintainUndoStates()
	{
		undoAction.setEnabled(undoManager.canUndo());
		saveAction.setEnabled(undoManager.canUndo());
		revertAction.setEnabled(artwork.getFile()!=null && undoManager.canUndo());
		redoAction.setEnabled(undoManager.canRedo());
		undoAction.setName(undoManager.getUndoPresentationName());
		redoAction.setName(undoManager.getRedoPresentationName());
		
		if (PlatformFriend.RUNNING_ON_MAC)
		{
			getRootPane().putClientProperty(WINDOW_MODIFIED, Boolean.valueOf(undoManager.canUndo()));
		}
		
		canvas.invalidate();
		canvas.repaint();
	}
	
	/**
	 * If the frame contains loaded artwork or new artwork with edits, return true.
	 * Otherwise, returns false and the frame can be re-appropriated for other artwork.
	 */
	public boolean isSignificant()
	{
		if (artwork==null)
			return false;
		
		// System.out.println("undoManager.isSignificant(): "+undoManager.isSignificant());
		// System.out.println("undoManager.canUndo(): "+undoManager.canUndo());
		
		if (undoManager.isSignificant() && undoManager.canUndo())
			return true;
		
		// System.out.println("artwork.getFile(): "+artwork.getFile());
		
		if (artwork.getFile()!=null)
			return true;
		
		return false;
	}
	
	public boolean needsSaving()
	{
		if (artwork==null)
			return false;
		
		// System.out.println("undoManager.isSignificant(): "+undoManager.isSignificant());
		// System.out.println("undoManager.canUndo(): "+undoManager.canUndo());
		
		if (undoManager.isSignificant() && undoManager.canUndo())
			return true;
		
		return false;
	}
	
	/**
	 * @return true, if the request to close if successful
	 *         false, if the user is prompted and elects to cancel the close request
	 */
	public boolean close()
	{
		if (needsSaving())
		{
			StringBuffer questionText = new StringBuffer();
			
			// TODO if locale is Spanish, append one of those upside down question marks
			
			questionText.append(PossibleAction.DIALOG_TEXT.getString("saveChangesQuestion"));
			questionText.append(" ");
			
			if (artwork.getFile()==null)
				questionText.append(PossibleAction.DIALOG_TEXT.getString("untitledDocumentPhrase"));
			else
				questionText.append(artwork.getFile().getName());
			
			questionText.append("?");
			
			int userChoice = JOptionPane.showConfirmDialog(ArtworkFrame.this,
					questionText, 
					PossibleAction.DIALOG_TEXT.getString("saveChangesTitle"), 
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			switch (userChoice)
			{
				case JOptionPane.YES_OPTION:
					saveAction.actionPerformed(new ActionEvent(ArtworkFrame.this, 0, "Save"));
					
					if (artwork.getFile()==null)
						return false;
					
					break;
				
				case JOptionPane.NO_OPTION:					
					break;
					
				case JOptionPane.CANCEL_OPTION:
					return false;
			}
		}
		
		dispose();
		
		undoManager.discardAllEdits();
		canvas.getSelection().resetSelection();
		canvas.getSelection().setDirty(false, this);
		balloonistPanel.getDrawableDialog().setListenerToNotifyAboutUndoableEdits(null);
		balloonistPanel.getArtworkTreePanel().getTree().removeUndoableEditListener(this);
		balloonistPanel.getArtworkTreePanel().getTree().setModel(null);
		balloonistPanel.getArtworkTreePanel().setArtwork(null);
		artwork.setChangeListener(null);
		canvas.setListenerToNotifyAboutUndoableEdits(null);
		balloonistPanel.getDrawableDialog().getBalloonPropsPanel().getJText().setDocument(new javax.swing.text.DefaultStyledDocument());
		
		if (artwork.getSill().getChangeSupport()!=null)
		{
			artwork.getSill().getChangeSupport().clear();
			artwork.getSill().setChangeSupport(null);
		}
		
		if (artwork.getSill().getTreeModelSupport()!=null)
		{
			artwork.getSill().getTreeModelSupport().clear();
			artwork.getSill().setTreeModelSupport(null);
		}
		
		canvas.getSelection().getSelfContainedTreeSelectionListenerSupport().clear();
		balloonistPanel.setChangeListener(null);
		
		// TODO remove undoable edit listeners from balloonistPanel.getArtworkTreePanel().getTree()?
		
		// TODO remove rest of listeners
		
		setArtwork(null);
		balloonistPanel.setSelection(null);
		canvas = null;
		scroller = null;
		toolbar = null;
		balloonistPanel = null;
		undoManager = null;
		
		getApplication().unregister(this);
		
		return true;
	}
	
	public JMenu generateItemMenu(final JFrame hub)
	{
		JMenu menu = new JMenu(MENU_TEXT.getString("itemLabel"));
		
		addStemAction = new AbstractAction(MENU_TEXT.getString("addStemLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				canvas.addStem();
			}
		};
		JMenuItem item = menu.add(addStemAction);
		addStemAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		menu.addSeparator();
		
		/*
		item = menu.add(menuText.getString("splitPanelVerticallyLabel"));
		item.setEnabled(false);
		
		item = menu.add(menuText.getString("splitPanelHorizontallyLabel"));
		item.setEnabled(false);
		*/
		
		scaleAction = new PossibleAction(MENU_TEXT.getString("scaleLabel")+"...")
		{
			public void process(int modifiers) throws Exception
			{
				ScaleBox scaleBox = new ScaleBox();
				int result = JOptionPane.showConfirmDialog(ArtworkFrame.this, scaleBox, MENU_TEXT.getString("scaleLabel"), JOptionPane.OK_CANCEL_OPTION);
				
				if (result==JOptionPane.OK_OPTION)
				{
					Scale2dEdit scaleEdit = scaleBox.toEdit();
					
					if (scaleEdit!=null)
					{
						scaleEdit.addSelectablesFrom(canvas.getSelection());

						if (scaleEdit.execute())
						{
							undoableEditHappened(new UndoableEditEvent(ArtworkFrame.this, scaleEdit));
						}
					}
				}
			}
		};
		item = menu.add(scaleAction);
		scaleAction.addPropertyChangeListener(new ActionChangedListener(item));
		
		return menu;
	}
	
	public void handlePaste(boolean behind)
	{
		Sill targeted = targetSill();
		Clipping clipping = Clipping.getClippingFromClipboard();

		if (targeted==null || clipping==null)
		{
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		Crowd crowd = targeted.firstContainedCrowd();

		PasteEdit pasteEdit = null;
		
		if (crowd==null)
		{
			crowd = new Crowd();
			
			if (clipping.couldBePastedTo(crowd))
			{
				targeted.add(crowd);
			}
		}
		
		if (clipping.couldBePastedTo(crowd))
		{
			pasteEdit = new PasteEdit(crowd);
		}
		else if (clipping.couldBePastedTo(targeted))
		{
			pasteEdit = new PasteEdit(targeted);
		}

		if (pasteEdit!=null)
		{
			pasteEdit.setBehind(behind);
			
			pasteEdit.addSelectablesFrom(clipping);
			getCanvas().queueEdit(pasteEdit);
			pasteEdit.execute();
			getCanvas().announceEdit();
	
			// canvas.stateChanged(); // shouldn't the model be issuing this state changed event upon pasting?
		}
		else
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}
	
	public void maintainMenuItemEnablednesses()
	{
		addStemAction.setEnabled(getCanvas().getSelection().containsInstanceOf(Stemmed.class)); // TODO exclude balloons that contain Parallelograms
		
		boolean perchesSelected = getCanvas().getSelection().containsInstanceOf(Perch.class);
		boolean balloonsSelected = getCanvas().getSelection().containsInstanceOf(Balloon.class);
		
		mergeAction.setEnabled(balloonsSelected);
		orderSectionsSpatiallyAction.setEnabled(balloonsSelected);
		partAction.setEnabled(balloonsSelected);
		
		boolean anythingSelected = (getCanvas().getSelection().getSelectedItemsCount()>0);
		
		bringForwardAction.setEnabled(anythingSelected);
		sendBackwardAction.setEnabled(anythingSelected);
		bringToFrontAction.setEnabled(anythingSelected);
		sendToBackAction.setEnabled(anythingSelected);
		clearAction.setEnabled(anythingSelected);
		selectNoneAction.setEnabled(anythingSelected);
		
		scaleAction.setEnabled(getCanvas().getSelection().containsInstanceOf(Resizeable.class));
		
		// TODO handle Copy and Paste in cooperation with the text pane
	}
	public void initializeSizing()
	{
		// Thread.dumpStack();
		Dimension initialSize = null;
		
		if (artwork==null || artwork.getEnclosure()==null)
		{
			initialSize = new Dimension(1320,710);
		}
		else
		{
			initialSize = new Dimension();
			initialSize.setSize(artwork.getWidth()+balloonistPanel.getWidth()+toolbar.getWidth()+5,
					artwork.getHeight()+26);
		}
		
		// System.out.println("initial initialSize="+initialSize);
		
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (screenDimension.getWidth()<initialSize.getWidth())
			initialSize.setSize(screenDimension.getWidth(), initialSize.getHeight());
		
		if (screenDimension.getHeight()<initialSize.getHeight())
			initialSize.setSize(initialSize.getWidth(), screenDimension.getHeight());
		
		setSize(initialSize);
		setVisible(true);
	}
	
	/*
	protected void finalize() throws Throwable
	{
		System.out.println("finalizing: "+this);
		super.finalize();
	}
	*/
	
	public HypertextFrame getHelpFrame()
	{
		if (helpFrame==null)
			return null;
		else
			return (HypertextFrame) helpFrame.get();
	}
	
	private void setHelpFrame(HypertextFrame designatedHelpFrame)
	{
		if (designatedHelpFrame==null)
		{
			helpFrame = null;
		}
		else
		{
			helpFrame = new WeakReference(designatedHelpFrame);
		}
	}

	private BalloonistApplication getBalloonistApplication()
	{
		return (BalloonistApplication) getApplication();
	}
	
	/**
	 * Syncs the zoomer control with the zoom factor of the artwork pane.
	 */
	private void refreshZoomer()
	{
		zoomer.setValue( (int) (100 * canvas.getScaleFactor()));					
	}
	
	/**
	 * If the proposed value is in the range 0.25 thru 2.00 inclusive, returns the
	 * value.
	 *
	 * Otherwise, returns the closest value to the proposed one in the 
	 * range 0.25 thru 2.00 inclusive.
	 */
	private double validateProposedScaleFactor(final double proposed)
	{
		if (proposed>2.0)
		{
			return 2.0;
		}
		else if (proposed<0.25)
		{
			return 0.25;
		}
		else
		{
			return proposed;
		}
	}
}
