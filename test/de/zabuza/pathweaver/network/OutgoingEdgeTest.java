package de.zabuza.pathweaver.network;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link OutgoingEdge}.
 * 
 * @author Zabuza
 *
 */
public final class OutgoingEdgeTest {

	/**
	 * Test method for {@link OutgoingEdge#getDestination()}.
	 */
	@Test
	public void testGetDestination() {
		Node node = new Node();
		Node differentNode = new Node();
		int cost = 5;
		OutgoingEdge edge = new OutgoingEdge(node, cost);
		OutgoingEdge anotherEdge = new OutgoingEdge(differentNode, cost);

		Assert.assertEquals(node, edge.getDestination());
		Assert.assertEquals(differentNode, anotherEdge.getDestination());
	}

	/**
	 * Test method for {@link OutgoingEdge#OutgoingEdge(Node, int)} .
	 */
	@Test
	public void testOutgoingEdge() {
		Node node = new Node();
		int cost = 5;
		OutgoingEdge edge = new OutgoingEdge(node, cost);
		Assert.assertEquals(node, edge.getDestination());
		Assert.assertEquals(cost, edge.getCost());

		Node anotherNode = new Node();
		int anotherCost = 2;
		OutgoingEdge anotherEdge = new OutgoingEdge(anotherNode, anotherCost);
		Assert.assertEquals(anotherNode, anotherEdge.getDestination());
		Assert.assertEquals(anotherCost, anotherEdge.getCost());
	}

}
