/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.BalloonEngineState;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


/**
 * An action taken in a GUI that may or may not succeed.
 * 
 * Exceptions will be announced to the user via a dialog box. To do something else with an 
 * Exception, override {@link #logException(Exception)}.
 *
 * @author dhorlick
 */
public abstract class PossibleAction extends AbstractAction
{
	private Frame frame;
	private Component component;
	
	/**
	 * The title for this action. Ellipses will be managed corrected regardless of whether you provide
	 * one or not.
	 */
	private String title;
	
	/**
	 * Useful for storing the name of a parent menu.
	 */
	private String optionalPrefix;
	
	private static final String ELLIPSIS = "...";
	
	public final static ResourceBundle DIALOG_TEXT = BalloonEngineState.DIALOG_TEXT;
	
	public PossibleAction(String designatedTitle)
	{
		super();
		setTitle(designatedTitle);
	}
	
	public PossibleAction(String designatedTitle, Frame designatedFrame)
	{
		this(designatedTitle);
		setFrame(designatedFrame);
	}
	
	/**
	 * Override this to do something useful.
	 */
	public abstract void process(int modifiers) throws Exception;
	
	/**
	 * @param designatedFrame An optional frame to attach a file dialog too.
	 */
	protected void setFrame(Frame designatedFrame)
	{
		frame = designatedFrame;
	}
	
	public Frame getFrame()
	{
		return frame;
	}

	/**
	 * @param designatedTitle We'll add the trailing ellipsis if you don't.
	 */
	public void setTitle(String designatedTitle)
	{
		designatedTitle = FileAction.cleanTitle(designatedTitle);
		title = designatedTitle;
		
		putValue(NAME, title + ELLIPSIS);
	}
	
	public static String cleanTitle(String designatedTitle)
	{
		if (designatedTitle!=null && designatedTitle.endsWith(ELLIPSIS))
		{
			designatedTitle = designatedTitle.substring(0, designatedTitle.length()-ELLIPSIS.length());
		}
		
		return designatedTitle;
	}
	
	/**
	 * @return The name of the Action. Same as the title, but with a trailing ellipsis.
	 */
	public String getName()
	{
		return (String) getValue(NAME);
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void actionPerformed(ActionEvent event)
	{
		Frame appropriateFrame = getOrDetermineFrame();			
		
		try
		{
			if (event==null)
				process(0);
			else
				process(event.getModifiers());
		}
		catch (Exception exception)
		{
			// exception.printStackTrace();

			StringBuffer prefixedTitle = new StringBuffer();
			StringBuffer message = new StringBuffer().append(DIALOG_TEXT.getString("couldNotLabel"));
			message.append(" ");
				
			if (getOptionalPrefix()!=null)
			{
				prefixedTitle.append(getOptionalPrefix());
				prefixedTitle.append(" ");
			}
			
			prefixedTitle.append(getTitle());
			message.append(prefixedTitle);
			
			String exceptionMessage = exception.getMessage();
			
			if (exceptionMessage!=null && exceptionMessage.length()>0)
			{
				message.append("; ");
				
				if (exceptionMessage.length()>1 && Character.isUpperCase(exceptionMessage.charAt(0)))
					exceptionMessage = Character.toLowerCase(exceptionMessage.charAt(0)) + exceptionMessage.substring(1);
					
				message.append(exceptionMessage);
			}
			
			if (!message.toString().endsWith("."))
				message.append(".");
			
			JTextArea messageTextArea = new JTextArea(message.toString());
			messageTextArea.setColumns(32);
			messageTextArea.setLineWrap(true);
			messageTextArea.setWrapStyleWord(true);
			messageTextArea.setBackground(null);
			messageTextArea.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			
			JOptionPane.showMessageDialog(appropriateFrame,
					messageTextArea,
					prefixedTitle.toString() + " " + DIALOG_TEXT.getString("problemLabel"), JOptionPane.INFORMATION_MESSAGE);

			logException(exception);
		}
	}	
	
	/**
	 * Override this method to log or otherwise do something useful with announced Exceptions
	 * 
	 * By default, it just prints the stack trace to the standard error stream.
	 */
	public void logException(Exception exception)
	{
		exception.printStackTrace();
	}
	
	public static Frame determineFrame(Component designatedComponent)
	{
		Component component=designatedComponent;
			
		for (; component.getParent()!=null; component=component.getParent());
		
		if (component instanceof Frame)
			return (Frame) component;
		else
			return null;
	}
	
	public Frame determineFrameFromComponent()
	{
		if (component==null)
			return null;
		
		return determineFrame(component);
	}
	
	public Frame getOrDetermineFrame()
	{
		if (frame!=null)
			return frame;
		
		return determineFrameFromComponent();
	}
	
	public void setComponent(Component designatedComponent)
	{
		component = designatedComponent;
	}
	
	public Component getComponent()
	{
		return component;
	}
	
	public String getOptionalPrefix()
	{
		return optionalPrefix;
	}
	
	public void setOptionalPrefix(String designatedOptionalPrefix)
	{
		optionalPrefix = designatedOptionalPrefix;
	}
}
