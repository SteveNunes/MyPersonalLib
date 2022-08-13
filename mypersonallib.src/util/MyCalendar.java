package util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import enums.PeriodToMillis;

public class MyCalendar {
	
	/*
	a    AM/PM
	d    Dia do mês
	D    Dia do ano
	E    Dia da semana (Extenso abreviado)
	EEEE Dia da semana (Extenso completo)
	F    Dia da semana no m�s (ex: 1 Terça de Dezembro)
	G    d.C./a.C.
	h    Hora (formato 12 horas)
	H    Hora (formato 24 horas)
	k    Hora (1-24 ao invés de 0-23)
	K    Hora (0-11 AM/PM)
	L    ???
	LLL  fev. (???)
	LLLL fevereiro (???)
	m    Minutos
	M    Mês (Num�rico)
	MMM  Mês (Extenso abreviado)
	MMMM Mês (Extenso completo)
	s    Segundos
	SSS  Milisegundos do segundo atual
	u    Dia da semana numérico (1=Segunda, 7=Domingo)
	w    Semana do ano
	W    Semana do mês
	X    Time zone	ISO 8601 time zone	-08; -0800; -08:00
	y    Ano
	Y    Ano
	z    TimeZone no formato "GMT"
	zzzz TimeZone no formato "Horário do Meridiano de Greenwich"
	Z    TimeZone no formato "+0000"
	 */
	
	private static Date lastDay = getDateAtMidnight();
	
	public static Date stringToDate(String literalDate, String simpleDateFormat) throws Exception
		{ return new SimpleDateFormat(simpleDateFormat).parse(literalDate); }
	
	public static String dateToString(Date date, String simpleDateFormat)
		{ return new SimpleDateFormat(simpleDateFormat).format(date); }

	public static Boolean dayWasChanged()
		{ return lastDay.getTime() != getDateAtMidnight().getTime(); }

	public static void setLastDayToCurrentDay()
		{ lastDay = getDateAtMidnight(); }

	public static long getOnlyHourTimeFromDate(Date date) {
		String str = new SimpleDateFormat("HH:mm:ss:SSS").format(date);
		try 
			{ date = new SimpleDateFormat("HH:mm:ss:SSS").parse(str); }
		catch (Exception e)
			{ throw new RuntimeException(e.getMessage()); }
		return date.getTime();
	}

	public static long getOnlyHourTimeFromDate()
		{ return getOnlyHourTimeFromDate(new Date()); }
	
	public static Date getDateAtTime(Date date, String time) {
		String str = new SimpleDateFormat("dd/MM/yyyy").format(date);
		try
			{ date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(str + " " + time); }
		catch (Exception e)
			{ throw new RuntimeException(e.getMessage()); }
		return date;
	}
	
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
	
	public static Boolean isSameDay(Date dt1, Date dt2)
		{ return getDay(dt1) == getDay(dt2); }
	
	public static Boolean isSameDate(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try { 
			date1 = sdf.parse(sdf.format(date1));
			date2 = sdf.parse(sdf.format(date2));
		}
		catch (Exception e)
			{ e.printStackTrace(); }
		return date1.equals(date2);
	}
	
	public static Boolean isSameDate(Date date)
		{ return isSameDate(date, new Date()); }

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
	
	public static String getIntervaloEmDiasEntreDatasStr(Date date, Date date2, Boolean shortName) {
	  StringBuilder result = new StringBuilder();
		int gap[] = new int[4];
		String s[][][] = {
				{{"ano", "mes", "sem.", "dia"}, {"anos", "mes.", "sem.", "dias"}},
				{{"ano", "mes", "semana", "dia"}, {"anos", "meses", "semanas", "dias"}}
		};
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
				result.append(s[shortName ? 0 : 1][gap[n] > 1 ? 1 : 0][n]);
				if (n2++ > 0)
					break;
			}
		}
		return result.isEmpty() ? "Menos de 1 dia" : result.toString();
	}

	public static String getIntervaloEmDiasEntreDatasStr(Date date, Date date2)
		{ return getIntervaloEmDiasEntreDatasStr(date, date2, false); }
	
	public static void setDayTo(Date date, int day) {
		try {
			String dateStr = day + new SimpleDateFormat("/MM/yyyy HH:mm:ss").format(date);
			date.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateStr).getTime());
		}
		catch (Exception e) { }
	}
	
	public static void setMonthTo(Date date, int month) {
		try {
			String dateStr = MyCalendar.getDay(date) + "/" + month + new SimpleDateFormat("/yyyy HH:mm:ss").format(date);
			date.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateStr).getTime());
		}
		catch (Exception e) { }
	}
	
	public static void setYearTo(Date date, int year) {
		try {
			String dateStr = MyCalendar.getDay(date) + "/" + MyCalendar.getMonth(date) + "/" + year + " " + new SimpleDateFormat("HH:mm:ss").format(date);
			date.setTime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateStr).getTime());
		}
		catch (Exception e) { }
	}

	public static String getIntervaloEmDiasEntreDatasStr(Date date)
		{ return getIntervaloEmDiasEntreDatasStr(date, new Date()); }
	
	/**
	 * Informando um valor em milisegundos, retorna uma array de int onde cada casa contém a duração em [DI][HO][MIN][SEG]
	 * @param cTimeDuration		Duração em milisegundos á converter em DD:HH:MM:SS
	 * @param show		0/1 especificando as casas que serão exibidas<br>
	 * <b>Ex:</b><br>
	 * "1111" - DD:HH:MM:SS<br>
	 * "0111" - HH:MM:SS<br>
	 * "0110" - HH:MM<br><br>
	 * Se for especificado 'x' no lugar de '1/0', significa que aquele campo não deve contabilizar no calculo.<br>
	 * Ex:<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "1111" retornará [1][24][0][0]<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "x111" retornará [0][36][0][0]<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "xx11" retornará [0][0][2160][0]<br>
	 * @return		Array de int contendo a duração em [DI][HO][MIN][SEG]
	 */
	public static int[] duration(long cTimeDuration, String show) {
		int[] dur = new int[4];
		long[] millis = {PeriodToMillis.DAY.getValue(), PeriodToMillis.HOUR.getValue(), PeriodToMillis.MINUTE.getValue(), PeriodToMillis.SECOND.getValue()};
		for (int n = 0; n < 4; n++) {
			if (show.charAt(n) != 'x') {
				dur[n] = (int)(cTimeDuration / millis[n]);
				cTimeDuration -= millis[n] * dur[n];
			}
		}
		return dur;
	}
	
	public static int[] duration(long cTimeDuration)
		{ return duration(cTimeDuration, "1111"); }

	/**
	 * Informando um valor em milisegundos, retorna a duração no formato DD:HH:MM:SS
	 * @param cTimeDuration		Duração em milisegundos á converter em DD:HH:MM:SS
	 * @param show		0/1 especificando as casas que serão exibidas<br>
	 * <b>Ex:</b><br>
	 * "1111" - DD:HH:MM:SS<br>
	 * "0111" - HH:MM:SS<br>
	 * "0110" - HH:MM<br>
	 * Se for especificado 'x' no lugar de '1/0', significa que aquele campo não deve contabilizar no calculo.<br>
	 * Ex:<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "1111" retornará 01:24:00:00<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "x111" retornará 36:00:00<br>
	 * Se especificar duração suficiente para 36 horas, e informar em <b>show</b> "xx11" retornará 2160:00<br>
	 * @return		Duração no formato DD:HH:MM:SS
	 */
	public static String durationStr(long cTimeDuration, String show) {
		int[] dur = duration(cTimeDuration, show);
		StringBuilder result = new StringBuilder();
		for (int n = 0; n < 4; n++)
			if (show.charAt(n) == '1' && (n > 0 || dur[0] > 0)) {
				if (!result.isEmpty())
					result.append(":");
				result.append(Misc.fillWithZerosAtLeft("" + dur[n], 2));
			}
		return result.toString();
	}
	
	public static String durationStr(long cTimeDuration)
		{ return durationStr(cTimeDuration, "1111"); }

}
