package intelligentProduct;

public class State {
	public State(String name, int cost, int lval, int gval) {
		this.name = name;
		this.cost = cost;
		this.lVal = lval;
		this.gVal = gval;
	}
	
	public String name;
	public int cost;
	public int lVal;
	public int gVal;
}