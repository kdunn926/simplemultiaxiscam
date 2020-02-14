package biz.wolschon.cam.multiaxis.strategy;

import java.io.IOException;
import java.util.Arrays;

import biz.wolschon.cam.multiaxis.trigonometry.Axis;

/**
 * We limit movements in Z to never be lower then {@link #mZLimit}.
 * @author marcuswolschon
 *
 */
public class ZLimitingStrategy implements IStrategy {

	/**
	 * We limit movements in Z to never be lower then this.
	 */
	private double mZLimit = Double.MIN_VALUE;
	/**
	 * The next strategy in the chain of command.
	 */
	private IStrategy mNext;
	/**
	 * True if we have limited a movement while running.
	 * Can be reset via {@link #resetHasLimited()}
	 */
	private boolean mHasLimited = false;
	/**
	 * @return the hasLimited
	 */
	public boolean isHasLimited() {
		return mHasLimited;
	}
	/**
	 * @param aHasLimited the hasLimited to set
	 */
	public void resetHasLimited() {
		mHasLimited = false;
	}
	/**
	 * 
	 * @param aZLimit We limit movements in Z to never be lower then this.
	 * @param aNext The next strategy in the chain of command.
	 */
	public ZLimitingStrategy(final double aZLimit, final IStrategy aNext) {
		super();
		mZLimit = aZLimit;
		mNext = aNext;
	}
	public ZLimitingStrategy(final IStrategy aNext) {
		super();
		mZLimit = Double.MIN_VALUE;
		mNext = aNext;
	}

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
	 * @return We limit movements in Z to never be lower then this.
	 */
	public double getZLimit() {
		return mZLimit;
	}

	/**
	 * @param aZLimit We limit movements in Z to never be lower then this.
	 */
	public void setZLimit(final double aZLimit) {
		mZLimit = aZLimit;
	}

	@Override
	public void runStrategy(double[] aStartLocation, boolean aIsCutting)
			throws IOException {
		// TODO Auto-generated method stub
		if (aStartLocation[Axis.Z.ordinal()] < getZLimit()) {
			double[] limited = Arrays.copyOf(aStartLocation, aStartLocation.length);
			limited[Axis.Z.ordinal()] = getZLimit();
			mHasLimited = true;
			getNext().runStrategy(limited, aIsCutting);
		} else {
			getNext().runStrategy(aStartLocation, aIsCutting);
		}
		
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
