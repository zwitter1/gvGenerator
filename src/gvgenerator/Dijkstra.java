package gvgenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    public static final int INFINITE_DISTANCE = Integer.MAX_VALUE;
    private static final int INITIAL_CAPACITY = 150;
    private final Comparator<Node> shortestDistanceComparator = new Comparator<Node>()
    {
            public int compare(Node left, Node right)
            {
                double result = getShortestDistance(left) - getShortestDistance(right);
                return (int) ((result == 0) ? left.compareTo(right) : result);
            }
    };
    
    private final Routes map;
    ArrayList<Node> vertices;
    private final PriorityQueue<Node> unsettledNodes = new PriorityQueue<Node>(INITIAL_CAPACITY, shortestDistanceComparator);
    private final Set<Node> settledNodes = new HashSet<Node>();
    private final Map<Node, Double> shortestDistances = new HashMap<Node, Double>();
    private final Map<Node, Node> predecessors = new HashMap<Node, Node>();
    
    public Dijkstra(ArrayList<Node> actorsArray) {
		
    	this.vertices = actorsArray;
    	
    	//Determine the routes between the nodes
    	Routes routes = new Routes(actorsArray.size());
    	
    	//Use edges to add the routes
    	for (int i = 0; i < actorsArray.size(); i++) {
    		
    		Node n = actorsArray.get(i);
    		
    		for (Edge edge: n.getEdges()) {
    			Node nextHop = edge.getNextHop();
    			double weight = edge.getWeight();
    			routes.addDirectRoute(n, nextHop, 1.0/weight); 
    		}
    	}
    	
    	this.map = routes;
	}
    
    private void init(Node start)
    {
        settledNodes.clear();
        unsettledNodes.clear();
        
        shortestDistances.clear();
        predecessors.clear();
        
        // add source
        setShortestDistance(start, 0);
        unsettledNodes.add(start);
    }
    
    //For a given source node, get the average distance to the rest of the network
    //Find the shortest path between source-dest first
    public double getAverageDistance(Node source) {
    	
    	double totalDistance = 0;
    	
    	//Get the shortest path for each destination
    	for (Node destination : this.vertices) {

    		if (source != destination) {
    			double cost = getCostofSP(source, destination);
    			if (cost == Integer.MAX_VALUE)
    				totalDistance = 0;//Integer.MAX_VALUE;
    			else
    				totalDistance += cost;
    		}
    	}
    	//double avgDistance = totalDistance/(this.vertices.size()-1.0);
    	return 1.0/totalDistance;
    	
    	//return 1/(avgCost/(this.vertices.size()-1));
    	//return (this.vertices.size()-1)/totalDistance;
    }
    
    //For the given source nodes (samples), get the min distance to destinations
    //Find the shortest path between source-dest first
    public double[] getAverageDistanceSample(int[] sampleIDs) {
    	
    	double distances[] = new double[this.vertices.size()];
    	
    	//Get the shortest path for each destination
    	for (int i=0; i<sampleIDs.length; i++) {
    		
    		Node source = this.vertices.get(sampleIDs[i]);

    		//Get the shortest path for each destination
        	for (int j=0; j<this.vertices.size(); j++) {
        		
        		Node destination = this.vertices.get(j);
        		if (source != destination) {
        			double cost = getCostofSP(source, destination);
        			if (cost == Integer.MAX_VALUE)
        				;//distances[j] = 0;//Integer.MAX_VALUE;
        			else
        				distances[j] += 1/cost;
        		}
        	}
    	}
    	//double avgDistance = totalDistance/(this.vertices.size()-1.0);
    	//return 1.0/totalDistance;
    	return distances;
    	
    	//return 1/(avgCost/(this.vertices.size()-1));
    	//return (this.vertices.size()-1)/totalDistance;
    }
    
    //Checks whether a given id is in the list
    public boolean isInList(int[] list, int id) {
    	
    	for (int i=0; i<list.length; i++)
    		if (list[i] == id)
    			return true;
    	return false;
    }
    
	//Return next hop for the given source and destination nodes
    public double getCostofSP(Node start, Node destination)
    {
    	double cost = 0;
        init(start);
        
        // the current node
        Node u;
        
        // extract the node with the shortest distance
        while ((u = unsettledNodes.poll()) != null)
        {
            assert !isSettled(u);
            
            // stop if reached destination 
            if (u == destination) break;
            
            settledNodes.add(u);
            
            checkNeighbors(u);
        }
        
        //Get the cost for the destination through shortest path
    	cost = getShortestDistance(destination);
    	return cost;
    }

    
    //Returns the next hop to reach destination for all of the nodes
    public void run(Node destination)
    {
    	Node nextHop;
    	
    	for (Node source : this.vertices) {
    		nextHop = run(source, destination);
    		if (nextHop != null)
    			source.setNextHop(nextHop);
    	}
    	
    	//Set the Sroute for the nodes
    	ArrayList<Node> sroute;
    	for (Node g : this.vertices) {
    		
    		sroute = findSroute(g);
    		g.setSRoute(sroute);
    	}
    }
    
    //
    private ArrayList<Node> findSroute(Node g) {
		
    	ArrayList<Node> sroute = new ArrayList<Node>();
    	Node nextHop;
    	
    	while ((nextHop = g.getNextHop()) != null) {
    		
    		Node newG = new Node(nextHop.getID());
    		newG.setX(nextHop.getX());
    		newG.setY(nextHop.getY());
    		sroute.add(newG);
    		
    		g = getVertice(nextHop.getID());
    	}
    	
		return sroute;
	}
    
    private Node getVertice(int id) {
    	
    	for (Node g: this.vertices) {
    		if (g.getID() == id)
    			return g;
    	}
    	return null;
    }

	//Return next hop for the given source and destination nodes
    public Node run(Node start, Node destination)
    {
        init(start);
        
        // the current node
        Node u;
        
        // extract the node with the shortest distance
        while ((u = unsettledNodes.poll()) != null)
        {
            assert !isSettled(u);
            
            // stop if reached destination 
            if (u == destination) break;
            
            settledNodes.add(u);
            
            checkNeighbors(u);
        }
        
        //Get the next hop for destination
        return getNextHop(start, destination);
    }

    //If v is reachable from u, then u is a predecessor of v and v is a successor of u
    private void checkNeighbors(Node u)
    {
        for (Node v : map.getDestinations(vertices, u))
        {
            if (isSettled(v))
            	continue;
            
            double shortDist = getShortestDistance(u) + map.getDistance(u, v);
            
            if (shortDist < getShortestDistance(v))
            {
                setShortestDistance(v, shortDist);
                setPredecessor(v, u);
            }
        }        
    }

    public Node getNextHop(Node source, Node destination)
    {
    	Node predecessor, nextHop = null;
    	predecessor = getPredecessor(destination);
    	if (predecessor == null)	//This is sink node
    		return nextHop;
    	
    	if (predecessor.equals(source))	//This is one hop neighbor of sink node
    		return destination;
    	
    	while ((predecessor != null) && (predecessor != source)) {
    		nextHop = predecessor;
    		predecessor = getPredecessor(predecessor);
    	}
    	return nextHop;
    }
    
    private boolean isSettled(Node v)
    {
        return settledNodes.contains(v);
    }
 
    public double getShortestDistance(Node vertice)
    {
        Double d = shortestDistances.get(vertice);
        return (d == null) ? INFINITE_DISTANCE : d;
    }
    
    private void setShortestDistance(Node vertice, double distance)
    {
        unsettledNodes.remove(vertice);
        shortestDistances.put(vertice, distance);
		unsettledNodes.add(vertice);        
    }
    
    public Node getPredecessor(Node vertice)
    {
        return predecessors.get(vertice);
    }
    
    private void setPredecessor(Node a, Node b)
    {
        predecessors.put(a, b);
    }
    
    private class Routes
    {
    	private final double[][] distances;
    	
    	Routes(int numNodes)
    	{
    		distances = new double[numNodes][numNodes];
    	}
    	
    	public void addDirectRoute(Node start, Node end, double distance)
    	{
    		distances[start.getID()][end.getID()] = distance;
    	}
    	
    	public double getDistance(Node start, Node end)
    	{
    		return distances[start.getID()][end.getID()];
    	}
    	
    	public List<Node> getDestinations(ArrayList<Node> vertices, Node vertice)
    	{
    		List<Node> list = new ArrayList<Node>();
    		
    		for (int i = 0; i < distances.length; i++)
    		{
    			if (distances[vertice.getID()][i] > 0)
    				list.add( vertices.get(i) );
    		}
    		return list;
    	}

    	public List<Node> getPredecessors(Node[] vertices, Node vertice)
    	{
    		List<Node> list = new ArrayList<Node>();
    		
    		for (int i = 0; i < distances.length; i++)
    		{
    			if (distances[i][vertice.getID()] > 0)
    				list.add( vertices[i] );
    		}
    		return list;
    	}
    	
    	public Routes getInverse()
    	{
    		Routes transposed = new Routes(distances.length);
    		
    		for (int i = 0; i < distances.length; i++){
    			for (int j = 0; j < distances.length; j++){
    				transposed.distances[i][j] = distances[j][i];
    			}
    		}
    		return transposed;
    	}
    }
}

