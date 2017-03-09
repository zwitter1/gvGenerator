package gvgenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DijkstraAll implements DijkstraGeneric{

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
    private final Map<Node, ArrayList<LinkedList>> predecessorPaths = new HashMap<Node, ArrayList<LinkedList>>();
    private final Map<Node, ArrayList<LinkedList>> longerPredecessorPaths = new HashMap<Node, ArrayList<LinkedList>>();
    
    public DijkstraAll(ArrayList<Node> actorsArray) {
		
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
        predecessorPaths.clear();
        longerPredecessorPaths.clear();
        
        // add source
        setShortestDistance(start, 0);
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
        //printPaths();
    }

    //If v is reachable from u, then u is a predecessor of v and v is a successor of u
    private void checkNeighbors(Node u)
    {
        for (Node v : map.getDestinations(vertices, u))
        {
        	double shortDist = getShortestDistance(u) + map.getDistance(u, v);
        	
            if (isSettled(v)) {
            	
            	//
            	if (shortDist > getShortestDistance(v))
            		setLongerPredecessor(v, u);
            	//
            	continue;
            }
            
            //double shortDist = getShortestDistance(u) + map.getDistance(u, v);
            if (shortDist < getShortestDistance(v))
            {
                setShortestDistance(v, shortDist);
                setPredecessor(v, u, true);
            }
            else if (shortDist == getShortestDistance(v))
            {
                setPredecessor(v, u, false);
            }else
            {
            	setLongerPredecessor(v, u);
            }
        }        
    }
    
    private void setLongerPredecessor(Node v, Node u)
    {
    	ArrayList<LinkedList> preLongerPaths = longerPredecessorPaths.get(v);
		ArrayList<LinkedList> prePreLongerPaths = longerPredecessorPaths.get(u);
		ArrayList<LinkedList> prePrePaths = predecessorPaths.get(u);
				
		//Set all paths from u to longer paths of v
		if (preLongerPaths == null)
			preLongerPaths = new ArrayList<LinkedList>();
		
		//Use the shortest paths of u first
		for (LinkedList ll : prePrePaths) {

			LinkedList nll = new LinkedList(u);
			nll.addHop(ll);
			//Check threshold
			if (satisfiesThreshold(v, nll))
				preLongerPaths.add(nll);
		}
		//Use longer paths of u if any
		if (prePreLongerPaths != null) {
			
			for (LinkedList ll : prePreLongerPaths) {

				LinkedList nll = new LinkedList(u);
				nll.addHop(ll);
				if (satisfiesThreshold(v, nll))
					preLongerPaths.add(nll);
			}		
		}
		longerPredecessorPaths.put(v, preLongerPaths);
    }
    
	private void setPredecessor(Node v, Node u, boolean overwrite)
    {
    	ArrayList<LinkedList> prePrePaths = predecessorPaths.get(u);
    	ArrayList<LinkedList> prePaths = predecessorPaths.get(v); 
    	//
    	ArrayList<LinkedList> preLongerPaths = longerPredecessorPaths.get(v);
    	//
	
    	if (prePaths == null)
    		prePaths = new ArrayList<LinkedList>();
    	else if (overwrite) {
    		//
    		//Move to prePaths to longer paths list
    		
    			
			ArrayList<LinkedList> newLongerPaths = new ArrayList<LinkedList>();
			//Check existing longer path lengths
			if (preLongerPaths != null) {
				for (LinkedList ll:preLongerPaths)
					if (satisfiesThreshold(v, ll))
						newLongerPaths.add(ll);
			}
			//Check new longer path lengths
			for (LinkedList ll:prePaths)
				if (satisfiesThreshold(v, ll))
					newLongerPaths.add(ll);
			
			//(ArrayList<LinkedList>) prePaths.clone();
			//if (preLongerPaths != null)
			//	newLongerPaths.addAll(preLongerPaths);
  	
    		longerPredecessorPaths.put(v, newLongerPaths);
    		
			//
			prePaths.clear();
    	}
		
		if (prePrePaths != null) {
			//Use predecessors of node u if any available
			for (LinkedList ll : prePrePaths) {

				LinkedList nll = new LinkedList(u);
				nll.addHop(ll);
				prePaths.add(nll);
			}
			//
			//
			//Use longer paths of u if any
			ArrayList<LinkedList> prePreLongerPaths = longerPredecessorPaths.get(u);
			if ((prePreLongerPaths != null) && 
					//(longerPredecessorPaths.size() < predecessorPaths.size()*Constants.LongerPathsThreshold)) {
					(longerPredecessorPaths.size() < predecessorPaths.size()*Variables.getInstance().getThreshold())) {
					
				if (preLongerPaths == null)
					preLongerPaths = new ArrayList<LinkedList>();
				for (LinkedList ll : prePreLongerPaths) {

					LinkedList nll = new LinkedList(u);
					nll.addHop(ll);
					if (satisfiesThreshold(v, nll))
						preLongerPaths.add(nll);
				}
				longerPredecessorPaths.put(v, preLongerPaths);
			}			
			//
			//
		} else {
			LinkedList nll = new LinkedList(u);
			prePaths.add(nll);
		}
    	//Update predecessors for node v	   		
    	predecessorPaths.put(v, prePaths);	
    }
    
    public ArrayList<LinkedList> getPredecessor(Node vertice)
    {
        return predecessorPaths.get(vertice);
    }
    
    //@Override
    public ArrayList<Node> getVertices() {
    	return this.vertices;
    }
    
    //@Override
    public Map<Node, ArrayList<LinkedList>> getPredPaths() {
    	return this.predecessorPaths;
    }
    
    //@Override
    public Map<Node, ArrayList<LinkedList>> getLongerPredPaths() {
    	return this.longerPredecessorPaths;
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
    
    private boolean satisfiesThreshold(Node v, LinkedList longerPath) {
        
    	boolean doesSatisfy = false;
    
		double shortestPathLength = getShortestDistance(v);
		double longerPathLength = Helper.getWeightedPathLength(longerPath, v);
		
		//if (longerPathLength <= shortestPathLength*(Constants.LongerPathsThreshold+1))
		if (longerPathLength <= shortestPathLength*(Variables.getInstance().getThreshold()+1))
			doesSatisfy = true;
		
		return doesSatisfy;
    }
	
    public void printPaths() {
    	
		StringBuffer s = new StringBuffer();
		for (Node n : this.vertices) {

			if (predecessorPaths.get(n) != null) {	
			
				for (LinkedList path : predecessorPaths.get(n)) {
	
					s.append(n.getID());
					LinkedList curHop = path;
					while (curHop != null) {
						s.insert(0, " --> ");
						s.insert(0, curHop.getCurNode().getID());
						curHop = curHop.getNextHop();
					}
					System.out.println(s);
					s.setLength(0);;
				}
			}
			if (longerPredecessorPaths.get(n) != null){
				for (LinkedList path : longerPredecessorPaths.get(n)) {
	
					s.append("]");
					s.insert(0, n.getID());
					LinkedList curHop = path;
					while (curHop != null) {
						s.insert(0, " --> ");
						s.insert(0, curHop.getCurNode().getID());
						curHop = curHop.getNextHop();
					}
					s.insert(0, "[");
					System.out.println(s);
					s.setLength(0);;
				}
			}
		}
    }
}

