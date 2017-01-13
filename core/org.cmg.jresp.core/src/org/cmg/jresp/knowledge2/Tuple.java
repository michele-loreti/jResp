package org.cmg.resp.knowledge2;

import org.cmg.resp.policy.facpl.function.comparison.Equal;

public class Tuple {
	private Object fields[];
	public Tuple(Object... fields) {
		this.fields = fields;
	}

	public int length() {
		return fields.length;
	}

	public Object get(int i) {
		return fields[i];
	}
	public boolean equals(Object obj){
		if(obj instanceof Tuple){
			Tuple t=(Tuple)obj;
			for (int i=0; i<t.length(); i++){
				if(!t.get(i).equals(fields[i])){
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
	public String toString(){
		String prova = "[";
		for (Object field: fields){
			prova = prova +" "+ field.toString();
		}
		return prova+"]";
	}

}
