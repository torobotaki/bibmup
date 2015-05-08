package bibmup;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;


public class MupObject {
	HashSet<MupObject> parents = new HashSet<MupObject>();
	HashSet<MupObject> children = new HashSet<MupObject>();
	String name = "";
	String attachment = "";


	public MupObject(String nm) {
		name = nm;
	}


	public HashMap<String, MupObject> addParent(String parentName,HashMap<String, MupObject> list) {
		if (!list.containsKey(parentName)){
			MupObject parent = new MupObject(parentName);
			list.put(parentName, parent);
		}
		MupObject parent =list.get(parentName);
		parents.add(parent);
		parent.addChild(this);
		return list;
	}

	public void addChild(MupObject child) {
		children.add(child);
	}
	public void addChildren(HashSet<BibTeXEntry> childEntries) {
		children.addAll(childEntries);
	}

	public boolean isParentOf(MupObject p){
		return (parents.contains(p));
	}
	
	public boolean isChildOf(MupObject c){
		return (children.contains(c));
	}

	public boolean containsAnyAsParent(Collection<MupObject> col) {
		for (MupObject o:col) {
			if (parents.contains(o)){
				return true;
			}
		}
		return false;
	}

	
	public HashSet<BibTeXEntry> getChildEntries(){
		HashSet<BibTeXEntry> childEntries = new HashSet<BibTeXEntry>();
		for (MupObject child:children){
			if ((child instanceof BibTeXEntry)) {
				childEntries.add((BibTeXEntry)child);
			}
		}
		return childEntries;
	}
}
