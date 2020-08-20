package com.could.quartz.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QuartzCronDateUtils {
	/***
	 * 日期转换cron表达式时间格式
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatDateByPattern(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String formatTimeStr = null;
		if (date != null) {
			formatTimeStr = sdf.format(date);
		}
		return formatTimeStr;
	}

	/***
	 * convert Date to cron
	 * 
	 * @param date:时间
	 * @return
	 */
	public static String getCron(Date date) {
		String dateFormat = "ss mm HH dd MM ? yyyy";
		return formatDateByPattern(date, dateFormat);
	}
}
