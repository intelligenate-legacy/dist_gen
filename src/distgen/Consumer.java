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
//	private int consumerId; 		// The consumer's ID
	private double eUse = 0;			// electricity use
	private double gUseHeat;		// gas use for heat
	private double gUseElec;		// gas use for electricity
	private String eSource;			// electricity source
	private String heatSource;		// heat source
	private double eCost;			// electricity cost
	private double eToSell;			// electricity to sell
	//private String consumerType;	// consumer type
	private String housingType;		// housing type
	
	/**
    *
    * This is an agent property.
    * @field energy
    *
    */
   @Parameter (displayName = "eUse", usageName = "eUse")
   private double getEuse() {
       return eUse;
   }
   private void setEuse(double newValue) {
       eUse = newValue;
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

    // This constructor is used to create additional consumers
	public Consumer (double energy){
		this.setEuse(energy);               // assign the offspring energy
	}

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

    /**
     *
     * This is the step behavior.
     * @method step
     *
     */

}
