/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author dhorlick
 */
public class HorizontallyScrollingTextField extends Box
{
	private JTextArea textArea;
	private JScrollPane scrollPane;
	
	public HorizontallyScrollingTextField()
	{
		super(BoxLayout.X_AXIS);
		textArea = new JTextArea();
		textArea.setRows(1);
		scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(scrollPane);
	}
	
	public static void main(String [] args)
	{
		javax.swing.JFrame frame = new javax.swing.JFrame("test");
		frame.setSize(400,340);
		HorizontallyScrollingTextField hstf = new HorizontallyScrollingTextField();
		hstf.setText("AlwaysOnYourSideAlwaysOnYourSideAlwaysOnYourSideAlwaysOnYourSideAlwaysOnYourSide");
		// Box box = new Box(BoxLayout.X_AXIS);
		// box.add(hstf);
		
		JLabel label = new JLabel("jert");
		
		AbstractLabeledComponent alc = new AbstractLabeledComponent(label)
		{
			public void stateChanged(ChangeEvent e)
			{
			}
		};
		
		// alc.add(box);
		alc.add(hstf);
		alc.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		
		frame.getContentPane().add(alc);
		frame.setVisible(true);
	}
	
	public JTextArea getTextArea()
	{
		return textArea;
	}
	
	public void setText(String designatedText)
	{
		textArea.setText(designatedText);
	}
}
