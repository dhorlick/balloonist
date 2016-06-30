/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.svg;

import java.awt.Shape;
import java.awt.geom.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.smithandtinkers.util.StructureException;

/**
 * @author dhorlick
 */
public class ScalableVectorGraphicsTranslator
{
	private static final String IDENTIFIER_G = "g";
	
	private static final String IDENTIFIER_RECT = "rect";
		private static final String IDENTIFIER_X = "x";
		private static final String IDENTIFIER_Y = "y";
		private static final String IDENTIFIER_WIDTH = "width";
		private static final String IDENTIFIER_HEIGHT = "height";
	
	// TODO ...
	
	/**
	 * @throws StructureException if corresponding attribute values are missing.
	 * @throws NumberFormatException if the document contains invalid numeric data.
	 */
	public ShapeList svgToShape(Node node) throws StructureException, NumberFormatException
	{
		ShapeList shapes = new ShapeList();
		
		svgToShape(node, shapes);
		
		return shapes;
	}
	
	/**
	 * @throws StructureException if corresponding attribute values are missing in the document.
	 * @throws NumberFormatException if the document contains invalid numeric data.
	 */
	protected void svgToShape(Node node, ShapeList shapes) throws StructureException, NumberFormatException
	{
		for (int loop=0; loop<=node.getChildNodes().getLength()-1; loop++)
		{
			Node subnode = node.getChildNodes().item(loop);
			
			if (IDENTIFIER_G.equals(subnode.getNodeName()))
			{
				ShapeList subshapes = new ShapeList();
				shapes.add(subshapes);
				svgToShape(subnode, subshapes);
			}
			else if (IDENTIFIER_RECT.equals(subnode.getNodeName()))
			{
				validate(subnode, IDENTIFIER_X);
				Element subelement = (Element) subnode;
				
				Rectangle2D rect = new Rectangle2D.Double();
				double x = Double.parseDouble(subelement.getAttribute(IDENTIFIER_X));
				double y = Double.parseDouble(subelement.getAttribute(IDENTIFIER_Y));
				double width = Double.parseDouble(subelement.getAttribute(IDENTIFIER_WIDTH));
				double height = Double.parseDouble(subelement.getAttribute(IDENTIFIER_HEIGHT));
				
				rect.setRect(x, y, width, height);
				
				shapes.add(rect);
			}
			else
			{
				// TODO ...
			}
		}
	}
	
	// TODO add methods to set Color, font style, etc.
	
	public static void validate(Node node, String attrIdentifier) throws StructureException
	{
		if ((!(node instanceof Element)) || !((Element)node).hasAttribute(attrIdentifier))
		{
			throw new StructureException("Node " + node.getNodeName() + " is missing attribute \"" + attrIdentifier + "\".", attrIdentifier);
		}
	}

	public void shapeToSvg(Document doc, Node parent, Shape shape)
	{
		ShapeList shapes = new ShapeList();
		shapes.add(shape);
		shapeToSvg(doc, parent, shapes);
	}

	private void shapeToSvg(Document doc, Node parent, ShapeList shapes)
	{
		for (int loop=0; loop<=shapes.size()-1; loop++)
		{
			Shape shape = (Shape) shapes.get(loop);
			if (shape instanceof Rectangle2D)
			{
				Rectangle2D rect = (Rectangle2D) shape;
				Element rectElement = doc.createElement(IDENTIFIER_RECT);
				rectElement.setAttribute(IDENTIFIER_X, String.valueOf(rect.getX()));
				rectElement.setAttribute(IDENTIFIER_Y, String.valueOf(rect.getY()));
				rectElement.setAttribute(IDENTIFIER_WIDTH, String.valueOf(rect.getWidth()));
				rectElement.setAttribute(IDENTIFIER_HEIGHT, String.valueOf(rect.getHeight()));
				
				parent.appendChild(rectElement);
			}
			else
			{
				// TODO ...
			}
		}
	}
}
