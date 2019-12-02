package Cliente;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			String opcion="CLONE";//Prueba, solicitar por teclado en la realidad-
			if (opcion=="ADD")
			{
				out.write("ADD " + "prueba" + "\r\n");
				out.flush();
			}
			if(opcion=="CLONE")
			{
				out.write("CLONE " + "prueba" + "\r\n");
				out.flush();
				FileOutputStream f=new FileOutputStream("prueba");
				ObjectOutputStream oos=new ObjectOutputStream(f);
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				Repositorio repo=(Repositorio) ois.readObject();
				repositoriosLocalesConfirmados.add(repo);
				oos.writeObject(repo);
				oos.flush();
				ois.close();
				oos.close();
				
			}
			if(opcion=="REMOVE")
			{
				out.write("REMOVE " + "prueba" + "\r\n");
				out.flush();
			}
			if(opcion=="PUSH")
			{
				out.write("PUSH " + "prueba" + "\r\n");
				out.flush();
				Repositorio repo=null;
				for(int i=0;i<repositoriosLocalesConfirmados.size();i++)
				{
					if(repositoriosLocalesConfirmados.get(i).getNombre().equals("prueba"))
					{
						repo=repositoriosLocalesConfirmados.get(i);
					}
				}
				
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(repo);
				oos.flush();
				out.flush();
			}
			if(opcion=="PULL")
			{
				out.write("PULL " + "prueba" + "\r\n");
				out.flush();
				FileOutputStream f=new FileOutputStream("prueba");
				ObjectOutputStream oos=new ObjectOutputStream(f);
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				Repositorio repo=(Repositorio) ois.readObject();
				Repositorio repoActual=null;
				for(int i=0;i<repositoriosLocalesConfirmados.size();i++)
				{
					if(repositoriosLocalesConfirmados.get(i).getNombre().equals("prueba"))
					{
						repoActual=repositoriosLocalesConfirmados.get(i);
					}
				}
				//if()
						{
							repositoriosLocalesConfirmados.add(repo);
						}
				
				oos.writeObject(repo);
				oos.flush();
				ois.close();
				oos.close();
				
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
