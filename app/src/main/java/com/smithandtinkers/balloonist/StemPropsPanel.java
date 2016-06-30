/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.layout.edit.ChangeStemTypeEdit;
import com.smithandtinkers.layout.edit.LeadingEdgeEdit;
import com.smithandtinkers.layout.edit.RelocateEdit;
import com.smithandtinkers.layout.edit.RootWidthEdit;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;

import com.smithandtinkers.geom.AbstractStem;
import com.smithandtinkers.geom.Stem;
import com.smithandtinkers.gui.LabeledDropDown;
import com.smithandtinkers.gui.LabeledQuantityField;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.SilenceableComboBox;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;


/**
 * @author dhorlick
 */
public class StemPropsPanel extends NamedAbstractPropsPanel
{
	private SilenceableSlider leadingEdgeAngleSilenceableSlider;
	private Averager leadingEdgeAngleAverager;
	
	private SilenceableSlider rootWidthSilenceableSlider;
	private Averager rootWidthAverager;
	
	private LabeledQuantityField xField;
	private LabeledQuantityField yField;
	
	private JPanel typePanel;
	private CardLayout typePanelCardLayout;
	private SilenceableComboBox typeComboBox;
	
	private BubbledTypeBox bubbledTypeBox;
	
	private boolean startNewLeadingEdgeEdit;
	private boolean startNewRootWidthEdit;

	private StemCurvingBox stemCurvingBox;
	
	public StemPropsPanel()
	{
		super();
	}

	public void revealed()
	{
	}

	protected void initialize()
	{
		super.initialize();
		
		final Box topBox = Box.createVerticalBox();
		
		leadingEdgeAngleSilenceableSlider = new SilenceableSlider();
		leadingEdgeAngleSilenceableSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				startNewLeadingEdgeEdit = true;
			}
			
		});
		
		LabeledSlider leadingEdgeAngleSlider = new LabeledSlider(new JLabel(DrawableDialog.DIALOG_TEXT.getString("leadingEdgeAngleLabel")), null, leadingEdgeAngleSilenceableSlider);
		leadingEdgeAngleSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		
		leadingEdgeAngleAverager = new Averager(Stem.class)
		{
			public double tally(Object object)
			{
				Stem stem = (Stem) object;
				return stem.getLeadingEdgePositionAsPerimeterFraction();
			}
		};
		
		leadingEdgeAngleSilenceableSlider.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e)
			{	
				double dimensionlessLeadingEdgeFraction = Averager.determineSliderFraction(leadingEdgeAngleSilenceableSlider);
				LeadingEdgeEdit leadingEdgeEdit = new LeadingEdgeEdit(dimensionlessLeadingEdgeFraction);
				leadingEdgeEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				
				if (startNewLeadingEdgeEdit)
				{
					leadingEdgeEdit.setIsolate(true);
					startNewLeadingEdgeEdit = false;
				}
				
				if (leadingEdgeEdit.execute())
				{
					getDrawableDialog().announceEdit(this, leadingEdgeEdit);
					
					if (getChangeListener()!=null)
						getChangeListener().stateChanged(CHANGE_EVENT);  // TODO is this state change message still necessary?
				}
			}
		});
		
		topBox.add(leadingEdgeAngleSlider);
		
		rootWidthSilenceableSlider = new SilenceableSlider();
		rootWidthSilenceableSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				startNewRootWidthEdit = true;
			}
			
		});
		LabeledSlider rootWidthSlider = new LabeledSlider(new JLabel(getDrawableDialog().DIALOG_TEXT.getString("rootWidthLabel")), null, rootWidthSilenceableSlider);
		rootWidthSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		
		rootWidthAverager = new Averager(Stem.class)
		{
			public double tally(Object object)
			{
				Stem stem = (Stem) object;
				return stem.getRootWidthInPoints();
			}
		};
		
		rootWidthSilenceableSlider.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e)
			{
				double rootWidthInPoints = Averager.mapValueFromSlider(rootWidthSilenceableSlider, AbstractStem.minAllowableRootWidthInPoints(), AbstractStem.maxAllowableRootWidthInPoints());
				
				RootWidthEdit rootWidthEdit = new RootWidthEdit(rootWidthInPoints);
				rootWidthEdit.addSelectablesFrom(getDrawableDialog().getSelection());
				
				if (startNewRootWidthEdit)
				{
					rootWidthEdit.setIsolate(true);
					startNewRootWidthEdit = false;
				}
				
				if (rootWidthEdit.execute())
				{
					getDrawableDialog().announceEdit(this, rootWidthEdit);
					
					if (getChangeListener()!=null)
						getChangeListener().stateChanged(CHANGE_EVENT);  // TODO is this state change message still necessary?
				}
			}
		});
		
		Box wrappedRootWidthSlider = Box.createHorizontalBox();
		wrappedRootWidthSlider.add(rootWidthSlider);
		wrappedRootWidthSlider.add(Box.createHorizontalGlue());
		wrappedRootWidthSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
		wrappedRootWidthSlider.setAlignmentY(Component.CENTER_ALIGNMENT);
		topBox.add(wrappedRootWidthSlider);
		
		Box focusBox = Box.createHorizontalBox();
		focusBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		focusBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		xField = new LabeledQuantityField(new JLabel(getDrawableDialog().DIALOG_TEXT.getString("xLabel")), new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2), DrawableDialog.UNITS);
		xField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		xField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		
		yField = new LabeledQuantityField(new JLabel(getDrawableDialog().DIALOG_TEXT.getString("yLabel")), new JTextField("0", DrawableDialog.TEXT_FIELD_COLUMNS+2), DrawableDialog.UNITS);
		yField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		yField.setFiguresAfterDecimalPoint(DrawableDialog.FIGURES_AFTER_DECIMAL_POINT);
		
		ItemListener il = new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				StemPropsPanel.this.stateChanged();
			}	
		};
		
		final Averager xFocusAverager = new Averager(Stem.class)
		{
			public double tally(Object object)
			{
				Stem stem = (Stem) object;
				return stem.getFocus().getX();
			}
		};
		
		final Averager yFocusAverager = new Averager(Stem.class)
		{
			public double tally(Object object)
			{
				Stem stem = (Stem) object;
				return stem.getFocus().getY();
			}
		};
		
		xField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;
					
					return xFocusAverager.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
				}

				public void setDouble(double designatedDouble)
				{
					RelocateEdit relocateEdit = new RelocateEdit(designatedDouble, false, PerimeterSegment.HOT_SPOT);
					relocateEdit.setIsolate(true);
					relocateEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = relocateEdit.execute();
					
					getDrawableDialog().announceEdit(StemPropsPanel.this, relocateEdit);
					
					if (didSomething)
						fireChangeEvent();
				}
			}
		);

		yField.setModel(new PrimitiveDoubleHolder()
			{
				public double getDouble()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return 0.0;
					
					return yFocusAverager.average(getDrawableDialog().getSelection().iterateOverSelectedItems());
				}

				public void setDouble(double designatedDouble)
				{
					RelocateEdit relocateEdit = new RelocateEdit(designatedDouble, true, PerimeterSegment.HOT_SPOT);
					relocateEdit.setIsolate(true);
					relocateEdit.addSelectablesFrom(getDrawableDialog().getSelection());
					boolean didSomething = relocateEdit.execute();
					
					getDrawableDialog().announceEdit(StemPropsPanel.this, relocateEdit);
					
					if (didSomething)
						fireChangeEvent(); // TODO is this repaint op still necessary?
				}
			}
		);
		
		xField.getDropdown().addItemListener(il);
		yField.getDropdown().addItemListener(il);
		
		focusBox.add(xField);
		focusBox.add(yField);
		
		focusBox.setBorder(BorderFactory.createTitledBorder(getDrawableDialog().DIALOG_TEXT.getString("focusLabel")));
		topBox.add(focusBox);
		
		final Box typeAndCurveBox = Box.createHorizontalBox();
		
		Box typeBox = Box.createVerticalBox();
		Box typeBoxLine1 = Box.createHorizontalBox();
		typeComboBox = new SilenceableComboBox(AbstractStem.getWidgetedTypeIndex());
		
		typeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// System.out.println("Type changed "+e.getSource());
				
				AbstractStem.Type newType = (AbstractStem.Type) typeComboBox.getSelectedItem();
				ChangeStemTypeEdit changeStemTypeEdit = new ChangeStemTypeEdit(newType);
				changeStemTypeEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
				
				if (changeStemTypeEdit.execute())
				{
					getDrawableDialog().announceEdit(StemPropsPanel.this, changeStemTypeEdit);
					stateChanged(); // since there would be no new selection event to trigger a shuffle
				}
			}
		});
		
		LabeledDropDown labeledTypeDropDown = new LabeledDropDown(new JLabel(DrawableDialog.DIALOG_TEXT.getString("typeLabel")), typeComboBox);
		labeledTypeDropDown.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		typeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		typeBoxLine1.add(labeledTypeDropDown);
		// typeBoxLine1.add(Box.createHorizontalGlue());
		typeBoxLine1.setAlignmentX(Component.LEFT_ALIGNMENT);
		typeBox.add(typeBoxLine1);
		
		typePanel = new JPanel();
		typePanel.setBorder(BorderFactory.createTitledBorder(DrawableDialog.DIALOG_TEXT.getString("typeOptionsLabel")));
		typePanelCardLayout = new CardLayout();
		typePanel.setLayout(typePanelCardLayout);
		typePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		typePanel.add(AbstractStem.ICICLE_TYPE.getCode(), buildEmptyTypeBox());
		typePanel.add(AbstractStem.LOLLIPOP_TYPE.getCode(), buildEmptyTypeBox());
		
		bubbledTypeBox = new BubbledTypeBox(this);
		typePanel.add(AbstractStem.BUBBLED_TYPE.getCode(), bubbledTypeBox);
		
		stemCurvingBox = new StemCurvingBox(this);
		
		Box typeBoxLine2 = Box.createHorizontalBox();
		// typeBoxLine2.add(Box.createHorizontalStrut(42));
		typeBoxLine2.add(typePanel);
		typeBoxLine2.setAlignmentX(Component.LEFT_ALIGNMENT);
		typeBox.add(typeBoxLine2);
		
		typeAndCurveBox.add(typeBox);
		typeAndCurveBox.add(Box.createHorizontalStrut(4));
		typeAndCurveBox.add(new JSeparator(JSeparator.VERTICAL));
		typeAndCurveBox.add(Box.createHorizontalStrut(2));
		typeAndCurveBox.add(stemCurvingBox); 
		
		topBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		typeAndCurveBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		add(topBox);
		add(typeAndCurveBox);
	}

	protected void stateChanged()
	{
		super.stateChanged();
		
		leadingEdgeAngleSilenceableSlider.setSilent(true);
		leadingEdgeAngleAverager.averageTo(getDrawableDialog().getSelection().iterateOverSelectedItems(), leadingEdgeAngleSilenceableSlider, 0.0, 1.0);
		leadingEdgeAngleSilenceableSlider.setSilent(false);
		
		rootWidthSilenceableSlider.setSilent(true);
		rootWidthAverager.averageTo(getDrawableDialog().getSelection().iterateOverSelectedItems(), rootWidthSilenceableSlider, AbstractStem.minAllowableRootWidthInPoints(), AbstractStem.maxAllowableRootWidthInPoints()); 
		rootWidthSilenceableSlider.setSilent(false);
		
		xField.stateChanged();
		yField.stateChanged();
		
		AbstractStem firstSelectedStem = findFirstSelectedStem();
		
		if (firstSelectedStem!=null)
		{
			typeComboBox.setSilent(true);
			typeComboBox.setSelectedItem(firstSelectedStem.getType());
			typeComboBox.setSilent(false);

			// System.out.println("showing: "+firstSelectedStem.getType().getCode());
			typePanelCardLayout.show(typePanel, firstSelectedStem.getType().getCode());
			
			if (firstSelectedStem.getType()==AbstractStem.BUBBLED_TYPE)
			{
				bubbledTypeBox.getBubbleDensitySlider().setSilent(true);
				Averager.mapValueToSlider(
						firstSelectedStem.determineBubbleDensity(), 
						bubbledTypeBox.getBubbleDensitySlider(), 
						AbstractStem.MINIMUM_BUBBLE_DENSITY, AbstractStem.MAXIMUM_BUBBLE_DENSITY);
				bubbledTypeBox.getBubbleDensitySlider().setSilent(false);
			}
			
			stemCurvingBox.getFocusInclinationSlider().setSilent(true);
			Averager.mapValueToSlider(
					firstSelectedStem.getFocusStemInclinationInRadians(), 
					stemCurvingBox.getFocusInclinationSlider(), 
					0.0, Math.PI/2.0);
			stemCurvingBox.getFocusInclinationSlider().setSilent(false);

			stemCurvingBox.getRootInclinationSlider().setSilent(true);
			Averager.mapValueToSlider(
					firstSelectedStem.getRootStemInclinationInRadians(), 
					stemCurvingBox.getRootInclinationSlider(), 
					0.0, Math.PI/2.0);
			stemCurvingBox.getRootInclinationSlider().setSilent(false);

			stemCurvingBox.getRigiditySlider().setSilent(true);
			Averager.mapValueToSlider(
					1.0 - firstSelectedStem.getBendiness(), 
					stemCurvingBox.getRigiditySlider(), 
					0.0, 1.0);
			stemCurvingBox.getRigiditySlider().setSilent(false);
			
			typePanel.invalidate();
		}
	}
	
	public AbstractStem findFirstSelectedStem()
	{
		if (getDrawableDialog().getSelection()==null)
			return null;
		
		Object found = getDrawableDialog().getSelection().findFirst(AbstractStem.class);
		
		if (found!=null)
			return (AbstractStem) found;
		
		return null;
	}
	
	private static Box buildEmptyTypeBox()
	{
		Box emptyTypeBox = Box.createVerticalBox();
		JLabel noneLabel = new JLabel(DrawableDialog.DIALOG_TEXT.getString("noneLabel"));
		noneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		emptyTypeBox.add(Box.createVerticalGlue());
		emptyTypeBox.add(noneLabel);
		emptyTypeBox.add(Box.createVerticalGlue());
		
		return emptyTypeBox;
	}
}
