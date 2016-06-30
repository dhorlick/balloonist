/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.geom;

import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.WidgetedTypesafeList;

import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A super ellipse whose permiter can be ruffled.
 *
 * @author dhorlick
 */
public class RuffleableSuperEllipse extends AbstractSuperEllipse
{
	private RuffleDieList ruffleDies = new RuffleDieList();
	
	private Type type = ROUND_TYPE;
	
	protected final static String IDENTIFIER_RUFFLES = "ruffles";
		public final static String IDENTIFIER_TYPE = "type";
	
	public static final Type ROUND_TYPE = new Type("round");
	public static final Type JAGGED_TYPE = new Type("jagged");
	
	public RuffleableSuperEllipse()
	{
		super();
		ruffleDies.setGodParent(this);
	}
	
	public RuffleDieList getRuffles()
	{
		return ruffleDies;
	}
	
	public boolean isRuffled()
	{
		return (ruffleDies.size()>0);
	}
	
	public void rufflePrettily()
	{
		if (ruffleDies.size()==0)
		{
			if (getType()==ROUND_TYPE)
			{
				RuffleDie big = new RuffleDie();
				big.setPreferredWidthInPoints(27.0);
				big.setHeightInPoints(7.0);

				RuffleDie small = new RuffleDie();
				small.setPreferredWidthInPoints(12.0);
				small.setHeightInPoints(3.0);

				ruffleDies.add(big);
				ruffleDies.add(small);
			}
			else if (getType()==JAGGED_TYPE)
			{
				RuffleDie big = new RuffleDie();
				big.setPreferredWidthInPoints(27.0);
				big.setHeightInPoints(15.0);

				RuffleDie small = new RuffleDie();
				small.setPreferredWidthInPoints(19.0);
				small.setHeightInPoints(8.0);

				ruffleDies.add(big);
				ruffleDies.add(small);
			}
			
			SingleThreadedChangeSupport.fireChangeEvent(this, CHANGE_EVENT);
		}
	}

	public int determineRuffleQuantity(double designatedArcLength)
	{
		if (ruffleDies.size()==0)
			return 0;
		
		double totalRufflesWidth = totalRufflePreferredDieWidths();
		
		if (totalRufflesWidth==0.0)
			return 0;
		
		int ruffles = ruffleDies.size() * (int) (designatedArcLength / totalRufflesWidth);
		
		if (ruffles>0 && ruffles<3)
			return 3;
		else
			return ruffles;
	}

	public void applyAttributesAndSubElements(Document doc, Element element)
	{
		super.applyAttributesAndSubElements(doc, element);
		
		if (isRuffled())
		{
			Element rufflesElement = doc.createElement(IDENTIFIER_RUFFLES);
			
			element.appendChild(rufflesElement);
			
			if (getType()!=null)
			{
				rufflesElement.setAttribute(IDENTIFIER_TYPE, String.valueOf(getType()));
			}
			
			for (int loop=0; loop<=ruffleDies.size()-1; loop++)
			{
				RuffleDie ruffleDie = (RuffleDie) ruffleDies.get(loop);
				ruffleDie.save(doc, rufflesElement, null);
			}
		}
	}

	public void open(Object parent, Node node, ArchiveContext archiveContext) throws NumberFormatException
	{
		super.open(parent, node, archiveContext);
		
		Element element = (Element) node;
		
		for (int loop=0; loop<=element.getChildNodes().getLength()-1; loop++)
		{
			Node subnode = element.getChildNodes().item(loop);
			
			if (IDENTIFIER_RUFFLES.equals(subnode.getNodeName()))
			{
				Element subelement = (Element) subnode;
				
				if (subelement.hasAttribute(IDENTIFIER_TYPE))
				{
					setType((Type)Type.find(subelement.getAttribute(IDENTIFIER_TYPE)));
				}
				
				for (int subloop=0; subloop<=subelement.getChildNodes().getLength()-1; subloop++)
				{
					Node subsubnode = subnode.getChildNodes().item(subloop);

					if (RuffleDie.IDENTIFIER_RUFFLE_DIE.equals(subsubnode.getNodeName()))
					{
						RuffleDie ruffleDie = new RuffleDie();
						ruffleDie.open(this, subsubnode, archiveContext);
						
						// System.out.println("adding ruffle die: "+ruffleDie);
						ruffleDies.add(ruffleDie);
					}
				}
			}
		}
	}
	
	public float pickAppropriateRuffleMagnification(int index)
	{
		int dieIndex = whichDie(index);
		RuffleDie die = (RuffleDie) ruffleDies.get(dieIndex);
		
		// float guess = (0.003f*(float)die.getPreferredWidthInPoints()) + 5.3f * (float)die.getHeightInPoints() / (float)die.getPreferredWidthInPoints();
		float guess = (0.003f*(float)die.getPreferredWidthInPoints()) + 5.3f * (float)die.getHeightInPoints() / (float)averageRufflePreferredDieWidth();
		
		return guess;
		
		// return 1.25f;
	}
	
	/**
	 * @param ruffleIndex a zero-numbered index
	 * @return a zero-numbered index
	 */
	public int whichDie(int ruffleIndex)
	{
		return ruffleIndex % ruffleDies.size();
	}
	
	public double computeNotchStart(int notchIndex, int totalNotches, double totalArcLength)
	{
		// return (notchIndex+1)*totalArcLength/totalNotches;
		
		if (ruffleDies.size()==0)
			return super.computeNotchStart(notchIndex, totalNotches, totalArcLength);
		
		if (notchIndex>=totalNotches-1)
			return totalArcLength;
		
		// double start = (notchIndex / ruffleDies.size()) * totalRufflePreferredDieWidths();
		
		double totalActualDieWidths = ruffleDies.size() * totalArcLength / totalNotches;
		double start = (notchIndex / ruffleDies.size()) * totalActualDieWidths;
		
		double adjustmentFactor = totalActualDieWidths / totalRufflePreferredDieWidths();
		
		int ruffleDieIndex = whichDie(notchIndex);
		
		for (int loop=0; loop<=ruffleDieIndex; loop++)
		{
			RuffleDie ruffleDie = (RuffleDie) ruffleDies.get(loop);
			start += adjustmentFactor * ruffleDie.getPreferredWidthInPoints();
		}
		
		return start;
	}
	
	public double totalRufflePreferredDieWidths()
	{
		double totalRufflesWidth = 0.0;
		
		for (int loop=0; loop<=ruffleDies.size()-1; loop++)
		{
			RuffleDie ruffleDie = (RuffleDie) ruffleDies.get(loop);
			totalRufflesWidth += ruffleDie.getPreferredWidthInPoints();
		}
		
		return totalRufflesWidth;
	}
	
	public double averageRufflePreferredDieWidth() // TODO cache?
	{
		if (ruffleDies.size()==1)
			return ( (RuffleDie) ruffleDies.get(0)).getPreferredWidthInPoints();
		
		return totalRufflePreferredDieWidths()/(double)ruffleDies.size();
	}
	
	public static class Type
	{
		private String code;
		
		private static Map indexByCode = new LinkedHashMap();
		
		private Type(String designatedCode)
		{
			code = designatedCode;
			indexByCode.put(designatedCode, this);
		}
		
		public static Type find(String requestedCode)
		{
			if (!indexByCode.containsKey(requestedCode))
				return null;

			return (Type) indexByCode.get(requestedCode);
		}

		public Object readResolve() throws ObjectStreamException
		{
			return find(code);
		}

		public String getCode()
		{
			return code;
		}
		
		public static WidgetedTypesafeList getWidgetedIndex()
		{
			WidgetedTypesafeList wtl = new WidgetedTypesafeList(RuffleableSuperEllipse.Type.class);
			wtl.addAll(indexByCode.values());
			
			return wtl;
		}
		
		public String determineInternationalizedName()
		{
			String key = code + "RuffleTypeLabel";
			return AbstractNamed.NAMES_TEXT.getString(key);
		}
		
		public String toString()
		{
			String i8ndName = determineInternationalizedName();
			
			if (i8ndName==null)
				return code;
			else
				return i8ndName;
		}
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type designatedType)
	{
		type = designatedType;
	}
	
	public double getHeight(int requestedRuffleIndex)
	{
		int dieIndex = whichDie(requestedRuffleIndex);
		RuffleDie die = (RuffleDie) ruffleDies.get(dieIndex);
		return die.getHeightInPoints();
	}
	
	public static WidgetedTypesafeList getWidgetedTypeIndex()
	{
		return Type.getWidgetedIndex();
	}
	
	public RuffleDie getRuffleDie(int requestedRuffleIndex)
	{
		int dieIndex = whichDie(requestedRuffleIndex);
		RuffleDie die = (RuffleDie) ruffleDies.get(dieIndex);
		return die;
	}
}
