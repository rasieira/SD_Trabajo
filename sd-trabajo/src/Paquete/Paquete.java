package Paquete;

import java.io.Serializable;

import Respositorios.Repositorio;

public class Paquete implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String comando;
	private Repositorio repositorio;

	public Paquete(String comando, Repositorio repositorio) {
		this.setComando(comando);
		this.repositorio = repositorio;
	}

	public Paquete(String comando) {
		this.comando = comando;
		this.repositorio = null;
	}

	public Repositorio getRepositorio() {
		return repositorio;
	}

	public void setRepositorio(Repositorio repositorio) {
		this.repositorio = repositorio;
	}

	public String getComando() {
		return comando;
	}

	public void setComando(String comando) {
		this.comando = comando;
	}

}
