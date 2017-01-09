package org.cmg.jresp.policy;

public class RequestAttributeName {
	private String idAttribute;
	private String category;

	public RequestAttributeName(String category, String s) {
		this.category = category;
		this.idAttribute = s;
	}

	public String getIDAttribute() {
		return idAttribute;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public boolean equals(Object arg) {
		if (arg instanceof RequestAttributeName) {
			if (this.category.equals(((RequestAttributeName) arg).getCategory())) {
				if (this.idAttribute.equals(((RequestAttributeName) arg).getIDAttribute())) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return this.category + "/" + this.idAttribute;
	}

}
