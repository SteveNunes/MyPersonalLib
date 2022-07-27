package enums;

public enum PeriodToMillis {

	YEAR(31536000000L),
	YEAR_BINARY(31622400000L),
	MONTH_JAN(2678400000L),
	MONTH_FEB(2419200000L),
	MONTH_FEB29(2505600000L),
	MONTH_MAR(2678400000L),
	MONTH_APR(2592000000L),
	MONTH_MAY(2678400000L),
	MONTH_JUN(2592000000L),
	MONTH_JUL(2678400000L),
	MONTH_AUG(2678400000L),
	MONTH_SEP(2592000000L),
	MONTH_OCT(2678400000L),
	MONTH_NOV(2592000000L),
	MONTH_DEC(2678400000L),
	WEEK(604800000L),
	DAY(86400000L),
	HOUR(3600000L),
	MINUTE(60000L),
	SECOND(1000L);

	private final long value;

	PeriodToMillis(long value)
		{ this.value = value; }

	public long getValue()
		{ return value; }
	
}
