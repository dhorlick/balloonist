/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.smithandtinkers.gui.LabeledTextField;
import com.smithandtinkers.layout.edit.RenameEdit;
import com.smithandtinkers.mvc.StringHolder;
import com.smithandtinkers.util.Named;

/**
 *
 * @author dhorlick
 */
public class NamedAbstractPropsPanel extends AbstractPropsPanel // TODO rename without "abstract"
{
	protected LabeledTextField nameField;
	
	protected void initialize()
	{
		JTextField nameJTextField = new JTextField();
		nameField = new LabeledTextField(new JLabel(DrawableDialog.DIALOG_TEXT.getString("nameLabel")), nameJTextField);
		nameField.setFontSize(DrawableDialog.WIDGET_FONT_SIZE);
		add(nameField);
		
		nameField.setModel(new StringHolder()
			{
				public String getString()
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return getDrawableDialog().DIALOG_TEXT.getString("unnamedLabel");
					
					if (getDrawableDialog().getSelection().getSelectedItemsCount()==1)
					{
						if (getDrawableDialog().getSelection().getSelectedItem(0) instanceof Named)
						{
							Named named = (Named) getDrawableDialog().getSelection().getSelectedItem(0);
							return named.getName();
						}
						else
						{
							return getDrawableDialog().DIALOG_TEXT.getString("unnamedLabel");
						}
					}
					else
					{
						return getDrawableDialog().DIALOG_TEXT.getString("multipleLabel"); // TODO make this more descriptive?
					}
				}

				public void setString(String designatedString)
				{
					if (getDrawableDialog()==null || getDrawableDialog().getSelection()==null)
						return;
					
					String oldValue = getString();
					
					if (oldValue==designatedString || (designatedString!=null && designatedString.equals(oldValue)))
						return;
					
					RenameEdit renameEdit = new RenameEdit(designatedString);
					renameEdit.addItemsFrom(getDrawableDialog().getSelection().iterateOverSelectedItems());
					if (renameEdit.execute())
					{
						getDrawableDialog().announceEdit(NamedAbstractPropsPanel.this, renameEdit);
					}
				}
			}
		);
	}

	public LabeledTextField getNameField()
	{
		return nameField;
	}

	protected void stateChanged()
	{
		nameField.stateChanged();
	}

	public void revealed()
	{
	}
}
