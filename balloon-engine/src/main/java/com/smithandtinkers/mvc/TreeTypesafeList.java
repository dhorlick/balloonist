/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import com.smithandtinkers.util.Kid;

import com.smithandtinkers.util.KidTypesafeList;
import java.util.Iterator;
import javax.swing.event.ChangeListener;



/**
 * A Typesafe List that will message tree model events upon alteration.
 * 
 * @author dhorlick
 */
public class TreeTypesafeList extends KidTypesafeList
{
	private SingleThreadedTreeModelListenerSupport treeModelSupport;
	private SingleThreadedChangeSupport changeSupport; // move this to a subclass. some clients may not appreciate having changes managed for them.
	
	public TreeTypesafeList(Class designatedConstituentType)
	{
		super(designatedConstituentType);
	}

	public void add(int index, Object designatedConstituent)
	{
		super.add(index, designatedConstituent);
		
		SingleThreadedTreeModelListenerSupport sttmls = getTreeModelSupport();
		
		// if (changeSupport!=null && designatedConstituent instanceof Kid)
		if (sttmls!=null)
		{
			// System.out.println("fireing node insert: "+designatedConstituent);
			Object[] ancestry = determineDecoratedAncestry();
			sttmls.fireNodeInsert(new TreeModelEvent(this, ancestry, new int[] {index}, new Object[] {designatedConstituent} )); // TODO make arg 0 the model instead?
			// System.out.println("ttl.add: ancestry of "+designatedConstituent+": "+java.util.Arrays.asList(ancestry));
		}
		/* else
			System.out.println(this+": help! i don't have tree model support to bequeath. my parent is "+getParent()); */
		
		if (designatedConstituent instanceof TreeTypesafeList)
		{
			((TreeTypesafeList)designatedConstituent).setTreeModelSupport(sttmls);
			// System.out.println("*** Established add-time tree model support ("+getTreeModelSupport()+") for: "+designatedConstituent+ "," + designatedConstituent.getClass().getName()+" ****");
		}
	}
	
	public Object set(int index, Object element)
	{
		Object result = super.set(index, element);
		
		SingleThreadedTreeModelListenerSupport sttmls = getTreeModelSupport();
		
		if (sttmls!=null && result!=element && element instanceof Kid)
		{
			sttmls.fireNodeInsert(new TreeModelEvent(this, determineDecoratedAncestry()));
		}
		
		if (element instanceof TreeTypesafeList)
		{
			((TreeTypesafeList)element).setTreeModelSupport(sttmls);
			// System.out.println("*** Established set-time tree model support ("+getTreeModelSupport()+") for: "+element+ "," + element.getClass().getName()+" ****");
		}
		
		return result;
	}
	
	public SingleThreadedTreeModelListenerSupport getTreeModelSupport()
	{
		if (treeModelSupport!=null)
		{
			// System.out.println("found treeModelSupport at: "+this);
			return treeModelSupport;
		}
		
		if (getParent()!=null && getParent() instanceof TreeTypesafeList)
		{
			return ((TreeTypesafeList)getParent()).getTreeModelSupport();
		}
		
		// System.out.println("couldn't find support");
		
		return null;
	}
	
	public void setTreeModelSupport(SingleThreadedTreeModelListenerSupport designatedChangeSupport)
	{
		// System.out.println("setTreeModelSupport: "+designatedChangeSupport);
		
		treeModelSupport = designatedChangeSupport;
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			// System.out.println("setTreeModelSupport loop: "+loop);
			
			if (get(loop) instanceof TreeTypesafeList)
			{
				( (TreeTypesafeList) get(loop) ).setTreeModelSupport(designatedChangeSupport);
			}
		}
		
		// System.out.println("completed setTreeModelSupport");
	}
	
	public Object remove(int index)
	{
		// System.out.println("Goodbye: "+index);
		
		Object [] path = null;
		path = determineDecoratedAncestry();
		
		Object removed = super.remove(index);
		int [] indexes = new int[1];
		indexes[0] = index;
		Object [] children = new Object[1];
		children[0] = removed;
		
		SingleThreadedTreeModelListenerSupport sttmls = getTreeModelSupport();
		// System.out.println("sttmls="+sttmls);
		
		if (sttmls!=null && path!=null)
		{
			// System.out.println("remove: path="+Arrays.asList(path));
			// System.out.println("\t(old value would have been: "+Arrays.asList(determineAncestry())+")");
			sttmls.fireNodeRemove(new TreeModelEvent(this, path, indexes, children));
		}
		/* else
		{
			System.out.println(this+"I don't have any tree model support! my parent is "+getParent());
		} */
		
		return removed;
	}
	
	/**
	 * We are flagrantly violating the equals contract for value and container classes here to meet the
	 * depraved needs of TreeModel.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		return (this==o);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * 
	 * We are flagrantly violating the hash code contract for value and container classes here to meet the
	 * depraved needs of TreeModel.
	 */
	public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	public boolean reallyEquals(TreeTypesafeList other)
	{
		if (other==null)
			return false;
		
		return super.equals(other);
	}
	
	public int realHashCode()
	{
		return super.hashCode();
	}
	
	public TreePath determineTreePath()
	{
		return new TreePath(determineDecoratedAncestry());
	}
	
	public SingleThreadedChangeSupport getChangeSupport()
	{
		if (changeSupport!=null)
		{
			return changeSupport;
		}
		
		if (getParent()!=null && getParent() instanceof TreeTypesafeList)
		{
			// System.out.println("trying... getParent()="+getParent());
			TreeTypesafeList ttl = (TreeTypesafeList) getParent();
			SingleThreadedChangeSupport changeSupportOfParent = ttl.getChangeSupport();
			
			if (changeSupportOfParent!=null)
				return changeSupportOfParent;
		}
		
		return null;
	}

	public void setChangeSupport(SingleThreadedChangeSupport designatedChangeSupport)
	{
		// System.out.println("setChangeSupport: starting");
		
		changeSupport = designatedChangeSupport;
		
		// System.out.println("changeSupport="+changeSupport);
	}
	
	public void addChangeListener(ChangeListener designatedListener)
	{
		getChangeSupport().addChangeListener(designatedListener);
		
		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof ChangeIssuer)
			{
				ChangeIssuer ci = (ChangeIssuer) get(loop);
				ci.addChangeListener(designatedListener);
			}
		}
	}
	
	public void removeChangeListener(ChangeListener designatedChangeListener)
	{
		getChangeSupport().removeChangeListener(designatedChangeListener);

		for (int loop=0; loop<=size()-1; loop++)
		{
			if (get(loop) instanceof ChangeIssuer)
			{
				ChangeIssuer ci = (ChangeIssuer) get(loop);
				ci.removeChangeListener(designatedChangeListener);
			}
		}
	
	}
}
