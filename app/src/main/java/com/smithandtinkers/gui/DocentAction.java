/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.balloonist.Docent;
import com.smithandtinkers.layout.edit.PresentableEdit;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;


/**
 * @author dhorlick
 */

public class DocentAction extends AbstractAction
{
	private Frame parent = null;
	
	public DocentAction(Frame designatedParent)
	{
		super(PresentableEdit.MENU_TEXT.getString("aboutLabel"));
		parent = designatedParent;
	}

	public void actionPerformed(ActionEvent event)
	{
		displayAboutBox();
	}
	
	public void displayAboutBox()
	{
		Docent docent = new Docent();
		JOptionPane.showMessageDialog(parent, docent, PresentableEdit.MENU_TEXT.getString("aboutLabel"), JOptionPane.PLAIN_MESSAGE);
	}
}
