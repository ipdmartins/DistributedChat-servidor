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

	private static ClienteServer cliente;
	private static Gson gson = new Gson();
	private static String req = "";
	private static String res = "";
	private static Socket socketCliente;
	private static StreamServer streamer;
	private static String serverIP = "10.60.92.90";
	private static String serverPort = "56003";

	@Test
	public void testeClienteControlRegister() {
		cliente = new ClienteServer();
		cliente.setNome("nome10");
		cliente.setEmail("email10");
		cliente.setAnoNasc("1901");
		cliente.setSenha("123");
		cliente.setPortaCliente(Integer.parseInt("56005"));
		cliente.setIpCliente("ipClientes");
		cliente.setPortaServer(Integer.parseInt(serverPort));
		cliente.setIpServer(serverIP);
		cliente.setStatus("null");
		req = gson.toJson(cliente);
		if (socketCliente == null) {
			connect();
		}
		res = addresser("A", req);
		assertEquals(res, "stored");
	}

//	@Test
	public void testeClienteControlActualize() {
		cliente = new ClienteServer();
		cliente.setNome("nome1");
		cliente.setEmail("teste1@email.com");
		cliente.setAnoNasc("1975");
		cliente.setSenha("123");
		cliente.setPortaCliente(Integer.parseInt("56005"));
		cliente.setIpCliente("ipCliehfghfd");
		cliente.setPortaServer(Integer.parseInt(serverPort));
		cliente.setIpServer(serverIP);
		cliente.setStatus("null");
		req = gson.toJson(cliente);
		if (socketCliente == null || socketCliente.isClosed()) {
			connect();
		}
		res = addresser("A", req);
		assertEquals(res, "actualized");
	}

//	@Test
	public void testeClienteControlLogin() {
		boolean cond = true;
		List<ClienteServer> listaClintes = new ArrayList<ClienteServer>();
		List<String> contatos = new ArrayList<String>();
		contatos.add("teste4@email.com");
		contatos.add("teste6@email.com");
		contatos.add("teste1@email.com");
		contatos.add("teste8@email.com");
		req = "teste2@email.com" + "," + "123";
		if (socketCliente == null || socketCliente.isClosed()) {
			connect();
		}
		res = addresser("B", req);
		if (res.equalsIgnoreCase("Welcome")) {
			System.out.println("retornou welcome");
			String res = listManager();
			cliente = gson.fromJson(res, ClienteServer.class);
			System.out.println("retornou cliente: " + cliente);
			if (cliente.getEmail().equalsIgnoreCase("teste2@email.com")) {
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

//	@Test
	public void testeClienteControlAdd() {
		boolean cond = true;
		List<ClienteServer> listaClintes = new ArrayList<ClienteServer>();
		List<String> contatos = new ArrayList<String>();
		contatos.add("teste4@email.com");
		contatos.add("teste5@email.com");
		contatos.add("teste6@email.com");

		req = "teste3@email.com" + "," + "teste6@email.com";
		if (socketCliente == null || socketCliente.isClosed()) {
			connect();
		}
		res = addresser("D", req);
		if (res.equalsIgnoreCase("added")) {
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
		assertTrue(cond);
	}

//	@Test
	public void testeClienteControlRemove() {
		boolean cond = true;
		List<ClienteServer> listaClintes = new ArrayList<ClienteServer>();
		List<String> contatos = new ArrayList<String>();

		req = "teste4@email.com" + "," + "teste5@email.com";
		if (socketCliente == null || socketCliente.isClosed()) {
			connect();
		}
		res = addresser("E", req);
		if (res.equalsIgnoreCase("removed")) {
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
		assertTrue(cond);

	}

//	@Test
	public void testeClienteControlLogout() {
		req = "teste5@email.com";
		if (socketCliente == null || socketCliente.isClosed()) {
			connect();
		}
		res = addresser("C", req);
		assertEquals(res, "logged out");
	}

	public String addresser(String req, String response) {
		try {
			this.streamer.sendMessage(req);
			this.streamer.sendMessage(response);// response é o Json sendo enviado
			this.req = streamer.readMessage();
		} catch (IOException e) {
			System.err.println("ERRO ENVIO GSON REGISTRO " + e);
		}
		return this.req;
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
