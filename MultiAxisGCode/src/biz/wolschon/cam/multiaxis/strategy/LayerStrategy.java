package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;


/**
 * @author marcuswolschon
 *
 */
public class LayerStrategy implements IStrategy {

	private double mLayerHeight;
	private double mStartZ;

	/**
	 * We modify the z-limit in this strategy at every layer. It must be below the last strategy that modifies the Z machine position.
	 */
	private ZLimitingStrategy mZLimit;

	/**
	 * We modify the z-limit in this strategy at every layer. It must be below the last strategy that modifies the Z machine position.
	 * @return the zLimit
	 */
	public ZLimitingStrategy getZLimit() {
		return mZLimit;
	}

	/**
	 * The next strategy in the chain of command.
	 */
	private IStrategy mNext;

	/**
	 * @return The next strategy in the chain of command.
	 */
	public IStrategy getNext() {
		return mNext;
	}

	/**
	 * @param aNext The next strategy in the chain of command.
	 */
	public void setNext(final IStrategy aNext) {
		mNext = aNext;
	}
	/**
	 * 
	 * @param aLayerHeight
	 * @param aNext The next strategy in the chain of command.
	 * @param aLastStrategy we modify the z-limit in this strategy at every layer. It must be below the last strategy that modifies the Z machine position.
	 */
	public LayerStrategy(double aLayerHeight, final ZLimitingStrategy aLastStrategy, IStrategy aNext, final double aStartZ) {
		super();
		mLayerHeight = aLayerHeight;
		mNext = aNext;
		mZLimit = aLastStrategy;
		mStartZ = aStartZ;
	}

	@Override
	public void runStrategy(double[] aStartLocation, boolean aIsCutting)
			throws IOException {
		double[] limited;
		double zLimit = mStartZ;
		ZLimitingStrategy zLimitStep = getZLimit();
		IStrategy nextStep = getNext();

		do {
			limited = Arrays.copyOf(aStartLocation, aStartLocation.length);
			zLimit -= getLayerHeight();
			zLimitStep.setZLimit(zLimit);
			zLimitStep.resetHasLimited();
			nextStep.runStrategy(limited, aIsCutting);
		} while (zLimitStep.isHasLimited()); 
	}

	/**
	 * @return the layerHeight
	 */
	public double getLayerHeight() {
		return mLayerHeight;
	}

	/**
	 * @param aLayerHeight the layerHeight to set
	 */
	public void setLayerHeight(double aLayerHeight) {
		mLayerHeight = aLayerHeight;
	}

	@Override
	public void endStrategy() throws IOException {
		getNext().endStrategy();
	}

	@Override
	public void addProgressListener(IProgressListener aListener) {
		getNext().addProgressListener(aListener);
	}

}
