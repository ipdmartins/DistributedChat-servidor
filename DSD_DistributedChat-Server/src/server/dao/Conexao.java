package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author Igor
 */
public class Conexao {

	static Connection conn = null;

	public synchronized Connection getConnection() {
		String url = "jdbc:sqlite:db/ClienteServer.db";

		if (conn == null) {
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(url);
			} catch (ClassNotFoundException ex) {
				System.err.println("Conexao (class). Não foi possivel conectar no banco" + ex.getMessage());
			} catch (SQLException ex) {
				System.err.println("Conexao (sql). Não foi possivel conectar no banco" + ex.getMessage());
			}
		}
		return conn;
	}

	public ResultSet executaBusca(String sql) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			conn.close();
			return rs;
		} catch (SQLException e) {
			System.err.println("Conexao (sql). Não foi possivel conectar no banco" + e.getMessage());
			return null;
		}
	}

	public boolean desconectar() {

		try {
			if (this.conn.isClosed() == false) {
				this.conn.close();
				return true;
			}

		} catch (Exception e) {
			System.err.println("Erro ao desconectar" + e.getMessage());
		}
		return false;
	}

}
