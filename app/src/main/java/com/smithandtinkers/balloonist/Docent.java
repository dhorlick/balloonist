/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.LinkedLabel;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.util.ResourceFriend;

import java.awt.Component;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * @author dhorlick
 */
public class Docent extends JPanel
{
	private JPanel registrationPanel;
	
	public Docent()
	{
		init();
	}
	
	private void init()
	{
		Box box = Box.createVerticalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Box topPart = Box.createHorizontalBox();
		final ImageIcon IMAGE_ICON = ResourceFriend.retrieveImageIcon("resources/pictures/splash.png");
		JLabel iconLabel = new JLabel(IMAGE_ICON);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		topPart.add(iconLabel);
		
		
		topPart.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		ResourceBundle props = ResourceBundle.getBundle("resources/text/app");
		
		final String VERSION = props.getString("version");
		final String RELEASED = props.getString("released");
		final String NAME = props.getString("name");
		final String DESCRIPTION = props.getString("description");
		final String AUTHOR = props.getString("author");
		
		Box appInfoPanel = Box.createVerticalBox();
		
		// appInfoPanel.setLayout(new BoxLayout(appInfoPanel, BoxLayout.Y_AXIS));
		appInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		StringBuffer nameAndVersion = new StringBuffer();
		nameAndVersion.append(NAME);
		if (VERSION!=null)
		{
			nameAndVersion.append(" v");
			nameAndVersion.append(VERSION);
		}
		
		final JLabel nameLabel = new JLabel(nameAndVersion.toString());
		nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		appInfoPanel.add(nameLabel);
		
		final JLabel authorLabel = new JLabel(PossibleAction.DIALOG_TEXT.getString("byLabel")+" "+AUTHOR);
		authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		appInfoPanel.add(authorLabel);

		StringBuffer message = new StringBuffer();
		message.append("\n\n");
		message.append(DESCRIPTION);
		message.append("\n\n");
		
		message.append(RELEASED);
		
		JTextArea attribution = new JTextArea(message.toString());
		attribution.setAlignmentX(Component.LEFT_ALIGNMENT);
		attribution.setColumns(32);
		attribution.setLineWrap(true);
		attribution.setWrapStyleWord(true);
		attribution.setEditable(false);

		attribution.setBackground(null);
		
		appInfoPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		appInfoPanel.add(attribution);
		
		topPart.add(appInfoPanel);
		
		box.add(topPart);

		add(box);
	}
}
