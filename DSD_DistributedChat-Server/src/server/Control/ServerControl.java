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
		this.portaServer = 56003;
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
					System.out.println("id stream: " + (streamList.size() - 1));
					manager.start();
					System.out.println("LANÇOU MANAGER");
				} else {
					System.out.println("CONEXÃO NÃO EFETUADA");
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

			if (clienteTeste == null) {
				if (dao.store(cliente)) {
					teste = "stored";
					addresser(teste, idStream);
					getUserList(idStream, 1, 0);
				} else {
					teste = "fail to store";
					addresser(teste, idStream); 
				}
			} else if (clienteTeste.getEmail().equalsIgnoreCase(cliente.getEmail())
					&& clienteTeste.getSenha().equalsIgnoreCase(cliente.getSenha())) {
				if (dao.update(cliente)) {
					teste = "actualized";
					addresser(teste, idStream);
				} else {
					teste = "fail to actualize";
					addresser(teste, idStream);
				}
			}
		} else {
			teste = "fail to store";
			addresser(teste, idStream);
		}
	}

	public String authenticateUser(String response, int idStream) {
		String[] vetor = response.split(",");
		String email = vetor[0];
		String senha = vetor[1];

		ClienteServer cliLogin = dao.consultarCliente(email);
		System.out.println("login server achou o cliente: " + cliLogin);

		if (email.equalsIgnoreCase(cliLogin.getEmail()) && senha.equalsIgnoreCase(cliLogin.getSenha())) {
			addresser("Welcome", idStream);
			getUserList(idStream, 2, cliLogin.getId());
		} else {
			addresser("fail to log", idStream);
		}
		return "";
	}

	public void addContact(String response, int idStream) {
		String[] vetor = response.split(",");
		String emailCliente = vetor[0];
		String emailContato = vetor[1];

		ClienteServer cont = dao.consultarCliente(emailContato);
		ClienteServer cli = dao.consultarCliente(emailCliente);

		if (cont != null && cli != null) {
			if (dao.storeContact(cli.getId(), cont.getId())) {
				addresser("added", idStream);
				getUserList(idStream, 2, cli.getId());
			} else {
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
			if (dao.removeContact(cli.getId(), cont.getId())) {
				addresser("removed", idStream);
				getUserList(idStream, 2, cli.getId());
			} else {
				addresser("fail to remove", idStream);
			}
		}
	}

	public void isClientAlive() {
		while(true) {
			try {
				sleep(10000);
				List<ClienteServer> lista = dao.consultar(1, 0);
				boolean cond = false;
				for (int i = 0; i < lista.size(); i++) {
					for (int j = 0; j < liveclients.size(); j++) {
						if(lista.get(i).getEmail().equalsIgnoreCase(liveclients.get(j))) {
							cond = true;
							break;
						}
					}
					if(!cond) {
						lista.get(i).setStatus("OFFLINE");
						dao.update(lista.get(i));
					}
					cond = false;
				}
				lista = dao.consultar(1, 0);
				for (int i = 0; i < lista.size(); i++) {
					if(lista.get(i).getStatus().equalsIgnoreCase("ONLINE")){
						for (int j = 0; j < streamList.size(); j++) {
							if(streamList.get(j).getSocketStream().getInetAddress().toString().
									equalsIgnoreCase(lista.get(i).getIpCliente())){
										getUserList(streamList.get(j).getIdStream(), 2, lista.get(i).getId());
									}
						}
					}
				}
			} catch (Exception e) {
				System.err.println("ERRO NO SERVER isClientAlive " + e);
			}
		}
	}

	public void getUserList(int idStream, int opt, int idBusca) {
		clienteList = dao.consultar(opt, idBusca);
		String listaJson = gson.toJson(clienteList);
		System.out.println("getUserList server JSON, deve enviar ao cliente: " + listaJson);
		addresser(listaJson, idStream);
		clienteList.clear();
	}

	public void logout(String response, int idStream) {

		answer = "logged out";
		addresser(answer, idStream);
//		try {
//			sleep(20000);
//			socketList.get(idStream).close();
//			streamList.get(idStream).closeStream();
//		} catch (IOException | InterruptedException e) {
//			System.err.println("ERRO AO FECHAR SOCKET NO SERVER LOGOUT " + e);
//		}
	}

	public void addresser(String response, int idStream) {
		try {
			System.out.println("addresser: " + idStream);
			streamList.get(idStream).sendMessage(response);
		} catch (IOException e) {
			System.err.println("ERRO NA CONEXAO " + e);
		}
	}

	public List<String> getLiveclients() {
		return liveclients;
	}

	

}
