package gvgenerator;

import java.util.ArrayList;
import java.util.Map;

public interface DijkstraGeneric {

	public ArrayList<Node> getVertices();
	public void run(Node start);
    public Map<Node, ArrayList<LinkedList>> getPredPaths();
    public Map<Node, ArrayList<LinkedList>> getLongerPredPaths();
}
