package distgen;

import javassist.bytecode.Descriptor.Iterator;
import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.valueLayer.GridValueLayer;
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

	private Iterable edges;

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
//		int xdim = 50;   // The x dimension of the physical space
//		int ydim = 50;   // The y dimension of the physical space
//
//		// Create a new 2D grid to model the discrete patches of grass.  The inputs to the
//		// GridFactory include the grid name, the context in which to place the grid,
//		// and the grid parameters.  Grid parameters include the border specification,
//		// random adder for populating the grid with agents, boolean for multiple occupancy,
//		// and the dimensions of the grid.
//		GridFactoryFinder.createGridFactory(null).createGrid("Simple Grid", context,
//				new GridBuilderParameters<SimpleAgent>(new repast.simphony.space.grid.WrapAroundBorders(),
//						new RandomGridAdder<SimpleAgent>(), true, xdim, ydim));
//
////		 Create a new 2D continuous space to model the physical space on which the sheep
////		 and wolves will move.  The inputs to the Space Factory include the space name, 
////		 the context in which to place the space, border specification,
////		 random adder for populating the grid with agents,
////		 and the dimensions of the grid.
//		ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null)
//		.createContinuousSpace("Continuous Space", context, new RandomCartesianAdder<SimpleAgent>(),
//				new repast.simphony.space.continuous.WrapAroundBorders(), xdim, ydim, 1);
//
////		 Create a new 2D value layer to store the state of the city.  This is
////		 only used for visualization since it's faster to draw the value layer
////		 in 2D displays compared with rendering each city space as an agent.
//		GridValueLayer vl = new GridValueLayer("City", true, 
//				new repast.simphony.space.grid.WrapAroundBorders(),xdim,ydim);
//
//		context.addValueLayer(vl);

		// The environment parameters contain the user-editable values that appear in the GUI.
		//  Get the parameters p and then specifically the initial numbers of consumers and generators.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numConsumers = (Integer)p.getValue("initialNumConsumers");
		int numGenerators = (Integer)p.getValue("initialNumGenerators");
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
		// Iterate over the number of generators
		for (int i = 0; i < numGenerators; i++) {
			Generator generator = new Generator();          // create a new generator
			context.add(generator);                 // add a new generator to the root context
		}
		
		//BarabasiAlbertGenerator gen = new BarabasiAlbertGenerator(25, 3); //, directed, parallel, seed);
		NetworkGenerator gen = new WattsBetaSmallWorldGenerator(0.2, 6, false);
		NetworkBuilder builder = new NetworkBuilder("ConsumerNetwork", context, false);
		builder.setGenerator(gen);
		Network net = builder.buildNetwork();

		
//		java.util.Iterator<SimpleAgent> list = context.getObjects(Consumer.class).iterator();
//	
//
//        // Loop over all Sheep
//        while (list.hasNext()) {
//
//        	Iterable<SimpleAgent> neighbors = context.getRandomObjects(Consumer.class, 2);
//            Consumer consumer = (Consumer) list.next();
//            edges = Network.getEdges(consumer);
//
//        }

		// If running in batch mode, tell the scheduler when to end each run.
		if (RunEnvironment.getInstance().isBatch()){

			double endAt = (Double)p.getValue("runlength");     
			RunEnvironment.getInstance().endAt(endAt);
		}
		return context;             
	}
}


