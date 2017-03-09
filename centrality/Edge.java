package centrality;

public class Edge {

	private Node nextHop;
	private double weight;
	
	public Edge(Node nextHop, double weight) {
		super();
		this.nextHop = nextHop;
		this.weight = weight;
	}
	
	public Node getNextHop() {
		return nextHop;
	}
	public void setNextHop(Node nextHop) {
		this.nextHop = nextHop;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
