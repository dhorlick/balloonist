/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dhorlick
 */
public class TrimExcessItemsEdit extends PresentableEdit
{
	private List listToDeleteFrom;
	private int newSize;
	
	private List trimmed = new ArrayList();
	
	public TrimExcessItemsEdit(List designatedList, int designatedNewSize)
	{
		super("Trim Excess Items"); // TODO i8n
		
		// System.out.println("designatedNewSize="+designatedNewSize);
		
		newSize = designatedNewSize;
		listToDeleteFrom = designatedList;
	}

	public boolean hasEffect()
	{
		if (listToDeleteFrom!=null)
			return true;
		
		return false;
	}

	public boolean execute()
	{
		if (trimmed.size()>0)
			throw new IllegalStateException("Can only be executed once.");
		
		for (int deleteLoop=listToDeleteFrom.size()-1; deleteLoop>=newSize; deleteLoop--)
		{
			// System.out.println("removing: "+deleteLoop);
			trimmed.add(listToDeleteFrom.remove(deleteLoop));
		}
		
		return true;
	}

	public boolean backout()
	{
		for (int loop=trimmed.size()-1; loop>=0; loop--)
		{
			listToDeleteFrom.add(trimmed.get(loop));
		}
		
		trimmed.clear();
		
		return true;
	}
}
