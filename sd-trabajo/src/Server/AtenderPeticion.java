package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Paquete.Paquete;
import Respositorios.Repositorio;

public class AtenderPeticion implements Runnable {

	private static List<Repositorio> repositorios = new ArrayList<Repositorio>();
	private Socket S;

	public AtenderPeticion(Socket S, List<Repositorio> repositorios) {
		this.S = S;
		AtenderPeticion.repositorios = repositorios;
	}

	@Override
	public void run() {
		try (ObjectInputStream ois = new ObjectInputStream(S.getInputStream());
				ObjectOutputStream oos = new ObjectOutputStream(S.getOutputStream());) {
			Paquete paquete = null;
			try {
				paquete = (Paquete) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String request = paquete.getComando();

			String[] request_array = request.split(" ");
			String opcion = request_array[0];
			Paquete devuelto = null;
			switch (opcion) {
			case "CLONE":
				devuelto = AtenderPeticion.clone(request_array[1]);
				break;
			case "ADD":
				Repositorio repo = paquete.getRepositorio();
				if (repo != null) {
					devuelto = AtenderPeticion.anadir(repo);
				} else {
					devuelto = new Paquete("Error\r\n");
				}
				break;
			case "REMOVE":
				devuelto = AtenderPeticion.remove(request_array[1]);
				break;
			case "PUSH":
				Repositorio repo1 = paquete.getRepositorio();
				if (repo1 != null) {
					devuelto = AtenderPeticion.push(repo1);
				} else {
					devuelto = new Paquete("Error\r\n");
				}
				break;
			case "PULL":
				devuelto = AtenderPeticion.pull(request_array[1]);
				break;
			default:
				devuelto = new Paquete("Error\r\n");
				break;
			}
			oos.writeObject(devuelto);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Paquete clone(String repositorio) {
		Repositorio repo = null;
		Paquete devuelto = null;
		for (int i = 0; i < repositorios.size(); i++) {
			if (repositorios.get(i).getNombre().equals(repositorio)) {
				repo = repositorios.get(i);
			}
		}
		if (repo != null) {
			devuelto = new Paquete("Repositorio clonado", repo);
		} else {
			devuelto = new Paquete(repositorio + " no existe\r\n");
		}

		return devuelto;
	}

	public static Paquete push(Repositorio repositorio) {
		repositorios.add(repositorio);
		FileOutputStream f = null;
		try {
			f = new FileOutputStream("BDServer\\" + repositorio.getNombre());
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
			oos.writeObject(repositorio);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Server.repositoriosSerializadosServer.put(repositorio.getNombre(), "BDServer\\" + repositorio.getNombre());
		synchronized (Server.RUTA_DE_LA_BD_SERVER) {
			ObjectOutputStream elMap = null;
			try {
				elMap = new ObjectOutputStream(new FileOutputStream(Server.RUTA_DE_LA_BD_SERVER));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				elMap.writeObject(Server.repositoriosSerializadosServer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Server.leerBD();
			try {
				elMap.flush();
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
		Paquete devuelto = new Paquete(repositorio.getNombre() + " ha sido subido\r\n");
		return devuelto;

	}

	public static Paquete anadir(Repositorio repositorio) {
		boolean noExiste = true;
		Paquete devuelto = null;
		for (int i = 0; i < repositorios.size(); i++) {
			if (repositorios.get(i).getNombre().equals(repositorio.getNombre())) {
				noExiste = false;
				devuelto = new Paquete("Ya existe un repositorio con ese nombre\r\n");
				break;
			}
		}

		if (noExiste) {
			Repositorio repo = new Repositorio(repositorio.getNombre());
			repositorios.add(repo);
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream("BDServer\\" + repo.getNombre()));
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			try {
				oos.writeObject(repo);
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			try {
				oos.flush();
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			try {
				oos.close();
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			Server.repositoriosSerializadosServer.put(repo.getNombre(), "BDServer\\" + repo.getNombre());
			ObjectOutputStream elMap = null;
			try {
				elMap = new ObjectOutputStream(new FileOutputStream(Server.RUTA_DE_LA_BD_SERVER));
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			try {
				elMap.writeObject(Server.repositoriosSerializadosServer);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				elMap.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				elMap.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			devuelto = new Paquete(repositorio.getNombre() + " ha sido creado\r\n");
		}
		return devuelto;
	}

	public static Paquete pull(String repositorio) {
		return clone(repositorio);
	}

	public static Paquete remove(String repositorio) {
		boolean aux = false;
		Paquete devuelto = null;
		for (int i = 0; i < repositorios.size(); i++) {
			if (repositorios.get(i).getNombre().equals(repositorio) && (!aux)) {
				aux = true;
			}
		}
		Server.repositoriosSerializadosServer.remove(repositorio);
		ObjectOutputStream elMap = null;
		try {
			elMap = new ObjectOutputStream(new FileOutputStream(Server.RUTA_DE_LA_BD_SERVER));
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			elMap.writeObject(Server.repositoriosSerializadosServer);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			elMap.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			elMap.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (aux) {
			List<Repositorio> nombrados = repositorios.stream().filter(n -> n.getNombre().equals(repositorio))
					.collect(Collectors.toList());
			repositorios.removeAll(nombrados);
			File archivoBorrar = new File("BDServer\\" + repositorio);
			archivoBorrar.delete();
			devuelto = new Paquete(repositorio + " ha sido eliminado\r\n");
		} else {
			devuelto = new Paquete(repositorio + " no existe\r\n");
		}
		return devuelto;
	}
}
