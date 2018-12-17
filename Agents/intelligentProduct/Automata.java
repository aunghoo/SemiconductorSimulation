package intelligentProduct;

import java.util.ArrayList;

import sharedInformation.ProductState;
import sharedInformation.ResourceEvent;
import intelligentProduct.Z3State;
import intelligentProduct.Route;
import intelligentProduct.Transition;


public class Automata {
	public String startState;
	public String endState;
	//public ArrayList<ProductState> states;
	public ArrayList<Z3State> z3States = new ArrayList<Z3State>();
	public ArrayList<Transition> transitions = new ArrayList<Transition>();
	
	void addState(ProductState ps) {
		//System.out.print("Something");
		Z3State st = new Z3State(ps.getName(), ps.getCost(), ps.getlVal(), ps.getgVal());
		//System.out.print("Done");
		/*State st = null;
		st.name = ps.getProcessCompleted();
		st.cost = ps.getCost();
		st.lVal = ps.getlVal();
		st.gVal = ps.getgVal();*/
		this.z3States.add(st);
		//System.out.print("added");
		
	}
	public void addTransition(ProductState ps, ArrayList<Route> routes) {
		Transition t = new Transition(ps, routes);
		transitions.add(t);
	}
	
	public void setStart(String start) {
		startState = start;
	}
	public void setEnd(String end) {
		endState = end;
	}
	
}
