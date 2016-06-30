/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.undo.UndoableEdit;


/**
 * My best Edit skeleton so far.
 *
 * @author dhorlick
 */
public abstract class StatefulMultipleEdit extends MultipleEdit
{
	private Object newValue;
	private Map oldValues = new HashMap();
	
	/**
	 * {@link #setState(Object, Object)} can use this as a
	 * value to indicate that the operation didn't do anything. This preserves null for use
	 * as a meaningful change value.
	 */
	protected final static Object NO_EFFECT = new Object();
	
	public StatefulMultipleEdit(String requestedTitle, Object designatedNewValue)
	{
		super(requestedTitle);
		newValue = designatedNewValue;
	}
	
	protected void recordOldValue(Object thing, Object oldValue)
	{
		// System.out.println("recording old value: "+oldValue + " for "+thing);
		oldValues.put(thing, oldValue);
	}
	
	protected Object recallOldValue(Object thing)
	{
		return oldValues.get(thing);
	}

	public final boolean execute(Object thing)
	{
		Object oldValue = setState(thing, newValue);
		
		if (oldValue==NO_EFFECT)
			return false;

		recordOldValue(thing, oldValue);
		return true;
	}

	public final boolean backout(Object thing)
	{
		if (oldValues.containsKey(thing))
		{
			Object oldValue = recallOldValue(thing);
			setState(thing, oldValue);
			return true;
		}
		else
			System.err.println("could not find undone value for thing: "+thing);
		
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
			StatefulMultipleEdit anEditAsStatefulMultipleEdit = (StatefulMultipleEdit) anEdit;
			
			if (!anEditAsStatefulMultipleEdit.isIsolate() && affectedItems.equals(anEditAsStatefulMultipleEdit.affectedItems))
			{
				anEditAsStatefulMultipleEdit.oldValues = oldValues;
				newValue = anEditAsStatefulMultipleEdit.newValue;
			
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param thing the item whose state should be set
	 * @param value the value that item's state should be set to
	 *
	 * @return the old value, or {@link #NO_EFFECT}, if the method had no effect
	 */
	public abstract Object setState(Object thing, Object value);
	
	/**
	 * @return a read-only map of the old values, if any
	 */
	public Map getOldValues()
	{
		return Collections.unmodifiableMap(oldValues);
	}
}
