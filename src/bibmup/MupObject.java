package bibmup;

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




}
