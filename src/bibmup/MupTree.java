package bibmup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
		db.ideas.put(unsortedName, unsorted);
		unsorted.addParent(rootname, db.ideas);
		int i = 0;
		for (BibTeXEntry entry:db.entries) {
			if (entry.parents.size() ==0){
				entry.addParent(unsortedName, db.ideas);
				i++;
			}
		}
		System.out.println(i+" unsorted entries added to node "+unsortedName);

	}

	public void writeMindMup(String filename) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		int num = writeNode(0, 0, 1, root, writer);
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

	public Integer writeNode(Integer rank, Integer tabs, Integer id, MupObject node, BufferedWriter writer) throws IOException{
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
			writer.write(tabulation+"      \"content\": "+quote(node.attachment)+"\n");
			writer.write(tabulation+"    },\n");
		}
		writer.write(tabulation+"    \"style\": {}\n");
		writer.write(tabulation+"    \"colapsed\": \"true\"\n");
		writer.write(tabulation+" },\n");
		writer.write(tabulation+" \"ideas\": {\n");
		Integer childNo = 1;
		tabs++;
		int rightORleft = 1;
		for (MupObject child:node.children){
			id = writeNode(rightORleft*childNo, tabs, id, child, writer);
			rightORleft *= -1;
			childNo++;
		}
		writer.write(tabulation+"    }\n");  //close ideas
		writer.write(tabulation+"}");
		return id;
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
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
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
