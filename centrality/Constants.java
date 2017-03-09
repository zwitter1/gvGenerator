package centrality;


public interface Constants {

	final double PageRankFluctThreshold = 0.000001; //Page rank
    final double DistributionParameter = 0.85; //Page rank
    //final double LongerPathsThreshold = 1; //Threshold for adapted betweenness
    final int SampleSize = 1;
    final boolean isSample = false;
	public enum CentralityMetric {
	    CLOSENESS, PAGERANK, BETWEENNESS, ADAPTIVE_BETWEENNESS
	}
}
