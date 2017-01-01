package org.cmg.jresp.exceptions;

public class MissingAttributeException extends Exception {

	private static final long serialVersionUID = 1L;

	public MissingAttributeException(String name) {
		super("Missing Attribute in Request:" + name);
	}

}
