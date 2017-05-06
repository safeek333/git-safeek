package com.xsl.compare;

import java.util.LinkedList;

public class XSLElement {
	private LinkedList<String> lines;
	
	public XSLElement(){
		lines = new LinkedList<String>();
	}
	
	
	
	public LinkedList<String> getLines() {
		return lines;
	}



	public void setLines(LinkedList<String> lines) {
		this.lines = lines;
	}



	public void addLine(String line){
		lines.add(formatLine(line));
	}
	
	public boolean removeLine(String line){
		return lines.remove(line);
	}
	
	public String formatLine(String line){
		line = line.trim();
		return line;
	}



	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<lines.size();i++){
			sb.append(lines.get(i));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
}
