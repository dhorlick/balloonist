/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.gui;

import com.smithandtinkers.geom.ShapeFriend;
import com.smithandtinkers.graphics.PaintStore;
import com.smithandtinkers.layout.AbstractColorful;
import com.smithandtinkers.layout.Colorful;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class ColorPicker extends JComponent // JButton
{
	private transient Stroke thickStroke;
	private transient Dimension previousSize;
	private JWindow colorPopup;
	private ColorChart colorChart;
	private Colorful colorfulModel;
	
	private Portion affected;
	
	private static final int MAX_COLOR_AS_INT = 256*256*256;
	private static Dimension MINIMUM_SIZE = new Dimension(27,20);
	private static Stroke THIN_STROKE = new BasicStroke((float)0.6);
	
	public ColorPicker()
	{
		super();
		initialize();
		colorfulModel = new AbstractColorful();
	}
	
	public ColorPicker(Colorful designatedColorfulModel)
	{
		super();
		setColorfulModel(designatedColorfulModel);
		initialize();
	}
	
	public void initialize()
	{	
		// setUI(new BasicButtonUI());
		
		addMouseListener(new MouseListener()
		{
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				
			}

			public void mouseReleased(MouseEvent e)
			{
				// System.out.println("cp: release. clickcount = " + e.getClickCount());
				if (e.getClickCount()==0)
					updateColorfulModel();
			}

			public void mousePressed(MouseEvent e)
			{
				if (colorPopup==null)
					generateColorPopup();
				
				if (colorPopup.isVisible())
				{
					colorPopup.setVisible(false);
				}
				else
				{
					Point place = getLocationOnScreen();

					maintainPortionTargeting(e);

					adjustForPortion(place);			

					colorPopup.setLocation(place);
					colorPopup.setVisible(true);
					colorPopup.toFront();
					colorPopup.requestFocusInWindow();
				}
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{	
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e)
			{
				if (colorPopup!=null && colorChart!=null && colorPopup.isVisible()) // I have no idea how JComboBox accomplishes this.
				{
					adjustForPortion(e);
					colorChart.dispatchEvent(e);
				}
			}

			public void mouseMoved(MouseEvent e)
			{
				maintainPortionTargeting(e);
			}});
	}
	
	private final int [] X_POINTS = new int[3];
	private final int [] Y_POINTS = new int[3];
	
	protected void paintComponent(Graphics g)
	{
		// super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		final Stroke initialStroke = g2.getStroke();
		Dimension size = getSize();
		// System.out.println("color picker size="+size);
		
		if (previousSize==null || thickStroke==null || !size.equals(previousSize))
		{
			thickStroke = new BasicStroke((float)size.getWidth()/6.0f);
			previousSize = size;
		}
		
		int widthAsInt = getWidth();
		int heightAsInt = getWidth();
		
		g.setColor(Color.white);
		
		final Paint originalPaint = g2.getPaint();
		g2.setPaint(PaintStore.getInstance().getOceanWaveTexture());
		g.fillRect(0,0, widthAsInt, heightAsInt);
		g2.setPaint(originalPaint);
		
		if (getColorfulModel().getFillColor()!=null)
		{
			g.setColor(getColorfulModel().getFillColor());
			g.fillOval(-5*widthAsInt/7, -heightAsInt/10, (int)(widthAsInt*1.5), (int)(widthAsInt*1.5));
		}
		
		if (getColorfulModel().getOutlineColor()!=null)
		{
			g2.setStroke(thickStroke);
			g2.setColor(getColorfulModel().getOutlineColor());
			g.drawOval(-5*widthAsInt/7, -heightAsInt/10,(int)(widthAsInt*1.5), (int)(widthAsInt*1.5));
			g2.setStroke(initialStroke);
		}
		
		g2.setColor(Color.black);
		buildDropdownTriangle(0.13, 0.87, size, X_POINTS, Y_POINTS);
		g2.fillPolygon(X_POINTS, Y_POINTS, 3);
		
		if (colorfulModel.getFillColor()==Color.black && colorfulModel.getOutlineColor()!=Color.black && colorfulModel.getOutlineColor()!=null)
		{
			g2.setColor(colorfulModel.getOutlineColor());
			g2.drawPolygon(X_POINTS, Y_POINTS, 3);
		}
		
		g2.setColor(Color.black);
		buildDropdownTriangle(0.71, 0.87, size, X_POINTS, Y_POINTS);
		g2.fillPolygon(X_POINTS, Y_POINTS, 3);
		
		final Stroke ORIGINAL_STROKE = g2.getStroke();
		if (getSize().getWidth()<=MINIMUM_SIZE.getWidth())
		{
			g2.setStroke(THIN_STROKE);
		}
		g2.setColor(colorfulModel.getFillColor());
		g2.drawPolygon(X_POINTS, Y_POINTS, 3);
		if (getSize().getWidth()<=MINIMUM_SIZE.getWidth())
		{
			g2.setStroke(ORIGINAL_STROKE);
		}
		
		g.setColor(Color.gray);
		g.drawRect(0, 0, widthAsInt-1, getHeight()-1);
	}
	
	public static void buildDropdownTriangle(double leftmostXFraction, double lowerYFraction, Dimension size, final int[] xPoints, final int[] yPoints)
	{
		final double HALF_WIDTH = 0.10;
		final double FULL_HEIGHT = 0.10;
		
		xPoints[0]=(int)(leftmostXFraction*size.getWidth());
		xPoints[1]=(int)((leftmostXFraction+2*HALF_WIDTH)*size.getWidth());
		xPoints[2]=(int)((leftmostXFraction+HALF_WIDTH)*size.getWidth());
		yPoints[0]=(int)(lowerYFraction*size.getHeight()-FULL_HEIGHT*size.getWidth());
		yPoints[1]=yPoints[0];
		yPoints[2]=(int)(lowerYFraction*size.getHeight());
	}
	
	public static Color getIndexedColor(final int index, final int colors)
	{
		double asFraction = (double)index/(double)colors;
		
		int colorAsInt = (int) (MAX_COLOR_AS_INT*asFraction);
		
		System.out.print("colorAsInt="+colorAsInt);
		
		// suppose out range was 1-1000 and our number was 946 and we wanted 3 ranges of 1-10:
		//   1) 946 / 10^2 = 9.46 --> 9
		//   2) 946 / 10 = 94.6 --> 94
		//       94 % 10 = 4
		//   3) 946 % 10 = 6
		
		return asColor(colorAsInt);
	}
	
	public static Color asColor(int colorAsInt)
	{
		int redValue = (colorAsInt / 256 / 256) - 1;
		int blueValue = (colorAsInt / 256) % 256;
		int greenValue = colorAsInt % 256;
		
		System.out.println(" coord: ("+redValue+","+blueValue+","+greenValue+")");
		
		return new Color(redValue, blueValue, greenValue);
	}
	
	/**
	 * @return the parent dialog or frame of this component, or null if none can be found.
	 */
	public Window findParentWindow()
	{
		return findParentWindow(this);
	}
	
	/**
	 * @return the parent dialog or frame of the designated component, or null if none can be found.
	 */
	public static Window findParentWindow(Component component)
	{
		if (component.getParent()==null)
		{
			return null;
		}
		
		if (component.getParent() instanceof Dialog || component.getParent() instanceof Frame)
			return (Window) component.getParent();
		
		return findParentWindow(component.getParent());
	}
	
	public void generateColorPopup()
	{
		final Window parentWindow = findParentWindow();
		colorPopup = new JWindow(parentWindow);
		
		colorChart = new ColorChart();
		colorPopup.getContentPane().add(colorChart);
		colorChart.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				// System.out.println("ColorChart: clicked");
				updateColorfulModel();
			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{	
			}

			public void mouseExited(MouseEvent e)
			{
				// System.out.println("ColorChart: exited");
			}
		});
		colorPopup.pack();
	}
		
	protected void updateColorfulModel()
	{
		colorPopup.setVisible(false);
		
		if (colorfulModel!=null)
		{
			// System.out.print(Integer.toHexString(colorChart.getSelectedColor().getRGB()));
			
			if (getAffected()==Portion.FILL)
				colorfulModel.setFillColor(colorChart.getSelectedColor());
			else if (getAffected()==Portion.OUTLINE)
				colorfulModel.setOutlineColor(colorChart.getSelectedColor());
		}
		
		invalidate();
		repaint();
	}

	public Colorful getColorfulModel()
	{
		return colorfulModel;
	}

	public void setColorfulModel(Colorful designadModel)
	{
		colorfulModel = designadModel;
	}

	public Dimension getMinimumSize()
	{
		return MINIMUM_SIZE;
	}

	public Dimension getPreferredSize()
	{
		return MINIMUM_SIZE;
	}
	
	public Portion getAffected()
	{
		return affected;
	}

	private void setAffected(Portion designatedAffected)
	{
		affected = designatedAffected;
	}
	
	/**
	 * Adjusts the place parameter appropriately given the chosen portion.
	 */
	private void adjustForPortion(Point place)
	{
		place.translate(0, getHeight());
		
		if (getAffected()==Portion.OUTLINE)
			place.translate(getWidth()-colorPopup.getWidth(), 0);
	}
	
	private void adjustForPortion(MouseEvent mouseEvent)
	{
		mouseEvent.translatePoint(0, -1*getHeight());
		
		if (getAffected()==Portion.OUTLINE)
			mouseEvent.translatePoint(-1*(getWidth()-colorPopup.getWidth()), 0);
	}
	
	private void maintainPortionTargeting(MouseEvent e)
	{
		if (e.getPoint().getX()>getWidth()/2)
		{
			setAffected(Portion.OUTLINE);
			
			if (colorfulModel!=null)
				setToolTipText("outline: "+ShapeFriend.describe(colorfulModel.getOutlineColor()));
		}
		else
		{
			setAffected(Portion.FILL);
			
			if (colorfulModel!=null)
				setToolTipText("fill: "+ShapeFriend.describe(colorfulModel.getFillColor()));
		}
	
	}
	
	public void stateChanged()
	{
		invalidate();
		validate();
		repaint();
	}
	
	public void stateChanged(ChangeEvent designatedChangeEvent)
	{
		stateChanged();
	}

	public static class Portion
	{
		public static final Portion FILL = new Portion();
		public static final Portion OUTLINE = new Portion();
		
		private Portion() {}
	}
}
