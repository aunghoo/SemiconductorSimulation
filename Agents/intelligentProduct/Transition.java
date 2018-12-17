package intelligentProduct;

import java.util.ArrayList;

import sharedInformation.ProductState;
import intelligentProduct.Route;

public class Transition {
	public Transition(ProductState parent, ArrayList<Route>routes) {
		this.parent = parent.getName();
		this.routes = routes;/*
		for (Route r : routes) {
			this.routes.add(r);
		}*/
	}
	public String parent;
	public ArrayList<Route> routes;	
}