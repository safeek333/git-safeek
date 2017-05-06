package com.xsl.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Element {
	String type;
	String name;
	List<String> propName;
	List<String> propVal;
	
	public Element(){
		propName = new ArrayList<String>();
		propVal = new ArrayList<String>();
	}
	
	
	public static Element parse(String s) throws Exception{
		Element e = new Element();
		Pattern p = Pattern.compile("<xs:([^ ]+)\\s");
		Matcher m = p.matcher(s);
		boolean flag = false;
		if(m.find()){
			e.type = m.group(1);
			flag = true;
		}
		
		if(flag == true){
			p = Pattern.compile(" ([^=]+)=\\s*\"([^\"]+)\"");
			m = p.matcher(s);
			while(m.find()){
				e.propName.add(m.group(1));
				e.propVal.add(m.group(2));
				if(m.group(1).equals("name")){
					e.name = m.group(2);
				}                 
				if(e.type.equals("enumeration") && m.group(1).equals("value")){
					e.name =  m.group(2);
					e.propName.add("name");
					e.propVal.add(e.name);
				}
			}
			
		}
		
		
		
		return e;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((propName == null) ? 0 : propName.hashCode());
		result = prime * result + ((propVal == null) ? 0 : propVal.hashCode());
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
		Element other = (Element) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (propName == null) {
			if (other.propName != null)
				return false;
		} else if (!propName.equals(other.propName))
			return false;
		if (propVal == null) {
			if (other.propVal != null)
				return false;
		} else if (!propVal.equals(other.propVal))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Element [type=" + type + ", name=" + name + ", propName=" + propName + ", propVal=" + propVal + "]";
	}
	
	
	
}
