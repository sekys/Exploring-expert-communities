package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.VectorWritable;

public class ParseClankyHandler extends PersonHandler {

	public static String VYSTUP_SPOLUAUTORY_TEXT = "data/spracovaneVzorkySXML/spoluatoryAkoText.txt";
	public static String VYSTUP_SPOLUAUTORY_DATA = "data/spracovaneVzorkySXML/autorov10";
	
	private ParseResult spoluAutory;

	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	private static Logger logger = Logger.getLogger(ParseClankyHandler.class);

	
	public ParseClankyHandler() {
		spoluAutory = new ParseResult();
		logger.info("Spustam parsovanie");
	}

	protected void endPublication() {
		int size = personsPerPublication.size();
		if (size < 2 || size != 30) {
			return;
		}

		for (String autorA : personsPerPublication) {
			for (String autorB : personsPerPublication) {
				int compare = autorA.compareTo(autorB);
				if (compare >= 0) {
					// -Preskoc rovnake mena
					// - A aj meno A ked je mensie ako B
					// Pretoze nemozme pridat rovnake data 2x.
					continue;
				}

				double ohodnotenie = OhodnotSpoluautorstvo();
				spoluAutory.addResult(autorA, autorB, ohodnotenie);
			}
		}
	}

	protected double OhodnotSpoluautorstvo() {
		/*
		 * typ clanku -> www 0.1, inproceeding 0.2 -> subjektivne rozdelenie
		 * podla nas pocet autorov -> 5.0, 8.0 .... alebo lepsie sa dohodneme ze
		 * pocet autorov je gausovo rozdelenie a tzv najoptimalnejsi pocet je
		 * ked ma 4 autorov rok vydania -> najnovsie clanky , rok 2014 maju 1.0
		 * 2013 maju 0.8 hodnotu pocet citatov -> cim viac tym lepsie, lienearne
		 * rozdelenie alebo logaritmicke ?
		 * 
		 * 
		 * Rozdieli medzi dimenaziamy riesi : Weighting the different dimensions
		 * solves this problem.
		 * 
		 * Mozme porovnat autora a editora !
		 */
		return 0.1;
	}

	public void endDocument() {
		logger.info("Koniec dokumentu");
		spoluAutory.vybudujZoznamUnikatnychMien();
		logger.info("ZOradene mena");

		// Konfiguracia zapisu
		Configuration conf = new Configuration();
		FileSystem fs;
		SequenceFile.Writer writer = null;
		try {
			fs = FileSystem.get(conf);
			Path path = new Path(VYSTUP_SPOLUAUTORY_DATA);
			writer = new SequenceFile.Writer(fs, conf, path, Text.class,
					VectorWritable.class);
			serializujData(writer);
			writer.close();
			logger.info("Data su zoserializovane");
			serializujAkoText();
			logger.info("Data su prevedene do textu");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void serializujAkoText() throws IOException {
		Writer output;
		File file = new File(VYSTUP_SPOLUAUTORY_TEXT);
		FileOutputStream is = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(is);
		output = new BufferedWriter(osw);
		
		for (String meno : spoluAutory.zoznamMien) {
			output.write(meno);
			output.write(": ");
			output.write(spoluAutory.get(meno).toString());
			output.write("\n");
			output.flush();
		}
		output.close();
	}
	
	private void serializujData(SequenceFile.Writer writer) throws IOException {
		// Spracovanie vlastnosti
		VectorWritable vec = new VectorWritable();
		RandomAccessSparseVector vektor;
		NamedVector namedVector;

		// Serializuj data
		for (String meno : spoluAutory.zoznamMien) {
			vektor = new RandomAccessSparseVector(Integer.MAX_VALUE, 20);
			serializujVektor(meno, vektor);
			namedVector = new NamedVector(vektor, meno);
			vec.set(namedVector);
			writer.append(new Text(namedVector.getName()), vec);
		}
	}

	private void serializujVektor(String meno, RandomAccessSparseVector vektor)
			throws IOException {
		List<Dvojica> dvojice = spoluAutory.get(meno);
		if (dvojice == null) {
			throw new IOException();
		}
		int index;
		for (Dvojica dvojica : dvojice) {
			index = spoluAutory.getIndex(dvojica.meno);
			if (index < 0) {
				throw new IOException();
			}
			vektor.set(index, dvojica.ohodnotenie);
		}
	}
}
