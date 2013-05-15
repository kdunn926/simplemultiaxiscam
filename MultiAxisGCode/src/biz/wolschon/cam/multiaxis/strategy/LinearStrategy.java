package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;

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
	private Axis mAxis;
	private double mStep;
	private IStrategy mNextStrategy;

	public LinearStrategy(IModel model, Axis axis, double step, IStrategy child) {
		this.mModel = model;
		this.mAxis = axis;
		this.mStep = step;
		this.mNextStrategy = child;
	}

	@Override
	public void runStrategy(double startLocation[]) throws IOException {
		double[] currentLocation = new double[startLocation.length];
		System.arraycopy(startLocation, 0, currentLocation, 0, startLocation.length);
		double current = startLocation[mAxis.ordinal()];
		double max = mModel.getMax(mAxis);
		while (current < max) {
			currentLocation[mAxis.ordinal()] = current;
			mNextStrategy.runStrategy(currentLocation);
			current += mStep;
		}
	}

}
