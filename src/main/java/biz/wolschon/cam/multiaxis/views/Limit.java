package biz.wolschon.cam.multiaxis.views;

import biz.wolschon.cam.multiaxis.trigonometry.Axis;

public class Limit {
	private Double[] mMinimum = new Double[Axis.values().length];
	private Double[] mMaximum = new Double[Axis.values().length];;

	public void addAxis(final double aMinValue, final double aMaxValue, final Axis anAxis) {
		this.mMaximum[anAxis.ordinal()] = Math.max(aMinValue, aMaxValue);
		this.mMinimum[anAxis.ordinal()] = Math.min(aMinValue, aMaxValue);
	}
	public boolean isAxisLimited(final Axis anAxis) {
		return mMinimum[anAxis.ordinal()] != null; 
	}
	public double getMinimum(final Axis anAxis) {
		return mMinimum[anAxis.ordinal()] ;
	}
	public double getMaximum(final Axis anAxis) {
		return mMaximum[anAxis.ordinal()] ;
	}
}