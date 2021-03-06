package de.zabuza.pathweaver.examples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import de.zabuza.pathweaver.network.Node;
import de.zabuza.pathweaver.network.algorithm.shortestpath.IShortestPathComputation;
import de.zabuza.pathweaver.network.algorithm.shortestpath.arcflag.ArcFlagShortestPathComputation;
import de.zabuza.pathweaver.network.algorithm.shortestpath.arcflag.OneAxisRectanglePartitioningProvider;
import de.zabuza.pathweaver.network.road.RoadNetwork;
import de.zabuza.pathweaver.network.road.RoadNode;
import de.zabuza.pathweaver.network.road.RoadUtil;

/**
 * Class which demonstrates the usage of reading an OSM-File into a road network
 * and then applying arc flag shortest path computation.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class ArcFlagExample {
	/**
	 * Demonstrates the usage of reading an OSM-File into a road network and
	 * then applying arc flag shortest path computation.
	 * 
	 * @param args
	 *            Not supported
	 * @throws IOException
	 *             If an I/O-Exception occurred
	 * @throws FileNotFoundException
	 *             If the given file was not found
	 */
	public static void main(final String[] args) throws FileNotFoundException, IOException {
		// Loading file
		System.out.println("Loading file...");
		final File osmFile = new File("res/examples/saarland.osm");

		// Creating road network
		System.out.println("Creating road network...");
		long startTimestamp = System.currentTimeMillis();
		final RoadNetwork network = RoadNetwork.createFromOsmFile(osmFile);
		long endTimestamp = System.currentTimeMillis();
		float durationSeconds = (endTimestamp - startTimestamp + 0.0f) / 1000;
		System.out.println("\tNodes: " + network.getSize() + ", Edges: " + network.getAmountOfEdges());
		System.out.println("\tTime needed: " + durationSeconds + " seconds");

		// Reducing to largest SCC
		System.out.println("Reducing to largest SCC...");
		startTimestamp = System.currentTimeMillis();
		network.reduceToLargestScc();
		endTimestamp = System.currentTimeMillis();
		durationSeconds = (endTimestamp - startTimestamp + 0.0f) / 1000;
		System.out.println("\tNodes: " + network.getSize() + ", Edges: " + network.getAmountOfEdges());
		System.out.println("\tTime needed: " + durationSeconds + " seconds");

		// Preparing random queries
		System.out.println("Preparing random queries...");
		startTimestamp = System.currentTimeMillis();
		final float latitudeMin = 49.20f;
		final float latitudeMax = 49.25f;
		final float longitudeMin = 6.95f;
		final float longitudeMax = 7.05f;
		final OneAxisRectanglePartitioningProvider provider = new OneAxisRectanglePartitioningProvider(network,
				latitudeMin, latitudeMax, longitudeMin, longitudeMax);
		final IShortestPathComputation computation = new ArcFlagShortestPathComputation(network, provider);
		final Object[] nodes = network.getNodes().toArray();
		final int amountOfNodes = nodes.length;
		final Random rnd = new Random();
		final int queryAmount = 100;
		final int logEvery = 5;
		long totalRunningTime = 0;
		double totalCost = 0.0;
		endTimestamp = System.currentTimeMillis();
		durationSeconds = (endTimestamp - startTimestamp + 0.0f) / 1000;
		System.out.println("\tNodes: " + network.getSize() + ", Edges: " + network.getAmountOfEdges());
		System.out.println("\tTime needed: " + durationSeconds + " seconds");

		// Starting random queries
		System.out.println("Starting random queries...");
		for (int i = 1; i <= queryAmount; i++) {
			final int sourceIndex = rnd.nextInt(amountOfNodes);
			final Node source = (Node) nodes[sourceIndex];

			// Only use destinations which are inside the rectangle
			Node destination;
			do {
				final int destinationIndex = rnd.nextInt(amountOfNodes);
				destination = (Node) nodes[destinationIndex];
			} while (!provider.isInsideRectangle((RoadNode) destination));

			startTimestamp = System.currentTimeMillis();
			final Optional<Float> result = computation.computeShortestPathCost(source, destination);
			// Ignore queries where source can not reach destination
			if (!result.isPresent()) {
				i--;
				continue;
			}
			totalCost += result.get().doubleValue();
			endTimestamp = System.currentTimeMillis();
			totalRunningTime += (endTimestamp - startTimestamp);

			if (i % logEvery == 0) {
				System.out.println("\t\tProcessed " + i + " queries.");
			}
		}
		final double averageCost = totalCost / queryAmount;
		final float averageTimeInSeconds = (totalRunningTime / queryAmount + 0.0f) / 1000;
		System.out.println("\tAverage cost: " + averageCost);
		System.out.println("\tAverage time: " + averageTimeInSeconds);

		// Starting search space query
		System.out.println("Starting search space query...");
		final int sourceIndex = rnd.nextInt(amountOfNodes);
		final Node source = (Node) nodes[sourceIndex];
		// Only use destinations which are inside the rectangle
		Node destination;
		do {
			final int destinationIndex = rnd.nextInt(amountOfNodes);
			destination = (Node) nodes[destinationIndex];
		} while (!provider.isInsideRectangle((RoadNode) destination));
		@SuppressWarnings("unchecked")
		final Set<RoadNode> searchSpace = (Set<RoadNode>) (Set<?>) computation.computeShortestPathSearchSpace(source,
				destination);
		// Save the search space to a file on the desktop
		final String tsvData = RoadUtil.getPositionsTsv(searchSpace);
		final File desktop = new File(System.getProperty("user.home"), "Desktop");
		final File searchSpaceFile = new File(desktop, "searchSpace.tsv");
		System.out.println("\tSaving to: " + searchSpaceFile);
		try (final BufferedWriter bw = new BufferedWriter(new FileWriter(searchSpaceFile))) {
			bw.write(tsvData);
		}
	}
}
