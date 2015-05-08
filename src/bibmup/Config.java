package bibmup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Config {
	public String rootIgnore ="";
	public ArrayList<String> flatKeys = new ArrayList<String>();
	public ArrayList<String> hierchicalKeys = new ArrayList<String>();
	public Integer prune = 0;
	public String rootName = "";
	public String unsortedName = "";
	
	
	public Config(){
		rootIgnore ="PhD";
		flatKeys.add("keywords");
		flatKeys.add("mendeley-tags");
		hierchicalKeys.add("mendeley-groups");
		prune = 0;
		rootName = "Bibliography";
		unsortedName = "Unsorted";
	}

	public Config(String filename) throws Exception {
		BufferedReader buffer = new BufferedReader(new FileReader(filename));
		String line = "";
		while ((line = buffer.readLine()) != null) {
			if (line.startsWith("rootIgnore")) {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					rootIgnore = parts[1].trim();
				}
			}
			else if(line.startsWith("prune"))  {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					prune = Integer.valueOf(parts[1].trim());
				}
			}
			else if(line.startsWith("unsortedName")) {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					unsortedName = parts[1].trim();
				}
			}
			else if(line.startsWith("rootName")) {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					rootName = parts[1].trim();
				}
			}
			else if(line.startsWith("flatKeys")) {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					String[] keys = parts[1].split(",");
					for (String s:keys) {
						addFlatKey(s.trim());
					}
				}
			}
			else if(line.startsWith("hierarchicalKeys")) {
				String[] parts = line.split("=");
				if (parts.length != 2) {
					buffer.close();
					throw (new Exception("problem with config line: "+line));
				}
				else {
					String[] keys = parts[1].split(",");
					for (String s:keys) {
						addHierarchicalKey(s.trim());
					}
				}
			}
			
		}
		buffer.close();
	}
	
	public void addFlatKey(String s) {
		flatKeys.add(s);
	}
	
	public void addHierarchicalKey(String s){
		hierchicalKeys.add(s);
	}
	

}
