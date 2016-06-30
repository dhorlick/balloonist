/**
 * Copyleft 2006 by Dave Horlick

 */
 
package com.smithandtinkers.util;

// import org.apache.xpath.objects.XNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ExsltDatetime // extends org.apache.xalan.lib.ExsltDatetime
{
	public static final SimpleDateFormat ISO_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final SimpleDateFormat      ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static String seconds(String designatedDateTime) throws ParseException // used to return XNumber, but Java doesn't include Xalan anymore
	{
		Date date = null;
		
		try
		{
			date = ISO_DATE_TIME_FORMAT.parse(designatedDateTime);
		}
		catch (ParseException exception)
		{
			date = ISO_DATE_FORMAT.parse(designatedDateTime);
		}
		
		return String.valueOf(date.getTime()/1000L);
	}
}