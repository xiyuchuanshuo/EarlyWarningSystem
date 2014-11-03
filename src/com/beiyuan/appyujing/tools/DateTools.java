/**
 * 
 */
package com.beiyuan.appyujing.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kuoyu
 *
 */
public class DateTools {

	public static String dataFormat(String date){
		String reTime = null ;
		  SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
		  Date dDate;
		try {
			dDate = format.parse("200911120000");
			 SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			reTime = format2.format(dDate);
			  System.out.println(reTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			 return reTime;
		}
		
		 
		 
	}
}
