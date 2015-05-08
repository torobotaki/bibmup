package bibmup;


public class BibTeXEntry extends MupObject{
	
	public BibTeXEntry(String nm) {
		super(nm);
	}


	public void addContent(String c){
		attachment = c;
	}

	
}
