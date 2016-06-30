/*
 Copyleft 2004 by Dave Horlick
*/


package com.smithandtinkers.layout;

import com.smithandtinkers.svg.SvgFlavor;


/**
 * A set of instructions for how an export should be conducted.
 */
public interface ExportProfile
{	
	public boolean shouldIncludeFlowingText();
	
	public boolean shouldIncludeGlyphedText();
	
	public boolean shouldIncludeBalloons();
	
	public SvgFlavor getSvgFlavor();
}
