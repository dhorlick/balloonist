/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.smithandtinkers.text.StyledDocumentStraw;


/**
 * @author dhorlick
 */
public abstract class SvgFlavor
{
	public static final String BATIK_EXT_NAMESPACE_URI = "http://xml.apache.org/batik/ext";
	
	private String label;
	private String description;
	
	public static final SvgFlavor SVG_1_0 = new SvgFlavor("SVG 1.0", "Scalable Vector Graphics 1.0 XML format. Compatible with Adobe Illustrator 10+", true, false, 1.0)
	{
		public Element createFlowRootElement(Document doc)
		{
			return null;
		}

		public Element createFlowRegionElement(Document doc)
		{
			return null;
		}

		public Element createFlowDivElement(Document doc)
		{
			return null;
		}

		public Element createFlowParaElement(Document doc)
		{
			return null;
		}
		
		public String getStyledTextElementName()
		{
			return StyledDocumentStraw.IDENTIFIER_TSPAN;
		}
	};
	
	public static final SvgFlavor ADOBE_SVG_VIEWER_60P1_SVG_1_1 = new SvgFlavor("Adobe SVG Viewer 6.0 preview 1 SVG 1.1", "Scalable Vector Graphics 1.1 XML format with Adobe extensions. Compatible with Adobe Illustrator CS", true, true, 1.1)
	{
		public Element createFlowRootElement(Document doc)
		{
			return doc.createElement("flowDef"); // TODO use namespace?
		}

		public Element createFlowRegionElement(Document doc)
		{
			return doc.createElement("region");
		}

		public Element createFlowDivElement(Document doc)
		{
			return doc.createElement("flow");
		}

		public Element createFlowParaElement(Document doc)
		{
			return doc.createElement("flowPara"); // TODO confirm
		}
		
		public String getStyledTextElementName()
		{
			return "flowSpan";
		}
	};
	
	public static final SvgFlavor APACHE_BATIK_1_5_SVG = new SvgFlavor("Apache Batik 1.5 SVG 1.1", "Scalable Vector Graphics 1.1 XML format with Apache Batik", true, true, 1.1)
	{

		public Element createFlowRootElement(Document doc)
		{
			return doc.createElementNS(BATIK_EXT_NAMESPACE_URI, "flowText");
		}

		public Element createFlowRegionElement(Document doc)
		{
			return doc.createElementNS(BATIK_EXT_NAMESPACE_URI, "flowRegion");
		}

		public Element createFlowDivElement(Document doc)
		{
			return doc.createElementNS(BATIK_EXT_NAMESPACE_URI, "flowDiv");
		}

		public Element createFlowParaElement(Document doc)
		{
			return doc.createElementNS(BATIK_EXT_NAMESPACE_URI, "flowPara");
		}
		
		public String getStyledTextElementName()
		{
			return "flowSpan";
		}
	};
	
	public static final SvgFlavor SVG_1_2 = new SvgFlavor("SVG 1.2", "Scalable Vector Graphics 1.2 XML format.", true, true, 1.2)
	{
		public Element createFlowRootElement(Document doc)
		{
			return doc.createElement("flowRoot");
		}

		public Element createFlowRegionElement(Document doc)
		{
			return doc.createElement("flowRegion");
		}

		public Element createFlowDivElement(Document doc)
		{
			return doc.createElement("flowDiv");
		}

		public Element createFlowParaElement(Document doc)
		{
			return doc.createElement("flowPara");
		}
		
		public String getStyledTextElementName()
		{
			return "flowSpan";
		}
	};
	
	private boolean supportsIncludeFlowingText;
	private boolean supportsIncludeGlyphedText;
	private double svgVersion;
	
	protected SvgFlavor(String designatedLabel, String designatedDescription, boolean designatedSupportsIncludeGlyphedText, boolean designatedSupportsIncludeFlowingText, double designatedSvgVersion)
	{
		setLabel(designatedLabel);
		setDescription(designatedDescription);
		
		setSupportsIncludeGlyphedText(designatedSupportsIncludeGlyphedText);
		setSupportsIncludeFlowingText(designatedSupportsIncludeFlowingText);
		setSvgVersion(designatedSvgVersion);
	}
	
	public void setSupportsIncludeGlyphedText(boolean designatedSupportsIncludeGlyphedText)
	{
		supportsIncludeGlyphedText = designatedSupportsIncludeGlyphedText;
	}
	
	public void setSupportsIncludeFlowingText(boolean designatedSupportsIncludeFlowingText)
	{
		supportsIncludeFlowingText = designatedSupportsIncludeFlowingText;
	}
	
	public boolean getSupportsIncludeGlyphedText()
	{
		return supportsIncludeGlyphedText;
	}
	
	public boolean getSupportsIncludeFlowingText()
	{
		return supportsIncludeFlowingText;
	}

	public void setSvgVersion(double designatedSvgVersion)
	{
		svgVersion = designatedSvgVersion;
	}
	
	public double getSvgVersion()
	{
		return svgVersion;
	}
	
	public void setLabel(String designatedLabel)
	{
		label = designatedLabel;
	}
	
	public void setDescription(String designatedDescription)
	{
		description = designatedDescription;
	}
	
	public abstract Element createFlowRootElement(Document doc);
	public abstract Element createFlowRegionElement(Document doc);
	public abstract Element createFlowDivElement(Document doc);
	public abstract Element createFlowParaElement(Document doc);
	public abstract String getStyledTextElementName();
}
