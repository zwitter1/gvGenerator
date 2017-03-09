package gvgenerator;

import java.util.ArrayList;
import java.util.List;

public class Routes {

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
