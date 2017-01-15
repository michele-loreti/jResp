package org.cmg.jresp.topology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.Gson;

public class SocketReceiver implements Runnable {

	/**
	 * 
	 */
	private ServerSocket ssocket;

	private Gson gson = RESPFactory.getGSon();

	private MessageReceiver receiver;

	/**
	 * @param socketPort
	 */
	public SocketReceiver(ServerSocket ssocket, MessageReceiver receiver) {
		this.ssocket = ssocket;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for connections at " + ssocket.getInetAddress().getCanonicalHostName() + ":"
						+ ssocket.getLocalPort());
				Socket s = ssocket.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				jRESPMessage msg = gson.fromJson(reader, jRESPMessage.class);
				receiver.receiveMessage(msg);
				reader.close();
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
