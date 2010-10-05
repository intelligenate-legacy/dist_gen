package distgen;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameter;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;
import cern.jet.random.Poisson;

public class Generator extends SimpleAgent{
	private double eCostGrid1 = 0;
	private double eCostOther1 = 0;	
	private double carbonOutGrid1 = 0;
	private double carbonOutOther1 = 0;
	
	@Parameter (displayName = "eCostGrid1", usageName = "eCostGrid1")
	public double getECostGrid() {
		return eCostGrid1;
	}
	private void setECostGrid(double newValue) {
		eCostGrid1 = newValue;
	}

	@Parameter (displayName = "eCostOther1", usageName = "eCostOther1")
	public double getECostOther() {
		return eCostOther1;
	}
	private void setECostOther(double newValue) {
		eCostOther1 = newValue;
	}
	
	@Parameter (displayName = "carbonOutOther1", usageName = "carbonOutOther1")
	public double getCarbonOutOther() {
		return carbonOutOther1;
	}
	private void setCarbonOutOther(double newValue) {
		carbonOutOther1 = newValue;
	}

	@Parameter (displayName = "carbonOutGrid1", usageName = "carbonOutGrid1")
	public double getCarbonOutGrid() {
		return carbonOutGrid1;
	}
	private void setCarbonOutGrid(double newValue) {
		carbonOutGrid1 = newValue;
	}

	
	// This constructor is used to create initial generators from the context creator
	
	public Generator(){
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double generatorType = (Double)p.getValue("generatorType");

		double eCostGrid = (Double)p.getValue("eCostGrid1");
		double eCostOther = (Double)p.getValue("eCostOther1");
		double carbonOutputGrid = (Double)p.getValue("carbonOutGrid1");
		double carbonOutputOther = (Double)p.getValue("carbonOutOther1");
		
		

	}
	
	 /**
    *
    * This is the step behavior.
    * @method step
    *
    */
	public void step() {
	    // Get the context in which the generator resides.
			Context context = ContextUtils.getContext(this);
	}
	
	// called by consumer to compare energy cost
	public static double getEnergyCost(double avgDemand, String eSource){
		double energyCost = 0;
		Parameters p = RunEnvironment.getInstance().getParameters();
		
		int tickCount = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); // gets the tickCount and casts it to an integer

		double eCostGrid = (Double)p.getValue("eCostGrid" + tickCount);
		double eCostOther = (Double)p.getValue("eCostOther" + tickCount);
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
	
	public static double getCO2Output(double avgDemand, String eSource){
		Parameters p = RunEnvironment.getInstance().getParameters();
		int tickCount = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); // gets the tickCount and casts it to an integer
		double carbonOutputGrid = (Double)p.getValue("carbonOutGrid" + tickCount);
		double carbonOutputOther = (Double)p.getValue("carbonOutOther" + tickCount); 
		double cO2Output = 0;
		
		
		if (eSource.equals("grid")){
			cO2Output = carbonOutputGrid * avgDemand;
			System.out.println("CO2 output from the grid is " + cO2Output);
			return cO2Output;
		}
		else if (eSource.equals("other")){
			cO2Output = carbonOutputOther * avgDemand;
			System.out.println("CO2 output from other source is " + cO2Output);
			return cO2Output;
		}
		else{
			System.out.println("CO2 output failed, CO2 output is: " + cO2Output);
			return cO2Output;
		}
	
	}

}
