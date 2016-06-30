/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.mvc.UrlHolder;
import com.smithandtinkers.text.SpacelessDocument;
import com.smithandtinkers.util.BugException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.InputVerifier;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;


/**
 *
 * @author dhorlick
 */
public class LabeledUrlField extends AbstractLabeledFieldComponent
{
	private final static UrlVerifier URL_VERIFIER = new UrlVerifier();
	
	private UrlHolder model = new DefaultUrlHolder();
	
	public LabeledUrlField(JLabel designatedLabel, JTextField designatedTextField)
	{
		super(designatedLabel, designatedTextField);
		
		if (designatedTextField!=null && designatedTextField.getDocument()==null
				|| !(designatedTextField.getDocument() instanceof SpacelessDocument))
		{
			designatedTextField.setDocument(new SpacelessDocument());
		}
	}

	public void stateChanged(ChangeEvent e)
	{
		stateChanged();
	}
	
	public void stateChanged()
	{
		if (getModel()!=null)
		{
			if (getModel().getUrl()!=null)
			{
				String asText = String.valueOf(getModel().getUrl());
				
				if (field.getText()==null || !field.getText().equals(asText))
					field.setText(asText);
			}
			else
			{
				field.setText("");
			}
		}
	}
	
	private static class UrlVerifier extends InputVerifier
	{
		public boolean verify(javax.swing.JComponent input)
		{
			JTextField inputAsTextField = (JTextField) input;
			if (inputAsTextField==null || inputAsTextField.getText()==null || inputAsTextField.getText().length()==0)
				return true;

			try
			{	
				new URL(inputAsTextField.getText());
				return true;
			}
			catch (MalformedURLException exception)
			{
				Toolkit.getDefaultToolkit().beep();
				System.out.println("bad URL: "+inputAsTextField.getText());
				
				return false;
			}
		}
	}
	
	public void setField(JTextField designatedField)
	{
		super.setField(designatedField);
		
		if (!(field.getInputVerifier() instanceof UrlVerifier))
		{
			field.setInputVerifier(URL_VERIFIER);
		}
		
		// TODO if there was another field before, remove any NumberVerfier, remove this is a document listener, and remove it from this
		
		field.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				postChange(); // TODO verify that this isn't getting sent out in response to JTextField.setText. to avoid an infinite loop, we only want it to fire if the *user* makes a change.
			}
		});
		
		field.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
			}

			public void focusLost(FocusEvent e)
			{
				postChange();
			}
		});
	}
	
	private void postChange()
	{
		if (getModel()==null)
			throw new IllegalStateException("Model isn't set.");
		
		try
		{
			getModel().setUrl(new URL(field.getText()));
		}
		catch (MalformedURLException exception)
		{
		}
	}

	public UrlHolder getModel()
	{
		return model;
	}

	public void setModel(UrlHolder model)
	{
		this.model = model;
	}
	
	public class DefaultUrlHolder implements UrlHolder
	{
		private URL url;
		
		public void setUrl(URL designatedUrl)
		{
			url = designatedUrl;
		}

		public URL getUrl()
		{
			return url;
		}
	}
}
