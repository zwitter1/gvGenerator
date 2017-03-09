package centrality;

import java.util.ArrayList;

public class Centrality {

	DijkstraGeneric dijkstra;
	
	public Centrality(DijkstraGeneric dijkstra) {
		this.dijkstra = dijkstra;
	}
	
	public Centrality() {
	}

    //Compute betweenness scores for the given vertices
    public double[] runBetweenness(ArrayList<Node> vertices, boolean isAdaptive) {
    	
    	double[] count = new double[dijkstra.getVertices().size()];
    	Node start;
    	
    	for (int i=0; i<vertices.size(); i++) {		
    			
			start = vertices.get(i);
			dijkstra.run(start);
			
			double curCount[] = isAdaptive ? getBetwennessAdaptiveScores() : getBetwennessScores();
			for (int k=0; k<curCount.length; k++)
				count[k] += curCount[k];
    	    
    	}
    	//Normalize betweenness scores
		for (int k=0; k<count.length; k++)
			if (vertices.size() == dijkstra.getVertices().size())
				//Betweenness for all nodes
				count[k] /= ((count.length-1) * (count.length-2));
			else	
				//Betweenness for sample nodes
				count[k] /= (vertices.size() * (count.length-2));
		
    	return count;
    }		
    
    //Get the number of times a node is part of a shortest path
    private double[] getBetwennessScores() {
		
    	double[] totCount = new double[dijkstra.getVertices().size()];
    	double[] count = new double[dijkstra.getVertices().size()];
    	
    	ArrayList<LinkedList> paths = null;
    	for (Node n : dijkstra.getVertices()) {

    		//Get all shortest paths to node n
    		paths = dijkstra.getPredPaths().get(n);
    		
			if (paths == null)
				continue;
		
			for (LinkedList path: paths) {
				
				LinkedList curHop = path;
				while (curHop != null) {
					Node curNode = curHop.getCurNode();
					if (curHop.getNextHop() != null)
						count[curNode.getID()]++;
					curHop = curHop.getNextHop();
				}
			}
			for (int i=0; i<count.length; i++) {
				totCount[i] += count[i] / paths.size();
				count[i] = 0;
			}
    	}
		return totCount;
	}
    
    //Get the number of times a node is part of a shortest path
    private double[] getBetwennessAdaptiveScores() {
		
    	double[] totCount = new double[dijkstra.getVertices().size()];
    	double[] count = new double[dijkstra.getVertices().size()];
    	
    	ArrayList<LinkedList> paths = null;
    	for (Node n : dijkstra.getVertices()) {

    		//Get all shortest paths to node n
    		paths = dijkstra.getPredPaths().get(n);
    		double k;	//Shortest path length
    		double denominator = 0;
    		
			if (paths != null) {			
		
				k = Helper.getWeightedPathLength(paths.get(0), n); //

				for (LinkedList path: paths) {
					
					denominator += k/k;			//
					LinkedList curHop = path;
					while (curHop != null) {
						Node curNode = curHop.getCurNode();
						if (curHop.getNextHop() != null)
							count[curNode.getID()] += (k/k);
						curHop = curHop.getNextHop();
					}
				}
					
				//Consider also longer paths if any
				paths = dijkstra.getLongerPredPaths().get(n);
				double l = .0;	//Longer path length
				
				if (paths != null) {			
					
					for (LinkedList path: paths) {
						
						l = Helper.getWeightedPathLength(path, n);	//
						denominator += k/l;			//
						LinkedList curHop = path;
						while (curHop != null) {
							Node curNode = curHop.getCurNode();
							if (curHop.getNextHop() != null)
								count[curNode.getID()] += (k/l);
							curHop = curHop.getNextHop();
						}
					}
				}
				
				for (int i=0; i<count.length; i++) {
					totCount[i] += count[i] / denominator;
					count[i] = 0;
				}
			}
    	}
		return totCount;
	}
    
    public double[] getPageRankCentralityScoreAll(ArrayList<Node> nodesArray) {
    	
		double scores[] = new double[nodesArray.size()];
		double newScores[] = new double[nodesArray.size()];
		
		//Initialize probabilities for the nodes
		sharePageRankProb(scores, 1);
		
		double fluctRate = Double.MAX_VALUE;
		while (fluctRate > Constants.PageRankFluctThreshold) {
 
			for (Node n : nodesArray) {
				
				double totalEdgeScore = getTotalEdgeScore(n.getEdges());
				double curScore = scores[n.getID()];
				
				//If has outgoing edges, distribute the probability to the edges
				if (totalEdgeScore > 0) {
					
					double toEdges = curScore * Constants.DistributionParameter;
					double toAll = curScore * (1-Constants.DistributionParameter);
					
					//Distribute p to outgoing edges
					for (Edge e : n.getEdges()) 
						newScores[e.getNextHop().getID()] += toEdges * (e.getWeight()/totalEdgeScore);
					//Distribute p to all
					sharePageRankProb(newScores, toAll);
				}
				else {
					sharePageRankProb(newScores, curScore);
				}
				
			}
			
			//System.out.println(Helper.getFluctuationRate(scores, newScores));
			fluctRate = Helper.getFluctuationRate(scores, newScores);
			
			for (int i=0; i<scores.length; i++) {
				scores[i] = newScores[i];
				newScores[i] = 0;
			}
		}	
		
		System.out.println(getTotalPageRankProb(scores));
		
		return scores;
    }
    
	public double[] getPageRankCentralityScoreSample(ArrayList<Node> nodesArray, ArrayList<Node> samples) {
		
		double scores[] = new double[nodesArray.size()];
		double newScores[] = new double[nodesArray.size()];
		
		//Initialize probabilities for the nodes
		sharePageRankProb(scores, samples, 1);
		
		double fluctRate = Double.MAX_VALUE;
		while (fluctRate > Constants.PageRankFluctThreshold) {
			

			for (Node n : nodesArray) {
				
				double totalEdgeScore = getTotalEdgeScore(n.getEdges());
				double curScore = scores[n.getID()];
	
				//If has outgoing edges, distribute the probability to the edges
				if (totalEdgeScore > 0) {
					
					double toEdges = curScore * Constants.DistributionParameter;
					double toAll = curScore * (1-Constants.DistributionParameter) * (1-Constants.DistributionParameter);
					double toSamples = curScore * (1-Constants.DistributionParameter) * Constants.DistributionParameter;
					
					//Distribute p to outgoing edges
					for (Edge e : n.getEdges()) 
						newScores[e.getNextHop().getID()] += toEdges * (e.getWeight()/totalEdgeScore);
					//Distribute p to all
					sharePageRankProb(newScores, toAll);
					sharePageRankProb(newScores, samples, toSamples);
				}
				else {
					sharePageRankProb(newScores, curScore * (1-Constants.DistributionParameter));
					sharePageRankProb(newScores, samples, curScore * Constants.DistributionParameter);
				}
				
			}
			
			//System.out.println(Helper.getFluctuationRate(scores, newScores));
			fluctRate = Helper.getFluctuationRate(scores, newScores);
			
			for (int i=0; i<scores.length; i++) {
				scores[i] = newScores[i];
				newScores[i] = 0;
			}
		}
		
		System.out.println(getTotalPageRankProb(scores));
		
		return scores;
	}
	
	
	/*public double[] getPageRankCentralityScoreAll(ArrayList<Node> nodesArray) {

		double scores[] = new double[nodesArray.size()];
		double curScores[] = new double[nodesArray.size()];
		
		//Initialize probabilities for the nodes
		sharePageRankProb(scores, 1);
		sharePageRankProb(curScores, 1);
		
		for (int t=0; t<20; t++) {
			
			for (Node n : nodesArray) {
				
				double totalEdgeScore = getTotalEdgeScore(n.getEdges());
				double curScore = curScores[n.getID()];
				//If has outgoing edges, distribute the probability to the edges
				if (totalEdgeScore > 0) {
					
					double toEdges = curScore * Constants.DistributionParameter;
					double toAll = curScore * (1-Constants.DistributionParameter);

					//Update current page rank
					curScores[n.getID()] -= (curScore);
					
					//Distribute p to outgoing edges
					for (Edge e : n.getEdges()) 
						curScores[e.getNextHop().getID()] += toEdges * (e.getWeight()/totalEdgeScore);
					//Distribute p to all
					sharePageRankProb(curScores, toAll);
				}
				else {
					sharePageRankProb(curScores, curScore);
				}
				
			}
			
			double errRate = Helper.getFluctuationRate(scores, curScores);
			System.out.println(errRate);
			
			for (int i=0; i<scores.length; i++) {
				scores[i] = curScores[i];
				curScores[i] = 0;
			}
		}	
		
		//System.out.println(getTotalPageRankProb(nodesArray));
		
		for (int i=0; i<nodesArray.size(); i++)
			scores[i] = nodesArray.get(i).getPageRankScore();
		return scores;
	}*/
	
	/*public double[] getPageRankCentralityScoreAll(ArrayList<Node> nodesArray) {

		double scores[] = new double[nodesArray.size()];
		
		//Initialize probabilities for the nodes
		sharePageRankProb(nodesArray, 1);
		
		for (int t=0; t<20; t++) {
			for (int i=0; i<nodesArray.size(); i++) {
				
				Node n = nodesArray.get(i);
				double curProb = n.getPageRankScore();

				double totalEdgeScore = getTotalEdgeScore(n.getEdges());
				//If has outgoing edges, distribute the probability to the edges
				if (totalEdgeScore > 0) {
					for (Edge e : n.getEdges()) {
						
						Node nextHop = e.getNextHop();
						double weight = e.getWeight();
						
						double edgeProb = curProb * (weight/totalEdgeScore);
												
						nextHop.addToPageRankScore( edgeProb * Constants.DistributionParameter );
						sharePageRankProb(nodesArray, edgeProb * (1-Constants.DistributionParameter));
					}
				}
				else {
					sharePageRankProb(nodesArray, curProb);
				}
				//Update current pagerank
				n.setPageRankScore(n.getPageRankScore()-curProb);
			}
			
		}	
		System.out.println(getTotalPageRankProb(nodesArray));
		
		for (int i=0; i<nodesArray.size(); i++)
			scores[i] = nodesArray.get(i).getPageRankScore();
		return scores;
	}*/
	
	//Personalized page rank
	/*public double[] getPageRankCentralityScoreSample(ArrayList<Node> nodesArray, ArrayList<Node> samples) {
			
		double scores[] = new double[nodesArray.size()];			
			
		//Initialize probabilities for the nodes
		sharePageRankProb(samples, 1);
		
		for (int t=0; t<20; t++) {
			for (int i=0; i<nodesArray.size(); i++) {
	
				Node n = nodesArray.get(i);
				double curProb = n.getPageRankScore();

				double totalEdgeScore = getTotalEdgeScore(n.getEdges());
				//If has outgoing edges, distribute the probability to the edges
				if (totalEdgeScore > 0) {
					for (Edge e : n.getEdges()) {
						
						Node nextHop = e.getNextHop();
						double weight = e.getWeight();
						
						double edgeProb = curProb * (weight/totalEdgeScore);
													
						nextHop.addToPageRankScore( edgeProb * Constants.DistributionParameter );
						sharePageRankProb(nodesArray, edgeProb * (1-Constants.DistributionParameter) * (1-Constants.DistributionParameter));
						sharePageRankProb(samples, edgeProb * (1-Constants.DistributionParameter) * Constants.DistributionParameter);
					}
				}
				else {
					sharePageRankProb(nodesArray, curProb * (1-Constants.DistributionParameter));
					sharePageRankProb(samples, curProb * Constants.DistributionParameter);
				}
				//Update current pagerank
				n.setPageRankScore(n.getPageRankScore()-curProb);
			}		
		}
		
		System.out.println(getTotalPageRankProb(nodesArray));
		
		for (int i=0; i<nodesArray.size(); i++)
			scores[i] = nodesArray.get(i).getPageRankScore();
		return scores;
	}*/
	
	private static double getTotalPageRankProb(double[] scores) {
		
		double count = 0;
		for (Double d : scores)
			count += d;
		return count;
	}
	
	private static void sharePageRankProb(double[] scores, double p) {
		
		for (int i=0; i<scores.length; i++)
			scores[i] += (p+.0)/scores.length;
	}
	
	private static void sharePageRankProb(double[] scores, ArrayList<Node> samples, double p) {
		
		for (Node n:samples)
			scores[n.getID()] += (p+.0)/samples.size();
	}
	
	private static double getTotalEdgeScore(ArrayList<Edge> edges) {
		
		double score = 0;
		for (Edge e : edges)
			score += e.getWeight();
		return score;
	}
	
	public double[] getClosenessCentralityScoreSample(ArrayList<Node> nodesArray) {
		
		double scores[] = new double[nodesArray.size()];
		//Use Dijkstra
		Dijkstra dijkstra = new Dijkstra(nodesArray);
		
		int sampleIDs[] = new int[Constants.SampleSize];
		sampleIDs[0] = 1;
		
		scores = dijkstra.getAverageDistanceSample(sampleIDs);
		
		return scores;
	}
		
	public double[] getClosenessCentralityScoreAll(ArrayList<Node> nodesArray) {

		double scores[] = new double[nodesArray.size()];
		//Use Dijkstra
		Dijkstra dijkstra = new Dijkstra(nodesArray);
		
		for (int i=0; i<nodesArray.size(); i++) {

			double centralityScore = dijkstra.getAverageDistance(nodesArray.get(i));
			nodesArray.get(i).setCentralityScore(centralityScore);
			scores[i] = centralityScore;
		}
		
		return scores;
	}
}
