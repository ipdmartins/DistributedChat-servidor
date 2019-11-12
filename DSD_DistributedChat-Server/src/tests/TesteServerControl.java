/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import org.junit.Test;
import com.google.gson.Gson;
import server.Control.ServerControl;
import server.Model.ClienteServer;
import server.dao.ClienteDao;
import server.dao.Conexao;
import static org.junit.Assert.*;
import java.sql.Connection;

/**
 *
 * @author ipdmartins
 */

public class TesteServerControl {

	private static ClienteServer cliente;
	private static String answer = "";
	private static Gson gson;
	private static ServerControl server;
	private static String result = "";

	private static Conexao conexao = new Conexao();
	private static Connection conn = null;
	private static ClienteDao dao = new ClienteDao();


	@Test
	public void testeViewRegister() {
		result = testeClienteControlRegister("nome1", "teste1@email.com", "1901", "123", "5600", "ipCli1", 
				"56001", "ipSer1");
		assertEquals(result, "stored");
	}

	public String testeClienteControlRegister(String nome, String email, String nasc, String pass, String porta,
			String ipCliente, String serverPort, String serverIP) {

		cliente = new ClienteServer();
		cliente.setNome(nome);
		cliente.setEmail(email);
		cliente.setAnoNasc(nasc);
		cliente.setSenha(pass);
		cliente.setPortaCliente(Integer.parseInt(porta));
		cliente.setIpCliente(ipCliente);
		cliente.setPortaServer(Integer.parseInt(serverPort));
		cliente.setIpServer(serverIP);
		cliente.setStatus("null");
		gson = new Gson();
		answer = gson.toJson(cliente);
		server = new ServerControl();
		return server.register(answer, 1);
	}

	public void testeControlClienteRegister() {

	}

//	@Test
//	public void testaConexao() {
//		conexao = new Conexao();
//		conn = null;
//		conn = conexao.getConnection();
//		assertNotNull(conn);
//		conexao.desconectar();
//	}
//
//	@Test
//	public void testaStoreDao() {
//		cliente = new ClienteServer();
//		dao = new ClienteDao();
//		cliente.setNome("Maria");
//		cliente.setEmail("email2@email.com");
//		cliente.setAnoNasc("2000");
//		cliente.setSenha("123");
//		cliente.setPortaCliente(56000);
//		cliente.setIpCliente("f6dsf56a4d");
//		cliente.setPortaServer(56002);
//		cliente.setIpServer("dfasdfafff");
//		cliente.setStatus("online");
//		boolean teste = false;
//		boolean teste2 = false;
//		teste = dao.store(cliente);
//		ClienteServer c = dao.consultarCliente("email2@email.com");
//
//		// teste2 = dao.update(cliente);
//
//		dao.desconectar();
//
//		assertTrue(teste);
//		// assertTrue(teste2);
//
//	}
//
//	@Test
//	public void testaConsultaDao() {
//		ClienteServer c = dao.consultarCliente("email2@email.com");
//		dao.desconectar();
//		assertNotNull(c);
//	}
//
//	@Test
//	public void testaConsultaListaDao() {
//		ClienteServer c = dao.consultarCliente("email2@email.com");
//		dao.desconectar();
//		assertNotNull(c);
//	}

//    @Test
//    public void testaConsultaUpdate() {
//    	cliente =new ClienteServer();
//    	boolean teste = false;
//    	dao = new ClienteDao();
//    	cliente.setEmail("email3@email.com");
//    	teste = dao.update(cliente);
//    	dao.desconectar();
//    	assertTrue(teste);
//    }

//    @Test
//    public void testaStore(){
//    	
//    	String json = "{\"nome\":\"Igor\",\"email\":\"email@email\",\"senha\":\"123\",\"anoNasc\":\"1980\",\"portaCliente\":56000,"
//    			+ "\"ipCliente\":\"131231651561\",\"portaServer\":56001,\"ipServer\":\"4941616\",\"status\":\"null\"}";
//    	
//        result = server.register(json, 1);
//        
//        assertEquals(result, "stored");
//        
//    }

}
