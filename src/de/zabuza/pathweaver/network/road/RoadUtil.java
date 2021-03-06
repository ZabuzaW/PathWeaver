package de.zabuza.pathweaver.network.road;

import java.util.StringJoiner;

/**
 * Utility class which offers methods useful for road networks.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class RoadUtil {
	/**
	 * The mean radius of planet earth, used as a fast approximation.
	 */
	private static final int EARTH_RADIUS_MEAN = 6_371_000;
	/**
	 * The degrees which represents a half circle.
	 */
	private static final int HALF_CIRCLE_DEG = 180;
	/**
	 * Message of an exception which is thrown when a given road type is not
	 * supported by the using method.
	 */
	private static final String ILLEGAL_ROAD_TYPE = "The given road type is not supported by this method.";
	/**
	 * The average speed on a living street in km/h.
	 */
	private static final float LIVING_STREET_SPEED = 10;
	/**
	 * The average speed on a motorway link in km/h.
	 */
	private static final float MOTORWAY_LINK_SPEED = 50;
	/**
	 * The average speed on a motorway in km/h.
	 */
	private static final float MOTORWAY_SPEED = 110;
	/**
	 * Factor to multiply with for conversion from meter per second to kilometer
	 * per hour.
	 */
	private static final float MS_TO_KMH = 3.6f;
	/**
	 * The average speed on a primary link in km/h.
	 */
	private static final float PRIMARY_LINK_SPEED = 50;
	/**
	 * The average speed on a primary road in km/h.
	 */
	private static final float PRIMARY_SPEED = 70;
	/**
	 * The average speed on a residential road in km/h.
	 */
	private static final float RESIDENTIAL_SPEED = 30;
	/**
	 * The average speed on a regular road in km/h.
	 */
	private static final float ROAD_SPEED = 40;
	/**
	 * The average speed on a secondary link in km/h.
	 */
	private static final float SECONDARY_LINK_SPEED = 50;
	/**
	 * The average speed on a secondary road in km/h.
	 */
	private static final float SECONDARY_SPEED = 60;
	/**
	 * The average speed on a service road in km/h.
	 */
	private static final float SERVICE_SPEED = 5;
	/**
	 * The average speed on a tertiary road in km/h.
	 */
	private static final float TERTIARY_SPEED = 50;

	/**
	 * The average speed on a trunk link in km/h.
	 */
	private static final float TRUNK_LINK_SPEED = 50;

	/**
	 * The average speed on a trunk in km/h.
	 */
	private static final float TRUNK_SPEED = 110;

	/**
	 * Separator that is used in TSV-files.
	 */
	private final static String TSV_SEPARATOR = "\t";
	/**
	 * The average speed on an unclassified road in km/h.
	 */
	private static final float UNCLASSIFIED_SPEED = 40;

	/**
	 * The average speed on an unsurfaced road in km/h.
	 */
	private static final float UNSURFACED_SPEED = 30;

	/**
	 * Converts a value in degree to radians.
	 * 
	 * @param deg
	 *            The degree to convert
	 * @return The given value in radians
	 */
	public static double degToRad(final double deg) {
		return deg * Math.PI / HALF_CIRCLE_DEG;
	}

	/**
	 * Computes the distance between the two given positions using an
	 * approximation of the earth as an equirectangular projection.
	 * 
	 * @param firstLatitudeDeg
	 *            The latitude of the first position in degrees
	 * @param firstLongitudeDeg
	 *            The longitude of the first position in degrees
	 * @param secondLatitudeDeg
	 *            The latitude of the second position in degrees
	 * @param secondLongitudeDeg
	 *            The longitude of the second position in degrees
	 * @return The distance between the two given positions in meter
	 */
	public static float distanceEquiRect(final float firstLatitudeDeg, final float firstLongitudeDeg,
			final float secondLatitudeDeg, final float secondLongitudeDeg) {
		final double firstLatitudeRad = degToRad(firstLatitudeDeg);
		final double firstLongitudeRad = degToRad(firstLongitudeDeg);
		final double secondLatitudeRad = degToRad(secondLatitudeDeg);
		final double secondLongitudeRad = degToRad(secondLongitudeDeg);

		final double x = ((secondLongitudeRad - firstLongitudeRad)
				* Math.cos((firstLatitudeRad + secondLatitudeRad) / 2));
		final double y = (secondLatitudeRad - firstLatitudeRad);
		final float distance = (float) (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) * EARTH_RADIUS_MEAN);

		return distance;
	}

	/**
	 * Computes the distance between the two given road nodes using an
	 * approximation of the earth as an equirectangular projection.
	 * 
	 * @param firstNode
	 *            The first node
	 * @param secondNode
	 *            The second node
	 * @return The distance between the two given nodes in meter
	 */
	public static float distanceEquiRect(final RoadNode firstNode, final RoadNode secondNode) {
		return distanceEquiRect(firstNode.getLatitude(), firstNode.getLongitude(), secondNode.getLatitude(),
				secondNode.getLongitude());
	}

	/**
	 * Gets the average speed for the given road type in km/h.
	 * 
	 * @param type
	 *            The road type to get the average speed for
	 * @return The average speed for the given road type in km/h
	 * @throws IllegalArgumentException
	 *             If the given road type is not supported by this operation.
	 */
	public static float getAverageSpeedOfRoadType(final ERoadType type) throws IllegalArgumentException {
		if (type == ERoadType.MOTORWAY) {
			return MOTORWAY_SPEED;
		} else if (type == ERoadType.TRUNK) {
			return TRUNK_SPEED;
		} else if (type == ERoadType.PRIMARY) {
			return PRIMARY_SPEED;
		} else if (type == ERoadType.SECONDARY) {
			return SECONDARY_SPEED;
		} else if (type == ERoadType.TERTIARY) {
			return TERTIARY_SPEED;
		} else if (type == ERoadType.MOTORWAY_LINK) {
			return MOTORWAY_LINK_SPEED;
		} else if (type == ERoadType.TRUNK_LINK) {
			return TRUNK_LINK_SPEED;
		} else if (type == ERoadType.PRIMARY_LINK) {
			return PRIMARY_LINK_SPEED;
		} else if (type == ERoadType.SECONDARY_LINK) {
			return SECONDARY_LINK_SPEED;
		} else if (type == ERoadType.ROAD) {
			return ROAD_SPEED;
		} else if (type == ERoadType.UNCLASSIFIED) {
			return UNCLASSIFIED_SPEED;
		} else if (type == ERoadType.RESIDENTIAL) {
			return RESIDENTIAL_SPEED;
		} else if (type == ERoadType.UNSURFACED) {
			return UNSURFACED_SPEED;
		} else if (type == ERoadType.LIVING_STREET) {
			return LIVING_STREET_SPEED;
		} else if (type == ERoadType.SERVICE) {
			return SERVICE_SPEED;
		} else {
			throw new IllegalArgumentException(ILLEGAL_ROAD_TYPE);
		}
	}

	/**
	 * Gets the road type with the fastest average speed.
	 * 
	 * @return The road type with the fastest average speed
	 */
	public static ERoadType getFastestRoadType() {
		return ERoadType.MOTORWAY;
	}

	/**
	 * Gets the positions of given road nodes in a TSV-format.
	 * 
	 * @param nodes
	 *            Nodes to get position of
	 * @return The positions of the given road nodes in a TSV-format
	 */
	public static String getPositionsTsv(final Iterable<RoadNode> nodes) {
		final StringJoiner joiner = new StringJoiner(System.lineSeparator());
		for (final RoadNode node : nodes) {
			joiner.add(RoadUtil.getPositionTsv(node));
		}
		return joiner.toString();
	}

	/**
	 * Gets the position of a given road node in a TSV-format.
	 * 
	 * @param node
	 *            Node to get position of
	 * @return The position of a given road node in a TSV-format
	 */
	public static String getPositionTsv(final RoadNode node) {
		return node.getLatitude() + TSV_SEPARATOR + node.getLongitude();
	}

	/**
	 * Gets the road type corresponding to the type of the OSM-format.
	 * 
	 * @param osmRoadType
	 *            The road type from the OSM-format
	 * @return The road type corresponding to the type of the OSM-format
	 * @throws IllegalArgumentException
	 *             If the given road type is not supported by this operation.
	 */
	public static ERoadType getRoadTypeFromOsm(final String osmRoadType) throws IllegalArgumentException {
		final String upperCase = osmRoadType.toUpperCase();
		final ERoadType type = ERoadType.valueOf(upperCase);
		return type;
	}

	/**
	 * Gets the time needed to travel the given distance with the given speed.
	 * 
	 * @param distance
	 *            The distance to travel in meter
	 * @param speed
	 *            The speed to use in km/h
	 * @return The time needed to travel the given distance with the given speed
	 */
	public static float getTravelTime(final float distance, final float speed) {
		final float speedInMs = kmhToMs(speed);
		final float travelTime = distance / speedInMs;
		return travelTime;
	}

	/**
	 * Converts a speed given in kilometer per hour to meter per second.
	 * 
	 * @param kmh
	 *            The value to convert in kilometer per hour
	 * @return The converted value in meter per second
	 */
	public static float kmhToMs(final float kmh) {
		return kmh / MS_TO_KMH;
	}

	/**
	 * Converts a speed given in meter per second to kilometer per hour.
	 * 
	 * @param ms
	 *            The value to convert in meter per second
	 * @return The converted value in kilometer per hour
	 */
	public static float msToKmh(final float ms) {
		return ms * MS_TO_KMH;
	}

	/**
	 * Converts a value in radians to degree.
	 * 
	 * @param rad
	 *            The radians to convert
	 * @return The given value in degree
	 */
	public static double radToDeg(final double rad) {
		return rad * HALF_CIRCLE_DEG / Math.PI;
	}

	/**
	 * Utility class. No implementation.
	 */
	private RoadUtil() {

	}
}
