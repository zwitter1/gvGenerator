package gvgenerator;

public class LinkedList
{
	private Node curNode;
	private LinkedList nextNode;
	
	public LinkedList(Node curNode) {
		this.curNode = curNode;
	}
	public void addHop(LinkedList nextNode) {
		
		this.nextNode = nextNode;
	}
	public Node getCurNode() {
		return curNode;
	}
	public LinkedList getNextHop() {
		return nextNode;
	}
}
