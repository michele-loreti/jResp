package org.cmg.resp.knowledge2;

public class ActualTemplateField implements TemplateField {
	private Object value;

	public ActualTemplateField(Object value) {
		this.value = value;
	}

	public boolean match(Object o) {
		if (this.value == o) {
			return true;
		} else {
			return (this.value != null) && (this.value.equals(o));
		}
	}

	public boolean isActual() {
		return true;
	}
	public String toString(){
		return value.toString();
	}
	
	public Object getValue() {
		return value;
	}
}
