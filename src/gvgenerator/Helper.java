/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvgenerator;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import gvgenerator.Constants.CentralityMetric;
/**
 *
 * @author zach
 */
public class Helper {
    	//Reads a csv file into an integer matrix
	public static int[][] readMatrixFromCSV(String inputFile) {
		
		int matrix[][] = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            
            int count = br.readLine().split(",").length-1;
            matrix = new int[count][count];

            String line = null;
            for (int i=0; i<count; i++) {
                
            	line = br.readLine();
            	for (int j=0; j<count; j++)
            		if (i != j)
            			matrix[i][j] = Integer.parseInt( line.split(",")[j+1] );
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
	}
	
	public static int[][] readMatrixFromMTX(String inputFile, int length) {
		
		int matrix[][] = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            
            br.readLine();
            int count = length;
            matrix = new int[count][count];

            String line = null;
            while ((line=br.readLine()) != null) {
                
            	int source = Integer.parseInt( line.split(" ")[0] );
            	int destination = Integer.parseInt( line.split(" ")[1] );
            	int weight = Integer.parseInt( line.split(" ")[2] );
            	matrix[source][destination] = weight;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
	}
	
	//Reads the tnt log file into an integer matrix
	public static int[][] readMatrixFromTntLog(String inputFile, int count) {
		
		int matrix[][] = null;
		ArrayList<String> locations = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            
            matrix = new int[count][count];
            String line = null;
            int sId = -1, dId = -1;
            
            while ((line=br.readLine()) != null) {
            	
            	if (line.substring(0, 1).equals("F")) {
            		
            		String source = line.split(" ")[1];
            		String destination = line.split(" ")[3];
            		if (!locations.contains(source)) {
            			locations.add(source);
            		}
            		if (!locations.contains(destination)) {
            			locations.add(destination);
            		}
            		sId = locations.indexOf(source);
            		dId = locations.indexOf(destination);

            	}else if (line.substring(0, 1).equals("C")) {
            		
            		String s = line.split(" ")[5].split("-")[1];
            		s = s.substring(0, s.lastIndexOf(')'));
            		int numTrans = Integer.parseInt(s);
            		if (matrix[sId][dId] != 0)
            			System.out.println("non zero");
            		matrix[sId][dId] = numTrans;
            	}else
            		continue;
            }
            br.close();
            //System.out.println(locations.size());
            //for (String s:locations)
            //	System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
	}
	
	//Reads the tnt log file into an integer matrix
	public static int[][] readMatrixFromLogFile(byte[] content) {
		
		int matrix[][] = null;
		ArrayList<String> locations = new ArrayList<String>();
		InputStream is = new ByteArrayInputStream(content);
		int count = getLocations(content).size();
		
        try {
            //BufferedReader br = new BufferedReader(new FileReader(inputFile));
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            matrix = new int[count][count];
            String line = null;
            int sId = -1, dId = -1;
            
            while ((line=br.readLine()) != null) {
            	
            	if (line.substring(0, 1).equals("F")) {
            		
            		String source = line.split(" ")[1];
            		String destination = line.split(" ")[3];
            		if (!locations.contains(source)) {
            			locations.add(source);
            		}
            		if (!locations.contains(destination)) {
            			locations.add(destination);
            		}
            		sId = locations.indexOf(source);
            		dId = locations.indexOf(destination);

            	}else if (line.substring(0, 1).equals("C")) {
            		
            		String s = line.split(" ")[5].split("-")[1];
            		s = s.substring(0, s.lastIndexOf(')'));
            		int numTrans = Integer.parseInt(s);
            		if (matrix[sId][dId] != 0)
            			System.out.println("non zero");
            		matrix[sId][dId] = numTrans;
            	}else
            		continue;
            }
            br.close();
            //System.out.println(locations.size());
            //for (String s:locations)
            //	System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return matrix;
	}
	
	public static ArrayList<String> getLocations(byte[] content) {
		
		int matrix[][] = null;
		ArrayList<String> locations = new ArrayList<String>();
		InputStream is = new ByteArrayInputStream(content);
		int count = 100;
		
		
        try {
            //BufferedReader br = new BufferedReader(new FileReader(inputFile));
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            matrix = new int[count][count];
            String line = null;
            int sId = -1, dId = -1;
            
            while ((line=br.readLine()) != null) {
            	
            	if (line.substring(0, 1).equals("F")) {
            		
            		String source = line.split(" ")[1];
            		String destination = line.split(" ")[3];
            		if (!locations.contains(source)) {
            			locations.add(source);
            		}
            		if (!locations.contains(destination)) {
            			locations.add(destination);
            		}
            		sId = locations.indexOf(source);
            		dId = locations.indexOf(destination);

            	}else if (line.substring(0, 1).equals("C")) {
            		
            		String s = line.split(" ")[5].split("-")[1];
            		s = s.substring(0, s.lastIndexOf(')'));
            		int numTrans = Integer.parseInt(s);
            		if (matrix[sId][dId] != 0)
            			System.out.println("non zero");
            		matrix[sId][dId] = numTrans;
            	}else
            		continue;
            }
            br.close();
            //System.out.println(locations.size());
            //for (String s:locations)
            //	System.out.println(s);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations;
	}
	
	//Saves a matrix to the given output file in csv format
	public static void saveMatrixToCSVFile(int[][] matrix, String outputFile) {
		
        try {         
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++) {
                	if (i != j)
                		pw.print(matrix[i][j] + "\t");     
                	else
                		pw.print("-" + "\t");
                }
                pw.write("\n");
            }
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//Saves a matrix to the given output file in mtx format
	public static void saveMatrixToMTXFile(int matrix[][], String outputFile) {
		
        try {         
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix.length; j++)
                	if (matrix[i][j] > 0)
                		pw.println(i + " " + j + " " + matrix[i][j]);       
            }
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	//Converts a csv file to an mtx file
	public static void convertFromCSVtoMTX(String inputFile, String outputFile) {
		
		int matrix[][] = null;
        matrix = readMatrixFromCSV(inputFile);
        saveMatrixToMTXFile(matrix, outputFile);
	}

	public static double[] getCentralityScore(CentralityMetric measure, int[][] matrix) {

		if (measure == CentralityMetric.CLOSENESS) {
				return getClosenessCentralityScore(matrix);
		}
		else if (measure == CentralityMetric.PAGERANK) {
				return getPageRankCentralityScore(matrix);
		}
		else if (measure == CentralityMetric.BETWEENNESS) {
				return getBetweennessCentralityScore(matrix, false);
		}
		else if (measure == CentralityMetric.ADAPTIVE_BETWEENNESS) {
				return getBetweennessCentralityScore(matrix, true);
		}
		else return null;
	}
	
	private static double[] getBetweennessCentralityScore(int[][] matrix, boolean isAdaptive) {
		
		double scores[] = new double[matrix.length];
		ArrayList<Node> nodesArray = convertMatrixToGraph(matrix, true);
		
		DijkstraGeneric dijkstra;
		if (isAdaptive)	
			dijkstra = new DijkstraAll(nodesArray);
		else
			dijkstra = new DijkstraMultiple(nodesArray);
		
		if (Constants.isSample) {
			//Get random sample nodes
			ArrayList<Node> sampleNodes = new ArrayList<Node>();//getRandomSamples(nodesArray);
			//sampleNodes.add(nodesArray.get(7)); //h7n9
			//sampleNodes.add(nodesArray.get(2));  //h5n1
			//sampleNodes.add(nodesArray.get(5));	//h7pa china
			sampleNodes.add(nodesArray.get(8));	//h7pa italy
			
			scores = new Centrality(dijkstra).runBetweenness(sampleNodes, isAdaptive);
		}
		else
			scores = new Centrality(dijkstra).runBetweenness(dijkstra.getVertices(), isAdaptive);
		
		return scores;
	}
	
	private static double[] getPageRankCentralityScore(int[][] matrix) {
		
		double scores[] = new double[matrix.length];
		ArrayList<Node> nodesArray = convertMatrixToGraph(matrix, false);
		
		if (Constants.isSample) {
			
			//Get random sample nodes
			ArrayList<Node> sampleNodes = new ArrayList<Node>();//getRandomSamples(nodesArray);
			//sampleNodes.add(nodesArray.get(7)); //h7n9
			//sampleNodes.add(nodesArray.get(2));  //h5n1
			//sampleNodes.add(nodesArray.get(5));	//h7pa china
			sampleNodes.add(nodesArray.get(8));	//h7pa italy
			
			scores = new Centrality().getPageRankCentralityScoreSample(nodesArray, sampleNodes);
		}
		else
			scores = new Centrality().getPageRankCentralityScoreAll(nodesArray);
		
		return scores;
	}
	
	private static double[] getClosenessCentralityScore(int[][] matrix) {
		
		double scores[] = new double[matrix.length];
		ArrayList<Node> nodesArray = convertMatrixToGraph(matrix, true);
		
		if (Constants.isSample) {
			
			//Get random sample nodes
			ArrayList<Node> sampleNodes = getRandomSamples(nodesArray);
			//scores = new domain.Centrality().getClosenessCentralityScoreSample(nodesArray, sampleNodes);
			scores = new Centrality().getClosenessCentralityScoreSample(sampleNodes);
		}
		else
			scores = new Centrality().getClosenessCentralityScoreAll(nodesArray);
		
		return scores;
	}
	
	//Returns the length of a given path with weighted edges
	public static double getWeightedPathLength(LinkedList path, Node vertice) {
		
		double weightedLength = 0.0;
		
		if (path != null) {			
				
			LinkedList curHop = path;
			Node destination = vertice;
			while (curHop != null) {
				Node source = curHop.getCurNode();
				double weight = 0;
				for (Edge edge : source.getEdges()) {
					
					if (edge.getNextHop().equals(destination)) {
						weight = edge.getWeight();
						break;
					}
				}
				if (weight > 0)
					weightedLength += weight;
				else {
					System.err.println("Weight error");
					System.exit(0);
				}
				curHop = curHop.getNextHop();
				destination = source;
			}	
		}	
		return weightedLength;
	}
	
	//Get random sample nodes
	private static ArrayList<Node> getRandomSamples(ArrayList<Node> vertices) {
		
		ArrayList<Node> sampleNodes = new ArrayList();
		Random rand = new Random();
		int index;
		for (int i=0; i<Constants.SampleSize; i++) {
			
			index = rand.nextInt(Constants.SampleSize);
			//make sure that this value was not selected before
			while (sampleNodes.contains(vertices.get(index)))
				index = rand.nextInt(Constants.SampleSize);
			sampleNodes.add(vertices.get(index));
		}
		return sampleNodes;
	}

	//Converts a matrix into a node array.
	private static ArrayList<Node> convertMatrixToGraph(int[][] matrix, boolean isInverse) {

		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i=0; i<matrix.length; i++) {
			
			Node node = new Node(i);
			nodes.add(node);
		}
		
		for (int i=0; i<matrix.length; i++) {

			for (int j=0; j<matrix.length; j++) {

				if (matrix[i][j] > 0) {
					
					Edge e;
					if (isInverse)	//ie. betweenness
						e = new Edge(nodes.get(j), 1.0/matrix[i][j]);
					else			//ie. pagerank
						e = new Edge(nodes.get(j), matrix[i][j]);

					nodes.get(i).addEdge(e);
				}
			}
		}
		return nodes;
	}

	//Checks if the scores are converged
	public static double getFluctuationRate(double[] scores, double[] curScores) {

		double diff = 0;
		
		if (scores.length != curScores.length) {
			System.err.println("Array lengths are not equal");
			System.exit(0);
		}
		
		for (int i=0; i<scores.length; i++)
			diff += (scores[i]-curScores[i])*(scores[i]-curScores[i]);
		
		return Math.sqrt(diff);
	}
}
