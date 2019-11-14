/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente.View;

import server.Control.ServerControl;
import server.Model.ClienteServer;
import server.dao.ClienteDao;

/**
 *
 * @author ipdmartins
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
    	ServerControl server = new ServerControl();
    	
    	
//    	ClienteDao dao = new ClienteDao();
//    	cliente.setNome("Maria");
//    	cliente.setEmail("email@email.com");
//    	cliente.setAnoNasc("2000");
//    	cliente.setSenha("123");
//    	cliente.setPortaCliente(56000);
//    	cliente.setIpCliente("f6dsf56a4d");
//    	cliente.setPortaServer(56002);
//    	cliente.setIpServer("dfasdfafff");
//    	cliente.setStatus("online");
//    	if(dao.store(cliente)) {
//    		System.out.println("GRAVOU");
//    	}
//    	System.out.println(dao.consultarCliente("email@email.com").toString());
//    	
//    	cliente.setAnoNasc("2001");
//    	ClienteDao daoo = new ClienteDao();
//    	if(daoo.update(cliente)) {
//    		System.out.println("ATUALIZOU");
//    	}
    	
    	/*CLASSE CONEXAO
    	Conexao conexao = new Conexao();
    	Connection conn = null;
    	conn = conexao.getConnection();
    	if(conn != null) {
    		System.out.println("CONECTOU");
    	}else{
    		System.out.println("FUDEU");
    	}
    	if(conexao.desconectar()) {
    		System.out.println("DESCONECTOU");
    	}else{
    		System.out.println("FUDEU");	
    	}
    	*/
    	

    }
    
}
