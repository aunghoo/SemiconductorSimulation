package sharedInformation;

import intelligentProduct.ProductAgent;
import resourceAgent.ResourceAgent;

import java.util.ArrayList;

public class RASchedule {
	ResourceAgent resourceAgent;
	ArrayList<ProductAgent> productAgents;
	ArrayList<Integer> startTimes;
	ArrayList<Integer> endTimes;
	
	/**
	 * Part agents, start times, and end times array lists must be the same size. Start and End Times must be sorted
	 * 
	 * @param resourceAgent
	 * @param partAgents (Array List) The part agents that occupy the resource agent
	 * @param startTimes (Array List)
	 * @param endTimes (Array List)
	 */
	public RASchedule(ResourceAgent resourceAgent) {
		
		this.resourceAgent = resourceAgent;
		this.productAgents = new ArrayList<ProductAgent>();
		this.startTimes = new ArrayList<Integer>();
		this.endTimes = new ArrayList<Integer>();
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String total = "";
		
		for (int i = 0; i<this.productAgents.size();i++){
			total = total + " "+ this.productAgents.get(i) + " [" + this.startTimes.get(i) + "," + this.endTimes.get(i)+"];";		
		}
		
		return resourceAgent + "Schedule:" + total;
	}
	
	public boolean checkPATime(ProductAgent productAgent, int startTime, int endTime){
		Integer checkStartTime;
		Integer checkEndTime;
		
		//Check to see if PA can be scheduled
		for(int i = 0; i < startTimes.size(); i++){
			checkStartTime = startTimes.get(i);
			checkEndTime = endTimes.get(i);
			
			//Check to see if the product agent has this time reserved
			if (startTime >= checkStartTime && endTime<=checkEndTime){
				if (this.productAgents.get(i).equals(productAgent)){
					return true;
				}
				else{
					return false;
				}
			}
		}
		return false;
	}



	/** Add a product agent to the schedule
	 * @param productAgent
	 * @param startTime
	 * @param endTime
	 * @param allowMultiple
	 * @return
	 */
	public boolean addPA(ProductAgent productAgent, Integer startTime, Integer endTime, boolean allowMultiple){
		
		if (startTime < 0 || endTime <= 0 || endTime<startTime ){
			System.out.println("End time and start time are wrong for " + resourceAgent + " for " + productAgent);
			return false;
		}
		
		//If the new product agent is the first product agent to be added
		if (productAgents.size() == 0){
			productAgents.add(productAgent);
			startTimes.add(startTime);
			endTimes.add(endTime);
			return true;
		}
		
		Integer checkStartTime;
		Integer checkEndTime;
		
		//If the new part agent is scheduled after the first one and it doesn't allow multiple scheduled together
		if (!allowMultiple){
			//Check to see if PA can be scheduled
			for(int i = 0; i < startTimes.size(); i++){
				checkStartTime = startTimes.get(i);
				checkEndTime = endTimes.get(i);
				
				if (startTime < checkEndTime){
					if (endTime <= checkStartTime){
						productAgents.add(i,productAgent);
						startTimes.add(i,startTime);
						endTimes.add(i,endTime);
						return true;
					}
					else{
						System.out.println("Resource busy " + resourceAgent + " for " + productAgent);
						return false;
					}
				}
			}
		}
		
		
		//If the new part agent is scheduled last and the resource is free
		productAgents.add(productAgent);
		startTimes.add(startTime);
		endTimes.add(endTime);
		return true;
	}
	
	/** Removes a product agent from the schedul
	 * @param productAgent
	 * @param startTime
	 */
	public void removePA(ProductAgent productAgent, int startTime){
		for(int index = 0; index < this.startTimes.size(); index++){
			//Find if there is a product agent scheduled for the proposed time to remove it
			if (startTime >= this.startTimes.get(index) && startTime< this.endTimes.get(index) && productAgent.equals(this.productAgents.get(index))){
				//Remove the product agent if the proposed scheduled agent is found
				this.startTimes.remove(index);
				this.endTimes.remove(index);
				this.productAgents.remove(index);
			}
		}
	}

	/**
	 * @param startTime
	 * @return The time the resource is free after the start time
	 */
	public int getFreeTime(int startTime){
		//Large number if there is nothing after this part
		int ret = 100000;
		
		for(int i = 0; i < startTimes.size(); i++){
			if (startTimes.get(i) > startTime){
				
				//The time until the first product agent is scheduled to arrive
				if (i==0){
					ret  = startTimes.get(i) - startTime;
				}
				
				//Check to see that the last event ended
				else if (endTimes.get(i-1) <= startTime){
					ret  = startTimes.get(i) - startTime;
				}
				
				// The resource is working during this time
				else{
					ret = 0;
				}
				break;
			}
		}
		
		return ret;
	}
	
}