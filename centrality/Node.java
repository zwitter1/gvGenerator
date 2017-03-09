package centrality;

import java.awt.*;
import java.util.ArrayList;

public class Node extends NetworkNode implements Cloneable{
    public Color color;
    public int firstVisitTime;
    public int lastVisitTime;
    public int lineID = -1;
    private ArrayList<Integer> labels = new ArrayList<Integer>();
    private boolean isSelected = false;
    private ArrayList<Node> neighborList;
    private ArrayList<Node> neighborDominatorList;
    private ArrayList<Node> DominateeList;
    private ArrayList<Node> DominatorList;
    private ArrayList<Node> EssentialLinkList;
    
    //IFS
    private int degree;			//Keeps the number of neighbors it has.
    private Node nextHop;
    ArrayList<Node> sroute;
    double totalMovement = 0;
    private boolean isBoundaryNode;
    private double energy;
    //IFS END
    private ArrayList<Edge> edges;
    private double centralityScore;
    private double pageRankScore;

    private Node terminal = null;

    //private Segment segment = null;


    //used for Kruskal only
    private int mstID = 0;

    public boolean representative = false;
    public boolean isRelay = false;
    
    public boolean isCV = false;
    public boolean isFailedNode = false;

    /**
     * if it is already a dominator then null, else points to its dominator
     */
    public Node dominator;

    /**
     * Parent node in the spanning tree
     */
    private Node stParent;

    // Triangle Approach
    int ccid = 0;


    public Node(int i) {
        diameter = 6;
        color = Color.GREEN;
        neighborList = new ArrayList<Node>();
        neighborDominatorList = new ArrayList<Node>();
        DominateeList = new ArrayList<Node>();
        DominatorList = new ArrayList<Node>();
        EssentialLinkList = new ArrayList<Node>();
        networkID = -1;
        stParent = null;
        id = i;
        nextHop = null;
        isBoundaryNode = false;
        
        edges = new ArrayList<Edge>();
        centralityScore = 0;
        pageRankScore = 0;
    }

    //IFS
    public ArrayList<Edge> getEdges() {
    	return this.edges;
    }
    public double getPageRankScore() {
		return pageRankScore;
	}

	public void setPageRankScore(double pageRankScore) {
		this.pageRankScore = pageRankScore;
	}
	
	public void addToPageRankScore(double pageRankScore) {
		this.pageRankScore += pageRankScore;
	}

	public double getCentralityScore() {
		return centralityScore;
	}

	public void setCentralityScore(double centralityScore) {
		this.centralityScore = centralityScore;
	}

	public void addEdge(Edge e) {
    	this.edges.add(e);
    }
    public void removeEdge(Edge e) {
    	this.edges.remove(e);
    }
    public Node getNextHop() {
    	return nextHop;
    }
    
    public void setNextHop(Node nextHop) {
    	this.nextHop = nextHop;
    }

    public void setSRoute(ArrayList<Node> sroute) {
    	this.sroute = sroute;
    }
    
    public ArrayList<Node> getSRoute() {
    	return sroute;
    }
    
    public void setTotalMovement(double d) {
    	this.totalMovement = d;
    }
    
    public double getTotalMovement() {
    	return this.totalMovement;
    }
    //IFS END
    
    public ArrayList<Node> getNeighborList() {
        return neighborList;
    }


    public ArrayList<Node> getDominatorList() {
        return DominatorList;
    }

    public boolean isDominator() {
        return isDominator;
    }

    public ArrayList<Node> getDominateeList() {
        return DominateeList;
    }

    public ArrayList<Node> getNeighborDominatorList() {
        return neighborDominatorList;
    }

    public void addNeighborList(Node node) {
        this.neighborList.add(node);
    }
    
    //ifs
    public void clearNeighborList() {
        this.neighborList.clear();
    }

    public boolean isCV() {
		return isCV;
	}

	public void setCV(boolean isCV) {
		this.isCV = isCV;
	}

	public boolean isFailedNode() {
		return isFailedNode;
	}

	public void setFailedNode(boolean isFailedNode) {
		this.isFailedNode = isFailedNode;
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public void setEnergy(double energy) {
		this.energy = energy;
	}
	
	public void setRelay(boolean b) {	//added by fatih
		isRelay = b;
	}

	public String toString() {
        if (isRelay()) {
            return "R"+id + " - " + networkID;
        } else {
            return "S"+id + " - " + networkID;
        }
    }

    public void draw(Graphics g, boolean showEdges, boolean showID, boolean treeEdgesOnly) {
//        if (isDominator)
//            g.setColor(Color.RED);
//        else
//            g.setColor(Color.BLUE);
        //federation-begin
        if (id == 1 && !isRelay) {
            int r = 402;
//            g.drawOval((int)x-r,(int)y-r,2*r,2*r);
//            g.drawLine((int)x,(int)y,930,726);
        }
        if (id == 0 && !isRelay) {
            int r = 202;
//            g.drawOval((int)x-r,(int)y-r,2*r,2*r);
//            g.drawLine((int)x,(int)y,930,726);
        }
        if (!representative)
            g.setColor(Color.BLUE);
        else
            g.setColor(Color.RED);
        
        if (isCV)
        	g.setColor(Color.CYAN);  
        
        if (isFailedNode)		//modified by fatih
        	g.setColor(Color.RED);
        if (isRelay)			//modified by fatih
        	g.setColor(Color.DARK_GRAY);
        if (id == 1)
        	g.setColor(Color.YELLOW);
        if (isBoundaryNode)
        	g.setColor(Color.ORANGE);
        
        //federation-end
        if (!isRelay) {
            if (!isSelected)
                g.fillOval((int) (x - (diameter / 2)), (int) (y - (diameter / 2)), diameter, diameter);
            else {
                int nd = diameter + 4;
                g.fillOval((int) (x - (nd / 2)), (int) (y - (nd / 2)), nd, nd);
            }
        } else {
            g.setColor(Color.DARK_GRAY);	//BLUE edited by ifs
            g.fillRect((int) (x - (diameter / 2)), (int) (y - (diameter / 2)), diameter, diameter);
        }

        if (showID) {
            g.setColor(Color.BLACK);
            g.drawString("" + id, (int) x, (int) y + 20);
        }

        if (showEdges) {
            g.setColor(Color.BLACK);
            if (treeEdgesOnly) {
                for (int i = 0; i < EssentialLinkList.size(); i++) {
                    g.drawLine((int) x, (int) y, (int) EssentialLinkList.get(i).x, (int) EssentialLinkList.get(i).y);
                }
                if (stParent != null)
                    g.drawLine((int) x, (int) y, (int) stParent.x, (int) stParent.y);
            } else {
                for (int i = 0; i < neighborList.size(); i++) {
                    g.drawLine((int) x, (int) y, (int) neighborList.get(i).x, (int) neighborList.get(i).y);
                }
            }
        }
        if (isRelay)			//modified by fatih
        	g.setColor(Color.DARK_GRAY);
    }

    public int getDiameter() {
        return diameter;
    }


    public ArrayList<Node> getEssentialLinkList() {
        return EssentialLinkList;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setDominator(boolean dominator) {
        isDominator = dominator;
    }

    public Node getStParent() {
        return stParent;
    }

    public boolean isNeighbor(Node g) {
        for (int i = 0; i < neighborList.size(); i++) {
            Node gateway = neighborList.get(i);
            if (g == gateway)
                return true;
        }
        return false;
    }

    public int getMstID() {
        return mstID;
    }

    public void setMstID(int mstID) {
        this.mstID = mstID;
    }

    public ArrayList<Integer> getLabels() {
        return labels;
    }

    public boolean isRelay() {
        return isRelay;
    }

    public Node getTerminal() {
        return terminal;
    }

    public Node cloneGateway() {
        Node ng = new Node(id);
        ng.setX(getX());
        ng.setY(getY());
        ng.isRelay = isRelay;
        return ng;
    }
    
    @Override public Node clone() {
        try {
            Node clone = (Node) super.clone();
            clone.setId(id);
            clone.setX(getX());
            clone.setY(getY());
            
            /*ArrayList<Gateway> neighbors = new ArrayList<Gateway>();
            for (Gateway neighbor : neighborList) {
            	neighbors.add(neighbor.clone());
            }*/
            clone.degree = (neighborList == null) ? degree : neighborList.size();
            clone.neighborList = null;//neighbors;
            clone.isRelay = isRelay;
            clone.isBoundaryNode = isBoundaryNode;
            clone.energy = energy;
            
            if (this.nextHop == null)
            	clone.nextHop = null;
            else {
	            Node nextHop = new Node(this.nextHop.getID());
	            nextHop.setX(this.nextHop.getX());
	            nextHop.setY(this.nextHop.getY());
	            clone.nextHop = nextHop;
            }
            
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new Error("This should not occur since we implement Cloneable");
        }
    }

    //Returns the number of neighbors. by ifs.
    public int getDegree() {
    	return degree;
    }
    
    public int getCcid() {
        return ccid;
    }

    public void setCcid(int ccid) {
        this.ccid = ccid;
    }

    //ifs added for dijkstra
	public int compareTo(Node g) {
        return String.valueOf(this.getID()).compareTo(String.valueOf(g.getID()));
	}

	public void setBoundaryNode(boolean b) {
		this.isBoundaryNode = b;		
	}
	
	public boolean isBoundaryNode() {
		return isBoundaryNode;
	}
}
