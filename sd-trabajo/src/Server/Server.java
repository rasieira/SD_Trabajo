package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Respositorios.Repositorio;
import Server.AtenderPeticion;

public class Server {
	public static void main(String[] args)
	{
		List<Repositorio> repositorios = new ArrayList<Repositorio>();
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
