/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.Control;

import java.io.IOException;
import java.net.SocketException;

/**
 *
 * @author ipdmartins
 */
public class RequestManager extends Thread {

	private ServerControl serverControl;
    private StreamServer streamServer;
    private String response;
    private int idStream;

    public RequestManager(StreamServer streamServer, ServerControl serverControl, int idStream) {
    	this.serverControl = serverControl;
        this.streamServer = streamServer;
        this.response = "";
        this.idStream = idStream;
    }

    @Override
    public void run() {
        try {
            while (true) {
                response = streamServer.readMessage();
                if (!response.equalsIgnoreCase("")) {
                	switch(response) {
                	case "A":
                		response = streamServer.readMessage();
                		serverControl.register(response, idStream);
                		break;
                	case "B":
                		response = streamServer.readMessage();
                		serverControl.authenticateUser(response, idStream);
                		break;
                	case "C":
                		response = streamServer.readMessage();
                		serverControl.logout(response, idStream);
                		break;
                	case "D":
                		response = streamServer.readMessage();
                		serverControl.addContact(response, idStream);
                		break;
                	case "E":
                		response = streamServer.readMessage();
                		serverControl.removeContact(response, idStream);
                		break;
                	case "F":
                		response = streamServer.readMessage();
                		serverControl.setLiveClient(response);
                		break;
                	case "G":
                		response = streamServer.readMessage();
                		serverControl.validatePass(response, idStream);
                		break;
                	}
                	
                } else {
                    System.out.println("Resposta inválida");
                }
            }
        } catch (SocketException ex) {
            System.err.println("ERRO NO REQUESTMANAGER " + ex);
        } catch (IOException ex) {
            System.err.println("ERRO NO REQUESTMANAGER " + ex);
        }
    }

}
