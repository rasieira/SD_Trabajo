package Respositorios;
import java.io.Serializable;
import java.util.List;

public class Cuenta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Repositorio> repositorios;
	private Autor autor;
	public List<Repositorio> getRepositorios() {
		return repositorios;
	}
	public void setRepositorios(List<Repositorio> repositorios) {
		this.repositorios = repositorios;
	}
	public Autor getAutor() {
		return autor;
	}
	public void setAutor(Autor autor) {
		this.autor = autor;
	}
	
}
