package dataMining;

import java.awt.Graphics;
import java.awt.Graphics2D;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.clustering.display.DisplayClustering;

public class DisplayFuzzyKMeans extends DisplayClustering {

	private static Logger logger = Logger.getLogger(DisplayFuzzyKMeans.class);

	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	DisplayFuzzyKMeans() {
		initialize();
		this.setTitle("Fuzzy k-Means Clusters");
	}

	// Override the paint() method
	@Override
	public void paint(Graphics g) {
		plotSampleData((Graphics2D) g);
		plotClusters((Graphics2D) g);
	}

	public static void main(String[] args) throws Exception {
		Path output = new Path(ProcessFuzzyKMeans.VYSTUP);
		loadClustersWritable(output);
		new DisplayFuzzyKMeans();
	}
}
