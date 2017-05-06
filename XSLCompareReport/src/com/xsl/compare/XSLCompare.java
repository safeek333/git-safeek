package com.xsl.compare;

import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class XSLCompare {
	static File filePath = new File("D:\\compare");
	static File LEFT, RIGHT;
	static LinkedHashMap<XSLKey, XSLElement> leftMap = new LinkedHashMap<XSLKey, XSLElement>();
	static LinkedHashMap<XSLKey, XSLElement> rightMap = new LinkedHashMap<XSLKey, XSLElement>();
	public static void main(String[] args) throws Exception {
		System.out.println(sortFiles(filePath.toPath()));
		
		if(LEFT != null && RIGHT != null){
			leftMap = genMap(LEFT);
			
			rightMap = genMap(RIGHT);
			System.out.println(leftMap);
			System.out.println(rightMap);
			
			List<DataRecord> result = compare();
			for(DataRecord dr : result){
				System.out.println(dr);
			}
		}
	}
	
	public static LinkedHashMap<XSLKey, XSLElement> genMap(File file) throws Exception{
		LinkedHashMap<XSLKey, XSLElement> map = new LinkedHashMap<XSLKey, XSLElement>();
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		String status = " ";
		XSLElementType type = null;
		XSLKey key = null;
		XSLElement element = null;
		while((line = br.readLine())!=null){
			line = line.trim();
			if(line.startsWith("<xs:complexType ")){
				status = "CS";
				type = XSLElementType.ComplexType;
			}else if(line.startsWith("</xs:complexType")){
				status = "CE";
			}else if(line.startsWith("<xs:simpleType ")){
				status = "SS";
				type = XSLElementType.SimpleType;
			}else if(line.startsWith("</xs:simpleType")){
				status = "SE";
			}else{
				//status = " ";
			}
			
			switch(status){
				case "CS":
					if(key == null){
						String l = line;
						l = l.replace("<xs:complexType ", "");
						l = l.replace("name=", "");
						l = l.replace("\"", "");
						l = l.replace(">", "");
						key = new XSLKey(type, l);
						element = new XSLElement();
						element.addLine(line);
					}else{
						element.addLine(line);
					}
				break;
				case "CE":
					element.addLine(line);
					map.put(key, element);
					key = null;
					element = null;
					status = " ";
				break;
				case "SS":
					if(key == null){
						String l = line;
						l = l.replace("<xs:simpleType ", "");
						l = l.replace("name=", "");
						l = l.replace("\"", "");
						l = l.replace(">", "");
						key = new XSLKey(type, l);
						element = new XSLElement();
						element.addLine(line);
					}else{
						element.addLine(line);
					}
				break;
				case "SE":
					element.addLine(line);
					map.put(key, element);
					key = null;
					element = null;
					status = " ";
				break;
			}

		}
		return map;
	}
	
	public static List<Path> sortFiles(Path dir) throws Exception{
		List<Path> files = new ArrayList<>();
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for(Path p : stream) {
		        files.add(p);
		    }
		}

		Collections.sort(files, new Comparator<Path>() {
		    public int compare(Path o1, Path o2) {
		        try {
		            return Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));
		        } catch (IOException e) {
		            // handle exception
		        }
		        return 0;
		    }
		});
		
		if(files.size()>1){
			LEFT = new File(files.get(0).toString());
			RIGHT = new File(files.get(1).toString());
		}
		return files;
	}
	
	
	public static String getDifference(XSLElement le, XSLElement re){
		StringBuffer sb = new StringBuffer();
		
		if(le.getLines().get(0).contains("AddressType2Code")){
			System.out.println("---");
		}
		
		//Exactly Matched
		boolean flag = false;
		if(le.getLines().size() == re.getLines().size()){
			flag = true;
			for(int i=0;i<le.getLines().size();i++){
				for(int j=0;j<re.getLines().size();j++){
					if(i==j && !le.getLines().get(i).equals(re.getLines().get(j))){
						flag = false;
						break;
					}
				}
				if(!flag) break;
			}
		}
		if(flag) sb.append("Exactly Matched.");
		
		if(sb.indexOf("Exactly Matched.") == -1){
			flag = false;
			if(le.getLines().size() == re.getLines().size()){
				flag = true;
				for(int i=0;i<le.getLines().size();i++){
					int cnt = 0;
					for(int j=0;j<re.getLines().size();j++){
						if(le.getLines().get(i).equals(re.getLines().get(j))){
							cnt++;
						}
					}
					if(cnt == 0){ flag=false; break;}
				}
				if(flag) sb.append("All Matched But Order Changed.");
				else sb.append("Modification Detected;");
			}
		}
		
		//Sequence/choice added/deleted
		String[][] table = { {"<xs:sequence>","</xs:sequence>"}, 
				{"<xs:choice>","</xs:choice>"},
				{"<xs:restriction", "</xs:restriction"} ,
				{"<xs:simpleContent", "</xs:simpleContent"},
				{"<xs:extension", "</xs:extension"},
			};
		for(int i=0;i<table.length;i++){
			String eStart = table[i][0];
			String eEnd = table[i][1];
			if(le.getLines().contains(eStart) && le.getLines().contains(eEnd)){
				if(!re.getLines().contains(eStart) && !re.getLines().contains(eEnd)){
					sb.append(eStart.replace("<xs:", "").replace(">", "")+" Added;");
				}
			}
			
			if(re.getLines().contains(eStart) && re.getLines().contains(eEnd)){
				if(!le.getLines().contains(eStart) && !le.getLines().contains(eEnd)){
					sb.append(eStart.replace("<xs:", "").replace(">", "")+" Deleted;");
				}
			}
		}
		

		if(le.getLines().size() != re.getLines().size()){
			String[] eles = {"<xs:element", "<xs:enumeration", "<xs:fractionDigits", "<xs:totalDigits", "<xs:minInclusive", "<xs:attribute", "<xs:pattern","<xs:minLength","<xs:maxLength","<xs:restriction","<xs:any"};
			
			for(int k=0;k<eles.length;k++){
				String currEle = eles[k];
				int leleCnt = 0;
				int releCnt = 0;
				for(int i=0;i<le.getLines().size();i++){
					if(le.getLines().get(i).startsWith(currEle)) leleCnt++;
				}
				for(int j=0;j<re.getLines().size();j++){
					if(re.getLines().get(j).startsWith(currEle)) releCnt++;
				}
				
				if(leleCnt != releCnt){
					if(leleCnt > releCnt){
						if(leleCnt-releCnt == 1){
							sb.append("Single '"+currEle+"' Added;");
						}else{
							sb.append("Multiple '"+currEle+"' Added;");
						}
					}else{
						if(releCnt-leleCnt == 1){
							sb.append("Single '"+currEle+"' Deleted;");
						}else{
							sb.append("Multiple '"+currEle+"' Deleted;");
						}
					}
				}
			}
		}
		

		if(le.getLines().size() == re.getLines().size()){
			String[] eles = {"<xs:element","<xs:enumeration"};
			for(int k=0;k<eles.length;k++){
				
				String currEle = eles[k];
				for(int i=0;i<le.getLines().size();i++){
					try {
						Element lObj = Element.parse(le.getLines().get(i));
						Element rObj = Element.parse(re.getLines().get(i));
						
						if(lObj.name!=null && rObj.name != null && !lObj.name.equals(rObj.name)){
							sb.append(currEle.replace("<xs:", "") +"'"+lObj.name+"' Modified by '"+rObj.name+"'");
						}
						
					} catch (Exception e) {
						sb.append("Look at "+le.getLines().get(i) +" vs. "+re.getLines().get(i));
					}
				}
				
				
			}
		}

		
		String[] eles = {"<xs:element", "<xs:enumeration"};
		
		for(int l=0;l<eles.length;l++){
			boolean expFlag = false;

			List<Element> lee = new ArrayList<Element>();
			List<Element> ree = new ArrayList<Element>();

			for(int i=0;i<le.getLines().size();i++){
				if(le.getLines().get(i).startsWith(eles[l])){
					try{
						lee.add(Element.parse(le.getLines().get(i)));
					}catch(Exception e){
						expFlag = true;
					}
				}
			}
			for(int i=0;i<re.getLines().size();i++){
				if(re.getLines().get(i).startsWith(eles[l])){
					try{
						ree.add(Element.parse(re.getLines().get(i)));
					}catch(Exception e){
						expFlag = true;
					}
				}
			}
			
			if(!expFlag){
				for(int i=0;i<lee.size();i++){
					for(int j=0;j<ree.size();j++){
						Element fe = lee.get(i);
						Element se = ree.get(j);
						if(fe.name!=null && se.name!=null && fe.name.equals(se.name)){
							if(fe.propName.size() == se.propName.size()){
								List<String> lstName = new ArrayList<String>();
								List<String> lstValName = new ArrayList<String>();
								for(int a=0;a<fe.propName.size();a++){
									if(!fe.propName.get(a).equals(se.propName.get(a))){
										lstName.add(fe.propName.get(a));
									}
									if(!fe.propVal.get(a).equals(se.propVal.get(a))){
										lstValName.add(fe.propName.get(a)+"=(v1="+fe.propVal.get(a)+" != v2="+se.propVal.get(a)+")");
									}
								}
								if(!lstName.isEmpty()) sb.append("Property Name "+lstName+" Modified;");
								if(!lstValName.isEmpty()) sb.append("Property Value "+lstValName+" Modified;");
							}else{
								if(fe.propName.size() > se.propName.size()){
									if(fe.propName.size() - se.propName.size() == 1){
										sb.append("Element type <"+fe.type+"> and name '"+fe.name+"' Single property Added;");
									}else{
										sb.append("Element type <"+fe.type+"> and name '"+fe.name+"' Multiple property Added;");
									}
								}else{
									if(fe.propName.size() - se.propName.size() == -1){
										sb.append("Element type <"+fe.type+"> and name '"+fe.name+"' Single property Deleted;");
									}else{
										sb.append("Element type <"+fe.type+"> and name '"+fe.name+"' Multiple property Added;");
									}
								}
							}
						}
					}
				}
				
			}else{
				sb.append("Syntax Seems Wrong;");
			}
		}

		
		
		
		return sb.toString();
	}
	
	public static List<DataRecord> merge(List<DataRecord> r1, List<DataRecord> r2){
		for(int i=0;i<r2.size();i++){
			if(r2.get(i).getExisted_in() != null){
				if(r1.size()>i) r1.add(i,r2.get(i));
				else r1.add(r2.get(i));
			}
		}
		return r1;
	}
	
	public static List<DataRecord> compare(){
		List<DataRecord> r = new LinkedList<DataRecord>();
		Iterator<XSLKey> it = leftMap.keySet().iterator();
		while(it.hasNext()){
			DataRecord rec = new DataRecord();
			XSLKey key = it.next();
			rec.setType(key.getType().name());
			rec.setName(key.getName());
			rec.setDifference("");
			rec.setLeftText("");
			rec.setRightText("");
			if(rightMap.containsKey(key)){
				rec.setExisted_in("BOTH");
				rec.setDifference(getDifference(leftMap.get(key), rightMap.get(key)));
				rec.setLeftText(leftMap.get(key).toString());
				rec.setRightText(rightMap.get(key).toString());
			}else{
				rec.setExisted_in(LEFT.getName());
				rec.setLeftText(leftMap.get(key).toString());
			}
			
			r.add(rec);
		}
		
		List<DataRecord> r2 = new LinkedList<DataRecord>();
		it = rightMap.keySet().iterator();
		while(it.hasNext()){
			DataRecord rec = new DataRecord();
			XSLKey key = it.next();
			rec.setType(key.getType().name());
			rec.setName(key.getName());
			rec.setDifference("");
			rec.setLeftText("");
			rec.setRightText("");
			if(leftMap.containsKey(key)){
				//rec.setExisted_in("BOTH");
				//rec.setDifference(getDifference(leftMap.get(key), rightMap.get(key)));
			}else{
				rec.setExisted_in(RIGHT.getName());
				rec.setRightText(rightMap.get(key).toString());
			}
			r2.add(rec);
		}

		
		return merge(r,r2);
	}

}
