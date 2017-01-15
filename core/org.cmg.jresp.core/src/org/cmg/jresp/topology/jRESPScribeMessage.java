package org.cmg.jresp.topology;

import org.cmg.jresp.protocol.jRESPMessage;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class jRESPScribeMessage implements Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Id from;
	protected Id to;
	protected jRESPMessage message;

	public jRESPScribeMessage(Id from, Id to, jRESPMessage message) {
		this.from = from;
		this.to = to;
		this.message = message;
	}

	@Override
	public int getPriority() {
		return Message.LOW_PRIORITY;
	}

	/**
	 * @return the serialversionuid
	 */
	public long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the from
	 */
	public Id getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public Id getTo() {
		return to;
	}

	/**
	 * @return the message
	 */
	public jRESPMessage getMessage() {
		return message;
	}

}
