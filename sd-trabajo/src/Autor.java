
public class Autor {

	private String nombre;
	private String apellidos;
	private int ip;
	@SuppressWarnings("unused")
	private String contraseña;
	private Autor()
	{
		this.nombre=null;
		this.apellidos=null;
		this.ip=0;
		this.contraseña="admin";
		
	}
	private Autor(String nombre,String apellidos,int ip, String contraseña)
	{
		this.nombre=nombre;
		this.apellidos=apellidos;
		this.ip=ip;
		this.contraseña=contraseña;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public int getIp() {
		return ip;
	}
	public void setIp(int ip) {
		this.ip = ip;
	}
	
}
