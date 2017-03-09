package centrality;

public class Variables {

	private static Variables instance = null;
	private static double LongerPathsThreshold = 1; //Threshold for adapted betweenness
	
	protected Variables() {
	      // Exists only to defeat instantiation.
	}
	
	public static Variables getInstance() {
	      
		if(instance == null)
	         instance = new Variables();
	    
	    return instance;
	}
	
	public double getThreshold() {
		
		return LongerPathsThreshold;
	}
	
	public void setThreshold(double value) {
		
		this.LongerPathsThreshold = value;
	}
}

