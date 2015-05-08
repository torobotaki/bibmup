package bibmup;

public class BibLibToMindMup {

		static public void main(String[] args) throws Exception {
		
		            if(args.length < 2 ){
		                    System.err.println("Usage: java " + BibTeXLib.class + " <Input file> <Output file>?");
		
		                    System.exit(-1);
		            }
		
		           BibTeXLib db = new BibTeXLib(args[0], new Config());
		           MupTree tree = new MupTree(new Config(), db);
		           tree.writeMindMup(args[1]);
		           System.out.println("done");
		
		    }

		
}
