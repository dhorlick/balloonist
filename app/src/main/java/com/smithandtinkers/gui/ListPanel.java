/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import java.awt.Frame;
import java.util.*;

import javax.swing.*;

import com.smithandtinkers.util.TypesafeList;
import com.smithandtinkers.util.WidgetedTypesafeList;


public class ListPanel extends Box
{
	private JList myList = new JList();
	private Frame parentFrame = null;
	private String message;
	
	public ListPanel(WidgetedTypesafeList designatedModel, Frame designatedParentFrame)
	{
		this(designatedModel, designatedParentFrame, null);
	}
	
	public ListPanel(WidgetedTypesafeList designatedModel, Frame designatedParentFrame, String requestedMessage)
	{
		super(BoxLayout.PAGE_AXIS);
		setModel(designatedModel);
		setParentFrame(designatedParentFrame);
		setMessage(requestedMessage);
		initialize();
	}
	
	public void setModel(WidgetedTypesafeList designatedModel)
	{
		myList.setModel(designatedModel);
	}
	
	public void initialize()
	{
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		if (getMessage()!=null)
		{
			JLabel label = new JLabel(getMessage());
			add(label);
			add(Box.createVerticalStrut(15)); // TODO improve
		}
		
		JScrollPane scrollPane = new JScrollPane(myList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
	}
	
	public Object getModel()
	{
		return myList.getModel();
	}
	
	public TypesafeList getModelAsTypesafeList()
	{
		return (TypesafeList) myList.getModel();
	}
		
	/**
	 @return a java.util.List of the selected item Objects 
	 */
	public List getSelectedItems()
	{
		List selected = new ArrayList();
		
		for (int lap=0; lap<=myList.getModel().getSize()-1; lap++)
		{
			if (myList.getSelectionModel().isSelectedIndex(lap))
				selected.add(myList.getModel().getElementAt(lap));
		}
		
		return selected;
	}
	
	public void setParentFrame(Frame designatedParentFrame)
	{
		parentFrame = designatedParentFrame;
	}
	
	public Frame getParentFrame()
	{
		return parentFrame;
	}
	
	public JList getJList()
	{
		return myList;
	}
	
	public void setMessage(String requestedMessage)
	{
		message = requestedMessage;
	}
	
	public String getMessage()
	{
		return message;
	}
}
