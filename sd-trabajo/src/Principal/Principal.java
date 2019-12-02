package Principal;

import Cliente.Cliente;

public class Principal {

	public static void main(String[] args) {
		int puerto=Integer.parseInt(args[1]);
		Cliente c=new Cliente(args[0],puerto);
		String repositorio="prueba";
		c.añadir(repositorio);

	}

}
