/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.mvc.PrimitiveBooleanHolder;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author dhorlick
 */
public class WiredCheckbox extends SilenceableCheckBox
{
	private PrimitiveBooleanHolder booleanModel;
	private final ChangeListener CHANGE_LISTENER = new ChangeListener()
	{
		public void stateChanged(ChangeEvent e)
		{
			if (getBooleanModel()!=null)
			{
				boolean checkBoxState = isSelected();
				if (getBooleanModel().getBoolean()!=checkBoxState)
					getBooleanModel().setBoolean(checkBoxState);
			}
		}
	};
	
	public WiredCheckbox(String designatedLabel)
	{
		super(designatedLabel);
		addChangeListener(CHANGE_LISTENER);
	}

	public PrimitiveBooleanHolder getBooleanModel()
	{
		return booleanModel;
	}

	public void setBooleanModel(PrimitiveBooleanHolder designatedModel)
	{
		if (booleanModel == designatedModel)
			return;
		
		booleanModel = designatedModel;
		
		if (booleanModel!=null)
			setSelected(booleanModel.getBoolean());
	}
	
	public void stateChanged()
	{
		setSilent(true);
		setSelected(getBooleanModel().getBoolean());
		setSilent(false);
	}
	
	public void stateChanged(ChangeEvent designatedChangeEvent)
	{
		if (designatedChangeEvent==null && designatedChangeEvent.getSource()!=this)
			stateChanged();
	}

	public void setFontSize(int designatedFontSize)
	{
		setFont(getFont().deriveFont(designatedFontSize));
	}
}
