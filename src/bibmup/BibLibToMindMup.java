package bibmup;

public class BibLibToMindMup {

		static public void main(String[] args) throws Exception {
		
		            if(args.length < 1 ){
		                    System.err.println("Usage: java " + BibTeXLib.class + " <Input file> <Output file>?");
		
		                    System.exit(-1);
		            }
		
		           BibTeXLib db = new BibTeXLib(args[0], new Config());
		           MupTree tree = new MupTree(new Config(), db);
		           tree.writeMindMup("/tmp/out.mup");
		           System.out.println("done");
		
		    }

		
}
