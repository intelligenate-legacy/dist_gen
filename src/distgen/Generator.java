package distgen;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import cern.jet.random.Poisson;

public class Generator extends SimpleAgent{
	// This constructor is used to create initial generators from the context creator
	public Generator(){
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double generatorType = (Double)p.getValue("generatorType");

		double eCostGrid = (Double)p.getValue("eCostGrid");
		double eCostOther = (Double)p.getValue("eCostOther");
		

	}
	
	public double energyCost(double avgDemand, String eSource){
		double energyCost = 0;
		double eCostGrid = getECostGrid();
		String grid = "grid";
		String other = "other";
		
		if (eSource.equals(grid)){
			
			energyCost = eCostGrid * avgDemand;
		}
		
		return energyCost;
	}
	
	

}
