/**
 Copyleft Aug 11, 2004 by Dave Horlick
*/

package com.smithandtinkers.layout;

/**
 * Characterizes the vertical alignment of a {@link TextShape}.
 *
 * @author dhorlick
 */
public final class VerticalAlignment
{
	public final static VerticalAlignment TOP = new VerticalAlignment("Top");
	public final static VerticalAlignment CENTER = new VerticalAlignment("Center");
	public final static VerticalAlignment BOTTOM = new VerticalAlignment("Bottom");
	
	private String name;
	
	private VerticalAlignment(String designatedName)
	{
		setName(designatedName);
	}
	
	public String getName()
	{
		return name;
	}
	
	private void setName(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return getName();
	}
}
