/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.mvc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;

import com.smithandtinkers.util.AbstractKid;
import com.smithandtinkers.util.EqualsFriend;
import javax.swing.tree.TreeModel;
import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class ListTreeModelAdaptorTest extends TestCase
{
	public static void main(String [] args)
	{
		new ListTreeModelAdaptorTest().go();
	}
	
	public static TwoListsAndTreeModel generate()
	{
		final TreeTypesafeList ttl1 = new TreeTypesafeList(List.class)
		{
			public String toString()
			{
				return "kingdoms";
			}
			
			public int hashCode()
			{
				int result = super.hashCode();
				System.out.println(getClass().getName()+" "+toString()+" overheard: hashCode="+result);
				return result;
			}
		};
		
		final TreeTypesafeList ttl2 = new TreeTypesafeList(KidString.class)
		{
			public String toString()
			{
				return "animals";
			}
			
			public int hashCode()
			{
				int result = super.hashCode();
				System.out.println(getClass().getName()+" "+toString()+" overheard: hashCode="+result);
				return result;
			}
		};
		ttl2.add(new KidString("reptiles"));
		ttl2.add(new KidString("amphibians"));
		ttl2.add(new KidString("mammals"));
		ttl2.add(new KidString("fish"));
		
		ttl1.add(ttl2);
		
		final ListTreeModelAdaptor ltma = new ListTreeModelAdaptor() {

			public Object getRoot()
			{
				return ttl1;
			}
		};
		
		ttl1.setTreeModelSupport(ltma.getTreeModelSupport());
		
		TwoListsAndTreeModel tlatm = new TwoListsAndTreeModel();
		tlatm.setTreeModel(ltma);
		tlatm.setTreeTypesafeList1(ttl1);
		tlatm.setTreeTypesafeList2(ttl2);
		
		return tlatm;
	}
	
	public void test()
	{
		TwoListsAndTreeModel tlatm = generate();
		tlatm.getTreeTypesafeList2().add(new KidString("mollusks"));
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void go()
	{
		JFrame frame = new JFrame("test");
		frame.setSize(400,300);
		
		TwoListsAndTreeModel tlatm = generate();
		
		final TreeModel ltma = tlatm.getTreeModel();
		final TreeTypesafeList ttl1 = tlatm.getTreeTypesafeList1();
		final TreeTypesafeList ttl2 = tlatm.getTreeTypesafeList2();
		
		// System.out.println("ttl2.getTreeModelSupport()="+ttl2.getTreeModelSupport()+", before model hookup");
		
		JTree tree = new JTree(ltma);
		
		// System.out.println("ttl2.getTreeModelSupport()="+ttl2.getTreeModelSupport()+", after model hookup");
		
		tree.setShowsRootHandles(true);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(tree, BorderLayout.CENTER);
		
		JButton button = new JButton(new AbstractAction("Add Mollusks")
			{
				public void actionPerformed(ActionEvent e)
				{
					ttl2.add(new KidString("mollusks"));
					System.out.println("ltma=");
					TreeModelTest.printTreeModel(ltma);
				}
			}
		);
		
		panel.add(button, BorderLayout.SOUTH);
		
		frame.getContentPane().add(panel);
		frame.setVisible(true);
	}
	
	public static class KidString extends AbstractKid implements Serializable
	{
		private String string;
		
		public KidString(String designatedString)
		{
			string = designatedString;
		}
		
		public String toString()
		{
			return string;
		}
		
		public boolean equals(Object obj)
		{
			if (obj==null || (!(obj instanceof KidString)))
				return false;
			
			KidString other = (KidString) obj;
			
			return EqualsFriend.equals(string, other.string);
		}
		
		public int hashCode()
		{
			int result = EqualsFriend.hashCode(string);
			System.out.println(getClass().getName()+" "+toString()+" overheard: hashCode="+result);
			return result;
		}
	}
	
	private static class TwoListsAndTreeModel
	{
		private TreeTypesafeList treeTypesafeList1;
		private TreeTypesafeList treeTypesafeList2;
		private TreeModel treeModel;
		
		public TreeTypesafeList getTreeTypesafeList1()
		{
			return treeTypesafeList1;
		}

		public void setTreeTypesafeList1(TreeTypesafeList treeTypesafeList1)
		{
			this.treeTypesafeList1 = treeTypesafeList1;
		}

		public TreeTypesafeList getTreeTypesafeList2()
		{
			return treeTypesafeList2;
		}

		public void setTreeTypesafeList2(TreeTypesafeList treeTypesafeList2)
		{
			this.treeTypesafeList2 = treeTypesafeList2;
		}

		public TreeModel getTreeModel()
		{
			return treeModel;
		}

		public void setTreeModel(TreeModel treeModel)
		{
			this.treeModel = treeModel;
		}
	}
}
