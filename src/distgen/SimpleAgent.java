package distgen;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

/**
 * SimpleAgent is the parent class of all other agents.
 * 
 * @author Jason Veneman 
 */

@AgentAnnot(displayName = "Agent")
public class SimpleAgent {
	private double year; 		// The simulation year
	private double energyCost;
	private double avgDemand;
	
	// Schedule the step method for agents.  The method is scheduled starting at 
	// tick one with an interval of 1 tick.  Specifically, the step starts at 1, and
	// and recurs at 2,3,4,...etc
	@ScheduledMethod(start = 1, interval = 1, shuffle=true)
	public void step() {
		// Override by subclasses
	}


	public int isGrid() {
		return 0;
	}

	public int isOther() {
		return 0;
	}
	
}

