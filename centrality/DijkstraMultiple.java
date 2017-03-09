package centrality;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class DijkstraMultiple implements DijkstraGeneric{

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
    
    public DijkstraMultiple(ArrayList<Node> actorsArray) {
		
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
            if (isSettled(v))
            	continue;
            
            double shortDist = getShortestDistance(u) + map.getDistance(u, v);
            if (shortDist < getShortestDistance(v))
            {
                setShortestDistance(v, shortDist);
                setPredecessor(v, u, true);
            }
            else if (shortDist == getShortestDistance(v))
            {
                setPredecessor(v, u, false);
            }
        }        
    }
       
    private void setPredecessor(Node v, Node u, boolean overwrite)
    {
    	ArrayList<LinkedList> prePrePaths = predecessorPaths.get(u);
    	ArrayList<LinkedList> prePaths = predecessorPaths.get(v);  	 
	
    	if (prePaths == null)
    		prePaths = new ArrayList<LinkedList>();
    	else if (overwrite)
			prePaths.clear();
    	
		if (prePrePaths != null) {
			//Use predecessors of node u if any available
			for (LinkedList ll : prePrePaths) {

				LinkedList nll = new LinkedList(u);
				nll.addHop(ll);
				prePaths.add(nll);
			}
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
		// TODO Auto-generated method stub
		return null;
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
    
    public void printPaths() {
    	
		StringBuffer s = new StringBuffer();
		for (Node n : this.vertices) {

			if (predecessorPaths.get(n) == null)
				continue;
			
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
    }
}

