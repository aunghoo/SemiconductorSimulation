package intelligentProduct;

import java.util.ArrayList;

import sharedInformation.ProductState;
import sharedInformation.ResourceEvent;
import intelligentProduct.State;
import intelligentProduct.Route;


public class Automata {
	public String startState;
	public String endState;
	//public ArrayList<ProductState> states;
	public ArrayList<State> states;
	private ArrayList<Transition> transitions;
	
	public class Transition{
		public Transition(ProductState parent, ArrayList<Route>routes) {
			this.parent = parent.getProcessCompleted();
			this.routes = routes;
		}
		private String parent;
		private ArrayList<Route> routes;		
	}
	
	void addState(ProductState ps) {
		System.out.print("Something");
		State st = new State(ps.getProcessCompleted(), ps.getCost(), ps.getlVal(), ps.getgVal());
		System.out.print("Done");
		/*State st = null;
		st.name = ps.getProcessCompleted();
		st.cost = ps.getCost();
		st.lVal = ps.getlVal();
		st.gVal = ps.getgVal();*/
		this.states.add(st);
		System.out.print("added");
		
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
