/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.io.FileOwner;
import com.smithandtinkers.svg.GraphicalContent;

import java.io.File;
import java.net.URL;
import javax.swing.*;


public class BrowseField extends Box
{
	private LabeledUrlField labeledUrlField;
	private FileOwner baseFileOwner;
	
	public BrowseField(LabeledUrlField designatedLabeledUrlField)
	{
		super(BoxLayout.X_AXIS);
		setLabeledUrlField(designatedLabeledUrlField);
		initialize();
	}
	
	private void initialize()
	{
		setBackground(null);
		add(labeledUrlField);
		
		FileAction browseAction = new FileAction(PossibleAction.DIALOG_TEXT.getString("browseLabel"), false)
		{
			public void processFile(File theFile) throws Exception
			{
				URL asUrl = GraphicalContent.fileToURL(theFile);

				labeledUrlField.getModel().setUrl(asUrl);
				labeledUrlField.getField().requestFocus();
			}
		};
		
		browseAction.setComponent(this);
		
		JButton browseButton = new JButton(browseAction);
		browseButton.setText("\u2026"); // ellipsis
		browseButton.setToolTipText(PossibleAction.DIALOG_TEXT.getString("browseTooltipLabel"));
		
		add(browseButton);
	}
	
	public void setLabeledUrlField(LabeledUrlField designatedLabeledUrlField)
	{
		labeledUrlField = designatedLabeledUrlField;
	}
	
	public LabeledUrlField getLabeledUrlField()
	{
		return labeledUrlField;
	}
}