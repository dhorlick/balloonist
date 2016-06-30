/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.PlatformFriend;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


/**
 * Pretty much JOptionPane + text wrapping
 *
 * @author dhorlick
 */
public class DOptionPane extends JOptionPane
{
	private int maxCharactersPerLineCount = 46;
	
	public DOptionPane()
	{
		super();
	}
	
	public DOptionPane(int designatedMaxCharactersPerLineCount)
	{
		this();
		setMaxCharactersPerLineCount(designatedMaxCharactersPerLineCount);
	}
	
	public static void showMessageDialog(Component parentComponent, String requestedText) // TODO rework to return instances of this
	{
		JOptionPane.showMessageDialog(parentComponent, buildTransparentWrappingTextArea(requestedText));
	}
	
	public static void showMessageDialog(Component parentComponent, String requestedText,
			String requestedTitle, int requestedMessageType) // TODO rework to return instances of this
	{
		JTextArea textArea = buildTransparentWrappingTextArea(requestedText);
		
		JOptionPane.showMessageDialog(parentComponent, textArea,
				requestedTitle, requestedMessageType);		
	}
	
	public static int showConfirmDialog(Component parentComponent, String requestedText,
			String requestedTitle, int requestedMessageType) // TODO rework to return instances of this
	{
		JTextArea textArea = buildTransparentWrappingTextArea(requestedText);
		
		return JOptionPane.showConfirmDialog(parentComponent, textArea, 
				requestedTitle, requestedMessageType);
	}
	
	public static JTextArea buildTransparentWrappingTextArea(String requestedText)
	{
		JTextArea textArea = new JTextArea(requestedText);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBackground(null);
		textArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		textArea.setRows(5);
		textArea.setColumns(44); // TODO make configurable?
		textArea.setEditable(false);
		
		return textArea;
	}
	
	public int getMaxCharactersPerLineCount()
	{ 
		return maxCharactersPerLineCount;
	}
	
	/**
	 * This must be called at instantiation time. Presumably, JOptionPane
	 * uses it prior to be shown.
	 */
	private void setMaxCharactersPerLineCount(int designatedMaxCharactersPerLineCount)
	{
		maxCharactersPerLineCount = designatedMaxCharactersPerLineCount;
	}
	
	public String toString()
	{
		return "com.smithandtinkers.gui.DOptionPane {"
			 + "message = " + getMessage() + ", " 
			 + "maxCharactersPerLineCount = " + maxCharactersPerLineCount
		+ "}";
	}
}
