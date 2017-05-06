package com.xsl.compare;

public class DataRecord {
	String type;
	String name;
	String existed_in;
	String difference;
	String leftText;
	String rightText;
	
	public String getLeftText() {
		return leftText;
	}
	public void setLeftText(String leftText) {
		this.leftText = leftText;
	}
	public String getRightText() {
		return rightText;
	}
	public void setRightText(String rightText) {
		this.rightText = rightText;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExisted_in() {
		return existed_in;
	}
	public void setExisted_in(String existed_in) {
		this.existed_in = existed_in;
	}
	public String getDifference() {
		return difference;
	}
	public void setDifference(String difference) {
		this.difference = difference;
	}
	@Override
	public String toString() {
		return type + "|" + name + "|" + existed_in + "|"+ difference + "|" + leftText.replace("\n", "#") + "|" + rightText.replace("\n", "#") + "|";
	}
	
	

}
