/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.graphics.DrawingContext;
import com.smithandtinkers.io.ArchiveContext;
import com.smithandtinkers.layout.Alignable;
import com.smithandtinkers.layout.Interactive;
import com.smithandtinkers.layout.Selection;
import com.smithandtinkers.layout.VisiblySelectable;
import com.smithandtinkers.mvc.NameChangeEvent;
import com.smithandtinkers.mvc.SingleThreadedChangeSupport;
import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.Named;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class InteractivePerch extends AbstractInteractiveDecorator implements Perch, VisiblySelectable, Alignable, Named
{
	private TrimmedInteractive inner;
	private TrimmedInteractive outer;
	private String name;
	private double gapBetweenMarginAndText;
	
	public InteractivePerch(Interactive designatedInteractive, Marginal designatedMarginal)
	{
		super(designatedInteractive);
		setName(AbstractNamed.NAMES_TEXT.getString("shapeLabel"));
		inner = trim(designatedInteractive, designatedMarginal, true);
		outer = trim(designatedInteractive, designatedMarginal, false);
	}
	
	protected abstract TrimmedInteractive trim(Interactive designatedInteractive, Marginal designatedMarginal, boolean designatedInner);
	
	public Shapeable getInner()
	{
		return inner;
	}

	public Shapeable getOuter()
	{
		return outer;
	}

	public Shapeable getUnperched()
	{
		return getInteractive();
	}

	public static InteractivePerch perch(Interactive interactive, Marginal designatedMarginal)
	{
		if (interactive instanceof Parallelogram)
			return new ParallelogramPerch((Parallelogram)interactive, designatedMarginal);
		
		return null;
	}

	public void save(Document doc, Node parent, ArchiveContext archiveContext)
	{
		super.save(doc, parent, archiveContext);
	}

	public String getName()
	{
		return name;
	}
	
	public void setName(String designatedName)
	{
		if (name != designatedName && (designatedName==null || !designatedName.equals(name)))
		{
			name = designatedName;
			SingleThreadedChangeSupport.fireChangeEvent(this, new NameChangeEvent(this));
		}
	}
	
	public String toString()
	{
		if (name==null)
			return "shape";
		else
			return name;
	}
	
	public void paintSelection(DrawingContext drawingContext)
	{
		// not applicable here
	}
	
	public void paintSelectionIfAppropriate(DrawingContext drawingContext, Selection selected)
	{
		if (getUnperched() instanceof VisiblySelectable)
		{
			VisiblySelectable visiblySelectable = (VisiblySelectable)getUnperched();
			visiblySelectable.paintSelectionIfAppropriate(drawingContext, selected);
		}
	}

	public void setParent(java.util.Collection designatedParent)
	{
		super.setParent(designatedParent);
		
		if (designatedParent!=null && designatedParent instanceof Marginal)
		{
			Marginal marginal = (Marginal) designatedParent;
			inner.setMarginal(marginal);
			outer.setMarginal(marginal);
		}
	}
	
	public void setMarginal(Marginal designatedMarginal)
	{
		setParent(designatedMarginal);
	}
	
	public double getGapBetweenMarginAndText()
	{
		return gapBetweenMarginAndText;
	}

	public void setGapBetweenMarginAndText(double designatedGapBetweenMarginAndText)
	{
		gapBetweenMarginAndText = designatedGapBetweenMarginAndText;
	}
}