package Principal;

import Cliente.Cliente;

public class Principal {

	public static void main(String[] args) {
		int puerto=Integer.parseInt("6666");
		Cliente c=new Cliente("localhost",puerto);
		String repositorio="prueba";
		//c.añadir(repositorio);
		c.clonar(repositorio);

	}

}
