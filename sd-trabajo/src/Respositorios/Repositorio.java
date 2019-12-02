package Respositorios;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Repositorio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private List<Archivo> archivos;
	public Repositorio(String nombre) {
		this.setNombre(nombre);
		this.archivos = null;
	}
	public List<Archivo> getArchivos() {
		return archivos;
	}
	public void setArchivos(List<Archivo> archivos) {
		this.archivos = archivos;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
