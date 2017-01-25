package org.cmg.jresp.knowledge2;

public class Template {
	private TemplateField[] fields;

	public Template(TemplateField... fields) {
		this.fields = fields;
	}

	public boolean match(Object[] obj) {
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != this.fields) {
				return false;
			}
		}
		return true;
	}

	public Object get(int i) {
		return fields[i];
	}

	public int lenght() {
		return fields.length;
	}
}
