/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.geom.AbstractSuperEllipse;
import com.smithandtinkers.geom.Marginal;
import com.smithandtinkers.geom.SuperEllipsePerch;
import com.smithandtinkers.gui.ColorPicker;
import com.smithandtinkers.gui.FontFamilyComboBox;
import com.smithandtinkers.gui.FontSizeComboBox;
import com.smithandtinkers.gui.LabeledDropDown;
import com.smithandtinkers.gui.LabeledSlider;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.Artwork;
import com.smithandtinkers.layout.Balloon;
import com.smithandtinkers.layout.BalloonistPreferences;
import com.smithandtinkers.layout.Crowd;
import com.smithandtinkers.mvc.PrimitiveDoubleHolder;
import com.smithandtinkers.util.PlatformFriend;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * A panel to help the user administer application preferences.
 *
 * @author dhorlick
 */
public class BalloonistPrefsPanel extends JPanel
{
	private FontFamilyComboBox fontFamilyDropdown;
	private FontSizeComboBox fontSizeDropdown;
	
	private JComboBox pdfPreserveDropdown;
	private JCheckBox ignoreKerningCheckbox;
		
	private Container feedbackPanel;
	
	private Artwork artwork = new Artwork();
	private SuperEllipsePerch superEllipsePerch = new SuperEllipsePerch();
	private Balloon balloon = new Balloon();

	private final ArtworkPane artworkPane = new ArtworkPane();
	
	private static final int PDF_PRESERVE_ACCURACY_INDEX = 1; // (#2)

	private boolean editableTextExportPossible;
	
	public BalloonistPrefsPanel(BalloonistPreferences designatedBalloonistPreferences)
	{
		editableTextExportPossible = artworkPane.determineWhetherEditableTextExportPossible();
		feedbackPanel = buildFeedbackPanel();
		final Container textPanel = buildTextPanel(designatedBalloonistPreferences.getDefaultFont(), designatedBalloonistPreferences.isPreserveAccuracyOverEditability(), designatedBalloonistPreferences.isIgnoreFontKerning());
		final Container shapePanel = buildShapePanel(designatedBalloonistPreferences);
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add(textPanel, PossibleAction.DIALOG_TEXT.getString("textTabLabel"));
		tabbedPane.add(shapePanel, PossibleAction.DIALOG_TEXT.getString("shapeTabLabel"));
		
		final Box compositionBox = Box.createVerticalBox();
		compositionBox.add(feedbackPanel);
		compositionBox.add(Box.createVerticalStrut(10));
		compositionBox.add(tabbedPane);
		
		add(compositionBox);
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				updateFeedbackDimensions();
			}
		});
	}
	
	public void nicelyBorder(JComponent component)
	{
		Border emptied = BorderFactory.createEmptyBorder(6, 6, 6, 6);
		Border raised = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		Border compounded = BorderFactory.createCompoundBorder(raised, emptied);
		
		Border recompounded = BorderFactory.createCompoundBorder(emptied, compounded);
		
		component.setBorder(recompounded);
	}
	
	public Font getDefaultFontSelection()
	{
		return balloon.getStraw().getFirstFont();
	}
	
	/**
	 * Warning: be careful not to confuse this with {@link javax.swing.JPanel#setFont} !
	 */
	public void setDefaultFont(Font designatedDefaultFont)
	{
		if (balloon!=null)
			balloon.getStraw().setFont(designatedDefaultFont);
		
		if (fontFamilyDropdown!=null)
			fontFamilyDropdown.setFontFamilySelection(designatedDefaultFont.getFamily());
		
		if (fontSizeDropdown!=null)	
			fontSizeDropdown.setFontSizeSelection(designatedDefaultFont.getSize());
	}
	
	private void updateSampleFont()
	{
		final Font NEW_FONT = new Font(String.valueOf(fontFamilyDropdown.getFontFamilySelection()), Font.PLAIN, fontSizeDropdown.getFontSizeSelection() );
		// sample.setFont(NEW_FONT);
		balloon.getStraw().setFont(NEW_FONT);
	}
	
	public boolean getPreserveAccuracySelection()
	{
		if (pdfPreserveDropdown==null)
			return true;
		
		return (pdfPreserveDropdown.getSelectedIndex()==PDF_PRESERVE_ACCURACY_INDEX);
	}

	private Container buildFeedbackPanel()
	{
		final Box shapePanel = Box.createVerticalBox();
		artworkPane.setArtwork(getArtwork());
		// ap.getArtwork().setEnclosure(new Dimension(getWidth(), getHeight())); // ?
		balloon.setText(PossibleAction.DIALOG_TEXT.getString("nonsensePhrase"));
		balloon.setLayedOut(false);
		balloon.add(superEllipsePerch);
		
		Crowd crowd = new Crowd();
		crowd.add(balloon);
		artworkPane.getArtwork().getSill().add(crowd);
		
		shapePanel.add(artworkPane);
		
		artworkPane.getArtwork().setEnclosure(new Dimension(580, 120));
		balloon.setWidth(240.0);
		balloon.setHeight(80.0);
		balloon.setLocation(artworkPane.getArtwork().getEnclosure().getWidth()/2, artworkPane.getArtwork().getEnclosure().getHeight()/2);
		
		artworkPane.getSelection().setDirty(true, this);
		artworkPane.getSelection().registerSelection(balloon);
		artworkPane.getSelection().setDirty(false, this);
				
		return shapePanel;
	}
	
	private void updateFeedbackDimensions()
	{
		// artworkPane.getArtwork().setEnclosure(new Dimension(550, 200));
		
		getArtwork().setEnclosure(new Dimension(feedbackPanel.getWidth()-2, feedbackPanel.getHeight()-2));
		balloon.setLocation(artworkPane.getArtwork().getEnclosure().getWidth()/2, artworkPane.getArtwork().getEnclosure().getHeight()/2);
	}
	
	private Box buildShapePanel(BalloonistPreferences designatedBalloonistPreferences)
	{
		final Box shapePanel = Box.createVerticalBox();
		// shapePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		final Box heinAndColorfulBar = Box.createHorizontalBox();
		
		final SuperEllipsePerchPropsPanel.HeinParameterSlider heinParamSlider = new SuperEllipsePerchPropsPanel.HeinParameterSlider();
		// heinParamSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
		superEllipsePerch.setHeinParameter(designatedBalloonistPreferences.getDefaultHeinParameter());
		balloon.setLineThickness(designatedBalloonistPreferences.getDefaultLineThicknessInPoints());
		balloon.setInnerMarginInPoints(designatedBalloonistPreferences.getDefaultInnerMarginInPoints());
		
		if (designatedBalloonistPreferences.getDefaultColorfulness()!=null)
		{
			balloon.setFillColor(designatedBalloonistPreferences.getDefaultColorfulness().getFillColor());
			balloon.setOutlineColor(designatedBalloonistPreferences.getDefaultColorfulness().getOutlineColor());
		}
		
		heinParamSlider.associateHeinParameter(superEllipsePerch.getHeinParameter());
		
		final Box heinSubpanel = Box.createHorizontalBox();
		Action resetHeinParameter = new AbstractAction(PossibleAction.DIALOG_TEXT.getString("resetLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				superEllipsePerch.setHeinParameter(0.6);
				heinParamSlider.associateHeinParameter(superEllipsePerch.getHeinParameter());
				// invalidate();
				// revalidate();
				// repaint();
			}
		};
		final JButton resetHeinButton = new JButton(resetHeinParameter);
		heinSubpanel.add(heinParamSlider);
		heinSubpanel.add(resetHeinButton);
		nicelyBorder(heinSubpanel);
		
		heinAndColorfulBar.add(heinSubpanel);
		
		heinAndColorfulBar.add(Box.createHorizontalStrut(15));
		final ColorPicker picker = new ColorPicker();
		picker.setColorfulModel(balloon);
		heinAndColorfulBar.add(picker);
		
		shapePanel.add(heinAndColorfulBar);
		heinParamSlider.getSlider().addChangeListener(new ChangeListener()
			{
				public void stateChanged(ChangeEvent e)
				{
					double newHeinParam = heinParamSlider.associatedHeinParameter();
					
					if (AbstractSuperEllipse.wouldProspectiveHeinParameterBeAllowed(newHeinParam))
					{
						superEllipsePerch.setHeinParameter(newHeinParam);
					}
				}
			}
		);
                
		final SilenceableSlider marginSilenceableSlider = new SilenceableSlider(0, (int)Marginal.MAXIMUM_ALLOWABLE_MARGIN);

		final LabeledSlider marginLabeledSlider = new LabeledSlider(new JLabel(DrawableDialog.DIALOG_TEXT.getString("textMarginLabel")), null, marginSilenceableSlider);
		marginLabeledSlider.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		marginSilenceableSlider.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent changeEvent)
				{
					balloon.setMarginInPoints(marginSilenceableSlider.getValue());
					balloon.setLayedOut(false);
				}

			}
		);	
		
		marginSilenceableSlider.setValue((int)balloon.getInnerMarginInPoints());
		
		final Box marginAndThicknessBar = Box.createHorizontalBox();
		
		final Box marginSubpanel = Box.createHorizontalBox();
		marginSubpanel.add(marginLabeledSlider);
		
		final Action resetMargin = new AbstractAction(PossibleAction.DIALOG_TEXT.getString("resetLabel"))
		{
			public void actionPerformed(ActionEvent event)
            {
	            balloon.setInnerMarginInPoints(Marginal.DEFAULT_MARGIN);
	            marginSilenceableSlider.setValue((int)balloon.getInnerMarginInPoints());
	            // invalidate();
				// revalidate();
				// repaint();
            }			
		};
		final JButton resetMarginButton = new JButton(resetMargin);
		marginSubpanel.add(resetMarginButton);
		
		nicelyBorder(marginSubpanel);
		marginAndThicknessBar.add(marginSubpanel);
		
		final BalloonPropsPanel.LineThicknessControl lineThicknessControl = new BalloonPropsPanel.LineThicknessControl();
		lineThicknessControl.setModel(new PrimitiveDoubleHolder()
		{
			public double getDouble()
			{
				return balloon.getLineThickness();
			}
			
			public void setDouble(double designatedValue)
			{
				balloon.setLineThickness(designatedValue);
			}
		});
		
		marginAndThicknessBar.add(Box.createHorizontalStrut(10));
		marginAndThicknessBar.add(lineThicknessControl);
		shapePanel.add(Box.createVerticalStrut(10));
		shapePanel.add(marginAndThicknessBar);
		shapePanel.add(Box.createVerticalGlue());
                
		return shapePanel;
	}

	private Box buildTextPanel(Font designatedDefaultFont, boolean designatedPreserveAccuracy, boolean designatedIgnoreFontKerning)
	{
		final Box textPanel = Box.createVerticalBox();
		// textPanel.setBackground((Color)UIManager.get("Panel.background"));
		// textPanel.setBackground(null);
		
		final Box fontPanel = Box.createVerticalBox();
		final ActionListener UPDATE_SAMPLE_FONT_ACTION = new ActionListener()
		{
			public void actionPerformed(ActionEvent aevent)
			{
				updateSampleFont();
			}
		};
		
		fontFamilyDropdown = new FontFamilyComboBox();
		fontSizeDropdown = new FontSizeComboBox();
		
		setDefaultFont(designatedDefaultFont);
		
		fontSizeDropdown.addActionListener(UPDATE_SAMPLE_FONT_ACTION);
		fontFamilyDropdown.addActionListener(UPDATE_SAMPLE_FONT_ACTION);
		
		// JPanel subpanel = new JPanel();
		Box subpanel = Box.createHorizontalBox();
		// subpanel.setBackground(null);
		
		final String FONT_FAMILY_LABEL = PossibleAction.DIALOG_TEXT.getString("defaultLabel") + " " + PossibleAction.DIALOG_TEXT.getString("fontLabel");
		final String FONT_SIZE_LABEL = PossibleAction.DIALOG_TEXT.getString("defaultLabel") + " " + PossibleAction.DIALOG_TEXT.getString("fontSizeLabel");
		
		LabeledDropDown labeledFontFamilyDropdown = new LabeledDropDown(new JLabel(FONT_FAMILY_LABEL), fontFamilyDropdown);
		subpanel.add(labeledFontFamilyDropdown);
		
		LabeledDropDown laneledFontSizeDropdown = new LabeledDropDown(new JLabel(FONT_SIZE_LABEL), fontSizeDropdown);
		subpanel.add(laneledFontSizeDropdown);
		
		Action resetFontToPlatformDefault = new AbstractAction(PossibleAction.DIALOG_TEXT.getString("resetLabel"))
		{
			public void actionPerformed(ActionEvent e)
			{
				BalloonistPrefsPanel.this.setDefaultFont(
						BalloonistPreferences.determineSanSerifFont());
				updateSampleFont();
			}
		};
		JButton resetButton = new JButton(resetFontToPlatformDefault);
		subpanel.add(resetButton);
		
		fontPanel.add(subpanel, BorderLayout.SOUTH);
		fontPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		nicelyBorder(fontPanel);
		textPanel.add(fontPanel);
		
		final Box pdfPanel = Box.createVerticalBox();
		nicelyBorder(pdfPanel);
		
		if (isEditableTextExportPossible())
		{
			JLabel preserveLabel = new JLabel(PossibleAction.DIALOG_TEXT.getString("pdfPreserveLabel"));
			final String editabilityTextLabel = PossibleAction.DIALOG_TEXT.getString("pdfPreserveEditabilityLabel");
			final String accuracyTextLabel = PossibleAction.DIALOG_TEXT.getString("pdfPreserveAccuracyLabel");

			pdfPreserveDropdown = new JComboBox(new String [] {editabilityTextLabel, accuracyTextLabel});

			if (designatedPreserveAccuracy)
				pdfPreserveDropdown.setSelectedIndex(PDF_PRESERVE_ACCURACY_INDEX);

			LabeledDropDown labeledPreserveDropdown = new LabeledDropDown(preserveLabel, pdfPreserveDropdown);
			labeledPreserveDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
			pdfPanel.add(labeledPreserveDropdown);
		}
		
		if (PlatformFriend.RUNNING_ON_JAVA_60_OR_HIGHER)
		{
			ignoreKerningCheckbox = new JCheckBox(PossibleAction.DIALOG_TEXT.getString("ignoreFontKerningLabel"));
			ignoreKerningCheckbox.setSelected(designatedIgnoreFontKerning);
			ignoreKerningCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
			pdfPanel.add(ignoreKerningCheckbox);
		}
		
		if (pdfPanel.getComponentCount()>0)
		{
			pdfPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
			textPanel.add(pdfPanel);
		}
		
		textPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		// setSize(300, 230);
		return textPanel;
	}

	/**
	 * @return Artwork featuring the default characteristics.
	 */
	public Artwork getArtwork()
	{
		return artwork;
	}
	
	/**
	 * @return a Super Ellipse Perch featuring the default characteristics.
	 */
	public SuperEllipsePerch getSuperEllipsePerch()
	{
		return superEllipsePerch;
	}
	
	/**
	 * Writes the properties of this Prefs Panel to the provided BalloonistPreferences object.
	 */
	public BalloonistPreferences overlayBalloonistPreferencesTo(final BalloonistPreferences balloonistPreferences)
	{
		balloonistPreferences.setDefaultFont(getDefaultFontSelection());
		balloonistPreferences.setDefaultHeinParameter(superEllipsePerch.getHeinParameter());
		balloonistPreferences.setPreserveAccuracyOverEditability(getPreserveAccuracySelection());
		balloonistPreferences.setDefaultColorfulness(balloon);
		balloonistPreferences.setDefaultLineThicknessInPoints(balloon.getLineThickness());
		balloonistPreferences.setDefaultInnerMarginInPoints(balloon.getInnerMarginInPoints());
		
		if (ignoreKerningCheckbox!=null)
			balloonistPreferences.setIgnoreFontKerning(ignoreKerningCheckbox.isSelected());
		
		return balloonistPreferences;
	}

	private boolean isEditableTextExportPossible()
	{
		return editableTextExportPossible;
	}
}
