package bibmup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;


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
						//						Pattern.compile("stores.*store.*product").matcher(someString).find();

						if (Pattern.compile("[a-zA-Z]").matcher(s).find()) {
							s = makeTitle(s);
							ideas = entry.addParent(s, ideas);
						}
					}
				}
			}
		}
		entry.addContent(bibtext+"\n}");
		entries.add(entry);
		buffer.close();
		reader.close();
	}

	public static String capitalize(String s) {
		StringBuffer res = new StringBuffer();
		String[] strArr = s.split("\\s+");
		for (String str : strArr) {
			if (!str.matches("[A-Z]+")){
				char[] stringArray = str.trim().toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				str = new String(stringArray);
			}
			res.append(str).append(" ");
		}
		return res.toString();
	}

	public static String makeTitle(String s) {
		s = capitalize(s.replace("{", "").replace("}","").replaceAll("\\-", " ").replaceAll("[^a-zA-Z0-9\\s]", "").trim()).trim();
		return s;
	}


	public void moreSpecific(){
		for (BibTeXEntry entry:entries) {
			HashSet<MupObject> parents = new HashSet<MupObject>();
			parents.addAll(entry.parents);
			for (MupObject parent:parents) {
				// if entry.parents contains any children of parent
				if (entry.containsAnyAsParent(parent.children)) {
					entry.parents.remove(parent);
					parent.children.remove(entry);
					System.out.println("Removed parent "+parent.name+"  from "+entry.name+" (keeping only most specific category).");
				}
			}
		}

	}

	public void prune(Integer n){
		HashSet<MupObject> ideaValues = new HashSet<MupObject>();
		ideaValues.addAll(ideas.values());
		for (MupObject idea:ideaValues){
			HashSet<BibTeXEntry> childEntries = idea.getChildEntries();
			if (childEntries.size() <= n){
				for (MupObject parent:idea.parents){
					parent.addChildren(childEntries);
				}
				ideas.remove(idea.name);
				for (BibTeXEntry child:childEntries) {
					child.parents.remove(idea);
					idea.children.remove(child);
				}
				System.out.println("Removed category "+idea.name+" because we're pruning categories with equal or less than "+n+" entries");
			}
		}
	}


}
