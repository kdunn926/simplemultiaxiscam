package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;

import biz.wolschon.cam.multiaxis.model.IModel;
import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * Strategy to move along a given axis at a given increment and
 * call a second strategy for each such location.
 * @author marcuswolschon
 *
 */
public class LinearStrategy implements IStrategy {

	private IModel mModel;
	/**
	 * The axis we move along in this strategy. Any value set by a previous strategy is overwritten.
	 */
	private Axis mAxis;
	/**
	 * The increment (in the current unit, e.g. "mm") we use to move along #mAxis.
	 */
	private double mStep;
	/**
	 * The next strategy to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 */
	private IStrategy mNextStrategy;

	/**
	 * @param aAxis the axis we move along in this strategy. Any value set by a previous strategy is overwritten.
	 * @param aStep The increment (in the current unit, e.g. "mm") we use to move along aAxis.
	 * @param aChild The next strategy to call
	 */
	public LinearStrategy(IModel aModel, Axis aAxis, double aStep, IStrategy aChild) {
		this.mModel = aModel;
		this.mAxis = aAxis;
		this.mStep = aStep;
		this.mNextStrategy = aChild;
	}

	/**
	 * The next strategy to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 * @return the next strategy to call.
	 */
	public IStrategy getNextStrategy() {
		return mNextStrategy;
	}

	@Override
	public void runStrategy(final double aStartLocation[]) throws IOException {
		double[] currentLocation = Arrays.copyOf(aStartLocation, aStartLocation.length);
		double current = aStartLocation[mAxis.ordinal()];
		double max = mModel.getMax(mAxis);
		while (current < max) {
			currentLocation[mAxis.ordinal()] = current;
			getNextStrategy().runStrategy(Arrays.copyOf(currentLocation, aStartLocation.length));
			current += mStep;
		}
	}

	@Override
	public void endStrategy()  throws IOException {
		// we have nothing to finish/clean up but maybe the next strategy has.
		getNextStrategy().endStrategy();
	}
}
