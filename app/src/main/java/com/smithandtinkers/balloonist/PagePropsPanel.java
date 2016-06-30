/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.layout.PanelLayoutPolicy;
import com.smithandtinkers.layout.edit.PageDimensionsEdit;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;
import com.smithandtinkers.mvc.RenameableCompoundEdit;
import java.awt.Component;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author dhorlick
 */
public class PagePropsPanel extends AbstractPropsPanel
{	
	private LabeledQuantityField pageWidthLabeledQuantityField;
	private LabeledQuantityField pageHeightLabeledQuantityField;
	
	private PanelLayoutPolicyBox panelLayoutPolicyBox;
	
	public PagePropsPanel()
	{
		super();
	}

	protected void stateChanged()
	{
		if (pageWidthLabeledQuantityField!=null)
			pageWidthLabeledQuantityField.stateChanged();
		
		if (pageHeightLabeledQuantityField!=null)
			pageHeightLabeledQuantityField.stateChanged();
		
		if (panelLayoutPolicyBox!=null)
			panelLayoutPolicyBox.jog();
	}

	public void revealed()
	{
	}

	protected void initialize()
	{
		JTextField pageWidthTextField = new JTextField();
		JLabel pageWidthLabel = new JLabel(DrawableDialog.DIALOG_TEXT.getString("pageWidthLabel"));
		pageWidthLabeledQuantityField = new LabeledQuantityField(pageWidthLabel, pageWidthTextField, DrawableDialog.UNITS);
		pageWidthLabeledQuantityField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		pageWidthLabeledQuantityField.setMinimumValue(new Double(0.0));
		pageWidthLabeledQuantityField.setModel(new PrimitiveDoubleHolder()
		{
			public void setDouble(double designatedDouble)
			{
				Dimension newDimension = new Dimension();
				newDimension.setSize(designatedDouble,
						getDrawableDialog().getArtwork().getEnclosure().getHeight());
				
				PageDimensionsEdit pageDimEdit = new PageDimensionsEdit(getDrawableDialog().getArtwork(),
						newDimension);
				if (pageDimEdit.execute())
				{
					final RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(pageDimEdit.getPresentationName());
					compoundEdit.addEdit(pageDimEdit);
					compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
					
					// TODO give the user an option to lock panels and skip layout re-sync
					
					compoundEdit.end();
					
					getDrawableDialog().announceEdit(PagePropsPanel.this, compoundEdit);
				}
			}

			public double getDouble()
			{
				if (!ready())
					return 0.0;
				
				return getDrawableDialog().getArtwork().getEnclosure().getWidth();
			}
			
		});
		add(pageWidthLabeledQuantityField);
		
		JTextField pageHeightTextField = new JTextField();
		JLabel pageHeightLabel = new JLabel(DrawableDialog.DIALOG_TEXT.getString("pageHeightLabel"));
		pageHeightLabeledQuantityField = new LabeledQuantityField(pageHeightLabel, pageHeightTextField, DrawableDialog.UNITS);
		pageHeightLabeledQuantityField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		pageHeightLabeledQuantityField.setMinimumValue(new Double(0.0));
		pageHeightLabeledQuantityField.setModel(new PrimitiveDoubleHolder()
		{
			public void setDouble(double designatedDouble)
			{
				Dimension newDimension = new Dimension();
				newDimension.setSize(getDrawableDialog().getArtwork().getEnclosure().getWidth(), 
						designatedDouble);
				
				PageDimensionsEdit pageDimEdit = new PageDimensionsEdit(getDrawableDialog().getArtwork(),
						newDimension);
				if (pageDimEdit.execute())
				{
					final RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(pageDimEdit.getPresentationName());
					compoundEdit.addEdit(pageDimEdit);
					compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
					
					// TODO give the user an option to lock panels and skip layout re-sync
					
					compoundEdit.end();
					
					getDrawableDialog().announceEdit(PagePropsPanel.this, compoundEdit);
				}
			}

			public double getDouble()
			{
				if (!ready())
					return 0.0;
				
				return getDrawableDialog().getArtwork().getEnclosure().getHeight();
			}
		});
		add(pageHeightLabeledQuantityField);
		
		panelLayoutPolicyBox = new PanelLayoutPolicyBox();
		panelLayoutPolicyBox.setPanelLayoutPolicy(new PanelLayoutPolicy() {
			
			public boolean isManual()
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getArtwork()!=null
						&& getDrawableDialog().getArtwork().getLayout()!=null)
				{
					return getDrawableDialog().getArtwork().getLayout().isManual();
				}
				else
				{
					return false;
				}
			}

			public void setManual(boolean designatedManualness)
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getArtwork()!=null
						&& getDrawableDialog().getArtwork().getLayout()!=null)
				{
					getDrawableDialog().getArtwork().getLayout().setManual(designatedManualness);
				}
			}
			
		});
		panelLayoutPolicyBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panelLayoutPolicyBox);
	}
	
	public boolean ready()
	{
		return (getDrawableDialog()!=null
				&& getDrawableDialog().getArtwork()!=null
				&& getDrawableDialog().getArtwork().getEnclosure()!=null);
	}
}
