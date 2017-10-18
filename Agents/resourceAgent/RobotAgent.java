package resourceAgent;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections15.Transformer;

import Part.Part;
import Robot.RobotLLC;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import intelligentProduct.ProductAgent;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;
import sharedInformation.CapabilitiesEdge;
import sharedInformation.CapabilitiesNode;
import sharedInformation.PhysicalProperty;
import sharedInformation.RASchedule;

public class RobotAgent implements ResourceAgent {
	
	private RobotLLC robot;
	private boolean working;
	private String program;
	
	// Capabilities Graph
	private DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> robotCapabilities;
	private CapabilitiesEdge runningEdge;
	private Transformer<CapabilitiesEdge, Integer> weightTransformer;
	
	//Neighbors
	private ArrayList<ResourceAgent> neighbors;
	private HashMap<ResourceAgent, CapabilitiesNode> tableNeighborNode;
	private HashMap<Point, Object> tableLocationObject;
	
	//Scheduling
	private RASchedule schedule;
	
	/**
	 * @param name
	 * @param tableLocationObject - table of physical points and the corresponding physical object
	 * @param robotLLC
	 */
	public RobotAgent(String name, RobotLLC robot, HashMap<Point, Object> tableLocationObject){
		this.robot = robot;
		this.working = false;
		this.robotCapabilities = new DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge>();
		
		//Edge that is currently running
		this.runningEdge = null;
		//Transformer for shortest path
		this.weightTransformer = new Transformer<CapabilitiesEdge,Integer>(){
	       	public Integer transform(CapabilitiesEdge edge) {return edge.getWeight();}
		};

		this.neighbors = new ArrayList<ResourceAgent>();
		
		//Table of correlations between location and the object at that location
		this.tableLocationObject = tableLocationObject;
		
		createOutputGraph();
		
		this.schedule = new RASchedule(this);
	}
	
	@Override
	public String toString() {
		return "Robot Agent for " + this.robot.toString();
	}
	
	//================================================================================
    // Adding/getting neighbors for this resource
    //================================================================================
	
	public void addNeighbor(ResourceAgent neighbor){
		this.neighbors.add(neighbor);
	}
	
	public ArrayList<ResourceAgent> getNeighbors(){
		return this.neighbors;
	}

	//================================================================================
    // Product/resource team formation
    //================================================================================

	@Override
	public void teamQuery(ProductAgent productAgent, PhysicalProperty desiredProperty, CapabilitiesNode currentNode,
			int currentTime, int maxTime, ArrayList<ResourceAgent> teamList, ArrayList<CapabilitiesEdge> edgeList) {
		
		new ResourceAgentHelper().teamQuery(this, productAgent, desiredProperty, currentNode,
				currentTime, maxTime, teamList, edgeList, neighbors, tableNeighborNode, robotCapabilities, weightTransformer);
	}
	
	//================================================================================
    // Product agent scheduling
    //================================================================================
	
	@Override
	public RASchedule getSchedule() {
		return this.schedule;
	}


	@Override
	public boolean requestScheduleTime(ProductAgent productAgent, int startTime, int endTime) {
		return this.schedule.addPA(productAgent, startTime, endTime, false);
	}


	@Override
	public boolean removeScheduleTime(ProductAgent productAgent, int startTime) {
		return this.removeScheduleTime(productAgent, startTime);
	}
	
	//================================================================================
    // Product agent communication
    //================================================================================

	/**
	 * @return
	 */
	@Override
	public DirectedSparseGraph<CapabilitiesNode, CapabilitiesEdge> getCapabilities() {
		return this.robotCapabilities;
	}

	@Override
	public boolean query(String program, ProductAgent productAgent) {
		//Check if the query conforms with the current schedule
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		double startTime = schedule.getTickCount();
		
		CapabilitiesEdge desiredEdge = null;

		for (CapabilitiesEdge edge : this.getCapabilities().getEdges()){
			if (edge.getActiveMethod().equals(program)){
				desiredEdge = edge;
				break;
			}
		}
		
		//If the product agent is scheduled for this time, run the desired program
		if (desiredEdge!=null &&  this.schedule.checkPATime(productAgent, (int) startTime, (int) startTime+desiredEdge.getWeight())){
			return this.robot.runMoveObjectProgram(program);
		}
		
		return false;
	}

	//================================================================================
    // Helper methods
    //================================================================================
	
	/**
	 * Helper method to create the capabilities graph for the part
	 */
	@ScheduledMethod (start = 1, priority = 6000)
	private void createOutputGraph() {
		
		//For each program in the Robot LLC, create an edge 
		for(String program : this.robot.getProgramList()){
			Point[] endpoints = this.robot.getProgramEndpoints(program);
			Point start = endpoints[0];
			Point end = endpoints[1];
			
			//Create the physical property of being in each location
			PhysicalProperty startLocation = new PhysicalProperty(start);
			PhysicalProperty endLocation = new PhysicalProperty(end);
			
			//Create the capability nodes
			CapabilitiesNode startNode = new CapabilitiesNode(this.tableLocationObject.get(startLocation.getPoint()), null, startLocation);  
			CapabilitiesNode endNode = new CapabilitiesNode(this.tableLocationObject.get(endLocation.getPoint()), null, endLocation);
			
			//Estimate the weight (time it takes to move between two points) using the manhattan distance.
			//Multiplier to give more time to the robot to perform the action
			double multiplier = 2;
			int weight = (int) ((Math.abs(start.x-end.x) + Math.abs(start.y-end.y))*multiplier/this.robot.getRobot().getVelocity());
			
			//Create and add the edge to the capabilities
			CapabilitiesEdge programEdge = new CapabilitiesEdge(this, startNode, endNode, program, weight);
			this.robotCapabilities.addEdge(programEdge, startNode, endNode);
		}
	}
	
	/**
	 * Finds at which nodes the neighbors are connected
	 */
	@ScheduledMethod (start = 1, priority = 5000)
	public void findNeighborNodes(){
		this.tableNeighborNode = new HashMap<ResourceAgent, CapabilitiesNode>();
		
		//Fill the look up table that matches the neighbor with the node
		for (ResourceAgent neighbor : this.neighbors){
			for (CapabilitiesNode node : this.robotCapabilities.getVertices()){
				if(neighbor.getCapabilities().containsVertex(node)){
					//Assume only one node can be shared between neighbors
					this.tableNeighborNode.put(neighbor, node);
				};
				
			}
		}
	}

	//================================================================================
    // Testing
    //================================================================================
}