package Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Respositorios.Archivo;
import Respositorios.Repositorio;

public class Server {
	private static String RUTA_DEL_MAP = "base_de_datos_server";
	private static Map<String, String> repositoriosSerializadosServer = new HashMap<>();
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException, IOException
	{

		ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
		//////////////////////////////////////////////////////////////
		Archivo a1=null;
		Date d1=null;
		File f1=null;
		Repositorio r1=null;
		List<Archivo> archivos = new ArrayList<Archivo>();
		String nombre=null;
		long numero;
		for(int i=0;i<4;i++)
		{
			for(int j=0; j<5;j++)
			{
			numero= (long) (Math.random()*10*Math.random()+1*1000000*Math.random());
			nombre="prueba"+i;
			d1=new Date(numero);
			f1=new File(nombre);
			a1=new Archivo(f1,d1);
			archivos.add(a1);
			}
			r1=new Repositorio(nombre);
			r1.actualizarFechaModificacion();
			r1.setArchivos(archivos);
			FileOutputStream f = null;
			try {
				f = new FileOutputStream(r1.getNombre());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				oos.writeObject(r1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			repositoriosSerializadosServer.put(r1.getNombre(), r1.getNombre());
			elMap.writeObject(repositoriosSerializadosServer);
		}

		if(!new File(RUTA_DEL_MAP).exists())return;
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_DEL_MAP))) {
			Object leido = ois.readObject();
			if(leido instanceof Map<?,?>) {
				repositoriosSerializadosServer = (Map<String,String>) leido;
			}
		} catch(IOException|ClassNotFoundException e) {
			e.printStackTrace();
		}
		// construimos la lista a partir del map
		
		
		for(String nombreRepositorio : repositoriosSerializadosServer.keySet())
		{
			Repositorio r = null;
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(repositoriosSerializadosServer.get(nombreRepositorio)))) {
				r = (Repositorio) ois.readObject();
			} catch (IOException|ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			if(r!=null)
				repositorios.add(r);
		}
		
		///////////////////////////////////////////////////////////////
		//Crear el listado de Respositorios y pasarselo al atender peticion
		ExecutorService pool = Executors.newCachedThreadPool();
		
		ServerSocket SS;
		try
		{
			SS = new ServerSocket(6666);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		while(true)
		{
			try
			{
				pool.submit(new AtenderPeticion(SS.accept(),repositorios));
			}
			catch(IOException e)
			{
				e.printStackTrace();
				break;
			}
		}
		pool.shutdown();
		try
		{
			SS.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
