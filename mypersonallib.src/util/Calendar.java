package util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {
	
	public static Date stringToDate(String literalDate, String simpleDateFormat) throws Exception
		{ return new SimpleDateFormat(simpleDateFormat).parse(literalDate); }

	public static String dateToString(Date date, String simpleDateFormat)
		{ return new SimpleDateFormat(simpleDateFormat).format(date); }

	public final static long YEAR = 31536000000L;
	public final static long YEAR_BINARY = 31622400000L;
	public final static long MONTH_JAN = 2678400000L;
	public final static long MONTH_FEB = 2419200000L;
	public final static long MONTH_FEB29 = 2505600000L;
	public final static long MONTH_MAR = 2678400000L;
	public final static long MONTH_APR = 2592000000L;
	public final static long MONTH_MAY = 2678400000L;
	public final static long MONTH_JUN = 2592000000L;
	public final static long MONTH_JUL = 2678400000L;
	public final static long MONTH_AUG = 2678400000L;
	public final static long MONTH_SEP = 2592000000L;
	public final static long MONTH_OCT = 2678400000L;
	public final static long MONTH_NOV = 2592000000L;
	public final static long MONTH_DEC = 2678400000L;
	public final static long WEEK = 604800000L;
	public final static long DAY = 86400000L;
	public final static long HOUR = 3600000L;
	public final static long MINUTE = 60000L;
	public final static long SECOND = 1000L;
	
	private static long[] dateDistance = new long[7];
	
	private static Date lastDay = getDateAtMidnight();
	
	public static Boolean dayWasChanged()
		{ return lastDay.getTime() != getDateAtMidnight().getTime(); }

	public static void setLastDayToCurrentDay()
		{ lastDay = getDateAtMidnight(); }

	public static long getOnlyHourTimeFromDate(Date date) {
		String str = new SimpleDateFormat("HH:mm:ss:SSS").format(date);
		try { date = new SimpleDateFormat("HH:mm:ss:SSS").parse(str); }
		catch (Exception e) { throw new RuntimeException(e.getMessage()); }
		return date.getTime();
	}

	public static long getOnlyHourTimeFromDate()
		{ return getOnlyHourTimeFromDate(new Date()); }
	
	public static Date getDateAtTime(Date date, String time) {
		String str = new SimpleDateFormat("dd/MM/yyyy").format(date);
		try { date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str + " " + time); }
		catch (Exception e) { throw new RuntimeException(e.getMessage()); }
		return date;
	}
	
	public static Date getDateAtTime()
		{ return getDateAtTime(new Date(), "23:59:59"); }
	
	public static Date getDateAtMidnight(Date date)
		{ return getDateAtTime(date, "23:59:59"); }
	
	public static Date getDateAtMidnight()
		{ return getDateAtMidnight(new Date()); }

	private static int intFromSDF(Date dt, String format)
		{ return Integer.parseInt(new SimpleDateFormat(format).format(dt)); }
	
	public static Boolean isDawn(Date dt)
		{ return getHour(dt) < 6; }
	
	public static Boolean isDawn()
		{ return isDawn(new Date()); }

	public static Boolean isMorning(Date dt)
		{ return getHour(dt) >= 6 && getHour(dt) < 12; }
	
	public static Boolean isMorning()
		{ return isMorning(new Date()); }

	public static Boolean isAfternoon(Date dt)
		{ return getHour(dt) >= 12 && getHour(dt) < 18; }

	public static Boolean isAfternoon()
		{ return isAfternoon(new Date()); }

	public static Boolean isNight(Date dt)
		{ return getHour(dt) >= 18; }

	public static Boolean isNight()
		{ return isNight(new Date()); }
	
	public static Boolean isWeekDay(Date dt)
		{ return getWeekDay(dt) < 6; }
	
	public static Boolean isWeekDay()
		{ return isWeekDay(new Date()); }
	
	public static Boolean isWeekend(Date dt)
		{ return getWeekDay(dt) >= 6; }

	public static Boolean isWeekend()
		{ return isWeekend(new Date()); }

	public static int getWeekDay(Date dt)
		{ return intFromSDF(dt, "u"); }

	public static int getWeekDay()
		{ return getWeekDay(new Date()); }

	public static int getYear(Date dt)
		{ return intFromSDF(dt, "yyyy"); }

	public static int getYear()
		{ return getYear(new Date()); }

	public static int getMonth(Date dt)
		{ return intFromSDF(dt, "MM"); }

	public static int getMonth()
		{ return getMonth(new Date()); }

	public static int getDay(Date dt)
		{ return intFromSDF(dt, "dd"); }
	
	public static int getDay()
		{ return getDay(new Date()); }
	
	public static int getHour(Date dt)
		{ return intFromSDF(dt, "HH"); }
	
	public static int getHour()
		{ return getHour(new Date()); }

	public static int getMinute(Date dt)
		{ return intFromSDF(dt, "mm"); }

	public static int getMinute()
		{ return getMinute(new Date()); }

	public static int getSeconds(Date dt)
		{ return intFromSDF(dt, "ss"); }

	public static int getSeconds()
		{ return getSeconds(new Date()); }
	
	public static void setDateDistance(Date dt1, Date dt2) {
		long val = Math.abs(dt1.getTime() - dt2.getTime()), v;
		dateDistance[6] = v = val / 1000 / 60 / 60 / 24 / 365; // Years
		val -= v * 365 * 24 * 60 * 60 * 1000;
		dateDistance[5] = v = val / 1000 / 60 / 60 / 24 / 7; // Weeks
		val -= v * 7 * 24 * 60 * 60 * 1000;
		dateDistance[4] = v = val / 1000 / 60 / 60 / 24; // Days
		val -= v * 24 * 60 * 60 * 1000;
		dateDistance[3] = v = val / 1000 / 60 / 60; // Hours
		val -= v * 60 * 60 * 1000;
		dateDistance[2] = v = val / 1000 / 60; // Minutes
		val -= v * 60 * 1000;
		dateDistance[1] = v = val / 1000; // Seconds
		val -= v * 1000;
		dateDistance[0] = val; // Milliseconds
		
	}
	
	public static void setDateDistance(Date dt)
		{ setDateDistance(new Date(System.currentTimeMillis()), dt); }
	
	public static void setDateDistance(long millis)
		{ setDateDistance(new Date(0), new Date(millis)); }
	
	public static long getDateDistanceInMillis()
		{ return dateDistance[0]; }
	
	public static long getDateDistanceInSeconds()
		{ return dateDistance[1]; }
	
	public static long getDateDistanceInMinutes()
		{ return dateDistance[2]; }

	public static long getDateDistanceInHours()
		{ return dateDistance[3]; }
	
	public static long getDateDistanceInDays()
		{ return dateDistance[4]; }

	public static long getDateDistanceInWeeks()
		{ return dateDistance[5]; }

	public static long getDateDistanceInYears()
		{ return dateDistance[6]; }
	
	public static Boolean isSameDay(Date dt1, Date dt2)
		{ return getDay(dt1) == getDay(dt2); }
	
	public static Boolean isSameDate(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try { 
			date1 = sdf.parse(sdf.format(date1));
			date2 = sdf.parse(sdf.format(date2));
		}
		catch (Exception e) { e.printStackTrace(); }
		return date1.equals(date2);
	}
	
	public static Boolean isSameDate(Date date)
		{ return isSameDate(date, new Date()); }

	public static String getDateDistanceFromDateFormat(String dateFormat) {
		dateFormat = dateFormat.toLowerCase();
		if (dateFormat.contains("yyyy"))
			dateFormat = dateFormat.replace("yyyy", "" + getDateDistanceInYears());
		if (dateFormat.contains("ww"))
			dateFormat = dateFormat.replace("ww", "" + getDateDistanceInWeeks());
		if (dateFormat.contains("dd"))
			dateFormat = dateFormat.replace("dd", "" + getDateDistanceInDays());
		if (dateFormat.contains("hh"))
			dateFormat = dateFormat.replace("hh", "" + getDateDistanceInHours());
		if (dateFormat.contains("mm"))
			dateFormat = dateFormat.replace("mm", "" + getDateDistanceInMinutes());
		if (dateFormat.contains("ss"))
			dateFormat = dateFormat.replace("ss", "" + getDateDistanceInSeconds());
		if (dateFormat.contains("ee"))
			dateFormat = dateFormat.replace("ee", "" + getDateDistanceInMillis());
		return dateFormat;
	}
	
	/**
	 * Compara o dia da data informada por parâmetro com o dia da data atual.
	 * @param date		Data á ser comparada com a data atual
	 * @return {@code 0} se o dia data informada por parâmetro bater com o dia da data atual, independendo da hora 
	 * , {@code -1} se o dia data informada por parâmetro for inferior ao dia da data atual
	 * ou {@code 1} se o dia data informada por parâmetro for superior ao dia da data atual.
	 */
	public static int dateIsToday(Date date) {
		return getDateAtMidnight(date) == getDateAtMidnight() ? 0 :
			System.currentTimeMillis() > date.getTime() ? -1 : 1;
	}
	
	public static Date mixDateAndHour(Date dateToGetDate, Date dateToGetHour) {
		Date date = new Date(dateToGetDate.getTime() - getOnlyHourTimeFromDate(dateToGetDate));
		date.setTime(date.getTime() + getOnlyHourTimeFromDate(dateToGetHour));
		return date;
	}
	
	public static Date convertLocalDateToDate(LocalDate date)
		{ return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()); }

	public static LocalDate convertDateToLocalDate(Date date)
		{ return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); }
	
	public static int getIntervaloEmDiasEntreDatas(Date date, Date date2) {
		long n1 = date.getTime();
		long n2 = date2.getTime();
		return (int)TimeUnit.MILLISECONDS.toDays(n1 > n2 ? n1 - n2 : n2 - n1);
	}

	public static int getIntervaloEmDiasEntreDatas(Date date)
		{ return getIntervaloEmDiasEntreDatas(date, new Date()); }
	
	public static String getIntervaloEmDiasEntreDatasStr(Date date, Date date2) {
	  StringBuilder result = new StringBuilder();
		int gap[] = new int[4];
		String s[][] = {{"ano", "mes", "semana", "dia"}, {"anos", "meses", "semanas", "dias"}};
		gap[3] = getIntervaloEmDiasEntreDatas(date, date2);
		gap[0] = (gap[3] / 365);
		gap[3] -= gap[0] * 365;
		gap[1] = (gap[3] / 30);
		gap[3] -= gap[1] * 30;
		gap[2] = (gap[3] / 7);
		gap[3] -= gap[2] * 7;
		for (int n = 0, n2 = 0; n < gap.length; n++) {
			if (gap[n] > 0) {
				if (result.length() > 1)
					result.append(" e ");
				result.append(gap[n]);
				result.append(" ");
				result.append(s[gap[n] > 1 ? 1 : 0][n]);
				if (n2++ > 0)
					break;
			}
		}
		return result.isEmpty() ? "Menos de 1 dia" : result.toString();
	}
	
	public static String getIntervaloEmDiasEntreDatasStr(Date date)
		{ return getIntervaloEmDiasEntreDatasStr(date, new Date()); }

}
