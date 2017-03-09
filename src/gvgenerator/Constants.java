/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvgenerator;

/**
 *
 * @author zach
 */
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
