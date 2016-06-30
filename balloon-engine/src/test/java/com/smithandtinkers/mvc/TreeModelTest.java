/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import javax.swing.tree.TreeModel;

/**
 * @author dhorlick
 */
public class TreeModelTest
{
	public static void printTreeModel(TreeModel tm)
	{
		System.out.println(tm.getRoot());
		printTreeLevel(tm, tm.getRoot(), 1);
	}
	
	public static void printTreeLevel(TreeModel tm, Object obj, int level)
	{
		// System.out.println("Hello! obj="+obj+". tm.isLeaf(obj)="+tm.isLeaf(obj));
		// System.out.println("tm.getChildCount(obj)="+tm.getChildCount(obj));
		
		for (int loop=0; loop<=tm.getChildCount(obj)-1; loop++)
		{
			for (int indent=0; indent<=level-1; indent++)
				System.out.print("\t");
			
			boolean leaf = tm.isLeaf(tm.getChild(obj, loop));
			
			if (leaf)
				System.out.print("* ");
				
			System.out.println(tm.getChild(obj, loop));
			
			if (!leaf)
				printTreeLevel(tm, tm.getChild(obj, loop), level+1);
		}
	}
}
