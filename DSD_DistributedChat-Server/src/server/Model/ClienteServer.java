package server.Model;

public class ClienteServer {

	private int id;
	private String nome;
	private String email;
	private String anoNasc;
	private String senha;
	private int portaCliente;
	private String ipCliente;
	private int portaServer;
	private String ipServer;
	private String status;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPortaServer() {
		return portaServer;
	}

	public void setPortaServer(int portaServer) {
		this.portaServer = portaServer;
	}

	public String getIpServer() {
		return ipServer;
	}

	public void setIpServer(String ipServer) {
		this.ipServer = ipServer;
	}

	public String getIpCliente() {
		return ipCliente;
	}

	public void setIpCliente(String ipCliente) {
		this.ipCliente = ipCliente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String apelido) {
		this.nome = apelido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getAnoNasc() {
		return anoNasc;
	}

	public void setAnoNasc(String anoNasc) {
		this.anoNasc = anoNasc;
	}

	public int getPortaCliente() {
		return portaCliente;
	}

	public void setPortaCliente(int portaCliente) {
		this.portaCliente = portaCliente;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ClienteServer [nome=" + nome + ", email=" + email + ", senha=" + senha + ", anoNasc=" + anoNasc
				+ ", portaCliente=" + portaCliente + ", ipCliente=" + ipCliente + ", portaServer=" + portaServer
				+ ", ipServer=" + ipServer + ", status=" + status + "]";
	}
	
	
	

}