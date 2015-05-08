package bibmup;

public class BibLibToMindMup {

//	public static void main(String[] args) {
		static public void main(String[] args) throws Exception {
		
		            if(args.length < 1 ){
		                    System.err.println("Usage: java " + BibTeXLib.class + " <Input file>?");
		
		                    System.exit(-1);
		            }
		
		           BibTeXLib db = new BibTeXLib(args[0], new Config());
		           System.out.println("test");
		
		    }

}
