/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.layout.average.HeightAverager;
import com.smithandtinkers.layout.average.WidthAverager;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.edit.Scale2dEdit;
import com.smithandtinkers.layout.edit.Translate2dEdit;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;


/**
 * @author dhorlick
 */
public abstract class ResizeablePropsPanel extends NamedAbstractPropsPanel
{
	private LabeledQuantityField xField;
	private LabeledQuantityField yField;
	
	private LabeledQuantityField widthField;
	private LabeledQuantityField heightField;
	
	private final static Averager WIDTH_AVERAGER = new WidthAverager();
	private final static Averager HEIGHT_AVERAGER = new HeightAverager();
	
	public ResizeablePropsPanel()
	{
		super();
	}
	
	/**
	 * Called after construction. If you override this, please chain back up.
	 */
	protected void initialize()
	{
		super.initialize();
		
		Box posTwofer = Box.createHorizontalBox();
		posTwofer.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		final JTextField xJtf = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2);
		xField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("xLabel")), xJtf, DrawableDialog.UNITS);
		xField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		xField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		posTwofer.add(xField);
		
		final JTextField yJtf = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2);
		yField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("yLabel")), yJtf, DrawableDialog.UNITS);
		yField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		yField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		posTwofer.add(yField);
		
		add(posTwofer);
		
		Box dimTwofer = Box.createHorizontalBox();
		dimTwofer.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		final JTextField widthJtf = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2);
		widthField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("widthLabel")), widthJtf, DrawableDialog.UNITS);
		widthField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		widthField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		dimTwofer.add(widthField);
		
		final JTextField heightJtf = new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2);
		heightField = new LabeledQuantityField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("heightLabel")), heightJtf, DrawableDialog.UNITS);
		heightField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		heightField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		dimTwofer.add(heightField);
		
		add(dimTwofer);
		
		ItemListener il = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				ResizeablePropsPanel.this.stateChanged();
			}	
		};
		
		xField.getDropdown().addItemListener(il);
		yField.getDropdown().addItemListener(il);
		widthField.getDropdown().addItemListener(il);
		heightField.getDropdown().addItemListener(il);
		
		xField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;
					
					return getDrawableDialog().getSelection().averageX();
				}

				public void setDouble(double designatedDouble)
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null || getDouble()==designatedDouble)
						return;
						
					double avgX = getDrawableDialog().getSelection().averageX();
					double avgY = getDrawableDialog().getSelection().averageY();
					
					Translate2dEdit relocateEdit = new Translate2dEdit(avgX, avgY, designatedDouble, avgY);
					relocateEdit.setIsolate(true);
					relocateEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = relocateEdit.execute();
					
					getDrawableDialog().announceEdit(ResizeablePropsPanel.this, relocateEdit);
					
					// if (didSomething)
					// 	resyncAndRepaint(true);
				}
			}
		);
		
		yField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;

					return getDrawableDialog().getSelection().averageY();
				}

				public void setDouble(double designatedDouble)
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null || getDouble()==designatedDouble)
						return;
						
					double avgX = getDrawableDialog().getSelection().averageX();
					double avgY = getDrawableDialog().getSelection().averageY();
					
					Translate2dEdit relocateEdit = new Translate2dEdit(avgX, avgY, avgX, designatedDouble);
					relocateEdit.setIsolate(true);
					relocateEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = relocateEdit.execute();
					
					getDrawableDialog().announceEdit(ResizeablePropsPanel.this, relocateEdit);
					
					// if (didSomething)
					//	resyncAndRepaint(true);
				}		
			}
		);
		
		widthField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;
					
					return WIDTH_AVERAGER.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
				}

				public void setDouble(double designatedDouble)
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null || getDouble()==designatedDouble)
						return;
					
					if (designatedDouble<=0.0)
					{
						Toolkit.getDefaultToolkit().beep();
						stateChanged();
						return;
					}
					
					double averageWidth = WIDTH_AVERAGER.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
					
					Scale2dEdit scaleEdit = new Scale2dEdit(designatedDouble/averageWidth, 1.0);
					scaleEdit.setIsolate(true);
					scaleEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = scaleEdit.execute();
					
					getDrawableDialog().announceEdit(ResizeablePropsPanel.this, scaleEdit);
					
					// if (didSomething)
					// 	resyncAndRepaint(true);
				}
			}
		);
		
		heightField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;

					return HEIGHT_AVERAGER.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
				}
	
				public void setDouble(double designatedDouble)
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null || getDouble()==designatedDouble)
						return;
					
					if (designatedDouble<=0.0)
					{
						Toolkit.getDefaultToolkit().beep();
						stateChanged();
						return;
					}
					
					double averageHeight = HEIGHT_AVERAGER.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
					
					Scale2dEdit scaleEdit = new Scale2dEdit(1.0, designatedDouble/averageHeight);
					scaleEdit.setIsolate(true);
					scaleEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = scaleEdit.execute();
					
					getDrawableDialog().announceEdit(ResizeablePropsPanel.this, scaleEdit);
					
					// if (didSomething)
					// 	resyncAndRepaint(true);
				}
			}
		);
	}

	/**
	 * @see com.smithandtinkers.balloonist.AbstractPropsPanel#stateChanged()
	 */
	protected void stateChanged()
	{
		super.stateChanged();
		
		xField.stateChanged();
		yField.stateChanged();
		
		widthField.stateChanged();
		heightField.stateChanged();
	}
}
