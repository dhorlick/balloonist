/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.layout.edit;

import com.smithandtinkers.layout.*;
import java.util.ResourceBundle;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


/**
 *
 * @author dhorlick
 */
public abstract class PresentableEdit extends AbstractUndoableEdit
{
	private String presentationName;
	private boolean isolate;
	
	public static ResourceBundle MENU_TEXT = ResourceBundle.getBundle("resources/text/menu");
	
	public PresentableEdit(String designatedPresentationName)
	{
		super();
		setPresentationName(designatedPresentationName);
	}
	
	public String getPresentationName()
	{
		return presentationName;
	}

	public void setPresentationName(String desigantedPresentationName)
	{
		presentationName = desigantedPresentationName;
	}

	public void redo() throws CannotRedoException
	{
		super.redo();
		
		reExecute();
	}
	
	public void reExecute()
	{
		execute();
	}
	
	public void undo() throws CannotUndoException
	{
		super.undo();
		
		backout();
	}
	
	public boolean isIsolate()
	{
		return isolate;
	}

	public void setIsolate(boolean designatedIsolation)
	{
		isolate = designatedIsolation;
	}
	
	public abstract boolean execute();
	
	public abstract boolean backout();
	
	public abstract boolean hasEffect();
}
