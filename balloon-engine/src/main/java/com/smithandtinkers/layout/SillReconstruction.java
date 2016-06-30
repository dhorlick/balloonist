/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout;

import com.smithandtinkers.layout.edit.RebuildSillEdit;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.undo.CompoundEdit;

/**
 *
 * @author dhorlick
 */
public class SillReconstruction
{	
	private Map map = new LinkedHashMap(); // LinkedHashMap<Sill, RebuildSillEdit>
	
	public SillReconstruction()
	{
	}
	
	/**
	 * @param designatedEdit a non-null RebuildSillEdit with non-null fields
	 */
	public void update(RebuildSillEdit designatedEdit)
	{
		if (designatedEdit==null || designatedEdit.getSill()==null)
			throw new IllegalArgumentException();
		
		if (map.containsKey(designatedEdit.getSill()))
		{
			RebuildSillEdit existing = (RebuildSillEdit) map.get(designatedEdit.getSill());
			
			// System.out.println("found existing: "+existing);
			
			existing.setNewAperturePerch(designatedEdit.getNewAperturePerch());
			existing.setNewLocation(designatedEdit.getNewLocation());
			
			// keep the existing "old" values
			
			// System.out.println("\tafter mod: "+existing);
		}
		else
		{
			// System.out.println("brand new: "+designatedEdit);
			map.put(designatedEdit.getSill(), designatedEdit);
		}
	}
	
	/**
	 * Adds all of the accumulated edits from this sill reconstruction to the
	 * designated compound edit.
	 */
	public void addAllTo(CompoundEdit designatedCompoundEdit)
	{
		Iterator walk = map.values().iterator();
		
		while (walk.hasNext())
		{
			RebuildSillEdit rebuildSillEdit = (RebuildSillEdit) walk.next();
			designatedCompoundEdit.addEdit(rebuildSillEdit);
		}
	}
	
	public String toString()
	{
		return "SillReconstruction: "+map.toString();
	}
}
