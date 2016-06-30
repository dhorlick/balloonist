/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.geom.RuffleDieList;
import com.smithandtinkers.geom.RuffleableSuperEllipse;
import com.smithandtinkers.gui.DOptionPane;
import com.smithandtinkers.gui.LabeledDropDown;
import com.smithandtinkers.gui.MysteryContainer;
import com.smithandtinkers.gui.PossibleAction;
import com.smithandtinkers.gui.SilenceableComboBox;
import com.smithandtinkers.layout.edit.ChangeRuffleTypeEdit;
import com.smithandtinkers.mvc.ListModelTypesafeList;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;


/**
 *
 * @author dhorlick
 */
public class RuffleContainer extends MysteryContainer
{
	private RuffleableSuperEllipse ruffledSuperEllipse;
	private SilenceableComboBox typeComboBox;
	
	private static final String DISCORDANCE_ERROR = "Previously-established Model Ruffle Die list does not correspond with newly-designated Ruffled Super Ellipse";
	
	public RuffleContainer()
	{
		super(BoxLayout.Y_AXIS, RuffleComponent.class);
		
		buttonBox.add(Box.createHorizontalGlue());
		
		typeComboBox = new SilenceableComboBox(RuffleableSuperEllipse.getWidgetedTypeIndex());
		
		typeComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// System.out.println("Type changed "+e.getSource());
				if (ruffledSuperEllipse!=null)
				{				
					RuffleableSuperEllipse.Type newType = (RuffleableSuperEllipse.Type) typeComboBox.getSelectedItem();
					ChangeRuffleTypeEdit changeRuffleTypeEdit = new ChangeRuffleTypeEdit(newType);
					changeRuffleTypeEdit.add(ruffledSuperEllipse);

					if (changeRuffleTypeEdit.execute())
					{
						postEdit(changeRuffleTypeEdit);
						stateChanged(); // since there would be no new selection event to trigger a shuffle
					}
				}
			}
		});
		
		buttonBox.add(new LabeledDropDown(new JLabel(DrawableDialog.DIALOG_TEXT.getString("typeLabel")), 
				typeComboBox));
		
		final Dimension dims = new Dimension(220, 132); // TODO base on its item component dimensions?
		setMinimumSize(dims);
		setPreferredSize(dims);
	}

	public RuffleDieList getRuffleDieList()
	{
		return (RuffleDieList)getMysteryList();
	}

	public boolean additionsOkay()
	{
		if (ruffledSuperEllipse==null)
			throw new IllegalStateException("Corresponding Ruffled Super Ellipse has not been set.");
				
		if (getRuffleDieList().size()>=10)
		{
			Toolkit.getDefaultToolkit().beep();
			DOptionPane.showMessageDialog(this, 
					PossibleAction.DIALOG_TEXT.getString("maximumRuffleQuantityReachedMessage"));
			return false;
		}
		
		return true;
	}

	public RuffleableSuperEllipse getRuffledSuperEllipse()
	{
		return ruffledSuperEllipse;
	}

	public void setRuffledSuperEllipse(RuffleableSuperEllipse designatedRuffledSuperEllipse)
	{
		if (ruffledSuperEllipse != designatedRuffledSuperEllipse)
		{
			ruffledSuperEllipse = designatedRuffledSuperEllipse;
			
			if (designatedRuffledSuperEllipse!=null)
			{
				setModel(designatedRuffledSuperEllipse.getRuffles());
				
				typeComboBox.setSilent(true);
				typeComboBox.setSelectedItem(designatedRuffledSuperEllipse.getType());
				typeComboBox.setSilent(false);
			}
		}
	}

	public void setModel(ListModelTypesafeList designatedListEventIssuingTypesafeList)
	{
		if (designatedListEventIssuingTypesafeList!=null && ruffledSuperEllipse!=null && ruffledSuperEllipse.getRuffles()!=designatedListEventIssuingTypesafeList)
		{
			throw new IllegalArgumentException(DISCORDANCE_ERROR);
		}
		
		super.setModel(designatedListEventIssuingTypesafeList);
	}
	
	
}
