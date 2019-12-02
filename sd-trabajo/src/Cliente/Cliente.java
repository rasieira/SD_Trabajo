package Cliente;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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
	public void clonar(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			out.write("CLONE " + repositorio +"\r\n");
			out.flush();
			FileOutputStream f=new FileOutputStream("prueba");
			ObjectOutputStream oos=new ObjectOutputStream(f);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			Repositorio repo=(Repositorio) ois.readObject();
			getRepositoriosLocalesConfirmados().add(repo);
			oos.writeObject(repo);
			oos.flush();
			ois.close();
			oos.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void añadir(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			out.write("ADD " +repositorio+ "\r\n");
			out.flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void eliminar(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			out.write("REMOVE " + repositorio+"\r\n");
			out.flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void push(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			out.write("PUSH " + repositorio + "\r\n");
			out.flush();
			Repositorio repo=null;
			for(int i=0;i<getRepositoriosLocalesConfirmados().size();i++)
			{
				if(getRepositoriosLocalesConfirmados().get(i).getNombre().equals(repositorio))
				{
					repo=getRepositoriosLocalesConfirmados().get(i);
				}
			}
			repo.setVersion(repo.getVersion()+0.1);
			ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(repo);
			oos.flush();
			out.flush();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void pull(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			//Se queda con el repositorio con fecha mas reciente
			//REVISAR!!!!!
			out.write("CLONE " + repositorio+"\r\n");
			out.flush();
			FileOutputStream f=new FileOutputStream("prueba");
			ObjectOutputStream oos=new ObjectOutputStream(f);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			Repositorio repo=(Repositorio) ois.readObject();
			Repositorio repoActual=null;
			for(int i=0;i<Cliente.getRepositoriosLocalesConfirmados().size();i++)
			{
				if(Cliente.getRepositoriosLocalesConfirmados().get(i).equals(repositorio))
				{
					repoActual=Cliente.getRepositoriosLocalesConfirmados().get(i);
				}
			}
			if(repo.getFechaModif().getTime()>=repoActual.getFechaModif().getTime())
			{
				getRepositoriosLocalesConfirmados().add(repo);
			}
			oos.writeObject(repo);
			oos.flush();
			ois.close();
			oos.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<Repositorio> getRepositoriosLocalesConfirmados() {
		return repositoriosLocalesConfirmados;
	}
	public static void setRepositoriosLocalesConfirmados(List<Repositorio> repositoriosLocalesConfirmados) {
		Cliente.repositoriosLocalesConfirmados = repositoriosLocalesConfirmados;
	}

}
