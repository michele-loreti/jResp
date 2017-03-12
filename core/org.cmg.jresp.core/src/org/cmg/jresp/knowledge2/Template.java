package org.cmg.jresp.knowledge2;

public class Template {
	private TemplateField[] fields;
	private boolean containsActual;

	public Template(TemplateField... fields) {
		this.fields = fields;
		containsActual = false;
		checkContainsActual();
	}

	protected void checkContainsActual(){
		int n= fields.length;
		int i=0;
		while(!containsActual && i<n-1){
			if(fields[i].isActual()){
				containsActual = true;
			}
			i++;
		}
	}
	public boolean match(Object[] obj) {
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != this.fields) {
				return false;
			}
		}
		return true;
	}

	public TemplateField get(int i) {
		return fields[i];
	}

	public int lenght() {
		return fields.length;
	}
	public boolean getContainsActual(){
		return containsActual;
	}
	public String toString(){
		String a="";
		for(TemplateField field: fields){
			a=a+field.toString();
		}
		return a;
	}
	public int hashCode() {
		int i=0;
		for(TemplateField field: fields){
			i=i+field.hashCode();
		}
		return i;
	}
	public boolean equals(Object obj) {
		if (obj instanceof Template) {
			Template t = (Template) obj;
			for (int i = 0; i < t.lenght(); i++) {
				if (!t.get(i).equals(fields[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	protected Template getFormalTemplate() {
		TemplateField[] field = new TemplateField[fields.length];
		for (int i = 0; i < fields.length; i++) {
			TemplateField tf = fields[i];
			if (tf.isActual()) {
				ActualTemplateField actual = (ActualTemplateField) tf;
				tf = new FormalTemplateField(actual.getValue().getClass());
			}
			field[i] = tf;
		}
		return new Template(field);
	}
}
