package Cliente;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Respositorios.Repositorio;

public class Cliente {
	private static String RUTA_DEL_MAP = "base_de_datos_toguapa";
	private static Map<String, String> repositoriosSerializados = new HashMap<>(); 
	private static List<Repositorio> repositoriosLocalesConfirmados = new ArrayList<Repositorio>();
	private static String host="localhost";
	private static int puerto=6666;
	
	@SuppressWarnings("unchecked")
	public static void init() {
		// traernos el map.
		if(!new File(RUTA_DEL_MAP).exists())
			return;
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_DEL_MAP))) {
			Object leido = ois.readObject();
			if(leido instanceof Map<?,?>) {
				repositoriosSerializados = (Map<String,String>) leido;
			}
		} catch(IOException|ClassNotFoundException e) {
			e.printStackTrace();
		}
		// construimos la lista a partir del map
		for(String nombreRepositorio : repositoriosSerializados.keySet())
		{
			Repositorio r = null;
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(repositoriosSerializados.get(nombreRepositorio)))) {
				r = (Repositorio) ois.readObject();
			} catch (IOException|ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			if(r!=null)
				repositoriosLocalesConfirmados.add(r);
		}
	}
	public Cliente(String host,int puerto)
	{
		Cliente.puerto=puerto;
		Cliente.host=host;
	}
	public static void clonar(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			out.write("CLONE " + repositorio +"\r\n");
			out.flush();
			FileOutputStream f=new FileOutputStream(repositorio);
			ObjectOutputStream oos=new ObjectOutputStream(f);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			Repositorio repo=(Repositorio) ois.readObject();
			if(estaEnLocal(repositorio))
			{
				Cliente.pull(repositorio);
			}
			else
			{
				getRepositoriosLocalesConfirmados().add(repo);
				oos.writeObject(repo);
				repositoriosSerializados.put(repositorio, repositorio); //el segundo es la ruta
			}
			oos.flush();
			ois.close();
			oos.close();
			
			ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
			elMap.writeObject(repositoriosSerializados);
			elMap.close();
			
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
	public static void añadir(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			char[] respuesta = new char[255];
			out.write("ADD " +repositorio+ "\r\n");
			out.flush();
			while(in.read(respuesta) != -1) {
				System.out.println(respuesta);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void eliminar(String repositorio)
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
	public static void push(String repositorio)
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
	@SuppressWarnings("unused")
	public static void pull(String repositorio)
	{
		try (Socket s = new Socket(host, puerto);
				InputStreamReader in = new InputStreamReader(s.getInputStream());
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			//Se queda con el repositorio con fecha mas reciente
			//REVISAR!!!!!
			out.write("CLONE " + repositorio+"\r\n");
			out.flush();
			FileOutputStream f=new FileOutputStream(repositorio);
			ObjectOutputStream oos=new ObjectOutputStream(f);
			ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
			Repositorio remote=(Repositorio) ois.readObject();
			Repositorio local=null;
			List<Repositorio> listadeloscojones = Cliente.getRepositoriosLocalesConfirmados();
			for(int i=0;i<Cliente.getRepositoriosLocalesConfirmados().size();i++)
			{
				if(Cliente.getRepositoriosLocalesConfirmados().get(i).getNombre().equals(repositorio))
				{
					local=Cliente.getRepositoriosLocalesConfirmados().get(i);
				}
			}
			if((local==null)||(remote.getFechaModif().getTime()>=local.getFechaModif().getTime()))
			{
				getRepositoriosLocalesConfirmados().add(remote);
				repositoriosSerializados.put(repositorio, repositorio); // el segundo es la ruta
			}
			oos.writeObject(remote);
			ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
			elMap.writeObject(repositoriosSerializados);
			elMap.close();
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
	public static boolean estaEnLocal(String nombre)
	{
		boolean esta=false;
		for(int i=0; i<getRepositoriosLocalesConfirmados().size();i++)
		{
			if(getRepositoriosLocalesConfirmados().get(i).getNombre().equals(nombre))
			{
				esta=true;
			}
		}
		return esta;
	}
	public static void listar()
	{
		for(int i=0;i<repositoriosLocalesConfirmados.size();i++)
		{
			System.out.println(repositoriosLocalesConfirmados.get(i).getNombre());
		}
	}

}
