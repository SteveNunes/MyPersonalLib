package util;

import java.util.Date;

public class Cronometro {
	
	private Boolean pausado = true;
	private Date dataInicial;
	private Date dataDaPausa;

	public Cronometro() {
		pausado = true;
		dataInicial = new Date();
		dataDaPausa = new Date();
	}
	
	public long getDuracao() {
		long duration = System.currentTimeMillis() - dataInicial.getTime();
		if (pausado)
			duration -= System.currentTimeMillis() - dataDaPausa.getTime();
		return duration;
	}
	
	public Date getDataInicial()
		{ return dataInicial; }
	
	public void setDataInicial(Date data)
		{ dataInicial = data; }

	public Date getDataDePausa()
		{ return dataDaPausa; }
	
	public void setDataDePausa(Date data)
		{ this.dataDaPausa = data; }
	
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
	
	public void reset() {
		dataInicial.setTime(System.currentTimeMillis());
		dataDaPausa.setTime(System.currentTimeMillis());
	}

}
