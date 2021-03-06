package de.zabuza.pathweaver.examples.webdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.StringJoiner;

import de.zabuza.pathweaver.network.DirectedWeightedEdge;
import de.zabuza.pathweaver.network.Path;
import de.zabuza.pathweaver.network.algorithm.metric.landmark.LandmarkMetric;
import de.zabuza.pathweaver.network.algorithm.metric.landmark.RandomLandmarkProvider;
import de.zabuza.pathweaver.network.algorithm.shortestpath.AStarShortestPathComputation;
import de.zabuza.pathweaver.network.algorithm.shortestpath.IShortestPathComputation;
import de.zabuza.pathweaver.network.road.RoadNetwork;
import de.zabuza.pathweaver.network.road.RoadNode;

/**
 * Demo application which shows how the API can be used as the server of a web
 * application which solves shortest path computation tasks.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class WebDemoServer {
	/**
	 * The default port to use.
	 */
	private static final int DEFAULT_PORT = 8888;
	/**
	 * The HTTP GET-parameter that contains the request data.
	 */
	private static final String GET_REQUEST = "request=";
	/**
	 * The text used in HTTP GET requests to separate parameters.
	 */
	private static final String GET_SEPARATOR = "&";
	/**
	 * Text used in Java-Script to indicate the begin of an array.
	 */
	private static final String JS_ARRAY_BEGIN = "[";
	/**
	 * Delimiter used in Java-Script to separate entries in an array.
	 */
	private static final String JS_ARRAY_DELIMITER = ", ";
	/**
	 * Text used in Java-Script to indicate the end of an array.
	 */
	private static final String JS_ARRAY_END = "]";
	/**
	 * The name of the key that contains the destination latitude in the request
	 * data.
	 */
	private static final String KEY_DESTINATION_LATITUDE = "dLat";
	/**
	 * The name of the key that contains the destination longitude in the
	 * request data.
	 */
	private static final String KEY_DESTINATION_LONGITUDE = "dLng";
	/**
	 * The text used to separate different keys in the request data.
	 */
	private static final String KEY_SEPARATOR = ",";
	/**
	 * The name of the key that contains the source longitude in the request
	 * data.
	 */
	private static final String KEY_SORUCE_LONGITUDE = "sLng";
	/**
	 * The name of the key that contains the source latitude in the request
	 * data.
	 */
	private static final String KEY_SOURCE_LATITUDE = "sLat";
	/**
	 * The text used to separate key and value pairs in the request data.
	 */
	private static final String KEY_VALUE_SEPARATOR = ":";

	/**
	 * Starts the server of the web application.
	 * 
	 * @param args
	 *            Not supported
	 * @throws IOException
	 *             If an I/O-exception occurred
	 */
	public static void main(final String[] args) throws IOException {
		final WebDemoServer server = new WebDemoServer(DEFAULT_PORT);
		server.runService();
	}

	/**
	 * The computation object to use for solving shortest path tasks.
	 */
	private final IShortestPathComputation mComputation;
	/**
	 * The road network to compute the tasks on.
	 */
	private final RoadNetwork mNetwork;
	/**
	 * The port to listen at.
	 */
	private final int mPort;
	/**
	 * The ID of the current request the server is processing.
	 */
	private int mRequestId;
	/**
	 * The server socket used to listen for requests.
	 */
	private final ServerSocket mServerSocket;

	/**
	 * Creates a new server for the web application which listens at the given
	 * port for requests and solves them.
	 * 
	 * @param port
	 *            Port to use for communication
	 * @throws IOException
	 *             If an I/O-exception occurred
	 */
	public WebDemoServer(final int port) throws IOException {
		this.mPort = port;
		this.mRequestId = 0;
		this.mServerSocket = new ServerSocket(this.mPort);

		System.out.println("Initializing service...");
		// Loading road network from file
		System.out.println("\tLoading file...");
		final File osmFile = new File("res/examples/saarland.osm");
		// Creating road network
		System.out.println("\tCreating road network...");
		this.mNetwork = RoadNetwork.createFromOsmFile(osmFile);
		// Reducing to largest SCC
		System.out.println("\tReducing to largest SCC...");
		this.mNetwork.reduceToLargestScc();

		// Preparing algorithms
		System.out.println("\tPreparing A-Star (Landmark, random)...");
		this.mComputation = new AStarShortestPathComputation(this.mNetwork,
				new LandmarkMetric(42, this.mNetwork, new RandomLandmarkProvider(this.mNetwork)));
	}

	/**
	 * Runs the service in an infinite loop.
	 * 
	 * @throws IOException
	 *             If an I/O-exception occurred
	 */
	public void runService() throws IOException {
		final boolean continueService = true;
		while (continueService) {
			this.mRequestId++;

			System.out.println("Waiting for request on port " + this.mPort + " ...");
			try (final Socket clientSocket = this.mServerSocket.accept();
					final BufferedReader br = new BufferedReader(
							new InputStreamReader(clientSocket.getInputStream()))) {
				final String request = br.readLine();

				// Reject the request if invalid
				if (request == null) {
					System.err.println("\tCan not find request data, rejecting the request.");
					continue;
				}
				// Search the request data
				final int requestDataBeginIndex = request.indexOf(GET_REQUEST) + GET_REQUEST.length();
				final int requestDataEndIndex = request.indexOf(GET_SEPARATOR, requestDataBeginIndex);
				if (requestDataBeginIndex < 0 || requestDataEndIndex < 0) {
					System.err.println("\tCan not find request data, request rejected.");
					continue;
				}
				final String requestData = request.substring(requestDataBeginIndex, requestDataEndIndex);

				System.out.println("\t#" + this.mRequestId + " Request is: " + requestData);

				// Parse request data
				float sourceLatitude = 0f;
				float sourceLongitude = 0f;
				float destinationLatitude = 0f;
				float destinationLongitude = 0f;
				for (final String entry : requestData.split(KEY_SEPARATOR)) {
					final String[] keyValue = entry.split(KEY_VALUE_SEPARATOR);
					final String key = keyValue[0];
					final String value = keyValue[1];

					if (key.equals(KEY_SOURCE_LATITUDE)) {
						sourceLatitude = Float.parseFloat(value);
					} else if (key.equals(KEY_SORUCE_LONGITUDE)) {
						sourceLongitude = Float.parseFloat(value);
					} else if (key.equals(KEY_DESTINATION_LATITUDE)) {
						destinationLatitude = Float.parseFloat(value);
					} else if (key.equals(KEY_DESTINATION_LONGITUDE)) {
						destinationLongitude = Float.parseFloat(value);
					} else {
						System.err.println("\tUnknown key, key ignored.");
					}
				}

				if (sourceLatitude == 0f || sourceLongitude == 0f || destinationLatitude == 0f
						|| destinationLongitude == 0f) {
					System.err.println("\tMissing data, request rejected.");
					continue;
				}

				// Transform request data to nodes
				final RoadNode source = this.mNetwork.getNearestRoadNode(sourceLatitude, sourceLongitude);
				final RoadNode destination = this.mNetwork.getNearestRoadNode(destinationLatitude,
						destinationLongitude);

				// Compute the shortest path
				final Optional<Path> path = this.mComputation.computeShortestPath(source, destination);

				// Send an answer
				if (path.isPresent()) {
					// Build the answer
					final Path actualPath = path.get();
					final StringJoiner pathArray = new StringJoiner(JS_ARRAY_DELIMITER);

					// Append the data of the source
					final RoadNode pathSource = (RoadNode) actualPath.getSource();
					final String sourcePosArray = JS_ARRAY_BEGIN + pathSource.getLatitude() + JS_ARRAY_DELIMITER
							+ pathSource.getLongitude() + JS_ARRAY_END;
					pathArray.add(sourcePosArray);

					// Append the data of all following nodes
					for (final DirectedWeightedEdge edge : actualPath.getEdges()) {
						final RoadNode edgeDestination = (RoadNode) edge.getDestination();
						final String nodePosArray = JS_ARRAY_BEGIN + edgeDestination.getLatitude() + JS_ARRAY_DELIMITER
								+ edgeDestination.getLongitude() + JS_ARRAY_END;
						pathArray.add(nodePosArray);
					}

					// Build the answer text as jsonp which calls
					// a callback function
					final String jsonp = "redrawLineServerCallback({\n" + "  path: " + JS_ARRAY_BEGIN
							+ pathArray.toString() + JS_ARRAY_END + "\n" + "})\n";
					final String answer = "HTTP/1.0 200 OK\r\n" + "Content-Length: " + jsonp.length() + "\r\n"
							+ "Content-Type: application/javascript" + "\r\n" + "Connection: close\r\n" + "\r\n"
							+ jsonp;

					// Send the answer
					System.out.println("\t\tSending answer");
					try (final PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true)) {
						pw.println(answer);
					}
				} else {
					System.err.println("\tThere is no path, request rejected.");
					continue;
				}
			}
		}
	}
}
