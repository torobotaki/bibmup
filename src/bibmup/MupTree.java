package bibmup;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

public class MupTree  {
	MupObject root = null;


	public MupTree(Config config, BibTeXLib db){
		String rootname = config.rootName;
		root = new MupObject(rootname);
		db.ideas.put(rootname, root);
		for (MupObject idea:db.ideas.values()) {
			if (idea.parents.size() ==0) {
				if (!idea.equals(root)) {
					idea.addParent(rootname, db.ideas);
				}
			}
		}
		String unsortedName = config.unsortedName;
		MupObject unsorted = new MupObject(unsortedName);

		int i = 0;
		for (BibTeXEntry entry:db.entries) {
			if (entry.parents.size() ==0){
				entry.addParent(unsortedName, db.ideas);
				i++;
			}
		}
		if (i>0) {
			db.ideas.put(unsortedName, unsorted);
			unsorted.addParent(rootname, db.ideas);
			System.out.println(i+" unsorted entries added to node "+unsortedName);
		}
	}

	public void writeMindMup(String filename) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		int num = writeNode(0, 0, 1, root, writer, 0);
		writer.close();
		System.out.println("Wrote "+num+" nodes");
	}

	//	Format from: https://github.com/mindmup/mapjs/wiki/Data-Format
	//		{
	//			formatVersion:2, /*numeric, only applicable to root idea*/
	//			id: _idea id_, /* numeric */
	//			title: _idea title_, /* string */
	//			attr : {  /* key-value map of idea attributes, optional */
	//			   style: { }, /* key-value map of style properties, optional */
	//			   collapsed: true/false /* optional */
	//			   attachment: { contentType: _content type_, content: _content_ }
	//			}
	//			ideas: { _rank_: {_sub idea_}, _rank2_: {_sub idea 2_} ... }, /* key-value map of subideas, optional */ 
	//			}
	//		

	public Integer writeNode(Integer rank, Integer tabs, Integer id, MupObject node, BufferedWriter writer, Integer depth) throws IOException{
		String colour = "";
		if (!(node instanceof BibTeXEntry)) {
			Random rand = new Random();
			float hue = rand.nextFloat();
			// Saturation between 0.1 and 0.3
			float saturation = (rand.nextInt(2000) + 1000) / 10000f;
			float luminance = 0.9f;
			Color color = Color.getHSBColor(hue, saturation, luminance);
			colour =  Integer.toHexString(color.getRGB()).substring(2);
		}
		String tabulation = "";
		for (int i=0; i< tabs;i++){
			tabulation+="  ";
		}

		if (rank == 0) {
			writer.write(tabulation+"{\n");

		}
		else if (rank ==1 ){ 
			writer.write("\n"+tabulation+"\""+rank+"\":  "+"{\n");

		}
		else {
			writer.write(",\n"+tabulation+"\""+rank+"\":  "+"{\n");
		}


		if(node.equals(root)){
			writer.write("\"formatVersion\": 2,\n");
		}
		writer.write(tabulation+" \"title\": \""+node.name+"\",\n");
		writer.write(tabulation+" \"id\": "+id+",\n");
		id++;
		writer.write(tabulation+" \"attr\": {\n");
		if (node.attachment.length()!=0){
			writer.write(tabulation+"   \"attachment\": {\n");
			writer.write(tabulation+"      \"contentType\": \"text/html\",\n");
			writer.write(tabulation+"      \"content\": "+quote(beautifyAttachment(node.attachment))+"\n");
			writer.write(tabulation+"    },\n");
		}
		//		  "style": {
		//	          "background": "#FFFF00"
		//	        },

		if (colour.length()==0) {
			writer.write(tabulation+"    \"style\": {},\n");
		}
		else {
			writer.write(tabulation+"    \"style\": {\n");
			writer.write(tabulation+"         \"background\": \"#"+colour+"\"\n},\n");

		}
		if(node.equals(root)){
			writer.write(tabulation+"    \"collapsed\": false\n");
		}
		else {
			writer.write(tabulation+"    \"collapsed\": true\n");
		}
		writer.write(tabulation+" },\n");
		writer.write(tabulation+" \"ideas\": {\n");
		Integer childNo = 1;
		tabs++;
		int rightORleft = 1;
		TreeSet<MupObject> tree = new TreeSet<MupObject>(new NodeComparator());
		tree.addAll(node.children);
		for (MupObject child:tree){
			id = writeNode(rightORleft*childNo, tabs, id, child, writer, depth+1);
			rightORleft *= -1;
			childNo++;
		}
		writer.write(tabulation+"    }\n");  //close ideas
		writer.write(tabulation+"}");
		return id;
	}


	class NodeComparator implements Comparator<MupObject>{

		@Override
		public int compare(MupObject o1, MupObject o2) {
			// TODO Auto-generated method stub
			return o1.name.compareTo(o2.name);
		}
		
	}
	
	public static String beautifyAttachment(String s){
		String result = "";
		String bib = quote(s);
		HashMap<String, String> contents = getBasicsFromBib(s);
		result+="<div><table>";
		ArrayList<String> ks = new ArrayList<String>();
		ks.addAll(contents.keySet());
		result+=tableThis("Reference", contents.get("reference"));
		ks.remove("reference");
		result+=tableThis("title", contents.get("title"));
		ks.remove("title");
		result+=tableThis("author", contents.get("author"));
		ks.remove("author");
		result+=tableThis("year", contents.get("year"));
		ks.remove("year");
		
		for (String key:ks){
			result+=tableThis(key, contents.get(key));
		}
		result +="</table></div><div><h2>BibTeX</h2><p>"+bib+"</p></div>";
		return result;
	}
	
	private static String tableThis(String key, String value) {
		if (value.endsWith(",")) {
			value = value.substring(0, value.length()-1);
		}
		String result="";
		result+="<tr>";
		result+="<td><b>"+key+"</b></td>";
		result+="<td>"+value+"</td>";
		result+="</tr>";
		return result;
	}
	
	public static HashMap<String, String> getBasicsFromBib(String s){
		HashMap<String, String> result = new HashMap<String, String>();
		String[] lines = s.split("\n");
		for (String line:lines){
			if (line.length() >1){
				if (line.startsWith("@")){
					String ref = line.split("\\{")[1].replace(",","");
					result.put("reference", ref);
				}
				else {
					String key = line.split("=")[0].trim();
					String value = line.split("=")[1].replaceAll("\\{|\\}", "").trim();
					result.put(key, value);
				}
			}
		}
		return result;
	}
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char         c = 0;
		int          i;
		int          len = string.length();
		StringBuilder sb = new StringBuilder(len + 4);
		String       t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				//                if (b == '<') {
				sb.append('\\');
				//                }
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("<br>");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("<br>");
				break;
			default:
				if (c < ' ') {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

}
