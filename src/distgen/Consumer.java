package distgen;

import repast.simphony.annotate.AgentAnnot;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import java.io.*;
import java.math.*;
import java.util.*;
import javax.measure.unit.*;
import org.jscience.mathematics.number.*;
import org.jscience.mathematics.vector.*;
import org.jscience.physics.amount.*;

import cern.jet.random.Poisson;
import cern.jet.random.Uniform;
import repast.simphony.adaptation.neural.*;
import repast.simphony.adaptation.regression.*;
import repast.simphony.context.*;
import repast.simphony.context.space.continuous.*;
import repast.simphony.context.space.gis.*;
import repast.simphony.context.space.graph.*;
import repast.simphony.context.space.grid.*;
import repast.simphony.engine.environment.*;
import repast.simphony.engine.schedule.*;
import repast.simphony.engine.watcher.*;
import repast.simphony.groovy.math.*;
import repast.simphony.integration.*;
import repast.simphony.matlab.link.*;
import repast.simphony.query.*;
import repast.simphony.query.space.continuous.*;
import repast.simphony.query.space.gis.*;
import repast.simphony.query.space.graph.*;
import repast.simphony.query.space.grid.*;
import repast.simphony.query.space.projection.*;
import repast.simphony.parameter.*;
import repast.simphony.random.*;
import repast.simphony.space.continuous.*;
import repast.simphony.space.gis.*;
import repast.simphony.space.graph.*;
import repast.simphony.space.grid.*;
import repast.simphony.space.projection.*;
import repast.simphony.ui.probe.*;
import repast.simphony.util.*;
import simphony.util.messages.*;
import static java.lang.Math.*;
import static repast.simphony.essentials.RepastEssentials.*;
import repast.simphony.query.space.grid.GridWithin;
import repast.simphony.space.graph.Network;
import repast.simphony.query.space.graph.NetworkAdjacent;

public class Consumer extends SimpleAgent{
	private double eUse = 0;		// electricity use
	private double ageOfEnergy = 0;		// age of energy system
	private String eSource;			// electricity source
	//private String heatSource;		// heat source
	//private String consumerType;	// consumer type
	//private String housingType;		// housing type
	
	/**
    *
    * This is an agent property.
    * @field energy
    *
    */
   @Parameter (displayName = "eUse", usageName = "eUse")
   public double getEuse() {
       return eUse;
   }
   private void setEuse(double newValue) {
       eUse = newValue;
   }

   @Parameter (displayName = "eAge", usageName = "eAge")
   private double getAgeOfEnergy() {
       return ageOfEnergy;
   }
   private void setEage(double newValue) {
       ageOfEnergy = newValue;
   }
   
   @Parameter (displayName = "eSource", usageName = "eSource")
   public String getESource() {
       return eSource;
   }
   private void setESource(String newValue) {
       eSource = newValue;
   }
    /**
     *
     * This value is used to automatically generate agent identifiers.
     * @field serialVersionUID
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     * This value is used to automatically generate agent identifiers.
     * @field agentIDCounter
     *
     */
    protected static long agentIDCounter = 1;

    /**
     *
     * This value is the agent's identifier.
     * @field agentID
     *
     */
    protected String agentID = "Consumer " + (agentIDCounter++);

    /**
     *
     * This is the step behavior.
     * @method initialize
     *
     */


	// This constructor is used to create initial consumers from the context creator
	public Consumer(){
		// Get the consumer type from the environment parameters
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double consumerType = (Double)p.getValue("consumerType");
		int housingType = (Integer)p.getValue("housingType");
		double avgDemand = (Double)p.getValue("avgDemand");
		String eSource = (String)p.getValue("eSource");
//		String heatSource = (String)p.getValue("heatSource");
//		double eCost = (Double)p.getValue("eCost");
		double ageOfEnergy = (Double)p.getValue("ageOfEnergy");
		double eCostGrid = (Double)p.getValue("eCostGrid");
		double eCostOther = (Double)p.getValue("eCostOther");
		
		//setting the seed makes the random pulls from the distributions the same for each agent
		//RandomHelper.setSeed(776);	//could also make this a parameter
		
		// initialize demand around a Poisson distribution
		Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemand);
		
		this.setEuse(randomStream.nextInt());    // set the initial energy demand
		double randomAvgDemand = this.getEuse();
		System.out.println("random avg demand " + randomAvgDemand + " - next int " + randomStream.nextInt());
		//System.out.println(randomStream);
		
		// initialize ageOfEnergy (which determines when a consumer can switch energy sources) around a uniform distribution
		// could change this to a distribution around the actual age of housing stock
		Uniform randomStreamUni = (Uniform) RandomHelper.createUniform(1.0,100.0);
		//System.out.println("first age of energy " + randomStreamUni.nextInt());
		this.setEage(randomStreamUni.nextInt()/10);    // set the age of the consumer's energy system between 1 and 10 years
		//System.out.println("random age of energy set to " + this.getAgeOfEnergy());
		double randomAgeOfEnergy = this.getAgeOfEnergy();
		System.out.println("age of energy " + randomAgeOfEnergy);
		//System.out.println(randomStreamUni);
		
		// initialize electricity source, based on current (2010) choice of energy supplier grid is used by 98% and distributed generation is used by 2% 
		if (randomStreamUni.nextInt() > 2){
			this.setESource("grid");
		}
		else{
			this.setESource("other");
		}
		System.out.println("Energy source set to " + this.getESource());
		
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
			
			double eAge = this.getAgeOfEnergy();
			System.out.println("energy age pre " + this.getAgeOfEnergy());
			if (eAge >= 10){						// consumers are able to change their energy source after 10 years
				this.chooseNewEnergy();
			}
			else {
				eAge++;
				this.setEage(eAge); // increment the age of the energy source
			}
			System.out.println("energy age post " + this.getAgeOfEnergy());
	}
	
	private void chooseNewEnergy() {
		String grid = "grid";
		String other = "other";
		System.out.println("Choosing new energy, original energy source was " + getESource());
		if(getESource().equals(grid)){
			if(Generator.getEnergyCost(getEuse(), "other") > Generator.getEnergyCost(getEuse(), "grid"))	{	
				setESource("grid");						// set to grid since it's cheaper
				this.setEage(0); 						// set the age of the energy source back to zero
				System.out.println("New source is grid");
			}
			else{
				setESource("other");
				this.setEage(0); 						// set the age of the energy source back to zero
				System.out.println("New source is other");
			}
		}
		else if(getESource().equals(other)){
			if(Generator.getEnergyCost(getEuse(), "grid") > Generator.getEnergyCost(getEuse(), "other"))	{	
				setESource("other");					// set to other since it's cheaper
				this.setEage(0); 						// set the age of the energy source back to zero
				System.out.println("New source is other");
			}
			else{
				setESource("grid");
				this.setEage(0); 						// set the age of the energy source back to zero
				System.out.println("New source is grid");
			}
		}
	}
	  // Public getter for the data gatherer for counting 
	@Override
	public int isGrid() {
		String grid = "grid";
		if(getESource().equals(grid)){
			return 1;
		}
		else{
			return 0;	
		}
	}
	  // Public getter for the data gatherer for counting 
	@Override
	public int isOther() {
		String other = "other";
		if(getESource().equals(other)){
			return 1;
		}
		else{
			return 0;	
		}
	}
	
	public double totalEnergyCost(){
		return Generator.getEnergyCost(getEuse(), getESource());
	}
	
}
