package distgen;

import javassist.bytecode.Descriptor.Iterator;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.engine.schedule.ScheduledMethod;
import distgen.Consumer;
import distgen.Generator;
import distgen.SimpleAgent;
//import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
	
/**
 * 
 * 
 * @author Jason Veneman
 * 
 */
public class ContextCreator implements ContextBuilder<SimpleAgent> {

//	private Iterable edges;

	/**
	 * Builds and returns a context. Building a context consists of filling it with
	 * agents, adding projects and so forth. When this is called for the master context
	 * the system will pass in a created context based on information given in the
	 * model.score file. When called for subcontexts, each subcontext that was added
	 * when the master context was built will be passed in.
	 *
	 * @param context
	 * @return the built context.
	 */


	public Context build(Context<SimpleAgent> context) {
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule(); //get current schedule
		ScheduleParameters params = ScheduleParameters.createOneTime(1);
		schedule.schedule(params, this, "step");


		// The environment parameters contain the user-editable values that appear in the GUI.
		//  Get the parameters p and then specifically the initial numbers of consumers and generators.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numConsumers = (Integer)p.getValue("initialNumConsumers");
		int numGenerators = (Integer)p.getValue("initialNumGenerators");
		double rewireProb = (Double)p.getValue("rewireProb");
		int edges = (Integer)p.getValue("edges");
//		int vertices = numConsumers + numGenerators;
//		int numEdgesToAttach = 3;
//		boolean directed = false;
//		boolean parallel = false;
//		int seed = 777;

		// Populate the root context with the initial agents
		// Iterate over the number of consumers
		for (int i = 0; i < numConsumers; i++) {
			Consumer consumer = new Consumer();             // create a new consumer
			context.add(consumer);                  // add the new consumer to the root context
		}
//		// Iterate over the number of generators
//		for (int i = 0; i < numGenerators; i++) {
//			Generator generator = new Generator();          // create a new generator
//			context.add(generator);                 // add a new generator to the root context
//		}
		
		NetworkGenerator gen = new WattsBetaSmallWorldGenerator(rewireProb, edges, false);			// build a small world network with x edges on each vertex and a rewire probability of x as defined in batch_params.xml
		NetworkBuilder builder = new NetworkBuilder("ConsumerNetwork", context, false);
		builder.setGenerator(gen);
		Network net = builder.buildNetwork();

		
//		java.util.Iterator<SimpleAgent> list = context.getObjects(Consumer.class).iterator();
	
		// If running in batch mode, tell the scheduler when to end each run.
		if (RunEnvironment.getInstance().isBatch()){

			double endAt = (Double)p.getValue("runlength");     
			RunEnvironment.getInstance().endAt(endAt);
		}
		return context;             
	}
	
	public void step(){
		RunEnvironment.getInstance().endAt(20);		// run model for 20 times
	}
}


