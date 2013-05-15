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

	void runStrategy(double startLocation[]) throws IOException;
}
