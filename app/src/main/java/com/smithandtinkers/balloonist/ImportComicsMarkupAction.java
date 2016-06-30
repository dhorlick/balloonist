/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.gui.ApplicationFileAction;
import com.smithandtinkers.gui.ApplicationFrame;
import com.smithandtinkers.gui.DOptionPane;
import com.smithandtinkers.gui.ListPanel;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.io.CachingArchiveContext;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Balloon;
import com.smithandtinkers.layout.Crowd;
import com.smithandtinkers.layout.edit.PresentableEdit;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.WidgetedTypesafeList;
import com.smithandtinkers.util.XmlFriend;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author dhorlick
 */
public class ImportComicsMarkupAction extends ApplicationFileAction
{	
	public ImportComicsMarkupAction(ApplicationFrame designatedHub, BalloonistApplication designatedBalloonistApplication)
	{
		super(PresentableEdit.MENU_TEXT.getString("importComicsMLLabel"), designatedHub, false);
	}
	
	public void processFile(File theFile) throws Exception
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		documentBuilder.setEntityResolver(new EntityResolver()
		{
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
			{
				java.io.InputStream inputStream = new java.io.InputStream()
				{
					public int read()
					{
						return -1;
					}
				};
				
				return new InputSource(inputStream);
			}
		});
		
		FileInputStream fileInputStream = new FileInputStream(theFile);
		
		Document imported = documentBuilder.parse(fileInputStream);
		WidgetedTypesafeList stripCatalog = catalogStrips(imported);
		
		int stripToUse = 1;
		
		if (stripCatalog.size()==0)
		{
			DOptionPane.showMessageDialog(getFrame(), PossibleAction.DIALOG_TEXT.getString("striplessMessage"));
			
			return;
		}
		if (stripCatalog.size()>1)
		{
			final ListPanel listPanel = new ListPanel(stripCatalog, getFrame());
			listPanel.getJList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			final String PICK_STRIP_LABEL = PresentableEdit.MENU_TEXT.getString("pickStripLabel");

			int response = JOptionPane.showConfirmDialog(getFrame(), listPanel, PICK_STRIP_LABEL, JOptionPane.OK_CANCEL_OPTION);

			if (response==JOptionPane.OK_OPTION && listPanel.getSelectedItems().size()>0)
			{
				Object pick = listPanel.getSelectedItems().get(0);
				
				int index = stripCatalog.indexOf(pick);
				
				if (index>-1)
					stripToUse = 1+index;
				else
					System.err.println("Couldn't find chosen strip: "+pick);
			}
			else
			{
				return;
			}
		}
		
		Transformer autobot = transformerFactory.newTransformer(new StreamSource(ClassLoader.getSystemResourceAsStream("resources/xsl/comicsml2bnaml.xsl")));
		
		Document transformed = documentBuilder.newDocument();
		
		autobot.setParameter("strip-index", String.valueOf(stripToUse));
		final Font DEFAULT_FONT = BalloonEngineState.getInstance().getDefaultFont();
		
		if (DEFAULT_FONT!=null) // and it shouldn't...
		{
			autobot.setParameter("font-family", DEFAULT_FONT.getFamily());
			autobot.setParameter("font-size", String.valueOf(DEFAULT_FONT.getSize()));
			autobot.setParameter("margin", String.valueOf(BalloonEngineState.determineDefaultMargin()));
		}
		
		autobot.transform(new DOMSource(imported), new DOMResult(transformed));
		
		Artwork newArtwork = new Artwork();
		
		ArtworkFrame newArtworkFrame = new ArtworkFrame((BalloonistApplication)getApplication());
		
		CachingArchiveContext archiveContext = new CachingArchiveContext();
		archiveContext.setListenerToNotifyAboutUndoableEdits(newArtworkFrame);
		// System.out.println("archiveContext.getListenerToNotifyAboutUndoableEdits()="+archiveContext.getListenerToNotifyAboutUndoableEdits());
		newArtwork.open(null, transformed.getDocumentElement(), archiveContext);
		newArtwork.startOver();
		
		arrangeBalloons(newArtwork);

		newArtworkFrame.setArtwork(newArtwork);
		newArtwork.setChangeListener(newArtworkFrame);
	}
	
	public static void arrangeBalloons(Artwork designatedArtwork)
	{
		arrangeBalloons(designatedArtwork.getSill());
	}
	
	public static void arrangeBalloons(Sill designatedSill)
	{
		if (designatedSill==null)
			return;
		
		Iterator walk = designatedSill.iterator();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			if (item instanceof Sill)
			{
				arrangeBalloons((Sill) item);
			}
			else if (item instanceof Crowd)
			{
				arrangeBalloons((Crowd) item);
			}
			else if (item instanceof Balloon)
			{
				((Balloon)item).arrange();
			}
		}
	}
	
	public static void arrangeBalloons(Crowd designatedCrowd)
	{
		if (designatedCrowd==null)
			return;
		
		Iterator walk = designatedCrowd.iterator();
		
		while (walk.hasNext())
		{
			Object item = walk.next();
			if (item instanceof Balloon)
			{
				((Balloon)item).arrange();
			}
		}
	}
	
	public static WidgetedTypesafeList catalogStrips(Document doc)
	{
		WidgetedTypesafeList catalog = new WidgetedTypesafeList(String.class);
		
		int stripNumber = 0;
		
		Element comicsElement = doc.getDocumentElement();
		if (!"comic".equals(comicsElement.getNodeName()))
		{
			System.err.println("Root node is not a comic");
			return catalog;
		}
		
		for (int loop=0; loop<=comicsElement.getChildNodes().getLength()-1; loop++)
		{
			Node node = comicsElement.getChildNodes().item(loop);
			if ("strip".equals(node.getNodeName()))
			{
				++stripNumber;
				String title = null;
				
				for (int subloop=0; subloop<=node.getChildNodes().getLength()-1 && title==null; subloop++)
				{
					Node subnode = node.getChildNodes().item(subloop);
					
					if ("title".equals(subnode.getNodeName()))
					{
						title = XmlFriend.uncoverText(subnode);
					}
				}
				
				if (title==null && node instanceof Element)
				{
					Element element = (Element) node;
					
					if (element.hasAttribute("id"))
					{
						title = element.getAttribute("id");
					}
				}
				
				if (title==null)
				{
					title = AbstractNamed.NAMES_TEXT.getString("stripLabel")+" #"+stripNumber;
				}
				
				catalog.add(title);
			}
		}
		
		return catalog;
	}
}
