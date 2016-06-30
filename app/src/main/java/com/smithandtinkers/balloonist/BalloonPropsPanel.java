/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.BalloonEngineState;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Font;

import java.util.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;

import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.gui.*;
import com.smithandtinkers.layout.Averager;
import com.smithandtinkers.layout.Crowd;
import com.smithandtinkers.layout.edit.LineThicknessEdit;
import com.smithandtinkers.layout.edit.MarginEdit;
import com.smithandtinkers.layout.edit.OrientationEdit;
import com.smithandtinkers.layout.TextShape;
import com.smithandtinkers.layout.average.LineThicknessAverager;
import com.smithandtinkers.layout.average.MarginAverager;
import com.smithandtinkers.mvc.PrimitiveBooleanHolder;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;


/**
 * @author dhorlick
 */
public class BalloonPropsPanel extends ResizeablePropsPanel
{
	private LabeledDropDown fontDropdown;
	private LabeledDropDown fontSizeDropdown;
	private JTextPane jText;
	private ColorPicker colorPicker;
	private SilenceableSlider marginSilenceableSlider;
	private Averager marginAverager;
	private LineThicknessControl lineThicknessLabeledQuantityField;
	private WiredCheckbox verticalCheckBox;
	private boolean startNewMarginEdit;
	
	private final ChangeEvent CHANGE_EVENT = new ChangeEvent(this);
	
	protected void initialize()
	{
		super.initialize();
		
		final FontFamilyComboBox fontComboBox = new FontFamilyComboBox();
		fontComboBox.setFocusable(false);
		fontComboBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent aevent)
			{
				SimpleAttributeSet sset = new SimpleAttributeSet();
				StyleConstants.setFontFamily(sset, String.valueOf(fontComboBox.getSelectedItem()));
				jText.setCharacterAttributes(sset, false);
			}
		}
		);
		fontDropdown = new LabeledDropDown(new JLabel(DrawableDialog.DIALOG_TEXT.getString("fontLabel")), fontComboBox);
		fontDropdown.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		
		final FontSizeComboBox fontSizeComboBox = new FontSizeComboBox();
		
		fontSizeDropdown = new LabeledDropDown(new JLabel(DrawableDialog.DIALOG_TEXT.getString("fontSizeLabel")), fontSizeComboBox);
		fontSizeDropdown.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		colorPicker = new ColorPicker();
		SimpleLabeledComponent labeledColorPicker = new SimpleLabeledComponent(new JLabel(DrawableDialog.DIALOG_TEXT.getString("colorsLabel")), colorPicker);
		labeledColorPicker.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		labeledColorPicker.add(Box.createHorizontalGlue());
		
		jText = new JTextPane();
		
		final Font DEFAULT_FONT = BalloonEngineState.getInstance().getDefaultFont();
		
		jText.setFont(DEFAULT_FONT);
		SimpleAttributeSet sset = new SimpleAttributeSet();
		StyleConstants.setFontFamily(sset, DEFAULT_FONT.getFamily());
		StyleConstants.setFontSize(sset, DEFAULT_FONT.getSize());
		jText.setCharacterAttributes(sset, true);
		
		// jText.setPreferredSize( new Dimension( 180, 50 ) );
		
		Box twofer = Box.createHorizontalBox();
		
		twofer.add(fontDropdown);
		twofer.add(fontSizeDropdown);
		twofer.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(twofer);
		
		Box threefer = Box.createHorizontalBox();
		colorPicker.setMaximumSize(new Dimension(28, 22));
		colorPicker.setAlignmentX(Component.LEFT_ALIGNMENT);
		threefer.add(labeledColorPicker);
		
		threefer.add(Box.createHorizontalGlue());
		
		lineThicknessLabeledQuantityField = new LineThicknessControl();
		lineThicknessLabeledQuantityField.setModel(new PrimitiveDoubleHolder()
		{
			public double getDouble()
			{
				if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
				{
					return 0.0f;
				}
				
				LineThicknessAverager averager = new LineThicknessAverager();
				return averager.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
			}

			public void setDouble(double designatedLineThicknessInPoints)
			{
				if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
					return;
				
				LineThicknessEdit lineThicknessEdit = new LineThicknessEdit(designatedLineThicknessInPoints);
				lineThicknessEdit.addItemsFrom(getDrawableDialog().getSelection().getSelectedItems());
				
				if (lineThicknessEdit.execute())
				{
					getDrawableDialog().announceEdit(BalloonPropsPanel.this, lineThicknessEdit);
				}
			}
		});
		threefer.add(lineThicknessLabeledQuantityField);
		
		threefer.add(Box.createHorizontalGlue());
		
		verticalCheckBox = new WiredCheckbox(getDrawableDialog().DIALOG_TEXT.getString("verticalLabel"));
		verticalCheckBox.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		verticalCheckBox.setBooleanModel(new PrimitiveBooleanHolder()
		{
			public void setBoolean(boolean designatedBoolean)
			{
				// System.out.println("creating an orientation edit...");
				
				OrientationEdit orientationEdit = new OrientationEdit(designatedBoolean);
				
				orientationEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
				if (orientationEdit.execute())
				{
					getDrawableDialog().announceEdit(BalloonPropsPanel.this, orientationEdit);
				}
			}

			public boolean getBoolean()
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getSelection()!=null)
				{
					TextShape textShape = null;
					Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();

					while (walk.hasNext() && textShape==null)
					{
						Object item = walk.next();
						if (item instanceof TextShape)
							textShape = (TextShape) item;
					}

					if (textShape != null)
						return textShape.isVertical();
				}
				
				return false; // actually "nothing"
			}
		});
		verticalCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		verticalCheckBox.setFont(labeledColorPicker.getLabel().getFont());
		threefer.add(verticalCheckBox);
		threefer.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(threefer);
		
		marginSilenceableSlider = new SilenceableSlider(0, (int)Marginal.MAXIMUM_ALLOWABLE_MARGIN);
		marginSilenceableSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				Iterator walk = getDrawableDialog().getSelection().iterateOverSelectedItems();
				while (walk.hasNext())
				{
					Crowd.reLayoutCrowdsOf(walk.next());
				}
				
				startNewMarginEdit = true;
			}
		});
		LabeledSlider marginLabeledSlider = new LabeledSlider(new JLabel(DrawableDialog.DIALOG_TEXT.getString("textMarginLabel")), null, marginSilenceableSlider);
		marginLabeledSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(marginLabeledSlider);
		
		marginSilenceableSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent e)
			{
				MarginEdit marginEdit = new MarginEdit(marginSilenceableSlider.getValue());
				marginEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				
				if (startNewMarginEdit)
				{
					marginEdit.setIsolate(true);
					startNewMarginEdit = false;
				}
				
				if (marginEdit.execute())
				{
					getDrawableDialog().announceEdit(this, marginEdit);
					
					if (getChangeListener()!=null)
						getChangeListener().stateChanged(CHANGE_EVENT);
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(jText);
		scrollPane.setPreferredSize(new Dimension(500, 400)); // something appropriately big
		add(scrollPane);
		
		fontSizeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// System.out.println("aevent="+e);
				// System.out.println("aevent.getSource()="+e.getSource());
				
				SimpleAttributeSet sset = new SimpleAttributeSet();
				if (fontSizeComboBox.getSelectedItem() instanceof Integer)
				{
					Integer fontSize = (Integer) fontSizeComboBox.getSelectedItem();
					
					// System.out.println("selection: Integer! "+fontSize);
					
					if (fontSize!=null)
						StyleConstants.setFontSize(sset, fontSize.intValue());
				}
				else if (fontSizeComboBox.getSelectedItem() instanceof String)
				{
					String fontSize = (String) fontSizeComboBox.getSelectedItem();

					try
					{
						float fontSizeAsFloat = Float.parseFloat(fontSize);
						StyleConstants.setFontSize(sset, (int)fontSizeAsFloat);
					}
					catch (NumberFormatException exception)
					{
						return;
					}
				}
				
				jText.setCharacterAttributes(sset, false);
			}
		});
		
		jText.addCaretListener(new CaretListener() 
			{
				public void caretUpdate(CaretEvent e)
				{
					// System.out.println("received caret event @ "+System.currentTimeMillis());

					AttributeSet charAttr = jText.getCharacterAttributes();
					
					if (charAttr==null || e.getDot()!=e.getMark()) // TODO see if we can produce some kind of summary for selections
						return;
					
					int fontSize = StyleConstants.getFontSize(charAttr);
					
					// System.out.println("caret update! new logical font size: "+fontSize);
					
					Integer fontSizeAsObjectInteger = new Integer(fontSize);
					
					if (fontSizeComboBox.getSelectedItem()==null || !fontSizeComboBox.getSelectedItem().equals(fontSizeAsObjectInteger))
					{
						fontSizeComboBox.setFontSizeSelection(fontSize);
					}
					
					String fontName = StyleConstants.getFontFamily(charAttr);
					
					if (fontComboBox!=null) // and it shouldn't be
					{
						if (fontComboBox.getSelectedItem()==null || !fontComboBox.getSelectedItem().equals(fontName))
						{
							// System.out.println("changing font name to: "+fontName + " at "+System.currentTimeMillis());
							fontComboBox.setFontFamilySelection(fontName);
						}
					}
				}
			}
		);
		
		jText.addFocusListener(new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getChangeListener()!=null)
					getDrawableDialog().getChangeListener().stateChanged(CHANGE_EVENT);
			}

			public void focusGained(FocusEvent e)
			{
				if (getDrawableDialog()!=null && getDrawableDialog().getChangeListener()!=null)
					getDrawableDialog().getChangeListener().stateChanged(CHANGE_EVENT);
			}
		});
		
		final FocusListener textSelectionRevealingFocusListener = new FocusListener() {

			public void focusGained(FocusEvent e)
			{
				jText.getCaret().setSelectionVisible(true);
			}
	
			public void focusLost(FocusEvent e)
			{
			}
		}; 
		
		fontComboBox.addFocusListener(textSelectionRevealingFocusListener);
		fontSizeComboBox.addFocusListener(textSelectionRevealingFocusListener);
		
		// Font Size Combo Box is editable, so we need to listen for its editor receiving focus too.
		
		fontSizeComboBox.getEditor().getEditorComponent().addFocusListener(textSelectionRevealingFocusListener);
		
		colorPicker.setColorfulModel(generateColorfulModel());
		
		marginAverager = new MarginAverager();
	}
	
	public void stateChanged()
	{
		super.stateChanged();
		fontSizeDropdown.stateChanged();
		
		marginSilenceableSlider.setSilent(true);
		marginAverager.averageTo(getDrawableDialog().getSelection().iterateOverSelectedItems(), marginSilenceableSlider, 0, Marginal.MAXIMUM_ALLOWABLE_MARGIN); 
		marginSilenceableSlider.setSilent(false);
		
		colorPicker.stateChanged();
		lineThicknessLabeledQuantityField.stateChanged();
		verticalCheckBox.stateChanged();
	}
	
	/**
	 * Called when this object is revealed. Hooks up delegates with appropriate models from selection.
	 */
	public void revealed()
	{
		super.revealed();
		
		TextShape tshape = findTextShape();
		if (tshape!=null)
		{
			if (tshape.getText()!=jText.getDocument())
				jText.setDocument(tshape.getText()); // TODO test Undoings after re-select
		}
	}
	
	protected TextShape findTextShape()
	{
		if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null || getDrawableDialog().getSelection().getSelectedItemsCount()==0)
			return null;
		
		if (getDrawableDialog().getSelection().getSelectedItem(0) instanceof TextShape)
		{
			TextShape tshape = (TextShape) getDrawableDialog().getSelection().getSelectedItem(0);
			return tshape;
		}
		
		return null;
	}
	
	public JTextPane getJText()
	{
		return jText;
	}
	
	public static class LineThicknessControl extends TwiceLabeledQuantityField
	{
		public LineThicknessControl()
		{
			super(
					new JLabel(DrawableDialog.DIALOG_TEXT.getString("thicknessLabel")),
					new JTextField(),
					new JLabel(DrawableDialog.POINTS.getName()));

			getField().setColumns(5);
			setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
			setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
			setMinimumValue(new Double(0.2));
			setMaximumValue(new Double(5.0));
		}
	}
}
