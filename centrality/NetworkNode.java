package centrality;

public class NetworkNode {

    protected double x;
    protected double y;
    protected int id;
    protected int diameter;
    protected int networkID=-1;
    protected boolean isPrimaryPartition =false;
    /**
     * 1 if the node is dominator, 0 otherwise
     */
    protected boolean isDominator;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getID() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public boolean isIn(int x, int y) {
        return x >= this.x - (diameter / 2) && x <= this.x + (diameter / 2) && y >= this.y - (diameter / 2) && y <= this.y + (diameter / 2);
    }


    public int getNetworkID() {
        return networkID;
    }

    public void setNetworkID(int networkID) {
        this.networkID = networkID;
    }

    public boolean isPrimaryPartition() {
        return isPrimaryPartition;
    }


}
