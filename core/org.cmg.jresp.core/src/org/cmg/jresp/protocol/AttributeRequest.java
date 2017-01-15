package org.cmg.jresp.protocol;

import java.io.IOException;
import java.util.Arrays;

import org.cmg.jresp.topology.PointToPoint;

/**
 * This message is sent when a component needs to obtain the values of a given
 * set of attributes from a node.
 * 
 * @author Michele Loreti
 *
 */
public class AttributeRequest extends UnicastMessage {

	/**
	 * Names of requested attributes.
	 */
	private String[] attributes;

	/**
	 * Creates a new message.
	 * 
	 * @param source
	 *            address of the node originating the message
	 * @param session
	 *            an integer used to relate this message to a conversation
	 * @param target
	 *            name of the target node
	 * @param attributes
	 *            names of requested attributes
	 */
	public AttributeRequest(PointToPoint source, int session, String target, String[] attributes) {
		super(MessageType.ATTRIBUTE_REQUEST, source, session, target);
		this.attributes = attributes;
	}

	/**
	 * Returns the array containing the names of requested attributes.
	 * 
	 * @return the names of requested attributes.
	 */
	public String[] getAttributes() {
		return attributes;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return Arrays.deepEquals(attributes, ((AttributeRequest) obj).attributes);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(attributes);
	}

	@Override
	public String toString() {
		return getType() + "[" + super.toString() + "," + Arrays.toString(attributes) + "]";
	}

	@Override
	public void accept(MessageHandler messageHandler) throws IOException, InterruptedException {
		messageHandler.handle(this);
	}

}
