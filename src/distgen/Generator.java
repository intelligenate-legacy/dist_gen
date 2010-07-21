package distgen;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import cern.jet.random.Poisson;

public class Generator extends SimpleAgent{
	// This constructor is used to create initial consumers from the context creator
	public Consumer(){
		// Get the consumer type from the environment parameters
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double consumerType = (Double)p.getValue("consumerType");
		double housingType = (Double)p.getValue("housingType");
		double avgDemand = (Double)p.getValue("avgDemand");
		String eSource = (String)p.getValue("eSource");
		String heatSource = (String)p.getValue("heatSource");
		double eCost = (Double)p.getValue("eCost");
		
		//set the seed, need to test how leaving this out and having the seed based on the system clock affects the output
		RandomHelper.setSeed(777);	//could also make this a parameter
		
		// initialize demand around a Poisson distribution
		Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemand);
		

		this.setEuse(randomStream.nextInt());    // set the initial energy
		double randomAvgDemand = this.getEuse();
		System.out.println(randomAvgDemand);
		System.out.println(randomStream);
	}

}
