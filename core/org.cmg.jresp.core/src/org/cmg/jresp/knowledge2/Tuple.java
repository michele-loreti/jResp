package org.cmg.jresp.knowledge2;

public class Tuple {
	private Object fields[];

	public Tuple(Object... fields) {
		this.fields = fields;
	}

	public int getLength() {
		return fields.length;
	}

	public Object get(int i) {
		return fields[i];
	}

	public Class<?> getType(int i) {
		return fields[i].getClass();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple t = (Tuple) obj;
			for (int i = 0; i < t.getLength(); i++) {
				if (!t.get(i).equals(fields[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public <T> T get(int i, Class<T> c) {
		Object o = get(i);
		if (o == null) {
			return null;
		}
		if (c.isInstance(o)) {
			return c.cast(o);
		}
		throw new ClassCastException();
	}

	public String toString() {
		String prova = "[";
		for (Object field : fields) {
			prova = prova + " " + field.toString();
		}
		return prova + "]";
	}

	public Template getFormalTemplate() {
		int size = fields.length;
		FormalTemplateField[] formalFields = new FormalTemplateField[size];
		for (int i = 0; i < size; i++) {
			FormalTemplateField element = new FormalTemplateField(fields[i].getClass());
			formalFields[i] = element;
		}
		return new Template(formalFields);
	}
}
