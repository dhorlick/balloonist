/**
 Copyleft 2004 by Dave Horlick
 */

package com.smithandtinkers.layout;

import com.smithandtinkers.balloonist.Clipping;
import java.awt.datatransfer.DataFlavor;


/**
 * A public inventory of useful data flavors.
 * 
 * @author dhorlick
 */
public abstract class TasteSpoon
{
	private static final String PDF_LABEL = "PDF"; // "Portable Document Format";
	private static final String SVG_LABEL = "SVG"; // "Scalable Vector Graphics";
	
	public static final DataFlavor BALLOONIST_CLIPPING_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType 
		+ "; class="+Clipping.class.getName(), "Balloonist Clipping");
	
	public static final DataFlavor SVG_FLAVOR = new DataFlavor("image/svg+xml", SVG_LABEL);
	// public static final DataFlavor OLD_SVG_FLAVOR = new DataFlavor("image/svg-xml", SVG_LABEL);
	public static final DataFlavor PDF_FLAVOR = new DataFlavor("application/pdf", PDF_LABEL);
	public static final DataFlavor X_PDF_FLAVOR = new DataFlavor("application/x-pdf", PDF_LABEL);
	// public static final DataFlavor MAC_JAVA13_PDF_FLAVOR = new com.apple.mrj.datatransfer.OSTypeFlavor(new com.apple.mrj.MRJOSType("PDF "), "application/x-mac-ostype-50444620", PDF_LABEL);
	public static final DataFlavor MAC_JAVA11_PDF_FLAVOR = new DataFlavor("application/x-mac-data; ostype=\"PDF \"", PDF_LABEL);
	
	
}
