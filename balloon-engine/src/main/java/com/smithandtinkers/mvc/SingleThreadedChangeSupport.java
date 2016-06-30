/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import com.smithandtinkers.util.FamilyMember;
import com.smithandtinkers.util.GodKid;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.NullHatingTypesafeList;

import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * @author dhorlick
 */
public class SingleThreadedChangeSupport extends NullHatingTypesafeList implements ChangeIssuer
{
	public SingleThreadedChangeSupport()
	{
		super(ChangeListener.class);
	}
	
	public void addChangeListener(ChangeListener designatedListener)
	{
		// System.out.println("stcs adding: "+deisgnatedListener);
		
		if (designatedListener!=this)
			add(designatedListener);
	}
	
	/**
	 * Fire an existing PropertyChangeEvent to any registered listeners.
	 */
	public void fireChange(ChangeEvent event)
	{
		// System.out.println("firing change: "+event);
		
		for (int loop=size()-1; loop>=0; loop--)
		{
			ChangeListener changeListener = (ChangeListener) get(loop);
			changeListener.stateChanged(event);
		}
	}
	
	public void removeChangeListener(ChangeListener designatedChangeListener)
	{
		if (designatedChangeListener!=null)
			remove(designatedChangeListener);
	}
	
	
	/**
	 * Adds all the change listeners in this class to the designated change issuer.
	 */
	public void addAllChangeListenersTo(ChangeIssuer designatedChangeIssuer)
	{
		Iterator walk = iterator();
		
		while (walk.hasNext())
		{
			designatedChangeIssuer.addChangeListener((ChangeListener)walk.next());
		}
	}
	
	public static SingleThreadedChangeSupport findChangeSupport(FamilyMember designatedFamilyMember)
	{
		if (designatedFamilyMember instanceof TreeTypesafeList)
		{
			TreeTypesafeList ttl = (TreeTypesafeList) designatedFamilyMember;
			return ttl.getChangeSupport();
		}
		
		if (designatedFamilyMember instanceof Kid)
		{
			Kid asKid = (Kid) designatedFamilyMember;
			
			if (asKid.getParent()!=null && asKid.getParent() instanceof TreeTypesafeList)
			{
				// System.out.println("trying... getParent()="+getParent());
				TreeTypesafeList ttl = (TreeTypesafeList) asKid.getParent();
				return ttl.getChangeSupport();
			}
		}

		if (designatedFamilyMember instanceof GodKid)
		{
			GodKid asGodKid = (GodKid) designatedFamilyMember;
			if (asGodKid.getGodParent()!=null && asGodKid.getGodParent() instanceof TreeTypesafeList)
			{
				TreeTypesafeList ttl = (TreeTypesafeList) asGodKid.getGodParent();
				return ttl.getChangeSupport();
			}
		}
		
		if (designatedFamilyMember instanceof Kid)
		{
			Kid asKid = (Kid) designatedFamilyMember;

			if (asKid.getParent()!=null && asKid.getParent() instanceof FamilyMember)
			{
				return SingleThreadedChangeSupport.findChangeSupport((FamilyMember)asKid.getParent());
			}
		}
		
		if (designatedFamilyMember instanceof GodKid)
		{
			GodKid asGodKid = (GodKid) designatedFamilyMember;
			
			if (asGodKid.getGodParent()!=null && asGodKid.getGodParent() instanceof FamilyMember)
			{
				return SingleThreadedChangeSupport.findChangeSupport((FamilyMember)asGodKid.getGodParent());
			}
		}
		
		return null;
	}
	
	/**
	 * @return true, if change support was found
	 */
	public static boolean fireChangeEvent(Kid source)
	{
		return fireChangeEvent(source, new ChangeEvent(source));
	}
	
	/**
	 * @return true, if change support was found
	 */
	public static boolean fireChangeEvent(Kid source, ChangeEvent designatedEvent)
	{
		SingleThreadedChangeSupport changeSupport = findChangeSupport(source);
		if (changeSupport!=null)
		{
			changeSupport.fireChange(designatedEvent);
			return true;
		}
		
		return false;
	}
}
