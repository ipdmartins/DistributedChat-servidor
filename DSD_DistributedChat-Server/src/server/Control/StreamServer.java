/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.Control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author ipdmartins
 */
public class StreamServer {

    private BufferedReader in;
    private PrintWriter out;
    private Socket socketStream;

    public void createStream(Socket socket) throws IOException {
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socketStream = socket;
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public String readMessage() throws SocketTimeoutException, IOException {
        return in.readLine();
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }
    
    public Socket getSocketStream() {
		return socketStream;
	}

	public void setSocketStream(Socket socketStream) {
		this.socketStream = socketStream;
	}

	public void closeStream() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar out/in do Stream " + e);
        }
    }

}
