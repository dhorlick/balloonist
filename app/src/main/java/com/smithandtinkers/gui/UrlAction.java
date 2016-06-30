/**
 Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 * An abstract implementation of an action. When triggered, a URL dialog will come up with the
 * requested title and the user will be prompted to choose a URL. If the user makes a selection,
 * the selected URL will be passed to the overridden {@link #processUrl} method.
 *
 * 
 * @author dhorlick
 */
public abstract class UrlAction extends PossibleAction
{
	// private boolean saveMode = false;
	// private String optionalFileExtension;
		
	/**
	 * @param designatedTitle We'll add the trailing ellipsis if you don't.
	 */
	public UrlAction(String designatedTitle, Frame designatedFrame)
	{
		super(designatedTitle, designatedFrame);
	}

	public void process(int modifiers) throws Exception
	{	
		Frame frame = getOrDetermineFrame();
		
		Cursor originalCursor = null;
		
		if (frame!=null)
			originalCursor = frame.getCursor();
		
		final String pickUrlLabelText = PossibleAction.DIALOG_TEXT.getString("pickUrlLabel");
		JTextField jTextField = new JTextField();
		jTextField.setColumns(35);
		
		LabeledUrlField labeledUrlField = new LabeledUrlField(
				new JLabel(pickUrlLabelText), jTextField);
		
		labeledUrlField.setMaximumSize(new Dimension(520, 32));
		
		int response = JOptionPane.showConfirmDialog(getFrame(), labeledUrlField,
				pickUrlLabelText,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		if (response==JOptionPane.OK_OPTION)
		{
			URL theUrl = labeledUrlField.getModel().getUrl();
	
			if (theUrl!=null)
			{
				try
				{
					if (frame!=null)
						frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					processUrl(theUrl);
					// customize error message maybe?
				}
				finally
				{
					if (frame!=null)
						frame.setCursor(originalCursor);
				}
			}
		}
	}
	
	/**
	 * Override this to do something useful with the URL.
	 * 
	 * @param theUrl The URL the user chose.
	 */
	public abstract void processUrl(URL theUrl) throws Exception;
}
