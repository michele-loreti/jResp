package org.cmg.jresp.topology;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import org.cmg.jresp.RESPFactory;
import org.cmg.jresp.protocol.jRESPMessage;

import com.google.gson.Gson;

public class DatagramReceiver implements Runnable {

	private Gson gson = RESPFactory.getGSon();

	private MulticastSocket msocket;

	private MessageReceiver receiver;

	/**
	 * @param socketPort
	 */
	public DatagramReceiver(MulticastSocket msocket, MessageReceiver receiver) {
		this.msocket = msocket;
		this.receiver = receiver;
	}

	@Override
	public void run() {
		while (true) {
			try {
				DatagramPacket p = new DatagramPacket(new byte[5000], 5000);
				msocket.receive(p);
				String str = new String(p.getData(), p.getOffset(), p.getLength());
				jRESPMessage msg = gson.fromJson(str, jRESPMessage.class);
				receiver.receiveMessage(msg);
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
