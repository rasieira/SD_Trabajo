package Cliente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Respositorios.Repositorio;

public class Cliente {
	private static List<Repositorio> repositoriosLocalesConfirmados = new ArrayList<Repositorio>();
	private String host;
	private int puerto;
	public Cliente(String host,int puerto)
	{
		this.puerto=puerto;
		this.host=host;
	}
	
	public void conectar()
	{
		try (Socket s = new Socket(host, puerto);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			String opcion="ADD";//Prueba, solicitar por teclado en la realidad-
			if (opcion=="ADD")
			{
				out.write("ADD " + "prueba" + "\r\n");
				out.flush();
				System.out.println(in.readLine());
			}
			if(opcion=="CLONE ")
			{
				out.write("CLONE " + "prueba" + "\r\n");
				out.flush();
				FileInputStream f=new FileInputStream("prueba");
				ObjectInputStream ois=new ObjectInputStream(f);
				Repositorio repo=(Repositorio) ois.readObject();
				repositoriosLocalesConfirmados.add(repo);
				
				ois.close();
				
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
