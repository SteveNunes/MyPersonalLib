package util;

import java.util.Date;

import enums.PeriodToMillis;

public class DateGap {

	private static long[] dateGap = new long[7];

	public DateGap(Date date1, Date date2) {
		long val = Math.abs(date1.getTime() - date2.getTime()), v;
		dateGap[6] = v = val / PeriodToMillis.YEAR.getValue();
		val -= v * PeriodToMillis.YEAR.getValue();
		dateGap[5] = v = val / PeriodToMillis.WEEK.getValue();
		val -= v * PeriodToMillis.WEEK.getValue();
		dateGap[4] = v = val / PeriodToMillis.DAY.getValue();
		val -= v * PeriodToMillis.DAY.getValue();
		dateGap[3] = v = val / PeriodToMillis.HOUR.getValue();
		val -= v * PeriodToMillis.HOUR.getValue();
		dateGap[2] = v = val / PeriodToMillis.MINUTE.getValue();
		val -= v * PeriodToMillis.MINUTE.getValue();
		dateGap[1] = v = val / PeriodToMillis.SECOND.getValue();
		val -= v * PeriodToMillis.SECOND.getValue();
		dateGap[0] = val;
	}
	
	public DateGap(Date date)
		{ this(new Date(System.currentTimeMillis()), date); }
	
	public DateGap(long millis)
		{ this(new Date(0), new Date(millis)); }
	
	public long gapInMillis()
		{ return dateGap[0]; }
	
	public long gapInSeconds()
		{ return dateGap[1]; }
	
	public long gapInMinutes()
		{ return dateGap[2]; }

	public long gapInHours()
		{ return dateGap[3]; }
	
	public long gapInDays()
		{ return dateGap[4]; }

	public long gapInWeeks()
		{ return dateGap[5]; }

	public long gapInYears()
		{ return dateGap[6]; }
	
	public String getGapFromDateFormat(String dateFormat) {
		dateFormat = dateFormat.toLowerCase();
		if (dateFormat.contains("yyyy"))
			dateFormat = dateFormat.replace("yyyy", "" + gapInYears());
		if (dateFormat.contains("ww"))
			dateFormat = dateFormat.replace("ww", "" + gapInWeeks());
		if (dateFormat.contains("dd"))
			dateFormat = dateFormat.replace("dd", "" + gapInDays());
		if (dateFormat.contains("hh"))
			dateFormat = dateFormat.replace("hh", "" + gapInHours());
		if (dateFormat.contains("mm"))
			dateFormat = dateFormat.replace("mm", "" + gapInMinutes());
		if (dateFormat.contains("ss"))
			dateFormat = dateFormat.replace("ss", "" + gapInSeconds());
		if (dateFormat.contains("ee"))
			dateFormat = dateFormat.replace("ee", "" + gapInMillis());
		return dateFormat;
	}

}
