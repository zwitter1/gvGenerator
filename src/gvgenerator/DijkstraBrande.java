package gvgenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DijkstraBrande implements DijkstraGeneric{

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
    private ArrayList<Node> vertices;
    private final PriorityQueue<Node> unsettledNodes = new PriorityQueue<Node>(INITIAL_CAPACITY, shortestDistanceComparator);
    private final Set<Node> settledNodes = new HashSet<Node>();
    private final Map<Node, Double> shortestDistances = new HashMap<Node, Double>();
    private final Map<Node, Node> predecessors = new HashMap<Node, Node>();
    private final Map<Node, Integer> numSP = new HashMap<Node, Integer>(); //The number of shortest paths crossed from each node
    
    public DijkstraBrande(ArrayList<Node> actorsArray) {
		
    	this.vertices = actorsArray;
    	
    	//Determine the routes between the nodes
    	Routes routes = new Routes(actorsArray.size());
    	
    	//Use edges to add the routes
    	for (int i = 0; i < actorsArray.size(); i++) {
    		
    		Node n = actorsArray.get(i);
    		
    		for (Edge edge: n.getEdges()) {
    			Node nextHop = edge.getNextHop();
    			double weight = edge.getWeight();
    			routes.addDirectRoute(n, nextHop, weight); 
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
        numSP.clear();
        
        // add source
        setShortestDistance(start, 0);
        //unsettledNodes.add(start);
    }

	//Return next hop for the given source and destination nodes
    public void run(Node start)
    {
        init(start);
        
        // the current node
        Node u;
        
        // extract the node with the shortest distance
        while ((u = unsettledNodes.poll()) != null)
        {
            assert !isSettled(u);
            
            // stop if reached destination 
            //if (u == destination) break;
            
            settledNodes.add(u);
            
            checkNeighbors(u);
        }
        
        //Get the next hop for destination
        //return getNextHop(start, destination);
        for (Node n:this.vertices)
        	System.out.println(n.getID() + " " + numSP.get(n));
    }

    //If v is reachable from u, then u is a predecessor of v and v is a successor of u
    private void checkNeighbors(Node u)
    {
        for (Node v : map.getDestinations(vertices, u))
        {
            if (isSettled(v))
            	continue;
            
            double shortDist = getShortestDistance(u) + map.getDistance(u, v);
            
            if (shortDist <= getShortestDistance(v))
            {
                setShortestDistance(v, shortDist);
                setPredecessor(v, u);
            }
        }        
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
    
    private void setPredecessor(Node v, Node u)
    {
        predecessors.put(v, u);
        int numSPu = numSP.containsKey(u) ? numSP.get(u) : 0;
        numSPu++;
        numSP.put(u, numSPu);
    }

    //@Override
	public ArrayList<Node> getVertices() {
		return this.vertices;
	}

	//@Override
	public Map<Node, ArrayList<LinkedList>> getPredPaths() {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public Map<Node, ArrayList<LinkedList>> getLongerPredPaths() {
		return null;
	}
}

