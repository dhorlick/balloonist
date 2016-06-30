/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.balloonist;

import com.smithandtinkers.geom.RuffleDie;
import com.smithandtinkers.gui.MysteryComponent;
import com.smithandtinkers.gui.SilenceableSlider;
import com.smithandtinkers.layout.edit.RuffleHeightEdit;
import com.smithandtinkers.layout.edit.RuffleWidthEdit;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *
 * @author dhorlick
 */
public class RuffleComponent extends MysteryComponent implements ListCellRenderer, ChangeListener
{
	private SilenceableSlider preferredWidthSlider = new SilenceableSlider();
	private SilenceableSlider heightSlider = new SilenceableSlider();
	
	private boolean startNewRuffleWidthEdit;
	private boolean startNewRuffleHeightEdit;
	
	private final SliderWidthChangeListener SLIDER_WIDTH_CHANGE_LISTENER = new SliderWidthChangeListener();
	private final SliderHeightChangeListener SLIDER_HEIGHT_CHANGE_LISTENER = new SliderHeightChangeListener();
	
	public RuffleComponent()
	{
		super();
		add(preferredWidthSlider);
		preferredWidthSlider.setMinimum(4);
		add(heightSlider);
		heightSlider.setMinimum(3);
		heightSlider.setMaximum(16);
		// setSize(new Dimension(300,100));
		preferredWidthSlider.addChangeListener(SLIDER_WIDTH_CHANGE_LISTENER);
		preferredWidthSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				startNewRuffleWidthEdit = true;
			}			
		});
		heightSlider.addChangeListener(SLIDER_HEIGHT_CHANGE_LISTENER);
		heightSlider.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				startNewRuffleHeightEdit = true;
			}
		});
		
		setMaximumSize(heightSlider.getMaximumSize());
		setPreferredSize(heightSlider.getPreferredSize());
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if (value instanceof RuffleDie)
		{
			RuffleDie ruffleDie = (RuffleDie) value;
			setRuffleDie(ruffleDie);
		}
		
		return this;
	}
	
	public void setRuffleDie(RuffleDie designatedRuffleDie)
	{
		setModel(designatedRuffleDie);
	}
	
	public RuffleDie getRuffleDie()
	{
		if (getModel() instanceof RuffleDie)
			return (RuffleDie) getModel();
		else
			return null;
	}

	public void setModel(Object designatedModel)
	{
		if (designatedModel!=getModel())
		{
			RuffleDie designatedModelAsRuffleDie = (RuffleDie) designatedModel;
			
			/* if (getModel()!=null && getModel() instanceof RuffleDie)
			{
				RuffleDie oldModelAsRuffleDie = (RuffleDie) getModel();
				heightSlider.removeChangeListener(SLIDER_CHANGE_LISTENER);
			} */
			
			super.setModel(designatedModel);
			
			/* if (designatedModelAsRuffleDie!=null)
			{
				designatedModelAsRuffleDie.addChangeListener(this);
			} */
			
			if (designatedModelAsRuffleDie!=null)
			{
				stateChanged(null);
			}
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		stateChanged();
	}
	
	public void stateChanged()
	{
		// System.out.println("rc sc");
		
		preferredWidthSlider.setSilent(true);
		preferredWidthSlider.setValue((int)getRuffleDie().getPreferredWidthInPoints());
		preferredWidthSlider.setSilent(false);
		
		heightSlider.setSilent(true);
		heightSlider.setValue((int)getRuffleDie().getHeightInPoints());
		heightSlider.setSilent(false);
	}

	/* public boolean isShowing()
	{	
		return true;
	} */
	
	private class SliderWidthChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if (getRuffleDie()!=null)
			{
				// getRuffleDie().setPreferredWidthInPoints((double)preferredWidthSlider.getValue());
				RuffleWidthEdit ruffleWidthEdit = new RuffleWidthEdit((double)preferredWidthSlider.getValue());
				
				ruffleWidthEdit.add(getRuffleDie());
				
				if (startNewRuffleWidthEdit)
				{
					ruffleWidthEdit.setIsolate(true);
					startNewRuffleWidthEdit = false;
				}
				
				if (ruffleWidthEdit.execute())
				{
					postEdit(ruffleWidthEdit);
				}
			}
		}
	}
	
	private class SliderHeightChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			if (getRuffleDie()!=null)
			{
				// getRuffleDie().setHeightInPoints((double)heightSlider.getValue());
				RuffleHeightEdit ruffleHeightEdit = new RuffleHeightEdit((double)heightSlider.getValue());
				
				ruffleHeightEdit.add(getRuffleDie());
				
				if (startNewRuffleHeightEdit)
				{
					ruffleHeightEdit.setIsolate(true);
					startNewRuffleHeightEdit = false;
				}
				
				if (ruffleHeightEdit.execute())
				{
					postEdit(ruffleHeightEdit);
				}
			}
		}
	}
}
