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
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	private double version;
	private List<Archivo> archivos;
	private Date fechaModif;
	private Date fechaCreacion;
	public Repositorio(String nombre) {
		this.nombre=nombre;
		this.archivos = null;
		this.fechaModif=new Date();
		this.fechaCreacion=new Date();
		this.setVersion(1.0);
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
	public Date getFechaModif() {
		return fechaModif;
	}
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void actualizarFechaModificacion()
	{
		long max=0;
		Date aux=null;
		if(this.archivos==null)
		{
			this.fechaModif=this.fechaCreacion;
		}
		else
		{
			max=this.archivos.get(0).getFecha().getTime();
			for(int i = 0; i < this.archivos.size(); i++)
			{

				if(max<this.archivos.get(i).getFecha().getTime())
				{
					max=this.archivos.get(i).getFecha().getTime();
					aux=this.archivos.get(i).getFecha();
				}
			}
			if(this.fechaCreacion.getTime()>=max)
			{
				this.fechaModif=this.fechaCreacion;
			}
			else
			{
				this.fechaModif=aux;
			}
		}
	}
	public double getVersion() {
		return version;
	}
	public void setVersion(double version) {
		this.version = version;
	}
}
