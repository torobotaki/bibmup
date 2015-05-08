package bibmup;

public class BibLibToMindMup {

	static public void main(String[] args) throws Exception {
		Config config = new Config();

		if(args.length < 2 ){
			System.err.println("Usage: java " + BibTeXLib.class + " <Input file> <Output file> [<Config file>]");

			System.exit(-1);
		}

		if (args.length == 3) {
			config = new Config(args[2]);
		}
		BibTeXLib db = new BibTeXLib(args[0], config);
		db.moreSpecific();
		if (config.prune > 0) {
			db.prune(config.prune);
		}
		MupTree tree = new MupTree(config, db);
		tree.writeMindMup(args[1]);
		System.out.println("done");

	}


}
