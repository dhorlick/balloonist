package com.smithandtinkers.gui;

import com.smithandtinkers.geom.Perch;
import com.smithandtinkers.layout.edit.RearrangeEdit;
import com.smithandtinkers.layout.Sill;
import com.smithandtinkers.util.Kid;
import com.smithandtinkers.util.Logger;
import com.smithandtinkers.util.PlatformFriend;
import com.smithandtinkers.util.TypesafeList;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEditSupport;


/**
 * Substantially copied from DnDJTree from Swing Hacks, by Joshua Marrinacci & Chris Adamson
 */
public class RearrangeableTree extends JTree
	implements DragSourceListener, DropTargetListener, DragGestureListener
{
	static DataFlavor localObjectFlavor;
	static
	{
		try
		{
			localObjectFlavor =
				new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
		}
		catch (ClassNotFoundException cnfe)
		{ cnfe.printStackTrace(); }
	}
	static DataFlavor[] supportedFlavors = { localObjectFlavor };
	
	private DragSource dragSource;
	private DropTarget dropTarget;
	private Object dropTargetNode = null;
	private Object draggedNode = null;
	private RowRegion rowRegion;
	private boolean silent;
	
	private UndoableEditSupport undoableEditSupport = new UndoableEditSupport();
	
	private static RowRegion ROW_TOP = new RowRegion();
	private static RowRegion ROW_INTERIOR = new RowRegion();
	private static RowRegion ROW_BOTTOM = new RowRegion();
	
	public RearrangeableTree()
	{
		super();
		setLargeModel(true);
		setCellRenderer(new DnDTreeCellRenderer());
		setModel(new DefaultTreeModel(new DefaultMutableTreeNode("default")));
		dragSource = new DragSource();
		DragGestureRecognizer dgr =
			dragSource.createDefaultDragGestureRecognizer(this,
			DnDConstants.ACTION_MOVE,
			this);
		dropTarget = new DropTarget(this, this);
	}
	
	// DragGestureListener
	public void dragGestureRecognized(DragGestureEvent dge)
	{
		Logger.println("dragGestureRecognized");
		// find object at this x,y
		Point clickPoint = dge.getDragOrigin();
		TreePath path = getPathForLocation(clickPoint.x, clickPoint.y);
		if (path == null)
		{
			Logger.println("not on a node");
			return;
		}
		
		Object thingie = path.getLastPathComponent();
		
		/* Logger.println("thingie="+thingie);
		if (thingie!=null)
			Logger.println("\twith class: "+thingie.getClass()); */
		
		draggedNode = thingie;
		Transferable trans = new RJLTransferable(draggedNode);
		dragSource.startDrag(dge,Cursor.getDefaultCursor(),
			trans, this);
	}
	// DragSourceListener events
	public void dragDropEnd(DragSourceDropEvent dsde)
	{
		Logger.println("dragDropEnd()");
		dropTargetNode = null;
		draggedNode = null;
		repaint();
	}
	public void dragEnter(DragSourceDragEvent dsde)
	{}
	public void dragExit(DragSourceEvent dse)
	{}
	public void dragOver(DragSourceDragEvent dsde)
	{}
	public void dropActionChanged(DragSourceDragEvent dsde)
	{}
	
	// DropTargetListener events
	public void dragEnter(DropTargetDragEvent dtde)
	{
		Logger.println("dragEnter");
		dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		Logger.println("accepted dragEnter");
	}
	
	public void dragExit(DropTargetEvent dte)
	{}
	public void dragOver(DropTargetDragEvent dtde)
	{
		// figure out which cell it's over, no drag to self
		Point dragPoint = dtde.getLocation();
		
		// Logger.println("dragPoint="+dragPoint);
		
		TreePath path = getPathForLocation(dragPoint.x, dragPoint.y);
		
		if (path == null)
		{
			dropTargetNode = null;
		}
		else
		{
			dropTargetNode = path.getLastPathComponent();
			
			Rectangle pathBounds = getPathBounds(path);
			// Logger.println("pathBounds="+pathBounds);
			
			if (dragPoint.getY()==pathBounds.getY())
				rowRegion = ROW_TOP;
			else if (dragPoint.getY()==(pathBounds.getY()+pathBounds.getHeight()-1))
				rowRegion = ROW_BOTTOM;
			else
				rowRegion = ROW_INTERIOR;
		}
		
		repaint();
	}
	public void drop(DropTargetDropEvent dtde)
	{
		RearrangeEdit rearrangeEdit = new RearrangeEdit();
		Point dropPoint = dtde.getLocation();
		// int index = locationToIndex (dropPoint);
		TreePath path = getPathForLocation(dropPoint.x, dropPoint.y);
		
		if (path==null)
			return;
		
		Logger.println("drop path is " + path);
		boolean dropped = false;
		try
		{
			dtde.acceptDrop(DnDConstants.ACTION_MOVE);
			
			Object droppedObject =
				dtde.getTransferable().getTransferData(localObjectFlavor);
			
			MutableTreeNode droppedNode = null;
			if (droppedObject instanceof MutableTreeNode)
			{
				// remove from old location
				droppedNode = (MutableTreeNode) droppedObject;
				((DefaultTreeModel)getModel()).removeNodeFromParent(droppedNode);
				
					// TODO ditch? 2005-12-12
			}
			else
			{
				droppedNode = new DefaultMutableTreeNode(droppedObject);
			}
			// insert into spec'd path.  if dropped into a parent
			// make it last child of that parent
			
			final Object lastPathComponent = path.getLastPathComponent();
			
			if (lastPathComponent==droppedObject)
				return;	// can't drag self onto self
			
			if (lastPathComponent instanceof Collection && rowRegion==ROW_INTERIOR) // user has dragged an object into a node
			{
				TypesafeList prospectiveNewContainerAsTypesafeList = null;
				
				if (lastPathComponent instanceof TypesafeList)
					prospectiveNewContainerAsTypesafeList = (TypesafeList) lastPathComponent;
				else if (lastPathComponent instanceof Perch)
				{
					Perch asPerch = (Perch) lastPathComponent;
					if (asPerch.getUnperched()!=null && asPerch.getUnperched() instanceof TypesafeList)
						prospectiveNewContainerAsTypesafeList = (TypesafeList) asPerch.getUnperched();
				}
				
				if (prospectiveNewContainerAsTypesafeList != null)
				{
					if (!prospectiveNewContainerAsTypesafeList.getConstituentType().isAssignableFrom(droppedObject.getClass()))
					{
						Logger.println("dropped item would not fit in requested new destination");
						Toolkit.getDefaultToolkit().beep();
								
						return;
					}
				}				
				
				if (droppedObject instanceof Kid)
				{
					Kid droppedObjectAsKid = (Kid) droppedObject;
					if (droppedObjectAsKid.getParent()!=null)
					{
						if (droppedObjectAsKid.getParent() instanceof TypesafeList)
						{
							TypesafeList oldParentOfDroppedObjectAsTypesafeList = (TypesafeList) droppedObjectAsKid.getParent();
							if (!oldParentOfDroppedObjectAsTypesafeList.getConstituentType().isAssignableFrom(droppedObject.getClass()))
							{
								Logger.println("## incompatible constituent type... canceling drop");
								
								Logger.println("droppedObject.getClass()="+droppedObject.getClass());
								Logger.println("parentOfDroppedObjectAsTypesafeList.getConstituentType()="+oldParentOfDroppedObjectAsTypesafeList.getConstituentType());
								
								Toolkit.getDefaultToolkit().beep();
								
								return;
							}
							
							// sills can only be relocated within the outermost sill
							
							if (lastPathComponent instanceof Sill && droppedObject instanceof Perch)
							{
								Sill lastPathComponentAsSill = (Sill) lastPathComponent;
								
								if (lastPathComponentAsSill.getParent()!=null)
								{
									Logger.println("can't drag perch into a sill");
									Toolkit.getDefaultToolkit().beep();
									return;
								}
							}
						}
						
						// droppedObjectAsKid.getParent().remove(droppedObjectAsKid);
						
						if (droppedObjectAsKid.getParent() instanceof List)
							rearrangeEdit.setOldList((List)droppedObjectAsKid.getParent());
						else
						{
							Logger.println("droppedObjectAsKid.getParent() is not a List");
							return;
						}
					}
				}
				
				Collection lastPathComponentAsCollection = (Collection) lastPathComponent;
				// lastPathComponentAsCollection.add(droppedObject); // TODO add @ position 0?
				
				if (!(lastPathComponentAsCollection instanceof List))
				{
					Logger.println("lastPathComponentAsCollection is not a List");
					
					return;
				}
				
				rearrangeEdit.setNewList((List)lastPathComponentAsCollection);
				rearrangeEdit.add(droppedObject);
			}
			else if (lastPathComponent instanceof Kid) // user has dragged an object between to leaves
			{
				Kid lastPathComponentAsKid = (Kid) lastPathComponent;
				if (lastPathComponentAsKid.getParent()!=null && lastPathComponentAsKid.getParent() instanceof List)
				{
					List parentOfLastPathComponentAsList = (List) lastPathComponentAsKid.getParent();
					int indexOfLastComponent = parentOfLastPathComponentAsList.indexOf(lastPathComponentAsKid);
					
					if (indexOfLastComponent==-1)
					{
						System.err.println("tree is messed up");
						return;
					}
					
					if (droppedObject instanceof Kid)
					{
						Kid droppedObjectAsKid = (Kid) droppedObject;
						if (droppedObjectAsKid.getParent()!=null)
						{
							if (parentOfLastPathComponentAsList instanceof TypesafeList)
							{
								Logger.println("parent of new position would be a typesafe list...");
								
								TypesafeList parentOfLastPathComponentAsTypesafeList = (TypesafeList) parentOfLastPathComponentAsList;
								
								if (!parentOfLastPathComponentAsTypesafeList.getConstituentType().isAssignableFrom(droppedObject.getClass()))
								{
									Logger.println("prospective new neighbor has incompatible constituent type... canceling drop");
									Logger.println("droppedObject.getClass()="+droppedObject.getClass());
									Logger.println("parentOfLastPathComponentAsTypesafeList.getConstituentType()="+parentOfLastPathComponentAsTypesafeList.getConstituentType());
									
									Toolkit.getDefaultToolkit().beep();
									
									return;
								}
							}
							
							if (droppedObjectAsKid.getParent() instanceof List)
							{
								rearrangeEdit.setOldList((List)droppedObjectAsKid.getParent());
							}
							else
							{
								Logger.println("droppedObjectAsKid.getParent() is not a list");
								return;
							}
						}
					}
					
					indexOfLastComponent = parentOfLastPathComponentAsList.indexOf(lastPathComponentAsKid);
					
					if (rowRegion==ROW_TOP)
					{
						// TODO check for constituent type acceptability
						
						// parentOfLastPathComponentAsList.add(indexOfLastComponent, droppedObject);
						rearrangeEdit.setNewList(parentOfLastPathComponentAsList);
						rearrangeEdit.add(droppedObject);
					}
					else if (rowRegion==ROW_BOTTOM) // and it should
					{
						// TODO check for constituent type acceptability
						
						// parentOfLastPathComponentAsList.add(indexOfLastComponent+1, droppedObject);
						rearrangeEdit.setNewList(parentOfLastPathComponentAsList);
						rearrangeEdit.add(droppedObject);
						rearrangeEdit.setOldIndex(indexOfLastComponent+1);
					}
				}
			}
			else
			{
				Logger.println("unrecognized lastPathComponent type: "+lastPathComponent.getClass().getName());
			}
			
			if (rearrangeEdit.hasEffect())
			{
				rearrangeEdit.execute();
				undoableEditSupport.postEdit(rearrangeEdit);
			}
			
			dropped = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		dtde.dropComplete(dropped);
	}
	
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
	}
	
	// test
	public static void main(String[] args)
	{
		JTree tree = new RearrangeableTree();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("People");
		DefaultMutableTreeNode set1 = new DefaultMutableTreeNode("Set 1");
		DefaultMutableTreeNode set2 = new DefaultMutableTreeNode("Set 2");
		DefaultMutableTreeNode set3 = new DefaultMutableTreeNode("Set 3");
		set1.add(new DefaultMutableTreeNode("Chris"));
		set1.add(new DefaultMutableTreeNode("Kelly"));
		set1.add(new DefaultMutableTreeNode("Keagan"));
		set2.add(new DefaultMutableTreeNode("Joshua"));
		set2.add(new DefaultMutableTreeNode("Kimi"));
		set3.add(new DefaultMutableTreeNode("Michael"));
		set3.add(new DefaultMutableTreeNode("Don"));
		set3.add(new DefaultMutableTreeNode("Daniel"));
		root.add(set1);
		root.add(set2);
		set2.add(set3);
		DefaultTreeModel mod = new DefaultTreeModel(root);
		tree.setModel(mod);
		// expand all
		for (int i=0; i<tree.getRowCount(); i++)
			tree.expandRow(i);
		// show tree
		JScrollPane scroller =
			new JScrollPane(tree,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JFrame frame = new JFrame("DnD JTree");
		frame.getContentPane().add(scroller);
		frame.pack();
		frame.setVisible(true);
	}
	
	class RJLTransferable implements Transferable
	{
		Object object;
		public RJLTransferable(Object o)
		{
			object = o;
		}
		public Object getTransferData(DataFlavor df)
		throws UnsupportedFlavorException, IOException
		{
			if (isDataFlavorSupported(df))
				return object;
			else
				throw new UnsupportedFlavorException(df);
		}
		public boolean isDataFlavorSupported(DataFlavor df)
		{
			return (df.equals(localObjectFlavor));
		}
		public DataFlavor[] getTransferDataFlavors()
		{
			return supportedFlavors;
		}
	}
	
	// custom renderer
	class DnDTreeCellRenderer
		extends DefaultTreeCellRenderer
	{
		boolean isTargetNode;
		boolean isTargetNodeLeaf;
		boolean isLastItem;
		Insets normalInsets, lastItemInsets;
		int BOTTOM_PAD = 30;
		
		public DnDTreeCellRenderer()
		{
			super();
			normalInsets = super.getInsets();
			lastItemInsets =
				new Insets(normalInsets.top,
				normalInsets.left,
				normalInsets.bottom + BOTTOM_PAD,
				normalInsets.right);
		}
		
		public Component getTreeCellRendererComponent(JTree tree,
			Object value,
			boolean isSelected,
			boolean isExpanded,
			boolean isLeaf,
			int row,
			boolean hasFocus)
		{
			isTargetNode = (value == dropTargetNode);
			isTargetNodeLeaf = (isTargetNode &&
				(isLeaf(value)));
			// isLastItem = (index == list.getModel().getSize()-1);
			boolean showSelected = isSelected &
				(dropTargetNode == null);
			return super.getTreeCellRendererComponent(tree, value,
				isSelected, isExpanded,
				isLeaf, row, hasFocus);
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if (isTargetNode)
			{
				g.setColor(Color.black);
				if (rowRegion==ROW_TOP || isTargetNodeLeaf)
				{
					g.drawLine(0, 0, getSize().width, 0);
					
					if (PlatformFriend.RUNNING_ON_MAC)
					{
						final Shape oldClip = g.getClip();
						g.setClip(null);
						g.drawOval(-5, -2, 4, 4);
						g.setClip(oldClip);
					}
				}
				else if (rowRegion==ROW_BOTTOM)
				{
					final int Y_COORD = getSize().height-1;
					g.drawLine(0, Y_COORD, getSize().width, Y_COORD);
					
					if (PlatformFriend.RUNNING_ON_MAC)
					{
						final Shape oldClip = g.getClip();
						g.setClip(null);
						g.drawOval(-5, Y_COORD-2, 4, 4);
						g.setClip(oldClip);
					}
				}
				else
				{
					g.drawRect(0, 0, getSize().width-1, getSize().height-1);
				}
			}
		}
	}
	
	public static boolean isLeaf(Object designatedObject)
	{
		if (designatedObject instanceof TreeNode)
		{
			return ((TreeNode)designatedObject).isLeaf();
		}
		
		if (designatedObject instanceof Collection)
		{
			// return (((Collection)designatedObject).size()==0);
			return false;
		}
		
		return true;
	}
	
	public void addUndoableEditListener(UndoableEditListener l)
	{
		undoableEditSupport.addUndoableEditListener(l);
	}
	
	public void removeUndoableEditListener(UndoableEditListener l)
	{
		undoableEditSupport.removeUndoableEditListener(l);
	}
	
	private static class RowRegion
	{		
	}
	
	protected void fireValueChanged(TreeSelectionEvent e)
	{
		if (!isSilent())
			super.fireValueChanged(e);
	}
	
	public boolean isSilent()
	{
		return silent;
	}
	
	public void setSilent(boolean designatedSilent)
	{
		silent = designatedSilent;
	}
}