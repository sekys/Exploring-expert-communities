package parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class HistogramHandler extends PersonHandler {
	
	public static String VYSTUP = "data/histogramSpoluAutorov.txt";
	
	public int[] histogram;

	public HistogramHandler() {
		histogram = new int[125];

		for (int i = 0; i < histogram.length; i++) {
			histogram[i] = 0;
		}
	}

	protected void endPublication() {
		int pocet = personsPerPublication.size();
		if (pocet >= histogram.length) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		histogram[pocet]++;
	}

	public void endDocument() {
		try {
			Writer output;
			File file = new File(VYSTUP);
			FileOutputStream is = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(is);
			output = new BufferedWriter(osw);
			
			for(int i=0; i < histogram.length; i++) {
				int pocet = histogram[i];
				//if(pocet > 0) {
					output.write(Integer.toString(i) + ";" + Integer.toString(pocet) + "\n");	
				//}
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
