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
	public static String RUTA_DE_LA_BD_SERVER = "BDServer\\base_de_datos_server";
	public static Map<String, String> repositoriosSerializadosServer = new HashMap<>();
	public static List<Repositorio> repositoriosLocalesServer = new ArrayList<Repositorio>();

	@SuppressWarnings("unchecked")
	public static void leerBD() {

		if (!new File(RUTA_DE_LA_BD_SERVER).exists())
			return;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_DE_LA_BD_SERVER))) {
			Object leido = ois.readObject();
			if (leido instanceof Map<?, ?>) {
				repositoriosSerializadosServer = (Map<String, String>) leido;
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		synchronized(repositoriosSerializadosServer) {
		for (String nombreRepositorio : repositoriosSerializadosServer.keySet()) {
			Repositorio r = null;
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(repositoriosSerializadosServer.get(nombreRepositorio)))) {
				r = (Repositorio) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (r != null)
				repositoriosLocalesServer.add(r);
		}
	}

	}

	public static void crearRepositoriosPrueba() {
		ObjectOutputStream elMap = null;
		File directorio = new File("BDServer\\");
		directorio.mkdir();
		Archivo a1 = null;
		Date d1 = null;
		File f1 = null;
		Repositorio r1 = null;
		List<Archivo> archivos = new ArrayList<Archivo>();
		String nombre = null;
		long numero;
		for (int j = 0; j < 5; j++) {
			numero = (long) (Math.random() * 10 * Math.random() + 1 * 1000000 * Math.random());
			nombre = "pruebaArchivo" + j;
			d1 = new Date(numero);
			f1 = new File(nombre);
			a1 = new Archivo(f1, d1);
			archivos.add(a1);
		}
		for (int i = 0; i < 4; i++) {
			r1 = new Repositorio("pruebaRepositorio" + i);
			r1.actualizarFechaModificacion();
			r1.setArchivos(archivos);
			FileOutputStream f = null;
			try {
				f = new FileOutputStream("BDServer\\" + r1.getNombre());
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
			repositoriosSerializadosServer.put(r1.getNombre(), "BDServer\\" + r1.getNombre());

		}
		try {
			elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DE_LA_BD_SERVER));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			elMap.writeObject(repositoriosSerializadosServer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			elMap.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		Server.crearRepositoriosPrueba();
		Server.leerBD();
		ExecutorService pool = Executors.newCachedThreadPool();

		ServerSocket SS;
		try {
			SS = new ServerSocket(6666);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		while (true) {
			try {
				pool.submit(new AtenderPeticion(SS.accept(), repositoriosLocalesServer));
				Server.leerBD();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		pool.shutdown();
		try {
			SS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
