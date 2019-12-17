package Principal;

import java.util.InputMismatchException;
import java.util.Scanner;

import Cliente.Cliente;

public class Principal {

	public static void main(String[] args) {
		Cliente.init();
		@SuppressWarnings("resource")
		Scanner sc=new Scanner(System.in);
		int opcion = 0;
		while(true)
		{
			System.out.println("1)CLONE");
			System.out.println("2)PUSH");
			System.out.println("3)PULL");
			System.out.println("4)REMOVE");
			System.out.println("5)CREATE/ADD");
			System.out.println("6)LISTAR");
			try{opcion=sc.nextInt();}
			catch(InputMismatchException e)
			{
				System.out.println("Opcion incorrecta");
				return;
			}
			switch(opcion)
			{
			case 1:
				System.out.println("Introduce el nombre del Repositorio que quieres clonar");
				Scanner sc2=new Scanner(System.in);
				String nombreRepo=sc2.nextLine();
				Cliente.clonar(nombreRepo);
				break;
			case 2:
				System.out.println("Introduce el nombre del Repositorio que quieres subir");
				Scanner sc21=new Scanner(System.in);
				String nombreRepo1=sc21.nextLine();
				Cliente.push(nombreRepo1);
				break;
			case 3:
				System.out.println("Introduce el nombre del Repositorio que quieres traerte");
				Scanner sc211=new Scanner(System.in);
				String nombreRepo11=sc211.nextLine();
				Cliente.pull(nombreRepo11);
				break;
			case 4:
				System.out.println("Introduce el nombre del Repositorio que quieres eliminar");
				Scanner sc2111=new Scanner(System.in);
				String nombreRepo111=sc2111.nextLine();
				Cliente.eliminar(nombreRepo111);
				break;
			case 5:
				System.out.println("Introduce el nombre del Repositorio que quieres crear");
				Scanner sc21111=new Scanner(System.in);
				String nombreRepo1111=sc21111.nextLine();
				Cliente.añadir(nombreRepo1111);
				break;
			case 6:
				Cliente.listar();
				break;
			}
		}

	}

}
