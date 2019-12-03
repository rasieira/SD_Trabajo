package Principal;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import Cliente.Cliente;
import Respositorios.Archivo;
import Respositorios.Repositorio;

public class Principal {

	public static void main(String[] args) {
		int puerto=Integer.parseInt("6666");
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
		Cliente c=new Cliente("localhost",puerto);
		Archivo a1=null;
		Date d1=null;
		File f1=null;
		Repositorio r1=null;
		List<Archivo> archivos = new ArrayList<Archivo>();
		String nombre=null;
		long numero;
		for(int i=0;i<20;i++)
		{
			for(int j=0; j<20;j++)
			{
				numero= (long) (Math.random()*10+1*1000000);
				nombre="prueba"+i;
				d1=new Date(numero);
				f1=new File(nombre);
				a1=new Archivo(f1,d1);
				archivos.add(a1);
			}
			r1=new Repositorio(nombre);
			r1.actualizarFechaModificacion();
			r1.setArchivos(archivos);
			repositorios.add(r1);
		}
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(System.in);
		int opcion;
		while(true)
		{
			System.out.println("1)CLONE");
			System.out.println("2)PUSH");
			System.out.println("3)PULL");
			System.out.println("4)CREMOVE");
			System.out.println("5)CREATE/ADD");
			opcion=sc.nextInt();
			switch(opcion)
			{
			case 1:
				System.out.println("Introduce el nombre del Repositorio que quieres clonar");
				Scanner sc2=new Scanner(System.in);
				String nombreRepo=sc2.nextLine();
				c.clonar(nombreRepo);
				break;
			case 2:
				System.out.println("Introduce el nombre del Repositorio que quieres subir");
				Scanner sc21=new Scanner(System.in);
				String nombreRepo1=sc21.nextLine();
				c.push(nombreRepo1);
				break;
			case 3:
				System.out.println("Introduce el nombre del Repositorio que quieres traerte");
				Scanner sc211=new Scanner(System.in);
				String nombreRepo11=sc211.nextLine();
				c.pull(nombreRepo11);
				break;
			case 4:
				System.out.println("Introduce el nombre del Repositorio que quieres eliminar");
				Scanner sc2111=new Scanner(System.in);
				String nombreRepo111=sc2111.nextLine();
				c.eliminar(nombreRepo111);
				break;
			case 5:
				System.out.println("Introduce el nombre del Repositorio que quieres crear");
				Scanner sc21111=new Scanner(System.in);
				String nombreRepo1111=sc21111.nextLine();
				c.añadir(nombreRepo1111);
				break;
				default:
					break;
			}
		}

	}

}
