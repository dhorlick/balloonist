package com.smithandtinkers.svg;

import java.io.File;
import javax.imageio.ImageIO;
import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class GraphicalContentTest extends TestCase
{
	public void test()
	{
		String filename = "penguin.png";
		String supposedExtension = GraphicalContent.determineFileExtension(filename);

		if (!supposedExtension.equals("png"))
		{
			throw new IllegalStateException("Extension for filename \"" + filename
					+ "\" should report as \"png\" but instead reported as "
					+ supposedExtension + ".");
		}

		if (!GraphicalContent.hasFileExtension(new File(filename), "PNG"))
		{
			throw new IllegalArgumentException("GraphicalContent.hasFileExtension(filename, \"PNG\") eval'd false");
		}

		String [] formats = ImageIO.getReaderFormatNames();

		for (int loop=0; loop<=formats.length-1; loop++)
			System.out.println(formats[loop]);

		String fileBase = GraphicalContent.determineFileBase(filename);
		if (!"penguin".equals(fileBase))
			throw new IllegalStateException(fileBase);
	}
}
