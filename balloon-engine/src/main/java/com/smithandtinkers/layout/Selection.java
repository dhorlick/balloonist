/*
 Copyleft 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.*;

import javax.swing.tree.TreePath;

import com.smithandtinkers.mvc.SelfContainedTreeSelectionEvent;
import com.smithandtinkers.mvc.SelfContainedTreeSelectionListenerSupport;
import com.smithandtinkers.util.GodKid;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.NullHatingTypesafeList;
import com.smithandtinkers.util.TypesafeList;


/**
 * Keeps track of which {@link com.smithandtinkers.layout.Selectable} objects the user has selected,
 * and sends out notifications as appropriate.
 *
 * The central problem this class tries to address is coordination. It seeks to ensure that
 * <ul>
 *      <li>only Selectable objects can be selected</li>
 *      <li>an object cannot be selected twice</li>
 *      <li>null cannot be selected</li>
 *      <li>concurrent selections are announced to the outside world as a single discrete event</li>
 *      <li>an object cannot be selected at the same time as its parent.</li>
 * </ul>
 *
 * It permits a user to select items that overlap a particular point, or items that are overlapped
 * by a particular rectangle (but not both.)
 *
 * To select a group of items,
 * 
 * <ol>
 *      <li>Instantiate a Selection</li>
 *      <li>If you have trees that need to hear about updates to the selection state, call
 *      {@link #getSelfContainedTreeSelectionListenerSupport()} and add them to the return
 *      value.</li>
 *      <li>invoke {@link #setDirty(boolean, Object)}, where boolean is true and Object is
 *      the GUI element that can, for practical purposes, be considered responsible for conveying
 *      the user's selection.</li>
 *      <li>invoke {@link #registerSelection(com.smithandtinkers.layout.Selectable)} with
 *      each of the items that need to be selected.</li>
 *      <li>invoke {@link #setDirty(boolean, Object)}, where boolean is false and Object is
 *      the same as above. This will cause update events to be issued to any listeners.</li>
 * </ol>
 */
public class Selection
{
	private Point pointChoice; // TODO use x and y coords instead?
	private Rectangle rectChoice;
	private boolean dirty;
	
	private TypesafeList selectedItems = new NullHatingTypesafeList(Selectable.class);
	private List auxiliarySelectedItems = null;
	
	private boolean permitMultipleRegistrations;
	private boolean additiveMode;
	
	private SelfContainedTreeSelectionListenerSupport selfContainedTreeSelectionListenerSupport = new SelfContainedTreeSelectionListenerSupport(); 
	
	public Selection()
	{
	}
	
	/**
	 * You should follow up invocations with a call to Drawable.draw.
	 */
	public void setPointChoice(Point designatedPointChoice)
	{
		if (designatedPointChoice!=null)
		{
			// selectedItems.clear();
			rectChoice = null;
		}
		
		pointChoice = designatedPointChoice;
	}
	
	public Point getPointChoice() // TODO provide this in local coordinates instead
	{
		return pointChoice;
	}
	
	public Rectangle getRectChoice() // TODO provide this in local coordinates instead
	{
		return rectChoice;
	}
	
	/**
	 * You should follow up invocations with a call to Drawable.draw.
	 */
	public void setRectChoice(Rectangle designatedRectChoice)
	{
		if (designatedRectChoice!=null)
			pointChoice = null;
		
		rectChoice = designatedRectChoice;
	}

	public TypesafeList getSelectedItems()
	{
		return selectedItems;
	}
	
	public Iterator iterateOverSelectedItems()
	{
		return selectedItems.iterator();
	}
	
	public int getSelectedItemsCount()
	{
		return selectedItems.size();
	}
	
	public Selectable getSelectedItem(int index)
	{
		return (Selectable) selectedItems.get(index);
	}
	
	/**
	 * Makes a superficial determination of whether the provided selectable
	 * has been selected.
	 *
	 * For a definitive answer, consult
	 * {@link com.smithandtinkers.layout.Selectable#isSelected}
	 */
	public boolean isSelected(Selectable selectable)
	{
		return selectedItems.contains(selectable);
	}
	
	/**
	 * see {@link #setDirty(boolean, Object)}
	 */
	public boolean isDirty()
	{
		return dirty;
	}
	
	/**
	 * When true, on the next draw cycle, the hit target should be used to re-determine which
	 * object(s) are currently selected.
	 * 
	 * When this is set to false, the current selection state is compared with the previously
	 * registered one. If they are different, a selection event is issued.
	 * 
	 * @param source used to determine whether or not to provide selection notifications,
	 *               This parameter can be considered optional if the designatedDirtiness is
	 *               true.
	 */
	public void setDirty(boolean designatedDirtiness, Object source)
	{
		if (designatedDirtiness==dirty)
		{
			return;
		}
		
		// System.out.println("jelp");
		
		// System.out.println("\nsetting dirtiness: "+designatedDirtiness);
		
		dirty = designatedDirtiness;
		
		// System.out.println("previousSelectedItems: "+previousSelectedItems);
		// System.out.println("selectedItems: "+selectedItems);
		
		if (!dirty)
		{
			// System.out.println("issuing...");
			
			unselectObscuredItems();
			
			SelfContainedTreeSelectionEvent selfContainedEvent = buildSelfContainedTreeSelectionEvent(source);
			selfContainedTreeSelectionListenerSupport.fireValueChanged(selfContainedEvent);
			
			// setPermitMultipleRegistrations(false);
		}
	}
	
	public SelfContainedTreeSelectionEvent buildSelfContainedTreeSelectionEvent(Object source)
	{
		TreePath [] built = new TreePath[selectedItems.size()];
		
		for (int popLoop1=0; popLoop1<=selectedItems.size()-1; popLoop1++)
		{
			Kid kid = (Kid) selectedItems.get(popLoop1);
			built[popLoop1] = new TreePath(kid.determineAncestry());
		}
		
		return new SelfContainedTreeSelectionEvent(source, built);
	}

	public String toString()
	{
		return "com.smithandtinkers.layout.Selection {"
			 + "pointChoice = " + pointChoice + ", "
			 + "rectChoice = " + rectChoice + ", "
			 + "dirty = " + dirty + ", "
			 + "selectedItems = " + selectedItems + ", "
			 + "auxiliarySelectedItems = " + auxiliarySelectedItems
		+ "}";
	}
	
	/**
	 * Deletes the selected items.
	 */
	public void delete()
	{
		for (int loop=0; loop<=getSelectedItems().size()-1; loop++)
		{
			Object item = getSelectedItems().get(loop);
			if (item instanceof Kid)
			{
				Kid kid = (Kid) item;
				if (kid.getParent()!=null)
				{
					kid.getParent().remove(kid);
					kid.setParent(null);
				}
				else if (kid instanceof List)
				{
					List list = (List) item;
					
					list.clear();
				}
			}
		}
		
		getSelectedItems().clear();
	}
	
	/**
	 * Allows for the selection to be reset to nothing.
	 *
	 * Automatically marks selection as dirty.
	 */
	public void resetSelection()
	{
		// System.out.println("reseting selection");
		setDirty(true, null);
		selectedItems.clear();
		additiveMode = false;
	}
	
	/**
	 * Examines the selected items and returns the most specify type that they all implement.
	 * 
	 * @return a type, or null if no items are selected.
	 */
	public Class mostGeneralConstituentType()
	{	
		if (selectedItems==null || selectedItems.size()==0)
			return null;
		
		Class mostGeneral = (Class) selectedItems.get(0).getClass();
		
		for (int loop=1; loop<=selectedItems.size()-1 && mostGeneral!=Object.class; loop++)
		{
			Class qlass = selectedItems.get(loop).getClass();
			mostGeneral = Selection.moreGeneral(mostGeneral, qlass);
		}
		
  		return mostGeneral;
	}
	
	public static Class moreGeneral(Class one, Class two)
	{
		if (one.isAssignableFrom(two))
			return one;
		
		if (two.isAssignableFrom(one))
			return two;
			
		return Object.class;
	}
	
	/**
	 * If the selection is dirty, the provided drawable will be added to it.
	 * Otherwise, nothing.
	 */
	public void registerSelection(Selectable selectable)
	{
		if (isDirty() && !selectedItems.contains(selectable)
				&& !childAlreadySelected(selectable))
		{
			// System.out.println("selecting: "+selectable);
			selectedItems.add(selectable);
			
			if (selectable instanceof Kid)
			{
				unselectForefathersOf((Kid)selectable);
			}
		}
	}
	
	/**
	 * @return true, if a child of the designated selectable has been selected.
	 */
	public boolean childAlreadySelected(Selectable selectionCandidate)
	{
		if (!(selectionCandidate instanceof List))
			return false;
		
		List selectionCandidateAsList = (List) selectionCandidate;
		
		if (deepSelected(selectionCandidateAsList))
		{
			// System.out.println("child already selected");
			return true;
		}

		return false;
	}
	
	/**
	 * @return true, if the item parameter in contained anywhere in designatedCollection's
	 * Object hierarchy.
	 *         false otherwise
	 */
	public boolean deepSelected(List designatedList)
	{
		for (int loop=0; loop<=designatedList.size()-1; loop++)
		{
			Object loopItem = designatedList.get(loop);
			
			if (loopItem instanceof Selectable)
			{
				Selectable loopItemAsSelectable = (Selectable) loopItem;
				if (loopItemAsSelectable.isSelected(this))
					return true;
			}
			
			if (loopItem instanceof List)
			{
				if (deepSelected((List)loopItem))
					return true;
			}
		}
		
		return false;
	}
	
	public void unselectForefathersOf(Kid kid)
	{
		Object parent = kid.getParent();
		
		if (parent!=null)
		{
			if (parent instanceof Selectable)
			{
				Selectable parentAsSelectable = (Selectable) parent;
				
				if (parentAsSelectable.isSelected(this))
				{
					// System.out.println("unselecting: "+parentAsSelectable);
					selectedItems.remove(parentAsSelectable);
				}
			}
			
			if (parent instanceof Kid)
			{
				Kid parentAsKid = (Kid) parent;
				unselectForefathersOf(parentAsKid);
			}
		}
		
		if (kid instanceof GodKid)
		{
			GodKid godKid = (GodKid) kid;
			Object godParent = godKid.getGodParent();
			
			if (godParent!=parent && godParent!=null)
			{
				if (godParent instanceof Selectable)
				{
					Selectable godParentAsSelectable = (Selectable) godParent; 

					if (godParentAsSelectable.isSelected(this))
					{
						selectedItems.remove(godParentAsSelectable);
					}
				}
				
				if (godParent instanceof Kid)
				{
					unselectForefathersOf((Kid)godParent);
				}
			}
		}
	}
	
	public boolean isPermitMultipleRegistrations()
	{
		return permitMultipleRegistrations;
	}
	
	public void setPermitMultipleRegistrations(boolean designatedPermitMultipleRegistrations)
	{
		permitMultipleRegistrations = designatedPermitMultipleRegistrations;
	}

	public SelfContainedTreeSelectionListenerSupport getSelfContainedTreeSelectionListenerSupport()
	{
		return selfContainedTreeSelectionListenerSupport;
	}

	/**
	 * Requests that the selected items, or their crowds if applicable, be re-layed-out.
	 *
	 * Different from setDirty, which applies to the selection itself, not the items contained within it.
	 *
	 */
	public void reLayoutCrowdsOfSelectedItems()
	{
		for (int loop=0; loop<=getSelectedItemsCount()-1; loop++)
		{
			Crowd.reLayoutCrowdsOf(getSelectedItem(loop));
		}
	}
	
	/**
	 * @return the mean x coordinate for all selected items.
	 */
	public double averageX()
	{
		double averageX = 0.0;
		
		int resizeables = 0;
		
		for (int loop=0; loop<=selectedItems.size()-1; loop++)
		{
			if (getSelectedItem(loop) instanceof Resizeable)
			{
				Resizeable resizeable = (Resizeable) getSelectedItem(loop);
				Point2D resizeableLocation = resizeable.getLocation();
				if (resizeableLocation!=null)
				{
					averageX += resizeable.getLocation().getX();
					resizeables++;
				}
			}
		}
		
		if (resizeables==0)
			return 0.0;
		else
			return averageX / (double)resizeables;
	}
	
	/**
	 * @return the mean y coordinate for all selected items.
	 */
	public double averageY()
	{
		double averageY = 0.0;
		
		int resizeables = 0;
		
		for (int loop=0; loop<=selectedItems.size()-1; loop++)
		{
			if (getSelectedItem(loop) instanceof Resizeable)
			{
				Resizeable resizeable = (Resizeable) getSelectedItem(loop);
				Point2D resizeableLocation = resizeable.getLocation();
				
				if (resizeableLocation!=null)
				{
					averageY += resizeableLocation.getY();
					resizeables++;
				}
			}
		}
		
		if (resizeables==0)
			return 0.0;
		else
			return averageY / (double)resizeables;
	}
	
	/**
	 * Finds the first selected item of the requested type, or null if there aren't any.
	 */
	public Object findFirst(Class requestedType)
	{
		for (int loop=0; loop<=selectedItems.size()-1; loop++)
		{
			// if (selectedItems.get(loop)!=null && selectedItems.get(loop).getClass().isAssignableFrom(requestedType))
			if (selectedItems.get(loop)!=null && requestedType.isAssignableFrom(selectedItems.get(loop).getClass()))
			{
				// System.out.println("ff Yes: "+selectedItems.get(loop).getClass().getName());
				
				return selectedItems.get(loop);
			}
			// else
			// 	System.out.println("ff no: "+selectedItems.get(loop).getClass().getName());
		}
		
		// System.out.println("Couln't find type: "+requestedType);
		
		return null;
	}
	
	public boolean containsInstanceOf(Class requestedType)
	{
		return (findFirst(requestedType)!=null);
	}
	
	public void unselectObscuredItems()
	{
		if (!additiveMode && selectedItems.size()>1)
		{
			// System.out.println("unselected obscured...");
			List keeper = new ArrayList();
			keeper.add(selectedItems.get(selectedItems.size()-1));
			selectedItems.retainAll(keeper);
		}
	}
	
	public void addToSelection()
	{
		setDirty(true, null);
		additiveMode = true;
	}
	
	/**
	 * @return the ratio of width to height for the first selected item.
	 */
	public double determineAspectRatio()
	{
		Object first = findFirst(Resizeable.class);
		
		if (first==null)
			return 1.0;
			
		Resizeable asResizeable = (Resizeable) first;
		return Math.abs(asResizeable.getWidth()/asResizeable.getHeight());
	}
	
	public List getAuxiliarySelectedItems()
	{
		return auxiliarySelectedItems;
	}
	
	public void setAuxiliarySelectedItems(List designatedAuxiliarySelectedItems)
	{
		auxiliarySelectedItems = designatedAuxiliarySelectedItems;
	}
}
