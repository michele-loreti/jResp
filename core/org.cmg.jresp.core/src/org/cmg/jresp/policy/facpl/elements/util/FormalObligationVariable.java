package org.cmg.jresp.policy.facpl.elements.util;

public class FormalObligationVariable<T> {

	private String varName;
	private T value;
	private Class<T> type;

	public FormalObligationVariable(String name) {
		this.varName = name;
	}

	public String getVarName() {
		return varName;
	}

	public Class<T> getFormalFieldType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

}
