package util;

import java.text.SimpleDateFormat;
import java.util.Date;

import enums.PeriodToMillis;

public class Cronometro {
	
	private Boolean pausado = true;
	private Date dataInicial;
	private Date dataDaPausa;
	private SimpleDateFormat sdf;

	public Cronometro(String formato, Date dataInicial, Date dataDaPausa, Boolean pausado) {
		sdf = new SimpleDateFormat(formato);
		this.pausado = pausado;
		this.dataInicial = dataInicial == null ? new Date() : new Date(dataInicial.getTime());
		this.dataDaPausa = dataDaPausa == null ? new Date() : new Date(dataDaPausa.getTime());
	}

	public Cronometro(String formato, Date dataInicial)
		{ this(formato, dataInicial, null, null); }
	
	public Cronometro(String formato)
		{ this(formato, null, null, true); }

	public Cronometro()
		{ this("HH:mm:ss.SSS", null, null, true); }
	
	public long getDuracao() {
		long duration = System.currentTimeMillis() - dataInicial.getTime();
		if (pausado)
			duration -= System.currentTimeMillis() - dataDaPausa.getTime();
		return duration;
	}
	
	public String getDuracaoStr()
		{ return sdf.format(new Date(getDuracao() + PeriodToMillis.HOUR.getValue() * 3)); }
	
	public String getDuracaoStr(String simpleDateFormat) {
		sdf.applyPattern(simpleDateFormat);
		return getDuracaoStr();
	}
	
	public Date getDataInicial()
		{ return dataInicial; }
	
	public void setDataInicial(Date data)
		{ dataInicial.setTime(data.getTime()); }

	public void setDataInicial(long millis)
		{ setDataInicial(new Date(millis)); }

	public Date getDataDePausa()
		{ return dataDaPausa; }
	
	public void setDataDePausa(Date data)
		{ dataDaPausa.setTime(data.getTime()); }
	
	public void setDataDePausa(long millis)
		{ setDataDePausa(new Date(millis)); }

	public Boolean isResetado()
		{ return pausado && dataDaPausa.getTime() == dataInicial.getTime(); }

	public Boolean isPausado()
		{ return pausado; }

	public void setPausado(Boolean b) {
		if (pausado == b)
			return;
		if (pausado = b)
			dataDaPausa.setTime(System.currentTimeMillis());
		else
			dataInicial.setTime(dataInicial.getTime() + (System.currentTimeMillis() - dataDaPausa.getTime()));
	}
	
	public void setPausado()
		{ setPausado(!pausado); }
	
	public static long getDuracao(String formatoHHMMSS) {
		String[] split = formatoHHMMSS.split(":");
		try {
			long duracao = 0;
			long[] incs = {PeriodToMillis.SECOND.getValue(), PeriodToMillis.MINUTE.getValue(),
										 PeriodToMillis.HOUR.getValue(), PeriodToMillis.DAY.getValue()};
			for (int p = split.length - 1; p >= 0; p--)
				duracao += Long.parseLong(split[p]) * incs[split.length - (p + 1)];
			return duracao;
		}
		catch (Exception e)
			{ throw new RuntimeException(formatoHHMMSS + " -> formato inválido (Deve ser: [DD:][HH:][MM:]SS"); }
	}
	
	public void reset() {
		dataInicial.setTime(System.currentTimeMillis());
		dataDaPausa.setTime(System.currentTimeMillis());
	}

}
