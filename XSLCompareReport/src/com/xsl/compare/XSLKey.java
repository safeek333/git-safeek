package com.xsl.compare;

enum XSLElementType {ComplexType, SimpleType}
public class XSLKey {
	XSLElementType type;
	String name;
	int index;
	
	public XSLKey(XSLElementType type, String name, int index){
		this.type = type;
		this.name = name;
		this.index = index;
	}
	
	public XSLKey(XSLElementType type, String name){
		this.type = type;
		this.name = name;
		this.index = 0;
	}
	
	public XSLElementType getType() {
		return type;
	}

	public void setType(XSLElementType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XSLKey other = (XSLKey) obj;
		if (index != other.index)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.type).append("|").append(this.name).append("|").append(this.index);
		return sb.toString();
	}
}
