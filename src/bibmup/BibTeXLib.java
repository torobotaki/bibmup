package bibmup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class BibTeXLib {
	HashSet<BibTeXEntry> entries = new HashSet<BibTeXEntry>();
	HashMap<String, MupObject> ideas = new HashMap<String, MupObject>();

	public BibTeXLib(String filename, Config config) throws IOException {
		File input = new File(filename);
		FileReader reader = new FileReader(input);
		BufferedReader buffer = new BufferedReader(reader);
		String line ="";
		String bibtext = "";
		String name = "";
		BibTeXEntry entry = null;
		while ((line = buffer.readLine()) != null) {
			if (line.startsWith("@")) {
				if (entry!=null){
					entry.addContent(bibtext+"\n}");
					entries.add(entry);
					bibtext = "";
					name = "";
				}
				bibtext += "\n"+line;
				String nameline = line.split("\\{")[1];
				String[] nameparts =  nameline.split(",");
				if (nameparts.length >0 ) {
					name = nameparts[0];
					name = name.replace("_", "");
				}
				else {
					name = "noref";
				}
				entry = new BibTeXEntry(name);
			}
			else {
				bibtext += "\n"+line;
			}
			for (String hierKey: config.hierchicalKeys) {
				if (line.startsWith(hierKey)) {
					String thegroups = line.split("=")[1];
					String[] groups = thegroups.split(",");
					for (String g:groups) {
						String[] split = g.split("\\/");
						String prev = split[0];
						prev = makeTitle(prev);
						for (String s:split){
							s = makeTitle(s);
							if (!s.equals("PhD")) {
								ideas = entry.addParent(s, ideas);
								MupObject idea = ideas.get(s);
								if (!prev.equals("PhD")) {
									ideas = idea.addParent(prev, ideas);
								}
							}
							prev = s;
						}
					}

				}
			}
			for (String flatKey:config.flatKeys) {
				if (line.startsWith(flatKey)) {
					String keywords = line.split("=")[1];
					String[] kwords = keywords.split(",");
					for (String s:kwords) {
						s = makeTitle(s);
						ideas = entry.addParent(s, ideas);
					}
				}
			}

		}
		buffer.close();
		reader.close();
	}

	public static String capitalize(String s) {
		StringBuffer res = new StringBuffer();
		String[] strArr = s.split(" ");
		for (String str : strArr) {
			char[] stringArray = str.trim().toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			str = new String(stringArray);

			res.append(str).append(" ");
		}
		return res.toString();
	}

	public static String makeTitle(String s) {
		s = capitalize(s.replace("{", "").replace("}","").trim()).trim();
		return s;
	}





}
