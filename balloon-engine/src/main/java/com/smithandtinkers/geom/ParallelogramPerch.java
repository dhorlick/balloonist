/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import com.smithandtinkers.layout.Interactive;

import com.smithandtinkers.util.AbstractNamed;
import com.smithandtinkers.util.PubliclyCloneable;

public class ParallelogramPerch extends InteractivePerch implements PubliclyCloneable
{
	public ParallelogramPerch(Parallelogram designatedParallelogram, Marginal designatedMarginal)
	{
		super(designatedParallelogram, designatedMarginal);
		setName(AbstractNamed.NAMES_TEXT.getString("parallelogramLabel"));
	}

	public ParallelogramPerch(Parallelogram designatedParallelogram)
	{
		this(designatedParallelogram, null);
	}
	
	public ParallelogramPerch()
	{
		this(new Parallelogram());
	}

	protected TrimmedInteractive trim(Interactive designatedInteractive, Marginal designatedMarginal, boolean designatedInner)
	{
		validateParallelogram(designatedInteractive);
		
		return new TrimmedParallelogram((Parallelogram)designatedInteractive, designatedMarginal, designatedInner);
	}

	/**
	 * @throws IllegalArgumentException if the designatedInteractive is not a parallelogram
	 */
	private void validateParallelogram(Interactive designatedInteractive)
	{
		if (!(designatedInteractive instanceof Parallelogram))
		{
			throw new IllegalArgumentException();
		}
	}

	protected void setInteractive(Interactive designatedInteractive)
	{
		super.setInteractive((Parallelogram)designatedInteractive);
	}
	
	public Parallelogram getParallelogram()
	{
		return (Parallelogram) getInteractive();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		Parallelogram clonedParallelogram = (Parallelogram) getParallelogram().clone();
		InteractivePerch cloned = new ParallelogramPerch(clonedParallelogram);
		
		return cloned;
	}
}
