/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smithandtinkers.gui.LabeledMagnitudeField;
import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.layout.edit.AspectRatioEdit;
import com.smithandtinkers.layout.edit.GapEdit;
import com.smithandtinkers.layout.Layout;
import com.smithandtinkers.layout.edit.PageMarginEdit;
import com.smithandtinkers.layout.edit.PanelCountEdit;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;
import com.smithandtinkers.mvc.PrimitiveIntegerHolder;
import com.smithandtinkers.mvc.RenameableCompoundEdit;


/**
 * @author dhorlick
 */
public class LayoutPropsPanel extends AbstractPropsPanel
{
	private LabeledMagnitudeField panelsField;
	private LabeledQuantityField aspectRatioField;
	private LabeledQuantityField vertGutterField;
	private LabeledQuantityField horizGutterField;
	
	private LabeledQuantityField horizPageMarginField;
	private LabeledQuantityField vertPageMarginField;
	
	public LayoutPropsPanel()
	{
		super();
	}
	
	protected void initialize()
	{
		horizPageMarginField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("horizontalPageMarginLabel")), new JTextField(DrawableDialog.TEXT_FIELD_COLUMNS), DrawableDialog.UNITS);
		horizPageMarginField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		horizPageMarginField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		horizPageMarginField.setMinimumValue(new Double(0.0));
		add(horizPageMarginField);
		
		vertPageMarginField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("verticalPageMarginLabel")), new JTextField(DrawableDialog.TEXT_FIELD_COLUMNS), DrawableDialog.UNITS);
		vertPageMarginField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		vertPageMarginField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		vertPageMarginField.setMinimumValue(new Double(0.0));
		add(vertPageMarginField);
		
		Box twofer = Box.createHorizontalBox();
		twofer.setAlignmentX(Component.LEFT_ALIGNMENT);
		twofer.setAlignmentY(Component.CENTER_ALIGNMENT);
		final JTextField panelCountField = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS);
		panelsField = new LabeledMagnitudeField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("panelLabel")), panelCountField);
		panelsField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		panelsField.setMinimumValue(new Integer(1));
		panelsField.setMaximumValue(new Integer(30));
		twofer.add(panelsField);
		
		final JTextField aspectRatio = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS);
		aspectRatioField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("aspectRatioLabel")), aspectRatio);
		aspectRatioField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		aspectRatioField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		twofer.add(aspectRatioField);
		aspectRatioField.setMinimumValue(new Double(0.0));
		aspectRatioField.setMaximumValue(new Double(10.0));
		add(twofer);
		
		final JTextField horizontalGutterSizeInPoints = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS);
		horizGutterField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("horizontalGutterSizeLabel")), horizontalGutterSizeInPoints, DrawableDialog.UNITS);
		horizGutterField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		horizGutterField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		horizGutterField.setMinimumValue(new Double(0.0));
		add(horizGutterField);
		
		final JTextField verticalGutterSizeInPoints = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS);
		vertGutterField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("verticalGutterSizeLabel")), verticalGutterSizeInPoints, DrawableDialog.UNITS);
		vertGutterField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		vertGutterField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		vertGutterField.setMinimumValue(new Double(0));
		add(vertGutterField);
		
		ItemListener il = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				LayoutPropsPanel.this.stateChanged();
			}	
		};
		
		vertGutterField.getDropdown().addItemListener(il);
		horizGutterField.getDropdown().addItemListener(il);
		vertPageMarginField.getDropdown().addItemListener(il);
		horizPageMarginField.getDropdown().addItemListener(il);
		
		panelsField.setModel(new PrimitiveIntegerHolder()
			{
				public int getInt()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0;
					
					return theLayout.getApertureQuantity();
				}

				public void setInt(int designatedInt)
				{
					if (getSillLayout()!=null && designatedInt!=getSillLayout().getApertureQuantity())
					{
						PanelCountEdit panelCountEdit = new PanelCountEdit(getDrawableDialog().getArtwork(), designatedInt);
						if (panelCountEdit.execute())
						{
							// System.out.println("building compound edit...");
							
							RenameableCompoundEdit compountEdit = new RenameableCompoundEdit(panelCountEdit.getPresentationName());
							compountEdit.addEdit(panelCountEdit);
							compountEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compountEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compountEdit);
						}
			
					}
				}
			}
		);
		
		aspectRatioField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0.0;
						
					return theLayout.getAspectRatio();
				}

				public void setDouble(double designatedDouble)
				{
					if (getSillLayout()!=null && designatedDouble!=getSillLayout().getAspectRatio())
					{
						AspectRatioEdit aspectRatioEdit = new AspectRatioEdit(getSillLayout(), designatedDouble);
						if (aspectRatioEdit.execute())
						{
							RenameableCompoundEdit compountEdit = new RenameableCompoundEdit(aspectRatioEdit.getPresentationName());
							compountEdit.addEdit(aspectRatioEdit);
							compountEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compountEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compountEdit);
						}
					}
				}
			}
		);
		
		vertGutterField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0.0;
					
					return theLayout.getVerticalGapInPoints();
				}

				public void setDouble(double designatedDouble)
				{
					if (getSillLayout()!=null && designatedDouble!=getSillLayout().getVerticalGapInPoints())
					{
						GapEdit gapEdit = new GapEdit(getSillLayout(), getSillLayout().getHorizontalGapInPoints(), designatedDouble);
						if (gapEdit.execute())
						{
							RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(gapEdit.getPresentationName());
							compoundEdit.addEdit(gapEdit);
							compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compoundEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compoundEdit);
						}
					}
				}
			}
		);
		
		horizGutterField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0.0;
					
					return theLayout.getHorizontalGapInPoints();
				}

				public void setDouble(double designatedDouble)
				{
					if (getSillLayout()!=null && designatedDouble!=getSillLayout().getHorizontalGapInPoints())
					{
						GapEdit gapEdit = new GapEdit(getSillLayout(), designatedDouble, getSillLayout().getVerticalGapInPoints());
						if (gapEdit.execute())
						{
							RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(gapEdit.getPresentationName());
							compoundEdit.addEdit(gapEdit);
							compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compoundEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compoundEdit);
						}
					}
				}
			}
		);
		
		horizPageMarginField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0.0;
					
					return theLayout.getHorizontalPageMarginInPoints();
				}
	
				public void setDouble(double designatedDouble)
				{
					if (getSillLayout()!=null && designatedDouble!=getSillLayout().getHorizontalPageMarginInPoints())
					{
						PageMarginEdit pageMarginEdit = new PageMarginEdit(getDrawableDialog().getArtwork(), designatedDouble, getSillLayout().getVerticalPageMarginInPoints());
						if (pageMarginEdit.execute())
						{
							RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(pageMarginEdit.getPresentationName());
							compoundEdit.addEdit(pageMarginEdit);
							compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compoundEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compoundEdit);
						}
					}
				}
			}
		);
		
		vertPageMarginField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					Layout theLayout = getSillLayout();
					
					if (theLayout==null)
						return 0.0;
					
					return theLayout.getVerticalPageMarginInPoints();
				}
	
				public void setDouble(double designatedDouble)
				{
					if (getSillLayout()!=null && designatedDouble!=getSillLayout().getVerticalPageMarginInPoints())
					{
						PageMarginEdit pageMarginEdit = new PageMarginEdit(getDrawableDialog().getArtwork(), getSillLayout().getHorizontalPageMarginInPoints(), designatedDouble);
						if (pageMarginEdit.execute())
						{
							RenameableCompoundEdit compoundEdit = new RenameableCompoundEdit(pageMarginEdit.getPresentationName());
							compoundEdit.addEdit(pageMarginEdit);
							compoundEdit.addEdit(getDrawableDialog().getArtwork().getLayout().syncSill(getDrawableDialog().getArtwork().getSill()));
							compoundEdit.end();
							getDrawableDialog().announceEdit(LayoutPropsPanel.this, compoundEdit);
						}

					}
				}
			}
		);
		
		setVisible(true);
	}

	public void stateChanged()
	{
		// panelsField.stateChanged();
		panelsField.stateChanged();
		aspectRatioField.stateChanged();
		vertGutterField.stateChanged();
		horizGutterField.stateChanged();
		// layoutWidthField.stateChanged();
		// layoutHeightField.stateChanged();
		horizPageMarginField.stateChanged();
		vertPageMarginField.stateChanged();
		
		invalidate();
		repaint();
	}

	/**
	 * @see com.smithandtinkers.balloonist.AbstractPropsPanel#revealed()
	 */
	public void revealed()
	{
	}
}
