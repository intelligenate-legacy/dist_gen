package distgen;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import cern.jet.random.Poisson;

public class Generator extends SimpleAgent{
	// This constructor is used to create initial generators from the context creator
	
	public Generator(){
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double generatorType = (Double)p.getValue("generatorType");

		double eCostGrid = (Double)p.getValue("eCostGrid");
		double eCostOther = (Double)p.getValue("eCostOther");
		

	}
	
	 /**
    *
    * This is the step behavior.
    * @method step
    *
    */
	public void step() {
	    // Get the context in which the consumer resides.
			Context context = ContextUtils.getContext(this);
	}
	
	// called by consumer to compare energy cost
	public static double getEnergyCost(double avgDemand, String eSource){
		double energyCost = 0;
		Parameters p = RunEnvironment.getInstance().getParameters();

		double eCostGrid = (Double)p.getValue("eCostGrid");
		double eCostOther = (Double)p.getValue("eCostOther");
		String grid = "grid";
		String other = "other";
		
		if (eSource.equals(grid)){
			energyCost = eCostGrid * avgDemand;
			System.out.println("Energy cost grid is " + energyCost);
			return energyCost;
		}
		else if (eSource.equals(other)){
			energyCost = eCostOther * avgDemand;
			System.out.println("Energy Cost other is " + energyCost);
			return energyCost;
		}
		else{
			System.out.println("Energy cost failed");
			return energyCost;
		}
	}
	
	

}
