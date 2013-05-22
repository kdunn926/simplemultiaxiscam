package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;

/**
 * A strategy either acts at a given location (e.g. move there to mill)
 * or interpolate along a given linear or rotational axis and call another strategy at each interpolated location.<br/>
 * Combinations of strategies thus allow all kinds of milling operations.
 * @author marcuswolschon
 *
 */
public interface IStrategy {

	/**
	 * Called by the parent strategy. Usually in a loop.
	 * @param startLocation some axis are already filled by the parent strategy.
	 */
	void runStrategy(double startLocation[]) throws IOException;

	/**
	 * Call this after the last call to #runStrategy(double[]) to clean up and finalize.
	 */
	void endStrategy() throws IOException;

	public void addProgressListener(final IProgressListener aListener);
}
