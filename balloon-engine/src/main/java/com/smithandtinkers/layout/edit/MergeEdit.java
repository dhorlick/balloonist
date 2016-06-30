/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.Kid;

import java.util.Iterator;
import java.util.List;


/**
 *
 * @author dhorlick
 */
public class MergeEdit extends ParticularsEdit
{
	private Balloon amalgamated;
	
	public MergeEdit()
	{
		super(MENU_TEXT.getString("mergeLabel"), TextShape.class);
	}
	
	public boolean addEdit(javax.swing.undo.UndoableEdit anEdit)
	{
		return false;
	}

	public boolean hasEffect()
	{
		return (affectedItems.size()>1);
	}

	public boolean execute()
	{
		if (hasEffect())
		{
			List granddad = null;
			
			amalgamated = new Balloon();
			BalloonEngineState.getInstance().applyStyleDefaultsTo(amalgamated);
			
			int verticals = 0;
			int horizontals = 0;
			
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
			{
				TextShape textShape = (TextShape) affectedItems.get(loop);
				
				if (granddad==null)
					granddad = (List) textShape.getParent();
				
				if (textShape.isVertical())
					verticals++;
				else
					horizontals++;
				
				for (int subloop=textShape.size()-1; subloop>=0; subloop--)
				{
					Object item = textShape.get(subloop);
					
					// textShape.remove(subloop);
					Object moving = textShape.get(subloop);
					if (moving instanceof Kid)
					{
						Kid asKid = (Kid) moving;
						asKid.setParent(null);
					}
					
					// TODO merge the texts
					
					amalgamated.add(item);
				}
				
				if (textShape.getParent()!=null)
				{
					textShape.getParent().remove(textShape);
					textShape.setParent(null); // i.e. delete
				}
			}
			
			if (verticals>0 && verticals>horizontals)
				amalgamated.setVertical(true);
			
			OrderSectionsSpatiallyEdit orderSectionsSpatiallyEdit = new OrderSectionsSpatiallyEdit();
			orderSectionsSpatiallyEdit.execute(amalgamated);
			
			// We don't need to provide for undoing the OrderSectionsSpatiallyEdit, since undoing the merge would destroy all the sections anyway
			
			List oldSequence = (List) orderSectionsSpatiallyEdit.getOldValues().get(amalgamated);
			
			Iterator walkNewSequence = amalgamated.iterator();
			
			while (walkNewSequence.hasNext())
			{
				Object perch = walkNewSequence.next();
				
				// find corresponding index in old sequence
				
				int correspondingIndexInOldSequence = oldSequence.indexOf(perch);
				TextShape undead = (TextShape) affectedItems.get(correspondingIndexInOldSequence);
				amalgamated.getStraw().absorb(undead.getStraw());
			}
			
			granddad.add(amalgamated);
			
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean backout()
	{
		if (hasEffect())
		{
			for (int amalgamatedShapesLoop=amalgamated.size()-1; amalgamatedShapesLoop>=0; amalgamatedShapesLoop--)
			{
				if (absorbable(amalgamated.get(amalgamatedShapesLoop)))
					amalgamated.remove(amalgamatedShapesLoop);
			}
			
			List granddad = (List) amalgamated.getParent();
			
			if (granddad!=null)
			{
				granddad.remove(amalgamated);
				
				for (int affectedTextShapesLoop=0; affectedTextShapesLoop<=affectedItems.size()-1; affectedTextShapesLoop++)
				{
					Object prodigal = affectedItems.get(affectedTextShapesLoop);
					
					granddad.add(0, prodigal);
					
					// re-establish prodigal son as parent of grandchildren
					
					if (prodigal instanceof List)
					{
						List prodigalAsList = (List) prodigal;
						for (int grandkidLoop=0; grandkidLoop<=prodigalAsList.size()-1; grandkidLoop++)
						{
							if (prodigalAsList.get(grandkidLoop) instanceof Kid)
							{
								Kid asKid = (Kid) prodigalAsList.get(grandkidLoop);
								asKid.setParent(prodigalAsList);
								
								// TODO worry about godparentage?
							}
						}
					}
				}
			}
		}
		
		return false; 
	}
	
	public boolean absorbable(Object requestedItem)
	{
		if (!hasEffect())
			return false;
		
		// System.out.println("evaluating absorbability...");
		
		for (int loop=1; loop<=affectedItems.size()-1; loop++)
		{
			List parent = (List) affectedItems.get(loop);
			
			for (int subloop=0; subloop<=parent.size()-1; subloop++)
			{
				if (requestedItem==parent.get(subloop))
				{
					// System.out.println("absorbable: true!");
					return true;
				}
			}
		}
		
		return false;
	}
}
