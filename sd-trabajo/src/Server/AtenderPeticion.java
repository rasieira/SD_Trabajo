package Server;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Respositorios.Repositorio;

public class AtenderPeticion implements Runnable {

	private static List<Repositorio> repositorios = new ArrayList<Repositorio>();
	private Socket S;
	public AtenderPeticion(Socket S,List<Repositorio> repositorios )
	{
		this.S=S;
		AtenderPeticion.repositorios=repositorios;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(S.getInputStream()));
				OutputStreamWriter out = new OutputStreamWriter(S.getOutputStream());)
		{
			String request = in.readLine();

			String[] request_array = request.split(" ");
			if ((!request_array[0].equals("CLONE") 
							&& !request_array[0].equals("ADD")
							&& !request_array[0].equals("PUSH")
							&& !request_array[0].equals("REMOVE")))
			{
				out.write("ERROR\r\n");
				out.flush();
				throw new IllegalArgumentException("Formato de comando incorrecto");
			}

			if (request_array[0].equals("ADD"))
			{
				if ((repositorios.contains(request_array[1])))
				{
					out.write("Ya existe un repositorio con ese nombre\r\n");
					out.flush();
				} else
				{
					Repositorio repo=new Repositorio(request_array[1]);
					repositorios.add(repo);
					for(int i=0; i<repositorios.size();i++)
					{
						System.out.println(repositorios.get(i).getNombre());
					}
					out.write( request_array[1]+ " ha sido creado\r\n");
					out.flush();
				}
	
			}
			if (request_array[0].equals("CLONE"))
			{
				boolean aux=false;
				for(int i=0;i<repositorios.size();i++)
				{
					if(repositorios.get(i).getNombre().equals(request_array[1]))
					{
						aux=true;
					}
				}
				if (aux)
				{
					Repositorio repo=null;
					for(int i=0;i<repositorios.size();i++)
					{
						if(repositorios.get(i).getNombre().equals(request_array[1]))
						{
							repo=repositorios.get(i);
						}
					}
					ObjectOutputStream oos=new ObjectOutputStream(S.getOutputStream());
					oos.writeObject(repo);
					oos.flush();
					out.flush();
				} else
				{
					out.write( request_array[1]+ " no existe\r\n");
					out.flush();
				}
	
			}
			if (request_array[0].equals("REMOVE"))
			{
				boolean aux=false;
				for(int i=0;i<repositorios.size();i++)
				{
					if(repositorios.get(i).getNombre().equals(request_array[1])&&(!aux))
					{
						aux=true;
					}
				}
				if (aux)
				{
					List<Repositorio> nombrados = repositorios.stream().filter(n -> n.getNombre().equals(request_array[1])).collect(Collectors.toList());
					repositorios.removeAll(nombrados);
					out.flush();
				} else
				{
					out.write(request_array[1]+"no existe\r\n");
					out.flush();
				}
	
			}
			if(request_array[0].equals("PUSH"))
			{
				
				FileOutputStream f=new FileOutputStream("prueba");
				ObjectOutputStream oos=new ObjectOutputStream(f);
				ObjectInputStream ois=new ObjectInputStream(S.getInputStream());
				Repositorio repo=(Repositorio) ois.readObject();
				repositorios.add(repo);
				oos.writeObject(repo);
				oos.flush();
				ois.close();
				oos.close();
			}

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
}
