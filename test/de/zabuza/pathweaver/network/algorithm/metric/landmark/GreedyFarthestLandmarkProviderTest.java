package de.zabuza.pathweaver.network.algorithm.metric.landmark;

import java.util.Collection;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.zabuza.pathweaver.network.IPathNetwork;
import de.zabuza.pathweaver.network.Node;
import de.zabuza.pathweaver.network.PathNetwork;
import de.zabuza.pathweaver.network.algorithm.metric.landmark.GreedyFarthestLandmarkProvider;

/**
 * Test for {@link GreedyFarthestLandmarkProvider}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class GreedyFarthestLandmarkProviderTest {
	/**
	 * Rule for expecting exceptions.
	 */
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	/**
	 * Test method for {@link GreedyFarthestLandmarkProvider#getLandmarks(int)}.
	 */
	@Test
	public void testGetLandmarks() {
		IPathNetwork network = new PathNetwork();
		Node firstNode = new Node(0);
		Node secondNode = new Node(1);
		Node thirdNode = new Node(2);
		Node fourthNode = new Node(3);
		network.addNode(firstNode);
		network.addNode(secondNode);
		network.addNode(thirdNode);
		network.addNode(fourthNode);
		network.addEdge(firstNode, secondNode, 1);
		network.addEdge(secondNode, firstNode, 1);
		network.addEdge(secondNode, thirdNode, 1);
		network.addEdge(thirdNode, fourthNode, 10);
		network.addEdge(fourthNode, thirdNode, 10);
		network.addEdge(thirdNode, secondNode, 1);
		Collection<Node> allNodes = network.getNodes();

		GreedyFarthestLandmarkProvider provider = new GreedyFarthestLandmarkProvider(network);

		Set<Node> firstSet = provider.getLandmarks(1);
		Assert.assertEquals(1, firstSet.size());
		Assert.assertTrue(allNodes.containsAll(firstSet));

		Set<Node> secondSet = provider.getLandmarks(2);
		Assert.assertEquals(2, secondSet.size());
		Assert.assertTrue(allNodes.containsAll(secondSet));

		Set<Node> thirdSet = provider.getLandmarks(3);
		Assert.assertEquals(3, thirdSet.size());
		Assert.assertTrue(allNodes.containsAll(thirdSet));

		Set<Node> fourthSet = provider.getLandmarks(4);
		Assert.assertEquals(4, fourthSet.size());
		Assert.assertTrue(allNodes.containsAll(fourthSet));

		Set<Node> fifthSet = provider.getLandmarks(2);
		Assert.assertTrue(fifthSet.contains(fourthNode));

		exception.expect(IllegalArgumentException.class);
		provider.getLandmarks(5);
		provider.getLandmarks(6);
		provider.getLandmarks(-1);
		provider.getLandmarks(0);
	}

}