/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.Control;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Thread.sleep;
import com.google.gson.Gson;
import server.Model.ClienteServer;
import server.dao.ClienteDao;

/**
 *
 * @author ipdmartins
 */
public class ServerControl {

	private List<ClienteServer> clienteList;
	private Socket socket;
	private ServerSocket server;
	private List<StreamServer> streamList;
	private List<Socket> socketList;
	private int portaServer;
	private ClienteServer cliente;
	private Gson gson;
	private String answer;
	private StreamServer streamServer;
	private RequestManager manager;
	private List<String> liveclients;
	private ClienteDao dao;

	public ServerControl() {
		this.socket = null;
		this.server = null;
		this.cliente = new ClienteServer();
		this.gson = new Gson();
		this.answer = "";
		this.streamServer = new StreamServer();
		this.clienteList = new ArrayList<ClienteServer>();
		this.streamList = new ArrayList<StreamServer>();
		this.socketList = new ArrayList<Socket>();
		this.liveclients = new ArrayList<String>();
		this.portaServer = 56005;
		this.dao = new ClienteDao();
		setServer();
	}

	public void setServer() {
		try {
			server = new ServerSocket(portaServer);
			server.setReuseAddress(true);
			connector();
		} catch (IOException e) {
			System.err.println("ERRO NA CONEXAO " + e);
		}
	}

	public void connector() {
		while (true) {
			try {
				this.socket = null;
				System.out.println("SERVER NO AGUARDO");
				socket = server.accept();
				if (socket != null) {
					socketList.add(socket);
					streamServer = new StreamServer();
					streamServer.createStream(socket, streamList.size() - 1);
					streamList.add(streamServer);
					manager = new RequestManager(streamServer, this, streamList.size() - 1);
					manager.start();
				} else {
					System.out.println("CONEX�O N�O EFETUADA");
				}
			} catch (IOException e) {
				System.err.println("ERRO NA CONEXAO " + e);
			}
		}
	}

	public void register(String response, int idStream) {
		String teste = "";
		this.cliente = gson.fromJson(response, ClienteServer.class);
		if (!cliente.getEmail().equalsIgnoreCase(null)) {
			ClienteServer clienteTeste = dao.consultarCliente(cliente.getEmail());
			String ip = socketList.get(idStream).getInetAddress().toString();
			if (ip.contains("/")) {
				ip = ip.replace("/", "");
			}
			this.cliente.setIpCliente(ip);
			this.cliente.setPortaCliente(socketList.get(idStream).getPort());

			if (clienteTeste == null) {
				if (dao.store(cliente)) {
					addresser("A", idStream);
					teste = "stored";
					addresser(teste, idStream);
				} else {
					addresser("A", idStream);
					teste = "fail to store";
					addresser(teste, idStream);
				}
			} else if (clienteTeste.getEmail().equalsIgnoreCase(cliente.getEmail())
					&& clienteTeste.getSenha().equalsIgnoreCase(cliente.getSenha())) {
				if (dao.update(cliente)) {
					addresser("C", idStream);
					teste = "actualized";
					addresser(teste, idStream);
				} else {
					addresser("C", idStream);
					teste = "fail to actualize";
					addresser(teste, idStream);
				}
			}
		} else {
			teste = "fail to store";
			addresser(teste, idStream);
		}
	}

	public void validatePass(String response, int idStream) {
		String[] vetor = response.split(",");
		String email = vetor[0];
		String senha = vetor[1];

		ClienteServer cliLogin = dao.consultarCliente(email);

		if (email.equalsIgnoreCase(cliLogin.getEmail()) && senha.equalsIgnoreCase(cliLogin.getSenha())) {
			addresser("B", idStream);
			addresser("Granted", idStream);
		} else {
			addresser("B", idStream);
			addresser("Validation failed", idStream);
		}
	}

	public void authenticateUser(String response, int idStream) {
		String[] vetor = response.split(",");
		String email = vetor[0];
		String senha = vetor[1];

		ClienteServer cliLogin = dao.consultarCliente(email);

		String ip = socketList.get(idStream).getInetAddress().toString();
		if (ip.contains("/")) {
			ip = ip.replace("/", "");
		}

		System.out.println("ip: " + cliLogin.getIpCliente());

		if (email.equalsIgnoreCase(cliLogin.getEmail()) && senha.equalsIgnoreCase(cliLogin.getSenha())) {
			cliLogin.setStatus("ONLINE");
			if (!cliLogin.getIpCliente().equalsIgnoreCase(ip)) {
				cliLogin.setIpCliente(ip);
			}
			if (socketList.get(idStream).getPort() == 56000) {
				cliLogin.setPortaCliente(56001);
			} else if (socketList.get(idStream).getPort() == 56005) {
				cliLogin.setPortaCliente(56004);
			} else {
				cliLogin.setPortaCliente(socketList.get(idStream).getPort() + 1);
			}
			dao.update(cliLogin);
			cliLogin.setTime();
			clienteList.add(cliLogin);
			addresser("D", idStream);
			String cliente = gson.toJson(cliLogin);
			addresser(cliente, idStream);
			addresser("E", idStream);
			getUserList(idStream, 2, cliLogin.getId(), 1, cliLogin, 1);
		} else {
			addresser("D", idStream);
			addresser("fail to log", idStream);
		}
	}

	public void addContact(String response, int idStream) {
		String[] vetor = response.split(",");
		String emailCliente = vetor[0];
		String emailContato = vetor[1];

		ClienteServer cont = dao.consultarCliente(emailContato);
		ClienteServer cli = dao.consultarCliente(emailCliente);

		if (cont != null && cli != null) {
			if (dao.storeContact(cli.getId(), cont.getId()) && dao.storeContact(cont.getId(), cli.getId())) {
				addresser("I", idStream);
				getUserList(idStream, 2, cli.getId(), 0, null, 1);
			} else {
				addresser("I", idStream);
				addresser("fail to add", idStream);
			}
		}
	}

	public void removeContact(String response, int idStream) {
		String[] vetor = response.split(",");
		String emailCliente = vetor[0];
		String emailContato = vetor[1];

		ClienteServer cont = dao.consultarCliente(emailContato);
		ClienteServer cli = dao.consultarCliente(emailCliente);

		if (cont != null && cli != null) {
			if (dao.removeContact(cli.getId(), cont.getId()) && dao.removeContact(cont.getId(), cli.getId())) {
				addresser("H", idStream);
				getUserList(idStream, 2, cli.getId(), 0, null, 1);
			} else {
				addresser("H", idStream);
				addresser("fail to remove", idStream);
			}
		}
	}

	public void getUserList(int idStream, int opt, int idBusca, int login, ClienteServer cliLogin, int cond) {
		// pega a lista de contatos de quem fez login e devolve a ele.
		List<ClienteServer> novaLista = dao.consultar(opt, idBusca);
		if (cond == 1) {
			String listaJson = gson.toJson(novaLista);
			System.out.println("enviando "+listaJson);
			addresser(listaJson, idStream);
		}

		if (login == 1) {
			for (int i = 0; i < novaLista.size(); i++) {
				if (novaLista.get(i).getStatus().equalsIgnoreCase("ONLINE")) {
					for (int j = 0; j < streamList.size(); j++) {
						String ip = streamList.get(j).getSocketStream().getInetAddress().toString();
						if (ip.contains("/")) {
							ip = ip.replace("/", "");
						}
						if (ip.equalsIgnoreCase(novaLista.get(i).getIpCliente())) {
							addresser("F", j);
							String update = gson.toJson(cliLogin, ClienteServer.class);
							addresser(update, j);
						}
					}
				}
			}
		}
		novaLista.clear();
	}

	public void logout(String response, int idStream) {
		ClienteServer cli = dao.consultarCliente(response);
		if (cli != null) {
			cli.setStatus("OFFLINE");
			dao.update(cli);
			for (int i = 0; i < clienteList.size(); i++) {
				if (cli.getEmail().equalsIgnoreCase(clienteList.get(i).getEmail())) {
					clienteList.remove(i);
				}
			}
			addresser("G", idStream);
			addresser("logged out", idStream);
			getUserList(idStream, 2, cli.getId(), 1, cli, 0);
		}
		try {
			sleep(20000);
			socketList.get(idStream).close();
			streamList.get(idStream).closeStream();
		} catch (IOException | InterruptedException e) {
			System.err.println("ERRO AO FECHAR SOCKET NO SERVER LOGOUT " + e);
		}
	}

	public void addresser(String response, int idStream) {
		try {
			streamList.get(idStream).sendMessage(response);
		} catch (IOException e) {
			System.err.println("ERRO NA CONEXAO " + e);
		}
	}

	public void setLiveClient(String req) {
		for (int i = 0; i < clienteList.size(); i++) {
			if(clienteList.get(i).getEmail().equalsIgnoreCase(req)) {
				clienteList.get(i).setTime();
				break;
			}
		}
	}

	public void isClientAlive() {
		while (true) {
			List<ClienteServer> lista = null;
			try {
				sleep(300000);
				for (int i = 0; i < clienteList.size(); i++) {
					long login = clienteList.get(i).getTime();
					long now = System.currentTimeMillis();
					if ((now - login) > 300) {
						clienteList.get(i).setStatus("OFFLINE");
						dao.update(clienteList.get(i));
						lista = dao.consultar(2, clienteList.get(i).getId());

						for (int j = 0; j < lista.size(); j++) {
							if (lista.get(i).getStatus().equalsIgnoreCase("ONLINE")) {
								for (int k = 0; k < streamList.size(); k++) {
									String ip = streamList.get(j).getSocketStream().getInetAddress().toString();
									if (ip.contains("/")) {
										ip = ip.replace("/", "");
									}
									if (ip.equalsIgnoreCase(lista.get(i).getIpCliente())) {
										addresser("F", j);
										String update = gson.toJson(clienteList.get(i), ClienteServer.class);
										addresser(update, j);
										break;
									}
								}
							}
						}
						streamList.get(i).closeStream();
						socketList.get(i).close();
						clienteList.remove(i);
					}
				}
			} catch (InterruptedException e) {
				System.err.println("ERRO SERVER isclientalive " + e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
