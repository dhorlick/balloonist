/*
 Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.undo.UndoableEdit;


/**
 * I'm not going to actually deprecate this yet, but I recommend the use of 
 * StatefulMultipleEdit instead.
 *
 * @author dave
 */
public abstract class StatefulSelectionEdit extends SelectionEdit
{
	private Object newValue;
	private Map oldValues = new HashMap();
	
	/**
	 * {@link #setState(com.smithandtinkers.layout.Selectable, Object)} can use this as a
	 * value to indicate that the operation didn't do anything. This preserves null for use
	 * as a meaningful change value.
	 */
	protected final static Object NO_EFFECT = new Object();
	
	public StatefulSelectionEdit(String requestedTitle, Object designatedNewValue)
	{
		super(requestedTitle);
		newValue = designatedNewValue;
		// System.out.println("initializing new value to: "+newValue);
	}
	
	protected void recordOldValue(Selectable thing, Object oldValue)
	{
		// System.out.println("recording old value: "+oldValue + " for "+thing);
		oldValues.put(thing, oldValue);
	}
	
	protected Object recallOldValue(Object thing)
	{
		return oldValues.get(thing);
	}

	public final boolean execute(Selectable selectable)
	{
		Object oldValue = setState(selectable, newValue);
		
		if (oldValue==NO_EFFECT)
			return false;

		recordOldValue(selectable, oldValue);
		return true;
	}

	public boolean backout(Selectable selectable)
	{
		if (oldValues.containsKey(selectable))
		{
			Object oldValue = recallOldValue(selectable);
			setState(selectable, oldValue);
			return true;
		}
		else
			System.err.println("could not find undone value for selectable: "+selectable);
		
		return false;
	}
	
	public boolean hasEffect()
	{
		return (newValue!=NO_EFFECT);
	}
	
	public boolean addEdit(UndoableEdit anEdit)
	{
		if (anEdit.getClass().equals(getClass()))
		{
			StatefulSelectionEdit anEditAsStatefulSelectionEdit = (StatefulSelectionEdit) anEdit;

			if (!anEditAsStatefulSelectionEdit.isIsolate())
			{
				anEditAsStatefulSelectionEdit.oldValues = oldValues;
				newValue = anEditAsStatefulSelectionEdit.newValue;

				return true;
			}
			else
			{
				// System.out.println("sse: wasn't isolate (backwards), not consolidating");
			}
		}
		
		return false;
	}
	
	/**
	 * @param selectable the item whose state should be set
	 * @param value the value that item's state should be set to
	 *
	 * @return the old value, or {@link #NO_EFFECT}, if the method had no effect
	 */
	public abstract Object setState(Selectable selectable, Object value);

	/*
	public boolean replaceEdit(UndoableEdit anEdit)
	{
		boolean retValue;
		
		retValue = super.replaceEdit(anEdit);
		System.out.println("replace edit invoked: "+retValue);
		return retValue;
	}
	*/
}
