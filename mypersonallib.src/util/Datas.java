package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

public class Datas {
	
	private static String gmt = "GMT";
	private static long cTime;
	
	/**
	 * Define a Time Zone para as pr�ximas chamadas dos m�todos dessa classe.
	 * @param timeZone		- GMT, GMT+1, GMT-1, etc...
	 */
	public static void setTimeZone(String timeZone) {
		gmt = timeZone;
		setCTime();
	}
	
	/**
	 * @return		o valor definido por setCTime()
	 */
	public static long getCTime() { return cTime; }
	
	/**
	 * Define o CTime para as pr�ximas chamadas dos m�todos dessa classe.
	 * @param ctime		- CTime em milisegundos.
	 */
	public static void setCTime(long ctime) { cTime = ctime; }
	
	/**
	 * Sobrecarga do m�todo 'setCTime(long ctime)'
	 * onde n�o � preciso passar o 'ctime' (� passado o valor de 'System.currentTimeMillis()')
	 */
	public static void setCTime()
		{ setCTime(System.currentTimeMillis()); }

	/**
	 * Decrementa o valor do CTime definido com setCTime()
	 * @param incVal		valor a ser decrementado
	 */
	public static void decCTime(long incVal) { cTime -= incVal; }
	
	/**
	 * Incrementa o valor do CTime definido com setCTime()
	 * @param incVal		valor a ser incrementado
	 */
	public static void incCTime(long incVal) { cTime += incVal; }
	
	/**
	 * Replica da identifier $fulldate do mIRC Scripting (mSL)
	 * @param ctime				   CTIME em milisegundos
	 * @param dateFormat	   Formatador da data (Ex: dd/MM/yyyy)
	 * @return		Uma string com a data formatada.
	 */
	public static String formatarData(long ctime, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat); 
		sdf.setTimeZone(TimeZone.getTimeZone(gmt));
		return sdf.format(new Date(ctime)).toString();
	}

	/**
	 * Sobrecarga do m�todo 'formatarData(long ctime, String dateFormat)'
	 * onde n�o � preciso passar a String formatadora.
	 */
	public static String formatarData(long ctime) 
		{ return formatarData(ctime, "EEEE MMMM dd HH:mm:ss yyyy"); }

	/**
	 * Sobrecarga do m�todo 'formatarData(long ctime, String dateFormat)'
	 * onde n�o � preciso passar o CTIME
	 */
	public static String formatarData(String dateFormat) 
		{ return formatarData(cTime, dateFormat); }
	
	/**
	 * Sobrecarga do m�todo 'formatarData(long ctime, String dateFormat)'
	 * onde n�o � preciso passar a String formatadora nem o CTIME
	 */
	public static String formatarData() 
		{	return formatarData(cTime, "EEEE MMMM dd HH:mm:ss yyyy");	}

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com a data formatada.
	 */
	public static String getData(long ctime)
		{	return formatarData(ctime, "dd/MM/yyyy"); }
	
	/**
	 * Sobrecarga do m�todo 'getData(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getData()
		{	return getData(cTime); }
	
	/**
	 * @param ctime			CTIME em milisegundos
	 * @param f					Se a hora vai ser retornada no formato 24 horas ou 12 horas
	 * @return					Uma string com a hora formatada correspondente ao CTIME
	 */
	public static String getHorario(long ctime, FormatoHora f)
		{ return formatarData(ctime, f == FormatoHora._24Horas ? "HH:MM:ss" : "hh:MM:ss"); }
	
	/**
	 * Sobrecarga do m�todo 'getHorario(long ctime, FormatoHora f)'
	 * onde n�o � preciso passar o 'f'
	 */
	public static String getHorario(long ctime)
		{	return getHorario(ctime); }
	
	/**
	 * Sobrecarga do m�todo 'getHorario(long ctime, FormatoHora f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getHorario(FormatoHora f)
		{	return getHorario(cTime, FormatoHora._24Horas); }
	
	/**
	 * Sobrecarga do m�todo 'getHorario(long ctime, FormatoHora f)'
	 * onde n�o � preciso passar o 'f' nem o 'ctime'
	 */
	public static String getHorario()
		{	return getHorario(cTime, FormatoHora._24Horas); }
	
	/**
	 * @param ctime		CTIME em milisegundos
	 * @param f				Se a hora vai ser retornada no formato 24 horas ou 12 horas
	 * @return				Uma string com as horas correspondente ao CTIME
	 */
	public static String getHoras(long ctime, FormatoHora f)
		{ return formatarData(ctime, f == FormatoHora._24Horas ? "HH" : "hh"); }
	
	/**
	 * Sobrecarga do m�todo 'getHoras(long ctime, FormatoHora f)'
	 * onde n�o � preciso passar o 'f'
	 */
	public static String getHoras(long ctime)
		{	return getHoras(ctime); }
	
	/**
	 * Sobrecarga do m�todo 'getHoras(long ctime, FormatoHora f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getHoras(FormatoHora f)
		{	return getHoras(cTime, f); }
	
	/**
	 * Sobrecarga do m�todo 'getHoras(long ctime, Boolean _24HoursFormat)'
	 * onde n�o � preciso passar o '_24HoursFormat' nem o 'ctime'
	 */
	public static String getHoras()
		{	return getHoras(cTime, FormatoHora._24Horas); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com os minutos correspondente ao CTIME
	 */
	public static String getMinutos(long ctime)
		{	return formatarData(ctime, "MM"); }

	/**
	 * Sobrecarga do m�todo 'getMinutos(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getMinutos()
		{	return getMinutos(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com os segundos correspondente ao CTIME
	 */
	public static String getSegundos(long ctime)
		{	return formatarData(ctime, "ss"); }

	/**
	 * Sobrecarga do m�todo 'getSegundos(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getSegundos()
		{	return getSegundos(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com os milisegundos correspondente ao CTIME
	 */
	public static String getMilliseg(long ctime)
		{	return formatarData(ctime, "SSS"); }

	/**
	 * Sobrecarga do m�todo 'getMilliseg(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getMilliseg()
		{	return getSegundos(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @param format	O formato que o dia da semana ser� retornado 
	 * @return				Uma string com o dia da semana correspondente ao CTIME
	 */
	public static String getDiaDaSemana(long ctime, FormatoNome f) {
		if (f == FormatoNome.NOME_CURTO) return formatarData(ctime, "E");
		else if (f == FormatoNome.NOME_LONGO) return formatarData(ctime, "EEEE");
		return formatarData(ctime, "u");
	}

	/**
	 * Sobrecarga do m�todo 'getDiaDaSemana(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getDiaDaSemana(FormatoNome f)
		{	return getDiaDaSemana(cTime, f); }

	/**
	 * Sobrecarga do m�todo 'getDiaDaSemana(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'FormatoNome f'
	 */
	public static String getDiaDaSemana(long ctime)
		{	return getDiaDaSemana(ctime, FormatoNome.NUMERICO); }

	/**
	 * Sobrecarga do m�todo 'getDiaDaSemana(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime' nem o 'FormatoNome f'
	 */
	public static String getDiaDaSemana()
		{	return getDiaDaSemana(cTime, FormatoNome.NUMERICO); }
	
	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com o dia do m�s correspondente ao CTIME
	 */
	public static String getDiaDoMes(long ctime) {
		return formatarData(ctime, "dd");
	}

	/**
	 * Sobrecarga do m�todo 'getDiaDaSemana(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getDiaDoMes()
		{	return getDiaDoMes(cTime); }
	
	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com o dia do ano correspondente ao CTIME
	 */
	public static String getDiaDoAno(long ctime) {
		return formatarData(ctime, "dd");
	}

	/**
	 * Sobrecarga do m�todo 'getDiaDoAno(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getDiaDoAno()
		{	return getDiaDoAno(cTime); }
	
	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com a semana do m�s correspondente ao CTIME
	 */
	public static String getSemanaDoMes(long ctime) {
		return formatarData(ctime, "W");
	}

	/**
	 * Sobrecarga do m�todo 'getSemanaDoMes(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getSemanaDoMes()
		{	return getSemanaDoMes(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com a semana do ano correspondente ao CTIME
	 */
	public static String getSemanaDoAno(long ctime) {
		return formatarData(ctime, "w");
	}

	/**
	 * Sobrecarga do m�todo 'getSemanaDoAno(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getSemanaDoAno()
		{	return getSemanaDoAno(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @param format	O formato que o m�s ser� retornado 
	 * @return				Uma string com m�s correspondente ao CTIME
	 */
	public static String getMes(long ctime, FormatoNome f) {
		if (f == FormatoNome.NOME_CURTO) return formatarData(ctime, "MMM"); 
		else if (f == FormatoNome.NOME_LONGO) return formatarData(ctime, "MMMM"); 
		return formatarData(ctime, "MM");
	}

	/**
	 * Sobrecarga do m�todo 'getMes(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getMes(FormatoNome f)
		{	return getMes(cTime, f); }

	/**
	 * Sobrecarga do m�todo 'getMes(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'FormatoNome f'
	 */
	public static String getMes(long ctime)
		{	return getMes(ctime, FormatoNome.NUMERICO); }

	/**
	 * Sobrecarga do m�todo 'getMes(long ctime, FormatoNome f)'
	 * onde n�o � preciso passar o 'ctime' nem o 'FormatoNome f'
	 */
	public static String getMes()
		{	return getMes(cTime, FormatoNome.NUMERICO); }
	
	/**
	 * @param f				O formato que a Time Zone ser� retornada 
	 * @return				Uma string com a Time Zone definida atualmente
	 */
	public static String getTimeZone(FormatoNome f) {
		if (f == FormatoNome.NOME_CURTO) return formatarData(cTime, "z"); 
		else if (f == FormatoNome.NOME_LONGO) return formatarData(cTime, "zzzz"); 
		return formatarData(cTime, "Z");
	}

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com o per�odo (a.C. / d.C.) correspondente a data do CTIME
	 */
	public static String getPeriodo(long ctime) {
		return formatarData(ctime, "G");
	}

	/**
	 * Sobrecarga do m�todo 'getPeriodo(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getPeriodo()
		{	return getPeriodo(cTime); }

	/**
	 * @param ctime		CTIME em milisegundos
	 * @return				Uma string com o per�odo do dia (AM / PM) correspondente a data do CTIME
	 */
	public static String getPeriodoDia(long ctime) {
		return formatarData(ctime, "a");
	}

	/**
	 * Sobrecarga do m�todo 'getPeriodoDia(long ctime)'
	 * onde n�o � preciso passar o 'ctime'
	 */
	public static String getPeriodoDia()
		{	return getPeriodoDia(cTime); }
	
	/**
	 * @param segundos		Dura��o em segundos
	 * @return		   A dura��o em [semanas], [dias], [horas], [minutos], [segundos]
	 * 							 de uma dura��o informada em segundos.
	 */
	public static String duracao(long segundos,DuracaoEm d,DuracaoFormato f) {
		long t, days = 0, hours = 0, mins = 0, secs = 0;
		StringBuilder result = new StringBuilder();
		List<String> res = new ArrayList<>();
		if (d == DuracaoEm.COMPLETO || d == DuracaoEm.DIAS) {
			days = t = TimeUnit.DAYS.convert(segundos, TimeUnit.SECONDS);
			segundos -= TimeUnit.SECONDS.convert(t, TimeUnit.DAYS);
			result.append(String.format("%02d:", days));
		}
		if (d == DuracaoEm.COMPLETO || d == DuracaoEm.RELOGIO || d == DuracaoEm.HORAS) {
			hours = t = TimeUnit.HOURS.convert(segundos, TimeUnit.SECONDS);
			segundos -= TimeUnit.SECONDS.convert(t, TimeUnit.HOURS);
			result.append(String.format("%02d:", hours));
		}
		if (d == DuracaoEm.COMPLETO || d == DuracaoEm.RELOGIO || d == DuracaoEm.MINUTOS) {
			mins = t = TimeUnit.MINUTES.convert(segundos, TimeUnit.SECONDS);
			secs = segundos - TimeUnit.SECONDS.convert(t, TimeUnit.MINUTES);
			result.append(String.format("%02d:", mins));
			if (d == DuracaoEm.COMPLETO || d == DuracaoEm.RELOGIO || d == DuracaoEm.SEGUNDOS)
			  result.append(String.format("%02d", secs));
		}
		if (f == DuracaoFormato.EXTENSO) {
			result = new StringBuilder();
			if (days > 0) res.add(days + (days > 1 ? " dias" : " dia"));
			if (hours > 0) res.add(hours + (hours > 1 ? " horas" : " hora"));
			if (mins > 0) res.add(mins + (mins > 1 ? " minutos" : " minuto"));
			if (secs > 0) res.add(secs + (secs > 1 ? " segundos" : " segundo"));
			for (int n = 0; n < res.size(); n++) {
				result.append(res.get(n));
				if (n + 1 < res.size()) result.append(n + 2 < res.size() ? ", " : " e ");
			}
		}
		return result.toString();
	}

	/**
	 * Sobrecarga do m�todo 'duracao(long segundos,DuracaoEm d,DuracaoFormato f)'
	 * onde n�o � preciso passar o 'DuracaoFormato f'
	 */
	public static String duracao(long segundos,DuracaoEm d)
		{ return duracao(segundos,d,DuracaoFormato.RELOGIO); }

	/**
	 * Sobrecarga do m�todo 'duracao(long segundos,DuracaoEm d,DuracaoFormato f)'
	 * onde n�o � preciso passar o 'DuracaoEm d'
	 */
	public static String duracao(long segundos,DuracaoFormato f)
		{ return duracao(segundos,DuracaoEm.RELOGIO); }

	/**
	 * Sobrecarga do m�todo 'duracao(long segundos,DuracaoEm d,DuracaoFormato f)'
	 * onde n�o � preciso passar o 'DuracaoFormato f' nem o 'DuracaoEm d'
	 */
	public static String duracao(long segundos)
		{ return duracao(segundos,DuracaoEm.RELOGIO,DuracaoFormato.RELOGIO); }
}

enum FormatoHora { _24Horas, _12Horas; }

enum FormatoNome { NUMERICO, NOME_CURTO, NOME_LONGO; }

enum MilisegPara {

	SEGUNDO(1000),
	MINUTO(60000),
	HORA(3600000),
	DIA(86400000),
	SEMANA(604800000);
	
	private final long millis;

	MilisegPara(long millis) { this.millis=millis; }

	public long getValue() { return millis; }

}

enum DuracaoEm { SEGUNDOS, MINUTOS, HORAS, DIAS, SEMANAS, RELOGIO, COMPLETO; }

enum DuracaoFormato { RELOGIO, EXTENSO; }