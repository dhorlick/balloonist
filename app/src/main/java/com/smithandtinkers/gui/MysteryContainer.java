/**
 * Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.layout.edit.MysteryAddEdit;
import com.smithandtinkers.layout.edit.MysteryDeleteEdit;
import com.smithandtinkers.mvc.ListModelTypesafeList;
import com.smithandtinkers.mvc.SimpleModelTypesafeList;
import com.smithandtinkers.util.BugException;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 * A container suitable for holding a small, ephemeral number of mystery components.
 *
 * @author dhorlick
 */
public abstract class MysteryContainer extends JPanel implements ListDataListener, ChangeListener
{
	private Class mysteryComponentType;
	
	private ListModelTypesafeList model;
	private UndoableEditSupport editSupport = new UndoableEditSupport();
	
	private Box contentBox;
	private JScrollPane scrollPane;
	
	protected Box buttonBox;
	
	private SimpleModelTypesafeList selectedMysteryComponents = new SimpleModelTypesafeList(MysteryComponent.class);
	
	/**
	 * @param designatedBoxLayoutVariety one of the BoxLayout constants
	 * @param designatedMysteryComponentType a subtype of MysteryComponent
	 */
	public MysteryContainer(int designatedBoxLayoutVariety, Class designatedMysteryComponentType)
	{
		contentBox = new Box(designatedBoxLayoutVariety);
		
		scrollPane = new JScrollPane(contentBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		
		buttonBox = new Box(BoxLayout.X_AXIS);
		Action addItemAction = new AbstractAction("+")
		{
			public void actionPerformed(ActionEvent event)
			{
				if (getMysteryList()!=null && additionsOkay())
				{
					// getMysteryList().add(newMysteryItem());
					MysteryAddEdit createEdit = new MysteryAddEdit(getMysteryList(), newMysteryItem());
					if (createEdit.execute())
					{
						postEdit(createEdit);
					}
				}
			}
		};
		
		Action removeItemAction = new AbstractAction("-")
		{
			public void actionPerformed(ActionEvent event)
			{
				if (getMysteryList()!=null)
				{
					MysteryDeleteEdit deleteEdit = new MysteryDeleteEdit(model);
					
					Iterator walk = selectedMysteryComponents.iterator();
					
					while (walk.hasNext())
					{
						MysteryComponent doomedComponent = (MysteryComponent) walk.next();
						// model.remove(doomedComponent.getModel());
						deleteEdit.add(doomedComponent.getModel());
					}
					
					if (deleteEdit.execute())
					{
						postEdit(deleteEdit);
					}
				}
				
				selectedMysteryComponents.clear();
				invalidate();
				repaint();
			}
		};
		
		buttonBox.add(new JButton(addItemAction));
		buttonBox.add(new JButton(removeItemAction));
		add(buttonBox, BorderLayout.SOUTH);
		
		mysteryComponentType = designatedMysteryComponentType; // TODO validate
		setAlignmentY(Component.TOP_ALIGNMENT);
	}
	
	public ListModelTypesafeList getMysteryList()
	{
		return model;
	}
	
	public void setModel(ListModelTypesafeList designatedListEventIssuingTypesafeList)
	{
		if (designatedListEventIssuingTypesafeList!=model)
		{
			if (designatedListEventIssuingTypesafeList!=null)
				designatedListEventIssuingTypesafeList.removeListDataListener(this);
			
			contentBox.removeAll();
			selectedMysteryComponents.clear();
			
			model = designatedListEventIssuingTypesafeList;
			
			// System.out.println("building: "+model.size());
			
			for (int loop=0; loop<=model.size()-1; loop++)
			{
				MysteryComponent mysteryComponent = newMysteryComponent();
				mysteryComponent.setModel(model.get(loop));
				contentBox.add(mysteryComponent);
			}
			
			if (model!=null)
				model.addListDataListener(this);
			
			invalidate();
			repaint();
		}
	}

	public Class getMysteryComponentType()
	{
		return mysteryComponentType;
	}
	
	public void intervalRemoved(ListDataEvent e)
	{
		// System.out.println("zing! "+e);
		
		for (int loop=e.getIndex1(); loop>=e.getIndex0(); loop--)
		{
			// System.out.println("\tremoving: "+loop);
			contentBox.remove(loop);
		}
		
		contentBox.invalidate();
		scrollPane.invalidate();
		scrollPane.validate();
		// System.out.println("contentBox.getComponentCount()"+contentBox.getComponentCount());
	}

	public void intervalAdded(ListDataEvent e)
	{
		for (int loop=e.getIndex0(); loop<=e.getIndex1(); loop++)
		{
			MysteryComponent mysteryComponent = newMysteryComponent();
			mysteryComponent.setModel(model.get(loop));
			contentBox.add(mysteryComponent, loop);
		}
		
		contentBox.invalidate();
		scrollPane.invalidate();
		scrollPane.validate();
	}

	public void contentsChanged(ListDataEvent e)
	{
		for (int loop=e.getIndex0(); loop<=e.getIndex1(); loop++)
		{
			MysteryComponent mc = (MysteryComponent) getComponent(loop);
			mc.stateChanged();
		}
		
		contentBox.invalidate();
		scrollPane.validate();
	}
	
	private MysteryComponent newMysteryComponent()
	{
		try
		{
			MysteryComponent mysteryComponent = (MysteryComponent) mysteryComponentType.newInstance();
			mysteryComponent.setMysteryContainer(this);
			return mysteryComponent;
		}
		catch (InstantiationException exception)
		{
			throw new BugException(exception);
		}
		catch (IllegalAccessException exception)
		{
			throw new BugException(exception);
		}
	}
	
	protected Object newMysteryItem()
	{
		try
		{
			if (getMysteryList()==null)
				return null;
			
			Object newMysteryItem = getMysteryList().getConstituentType().newInstance();
			
			return newMysteryItem;
		}
		catch (InstantiationException exception)
		{
			throw new BugException(exception);
		}
		catch (IllegalAccessException exception)
		{
			throw new BugException(exception);
		}
	}
	
	public void addUndoableEditListener(UndoableEditListener l)
	{
		editSupport.addUndoableEditListener(l);
	}
	
	public void removeUndoableEditListener(UndoableEditListener l)
	{
		editSupport.removeUndoableEditListener(l);
	}
	
	public void postEdit(UndoableEdit designatedEdit)
	{
		editSupport.postEdit(designatedEdit);
	}	

	public void stateChanged(ChangeEvent e)
	{
		stateChanged();
	}
	
	public void stateChanged()
	{
		for (int loop=0; loop<=contentBox.getComponentCount()-1; loop++)
		{
			Component component = (Component) (contentBox.getComponent(loop));
			
			if (component instanceof MysteryComponent)
			{
				MysteryComponent mysteryComponent = (MysteryComponent) component;
				mysteryComponent.stateChanged();
			}
		}
	}
	
	public ListModelTypesafeList getModel()
	{
		return model;
	}
	
	public void addListDataListener(ListDataListener requestedListDataListener)
	{
		if (model!=null)
			model.addListDataListener(requestedListDataListener);
	}
		
	public void removeListDataListener(ListDataListener requestedListDataListener)
	{
		if (model!=null)
			model.removeListDataListener(requestedListDataListener);
	}
	
	/**
	 * @return a List of selected components
	 */
	public List getSelected()
	{
		return selectedMysteryComponents;
	}
	
	/**
	 * Override this to prevent additions.
	 */
	public boolean additionsOkay()
	{
		return true;
	}
	
	public boolean isModelObjectSelected(Object modelObject)
	{
		Iterator walk = selectedMysteryComponents.iterator();
		
		while (walk.hasNext())
		{
			MysteryComponent component = (MysteryComponent) walk.next();
			if (component.getModel()==modelObject)
				return true;
		}
		
		return false;
	}
	
	/**
	 * @return a live List of selected model items.
	 */
	public List selectedModelItems()
	{
		return new AbstractList()
		{
			public Object get(int index)
			{
				MysteryComponent component = (MysteryComponent) selectedMysteryComponents.get(index);
				return component.getModel();
			}

			public int size()
			{
				return selectedMysteryComponents.size();
			}
		};
	}
	
	/**
	 * This is helpful for issuing change events when something new is selected, since this control
	 * doesn't expose a selection model.
	 */
	public void addContainerLevelChangeListener(ChangeListener designatedChangeListener)
	{
		selectedMysteryComponents.addChangeListener(designatedChangeListener);
	}
	
	public void removeContainerLevelChangeListener(ChangeListener designatedChangeListener)
	{
		selectedMysteryComponents.removeChangeListener(designatedChangeListener);
	}
	
	/**
	 * Resets the state of this control to what it was at instantation time.
	 *
	 * Please call this when done with the component so that its references can be
	 * garbage collected.
	 */
	public void reset()
	{
		selectedMysteryComponents.removeAllChangeListeners();
		selectedMysteryComponents.clear();
		// TODO remove undoable edit listeners
		setModel(null);
	}
}
