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
	 * Direction of movement
	 */
	private Direction mDirection = Direction.Conventional;
	/**
	 * The next strategy to call in a chain of command.</br>
	 * e.g. first strategy iterates along X, second along Y, third determines Z, last one writes the G-Code
	 */
	private IStrategy mNextStrategy;

	public enum Direction {
		Conventional,
		Climb,
		Meander
	}
	private boolean mMeanderTemp = true;
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

	public void setDirection(final Direction aDirection) {
		if (aDirection == null) {
			throw new IllegalArgumentException("null direction given");
		}
		this.mDirection = aDirection;
	}
	public Direction getDirection() {
		return mDirection;
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
		mMeanderTemp = !mMeanderTemp;
		double[] currentLocation = Arrays.copyOf(aStartLocation, aStartLocation.length);
		double start = aStartLocation[mAxis.ordinal()];
		double current = aStartLocation[mAxis.ordinal()];
		double max = mModel.getMax(mAxis);
		while (current < max) {
			switch (mDirection) {
				case Conventional:
					currentLocation[mAxis.ordinal()] = current;
					break;
				case Climb:
					currentLocation[mAxis.ordinal()] = (start + max) - (current - start);
					break;
				case Meander:
					if (mMeanderTemp) {
						currentLocation[mAxis.ordinal()] = current;
					} else {
						currentLocation[mAxis.ordinal()] = (start + max) - (current - start);
					}
					break;
			}
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
