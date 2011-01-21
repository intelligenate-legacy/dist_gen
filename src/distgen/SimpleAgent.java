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
//	private double energyCost;
//	private double avgDemand;
	
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


	public int isDeliberate() {
		return 0;
	}


	public int isCompare() {
		return 0;
	}


	public int isRepeat() {
		return 0;
	}


	public int isImitate() {
		return 0;
	}


	public int ageCount() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamDetachGrid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamDetachOther() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamAttachGrid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamAttachOther() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt2_4Grid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt2_4Other() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt5Grid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt5Other() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isMobileGrid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isMobileOther() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isMobileOtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt5OtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isAppt2_4OtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamAttachOtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isSingleFamDetachOtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isGrid2Grid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOtherX2OtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOtherX2Other() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOtherX2Grid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOther2OtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOther2Other() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isOther2Grid() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isGrid2OtherX() {
		// TODO Auto-generated method stub
		return 0;
	}


	public int isGrid2Other() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}

