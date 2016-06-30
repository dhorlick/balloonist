/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.net.URL;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smithandtinkers.gui.BrowseField;
import com.smithandtinkers.gui.LabeledUrlField;
import com.smithandtinkers.gui.SilenceableCheckBox;
import com.smithandtinkers.layout.GraphicResizeable;
import com.smithandtinkers.layout.edit.LinkednessEdit;
import com.smithandtinkers.layout.edit.ResetAspectRatioEdit;
import com.smithandtinkers.layout.edit.SourceEdit;
import com.smithandtinkers.mvc.UrlHolder;

public class GraphicResizeablePropsPanel extends ResizeablePropsPanel
{
	private BrowseField source;
	private LabeledUrlField labeledUrlField;
	private SilenceableCheckBox linkedCheckbox;
	
	public GraphicResizeablePropsPanel()
	{
		super();
	}
	
	protected void initialize()
	{
		super.initialize();
		
		Box resetDimsBox = Box.createHorizontalBox();
		resetDimsBox.add(Box.createHorizontalGlue());
		
		Action resetWidthAction = new AbstractAction(getDrawableDialog().DIALOG_TEXT.getString("resetWidthLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ResetAspectRatioEdit resetWidthEdit = new ResetAspectRatioEdit(getDrawableDialog().DIALOG_TEXT.getString("resetWidthLabel"), true);
				resetWidthEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (resetWidthEdit.execute())
					getDrawableDialog().announceEdit(GraphicResizeablePropsPanel.this, resetWidthEdit);
			}
		};
		
		Action resetHeightAction = new AbstractAction(getDrawableDialog().DIALOG_TEXT.getString("resetHeightLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				ResetAspectRatioEdit resetHeightEdit = new ResetAspectRatioEdit(getDrawableDialog().DIALOG_TEXT.getString("resetHeightLabel"), false);
				resetHeightEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (resetHeightEdit.execute())
					getDrawableDialog().announceEdit(GraphicResizeablePropsPanel.this, resetHeightEdit);
			}
		};
		
		resetDimsBox.add(new JButton(resetWidthAction));
		resetDimsBox.add(new JButton(resetHeightAction));
		
		resetDimsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(resetDimsBox);
		
		JTextField textField = new JTextField("", DrawableDialog.TEXT_FIELD_COLUMNS);
		labeledUrlField = new LabeledUrlField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("sourceLabel")), textField); 
		labeledUrlField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		labeledUrlField.setModel(new UrlHolder()
		{
			public void setUrl(URL designatedUrl)
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getSelection()!=null)
				{
					SourceEdit sourceEdit = new SourceEdit(designatedUrl);
					sourceEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
					
					if (sourceEdit.execute())
					{
						getDrawableDialog().announceEdit(this, sourceEdit);
					}
				}
			}

			public URL getUrl()
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getSelection()!=null)
				{
					GraphicResizeable graphicResizeable = findGraphicResizeable();
					
					if (graphicResizeable==null)
						return null;
					
					return graphicResizeable.getGraphicalContent().getSource();
				}
				
				return null;
			}
		});
		source = new BrowseField(labeledUrlField);
		// setComponentFontSize(source, DrawableDialog.WIDGET_FONT_SIZE);
		source.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(source);
		linkedCheckbox = new SilenceableCheckBox(DrawableDialog.DIALOG_TEXT.getString("linkedLabel"));
		linkedCheckbox.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				// System.out.println("linkedCheckbox: state change");
				
				LinkednessEdit linkednessEdit = new LinkednessEdit(linkedCheckbox.isSelected());
				linkednessEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				// System.out.println("linkednessEdit="+linkednessEdit);
				
				if (linkednessEdit.execute())
				{
					// System.out.println("announcing edit");
					getDrawableDialog().announceEdit(GraphicResizeablePropsPanel.this, linkednessEdit);
				}
				// else
				// 	System.out.println("NOT announcing edit");
			}
		});
		setComponentFontSize(linkedCheckbox, DrawableDialog.WIDGET_FONT_SIZE);
		linkedCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(linkedCheckbox);
	}

	protected void stateChanged()
	{
		super.stateChanged();
		
		if (source!=null)
			labeledUrlField.stateChanged();
		
		if (linkedCheckbox!=null)
		{
			GraphicResizeable graphicResizeable = findGraphicResizeable();
			
			if (graphicResizeable!=null && graphicResizeable.getGraphicalContent().isLinked()
					!= linkedCheckbox.isSelected())
			{
				linkedCheckbox.setSilent(true);
				linkedCheckbox.getModel().setSelected(graphicResizeable.getGraphicalContent().isLinked());
				linkedCheckbox.setSilent(false);
				linkedCheckbox.invalidate();
				linkedCheckbox.repaint();
			}
		}
	}
	
	public static void setComponentFontSize(JComponent designatedComponent, int desigantedFontSize)
	{
		Font font = designatedComponent.getFont();
		if (font!=null)
			designatedComponent.setFont(font.deriveFont((float)desigantedFontSize));
	}
	
	public GraphicResizeable findGraphicResizeable()
	{
		Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();

		while (walk.hasNext())
		{
			Object item = walk.next();
			if (item instanceof GraphicResizeable)
			{
				return (GraphicResizeable) item;
			}
		}
		
		return null;
	}
}
