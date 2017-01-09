package org.cmg.jresp.topology;

import org.cmg.jresp.protocol.jRESPMessage;

public interface MessageDispatcher {

	public abstract void addMessage(jRESPMessage msg);

	public abstract String getName();

}