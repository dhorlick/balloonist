/**
 Copyleft 2005 by Dave Horlick
 */

package com.smithandtinkers.geom;

import java.awt.*;

import javax.swing.JFrame;

import com.smithandtinkers.layout.Interactive;
import junit.framework.TestCase;


public class InteractivePerchTest extends TestCase
{
	public void test()
	{
		JFrame frame = new JFrame("bob");
		frame.setSize(310,190);
		Component component = null;
		
		component = buildComponent(new Parallelogram());
		// component = buildComponent(new PunctuatedSuperEllipse());
		
		frame.getContentPane().add(component);
		
		frame.setVisible(true);
	}

	private static Component buildComponent(final Interactive interactive)
	{
		InteractivePerch sperch = InteractivePerch.perch(interactive, new AbstractMarginal());
		
		ShapingContext shapingContext = new ConcreteShapingContext();
		
		final Shape shape = sperch.toShape(shapingContext, null);
		final Shape inner = sperch.getInner().toShape(shapingContext, null);
		final Shape outer = sperch.getOuter().toShape(shapingContext, null);
		
		final double pgramOriginX = interactive.getLocation().getX();
		final double pgramOriginY = interactive.getLocation().getY();
		
		System.out.println("pgram origin: ("+pgramOriginX +","+pgramOriginY+")");
		
		final double originX = shape.getBounds().getX();
		final double originY = shape.getBounds().getY();
		
		System.out.println("shape origin: ("+originX+","+originY+")");
		
		Component component = new Component()
		{
			public void paint(Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g;
				
				g2.setColor(Color.black);
				g2.draw(shape);
				g2.setColor(Color.red);
				g2.draw(inner);
				g2.setColor(Color.blue);
				g2.draw(outer);
				ShapeFriend.plotDot(originX, originY, Color.green, 3, g2, true);
				ShapeFriend.plotDot(pgramOriginX, pgramOriginY, Color.orange, 3, g2, true);
			}
		};
		return component;
	}
}
