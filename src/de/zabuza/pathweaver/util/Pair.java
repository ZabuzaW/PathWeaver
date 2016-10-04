package de.zabuza.pathweaver.util;

/**
 * Object for generic pairs which hold two objects of given types.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 * @param <E1>
 *            First type
 * @param <E2>
 *            Second type
 */
public class Pair<E1, E2> {

	/**
	 * First element of the pair.
	 */
	private E1 mFirstElement;
	/**
	 * Second element of the pair.
	 */
	private E2 mSecondElement;

	/**
	 * Creates a new pair holding the two given objects.
	 * 
	 * @param first
	 *            First object of the pair
	 * @param second
	 *            Second object of the pair
	 */
	public Pair(final E1 first, final E2 second) {
		mFirstElement = first;
		mSecondElement = second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (mFirstElement == null) {
			if (other.mFirstElement != null) {
				return false;
			}
		} else if (!mFirstElement.equals(other.mFirstElement)) {
			return false;
		}
		if (mSecondElement == null) {
			if (other.mSecondElement != null) {
				return false;
			}
		} else if (!mSecondElement.equals(other.mSecondElement)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the first element of the pair.
	 * 
	 * @return The first element of the pair
	 */
	public E1 getFirst() {
		return mFirstElement;
	}

	/**
	 * Gets the second element of the pair.
	 * 
	 * @return The second element of the pair
	 */
	public E2 getSecond() {
		return mSecondElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mFirstElement == null) ? 0 : mFirstElement.hashCode());
		result = prime * result + ((mSecondElement == null) ? 0 : mSecondElement.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + mFirstElement + ", " + mSecondElement + "]";
	}
}