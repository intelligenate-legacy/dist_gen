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
import repast.simphony.essentials.RepastEssentials;
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
import repast.simphony.engine.schedule.ScheduledMethod;
import edu.uci.ics.jung.graph.Graph;

public class Consumer extends SimpleAgent{
	private double eUse = 0;			// electricity use
	private double ageOfEnergy = 0;		// age of energy system
	private String eSource;				// electricity source
	private double polSen = 0;			// sensitivity to pollution
	private double lNSprev = 0;			// calculated level of need satisfaction
	private double uncertainty = 0;		// difference between the expected LNS and the actual LNS
	private String consumerType;		// consumer type


	
   @Parameter (displayName = "eUse", usageName = "eUse")
   public double getEuse() {
       return eUse;
   }
   private void setEuse(double newValue) {
       eUse = newValue;
   }

   @Parameter (displayName = "eAge", usageName = "eAge")
   public double getAgeOfEnergy() {
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
   
   @Parameter (displayName = "polSen", usageName = "polSen")
   public double getPolSen() {
       return polSen;
   }
   private void setPolSen(double newValue) {
       polSen = newValue;
   }
   
   @Parameter (displayName = "lNSprev", usageName = "lNSprev")
   public double getLNSprev() {
       return lNSprev;
   }
   private void setLNSprev(double newValue) {
       lNSprev = newValue;
       System.out.println("lNSprev set to " + lNSprev);
   }
   
   @Parameter (displayName = "uncertainty", usageName = "uncertainty")
   public double getUncertainty() {
       return uncertainty;
   }
   private void setUncertainty(double newValue) {
       uncertainty = newValue;
   }
   
   @Parameter (displayName = "consumerType", usageName = "consumerType")
   public String getConsumerType() {
       return consumerType;
   }
   private void setConsumerType(String newValue) {
       consumerType = newValue;
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
		Parameters p = RunEnvironment.getInstance().getParameters();

		String eSource = (String)p.getValue("eSource");						// energy source
		double ageOfEnergy = (Double)p.getValue("ageOfEnergy");				// age of the consumers energy system
		double polSen = (Double)p.getValue("polSen");						// pollution sensitivity
		String consumerType = "nothing";									// Get the consumer type from the environment parameters
		double uncertainty = (Double)p.getValue("uncertainty");				// the consumers uncertainty surrounding 
		
		
		//setting the seed makes the random draws from the distributions the same for each agent
		//RandomHelper.setSeed(776);	//could also make this a parameter
		
		Uniform randomStreamUni = (Uniform) RandomHelper.createUniform(1.0,1000.0);
		int nextRandomInt = randomStreamUni.nextInt();
		
		consumerType = initConsumerType(nextRandomInt);					// initialize housing types to their US average (e.g. 65% single family detached homes, 15% apartments in a 5+ unit building, etc.)
		
		initDemand(consumerType);										// initialize the consumer types with average demands around a Poisson distribution for their housing types
		
		randomStreamUni = (Uniform) RandomHelper.createUniform(1.0,100.0);
		// initialize ageOfEnergy (which determines when a consumer can switch energy sources) around a uniform distribution
		// could change this to a distribution around the actual age of housing stock
		this.setEage(randomStreamUni.nextInt()/10);    // set the age of the consumer's energy system between 1 and 10 years
		double randomAgeOfEnergy = this.getAgeOfEnergy();
		System.out.println("age of energy " + randomAgeOfEnergy);
		
		// initialize electricity source, based on current (2010) choice of energy supplier grid is used by 98% and distributed generation is used by 2% 
		if (randomStreamUni.nextInt() > 2){
			this.setESource("grid");
		}
		else{
			this.setESource("other");
		}
		System.out.println("Energy source set to " + this.getESource());
		
		//set the pollution sensitivity
		this.setPolSen(polSen);
		
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
			Parameters p = RunEnvironment.getInstance().getParameters();
//			Network network = FindNetwork("ConsumerNetwork");				// this line was used to show the network graph but it really slows down the run and when it's in the consumers never switch off of the grid
			
			double lNSmin = (Double)p.getValue("lNSmin");
			double uncertaintyMax = (Double)p.getValue("uncertaintyMax");

			double eAge = this.getAgeOfEnergy();
			
			String eSource = this.getESource();
			String consumerType = this.getConsumerType();
			
			
//			String eSource = this.getESource();
			
			double lNS = this.calcLNS();
			double uncertainty = this.calcUncertainty();
			setUncertainty(uncertainty);
			setLNSprev(lNS);
			
			if ((eAge >= 10) && (lNS < lNSmin) && (uncertainty < uncertaintyMax)){
				System.out.println("Deliberating");
				this.goDeliberate(eSource);
			}
			
			else if ((eAge >= 10) && (lNS < lNSmin) && (uncertainty > uncertaintyMax)){
				System.out.println("Comparing");
				this.goCompare(eSource, consumerType);
			}
			
			else if ((eAge >= 10) && (lNS > lNSmin) && (uncertainty < uncertaintyMax)){
				System.out.println("Repeating");
				this.goRepeat(eSource);
			}
			
			else if ((eAge >= 10) && (lNS > lNSmin) && (uncertainty > uncertaintyMax)){
				System.out.println("Imitating");
				this.goImitate(eSource);
			}
			
			else {
				eAge = eAge + 1;
				this.setEage(eAge);
				System.out.println("Age of energy is: " + this.getAgeOfEnergy());
			}
			
			initDemand(consumerType);
									
//			ORANetWriter writer = new ORANetWriter();
			 
//			writer.save(network.getName(), (Graph) network, "ConNetwork");
			
	}
	
	private String initConsumerType(int nextRandomInt) {
		if (nextRandomInt <= 649){		// set the consumer type
			this.setConsumerType("singleFamDetach");
			System.out.println("Consumer type set to singleFamDetach");
			System.out.println("Consumer type is actually: " + this.getConsumerType());
			return this.getConsumerType();
		}
		else if (nextRandomInt > 649 && nextRandomInt <= 717){		// set the consumer type based on housing type distribution in the US
			this.setConsumerType("singleFamAttach");
			System.out.println("Consumer type set to singleFamAttach");
			System.out.println("Consumer type is actually: " + this.getConsumerType());
			return this.getConsumerType();
		}
		else if (nextRandomInt > 717 && nextRandomInt <= 788){		// set the consumer type
			this.setConsumerType("appt2_4");
			System.out.println("Consumer type set to appt2_4");
			System.out.println("Consumer type is actually: " + this.getConsumerType());
			return this.getConsumerType();
		}
		else if (nextRandomInt > 788 && nextRandomInt <= 938){		// set the consumer type
			this.setConsumerType("appt5");
			System.out.println("Consumer type set to appt5");
			System.out.println("Consumer type is actually: " + this.getConsumerType());
			return this.getConsumerType();
		}
		else if (nextRandomInt > 938 && nextRandomInt <= 1000){		// set the consumer type
			this.setConsumerType("mobile");
			System.out.println("Consumer type set to mobile");
			System.out.println("Consumer type is actually: " + this.getConsumerType());
			return this.getConsumerType();
		}
		else {
			System.out.println("Error in setting consumer type!");
			return this.getConsumerType();
		}
	}
	
	private void initDemand(String consumerType) {
		Parameters p = RunEnvironment.getInstance().getParameters();

		double initAvgDemandSingleFamDetach = (Double)p.getValue("avgDemandSingleFamDetach");		// average electricity demand for a single family detached home
		double initAvgDemandSingleFamAttach = (Double)p.getValue("avgDemandSingleFamAttach");		// average electricity demand for a single family attached home
		double initAvgDemandAppt2_4 = (Double)p.getValue("avgDemandAppt2_4");			// average electricity demand for an apartment with 2-4 units
		double initAvgDemandAppt5 = (Double)p.getValue("avgDemandAppt5");		// average electricity demand for an apartment with 5+ units
		double initAvgDemandMobile = (Double)p.getValue("avgDemandMobile");		// average electricity demand for a mobile home
		int tickCount = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); // gets the tickCount and casts it to an integer
		double demandMultRef = (Double)p.getValue("demandMultRef" + tickCount);			// 
		double avgDemandSingleFamDetach = initAvgDemandSingleFamDetach * demandMultRef;
		double avgDemandSingleFamAttach = initAvgDemandSingleFamAttach * demandMultRef;
		double avgDemandAppt2_4 = initAvgDemandAppt2_4 * demandMultRef;
		double avgDemandAppt5 = initAvgDemandAppt5 * demandMultRef;
		double avgDemandMobile = initAvgDemandMobile * demandMultRef;
		
		if (consumerType.equals("singleFamDetach")){
			// initialize demand around a Poisson distribution
			Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemandSingleFamDetach);
			
			this.setEuse(randomStream.nextInt());    // set the initial energy demand
			double randomAvgDemand = this.getEuse();
			System.out.println("random avg demand for single family detached home " + randomAvgDemand + " - next int " + randomStream.nextInt());
		}
		else if (consumerType.equals("singleFamAttach")){										// todo: add other housing type energy demands	
			// initialize demand around a Poisson distribution
			Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemandSingleFamAttach);
			
			this.setEuse(randomStream.nextInt());    // set the initial energy demand
			double randomAvgDemand = this.getEuse();
			System.out.println("random avg demand for single family attached home " + randomAvgDemand + " - next int " + randomStream.nextInt());
		}
		else if (consumerType.equals("appt2_4")){										// todo: add other housing type energy demands	
			// initialize demand around a Poisson distribution
			Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemandAppt2_4);
			
			this.setEuse(randomStream.nextInt());    // set the initial energy demand
			double randomAvgDemand = this.getEuse();
			System.out.println("random avg demand for an appartment in a 2-4 unit building " + randomAvgDemand + " - next int " + randomStream.nextInt());
		}
		else if (consumerType.equals("appt5")){										// todo: add other housing type energy demands	
			// initialize demand around a Poisson distribution
			Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemandAppt5);
			
			this.setEuse(randomStream.nextInt());    // set the initial energy demand
			double randomAvgDemand = this.getEuse();
			System.out.println("random avg demand for an appartment in a 5+ unit building " + randomAvgDemand + " - next int " + randomStream.nextInt());
		}
		else if (consumerType.equals("mobile")){										// todo: add other housing type energy demands	
			// initialize demand around a Poisson distribution
			Poisson randomStream = (Poisson) RandomHelper.createPoisson(avgDemandMobile);
			
			this.setEuse(randomStream.nextInt());    // set the initial energy demand
			double randomAvgDemand = this.getEuse();
			System.out.println("random avg demand for an appartment in a mobile home " + randomAvgDemand + " - next int " + randomStream.nextInt());
		}
		else {
			System.out.println("Error in setting average demand!");
			System.out.println("Consumer type is: " + consumerType);
		}
	}

	private double getLNS1() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numConnections = (Integer)p.getValue("numConnections") + 1;
		double lNS1 = 0; 	// lNS1 is the level of need satisfaction based on identity and determined by the number of neighbors that consume the same product
		String grid = "grid";
		String other = "other";
		String eSource = this.getESource();
		double gridFriends = this.getGridFriendsCount() + 0.1;
		double otherFriends = this.getOtherFriendsCount() + 0.1;
		
		if(eSource.equals(grid)){
			lNS1 = gridFriends/numConnections;
			System.out.println("LNS1 grid is: " + lNS1);
		}
		else if (eSource.equals(other)){
			lNS1 = otherFriends/numConnections;		//not really sure if this is the right way to do LNS1, the Jager paper is unclear
			System.out.println("LNS1 other is:" + lNS1);
		}
		else{
			System.out.println("Error in LNS1 calculation!");
		}
		
		return lNS1;
	}
	
	private double getLNS2() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		double tasteGrid = (Double)p.getValue("personalTasteGrid");
		double tasteOther = (Double)p.getValue("personalTasteOther");
		double lNS2 = 0; 	// lNS2 is the level of need satisfaction based on personal taste, not really sure how to do this one...
		String grid = "grid";
		String other = "other";
		String eSource = this.getESource();
		
		if(eSource.equals(grid)){
			lNS2 = tasteGrid;
		}
		else{
			lNS2 = tasteOther;		
		}
		System.out.println("LNS2 is " + lNS2);
		return lNS2;
	}
	
	private double getLNS3() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		double budget = (Double)p.getValue("budget");
		double lNS3 = 0; 	// lNS3 is the level of need satisfaction based on leisure (i.e. cost). A cheaper product means more leisure time.
		String grid = "grid";
		String other = "other";
		String eSource = this.getESource();
		double eDemand = this.getEuse();
		double energyCostGrid = Generator.getEnergyCost(eDemand, "grid");
		double energyCostOther = Generator.getEnergyCost(eDemand, "other");
		
		if(eSource.equals(grid)){
			budget = energyCostGrid;
			if( energyCostOther > energyCostGrid){
				lNS3 = energyCostGrid/budget;		// lNS3 in this case is always equal to one and the consumer is fully satisfied since his energy source costs less than the other source
				System.out.println("LNS3 is " + lNS3);
				return lNS3;
			}
			else {
				lNS3 = energyCostOther/budget;		// the consumer becomes more unsatisfied with the greater the difference between what he/she is paying and what the other source costs
				System.out.println("LNS3 is " + lNS3);
				return lNS3;
			}
		}
		else if (eSource.equals(other)){
			budget = energyCostOther;
			if( energyCostGrid > energyCostOther){
				lNS3 = energyCostOther/budget;		// lNS3 in this case is always equal to one and the consumer is fully satisfied since his energy source costs less than the other source
				System.out.println("LNS3 is " + lNS3);
				return lNS3;
			}
			else {
				lNS3 = energyCostGrid/budget;		// the consumer becomes more unsatisfied with the greater the difference between what he is paying and what the other source costs
				System.out.println("LNS3 is " + lNS3);
				return lNS3;
			}
		}
		else{
			System.out.println("LNS3 calculation failed!");
			return lNS3;
		}
	}
	
	private double getLNS4() {
		Parameters p = RunEnvironment.getInstance().getParameters();
		double polSen = (Double)p.getValue("polSen");
		double lNS4 = 0; 	// lNS4 is the level of need satisfaction based on sensitivity to pollution
		String grid = "grid";
		String other = "other";
		String eSource = this.getESource();
		double eDemand = this.getEuse();
		double carbonOutGrid = Generator.getCO2Output(eDemand, "grid");
		double carbonOutOther = Generator.getCO2Output(eDemand, "other");

		
		if(eSource.equals(grid)){
			lNS4 = polSen/carbonOutGrid;
			System.out.println("LNS4 grid is " + lNS4);
			return lNS4;
		}
		else if(eSource.equals(other)){
			lNS4 = polSen/carbonOutOther;
			System.out.println("LNS4 other is " + lNS4);
			return lNS4;
		}
		else{
			System.out.println("LNS4 calculation failed!");
			return lNS4;
		}
		
	}
	
	private double calcLNS(){
		Parameters p = RunEnvironment.getInstance().getParameters();
		double gamma1 = (Double)p.getValue("gamma1");
		double gamma2 = (Double)p.getValue("gamma2");
		double gamma3 = (Double)p.getValue("gamma3");
		double gamma4 = 1-gamma1-gamma2-gamma3;
		double lNS1 = getLNS1();
		double lNS2 = getLNS2();
		double lNS3 = getLNS3();
		double lNS4 = getLNS4();
		double lNS = 0;
		
		lNS = pow(lNS1,gamma1) * pow(lNS2,gamma2) * pow(lNS3,gamma3) * pow(lNS4,gamma4);
		System.out.println("LNS is: " + lNS);
//		this.setLNSprev(lNS);
		return lNS;
		
	}
	
	private double calcUncertainty(){
		Parameters p = RunEnvironment.getInstance().getParameters();
		double lNSInit = (Double)p.getValue("lNSinit");
//		double lNSPrev = (Double)p.getValue("lNSPrev");
		double lNSprev = this.getLNSprev();
		System.out.println("lNSprev is " + lNSprev);

		double lNS = this.calcLNS();
		double uncertainty = 0;
		int tickCount = (int) RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if(tickCount == 1){
			uncertainty = abs(lNS - lNSInit);
			System.out.println("Initial uncertainty is " + uncertainty);
			return uncertainty;
		}
		else{
			uncertainty = abs(lNS - lNSprev);
			System.out.println("Uncertainty is " + uncertainty);
			return uncertainty;
		}
		
	}
	
	private void goDeliberate(String eSource){
		System.out.println("In deliberation");
		double lNS1 = getLNS1();
		double lNS2 = getLNS2();
		double lNS3 = getLNS3();
		double lNS4 = getLNS4();
		Parameters p = RunEnvironment.getInstance().getParameters();
		double tasteGrid = (Double)p.getValue("personalTasteGrid");
		double tasteOther = (Double)p.getValue("personalTasteOther");
		
		if (lNS1 > lNS2 && lNS1 > lNS3 && lNS1 > lNS4){
			if (this.getGridFriendsCount() > this.getOtherFriendsCount()){
				this.setESource("grid");
				this.setEage(0);
				System.out.println("Deliberating, LNS1 is greatest, choosing grid");
			}
			else if (this.getOtherFriendsCount() > this.getGridFriendsCount()){
				this.setESource("other");
				this.setEage(0);
				System.out.println("Deliberating, LNS1 is greatest, choosing other");
			}
			else {
				System.out.println("Error in deliberation 1");
			}
			
		}
		else if (lNS2 > lNS1 && lNS2 > lNS3 && lNS2 > lNS4){
			if (tasteGrid > tasteOther){
				this.setESource("grid");
				this.setEage(0);
				System.out.println("Deliberating, LNS2 is greatest, choosing grid");
			}
			else if (tasteOther > tasteGrid){
				this.setESource("other");
				this.setEage(0);
				System.out.println("Deliberating, LNS2 is greatest, choosing other");
			}
			else {
				this.setEage(0);
				System.out.println("Deliberating, LNS2 is greatest, choosing the same as before: " + eSource);
			}
		}
		else if (lNS3 > lNS1 && lNS3 > lNS2 && lNS3 > lNS4){
			double energyCostGrid = Generator.getEnergyCost(getEuse(), "grid");
			double energyCostOther = Generator.getEnergyCost(getEuse(), "other");
			
			if (energyCostGrid > energyCostOther){
				this.setESource("other");
				this.setEage(0);
				System.out.println("Deliberating, LNS3 is greatest, choosing other");
			}
			else if (energyCostOther > energyCostGrid){
				this.setESource("grid");
				this.setEage(0);
				System.out.println("Deliberating, LNS3 is greatest, choosing grid");
			}
			else {
				System.out.println("Error in deliberation 3");
			}
		}
		else if (lNS4 > lNS1 && lNS4 > lNS2 && lNS4 > lNS3){
			double eDemand = this.getEuse();
			double carbonOutGrid = Generator.getCO2Output(eDemand, "grid");
			double carbonOutOther = Generator.getCO2Output(eDemand, "other");
			if (carbonOutGrid > carbonOutOther){
				this.setESource("other");
				this.setEage(0);
				System.out.println("Deliberating, LNS4 is greatest, choosing other");
			}
			else if (carbonOutOther > carbonOutGrid){
				this.setESource("grid");
				this.setEage(0);
				System.out.println("Deliberating, LNS4 is greatest, choosing grid");
			}
			else {
				System.out.println("Error in deliberation 4");
			}
			
		}
		else{
			System.out.println("LNS1 is " + lNS1 + "LNS2 is " + lNS2 + "LNS3 is " + lNS3 + "LNS4 is " + lNS4);
			this.setEage(0);
		}
		
		
	}

	private void goCompare(String eSource, String consumerType){
		System.out.println("In Comparison");
		Parameters p = RunEnvironment.getInstance().getParameters();
//		double abilityTolerance = (Double)p.getValue("abilityTolerance");
		String neighborsEnergyChoice = this.neighborsEnergyChoice(consumerType);
		
		
		this.setESource(neighborsEnergyChoice);
		eSource = this.getESource();
		this.setEage(0);
		System.out.println("Comparing, new source is the same as the neighbors: " + eSource);

	}

	private void goRepeat(String eSource){
		// when satisfied but not uncertain the agent chooses the same source as last time
		System.out.println("In Repetition");
		this.setESource(eSource);
		this.setEage(0);
		System.out.println("Repeating, new source is the same as before " + eSource);
	}

	private void goImitate(String eSource){
		// when satisfied but uncertain the agent imitates what their neighbors are using
		System.out.println("In imitation");
		Parameters p = RunEnvironment.getInstance().getParameters();
		double imitateRatio = (Double)p.getValue("imitateRatio");
		double gridFriends = this.getGridFriendsCount() + 1;
		double otherFriends = this.getOtherFriendsCount() + 1;
		
		if((gridFriends/otherFriends) > imitateRatio){
			this.setESource("grid");
			this.setEage(0);
			System.out.println("Imitating, new source is grid");
		}
		else if ((otherFriends/gridFriends) > imitateRatio){
			this.setESource("other");
			this.setEage(0);
			System.out.println("Imitating, new source is other");
		}
		else{
			this.setESource(eSource);
			this.setEage(0);
			System.out.println("Imitating, new source is the same as before: " + eSource);
		}
	}
	
	
	public int getGridFriendsCount(){

		// Get the context in which the agent is residing
		Context context = (Context) ContextUtils.getContext (this);

		// Get the network projection from the context
		Network friends = (Network)context.getProjection("ConsumerNetwork");

		Iterator<Consumer> iterator = friends.getSuccessors(this).iterator();
		int totalGridFriends = 0;
		while (iterator.hasNext()) {
			if (((Consumer)iterator.next()).getESource() == "grid")
				totalGridFriends++;
		}	
		System.out.println("Total grid friends is " + totalGridFriends);
		return totalGridFriends;
	}
	
	public int getOtherFriendsCount(){

		// Get the context in which the agent is residing
		Context context = (Context) ContextUtils.getContext (this);

		// Get the network projection from the context
		Network friends = (Network)context.getProjection("ConsumerNetwork");

		Iterator<Consumer> iterator = friends.getSuccessors(this).iterator();
		int totalOtherFriends = 0;
		while (iterator.hasNext()) {
			if (((Consumer)iterator.next()).getESource() == "other")
				totalOtherFriends++;
		}	
		System.out.println("Total other friends is " + totalOtherFriends);
		return totalOtherFriends;
	}
	
	private String neighborsEnergyChoice(String consumerType){
		// Get the context in which the agent is residing
		Context context = (Context) ContextUtils.getContext (this);

		// Get the network projection from the context
		Network friends = (Network)context.getProjection("ConsumerNetwork");

		Iterator<Consumer> iterator = friends.getSuccessors(this).iterator();
		int totalGridFriends = 0;
		int totalOtherFriends = 0;
		String neighborsChoice = "none";
		while (iterator.hasNext()) {
			Consumer thisConsumer = (Consumer)iterator.next();
			if (thisConsumer.getConsumerType() == "singleFam" && thisConsumer.getESource() == "grid"){ 	// check which neighbors are of the same type and count their energy choice
				totalGridFriends++;
			}
			else if (thisConsumer.getConsumerType() == "singleFam" && thisConsumer.getESource() == "other"){
				totalOtherFriends++;
			}
			else {
				System.out.println("Error in computing neighborsEnergyChoice 1");
			}
			if (totalGridFriends > totalOtherFriends){
				neighborsChoice = "grid";
			}
			else if (totalOtherFriends > totalGridFriends){
				neighborsChoice = "other";
			}
			else{
				System.out.println("Error in computing neighborsEnergyChoice 2");
			}
		}	
		System.out.println("Neighbors energy choice is mostly " + neighborsChoice);
		return neighborsChoice;
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
	
	// Public getter for the data gatherer for summing total energy cost
	public double getTotalEnergyCost(){
		return Generator.getEnergyCost(getEuse(), getESource());
	}
	
	// Public getter for the data gatherer for summing total CO2 output
	public double getTotalCO2Output(){
		return Generator.getCO2Output(getEuse(), getESource());
	}
	
	// Public getter for the data gatherer for summing total LNS
	public double getTotalLNS(){
		return this.calcLNS();
	}
	
}
