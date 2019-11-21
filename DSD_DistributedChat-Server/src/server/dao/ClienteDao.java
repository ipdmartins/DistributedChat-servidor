/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import server.Model.ClienteServer;

/**
 *
 * @author 95875662034
 */
public class ClienteDao {

    private Conexao con;
    private PreparedStatement stm = null;
    private ResultSet rs = null;
    
    public ClienteDao() {
    	this.con = new Conexao();
    }

    public synchronized boolean store(ClienteServer v) {
        try {
            String sql = "insert into ClienteServer (nome, email, senha, anoNasc, portaCliente, ipCliente, portaServer, ipServer, status)"
                    + " values (?,?,?,?,?,?,?,?,?)";
            stm = con.getConnection().prepareStatement(sql);
            stm.setString(1, v.getNome());
            stm.setString(2, v.getEmail());
            stm.setString(3, v.getSenha());
            stm.setString(4, v.getAnoNasc());
            stm.setInt(5, v.getPortaCliente());
            stm.setString(6, v.getIpCliente());
            stm.setInt(7, v.getPortaServer());
            stm.setString(8, v.getIpServer());
            stm.setString(9, v.getStatus());
            stm.execute();
        } catch (SQLException ex) {
            System.err.println("ClienteDao (store). Não foi possível conectar no banco " + ex.getMessage());
            return false;
        }
        return true;
    }
    
    public synchronized ClienteServer consultarCliente(String id) {
    	ClienteServer cliente = new ClienteServer();
        PreparedStatement stm;
        String sql = "select * from ClienteServer where email = \""+id+"\"";
        try {
            stm = con.getConnection().prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
            	cliente.setId(rs.getInt("id"));
            	cliente.setNome(rs.getString("nome"));
            	cliente.setEmail(rs.getString("email"));
            	cliente.setSenha(rs.getString("senha"));
            	cliente.setAnoNasc(rs.getString("anoNasc"));
            	cliente.setPortaCliente(rs.getInt("portaCliente"));
               	cliente.setIpCliente(rs.getString("ipCliente"));
            	cliente.setPortaServer(rs.getInt("portaServer"));
            	cliente.setIpServer(rs.getString("ipServer"));
            	cliente.setStatus(rs.getString("status"));
            }else {
            	return null;
            }
        } catch (SQLException ex) {
        	System.err.println("ERRO CONSULTA CLIENTES POR ID NO DB " + ex);
        }
        return cliente;
    }
    
    public boolean update(ClienteServer c){
        String sql = "UPDATE ClienteServer set nome=?,anoNasc=?,portaCliente=?, ipCliente=?, portaServer=?, ipServer=?, status=? where email= \""+c.getEmail()+"\"";
        boolean retorno = false;
        try {
        	PreparedStatement stm = con.getConnection().prepareStatement(sql);
            stm.setString(1, c.getNome());
            stm.setString(2, c.getAnoNasc());
            stm.setInt(3, c.getPortaCliente());
            stm.setString(4, c.getIpCliente());
            stm.setInt(5, c.getPortaServer());
            stm.setString(6, c.getIpServer());
            stm.setString(7, c.getStatus());
            if (stm.executeUpdate() > 0) {
                retorno = true;
            }
        } catch (SQLException ex) {
        	System.err.println("ERRO UPDATE CLIENTES NO DB " + ex);
        }
        return retorno;
    }
    
    public ArrayList<ClienteServer> consultar(int opt, int idBusca) {
    	String sql = "";
        ArrayList<ClienteServer> clientes = new ArrayList<ClienteServer>();
        ClienteServer cliente;
        PreparedStatement stm;
        
        if(opt == 1) {
        	sql = "select * from ClienteServer order by id asc";
        }else if(opt == 2) {
        	sql = "select * from Contatos inner join ClienteServer on Contatos.idContato = "
        			+ "ClienteServer.id where Contatos.idCliente = \""+idBusca+"\"";
        }
        try {
            stm = con.getConnection().prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
            	cliente = new ClienteServer();
            	cliente.setId(rs.getInt("id"));
            	cliente.setNome(rs.getString("nome"));
            	cliente.setEmail(rs.getString("email"));
            	cliente.setSenha(rs.getString("senha"));
            	cliente.setAnoNasc(rs.getString("anoNasc"));
            	cliente.setPortaCliente(rs.getInt("portaCliente"));
               	cliente.setIpCliente(rs.getString("ipCliente"));
            	cliente.setPortaServer(rs.getInt("portaServer"));
            	cliente.setIpServer(rs.getString("ipServer"));
            	cliente.setStatus(rs.getString("status"));
            	clientes.add(cliente);
            }
        } catch (SQLException ex) {
        	System.err.println("ERRO CONSULTA CLIENTES DB " + ex);
        }
        return clientes;
    }
    
   public boolean storeContact(int idCliente, int idContato) {									
       try {
           String sql = "insert into Contatos (idCliente, idContato) values (?,?)";
           stm = con.getConnection().prepareStatement(sql);
           stm.setInt(1, idCliente);
           stm.setInt(2, idContato);
           stm.execute();
       } catch (SQLException ex) {
           System.err.println("Erro no ClienteDao storeContact " + ex.getMessage());
           return false;
       }
       return true;
    }

   public boolean removeContact(int idCliente, int idContato) {
	   try {
           String sql = "delete from Contatos where idCliente = ? and idContato = ?";
           stm = con.getConnection().prepareStatement(sql);
           stm.setInt(1, idCliente);
           stm.setInt(2, idContato);
           if (stm.executeUpdate() > 0) {
               return true;
           }
       } catch (SQLException ex) {
           System.err.println("Erro no ClienteDao removeContact " + ex.getMessage());
           return false;
       }
	   return true;
   }

    public void desconectar() {
    	con.desconectar();
    	
    }
    
    

}
