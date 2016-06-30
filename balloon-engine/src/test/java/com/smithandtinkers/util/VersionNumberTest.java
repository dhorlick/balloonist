package com.smithandtinkers.util;

import junit.framework.TestCase;

/**
 * @author dhorlick
 */
public class VersionNumberTest extends TestCase
{
	private static final String ONE_FOUR = "1.4";
	private static final String ONE_THREE_ONE = "1.3.1";

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void test()
	{
		final VersionNumber version14 = VersionNumber.parse(ONE_FOUR);
		final VersionNumber version131 = VersionNumber.parse(ONE_THREE_ONE);

		if (version14.compareTo(version131)!=Boolean.TRUE)
			throw new IllegalStateException();

		if (version131.compareTo(version14)!=Boolean.FALSE)
			throw new IllegalStateException();

		if (version131.isGreaterThanOrEqualTo(version14))
			throw new IllegalStateException();

		String strung131 = version131.toString();
		if (!"1.3.1".equals(strung131))
			throw new IllegalStateException(strung131);

		VersionNumber version15 = VersionNumber.parse("1.5");
		VersionNumber weird = VersionNumber.parse("1.4.2_05");

		if (version15.compareTo(weird)!=Boolean.TRUE)
			throw new IllegalStateException();

		VersionNumber version1502 = VersionNumber.parse("1.5.0.2");

		if (version1502.compareTo(version15)!=Boolean.TRUE)
			throw new IllegalStateException();

		if (version15.compareTo(version1502)!=Boolean.FALSE)
			throw new IllegalStateException();

		final long numberOfDigits1 = VersionNumber.determineNumberOfDigits(3049);
		if (numberOfDigits1!=4)
			throw new IllegalStateException(String.valueOf(numberOfDigits1));

		final long numberOfDigits2 = VersionNumber.determineNumberOfDigits(-22);
		if (numberOfDigits2!=2)
			throw new IllegalStateException(String.valueOf(numberOfDigits2));

		final long numberOfDigits3 = VersionNumber.determineNumberOfDigits(0);
		if (numberOfDigits3!=1)
			throw new IllegalStateException(String.valueOf(numberOfDigits3));

		final long numberOfDigits4 = VersionNumber.determineNumberOfDigits(7);
		if (numberOfDigits4!=1)
			throw new IllegalStateException(String.valueOf(numberOfDigits4));

		final long numberOfDigits5 = VersionNumber.determineNumberOfDigits(1);
		if (numberOfDigits5!=1)
			throw new IllegalStateException(String.valueOf(numberOfDigits4));

		final double diff1 = version14.subtract(version131);

		if (diff1!=0.09)
			throw new IllegalStateException(String.valueOf(diff1));

		final double diff2 = version15.subtract(weird);

		if (diff2!=0.075)
			throw new IllegalStateException(String.valueOf(diff2));

		final double diff3 = version15.subtract(version131);

		if (diff3!=0.19)
			throw new IllegalStateException(String.valueOf(diff3));

		VersionNumber twoDotOhDotOne = VersionNumber.parse("2.0.1");

		final double diff4 = twoDotOhDotOne.subtract(version131);
		if (diff4 != 0.7)
			throw new IllegalStateException(String.valueOf(diff4));

		final double diff5 = version131.subtract(twoDotOhDotOne);
		if (diff5 != -0.7)
			throw new IllegalStateException(String.valueOf(diff5));

		final VersionNumber oneDotOhDotFour = VersionNumber.parse("1.0.4");
		final VersionNumber oneDotOh = VersionNumber.parse("1.0");

		final double diff6 = oneDotOhDotFour.subtract(oneDotOh);
		if (diff6>0.1)
			throw new IllegalStateException(String.valueOf(diff6));
	}
}
