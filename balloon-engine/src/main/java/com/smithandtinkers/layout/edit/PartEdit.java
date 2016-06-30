/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.BalloonEngineState;
import com.smithandtinkers.layout.*;
import com.smithandtinkers.util.BugException;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.TypesafeList;

import java.text.AttributedString;
import java.util.Iterator;
import java.util.List;
import javax.swing.undo.UndoableEdit;


/**
 *
 * @author dhorlick
 */
public class PartEdit extends ParticularsEdit
{
	private TypesafeList pastings = new TypesafeList(PasteEdit.class);
	
	public PartEdit()
	{
		super(MENU_TEXT.getString("partLabel"), TextShape.class);
	}

	public boolean addEdit(UndoableEdit anEdit)
	{
		return false;
	}

	public boolean hasEffect()
	{
		return (affectedItems.size()>0);
	}

	public boolean execute()
	{
		if (hasEffect())
		{
			pastings.clear();
			
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
			{
				// System.out.println("\tPartEdit loop: "+loop);
				
				if (affectedItems.get(loop) instanceof TextShape)
				{
					TextShape asTextShape = (TextShape) affectedItems.get(loop);
					
					if (asTextShape.size()>1 && asTextShape.getParent()!=null && asTextShape.getParent() instanceof List)
					{
						TypesafeList granddad = (TypesafeList)asTextShape.getParent();
						granddad.remove(asTextShape);
						
						// System.out.println("parting "+asTextShape.size());
						PasteEdit pasteEdit = new PasteEdit(granddad);
						
						// System.out.println("asTextShape.size()="+asTextShape.size());
						
						for (int subloop=0; subloop<=asTextShape.size()-1; subloop++)
						{
							Object doomed = asTextShape.get(subloop); // no need to actually remove the perch, since we're also going to remove the text shape
							Balloon addition = new Balloon();
							BalloonEngineState.getInstance().applyStyleDefaultsTo(addition);
							AttributedString attrString = asTextShape.getStraw().toAttributedString(subloop);
							addition.getStraw().load(attrString);
							
							if (doomed instanceof Kid)
							{
								Kid asKid = (Kid) doomed;
								asKid.setParent(null);
							}
							
							addition.add(doomed);
							pasteEdit.add(addition);
							
							// System.out.println("adding: "+addition);
						}
						
						if (pasteEdit.execute())
						{
							pastings.add(pasteEdit);
						}
						// System.out.println("granddad.size() afterwards="+granddad.size());
					}
				}
			}
			
			return (pastings.size()>0);
		}
		
		return false;
	}

	public boolean backout()
	{
		if (hasEffect())
		{
			Iterator walkPastings = pastings.iterator();
			
			List granddad = null;
			
			while (walkPastings.hasNext())
			{
				PasteEdit pasteEdit = (PasteEdit) walkPastings.next();
				pasteEdit.backout();
				
				if (granddad==null)
					granddad = pasteEdit.getParent();
			}
			
			if (granddad==null)
				throw new BugException("Couldn't find granddad");
			
			for (int loop=0; loop<=affectedItems.size()-1; loop++)
			{
				if (affectedItems.get(loop) instanceof TextShape)
				{
					TextShape asTextShape = (TextShape) affectedItems.get(loop);
					granddad.add(asTextShape);
					
					// restablish parentage
					
					Iterator walk = asTextShape.iterator();
					while (walk.hasNext())
					{
						Kid kid = (Kid) walk.next();
						kid.setParent(asTextShape);
					}
				}
			}
			
			return true;
		}
		
		return false;
	}
}
