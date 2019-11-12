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
		this.liveclients = new ArrayList<String>();
		this.portaServer = 56000;
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

	public synchronized void connector() {
		while (true) {
			try {
				socket = server.accept();

				if (socket != null) {
					socketList.add(socket);
					streamServer = new StreamServer();
					streamServer.createStream(socket);
					streamList.add(streamServer);
					manager = new RequestManager(streamServer, this, streamList.size() - 1);
					manager.start();
				} else {
					System.out.println("CONEXÃO NÃO EFETUADA");
				}
			} catch (IOException e) {
				System.err.println("ERRO NA CONEXAO " + e);
			}
		}
	}

	public synchronized String register(String response, int idStream) {
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

		return teste;
	}

	public synchronized void authenticateUser(String response, int idStream) {
		String[] vetor = response.split(",");
		String email = vetor[0];
		String senha = vetor[1];

		ClienteServer cliLogin = dao.consultarCliente(email);

		if (email.equalsIgnoreCase(cliLogin.getEmail()) && senha.equalsIgnoreCase(cliLogin.getSenha())) {
			addresser("Welcome", idStream);
			getUserList(idStream, 2, cliLogin.getId());
		} else {
			addresser("fail to log", idStream);
		}
	}



	// COMO GUARDAR USUARIO NA LISTA DE CONTATOS DO DB
	public synchronized void addContact(String response, int idStream) {
		// fazer uma busca pelo email do contato no DB, encontrando, incluir em outra
		// tabela com vinculo com o id/email do contato.

		addresser("RESPOSTA AO SOLICITANTE", idStream);
	}

	// COMO REMOVER USUARIO NA LISTA DE CONTATOS DO DB
	public synchronized void removeContact(String response, int idStream) {
		// fazer uma busca pelo email do contato no DB, encontrando, excluir em outra
		// tabela com vinculo com o id/email do contato.

		addresser("RESPOSTA AO SOLICITANTE", idStream);
	}

	// COMO BUSCAR LISTA DE USUARIOS NA LISTA DE CONTATOS DO DB
	public synchronized void getUserList(int idStream, int opt, int idBusca) {
		clienteList = dao.consultar(opt, idBusca);
		String listaJson = gson.toJson(clienteList);
		addresser(listaJson, idStream);
		
		
		try {
			sleep(5000);
			for (int i = 0; i < liveclients.size(); i++) {
				// busca no DB os email ativos e inativos. monta um Gson e envia aos contatos.
				// remover todos elementos de liveclients

				addresser("RESPOSTA AO SOLICITANTE", i);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// observer atulizando contatos online aos que estao online
		// conexão fechada

	}
	
	// FALTA observer atulizando contatos online aos que estao online
	public synchronized void logout(String response, int idStream) {

		answer = "logged out";
		addresser(answer, idStream);
		try {
			sleep(3000);
			socketList.get(idStream).close();
			streamList.get(idStream).closeStream();
		} catch (IOException | InterruptedException e) {
			System.err.println("ERRO AO FECHAR SOCKET NO SERVER LOGOUT " + e);
		}
	}
	
	public synchronized void isClientAlive(String response, int idStream) {
		// deve adicionar o email dos usuarios ativos online
		liveclients.add(response);

	}

	public synchronized void addresser(String response, int idStream) {
		try {
			streamList.get(idStream).sendMessage("CLIENTE");
			streamList.get(idStream).sendMessage(response);
		} catch (IOException e) {
			System.err.println("ERRO NA CONEXAO " + e);
		}
	}

}
