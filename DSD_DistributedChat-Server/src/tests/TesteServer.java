/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.Control.ServerControl;
import server.Control.StreamServer;
import server.Model.ClienteServer;
import server.dao.ClienteDao;
import server.dao.Conexao;
import static org.junit.Assert.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ipdmartins
 */

public class TesteServer {

	private static String result = "";
	private static ClienteServer cliente;
	private static Gson gson = new Gson();
	private static String response = "";
	private static Socket socketCliente;
	private static StreamServer streamer;
	private static String serverIP = "192.168.2.171";
	private static String serverPort = "56003";

	@Test
	public void testeClienteControlRegister() {
		cliente = new ClienteServer();
		cliente.setNome("nome27");
		cliente.setEmail("teste27@email.com");
		cliente.setAnoNasc("1901");
		cliente.setSenha("123");
		cliente.setPortaCliente(Integer.parseInt("56005"));
		cliente.setIpCliente("ipClientes");
		cliente.setPortaServer(Integer.parseInt(serverPort));
		cliente.setIpServer(serverIP);
		cliente.setStatus("null");
		response = gson.toJson(cliente);
		if (socketCliente == null) {
			connect();
		}
		response = addresser("A", response);
		assertEquals(response, "stored");
	}

	@Test
	public void testeClienteControlActualize() {
		cliente = new ClienteServer();
		cliente.setNome("nome12");
		cliente.setEmail("teste12@email.com");
		cliente.setAnoNasc("1875");
		cliente.setSenha("1112");
		cliente.setPortaCliente(Integer.parseInt("56005"));
		cliente.setIpCliente("ipCliehfghfd");
		cliente.setPortaServer(Integer.parseInt(serverPort));
		cliente.setIpServer(serverIP);
		cliente.setStatus("null");
		response = gson.toJson(cliente);
		if (socketCliente == null) {
			connect();
		}
		response = addresser("A", response);
		assertEquals(response, "actualized");
	}

	@Test
	public void testeClienteControlLogin() {
		boolean cond = true;
		List<ClienteServer> listaClintes = new ArrayList<ClienteServer>();
		List<String> contatos = new ArrayList<String>();
		contatos.add("teste16@email.com");
		contatos.add("teste13@email.com");
		String login2 = "email24" + "," + "123";
		if (socketCliente == null) {
			connect();
		}
		login2 = addresser("B", login2);
		if (login2.equalsIgnoreCase("Welcome")) {
			String res = listManager();
			cliente = gson.fromJson(res, ClienteServer.class);
			if (cliente.getEmail().equalsIgnoreCase("email24")) {
				res = listManager();
				Type tipoLista = new TypeToken<ArrayList<ClienteServer>>() {
				}.getType();
				listaClintes = gson.fromJson(res, tipoLista);
				for (int i = 0; i < listaClintes.size(); i++) {
					if (!listaClintes.get(i).getEmail().equalsIgnoreCase(contatos.get(i))) {
						cond = false;
					}
				}
			}
		}
		assertTrue(cond);
	}

	@Test
	public void testeClienteControlAdd() {
		String add = "teste2@email.com" + "," + "teste3@email.com";
		if (socketCliente == null) {
			connect();
		}
		add = addresser("D", add);
		if (add.equalsIgnoreCase("added")) {
			add = listManager();
		}
		assertEquals(add, "added");
	}

	@Test
	public void testeClienteControlRemove() {
		String add = "teste2@email.com" + "," + "teste4@email.com";
		if (socketCliente == null) {
			connect();
		}
		add = addresser("E", add);
		if (add.equalsIgnoreCase("removed")) {
			add = listManager();
		}
		assertEquals(add, "removed");
	}

	@Test
	public void testeClienteControlLogout() {
		String logout = "logout";
		if (socketCliente == null) {
			connect();
		}
		logout = addresser("C", logout);
		assertEquals(logout, "logged out");
	}

	public String addresser(String req, String response) {
		try {
			this.streamer.sendMessage(req);
			this.streamer.sendMessage(response);// response é o Json sendo enviado
			this.response = streamer.readMessage();
		} catch (IOException e) {
			System.err.println("ERRO ENVIO GSON REGISTRO " + e);
		}
		return this.response;
	}

	public String listManager() {
		try {
			String json = this.streamer.readMessage();
			return json;
		} catch (IOException e) {
			System.err.println("ERRO AO RECEBER LISTA GSON DO SERVER " + e);
		}
		return "";
	}

	public void connect() {
		try {
			socketCliente = new Socket(serverIP, Integer.parseInt(serverPort));
			if (socketCliente != null) {
				this.streamer = new StreamServer();
				this.streamer.createStream(socketCliente, 1);
			} else {
				System.out.println("CONEXÃO NÃO EFETUADA");
			}
		} catch (IOException e) {
			System.err.println("ERRO NA CONEXAO " + e);
		}
	}

//	@Test
//	public void testeClienteControlNotifylive() {
//		String remove = "teste2@email.com";
//		if (socketCliente == null) {
//			connect();
//		}
//		remove = addresser("F", remove);
//		System.out.println("LISTA DE CONTATOS Notifylive "+remove);
//		Type tipoLista = new TypeToken<ArrayList<ClienteServer>>() {
//		}.getType();
//		List<ClienteServer> lista = gson.fromJson(remove, tipoLista);
//		assertEquals(lista.size(), 1);
//	}

}
