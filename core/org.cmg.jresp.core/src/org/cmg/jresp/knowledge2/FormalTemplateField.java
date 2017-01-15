package org.cmg.jresp.knowledge2;

public class FormalTemplateField implements TemplateField {
	private Class<?> type;
	private String varName;

	public FormalTemplateField(Class<?> type) {
		this.type = type;
	}

	public FormalTemplateField(Class<?> type, String name) {
		this.type = type;
		this.varName = name;
	}

	public boolean match(Object o) {
		return (o == null) || (type.isInstance(o));
	}

	public boolean isActual() {
		return false;
	}

	public static void main(String[] args) throws InterruptedException {
		FormalTemplateField formal = new FormalTemplateField(Integer.class);
		int i =0;
		System.out.println(formal.match(i));
	}
	
}
