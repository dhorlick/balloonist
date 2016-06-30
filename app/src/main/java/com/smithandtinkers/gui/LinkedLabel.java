/**
 * Copyleft 2005 by Dave Horlick

 */

package com.smithandtinkers.gui;

import com.smithandtinkers.util.BugException;
import com.smithandtinkers.util.PlatformFriend;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JLabel;

/**
 *
 * @author dhorlick
 */
public class LinkedLabel extends JLabel
{
	private HypertextPane hypertextPane;
	private HypertextFrame hypertextFrame;
	
	public LinkedLabel(final String designatedUrl, boolean designatedExternal)
	{
		this(designatedUrl, designatedUrl, designatedExternal);
	}

	/**
	 * Creates a linked label that should spawn an external web browser when clicked.
	 */
	public LinkedLabel(final String designatedUrl, final String designatedTitle, final boolean designatedExternal)
	{
		super("<html><u>"+designatedTitle+"</u></html>");
		
		setForeground(Color.blue);
		
		addMouseListener(new MouseListener()
		{
			public void mouseReleased(MouseEvent e)
			{
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
				// System.out.println("un-hover");
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			public void mouseEntered(MouseEvent e)
			{
				// System.out.println("hover");
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			public void mouseClicked(MouseEvent e)
			{
				if (designatedExternal)
					PlatformFriend.openUrl(designatedUrl);
				else
				{
					if (hypertextPane==null)
					{
						hypertextPane = new HypertextPane();
					}
					else
					{
						if (!hypertextFrame.isVisible())
							hypertextFrame.setVisible(true);
						
						hypertextFrame.toFront();
					}
					
					try
					{
						URL url = ClassLoader.getSystemResource(designatedUrl);
						
						if (url==null)
						{
							// System.out.println("ClassLoader.getSystemClassLoader()="+ClassLoader.getSystemClassLoader());
							throw new IllegalArgumentException("ClassLoader couldn't resolve: "+designatedUrl);
						}
						
						hypertextPane.setPage(url);
						hypertextFrame = new HypertextFrame(designatedTitle, hypertextPane);
						hypertextFrame.setSize(new Dimension(590, 560));
						hypertextFrame.setVisible(true);
					}
					catch (MalformedURLException exception)
					{
						throw new BugException(exception);
					}
					catch (IOException exception)
					{
						throw new RuntimeException(exception);
					}
				}
			}
		});
	}
}
