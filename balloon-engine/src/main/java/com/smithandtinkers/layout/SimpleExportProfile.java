/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import java.util.HashMap;
import java.util.Map;

import com.smithandtinkers.svg.SvgFlavor;

/**
 * A simple implementation of {@link ExportProfile}.
 */
public class SimpleExportProfile implements ExportProfile
{	
	private boolean includeFlowingText;
	private boolean includeGlyphedText;
	private boolean includeBalloons = true;
	
	private SvgFlavor svgFlavor;
	
	public SimpleExportProfile()
	{
	}
	
	public SimpleExportProfile(SvgFlavor designatedFlavor)
	{
		setIncludeFlowingText(designatedFlavor.getSupportsIncludeFlowingText());
		setIncludeGlyphedText(!designatedFlavor.getSupportsIncludeFlowingText());
	}
	
	public boolean shouldIncludeFlowingText()
	{
		return includeFlowingText;
	}
	
	public boolean shouldIncludeGlyphedText()
	{
		return includeGlyphedText;
	}
	
	public void setIncludeFlowingText(boolean designatedIncludeFlowingText)
	{
		includeFlowingText = designatedIncludeFlowingText;
	}
	
	public void setIncludeGlyphedText(boolean designatedIncludeGlyphedText)
	{
		includeGlyphedText = designatedIncludeGlyphedText;
	}
		
	public boolean shouldIncludeBalloons()
	{
		return includeBalloons;
	}	
	
	public String toString()
	{
		Map map = new HashMap();
		
		map.put("Include Flowing Text", new Boolean(includeFlowingText));
		map.put("Include Glyphed Text", new Boolean(includeGlyphedText));
		map.put("Include Balloons", new Boolean(includeBalloons));
		map.put("Svg Flavor", getSvgFlavor());
		
		return map.toString();	
	}

	public SvgFlavor getSvgFlavor()
	{
		return svgFlavor;
	}

	public void setSvgFlavor(SvgFlavor svgFlavor)
	{
		this.svgFlavor = svgFlavor;
	}
}
