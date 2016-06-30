/**
 Copyleft 2006 by Dave Horlick
 */

package com.smithandtinkers.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.UndoableEditListener;

import com.smithandtinkers.util.TypesafeList;


public abstract class AbstractArchiveContext implements ArchiveContext
{
	protected UndoableEditListener listenerToNotifyAboutUndoableEdits;
	protected TypesafeList missingFontFamilies = new TypesafeList(String.class);
	
	public AbstractArchiveContext()
	{
	}
	
	public UndoableEditListener getListenerToNotifyAboutUndoableEdits()
	{
		return listenerToNotifyAboutUndoableEdits;
	}

	public void setListenerToNotifyAboutUndoableEdits(UndoableEditListener designatedListenerToNotifyAboutUndoableEdits)
	{
		listenerToNotifyAboutUndoableEdits = designatedListenerToNotifyAboutUndoableEdits;
	}
	
	public List getMissingFontFamilies()
	{
		return missingFontFamilies;
	}
}
