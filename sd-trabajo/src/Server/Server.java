package Server;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Respositorios.Archivo;
import Respositorios.Repositorio;
import Server.AtenderPeticion;

public class Server {
	public static void main(String[] args)
	{
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
		
		//////////////////////////////////////////////////////////////
		Archivo a1=null;
		Date d1=null;
		File f1=null;
		Repositorio r1=null;
		List<Archivo> archivos = new ArrayList<Archivo>();
		String nombre=null;
		long numero;
		for(int i=0;i<10;i++)
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
			repositorios.add(r1);
		}
		///////////////////////////////////////////////////////////////
		//Crear el listado de Respositorios y pasarselo al atender peticion
		ExecutorService pool = Executors.newCachedThreadPool();
		
		ServerSocket SS;
		try
		{
			SS = new ServerSocket(6666);
		}
		catch(IOException e)
		{
			e.printStackTrace();
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
