package Respositorios;
import java.io.File;
import java.util.Date;
import java.io.Serializable;
public class Archivo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private File archivo;
	private Date fecha;
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public File getArchivo() {
		return archivo;
	}
	public void setArchivo(File archivo) {
		this.archivo = archivo;
	}
}
