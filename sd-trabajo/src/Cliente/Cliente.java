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
	public static void main(String[] args)
	{
		try (Socket s = new Socket("localhost", 6666);
				BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));)
		{
			String opcion=args[0];
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
