/**
 * Copyleft 2007 by Dave Horlick
 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.layout.PanelLayoutPolicy;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

/**
 * @author dhorlick
 */
public class PanelLayoutPolicyBox extends Box
{
	private PanelLayoutPolicy panelLayoutPolicy;
	
	private JRadioButton automaticPolicyButton;
	private JRadioButton manualPolicyButton;
	
	public PanelLayoutPolicyBox()
	{
		super(BoxLayout.X_AXIS);
		
		JLabel policyLabel = new JLabel(DrawableDialog.DIALOG_TEXT.getString("panelLayoutPolicyLabel"));
		
		automaticPolicyButton = new JRadioButton(DrawableDialog.DIALOG_TEXT.getString("panelLayoutPolicyAutomaticLabel"));
		manualPolicyButton = new JRadioButton(DrawableDialog.DIALOG_TEXT.getString("panelLayoutPolicyManualLabel"));
		
		policyLabel.setFont(policyLabel.getFont().deriveFont((float)DrawableDialog.WIDGET_FONT_SIZE));
		automaticPolicyButton.setFont(automaticPolicyButton.getFont().deriveFont((float)DrawableDialog.WIDGET_FONT_SIZE));
		manualPolicyButton.setFont(manualPolicyButton.getFont().deriveFont((float)DrawableDialog.WIDGET_FONT_SIZE));
		
		final ButtonGroup policyButtonGroup = new ButtonGroup();
		policyButtonGroup.add(automaticPolicyButton);
		policyButtonGroup.add(manualPolicyButton);
		
		add(policyLabel);
		add(Box.createHorizontalStrut(9));
		add(automaticPolicyButton);
		add(manualPolicyButton);
		
		final ActionListener actionListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent actionEvent)
			{
				if (getPanelLayoutPolicy()!=null)
				{
					if (actionEvent.getSource()==automaticPolicyButton)
					{
						getPanelLayoutPolicy().setManual(false);
					}
					else if (actionEvent.getSource()==manualPolicyButton)
					{
						getPanelLayoutPolicy().setManual(true);
					}
				}
			}
		};
		
		automaticPolicyButton.addActionListener(actionListener);
		manualPolicyButton.addActionListener(actionListener);
		
		final int PADDING = determinePadding(policyLabel);
		setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
	}
	
	public PanelLayoutPolicyBox(PanelLayoutPolicy designatedPanelLayoutPolicy)
	{
		this();
		setPanelLayoutPolicy(designatedPanelLayoutPolicy);
	}
	
	public static int determinePadding(Component component)
	{
		if (component==null)
			return 4;
		else
			return component.getFont().getSize()/2;
	}

	public PanelLayoutPolicy getPanelLayoutPolicy()
	{
		return panelLayoutPolicy;
	}

	public void setPanelLayoutPolicy(PanelLayoutPolicy designatedPanelLayoutPolicy)
	{
		if (panelLayoutPolicy != designatedPanelLayoutPolicy)
		{
			panelLayoutPolicy = designatedPanelLayoutPolicy;
			
			jog();
		}
	}
	
	/**
	 * Refreshes the value from the model. This would typically be called from some kind
	 * of listener.
	 */
	public void jog()
	{
		if (panelLayoutPolicy.isManual())
		{
			automaticPolicyButton.getModel().setSelected(false);
			manualPolicyButton.getModel().setSelected(true);
		}
		else
		{
			automaticPolicyButton.getModel().setSelected(true);
			manualPolicyButton.getModel().setSelected(false);
		}
	}
}
