package dataMining;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.clustering.display.DisplayClustering;
import org.apache.mahout.clustering.fuzzykmeans.FuzzyKMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.ManhattanDistanceMeasure;

import parser.ParseClankyHandler;

public class ProcessFuzzyKMeans extends DisplayClustering {

	private static Logger logger = Logger.getLogger(ProcessFuzzyKMeans.class);

	public static String VYSTUP = "data/dataPoClusteringu";
	
	static {
		PropertyConfigurator.configure("log4j.properties");
	}
	
	ProcessFuzzyKMeans() {
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
		Path samples = new Path(ParseClankyHandler.VYSTUP_SPOLUAUTORY_DATA);
		Path output = new Path(VYSTUP);
		Path clustersIn = new Path(output, "random-seeds");
		Configuration conf = new Configuration();

		HadoopUtil.delete(conf, output);
		// HadoopUtil.delete(conf, samples);
		RandomUtils.useTestSeed();

		// DisplayClustering.generateSamples();
		// writeSampleData(samples); // toto seserializuje data cize on si ich
		// potom sam deserializuje

		int maxIterations = 10;
		float threshold = 0.01F;
		float m = 1.1F;
		DistanceMeasure measure = new ManhattanDistanceMeasure(); // EuclideanDistanceMeasure
		
		logger.info("preparing");
		RandomSeedGenerator.buildRandom(conf, samples, clustersIn, 3, measure);
		logger.info("bulding random seed end");
		FuzzyKMeansDriver.run(samples, clustersIn, output, threshold,
				maxIterations, m, true, true, threshold, true);
		logger.info("clustering end");
	}
}
