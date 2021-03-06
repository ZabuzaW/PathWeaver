package de.zabuza.pathweaver.network.algorithm.shortestpath.arcflag;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.zabuza.pathweaver.network.Node;
import de.zabuza.pathweaver.network.road.RoadNetwork;
import de.zabuza.pathweaver.network.road.RoadNode;

/**
 * Partitions a given road network into two areas. One area contains the nodes
 * which are inside of a given axis aligned rectangle and the other contains the
 * nodes which are outside of it.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class OneAxisRectanglePartitioningProvider implements INetworkPartitioningProvider {
	/**
	 * The maximal latitude of the rectangle.
	 */
	private final float mLatitudeMax;
	/**
	 * The minimal latitude of the rectangle.
	 */
	private final float mLatitudeMin;
	/**
	 * The maximal longitude of the rectangle.
	 */
	private final float mLongitudeMax;
	/**
	 * The minimal longitude of the rectangle.
	 */
	private final float mLongitudeMin;
	/**
	 * The network to create a partitioning for.
	 */
	private final RoadNetwork mNetwork;

	/**
	 * Creates a new network partitioning provider which partitions a given road
	 * network into two areas. One area contains the nodes which are inside of a
	 * given axis aligned rectangle and the other contains the nodes which are
	 * outside of it.
	 * 
	 * @param network
	 *            The network to create a partitioning for
	 * @param latitudeMin
	 *            The minimal latitude of the rectangle
	 * @param latitudeMax
	 *            The maximal latitude of the rectangle
	 * @param longitudeMin
	 *            The minimal longitude of the rectangle
	 * @param longitudeMax
	 *            The maximal longitude of the rectangle
	 */
	public OneAxisRectanglePartitioningProvider(final RoadNetwork network, final float latitudeMin,
			final float latitudeMax, final float longitudeMin, final float longitudeMax) {
		this.mNetwork = network;
		this.mLatitudeMin = latitudeMin;
		this.mLatitudeMax = latitudeMax;
		this.mLongitudeMin = longitudeMin;
		this.mLongitudeMax = longitudeMax;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.pathweaver.network.algorithm.shortestpath.arcflag.
	 * INetworkPartitioningProvider#getPartitioning()
	 */
	@Override
	public Collection<Set<Node>> getPartitioning() throws IllegalArgumentException {
		final Set<Node> insideRectangle = new HashSet<>();
		final Set<Node> outsideRectangle = new HashSet<>();

		for (final Node node : this.mNetwork.getNodes()) {
			if (isInsideRectangle((RoadNode) node)) {
				insideRectangle.add(node);
			} else {
				outsideRectangle.add(node);
			}
		}

		final LinkedList<Set<Node>> partitions = new LinkedList<>();
		partitions.add(insideRectangle);
		partitions.add(outsideRectangle);
		return partitions;
	}

	/**
	 * Returns whether the given road node is inside the axis aligned rectangle
	 * or not.
	 * 
	 * @param node
	 *            The node in question
	 * @return <tt>True</tt> when the given road node is inside the axis aligned
	 *         rectangle, <tt>false</tt> if not.
	 */
	public boolean isInsideRectangle(final RoadNode node) {
		final float nodeLatitude = node.getLatitude();
		final float nodeLongitude = node.getLongitude();

		final boolean isInside = (nodeLatitude >= this.mLatitudeMin && nodeLatitude <= this.mLatitudeMax)
				&& (nodeLongitude >= this.mLongitudeMin && nodeLongitude <= this.mLongitudeMax);
		return isInside;
	}
}
