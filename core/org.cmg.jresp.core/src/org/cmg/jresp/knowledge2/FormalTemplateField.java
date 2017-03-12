package org.cmg.jresp.knowledge2;

public class FormalTemplateField implements TemplateField {
	private Class<?> type;
//	private String varName;

	public FormalTemplateField(Class<?> type) {
		this.type = type;
//		this.varName = type.toString();
	}

//	public FormalTemplateField(Class<?> type, String name) {
//		this.type = type;
//		this.varName = name;
//	}

	public boolean match(Object o) {
		return (o == null) || (type.isInstance(o));
	}

	public boolean isActual() {
		return false;
	}
	public Class<?> getType(){
		return type;
	}
	public static void main(String[] args) throws InterruptedException {
		FormalTemplateField formal = new FormalTemplateField(Integer.class);
		int i = 0;
		System.out.println(formal.match(i));
	}
	public String toString (){
		return type.toString();
	}
	public boolean equals(Object obj) {
		if(obj instanceof FormalTemplateField){
			FormalTemplateField tf = (FormalTemplateField) obj;
			return type.equals(tf.getType());
		}
		return false;
	}
	public int hashCode() {
		return type.hashCode();
	}
}
