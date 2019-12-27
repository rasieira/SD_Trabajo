package Cliente;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Paquete.Paquete;
import Respositorios.Repositorio;

public class Cliente {
	private static String RUTA_DEL_MAP = "BDCliente\\base_de_datos_cliente";
	private static Map<String, String> repositoriosSerializados = new HashMap<>();
	private static List<Repositorio> repositoriosLocalesConfirmados = new ArrayList<Repositorio>();
	private static String host = "localhost";
	private static int puerto = 6666;

	@SuppressWarnings("unchecked")
	public static void init() {
		File directorio = new File("BDCliente\\");
		directorio.mkdir();
		if (!new File(RUTA_DEL_MAP).exists())
			return;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RUTA_DEL_MAP))) {
			Object leido = ois.readObject();
			if (leido instanceof Map<?, ?>) {
				repositoriosSerializados = (Map<String, String>) leido;
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		for (String nombreRepositorio : repositoriosSerializados.keySet()) {
			Repositorio r = null;
			try (ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(repositoriosSerializados.get(nombreRepositorio)))) {
				r = (Repositorio) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}

			if (r != null)
				repositoriosLocalesConfirmados.add(r);
		}
	}

	public Cliente(String host, int puerto) {
		Cliente.puerto = puerto;
		Cliente.host = host;
	}

	public static void clonar(String repositorio) {
		if (!estaEnLocal(repositorio)) {
			try (Socket s = new Socket(host, puerto);
					ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {
				Paquete envio = new Paquete("CLONE " + repositorio + " \r\n");
				oos.writeObject(envio);
				oos.flush();
				Paquete recibido = (Paquete) ois.readObject();

				String mensajerecibido = recibido.getComando();
				Repositorio repo = recibido.getRepositorio();
				if (repo == null) {
					System.out.println(mensajerecibido);
				} else {
					FileOutputStream f = new FileOutputStream("BDCliente\\" + repositorio);
					ObjectOutputStream oos1 = new ObjectOutputStream(f);
					repo.setNombre(repo.getNombre());
					oos1.writeObject(repo);
					repositoriosSerializados.put(repositorio, "BDCliente\\" + repositorio); // el segundo es la ruta
					System.out.println(mensajerecibido);
					oos1.flush();
					oos1.close();
				}
				ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
				elMap.writeObject(repositoriosSerializados);
				elMap.close();
				repositoriosLocalesConfirmados.add(repo);
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

			
		} else {
			pull(repositorio);
		}
	}

	public static void anadir(String repositorio) {
		try (Socket s = new Socket(host, puerto);
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {

			Repositorio repo = new Repositorio(repositorio);
			FileOutputStream f = new FileOutputStream("BDCliente\\" + repositorio);
			ObjectOutputStream oos1 = new ObjectOutputStream(f);
			oos1.writeObject(repo);
			oos1.flush();
			oos1.close();
			repositoriosSerializados.put(repositorio, "BDCliente\\" + repositorio);
			ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
			elMap.writeObject(repositoriosSerializados);
			elMap.flush();
			elMap.close();
			Paquete envio = new Paquete("ADD " + repositorio + " \r\n", repo);
			oos.writeObject(envio);
			oos.flush();
			Paquete recibido = null;
			try {
				recibido = (Paquete) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String mensajerecibido = recibido.getComando();
			System.out.println(mensajerecibido);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void eliminar(String repositorio) {
		try (Socket s = new Socket(host, puerto);

				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {
			Paquete envio = new Paquete("REMOVE " + repositorio + " \r\n");
			oos.writeObject(envio);
			oos.flush();
			for (int i = 0; i < repositoriosLocalesConfirmados.size(); i++) {
				if (repositoriosLocalesConfirmados.get(i).getNombre().equals(repositorio)) {
					repositoriosLocalesConfirmados.remove(i);
					File archivoBorrar = new File("BDCliente\\" + repositorio);
					archivoBorrar.delete();
				}
			}
			repositoriosSerializados.remove(repositorio);
			Paquete recibido = null;
			try {
				recibido = (Paquete) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String mensajerecibido = recibido.getComando();
			System.out.println(mensajerecibido);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectOutputStream elMap = null;
		try {
			elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			elMap.writeObject(repositoriosSerializados);
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

	public static void push(String repositorio) {
		try (Socket s = new Socket(host, puerto);
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {
			Repositorio repo = null;
			for (int i = 0; i < getRepositoriosLocalesConfirmados().size(); i++) {
				if (getRepositoriosLocalesConfirmados().get(i).getNombre().equals(repositorio)) {
					repo = getRepositoriosLocalesConfirmados().get(i);
				}
			}
			repo.setVersion(repo.getVersion() + 1.0);
			Paquete envio = new Paquete("PUSH " + repositorio + " \r\n", repo);
			oos.writeObject(envio);
			oos.flush();
			Paquete recibido = null;
			try {
				recibido = (Paquete) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String mensajerecibido = recibido.getComando();
			System.out.println(mensajerecibido);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void pull(String repositorio) {
		try (Socket s = new Socket(host, puerto);

				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());) {
			Paquete envio = new Paquete("CLONE " + repositorio + " \r\n");
			oos.writeObject(envio);
			oos.flush();
			FileOutputStream f = new FileOutputStream("BDCliente\\" + repositorio);
			ObjectOutputStream oos1 = new ObjectOutputStream(f);

			Paquete recibido = (Paquete) ois.readObject();
			Repositorio remote = recibido.getRepositorio();
			Repositorio local = null;
			for (int i = 0; i < Cliente.getRepositoriosLocalesConfirmados().size(); i++) {
				if (Cliente.getRepositoriosLocalesConfirmados().get(i).getNombre().equals(repositorio)) {
					local = Cliente.getRepositoriosLocalesConfirmados().get(i);
				}
			}

			if ((local == null) || (remote.getFechaModif().getTime() >= local.getFechaModif().getTime())) {
				repositoriosSerializados.put(repositorio, "BDCliente\\" + repositorio); // el segundo es la ruta
			}
			String mensajerecibido = recibido.getComando();
			System.out.println(mensajerecibido);
			oos1.writeObject(remote);
			ObjectOutputStream elMap = new ObjectOutputStream(new FileOutputStream(RUTA_DEL_MAP));
			elMap.writeObject(repositoriosSerializados);
			elMap.close();
			oos1.flush();
			ois.close();
			oos1.close();
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

	public static boolean estaEnLocal(String nombre) {
		boolean esta = false;
		for (int i = 0; i < getRepositoriosLocalesConfirmados().size(); i++) {
			if (getRepositoriosLocalesConfirmados().get(i).getNombre().equals(nombre)) {
				esta = true;
			}
		}
		return esta;
	}

	public static void listar() {
		for (int i = 0; i < repositoriosLocalesConfirmados.size(); i++) {
			System.out.println(repositoriosLocalesConfirmados.get(i).getNombre());
		}
	}
}
